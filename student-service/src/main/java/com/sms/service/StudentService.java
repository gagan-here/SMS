package com.sms.service;

import com.sms.dto.StudentDto;
import com.sms.dto.TaskDto;
import com.sms.dto.TaskStatusDto;
import com.sms.entity.Student;
import com.sms.entity.Task;
import com.sms.enums.TaskStatus;
import com.sms.event.TaskAssignedEvent;
import com.sms.exception.ResourceNotFoundException;
import com.sms.repository.StudentRepository;
import com.sms.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {

  private final StudentRepository studentRepository;
  private final TaskRepository taskRepository;

  private final ModelMapper modelMapper;

  public StudentDto registerStudent(StudentDto studentDto) {
    Student student = modelMapper.map(studentDto, Student.class);
    Student savedStudent = studentRepository.save(student);
    return modelMapper.map(savedStudent, StudentDto.class);
  }

  @CacheEvict(value = "students", key = "#event.studentId")
  @Transactional
  public void assignTaskToStudent(TaskAssignedEvent event) {

    log.info("Assigning task to student with id: " + event.getStudentId());

    Student student =
        studentRepository
            .findById(event.getStudentId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Student not associated with user id: " + event.getStudentId()));

    Task assignedTask = modelMapper.map(event, Task.class);
    assignedTask.setTaskStatus(TaskStatus.PENDING);

    student.getTasks().add(assignedTask);
    studentRepository.save(student);

    log.info("Task assigned to student id: " + event.getStudentId());
  }

  @Cacheable(value = "students", key = "#id")
  public StudentDto getStudent(Long id) {
    Student student =
        studentRepository
            .findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException("Student not associated with user id: " + id));
    return modelMapper.map(student, StudentDto.class);
  }

  public Page<StudentDto> getAllTasks(Long studentId, PageRequest pageRequest) {
    return taskRepository
        .findByStudentId(studentId, pageRequest)
        .map(student -> modelMapper.map(student, StudentDto.class));
  }

  public TaskDto updateTaskStatus(Long id, TaskStatusDto status) {
    Task task =
        taskRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task not associated with id: " + id));
    task.setTaskStatus(TaskStatus.valueOf(status.getStatus().toUpperCase()));

    Task updatedTask = taskRepository.save(task);
    return modelMapper.map(updatedTask, TaskDto.class);
  }

  @CacheEvict(value = "students", key = "#id")
  public StudentDto updateStudent(Long id, StudentDto studentDto) {
    Student student =
        studentRepository
            .findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException("Student not associated with id: " + id));

    student.setFirstName(studentDto.getFirstName());
    student.setLastName(studentDto.getLastName());
    student.setEmail(studentDto.getEmail());

    Student updatedStudent = studentRepository.save(student);
    return modelMapper.map(updatedStudent, StudentDto.class);
  }

  public void deleteStudent(Long id) {
    studentRepository.deleteById(id);
  }
}
