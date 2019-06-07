package com.mrg.apigenerator.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.mrg.apigenerator.domain.EntityInformation;

@Configuration
@EnableScheduling
public class ChangeListener {
	
	private static final String ENTITY_ROOT_PATH = "C:\\Users\\mehme\\git\\apigenerator-template\\apigenerator-template\\src\\main\\java\\com\\mrg\\webapi\\model";
	private static final String ENTITY_ROOT_PATH_CL = "C:\\Users\\mehme\\git\\apigenerator-template\\apigenerator-template\\src\\main\\java\\com\\mrg\\webapi\\modelcl";
	
	@Autowired
	GeneratorService service;

	@Scheduled(fixedDelay = 60000)
	public void scheduleFixedDelayTask() {
		List<EntityInformation> oldEntities = service.findNewEntities(ENTITY_ROOT_PATH,"com.mrg.webapi.model.");
		List<EntityInformation> newEntities = service.generateEntities(ENTITY_ROOT_PATH_CL, "pom_cl.xml", "com.mrg.webapi.modelcl.");
		System.out.println("oldEntities size : " + oldEntities.size());
		System.out.println("newEntities size : " + newEntities.size());
	}
}
