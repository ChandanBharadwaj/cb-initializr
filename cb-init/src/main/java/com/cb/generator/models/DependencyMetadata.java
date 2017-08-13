package com.cb.generator.models;

import java.util.Map;


/**
 * Dependency metadata for a given spring boot {@link Version}.
 *
 */
public class DependencyMetadata {

	public String bootVersion;
	public Map<String, Dependency> dependencies;

	public Map<String, Repository> repositories;

	public Map<String, BillOfMaterials> boms;

	public DependencyMetadata() {
		this(null, null, null);
	}

	public DependencyMetadata(Map<String, Dependency> dependencies,
			Map<String, Repository> repositories, Map<String, BillOfMaterials> boms) {
		this.dependencies = dependencies;
		this.repositories = repositories;
		this.boms = boms;
	}


	public Map<String, Dependency> getDependencies() {
		return dependencies;
	}

	public Map<String, Repository> getRepositories() {
		return repositories;
	}

	public Map<String, BillOfMaterials> getBoms() {
		return boms;
	}

}
