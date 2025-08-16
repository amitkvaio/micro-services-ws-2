package com.springcloud.gateway.filter;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.springcloud.gateway.jwt.JwtUtil;

import reactor.core.publisher.Mono;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;

@Component
public class JwtAuthGlobalFilter implements GlobalFilter, Ordered {

	private Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		HttpHeaders headers = exchange.getRequest().getHeaders();
		
		logger.info("Global PRE Filter: JwtAuthGlobalFilter started");
		
		// 1. Check Authorization header
		if (!headers.containsKey(HttpHeaders.AUTHORIZATION)) {
			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
			return exchange.getResponse().setComplete();
		}

		String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
			return exchange.getResponse().setComplete();
		}

		String token = authHeader.substring(7); // remove "Bearer "
		logger.info("Global PRE Filter: JwtAuthGlobalFilter jwt token :{}", token);

		try {
			// 2. Validate token
			Jws<Claims> claims = JwtUtil.validateToken(token);

			// 3. Authorization: check role
			String role = claims.getBody().get("role", String.class);
			if (role == null || !role.equals("ADMIN")) {
				exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
				return exchange.getResponse().setComplete();
			}

			// If token is valid and role is allowed → proceed
			logger.info("Global PRE Filter: JwtAuthGlobalFilter jwt token has validated successfully!");
			
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				// Log response details (after processing)
				System.out.println("Global POST Filter: JwtAuthGlobalFilter Response status -> " + exchange.getResponse().getStatusCode());
			}));

		} catch (JwtException e) {
			// Invalid token
			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
			return exchange.getResponse().setComplete();
		}
	}

	@Override
	public int getOrder() {
		logger.info("Global PRE Filter: JwtAuthGlobalFilter getOrder method!");
		return -1; // run before route filters
	}
}
