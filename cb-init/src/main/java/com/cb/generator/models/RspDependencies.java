package com.cb.generator.models;

import java.util.List;

/**
 * @author Chandan
 *
 */
public class RspDependencies {

	String rspGroup;
	List<Dependency> rspDependencyList;
	/**
	 * @return the rspGroup
	 */
	public String getRspGroup() {
		return rspGroup;
	}
	/**
	 * @param rspGroup the rspGroup to set
	 */
	public void setRspGroup(String rspGroup) {
		this.rspGroup = rspGroup;
	}
	/**
	 * @return the rspDependencyList
	 */
	public List<Dependency> getRspDependencyList() {
		return rspDependencyList;
	}
	/**
	 * @param rspDependencyList the rspDependencyList to set
	 */
	public void setRspDependencyList(List<Dependency> rspDependencyList) {
		this.rspDependencyList = rspDependencyList;
	}
}
