package com.hotmart.handson.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "hands-on-2024", description="hands-on-2024", version = "1.0"),
        servers = {
                @Server(url = "http://localhost:8080", description = "local"),
                @Server(url = "https://hands-on-2024.buildstaging.com", description = "staging"),
                @Server(url = "https://hands-on-2024.devops.hotmart.com", description = "production"),
        }
)
public class OpenApiConfiguration {
}
