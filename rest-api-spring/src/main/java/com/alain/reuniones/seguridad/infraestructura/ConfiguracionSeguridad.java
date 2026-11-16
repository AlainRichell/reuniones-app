package com.alain.reuniones.seguridad.infraestructura;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class ConfiguracionSeguridad {

	@Bean
	SecurityFilterChain cadena(HttpSecurity http, PuntoEntradaNoAutenticado puntoEntrada) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
			.cors(Customizer.withDefaults())
			.sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(autorizacion -> autorizacion
				.requestMatchers(HttpMethod.GET, "/api/v1/salas/**", "/api/v1/reservas/**")
				.permitAll()
				.requestMatchers(HttpMethod.POST, "/api/v1/sesiones")
				.permitAll()
				.requestMatchers("/actuator/health", "/actuator/info", "/v3/api-docs/**", "/swagger-ui/**",
						"/swagger-ui.html")
				.permitAll()
				.anyRequest()
				.authenticated())
			.oauth2ResourceServer(recurso -> recurso.jwt(Customizer.withDefaults())
				.authenticationEntryPoint(puntoEntrada))
			.exceptionHandling(manejo -> manejo.authenticationEntryPoint(puntoEntrada))
			.httpBasic(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable);
		return http.build();
	}

	@Bean
	PasswordEncoder codificadorClaves() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager gestorAutenticacion(AuthenticationConfiguration configuracion) throws Exception {
		return configuracion.getAuthenticationManager();
	}

	@Bean
	CorsConfigurationSource fuenteCors(@Value("${aplicacion.cors.origenes:http://localhost:4200,http://localhost:8081}") List<String> origenes) {
		CorsConfiguration configuracion = new CorsConfiguration();
		configuracion.setAllowedOrigins(origenes);
		configuracion.setAllowedMethods(List.of("GET", "POST", "DELETE", "OPTIONS"));
		configuracion.setAllowedHeaders(List.of("*"));
		UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
		fuente.registerCorsConfiguration("/**", configuracion);
		return fuente;
	}

}
