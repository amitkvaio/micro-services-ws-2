# API Gateway With Manual Routes

This project shows how to define Spring Cloud Gateway routes in Java code.
Instead of depending only on discovery locator, we manually decide which paths should go to which service.

## Route Configuration

```java
@Configuration
public class SpringCloudGatewayRouting {

    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/currency-exchange/**")
                        .uri("lb://CURRENCY-EXCHANGE"))
                .route(p -> p
                        .path("/currency-conversion/**")
                        .uri("lb://CURRENCY-CONVERSION"))
                .route(p -> p
                        .path("/currency-conversion-feign/**")
                        .uri("lb://CURRENCY-CONVERSION"))
                .build();
    }
}
```

## Explanation

- `@Configuration` marks the class as a Spring configuration class.
- `@Bean` creates a Spring bean.
- `RouteLocator` defines the gateway routing rules.
- `.path("/currency-exchange/**")` matches incoming requests.
- `lb://CURRENCY-EXCHANGE` tells Gateway to use load balancing and Eureka service discovery.

## How It Works

1. Client calls Gateway:

```text
http://localhost:8765/currency-exchange/from/USD/to/INR
```

2. Gateway matches the path `/currency-exchange/**`.
3. Gateway asks Eureka for an available `CURRENCY-EXCHANGE` instance.
4. Gateway forwards the request to the selected instance.

Final backend call:

```text
http://localhost:8000/currency-exchange/from/USD/to/INR
```

## Why Manual Routes Are Useful

- We can expose only selected APIs.
- We get better control over route names.
- We can apply filters to specific routes.
- We avoid exposing every Eureka service automatically.

## URL Examples

Uppercase discovery-locator style:

```text
http://localhost:8765/CURRENCY-EXCHANGE/currency-exchange/from/USD/to/INR
http://localhost:8765/CURRENCY-CONVERSION/currency-conversion-feign/from/USD/to/INR/quantity/10
http://localhost:8765/CURRENCY-CONVERSION/currency-conversion/from/USD/to/INR/quantity/10
```

Lowercase discovery-locator style:

```text
http://localhost:8765/currency-exchange/currency-exchange/from/USD/to/INR
http://localhost:8765/currency-conversion/currency-conversion-feign/from/USD/to/INR/quantity/10
http://localhost:8765/currency-conversion/currency-conversion/from/USD/to/INR/quantity/10
```

Manual route style:

```text
http://localhost:8765/currency-exchange/from/USD/to/INR
http://localhost:8765/currency-conversion-feign/from/USD/to/INR/quantity/10
http://localhost:8765/currency-conversion/from/USD/to/INR/quantity/10
```

## Interview Points

- Manual routes give more control than discovery locator.
- `lb://` means load-balanced service lookup.
- Gateway integrates with Eureka to find the actual service instance.
