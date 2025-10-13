// MealquestBackendApplication.java

package com.mealquest;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableScheduling
public class MealquestBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(MealquestBackendApplication.class, args);
    }
}