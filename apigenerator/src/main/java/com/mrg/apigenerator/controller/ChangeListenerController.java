package com.mrg.apigenerator.controller;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrg.apigenerator.domain.EntityInformation;
import com.mrg.apigenerator.service.GeneratorService;

@Controller
public class ChangeListenerController {

	private ObjectMapper mapper;
	private SimpMessagingTemplate messageTemplate;

	public ChangeListenerController(ObjectMapper mapper, SimpMessagingTemplate messageTemplate) {
		this.mapper = mapper;
		this.messageTemplate = messageTemplate;
	}

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
		List<EntityInformation> newEntities = service.generateEntities(ENTITY_ROOT_PATH_CL, "pom_cl.xml", ENTITY_PACKAGE_CL);

		for (EntityInformation newEntity : newEntities) {

			if (oldEntities.contains(newEntity)) {
				// TODO : Table exist check columns
				
				
				
				oldEntities.remove(newEntity);
			}else {
				diff.put("EXTRA_" + UUID.randomUUID(), newEntity.getEntityName());
			}

		}		

		for (EntityInformation oldEntity : oldEntities) {
			diff.put("LACK_" + UUID.randomUUID() , oldEntity.getEntityName());
		}
		this.messageTemplate.convertAndSend("/info/values", diff.toString());

		System.out.println(diff.toString());

	}

}