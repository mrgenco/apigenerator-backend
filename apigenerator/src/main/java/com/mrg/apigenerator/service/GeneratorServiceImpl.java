package com.mrg.apigenerator.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.lang.model.element.Modifier;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.mrg.apigenerator.domain.DataSource;
import com.mrg.apigenerator.domain.MEntity;
import com.mrg.apigenerator.exception.DataSourceNotFoundException;
import com.mrg.apigenerator.exception.EntityGenerationException;
import com.mrg.apigenerator.repository.DataSourceRepository;
import com.mrg.apigenerator.repository.EntitiesRepository;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

/**
 * @author M.Rasid Gencosmanoglu
 *
 */
@Service
public class GeneratorServiceImpl implements GeneratorService {

	private DataSourceRepository dataSourceRepository;
	private EntitiesRepository entitiesRepository;

	private InvocationRequest invocationRequest;
	private Invoker invoker;
	private static final Logger log = LoggerFactory.getLogger(GeneratorServiceImpl.class);

	private static final String PROJECT_ROOT_PATH = "/apigenerator-template/src/main/java";
	private static final String APP_PROPERTIES_PATH = "/apigenerator-template/src/main/resources/application.properties";
	private static final String HIBERNATE_PROPERTIES_PATH = "/apigenerator-template/src/main/resources/hibernate.properties";

	@Autowired
	public GeneratorServiceImpl(DataSourceRepository dataSourceRepository, EntitiesRepository entitiesRepository) {
		this.dataSourceRepository = dataSourceRepository;
		this.entitiesRepository = entitiesRepository;
		this.invocationRequest = new DefaultInvocationRequest();
		this.invoker = new DefaultInvoker();
	}


	@Override
	public DataSource update(long id, DataSource update) {
		DataSource ds = dataSourceRepository.findById(id).orElseThrow(() -> new DataSourceNotFoundException("DS with id: " + id + " not found."));
		if (update.getName() != null) {
			ds.setName(update.getName());
		}
		return dataSourceRepository.save(ds);
	}
	
	@Override
	public void generateEntities() throws EntityGenerationException {
		// TODO : findByProjectName(projectName)
		DataSource dataSource = dataSourceRepository.findFirstByIsGeneratedOrderByProcessDateDesc(false);
		if (dataSource == null) {
			log.info("DataSource is null..");
			return;
		}
		byte[] properties = null;
		try {
			// CREATING APP.PROPERTIES
			// writing datasource infos to the application.properties file
			Files.deleteIfExists(Paths.get(APP_PROPERTIES_PATH));
			properties = dataSource.getAppProperties();
			writeToProperties(APP_PROPERTIES_PATH, properties);
			log.info("application.properties file is generated!");

			// CREATING HIBERNATE.PROPERTIES
			// writing datasource infos to the hibernate.properties file
			properties = dataSource.getHibernateProperties();
			writeToProperties(HIBERNATE_PROPERTIES_PATH, properties);
			log.info("hibernate.properties file is generated!");

			// CREATING ENTITIES
			// generating entity classes by running mvn antrun:run@hbm2java
			invocationRequest.setPomFile(new File("/apigenerator-template/pom.xml"));
			invocationRequest.setGoals(Collections.singletonList("antrun:run@hbm2java"));

			// TODO : update maven home with correct path
			invoker.setMavenHome(new File("D:\\Maven\\apache-maven-3.6.1"));
			invoker.execute(invocationRequest);
			log.info("entities are generated!");

			Files.deleteIfExists(Paths.get(HIBERNATE_PROPERTIES_PATH));
			
		} catch (Exception e) {
			log.error("Error occured while generating source files : " + e.getMessage() + e.getStackTrace());
			throw new EntityGenerationException("Error occured while generating source files");
		}
	}
	
	// TODO : return entities with their fields and data types
	@Override
	public List<MEntity> findNewEntities() {

		List<MEntity> newEntityList = new ArrayList<MEntity>();
//		Iterable<MEntity> iterableEntityList = entitiesRepository.findAll();
		
		String entityFilePath = PROJECT_ROOT_PATH + "com/mrg/webapi/model";
		File folder = new File(entityFilePath);
		File[] files = folder.listFiles();
		boolean isFound = false;

		if (files != null && files.length > 0) {
			for (File file : files) {
				String fileName = file.getName().replaceFirst("[.][^.]+$", "");
//				for (MEntity entity : iterableEntityList) {
//					if (entity.getEntityName().equals(fileName)) {
//						isFound = true;
//						break;
//					}
//				}
				if (!isFound) {
					MEntity newEntity = new MEntity(fileName, false, "No Rest API!");
					newEntityList.add(newEntity);
				}
			}
		}
		return newEntityList;
	}
	

	@Override
	public List<MEntity> generateRepositories(List<MEntity> newEntityList) {
		
		List<MEntity> entityList = new ArrayList<MEntity>();
		try {
			for (MEntity entity : newEntityList) {
				// Generating repository source files for each entity
				String serviceName = entity.getEntityName()+"Repository";
				TypeSpec entityRepository = TypeSpec.interfaceBuilder(serviceName).addAnnotation(Repository.class)
						.addModifiers(Modifier.PUBLIC)
						.addSuperinterface(ParameterizedTypeName
						.get(ClassName.get(PagingAndSortingRepository.class),
							 ClassName.get("com.mrg.webapi.model", entity.getEntityName()),
							 ClassName.get(Long.class)))
						.build();
				Path filePath = Paths.get(PROJECT_ROOT_PATH);
				JavaFile javaFile = JavaFile.builder("com.mrg.webapi.repository", entityRepository).build();
				javaFile.writeTo(filePath);
				entity.setServiceName(serviceName);
				entity.setServiceExist(true);
				entityList.add(entity);

				log.info(entity.getEntityName() + "Repository is generated...");
			}		
			saveNewEntities(entityList);
			return entityList;
			
		} catch (Exception ex) {
			log.error("Error occured while deploying application : " + ex.getMessage() + ex.getStackTrace());
		}
		return entityList;

	}
	
	@Override
	public void deploy() throws MavenInvocationException, IOException {
		
		// TODO : findByProjectName(projectName)
		DataSource dataSource = dataSourceRepository.findFirstByIsGeneratedOrderByProcessDateDesc(false);
		if (dataSource == null) {
			log.info("DataSource is null..");
			return;
		}
		// CONFIGURATION - try multiple datasource manually
		// TODO : generate Configuration beans for multiple datasources
		// generateDataSourceConfigBeans();
		// log.info("configuration beans for multiple datasources are generated!");

		// Updating datasource info by setting generated column true
		dataSource.setIsGenerated(true);
		dataSourceRepository.save(dataSource);
		
		// TODO : Setup a CI/CD pipeline, instead of generating and executing a fat jar.		
		// Generating a fat jar for webapi
		invocationRequest.setGoals(Collections.singletonList("package"));
		invoker.execute(invocationRequest);
		log.info("webapi jar is generated..");
		// Executing webapi jar
		Runtime.getRuntime().exec("java -jar /Users/mrgenco/Documents/MRG/webapi/target/webapi-0.0.1.jar");
		log.info("webapi application is running..");
		
	}

	private void writeToProperties(String propertyPath, byte[] properties) throws IOException {

		if (propertyPath.equals(HIBERNATE_PROPERTIES_PATH)) {

			File hibernatePropertiesFile = new File(HIBERNATE_PROPERTIES_PATH);
			if (hibernatePropertiesFile.createNewFile()) {
				log.info("hibernate.properties file is generated!");
			} else {
				log.info("hibernate.properties file is already exist!");
			}
			Files.write(hibernatePropertiesFile.toPath(), properties, StandardOpenOption.APPEND);

		} 
		if(propertyPath.equals(APP_PROPERTIES_PATH)){
			
			File appPropertiesFile = new File(APP_PROPERTIES_PATH);
			if (appPropertiesFile.createNewFile()) {
				log.info("application.properties file is generated!");
			} else {
				log.info("application.properties file is already exist!");
			}
			Files.write(appPropertiesFile.toPath(), properties, StandardOpenOption.APPEND);
		}

	}


	@Override
	public void saveNewEntities(List<MEntity> newEntityList) {

		entitiesRepository.saveAll(newEntityList);
		
	}

	@Override
	public Iterable<MEntity> findAllEntities() {
		return entitiesRepository.findAll();
	}

	

}
