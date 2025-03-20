package com.sms.teacher_service.service;

import com.sms.teacher_service.dto.TeacherDto;
import com.sms.teacher_service.entity.Teacher;
import com.sms.teacher_service.exceptions.ResourceNotFoundException;
import com.sms.teacher_service.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeacherService {

  private TeacherRepository teacherRepository;
  private final ModelMapper modelMapper;

  private static final String TEACHER_TOPIC = "teacher_topic";

  public TeacherDto registerTeacher(TeacherDto teacherDto) {
    Teacher teacher = modelMapper.map(teacherDto, Teacher.class);
    Teacher savedTeacher = teacherRepository.save(teacher);

    return modelMapper.map(savedTeacher, TeacherDto.class);
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
