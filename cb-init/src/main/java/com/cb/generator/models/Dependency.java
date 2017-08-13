package com.cb.generator.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import com.cb.init.InvalidInitializrMetadataException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.util.StringUtils;

/**
 * Meta-data for a dependency. Each dependency has a primary identifier and an arbitrary
 * number of {@code aliases}.
 *
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Dependency extends MetadataElement {

	public static final String SCOPE_COMPILE = "compile";
	public static final String SCOPE_COMPILE_ONLY = "compileOnly";
	public static final String SCOPE_RUNTIME = "runtime";
	public static final String SCOPE_PROVIDED = "provided";
	public static final String SCOPE_TEST = "test";
	public static final List<String> SCOPE_ALL = Arrays.asList(SCOPE_COMPILE,
			SCOPE_RUNTIME, SCOPE_COMPILE_ONLY, SCOPE_PROVIDED, SCOPE_TEST);

	private List<String> aliases = new ArrayList<>();
	private List<String> facets = new ArrayList<>();
	private List<String> versionList;
	private String groupId;
	private String artifactId;
	private String version;
	private String type;
	private List<Mapping> mappings = new ArrayList<>();
	private String scope = SCOPE_COMPILE;
	private String description;
	private String versionRange;

	@JsonIgnore
	private String versionRequirement;

	private String bom;
	private String repository;

	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private int weight;

	/**
	 * Specify if the dependency represents a "starter", i.e. the sole presence of that
	 * dependency is enough to bootstrap the context.
	 */
	private boolean starter = true;

	private List<String> keywords = new ArrayList<>();

	public Dependency() {
	}

	public Dependency(Dependency dependency) {
		super(dependency);
		this.aliases.addAll(dependency.aliases);
		this.facets.addAll(dependency.facets);
		this.groupId = dependency.groupId;
		this.artifactId = dependency.artifactId;
		this.version = dependency.version;
		this.type = dependency.type;
		this.mappings.addAll(dependency.mappings);
		this.scope = dependency.scope;
		this.description = dependency.description;
		this.versionRange = dependency.versionRange;
		this.versionRequirement = dependency.versionRequirement;
		this.bom = dependency.bom;
		this.repository = dependency.repository;
		this.weight = dependency.weight;
		this.starter = dependency.starter;
		this.keywords.addAll(dependency.keywords);
	}

	public void setScope(String scope) {
		if (!SCOPE_ALL.contains(scope)) {
			throw new InvalidInitializrMetadataException(
					"Invalid scope " + scope + " must be one of " + SCOPE_ALL);
		}
		this.scope = scope;
	}

	public void setVersionRange(String versionRange) {
		this.versionRange = StringUtils.hasText(versionRange) ? versionRange.trim()
				: null;
	}

	/**
	 * Specify if the dependency has its coordinates set, i.e. {@code groupId} and
	 * {@code artifactId}.
	 */
	private boolean hasCoordinates() {
		return groupId != null && artifactId != null;
	}

	/**
	 * Define this dependency as a standard spring boot starter with the specified name
	 * <p>
	 * If no name is specified, the root "spring-boot-starter" is assumed.
	 */
	public Dependency asSpringBootStarter(String name) {
		groupId = "org.springframework.boot";
		artifactId = StringUtils.hasText(name) ? "spring-boot-starter-" + name
				: "spring-boot-starter";
		if (StringUtils.hasText(name)) {
			setId(name);
		}
		return this;
	}

	/**
	 * Validate the dependency and complete its state based on the available information.
	 */
	public void resolve() {
		if (getId() == null) {
			if (!hasCoordinates()) {
				throw new InvalidInitializrMetadataException(
						"Invalid dependency, should have at least an id or a groupId/artifactId pair.");
			}
			generateId();
		}
		else if (!hasCoordinates()) {
			// Let"s build the coordinates from the id
			StringTokenizer st = new StringTokenizer(getId(), ":");
			if (st.countTokens() == 1) { // assume spring-boot-starter
				asSpringBootStarter(getId());
			}
			else if (st.countTokens() == 2 || st.countTokens() == 3) {
				groupId = st.nextToken();
				artifactId = st.nextToken();
				if (st.hasMoreTokens()) {
					version = st.nextToken();
				}
			}
			else {
				throw new InvalidInitializrMetadataException(
						"Invalid dependency, id should have the form groupId:artifactId[:version] but got "
								+ getId());
			}
		}
		
	}

	/**
	 * Generate an id using the groupId and artifactId
	 */
	public String generateId() {
		if (groupId == null || artifactId == null) {
			throw new IllegalArgumentException("Could not generate id for " + this
					+ ": at least groupId and artifactId must be set.");
		}
		setId(groupId + ":" + artifactId);
		return getId();
	}

	public List<String> getAliases() {
		return aliases;
	}

	public void setAliases(List<String> aliases) {
		this.aliases = aliases;
	}

	public List<String> getFacets() {
		return facets;
	}

	public void setFacets(List<String> facets) {
		this.facets = facets;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	/**
	 * Return the default version, can be {@code null} to indicate that the version is
	 * managed by the project and does not need to be specified.
	 */
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Return the type, can be {@code null} to indicate that the default type should be
	 * used (i.e. {@code jar}).
	 */
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Return the dependency mapping if an attribute of the dependency differs according
	 * to the Spring Boot version. If no mapping matches, default attributes are used.
	 */
	public List<Mapping> getMappings() {
		return mappings;
	}

	public void setMappings(List<Mapping> mappings) {
		this.mappings = mappings;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVersionRequirement() {
		return versionRequirement;
	}

	public void setVersionRequirement(String versionRequirement) {
		this.versionRequirement = versionRequirement;
	}
	public String getBom() {
		return bom;
	}

	public void setBom(String bom) {
		this.bom = bom;
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public boolean isStarter() {
		return starter;
	}

	public void setStarter(boolean starter) {
		this.starter = starter;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}


	public String getScope() {
		return scope;
	}

	public String getVersionRange() {
		return versionRange;
	}

	@Override
	public String toString() {
		return "Dependency{" + "id='" + getId() + '\'' +
				", groupId='" + groupId + '\'' +
				", artifactId='" + artifactId + '\'' +
				", version='" + version + '\'' + '}';
	}

	/**
	 * Map several attribute of the dependency for a given version range.
	 */
	public static class Mapping {

		/**
		 * The version range of this mapping.
		 */
		private String versionRange;

		/**
		 * The version to use for this mapping or {@code null} to use the default.
		 */
		private String groupId;

		/**
		 * The groupId to use for this mapping or {@code null} to use the default.
		 */
		private String artifactId;

		/**
		 * The artifactId to use for this mapping or {@code null} to use the default.
		 */
		private String version;

		

		public String getGroupId() {
			return groupId;
		}

		public void setGroupId(String groupId) {
			this.groupId = groupId;
		}

		public String getArtifactId() {
			return artifactId;
		}

		public void setArtifactId(String artifactId) {
			this.artifactId = artifactId;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}
		public String getVersionRange() {
			return versionRange;
		}

		public void setVersionRange(String versionRange) {
			this.versionRange = versionRange;
		}

		public static Mapping create(String range, String groupId, String artifactId,
				String version) {
			Mapping mapping = new Mapping();
			mapping.versionRange = range;
			mapping.groupId = groupId;
			mapping.artifactId = artifactId;
			mapping.version = version;
			return mapping;
		}
	}

	public static Dependency create(String groupId, String artifactId, String version,
			String scope) {
		Dependency dependency = withId(null, groupId, artifactId, version);
		dependency.setScope(scope);
		return dependency;
	}

	public static Dependency withId(String id, String groupId, String artifactId,
			String version) {
		Dependency dependency = new Dependency();
		dependency.setId(id);
		dependency.groupId = groupId;
		dependency.artifactId = artifactId;
		dependency.version = version;
		return dependency;
	}

	public static Dependency withId(String id, String groupId, String artifactId) {
		return withId(id, groupId, artifactId, null);
	}

	public static Dependency withId(String id, String scope) {
		Dependency dependency = withId(id, null, null);
		dependency.setScope(scope);
		return dependency;
	}

	public static Dependency withId(String id) {
		return withId(id, SCOPE_COMPILE);
	}

	/**
	 * @return the versionList
	 */
	public List<String> getVersionList() {
		return versionList;
	}

	/**
	 * @param versionList the versionList to set
	 */
	public void setVersionList(List<String> versionList) {
		this.versionList = versionList;
	}

}
