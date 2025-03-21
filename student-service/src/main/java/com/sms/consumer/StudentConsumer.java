package com.sms.consumer;

import com.sms.event.TaskAssignedEvent;
import com.sms.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentConsumer {

  private final StudentService studentService;

  @KafkaListener(topics = "task-created-topic", groupId = "student-service")
  public void listenTaskAssignment(TaskAssignedEvent event) {
    try {
      log.info("Received task assignment event: " + event);

      studentService.assignTaskToStudent(event);
    } catch (Exception e) {
      throw new RuntimeException("Error processing task assignment event: " + e.getMessage());
    }
  }
}
