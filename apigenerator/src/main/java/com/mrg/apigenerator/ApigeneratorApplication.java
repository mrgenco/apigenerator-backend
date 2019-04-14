package com.mrg.apigenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author M.Rasid Gencosmanoglu
 *
 */
@SpringBootApplication
public class ApigeneratorApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ApigeneratorApplication.class, args);
	}
	
	@Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                		.allowedOrigins("*")
                		.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
            }
        };
    }
	
}
