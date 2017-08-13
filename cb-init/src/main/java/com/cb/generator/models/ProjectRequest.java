package com.cb.generator.models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class ProjectRequest extends BasicProjectRequest {
	
	public static final String DEFAULT_STARTER = "root_starter";

	private  Map<String, Object> parameters = new LinkedHashMap<>();

	private List<Dependency> resolvedDependencies;

	private Map<String, BillOfMaterials> boms = new LinkedHashMap<>();

	private Map<String, Repository> repositories = new LinkedHashMap<>();
	
	private String build;

	public List<Dependency> getResolvedDependencies() {
		return resolvedDependencies;
	}

	public void setResolvedDependencies(List<Dependency> resolvedDependencies) {
		this.resolvedDependencies = resolvedDependencies;
	}

	/**
	 * Return the additional parameters that can be used to further identify the request.
	 */
	public Map<String, Object> getParameters() {
		return parameters;
	}

	public Map<String, BillOfMaterials> getBoms() {
		return boms;
	}

	public Map<String, Repository> getRepositories() {
		return repositories;
	}


	@Override
	public String toString() {
		return "ProjectRequest [" + "parameters=" + parameters + ", "
				+ (resolvedDependencies != null
				? "resolvedDependencies=" + resolvedDependencies + ", "
				: "")
				+ "boms=" + boms + ", " + "repositories="
				+ repositories + ", "
				+ (build != null ? "build=" + build : "") + "]";
	}

	/**
	 * @return the build
	 */
	public String getBuild() {
		return build;
	}

	/**
	 * @param build the build to set
	 */
	public void setBuild(String build) {
		this.build = build;
	}

}
