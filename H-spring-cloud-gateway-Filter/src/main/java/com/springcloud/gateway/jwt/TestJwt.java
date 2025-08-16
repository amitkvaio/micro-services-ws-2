package com.springcloud.gateway.jwt;

public class TestJwt {
	public static void main(String[] args) {
		String token = JwtUtil.generateToken("amit", "ADMIN");
		System.out.println("JWT Token: " + token);
	}
}

// JWT Token: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbWl0Iiwicm9sZSI6IkFETUlOIiwiaWF0IjoxNzU1MzM1NTgzLCJleHAiOjE3NTUzMzkxODN9.OF_5R7MJA47QeEtJubbwdICKwco9DXjBiGYJOeH5tG0