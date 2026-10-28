package com.coppel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class MainApplication {

	@Value("${app.allowedOrigins}")
	private String allowedOrigins;

	@Value("${app.allowedMethods}")
	private String allowedMethods;
	
	@Value("${app.allowedHeaders}")
	private String allowedHeaders;

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}

	/**
     * Configuración para permitir el acceso
     * a los servicios desde cualquier origen
     * @return WebMvcConfigurer - configuración
     */
    @Bean
    WebMvcConfigurer corsConfigurer() 
    {    
        return new WebMvcConfigurer() 
        {
			/**
			 * Configuración para permitir el acceso
			 */
			@Override
			public void addCorsMappings(CorsRegistry registry)
			{
				String[] origins = allowedOrigins.split(",");
				String[] methods = allowedMethods.split(",");
				String[] headers = allowedHeaders.split(",");

				registry.addMapping("/**")
					.allowedOrigins(origins)
					.allowedMethods(methods)
					.allowedHeaders(headers);
			}
        };
    }
}
