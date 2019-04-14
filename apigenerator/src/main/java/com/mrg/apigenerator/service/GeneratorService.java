package com.mrg.apigenerator.service;

import java.io.IOException;
import java.util.List;

import org.apache.maven.shared.invoker.MavenInvocationException;

import com.mrg.apigenerator.domain.DataSource;
import com.mrg.apigenerator.domain.MEntity;
import com.mrg.apigenerator.exception.EntityGenerationException;

public interface GeneratorService {
	
//	Iterable<DataSource> list();
	
//	DataSource create(DataSource post);
//	
//	DataSource read(long id);
//	
	DataSource update(long id, DataSource post);
//	
//	void delete(long id);
	
	void generateEntities() throws EntityGenerationException;
		
	List<MEntity> findNewEntities();

	List<MEntity> generateRepositories(List<MEntity> newEntityList);

	void deploy() throws MavenInvocationException, IOException;

	void saveNewEntities(List<MEntity> newEntityList);

	Iterable<MEntity> findAllEntities();
}
