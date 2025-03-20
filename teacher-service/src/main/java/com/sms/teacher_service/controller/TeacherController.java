package com.sms.teacher_service.controller;

import com.sms.teacher_service.dto.TeacherDto;
import com.sms.teacher_service.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherController {

  private final TeacherService teacherService;

  @PostMapping
  public ResponseEntity<TeacherDto> registerTeacher(@RequestBody TeacherDto teacher) {
    return ResponseEntity.ok(teacherService.registerTeacher(teacher));
  }

  @GetMapping("/{id}")
  public ResponseEntity<TeacherDto> getTeacher(@PathVariable Long id) {
    return ResponseEntity.ok(teacherService.getTeacher(id));
  }

  @GetMapping
  public ResponseEntity<Page<TeacherDto>> getAllTeachers(
      @RequestParam(defaultValue = "0") Integer pageOffset,
      @RequestParam(defaultValue = "10", required = false) Integer pageSize) {
    PageRequest pageRequest =
        PageRequest.of(pageOffset, pageSize, Sort.by(Sort.Direction.DESC, "firstName"));
    return ResponseEntity.ok(teacherService.getAllTeachers(pageRequest));
  }

  @PutMapping("/{id}")
  public ResponseEntity<TeacherDto> updateTeacher(
      @PathVariable Long id, @RequestBody TeacherDto teacherDto) {
    return ResponseEntity.ok(teacherService.updateTeacher(id, teacherDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
    teacherService.deleteTeacher(id);
    return ResponseEntity.noContent().build();
  }
}
