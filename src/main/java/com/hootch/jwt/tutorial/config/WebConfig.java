package com.hootch.jwt.tutorial.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {



	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("http://localhost:3000","http://localhost:8080")
				.allowedMethods(HttpMethod.POST.name()
								,HttpMethod.GET.name()
								,HttpMethod.PATCH.name()
								,HttpMethod.OPTIONS.name()
								,HttpMethod.PUT.name()
								,HttpMethod.DELETE.name());
	}


}
