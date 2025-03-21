package com.sms.dto;

import com.sms.enums.TaskStatus;
import lombok.Data;

@Data
public class TaskDto {
  private String subject;
  private String description;

  private TaskStatus taskStatus;
}
