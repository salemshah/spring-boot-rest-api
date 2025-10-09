package com.ecommerce.ecommerce.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("ecommerce-api")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spring Boot E-Commerce API")
                        .version("v1.0")
                        .description("A Spring Boot–based RESTFul API for an e-commerce platform.")
                        // 👇 Add author/contact info here
                        .contact(new Contact()
                                .name("Salem Shah")
                                .email("salemshahdev@gmail.com")
                                .url("https://salemshah.com"))
                        // 👇 Optional: Add license info too
//                        .license(new License()
//                                .name("Apache 2.0")
//                                .url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                )
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.myecommerce.com")
                                .description("Production Server")
                ));
    }
}
