package com.springcloud.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements GlobalFilter, Ordered  {

	private Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		logger.info("@LoggingFilter Global PRE Filter: Request Path Received -> {}", exchange.getRequest().getPath());
		// Continue request execution
		return chain.filter(exchange).then(Mono.fromRunnable(() -> {
			// Log response details (after processing)
			System.out.println("@LoggingFilter Global POST Filter: Response status -> " + exchange.getResponse().getStatusCode());
		}));
	}
	
	 // Set filter order (lower value = higher priority)
    @Override
    public int getOrder() {
    	logger.info("@LoggingFilter Global PRE Filter: getOrder method!");
        return -1;  // run before route filters
    }
}
