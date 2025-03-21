package com.sms.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskAssignedEvent {
  private Long studentId;
  private String subject;
  private String taskDescription;
}
