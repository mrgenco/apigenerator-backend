package com.mrg.apigenerator.domain;

import java.util.Map;

public class EntityInformation {
	
	private String entityName;
	private Map<String, String> fields;
	/**
	 * @return the entityName
	 */
	public String getEntityName() {
		return entityName;
	}
	/**
	 * @param entityName the entityName to set
	 */
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	/**
	 * @return the fields
	 */
	public Map<String, String> getFields() {
		return fields;
	}
	/**
	 * @param fields the fields to set
	 */
	public void setFields(Map<String, String> fields) {
		this.fields = fields;
	}

}
