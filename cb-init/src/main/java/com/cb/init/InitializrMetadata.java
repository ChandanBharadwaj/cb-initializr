package com.cb.init;


import java.util.Map;


import com.cb.generator.models.BillOfMaterials;
import com.cb.generator.models.Dependency;
import com.cb.generator.models.Repository;

public class InitializrMetadata {

	public Map<String,Repository> repositoryMap;
	public Map<String,Dependency> springDependecyMap;
	public Map<String,Dependency> rspDependency;
	public Map<String,Dependency> backendDependecy;
	public Map<String,BillOfMaterials> bomsMap;
	
		/**
	 * @return the springDependecyMap
	 */
	public Map<String, Dependency> getSpringDependecyMap() {
		return springDependecyMap;
	}

	/**
	 * @param springDependecyMap the springDependecyMap to set
	 */
	public void setSpringDependecyMap(Map<String, Dependency> springDependecyMap) {
		this.springDependecyMap = springDependecyMap;
	}

	/**
	 * @return the bomsMap
	 */
	public Map<String, BillOfMaterials> getBomsMap() {
		return bomsMap;
	}

	/**
	 * @param bomsMap the bomsMap to set
	 */
	public void setBomsMap(Map<String, BillOfMaterials> bomsMap) {
		this.bomsMap = bomsMap;
	}

	/**
	 * @return the repositoryMap
	 */
	public Map<String, Repository> getRepositoryMap() {
		return repositoryMap;
	}

	/**
	 * @param repositoryMap the repositoryMap to set
	 */
	public void setRepositoryMap(Map<String, Repository> repositoryMap) {
		this.repositoryMap = repositoryMap;
	}

	/**
	 * @return the rspDependency
	 */
	public Map<String, Dependency> getRspDependency() {
		return rspDependency;
	}

	/**
	 * @param rspDependency the rspDependency to set
	 */
	public void setRspDependency(Map<String, Dependency> rspDependency) {
		this.rspDependency = rspDependency;
	}

	/**
	 * @return the backendDependecy
	 */
	public Map<String, Dependency> getBackendDependecy() {
		return backendDependecy;
	}

	/**
	 * @param backendDependecy the backendDependecy to set
	 */
	public void setBackendDependecy(Map<String, Dependency> backendDependecy) {
		this.backendDependecy = backendDependecy;
	}
}