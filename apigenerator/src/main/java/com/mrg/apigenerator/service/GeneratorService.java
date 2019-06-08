package com.mrg.apigenerator.service;

import java.io.IOException;
import java.util.List;

import org.apache.maven.shared.invoker.MavenInvocationException;

import com.mrg.apigenerator.domain.DataSource;
import com.mrg.apigenerator.domain.EntityInformation;
import com.mrg.apigenerator.domain.MEntity;
import com.mrg.apigenerator.exception.EntityGenerationException;

public interface GeneratorService {
	

	List<MEntity> generateRepositories(List<MEntity> newEntityList);

	void deploy() throws MavenInvocationException, IOException;


	List<EntityInformation> findNewEntities(String path, String packageName);


	List<EntityInformation> generateEntities(String entityPath, String pomfile, String packageName)
			throws EntityGenerationException;

}
