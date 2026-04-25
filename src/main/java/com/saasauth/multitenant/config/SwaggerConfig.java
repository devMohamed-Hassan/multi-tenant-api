package com.saasauth.multitenant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

     @Bean
     public OpenAPI openAPI() {
          return new OpenAPI()
                    .info(new Info()
                              .title("Multi-Tenant API")
                              .description("Multi-tenant SaaS authentication API with JWT")
                              .version("v1.0.0")
                              .contact(new Contact()
                                        .name("Your Name")
                                        .email("your@email.com")))
                    .addSecurityItem(new SecurityRequirement()
                              .addList("Bearer Authentication"))
                    .components(new Components()
                              .addSecuritySchemes("Bearer Authentication",
                                        new SecurityScheme()
                                                  .type(SecurityScheme.Type.HTTP)
                                                  .scheme("bearer")
                                                  .bearerFormat("JWT")
                                                  .description("Enter your JWT token")));
     }
}