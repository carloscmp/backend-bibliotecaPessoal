package com.markDev.backend_biblioteca_springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.markDev.backend_biblioteca_springboot.dto.AuthRequest;
import com.markDev.backend_biblioteca_springboot.dto.AuthResponse;
import com.markDev.backend_biblioteca_springboot.dto.RefreshRequest; // Import para o novo DTO
import com.markDev.backend_biblioteca_springboot.security.JwtUtil;
import com.markDev.backend_biblioteca_springboot.security.UserDetailsServiceImpl;
import com.markDev.backend_biblioteca_springboot.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Autowired
	private UserService userService;

	@Autowired
	private JwtUtil jwtUtil;

	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@Valid @RequestBody AuthRequest registerRequest) {
		try {
			userService.registerUser(registerRequest);
			return ResponseEntity.ok("Utilizador registado com sucesso!");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	/**
	 * <<< MÉTODO DE LOGIN ALTERADO >>> Agora gera e retorna tanto o Access Token
	 * (curta duração) quanto o Refresh Token (longa duração).
	 */
	@PostMapping("/login")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
		} catch (BadCredentialsException e) {
			throw new Exception("Nome de utilizador ou senha incorretos", e);
		}

		final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

		// Gera os dois tokens
		final String accessToken = jwtUtil.generateToken(userDetails);
		final String refreshToken = jwtUtil.generateRefreshToken(userDetails);

		// Retorna ambos na resposta
		return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
	}

	/**
	 * <<< NOVO MÉTODO DE REFRESH >>> Recebe um Refresh Token válido e retorna um
	 * novo Access Token. É a mágica por trás do login automático.
	 */
	@PostMapping("/refresh")
	public ResponseEntity<?> refreshToken(@RequestBody RefreshRequest refreshRequest) {
		String refreshToken = refreshRequest.getRefreshToken();
		try {
			String username = jwtUtil.extractUsername(refreshToken);
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);

			// Valida o Refresh Token
			if (jwtUtil.validateToken(refreshToken, userDetails)) {
				// Se for válido, gera um novo Access Token
				String newAccessToken = jwtUtil.generateToken(userDetails);
				// Retorna o novo Access Token e o mesmo Refresh Token
				return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken));
			} else {
				return ResponseEntity.badRequest().body("Refresh Token inválido ou expirado.");
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Refresh Token inválido ou expirado.");
		}
	}
}
