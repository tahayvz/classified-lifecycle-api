package com.marketplace.classifieds.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI classifiedsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Classified Lifecycle API")
                        .description("API for classified listing lifecycle management")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Project Maintainers")
                                .email("maintainers@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Classified Lifecycle API Docs")
                        .url("https://github.com/your-username/classified-lifecycle-api"));
    }
}
