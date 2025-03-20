package com.sms.auth_service.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
  private String email;
  private String password;
}
