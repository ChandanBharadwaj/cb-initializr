package com.cb.generator.models;

import java.util.List;
import java.util.Map;

/**
 * @author Chandan
 *
 */
public class BackendDependencies {

	List<String> backendGroupList;
	Map<String,Dependency> backendDependencies;
	/**
	 * @return the backendGroup
	 */
	public List<String> getBackendGroupList() {
		return backendGroupList;
	}
	/**
	 * @param backendGroup the backendGroup to set
	 */
	public void setBackendGroupList(List<String> backendGroup) {
		this.backendGroupList = backendGroup;
	}
	/**
	 * @return the backendDependencies
	 */
	public Map<String, Dependency> getBackendDependencies() {
		return backendDependencies;
	}
	/**
	 * @param backendDependencies the backendDependencies to set
	 */
	public void setBackendDependencies(Map<String, Dependency> backendDependencies) {
		this.backendDependencies = backendDependencies;
	}
	
}
