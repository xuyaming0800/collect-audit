package com.autonavi.audit.entity;

import org.activiti.engine.RepositoryService;

public class ProcessDefinition implements java.io.Serializable {

	private static final long serialVersionUID = 2361150494289356102L;

	/** unique identifier */
	String id;

	/**
	 * category name which is derived from the tarNamespace attribute in the
	 * definitions element
	 */
	String category;

	/** label used for display purposes */
	String name;

	/** unique name for all versions this process definitions */
	String key;

	/** description of this process **/
	String description;

	/** version of this process definition */
	int version;

	/**
	 * name of {@link RepositoryService#ResourceAsStream(String, String) the
	 * resource} of this process definition.
	 */
	String resourceName;

	/** The deployment in which this process definition is contained. */
	String deploymentId;

	/** The resource name in the deployment of the diagram image (if any). */
	String diagramResourceName;

	/** Returns true if the process definition is in suspended state. */
	boolean suspended;

	/** The tenant identifier of this process definition */
	String tenantId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getDeploymentId() {
		return deploymentId;
	}

	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}

	public String getDiagramResourceName() {
		return diagramResourceName;
	}

	public void setDiagramResourceName(String diagramResourceName) {
		this.diagramResourceName = diagramResourceName;
	}

	public boolean isSuspended() {
		return suspended;
	}

	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

}
