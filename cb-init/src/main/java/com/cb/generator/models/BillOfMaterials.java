package com.cb.generator.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;



@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class BillOfMaterials {

	private String groupId;
	private String artifactId;
	private String version;
	private Integer order = Integer.MAX_VALUE;
	private List<String> additionalBoms = new ArrayList<>();
	private List<String> repositories = new ArrayList<>();
	private final List<Mapping> mappings = new ArrayList<>();

	public BillOfMaterials() {
	}

	private BillOfMaterials(String groupId, String artifactId) {
		this(groupId, artifactId, null);
	}

	private BillOfMaterials(String groupId, String artifactId, String version) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
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
	 * Return the version of the BOM. Can be {@code null} if it is provided via a mapping.
	 */
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	
	/**
	 * Return the relative order of this BOM where lower values have higher priority. The
	 * default value is {@code Integer.MAX_VALUE}, indicating lowest priority. The Spring
	 * Boot dependencies bom has an order of 100.
	 */
	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	/**
	 * Return the BOM(s) that should be automatically included if this BOM is required.
	 * Can be {@code null} if it is provided via a mapping.
	 */
	public List<String> getAdditionalBoms() {
		return additionalBoms;
	}

	public void setAdditionalBoms(List<String> additionalBoms) {
		this.additionalBoms = additionalBoms;
	}

	/**
	 * Return the repositories that are required if this BOM is required. Can be
	 * {@code null} if it is provided via a mapping.
	 */
	public List<String> getRepositories() {
		return repositories;
	}

	public void setRepositories(List<String> repositories) {
		this.repositories = repositories;
	}

	public List<Mapping> getMappings() {
		return mappings;
	}

	
	@Override
	public String toString() {
		return "BillOfMaterials [" + (groupId != null ? "groupId=" + groupId + ", " : "")
				+ (artifactId != null ? "artifactId=" + artifactId + ", " : "")
				+ (version != null ? "version=" + version + ", " : "")
				+ (order != null ? "order=" + order + ", " : "")
				+ (additionalBoms != null ? "additionalBoms=" + additionalBoms + ", "
						: "")
				+ (repositories != null ? "repositories=" + repositories : "") + "]";
	}

	public static class Mapping {

		private String versionRange;

		private String version;

		private List<String> repositories = new ArrayList<>();

		private List<String> additionalBoms = new ArrayList<>();

		public Mapping() {
		}

		private Mapping(String range, String version, String... repositories) {
			this.versionRange = range;
			this.version = version;
			this.repositories.addAll(Arrays.asList(repositories));
		}
		public static Mapping create(String range, String version) {
			return new Mapping(range, version);
		}

		public static Mapping create(String range, String version,
				String... repositories) {
			return new Mapping(range, version, repositories);
		}

		public String getVersionRange() {
			return versionRange;
		}

		public String getVersion() {
			return version;
		}

		public List<String> getRepositories() {
			return repositories;
		}

		public List<String> getAdditionalBoms() {
			return additionalBoms;
		}


		public void setVersionRange(String versionRange) {
			this.versionRange = versionRange;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public void setRepositories(List<String> repositories) {
			this.repositories = repositories;
		}

		public void setAdditionalBoms(List<String> additionalBoms) {
			this.additionalBoms = additionalBoms;
		}

		@Override
		public String toString() {
			return "Mapping ["
					+ (versionRange != null ? "versionRange=" + versionRange + ", " : "")
					+ (version != null ? "version=" + version + ", " : "")
					+ (repositories != null ? "repositories=" + repositories + ", " : "")
					+ (additionalBoms != null ? "additionalBoms=" + additionalBoms + ", "
							: "") + "]";
		}

	}

	public static BillOfMaterials create(String groupId, String artifactId) {
		return new BillOfMaterials(groupId, artifactId);
	}

	public static BillOfMaterials create(String groupId, String artifactId,
			String version) {
		return new BillOfMaterials(groupId, artifactId, version);
	}

	

}
