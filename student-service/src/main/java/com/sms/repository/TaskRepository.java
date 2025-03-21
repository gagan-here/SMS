package com.sms.repository;

import com.sms.entity.Task;
import com.sms.enums.TaskStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
  Page<Task> findByStudentId(Long studentId, PageRequest pageRequest);

  List<Task> findByTaskStatus(TaskStatus taskStatus);
}
