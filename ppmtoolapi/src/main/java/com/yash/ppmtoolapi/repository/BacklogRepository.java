package com.yash.ppmtoolapi.repository;

import org.springframework.data.repository.CrudRepository;

import com.yash.ppmtoolapi.domain.Backlog;

public interface BacklogRepository extends CrudRepository<Backlog, Long> {
	Backlog findByProjectIdentifier(String projectIdentifier);
	
}
