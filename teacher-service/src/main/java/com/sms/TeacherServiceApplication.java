package com.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TeacherServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(TeacherServiceApplication.class, args);
  }
}
