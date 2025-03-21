package com.sms.service;

import com.sms.dto.CreateTaskDto;
import com.sms.dto.TeacherDto;
import com.sms.entity.Teacher;
import com.sms.event.TaskAssignedEvent;
import com.sms.exception.ResourceNotFoundException;
import com.sms.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeacherService {

  @Value("${kafka.topic.task-created-topic}")
  private String TASK_CREATED_TOPIC;

  private final TeacherRepository teacherRepository;
  private final ModelMapper modelMapper;
  private final KafkaTemplate<Long, TaskAssignedEvent> kafkaTemplate;

  public TeacherDto registerTeacher(TeacherDto teacherDto) {
    Teacher teacher = modelMapper.map(teacherDto, Teacher.class);
    Teacher savedTeacher = teacherRepository.save(teacher);

    return modelMapper.map(savedTeacher, TeacherDto.class);
  }

  public void assignTask(CreateTaskDto createTaskDto) {
    TaskAssignedEvent taskAssignedEvent = modelMapper.map(createTaskDto, TaskAssignedEvent.class);
    kafkaTemplate.send(TASK_CREATED_TOPIC, taskAssignedEvent);
  }

  @Cacheable(value = "teachers", key = "#id")
  public TeacherDto getTeacher(Long id) {
    Teacher teacher =
        teacherRepository
            .findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException("Teacher not associated with user id: " + id));

    return modelMapper.map(teacher, TeacherDto.class);
  }

  public Page<TeacherDto> getAllTeachers(PageRequest pageRequest) {
    return teacherRepository
        .findAll(pageRequest)
        .map(teacher -> modelMapper.map(teacher, TeacherDto.class));
  }

  public TeacherDto updateTeacher(Long id, TeacherDto teacherDetails) {
    Teacher teacher =
        teacherRepository
            .findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException("Teacher not associated with id: " + id));

    teacher.setFirstName(teacherDetails.getFirstName());
    teacher.setLastName(teacherDetails.getLastName());
    teacher.setEmail(teacherDetails.getEmail());

    Teacher updatedTeacher = teacherRepository.save(teacher);
    return modelMapper.map(updatedTeacher, TeacherDto.class);
  }

  public void deleteTeacher(Long id) {
    teacherRepository.deleteById(id);
  }
}
