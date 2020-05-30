package com.yash.ppmtoolapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yash.ppmtoolapi.domain.Backlog;
import com.yash.ppmtoolapi.domain.Project;
import com.yash.ppmtoolapi.domain.ProjectTask;
import com.yash.ppmtoolapi.exception.ProjectNotFoundException;
import com.yash.ppmtoolapi.repositories.BacklogRepository;
import com.yash.ppmtoolapi.repositories.ProjectRepository;
import com.yash.ppmtoolapi.repositories.ProjectTaskRepository;

@Service
public class ProjectTaskService {

	@Autowired
	private BacklogRepository backlogRepository;

	@Autowired
	private ProjectTaskRepository projectTaskRepository;

	@Autowired
	private ProjectRepository projectRepository;

	public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask) {

		try {

			Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);

			projectTask.setBacklog(backlog);

			Integer backLogSequence = backlog.getPTSequence();

			backLogSequence++;
			backlog.setPTSequence(backLogSequence);

			projectTask.setProjectSequence(projectIdentifier + "-" + backLogSequence);
			projectTask.setProjectIdentifier(projectIdentifier);
			if (projectTask.getPriority() == 0 || projectTask.getPriority() == null) {
				projectTask.setPriority(3);
			}

			if (projectTask.getStatus() == "" || projectTask.getStatus() == null) {
				projectTask.setStatus("TO_DO");
			}
			return projectTaskRepository.save(projectTask);
		} catch (Exception ex) {
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
		// make sure we are searching on an existing backlog
		Backlog backlog = backlogRepository.findByProjectIdentifier(backlog_id);
		if (backlog == null) {
			throw new ProjectNotFoundException("Project with id: '" + backlog_id + "' does not exist");
		}
		// make sure that our task exists
		ProjectTask projectTask = projectTaskRepository.findByProjectSequence(pt_id);
		if (projectTask == null) {
			throw new ProjectNotFoundException("Project Task '" + pt_id + "' does not exist");
		}
		// make sure that the backlog/project_id in the path corresponds to the right
		// project
		if (!projectTask.getProjectIdentifier().equals(backlog_id)) {
			throw new ProjectNotFoundException(
					"Project task '" + pt_id + "' does not exist in project: '" + backlog_id + "'");
		}

		return projectTask;

	}

	public ProjectTask updateByProjectSequence(ProjectTask updatedTask, String backlog_id, String pt_id) {

		ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id);

		projectTask = updatedTask;
		return projectTaskRepository.save(projectTask);
	}

	public void deletePTByProjectSequence(String backlog_id, String pt_id) {
		ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id);
		Backlog backlog = projectTask.getBacklog();
		List<ProjectTask> pts = backlog.getProjectTasks();
		pts.remove(projectTask);
		backlogRepository.save(backlog);
		projectTaskRepository.delete(projectTask);
	}

}
