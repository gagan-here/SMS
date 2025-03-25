package com.sms.controller;

import com.sms.dto.DepartmentDto;
import com.sms.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teachers/department")
@RequiredArgsConstructor
public class DepartmentController {

  private final DepartmentService departmentService;

  @PostMapping
  public ResponseEntity<DepartmentDto> registerDepartment(
      @RequestBody DepartmentDto departmentDto) {
    return ResponseEntity.ok(departmentService.registerDepartment(departmentDto));
  }

  @GetMapping("/{id}")
  public ResponseEntity<DepartmentDto> getDepartment(@PathVariable Long id) {
    return ResponseEntity.ok(departmentService.getDepartment(id));
  }
}
