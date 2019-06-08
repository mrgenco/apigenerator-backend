package com.mrg.apigenerator.domain;

import java.util.HashMap;

public class EntityInformation {

	private String entityName;
	private HashMap<String, String> fields;

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
	public HashMap<String, String> getFields() {
		return fields;
	}

	/**
	 * @param fields the fields to set
	 */
	public void setFields(HashMap<String, String> fields) {
		this.fields = fields;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		EntityInformation entityInformation = (EntityInformation) obj;
		
		if (entityInformation.getEntityName().equals(this.getEntityName())) {
			return true;
		}
		return false;
	}
	
	public boolean isReallyEquals(Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		EntityInformation entityInformation = (EntityInformation) obj;
		
		if (entityInformation.getEntityName().equals(this.getEntityName()) && entityInformation.getFields().equals(this.getFields())) {
			return true;
		} else {
			
		}
		return false;
	}

	
	

}
