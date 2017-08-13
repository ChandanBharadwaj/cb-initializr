package com.cb.generator.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A group of {@link Dependency} instances identified by a name.
 *
 * @author Stephane Nicoll
 */
public class DependencyGroup {

	private String name;

	@JsonIgnore
	private String versionRange;

	@JsonIgnore
	private String bom;

	@JsonIgnore
	private String repository;

	final List<Dependency> content = new ArrayList<>();

	/**
	 * Return the name of this group.
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Return the default version range to apply to all dependencies of this group unless
	 * specified otherwise.
	 */
	public String getVersionRange() {
		return versionRange;
	}

	public void setVersionRange(String versionRange) {
		this.versionRange = versionRange;
	}

	/**
	 * Return the default bom to associate to all dependencies of this group unless
	 * specified otherwise.
	 */
	public String getBom() {
		return bom;
	}

	public void setBom(String bom) {
		this.bom = bom;
	}

	/**
	 * Return the default repository to associate to all dependencies of this group unless
	 * specified otherwise.
	 */
	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	/**
	 * Return the {@link Dependency dependencies} of this group.
	 */
	public List<Dependency> getContent() {
		return content;
	}

	public static DependencyGroup create(String name) {
		DependencyGroup group = new DependencyGroup();
		group.setName(name);
		return group;
	}

}
