package com.sms.student_service.service;

import com.sms.student_service.dto.StudentDto;
import com.sms.student_service.entity.Student;
import com.sms.student_service.exceptions.ResourceNotFoundException;
import com.sms.student_service.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentService {

  private StudentRepository studentRepository;
  private final ModelMapper modelMapper;

  private static final String STUDENT_TOPIC = "student_topic";

  public StudentDto registerStudent(StudentDto studentDto) {
    Student student = modelMapper.map(studentDto, Student.class);
    Student savedStudent = studentRepository.save(student);
    return modelMapper.map(savedStudent, StudentDto.class);
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

  public Page<StudentDto> getAllStudents(PageRequest pageRequest) {
    return studentRepository
        .findAll(pageRequest)
        .map(student -> modelMapper.map(student, StudentDto.class));
  }

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
