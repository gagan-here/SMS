package com.sms.controller;

import com.sms.dto.StudentDto;
import com.sms.dto.TaskDto;
import com.sms.dto.TaskStatusDto;
import com.sms.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

  private final StudentService studentService;

  @PostMapping
  public ResponseEntity<StudentDto> registerStudent(@RequestBody StudentDto student) {
    return ResponseEntity.ok(studentService.registerStudent(student));
  }

  @GetMapping("/{id}")
  public ResponseEntity<StudentDto> getStudent(@PathVariable Long id) {
    return ResponseEntity.ok(studentService.getStudent(id));
  }

  @GetMapping
  public ResponseEntity<Page<StudentDto>> getAllTasks(
      @PathVariable Long studentId,
      @RequestParam(defaultValue = "0") Integer pageOffset,
      @RequestParam(defaultValue = "10", required = false) Integer pageSize) {
    PageRequest pageRequest =
        PageRequest.of(pageOffset, pageSize, Sort.by(Sort.Direction.DESC, "firstName"));
    return ResponseEntity.ok(studentService.getAllTasks(studentId, pageRequest));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<TaskDto> updateTaskStatus(
      @PathVariable Long id, @RequestBody TaskStatusDto status) {
    return ResponseEntity.ok(studentService.updateTaskStatus(id, status));
  }

  @PutMapping("/{id}")
  public ResponseEntity<StudentDto> updateStudent(
      @PathVariable Long id, @RequestBody StudentDto studentDto) {
    return ResponseEntity.ok(studentService.updateStudent(id, studentDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
    studentService.deleteStudent(id);
    return ResponseEntity.noContent().build();
  }
}
