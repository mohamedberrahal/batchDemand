package com.bcp.tdu.model;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Address {
	@Id
	private Long id;
	private String name;
}