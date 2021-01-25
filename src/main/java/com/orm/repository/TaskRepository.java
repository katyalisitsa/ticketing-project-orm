package com.orm.repository;

import com.orm.entity.Project;
import com.orm.entity.Task;
import com.orm.entity.User;
import com.orm.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("Select count(t) from Task t where t.project.projectCode = ?1 " +
            "and t.taskStatus <> 'COMPLETED'")
    int totalNonCompletedTasks(String projectCode);

    @Query(value = "Select count(*) from tasks t join projects p on t.project_id = p.id " +
            "where p.project_code = ?1 and t.task_status = 'COMPLETED'", nativeQuery = true)
    int totalCompletedTasks(String projectCode);

    List<Task> findAllByProject(Project project);

    List<Task> findTasksByTaskStatusIsNotAndAssignedEmployee(Status status, User user);

    List<Task> findAllByProjectAssignedManager(User manager);

    List<Task> findAllByTaskStatusAndAssignedEmployee(Status status, User user);

    List<Task> findAllByAssignedEmployee(User user);


}