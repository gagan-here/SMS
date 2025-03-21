package com.sms.scheduler;

import com.sms.entity.Task;
import com.sms.enums.TaskStatus;
import com.sms.repository.TaskRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskCleanupScheduler {

  private final TaskRepository taskRepository;

  //  @Scheduled(cron = "0 0 * * * ?") // Runs every hour
  @Scheduled(cron = "0 * * * * ?") // Runs every minute
  @Transactional
  public void removeCompletedTasks() {
    log.info("Running scheduled cleanup for completed tasks...");

    List<Task> completedTasks = taskRepository.findByTaskStatus(TaskStatus.COMPLETED);
    if (!completedTasks.isEmpty()) {
      taskRepository.deleteAll(completedTasks);
      log.info("Deleted {} completed tasks", completedTasks.size());
    } else {
      log.info("No completed tasks found for deletion.");
    }
  }
}
