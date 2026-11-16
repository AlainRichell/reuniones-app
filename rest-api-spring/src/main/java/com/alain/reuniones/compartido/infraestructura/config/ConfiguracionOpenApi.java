package com.alain.reuniones.compartido.infraestructura.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class ConfiguracionOpenApi {

	@Bean
	OpenAPI api() {
		return new OpenAPI()
			.info(new Info().title("API de Reserva de Salas")
				.version("1.0.0")
				.description("""
						API REST para crear salas y reservarlas por franjas horarias.
						Las lecturas son públicas; las escrituras requieren un token JWT
						obtenido en POST /api/v1/sesiones."""))
			.servers(List.of(new Server().url("http://localhost:8080").description("Local"),
					new Server().url("http://localhost:8081").description("Docker")))
			.components(new Components().addSecuritySchemes("bearer-jwt",
					new SecurityScheme().type(SecurityScheme.Type.HTTP)
						.scheme("bearer")
						.bearerFormat("JWT")
						.description("Token obtenido en POST /api/v1/sesiones")));
	}

}
