package com.orm.service;

import com.orm.dto.ProjectDTO;
import com.orm.entity.Project;
import com.orm.entity.User;

import java.util.List;

public interface ProjectService {

    ProjectDTO getByProjectCode(String code);

    List<ProjectDTO> listAllProjects();

    Project save(ProjectDTO dto);

    void update(ProjectDTO dto);

    void delete(String code);

    void complete(String projectCode);

    List<ProjectDTO> listAllProjectDetails();

    List<ProjectDTO> readAllByAssignedManger(User user);
}