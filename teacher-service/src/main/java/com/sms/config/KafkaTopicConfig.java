package com.sms.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

  @Value("${kafka.topic.task-created-topic}")
  private String TASK_CREATED_TOPIC;

  @Bean
  public NewTopic teacherCreatedTopic() {
    return new NewTopic(TASK_CREATED_TOPIC, 3, (short) 1);
  }
}
