package com.mrg.apigenerator.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.mrg.apigenerator.domain.DataSource;

@Repository
public interface DataSourceRepository extends PagingAndSortingRepository<DataSource, Long> {

	DataSource findFirstByIsGeneratedOrderByProcessDateDesc(boolean isGenerated);
	
	DataSource findByProjectName(String projectName);
	
	List<DataSource> findAllByIsGeneratedOrderByProcessDateDesc(boolean isGenerated);
}
