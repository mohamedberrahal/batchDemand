package com.bcp.tdu.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.bcp.tdu.model.Person;
public interface EsPersonRepository extends ElasticsearchRepository<Person, Long> {
}