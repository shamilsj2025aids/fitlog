package com.fitlog.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fitLogOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FitLog — Personal Workout & Calorie Tracker API")
                        .description("Project Leap Assessment Backend — Java & DBMS (Course Code: U28CS491)\n\n" +
                                "Key Features:\n" +
                                "- Log workouts with type, duration, and calories burnt\n" +
                                "- Log meals with food items, quantity, and calories\n" +
                                "- Daily summary of calories in vs. calories out\n" +
                                "- Weekly trend of completed workouts with goal progression\n" +
                                "- Personal weekly workout-count goals\n" +
                                "- System-wide dashboard overview and search/filter capabilities")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("FitLog Development Team")
                                .email("shamils.j75@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://spring.io")));
    }
}
