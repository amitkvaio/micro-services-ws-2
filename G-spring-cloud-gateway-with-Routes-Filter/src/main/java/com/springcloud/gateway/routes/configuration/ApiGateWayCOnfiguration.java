package com.springcloud.gateway.routes.configuration;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGateWayCOnfiguration {
	@Bean
	public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
		return builder.routes()
				
				.route(p -> p.path("/get")  // A
						
				.filters(f -> f.addRequestHeader("MyHeader", "MyURI")
							.addRequestParameter("Param", "MyValue")) // B line number 17,18
						
				.uri("http://httpbin.org:80"))  // C
						
				 
				.route(p -> p.path("/currency-exchange/**")
							.uri("lb://currency-exchange"))
				
				.route(p -> p.path("/currency-conversion/**")
							.uri("lb://currency-conversion"))
				
				.route(p -> p.path("/currency-conversion-feign/**")
							.uri("lb://currency-conversion"))
				
				.route(p-> p.path("/currency-conversion-new/**")
								.filters(f -> f.rewritePath("/currency-conversion-new/(?<segment>.*)",
															"/currency-conversion-feign/${segment}"))
								.uri("lb://currency-conversion"))
				//D From line number 25 to 36
				.build();//E
	}
}

/*
Case 1: Comments all A,B,C,D, un-comments E - Default behavior as we have seen in previous program
Case 2: Un-comments A,C,E
	Try to access - http://localhost:8765/get and analyze it.
Case 3: Un-comments A,B,C,E, adding few headers in the request.

*/

