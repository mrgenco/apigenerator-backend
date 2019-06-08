package com.mrg.apigenerator.service;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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
	private static final String ENTITY_PACKAGE = "com.mrg.webapi.model.";
	private static final String ENTITY_PACKAGE_CL = "com.mrg.webapi.modelcl.";
	
	
	@Autowired
	GeneratorService service;

	@Scheduled(fixedDelay = 60000)
	public void scheduleFixedDelayTask() {

		HashMap<String, String> diff = new HashMap<String, String>();

		List<EntityInformation> oldEntities = service.findNewEntities(ENTITY_ROOT_PATH, ENTITY_PACKAGE);
//		List<EntityInformation> newEntities = service.findNewEntities(ENTITY_ROOT_PATH_CL, ENTITY_PACKAGE_CL);
		List<EntityInformation> newEntities = service.generateEntities(ENTITY_ROOT_PATH_CL, "pom_cl.xml", ENTITY_PACKAGE_CL);

		for (EntityInformation newEntity : newEntities) {

			if (oldEntities.contains(newEntity)) {
				// TODO : Table exist check columns
				
				
				
				oldEntities.remove(newEntity);
			}else {
				diff.put("EXTRA" + UUID.randomUUID(), newEntity.getEntityName());
			}

		}		

		for (EntityInformation oldEntity : oldEntities) {
			diff.put("LACK" + UUID.randomUUID() , oldEntity.getEntityName());
		}
		

		System.out.println(diff.toString());

	}
}
