package com.alain.reuniones.seguridad.infraestructura;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.alain.reuniones.PruebaIntegracionBase;

class SeguridadIT extends PruebaIntegracionBase {

	@Test
	void deberia_proteger_las_escrituras_y_mantener_publicas_las_lecturas() {
		ResponseEntity<String> sinToken = enviar(HttpMethod.POST, "/api/v1/salas",
				Map.of("nombre", "Segura " + UUID.randomUUID(), "capacidad", 4));
		assertThat(sinToken.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
		assertThat(texto(sinToken, "$.codigo")).isEqualTo("NO_AUTENTICADO");

		assertThat(enviar(HttpMethod.GET, "/api/v1/salas", null).getStatusCode()).isEqualTo(HttpStatus.OK);

		ResponseEntity<String> inicioSesion = enviar(HttpMethod.POST, "/api/v1/sesiones",
				Map.of("usuario", "admin", "clave", "admin123"));
		assertThat(inicioSesion.getStatusCode()).isEqualTo(HttpStatus.OK);
		String token = texto(inicioSesion, "$.tokenAcceso");
		assertThat(token).isNotBlank();

		ResponseEntity<String> conToken = enviarConToken(HttpMethod.POST, "/api/v1/salas",
				Map.of("nombre", "Segura " + UUID.randomUUID(), "capacidad", 4), token);
		assertThat(conToken.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	@Test
	void deberia_rechazar_las_credenciales_incorrectas() {
		ResponseEntity<String> inicioSesion = enviar(HttpMethod.POST, "/api/v1/sesiones",
				Map.of("usuario", "admin", "clave", "incorrecta"));

		assertThat(inicioSesion.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
		assertThat(texto(inicioSesion, "$.codigo")).isEqualTo("NO_AUTENTICADO");
	}

}
