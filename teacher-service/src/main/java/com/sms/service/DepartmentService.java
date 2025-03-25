package com.sms.service;

import com.sms.dto.DepartmentDto;
import com.sms.entity.Department;
import com.sms.exception.ResourceNotFoundException;
import com.sms.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepartmentService {

  private final DepartmentRepository departmentRepository;
  private final ModelMapper modelMapper;

  public DepartmentDto registerDepartment(DepartmentDto departmentDto) {
    Department department = modelMapper.map(departmentDto, Department.class);
    Department savedDepartment = departmentRepository.save(department);

    return modelMapper.map(savedDepartment, DepartmentDto.class);
  }

  public DepartmentDto getDepartment(Long id) {
    Department department =
        departmentRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException("Department not associated with user id: " + id));

    return modelMapper.map(department, DepartmentDto.class);
  }
}
