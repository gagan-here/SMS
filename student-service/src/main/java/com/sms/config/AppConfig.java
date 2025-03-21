package com.sms.config;

import com.sms.entity.Task;
import com.sms.event.TaskAssignedEvent;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  @Bean
  public ModelMapper modelMapper() {
    ModelMapper mapper = new ModelMapper();
    mapper
        .createTypeMap(TaskAssignedEvent.class, Task.class)
        .addMappings(m -> m.skip(Task::setId)); // Skip setting the ID
    return mapper;
  }
}
