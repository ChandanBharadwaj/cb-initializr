package com.cb.init;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.cb.generator.models.Dependency;
import com.cb.generator.models.DependencyMetadata;
import com.google.gson.Gson;

/**
 * @author Chandan
 *
 */

@Service
public class InitializrMetadataProviderService {

	@Autowired
	public RestTemplate restTemplate;
	
	@Value("${spring.repo.url:https://start.spring.io/dependencies}")
	public String springUrl;
	
	public Gson gson = new Gson();
	
	@Value("${spring.accepted.dependencies}")
	public String springAcceptedDependencies;
	
	public InitializrMetadata getInitializrMetadata(){
		InitializrMetadata initializrMetadata = new InitializrMetadata();
		initializrMetadata = getSpringDependencies(initializrMetadata);
		initializrMetadata = getOtherDependencies(initializrMetadata);
		return initializrMetadata;
	}

	/**
	 * @param initializrMetadata
	 * @return
	 */
	@Cacheable("OtherDependencies")
	private InitializrMetadata getOtherDependencies(InitializrMetadata initializrMetadata) {

		return initializrMetadata;
	}

	/**
	 * @param initializrMetadata
	 * @return initializrMetadata with spring dependencies by making rest call to spring 
	 */
	@Cacheable("SpringDependencies")
	private InitializrMetadata getSpringDependencies(InitializrMetadata initializrMetadata) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(springUrl).queryParam("bootVersion", "1.5.6.RELEASE");
		ResponseEntity<String> response=restTemplate.getForEntity(builder.build().encode().toUri(), String.class);
		DependencyMetadata data = gson.fromJson(response.getBody(), DependencyMetadata.class);
		initializrMetadata.setBomsMap(data.getBoms());
		initializrMetadata.setSpringDependecyMap(filterRequiredDependencies(data.getDependencies()));
		initializrMetadata.setRepositoryMap(data.getRepositories());
		return initializrMetadata;
	}

	/**
	 * @param dependencies
	 * @return
	 */
	private Map<String, Dependency> filterRequiredDependencies(Map<String, Dependency> dependencies) {
		List<String> filterList= Arrays.asList(springAcceptedDependencies.split("#"));
		return dependencies.entrySet().stream()
				.filter(k ->filterList.contains(k.getKey()))
				.collect(Collectors.toMap(p ->p.getKey(), p -> p.getValue()));
	}
}
