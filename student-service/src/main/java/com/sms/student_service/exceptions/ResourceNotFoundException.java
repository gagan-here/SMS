package com.sms.student_service.exceptions;

public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException() {}

  public ResourceNotFoundException(String message) {
    super(message);
  }
}
