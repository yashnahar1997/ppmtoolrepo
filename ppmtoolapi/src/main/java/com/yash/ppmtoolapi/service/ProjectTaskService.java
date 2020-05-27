package com.yash.ppmtoolapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yash.ppmtoolapi.domain.Backlog;
import com.yash.ppmtoolapi.domain.Project;
import com.yash.ppmtoolapi.domain.ProjectTask;
import com.yash.ppmtoolapi.exception.ProjectNotFoundException;
import com.yash.ppmtoolapi.repository.BacklogRepository;
import com.yash.ppmtoolapi.repository.ProjectRepository;
import com.yash.ppmtoolapi.repository.ProjectTaskRepository;

@Service
public class ProjectTaskService {

	@Autowired
	private BacklogRepository backlogRepository;

	@Autowired
	private ProjectTaskRepository projectTaskRepository;

	@Autowired
	private ProjectRepository projectRepository;

	public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask) {
		Backlog backlog = null;
		try {
			backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);

			projectTask.setBacklog(backlog);

			Integer backLogSequence = backlog.getPTSequence();
			backLogSequence++;
			backlog.setPTSequence(backLogSequence);

			projectTask.setProjectSequence(projectIdentifier + "-" + backLogSequence);
			projectTask.setProjectIdentifer(projectIdentifier);

			if (projectTask.getPriority() == null) {
				projectTask.setPriority(3);
			}
			if (projectTask.getStatus() == "" || projectTask.getStatus() == null) {
				projectTask.setStatus("TO_DO");
			}
			return projectTaskRepository.save(projectTask);
		} catch (Exception e) {
			throw new ProjectNotFoundException("Project Not Found");
		}
	}

	public Iterable<ProjectTask> findBacklogById(String id) {

		Project project = projectRepository.findByProjectIdentifier(id);

		if (project == null) {
			throw new ProjectNotFoundException("Project with id: '" + id + "' does not exist");
		}
		return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);

	}

	public ProjectTask findPTByProjectSequence(String backlog_id, String pt_id) {
		return projectTaskRepository.findByProjectSequence(pt_id);
	}

}
