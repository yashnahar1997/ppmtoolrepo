package com.yash.ppmtoolapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yash.ppmtoolapi.domain.Project;
import com.yash.ppmtoolapi.exception.PojectIdException;
import com.yash.ppmtoolapi.repository.ProjectRepository;

@Service
public class ProjectService {
	
	@Autowired
	ProjectRepository projectRepository;
	
	public Project saveOrUpdateProject(Project project) {
		try {
		
			return projectRepository.save(project);
		}catch(Exception ex) {
			throw new PojectIdException("Project Id '"+project.getProjectIdentifier().toUpperCase()+"' already exists");
		}
		
	}
	
	public Project findProjectByIdentifier(String projectId) {
		Project project=projectRepository.findByProjectIdentifier(projectId);
		if(project==null) {
			throw new PojectIdException("Project Id '"+projectId+"' does not exist");
		}
		return project;
	}
	
	public Iterable<Project> findAllProjects(){
		return projectRepository.findAll();
	}
	
	public void deleteProjectByIdentifier(String projectId) {
		Project project=projectRepository.findByProjectIdentifier(projectId);
		if(project==null) {
			throw new PojectIdException("Cannot delete project wid id '"+projectId+"' this project not exist");
		}
		projectRepository.delete(project);
		
	}
}
