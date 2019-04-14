package com.mrg.apigenerator.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class MEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	
		
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long Id;
	private String entityName;
	private String serviceName;
	private boolean serviceExist; 
	
	public MEntity(){}
	
	public MEntity(String entityName, boolean serviceExist, String serviceName){
		this.entityName = entityName;
		this.serviceExist = serviceExist;
		this.serviceName = serviceName;
	}
	
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public boolean isServiceExist() {
		return serviceExist;
	}
	public void setServiceExist(boolean serviceExist) {
		this.serviceExist = serviceExist;
	}
	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}
}
