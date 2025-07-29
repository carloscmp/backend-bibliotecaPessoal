package com.markDev.backend_biblioteca_springboot.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {
	private String username;
	private String password;
}