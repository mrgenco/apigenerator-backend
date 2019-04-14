package com.mrg.apigenerator.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.mrg.apigenerator.domain.MEntity;

@Repository
public interface EntitiesRepository extends PagingAndSortingRepository<MEntity, Long> {

}
