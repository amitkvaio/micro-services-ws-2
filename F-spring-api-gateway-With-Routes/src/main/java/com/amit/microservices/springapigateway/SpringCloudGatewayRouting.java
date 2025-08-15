package com.amit.microservices.springapigateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringCloudGatewayRouting {
	@Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/currency-exchange/**")  // Match all requests starting with this
                        .uri("lb://CURRENCY-EXCHANGE")) // Forward to Eureka service name
                .route(p -> p
                        .path("/currency-conversion/**")  // Match all requests starting with this
                        .uri("lb://CURRENCY-CONVERSION")) // Forward to Eureka service name
                .route(p -> p
                        .path("/currency-conversion-feign/**")  // Match all requests starting with this
                        .uri("lb://CURRENCY-CONVERSION")) // Forward to Eureka service name
                .build();
    }
}

/*
@Configuration → Marks this as a Spring configuration class.
@Bean → This method creates and returns a RouteLocator object.
RouteLocator → Defines the routing rules for the gateway.

.route(p -> p
            .path("/currency-exchange/**")  // Match all requests starting with this
            .uri("lb://CURRENCY-EXCHANGE")) // Forward to Eureka service name

Path: /currency-exchange/** → Matches all requests starting with /currency-exchange/.
URI: lb://currency-exchange means Load Balancer will find the currency-exchange service from Eureka.

📌 This works because Eureka has the service registered.


1.	Incoming request
	The route matches any URL starting with /currency-exchange/**.
	Example:
	http://localhost:8765/currency-exchange/from/USD/to/INR

2.	Gateway recognizes the route
	Because of .path("/currency-exchange/**"), the Spring Cloud Gateway knows this request should be routed to whatever is configured in .uri(...).
		lb://CURRENCY-EXCHANGE meaning
		lb:// means Load Balancer.
		CURRENCY-EXCHANGE is not a hostname — it is the service ID registered in Eureka.

4.	Gateway asks Eureka
	Gateway says: “Hey Eureka, give me an available instance for CURRENCY-EXCHANGE.”
	Eureka responds with the actual URL(s) of the instance(s), e.g.:
	http://localhost:8000
	or
	http://192.168.1.5:8000
	
5.	Gateway forwards the request
	Gateway sends the request to that resolved instance, keeping the path after /currency-exchange/ intact.
	So /currency-exchange/from/USD/to/INR becomes:
	http://localhost:8000/currency-exchange/from/USD/to/INR

6.	Benefit
	We don’t hardcode service URLs.
	Eureka + lb:// automatically handles service discovery and load balancing.

*/