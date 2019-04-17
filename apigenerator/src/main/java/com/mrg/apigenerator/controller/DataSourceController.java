package com.mrg.apigenerator.controller;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.apache.maven.shared.invoker.MavenInvocationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mrg.apigenerator.domain.DataSource;
import com.mrg.apigenerator.domain.MEntity;
import com.mrg.apigenerator.exception.DataSourceNotFoundException;
import com.mrg.apigenerator.exception.EntityGenerationException;
import com.mrg.apigenerator.repository.DataSourceRepository;
import com.mrg.apigenerator.service.GeneratorService;

@RestController
@RequestMapping("/ds")
public class DataSourceController {

	private GeneratorService generatorService;
	private DataSourceRepository dsRepository;


	@Autowired
	public DataSourceController(GeneratorService generatorService, DataSourceRepository dataSourceRepo) {
		this.generatorService = generatorService;
		this.dsRepository = dataSourceRepo;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public Iterable<DataSource> list() {
		return dsRepository.findAll();
	}

	/***************************************/
	/******** SCAN DATASOURCE START ********/
	/***************************************/
	// CREATE DATASOURCE AND CREATE ENTITIES
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public ResponseEntity<String> create(@RequestBody DataSource ds) {
		
		ResponseEntity<String> response;
		
		String isBadRequest = isValid(ds); 
		if(!isBadRequest.isEmpty()){
			response = new ResponseEntity<>(isBadRequest,HttpStatus.BAD_REQUEST); 
			return response;
		}		
		
		try{
			ds.setIsGenerated(false);
			ds.setProcessDate(new Date());
			dsRepository.save(ds);

			// TODO return entities and fields as response
			generatorService.generateEntities();
			response = new ResponseEntity<>("SUCCESS",HttpStatus.OK);
			
		}catch(EntityGenerationException ex){
			response = new ResponseEntity<>(ex.getMessage(), HttpStatus.OK);
		}
		return response;
	}

	private String isValid(DataSource ds) {
		
		if(ds.getUrl().isEmpty())
			return "DataSource url can not be empty";
		if(ds.getUsername().isEmpty())
			return "DataSource username can not be empty";
		if(ds.getPassword().isEmpty())
			return "DataSource password can not be empty";
		if(ds.getProjectName().isEmpty())
			return "ProjectName can not be empty";
		if(ds.getName().isEmpty())
			return "DataSource name can not be empty";
		return "";		
	}

	// RETURN NEW ENTITIES
	// Scans datasource and returns newly created entities
	@RequestMapping(value = "/findNewEntities", method = RequestMethod.GET)
	public Iterable<MEntity> findNewEntities() {
		// find and return newly generated entities
		return generatorService.findNewEntities();
	}

	/*******************************/
	/******** DEPLOY START ********/
	/*******************************/
	// DEPLOY APIs
	// generate repository code, deploy it,
	// set isServiceExist property to true
	// for each newly created entity
	@RequestMapping(value = "/generateAPIs", method = RequestMethod.GET)
	public String generateAPIs() throws MavenInvocationException, IOException {

		// generate repository code
		generatorService.generateRepositories(generatorService.findNewEntities());

		// deploy
		generatorService.deploy();

		return "success";
	}
	
	
	// SHOW ALL ENTITIES
	// retrieves created entities from datasource
	@RequestMapping(value = "/showAllEntities", method = RequestMethod.GET)
	public Iterable<MEntity> listEntities() {
		return generatorService.findAllEntities();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public DataSource read(@PathVariable(value = "id") long id) throws DataSourceNotFoundException {
		DataSource post = dsRepository.findById(id).orElseThrow(() -> new DataSourceNotFoundException("DS with id: " + id + " not found."));
		
		return post;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public DataSource update(@PathVariable(value = "id") long id, @RequestBody DataSource ds) {
		return generatorService.update(id, ds);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable(value = "id") long id) {
		if(dsRepository.existsById(id)) {
			dsRepository.deleteById(id);
		}
	}

	@ExceptionHandler(DataSourceNotFoundException.class)
	public void handleDSNotFound(DataSourceNotFoundException exception, HttpServletResponse response)
			throws IOException {
		response.sendError(HttpStatus.NOT_FOUND.value(), exception.getMessage());
	}
}
