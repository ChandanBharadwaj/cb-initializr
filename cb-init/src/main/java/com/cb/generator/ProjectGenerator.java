package com.cb.generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import com.cb.generator.exception.InitializrException;
import com.cb.generator.models.BillOfMaterials;
import com.cb.generator.models.Dependency;
import com.cb.generator.models.DependencyGroupEnum;
import com.cb.generator.models.MetadataElement;
import com.cb.generator.models.ProjectRequest;
import com.cb.generator.utils.TemplateRenderer;
import com.cb.init.InitializrMetadata;
import com.cb.init.InitializrMetadataProviderService;
/**
 * @author Chandan
 *
 */
public class ProjectGenerator {
	private static final Logger log = LoggerFactory.getLogger(ProjectGenerator.class);
	@Autowired
	private InitializrMetadataProviderService metadataProvider;
	
	@Value("${TMPDIR:.}/initializr")
	private String tmpdir;
	
	@Autowired
	private TemplateRenderer templateRenderer = new TemplateRenderer();
	@Autowired
	private ProjectResourceLocator projectResourceLocator = new ProjectResourceLocator();


	private File temporaryDirectory;
	
	private transient Map<String, List<File>> temporaryFiles = new LinkedHashMap<>();

	public File generateProjectStructure(ProjectRequest request) {
		try {
			return doGenerateProjectStructure(request);
		}
		catch (InitializrException ex) {
			//publishProjectFailedEvent(request, ex);
			throw ex;
		}
	}	
	/**
	 * @param request
	 * @return
	 */
	private File doGenerateProjectStructure(ProjectRequest request) {
		Map<String, Object> model = resolveModel(request);

		File rootDir;
		try {
			rootDir = File.createTempFile("tmp", "", getTemporaryDirectory());
		}
		catch (IOException e) {
			throw new IllegalStateException("Cannot create temp dir", e);
		}
		addTempFile(rootDir.getName(), rootDir);
		rootDir.delete();
		rootDir.mkdirs();

		File dir = initializerProjectDir(rootDir, request);
		String pom = new String(doGenerateMavenPom(model));
		writeText(new File(dir, "pom.xml"), pom);
		writeMavenWrapper(dir);
		
		generateGitIgnore(dir, request);

		String applicationName = request.getApplicationName();
		String language = request.getLanguage();

		String codeLocation = language;
		File src = new File(new File(dir, "src/main/" + codeLocation),
				request.getPackageName().replace(".", "/"));
		src.mkdirs();
		String extension = ("kotlin".equals(language) ? "kt" : language);
		write(new File(src, applicationName + "." + extension),
				"Application." + extension, model);

		File test = new File(new File(dir, "src/test/" + codeLocation),	request.getPackageName().replace(".", "/"));
		test.mkdirs();
		setupTestModel(request, model);
		write(new File(test, applicationName + "Tests." + extension),
				"ApplicationTests." + extension, model);

		File resources = new File(dir, "src/main/resources");
		resources.mkdirs();
		writeText(new File(resources, "application.properties"), "");
		new File(dir, "src/main/resources/templates").mkdirs();
		new File(dir, "src/main/resources/static").mkdirs();
		return rootDir;
	}

	/**
	 * @param request
	 * @return
	 */
	private Map<String, Object> resolveModel(ProjectRequest originalRequest) {
		ProjectRequest validatedRequest =validateRequest(originalRequest);
		Map<String, Object> model = new LinkedHashMap<>();
		InitializrMetadata metadata = metadataProvider.getInitializrMetadata();
		
		model.put("groupId", validatedRequest.getGroupId());
		model.put("artifactId", validatedRequest.getArtifactId());
		model.put("version", validatedRequest.getVersion());
		model.put("packaging", validatedRequest.getPackaging());
		model.put("name",validatedRequest.getName());
		model.put("description",validatedRequest.getName());
		//Parent pom
		model.put("mavenParentGroupId","org.springframework.boot");
		model.put("mavenParentArtifactId", "spring-boot-starter-parent");
		model.put("mavenParentVersion", validatedRequest.getBootVersion());
		
		// Adding dependencies
		List<Dependency> dependencies =mapDependencies(validatedRequest,metadata);
		model.put("compileDependencies",filterDependencies(dependencies, Dependency.SCOPE_COMPILE));
		model.put("runtimeDependencies",filterDependencies(dependencies, Dependency.SCOPE_RUNTIME));
		model.put("compileOnlyDependencies",filterDependencies(dependencies, Dependency.SCOPE_COMPILE_ONLY));
		model.put("providedDependencies",filterDependencies(dependencies, Dependency.SCOPE_PROVIDED));
		model.put("testDependencies",filterDependencies(dependencies, Dependency.SCOPE_TEST));
		
		// build properties
		Map<String, Object> buildProperties = new LinkedHashMap<>(); 		
		buildProperties.put("java.version", validatedRequest.getJavaVersion()!=null?validatedRequest.getJavaVersion():"1.8");
		buildProperties=getBuildProperties(buildProperties,metadata.getBomsMap(),dependencies);
		model.put("buildPropertiesVersions",buildProperties.entrySet());
		Map<String, Object> buildPropertiesMaven = new LinkedHashMap<>();		
		buildPropertiesMaven.put("project.build.sourceEncoding", "UTF-8");
		buildPropertiesMaven.put("project.reporting.outputEncoding", "UTF-8");
		model.put("buildPropertiesMaven",buildPropertiesMaven.entrySet());
				
		// adding boms
		List<Map<String,String>> resolvedBoms = buildResolvedBoms(metadata.getBomsMap(),dependencies);
		if(resolvedBoms!=null && resolvedBoms.size()>0) {
			model.put("hasBoms",true);
			model.put("resolvedBoms", resolvedBoms);
		}
//		model.put("repositoryValues", request.getRepositories().entrySet());
//		model.put("hasRepositories", true);
		//model.put("resolvedBoms","");
		return model;
	}
	
	
	/**
	 * @param buildProperties
	 * @param bomsMap
	 * @param dependencies
	 * @return
	 */
	private Map<String, Object> getBuildProperties(Map<String, Object> buildProperties,
			Map<String, BillOfMaterials> bomsMap, List<Dependency> dependencies) {
		
		List<String> bomIdList=new ArrayList<String>();
		
		dependencies.stream()
		.filter(p-> StringUtils.hasText(p.getBom()))
		.forEach(p -> {
			if(!bomIdList.contains(p.getBom())) {
				bomIdList.add(p.getBom());		
			}});
		bomsMap.entrySet().stream().filter(p ->bomIdList.contains(p.getKey())).forEach(bom ->{
			buildProperties.put(bom.getKey()+".version", bom.getValue().getVersion());
		});
		return buildProperties;
	}
	/**
	 * @param bomsMap
	 * @param dependencies
	 * @return
	 */
	private List<Map<String, String>> buildResolvedBoms(Map<String, BillOfMaterials> bomsMap,
			List<Dependency> dependencies) {
		List<String> bomIdList=new ArrayList<String>();
		dependencies.stream()
		.filter(p-> p.getBom()!=null)
		.forEach(p -> {
			if(!bomIdList.contains(p.getBom())) {
				bomIdList.add(p.getBom());		
			}});
		return bomsMap.entrySet().stream()
		.filter(p->bomIdList.contains(p.getKey())).
		<Map<String,String>>map(bom ->toBomModel(bom.getKey(),bom.getValue())).collect(Collectors.toList());
	}

	private Map<String,String> toBomModel(String bomId, BillOfMaterials bom) {
		Map<String, String> model = new HashMap<>();
		model.put("groupId", bom.getGroupId());
		model.put("artifactId", bom.getArtifactId());
		model.put("versionToken", "${"+bomId+".version}");
		return model;
	}
	/**
	 * @param validatedRequest
	 * @param metadata
	 * @return
	 */
	private List<Dependency> mapDependencies(ProjectRequest validatedRequest, InitializrMetadata metadata) {
	
		List<Dependency> dependencies = new ArrayList<>();
		Map<String ,List<String>> selectedDeps =validatedRequest.getDependencies();
		// spring
		List<String> selectedSpringDeps = selectedDeps.get(DependencyGroupEnum.SPRING.name());
		metadata.getSpringDependecyMap().entrySet().stream()
		.filter(p -> selectedSpringDeps.contains(p.getKey()))
		.map(p -> dependencies.add(p.getValue()));
		
		// rsp 
		List<String> selectedRspDeps = selectedDeps.get(DependencyGroupEnum.RSP.name());
		metadata.getRspDependency().entrySet().stream()
		.filter(p -> selectedRspDeps.contains(p.getKey()))
		.map(p -> dependencies.add(p.getValue()));
		
		// downstream
		List<String> selectedDownStreamDeps = selectedDeps.get(DependencyGroupEnum.DOWNSTREAM.name());
		metadata.getBackendDependecy().entrySet().stream()
		.filter(p -> selectedDownStreamDeps.contains(p.getKey()))
		.map(p -> dependencies.add(p.getValue()));
		
		return dependencies;
	}
	/**
	 * @param originalRequest
	 */
	private ProjectRequest validateRequest(ProjectRequest originalRequest) {
		if(originalRequest.getBootVersion()==null) {
			originalRequest.setBootVersion("1.5.6.RELEASE");
		}
		if(originalRequest.getApplicationName()==null) {
			originalRequest.setApplicationName("demo");
		}
		if(originalRequest.getGroupId()==null) {
			originalRequest.setGroupId("com.example");
		}
		if(originalRequest.getArtifactId()==null) {
			originalRequest.setArtifactId(originalRequest.getApplicationName());
		}
		if(originalRequest.getVersion()!=null) {
			originalRequest.setVersion("0.0.1-SNAPSHOT");
		}
		return originalRequest;
		
	}
	private static boolean isMavenBuild(ProjectRequest request) {
		return "maven".equals(request.getBuild());
	}
	private static List<Dependency> filterDependencies(List<Dependency> dependencies,
			String scope) {
		return dependencies.stream().filter(dep -> scope.equals(dep.getScope()))
				.collect(Collectors.toList());
	}
	private File getTemporaryDirectory() {
		if (temporaryDirectory == null) {
			temporaryDirectory = new File(tmpdir, "initializr");
			temporaryDirectory.mkdirs();
		}
		return temporaryDirectory;
	}
	private void addTempFile(String group, File file) {
		temporaryFiles.computeIfAbsent(group, key -> new ArrayList<>()).add(file);
	}
	private File initializerProjectDir(File rootDir, ProjectRequest request) {
		if (request.getBaseDir() != null) {
			File dir = new File(rootDir, request.getBaseDir());
			dir.mkdirs();
			return dir;
		}
		else {
			return rootDir;
		}
	}
	private byte[] doGenerateMavenPom(Map<String, Object> model) {
		return templateRenderer.process("starter-pom.xml", model).getBytes();
	}
	private void writeText(File target, String body) {
		try (OutputStream stream = new FileOutputStream(target)) {
			StreamUtils.copy(body, Charset.forName("UTF-8"), stream);
		}
		catch (Exception e) {
			throw new IllegalStateException("Cannot write file " + target, e);
		}
	}
	private void writeMavenWrapper(File dir) {
		writeTextResource(dir, "mvnw.cmd", "maven/mvnw.cmd");
		writeTextResource(dir, "mvnw", "maven/mvnw");

		File wrapperDir = new File(dir, ".mvn/wrapper");
		wrapperDir.mkdirs();
		writeTextResource(wrapperDir, "maven-wrapper.properties",
				"maven/wrapper/maven-wrapper.properties");
		writeBinaryResource(wrapperDir, "maven-wrapper.jar",
				"maven/wrapper/maven-wrapper.jar");
	}
	private File writeBinaryResource(File dir, String name, String location) {
		return doWriteProjectResource(dir, name, location, true);
	}
	private File writeTextResource(File dir, String name, String location) {
		return doWriteProjectResource(dir, name, location, false);
	}
	private File doWriteProjectResource(File dir, String name, String location,
			boolean binary) {
		File target = new File(dir, name);
		if (binary) {
			writeBinary(target, projectResourceLocator
					.getBinaryResource("classpath:project/" + location));
		}
		else {
			writeText(target, projectResourceLocator
					.getTextResource("classpath:project/" + location));
		}
		return target;
	}
	private void writeBinary(File target, byte[] body) {
		try (OutputStream stream = new FileOutputStream(target)) {
			StreamUtils.copy(body, stream);
		}
		catch (Exception e) {
			throw new IllegalStateException("Cannot write file " + target, e);
		}
	}
	/**
	 * Generate a {@code .gitignore} file for the specified {@link ProjectRequest}
	 * @param dir the root directory of the project
	 * @param request the request to handle
	 */
	protected void generateGitIgnore(File dir, ProjectRequest request) {
		Map<String, Object> model = new LinkedHashMap<>();
			model.put("build", "maven");
			model.put("mavenBuild", true);
		write(new File(dir, ".gitignore"), "gitignore.tmpl", model);
	}
	public void write(File target, String templateName, Map<String, Object> model) {
		String body = templateRenderer.process(templateName, model);
		writeText(target, body);
	}
	protected void setupTestModel(ProjectRequest request, Map<String, Object> model) {
		String imports = "";
		String testAnnotations = "";
		imports += String.format(generateImport("org.springframework.boot.test.context.SpringBootTest",request.getLanguage()) + "%n");
		imports += String.format(generateImport("org.springframework.test.context.junit4.SpringRunner",request.getLanguage()) + "%n");
		model.put("testImports", imports);
		model.put("testAnnotations", testAnnotations);
	}
	protected String generateImport(String type, String language) {
		String end = ";";
		return "import " + type + end;
	}
}
