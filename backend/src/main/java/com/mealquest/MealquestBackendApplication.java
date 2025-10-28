// MealquestBackendApplication.java
package com.mealquest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.mealquest")
@EntityScan("com.mealquest.model")
@EnableJpaRepositories("com.mealquest.repository")
public class MealquestBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(MealquestBackendApplication.class, args);
    }
}