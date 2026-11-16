package com.alain.reuniones.compartido.infraestructura.config;

import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracionZonaHoraria {

	@Bean
	ZoneId zonaHoraria(@Value("${aplicacion.zona-horaria:America/La_Paz}") String zona) {
		return ZoneId.of(zona);
	}

}
