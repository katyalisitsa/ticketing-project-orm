package com.cybertek.implementation;

import com.cybertek.dto.ProjectDTO;
import com.cybertek.dto.TaskDTO;
import com.cybertek.dto.UserDTO;
import com.cybertek.entity.User;
import com.cybertek.exception.TicketingProjectException;
import com.cybertek.mapper.UserMapper;
import com.cybertek.repository.TaskRepository;
import com.cybertek.repository.UserRepository;
import com.cybertek.service.ProjectService;
import com.cybertek.service.TaskService;
import com.cybertek.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {


    UserRepository userRepository;
    UserMapper userMapper;
    ProjectService projectService;
    TaskService taskService;
    TaskRepository taskRepository;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, ProjectService projectService, TaskService taskService, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.projectService = projectService;
        this.taskService = taskService;
        this.taskRepository = taskRepository;
    }

    @Override
    public List<UserDTO> listAllUsers() {
        List<User> list = userRepository.findAll(Sort.by("firstName"));
        return list.stream().map(obj -> {
            return userMapper.convertToDto(obj);
        }).collect(Collectors.toList());
    }

    @Override
    public UserDTO findByUserName(String username) {
        User user = userRepository.findByUserName(username);
        return userMapper.convertToDto(user);
    }

    @Override
    public void save(UserDTO dto) {
        User obj = userMapper.convertToEntity(dto);
        userRepository.save(obj);
    }

    @Override
    public UserDTO update(UserDTO dto) {

        //Find current user
        User user = userRepository.findByUserName(dto.getUserName());
        //Map update user dto to entity object
        User convertedUser = userMapper.convertToEntity(dto);
        //set id to the converted object
        convertedUser.setId(user.getId());
        //save updated user
        userRepository.save(convertedUser);

        return findByUserName(dto.getUserName());
    }

    @Override
    public void delete(String username) {
        User user = userRepository.findByUserName(username);

        if (user == null) {
            throw new TicketingProjectException("User does not exist");
        }

        if (!checkIfUserCanBeDeleted(user)) {
            throw new TicketingProjectException("User can not be deleted. It is linked by a project or task.");
        }

        user.setUserName(user.getUserName() + "-" + user.getId());

        user.setIsDeleted(true);
        userRepository.save(user);
    }

    //hard delete
    @Override
    public void deleteByUserName(String username) {
        userRepository.deleteByUserName(username);
    }


    @Override
    public List<UserDTO> listAllByRole(String role) {
        List<User> users = userRepository.findAllByRoleDescriptionIgnoreCase(role);
        return users.stream().map(obj -> {
            return userMapper.convertToDto(obj);
        }).collect(Collectors.toList());
    }

    @Override
    public Boolean checkIfUserCanBeDeleted(User user) {
        switch (user.getRole().getDescription()) {
            case "Manager":
                List<ProjectDTO> projectList = projectService.readAllByAssignedManger(user);
                return projectList.size() == 0;
            case "Employee":
                List<TaskDTO> taskList = taskService.readAllByEmployee(user);
                return taskList.size() == 0;
            default:
                return true;
        }
    }

}