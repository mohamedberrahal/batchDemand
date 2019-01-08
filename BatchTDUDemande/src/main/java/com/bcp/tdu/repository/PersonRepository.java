package com.bcp.tdu.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bcp.tdu.model.Person;
/**
 * 
 * @author berrahal 
 *
 */
public interface PersonRepository extends JpaRepository<Person, Long> {
}
