package com.mrg.apigenerator.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class DataSource implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long Id;
	private String projectName;
	private String name;
	private String url;
	private String username;
	private String password;
	private boolean isGenerated;	
	private Date processDate;
	
	public Date getProcessDate() {
		return processDate;
	}


	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}


	public DataSource() {}

   	
	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}
	
	public boolean getIsGenerated() {
		return isGenerated;
	}
	public void setIsGenerated(boolean isGenerated) {
		this.isGenerated = isGenerated;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getProjectName() {
		return projectName;
	}


	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public byte[] getAppProperties(){
		String properties = this.name+".datasource.url="+this.url+"\n"
				  +this.name+".datasource.username="+this.username+"\n"
				  +this.name+".datasource.password="+this.password+"\n"
				  +this.name+".datasource.driver-class-name="+"com.mysql.jdbc.Driver"+"\n"
				  +"server.port=8080"+"\n\n\n";
		return properties.getBytes();
	}
	
	public byte[] getHibernateProperties(){
		String properties = "hibernate.connection.driver_class="+"com.mysql.jdbc.Driver"+"\n"
				+"hibernate.connection.url="+this.getUrl()+"\n"	
				+"hibernate.connection.username="+this.getUsername()+"\n"
				+"hibernate.connection.password="+this.getPassword()+"\n"
				+"hibernate.dialect=org.hibernate.dialect.MySQLDialect"+"\n"
				+"hibernate.id.new_generator_mappings=false";
		return properties.getBytes();
	}
	

}
