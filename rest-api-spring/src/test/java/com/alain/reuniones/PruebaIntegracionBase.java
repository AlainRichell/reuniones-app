package com.alain.reuniones;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.jayway.jsonpath.JsonPath;

/** Un único contenedor de PostgreSQL para toda la JVM: el contexto de Spring se comparte entre clases. */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestPropertySource(properties = "aplicacion.jwt.secreto=clave-de-pruebas-con-mas-de-32-caracteres")
public abstract class PruebaIntegracionBase {

	@ServiceConnection
	static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17-alpine");

	static {
		POSTGRES.start();
	}

	@LocalServerPort
	private int puerto;

	private RestClient cliente;

	private String token;

	@BeforeEach
	void prepararCliente() {
		cliente = RestClient.builder().baseUrl("http://localhost:" + puerto).build();
		token = null;
	}

	protected UUID crearSala(String nombre) {
		ResponseEntity<String> respuesta = enviarConToken(HttpMethod.POST, "/api/v1/salas",
				Map.of("nombre", nombre, "capacidad", 8), tokenAdmin());
		assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		return UUID.fromString(texto(respuesta, "$.id"));
	}

	protected ResponseEntity<String> reservar(UUID salaId, String inicio, String fin) {
		return enviarConToken(HttpMethod.POST, "/api/v1/reservas",
				Map.of("salaId", salaId.toString(), "nombreResponsable", "Ana Pérez", "correoResponsable",
						"ana@example.com", "inicio", inicio, "fin", fin),
				tokenAdmin());
	}

	protected synchronized String tokenAdmin() {
		if (token == null) {
			ResponseEntity<String> inicioSesion = enviar(HttpMethod.POST, "/api/v1/sesiones",
					Map.of("usuario", "admin", "clave", "admin123"));
			assertThat(inicioSesion.getStatusCode()).isEqualTo(HttpStatus.OK);
			token = texto(inicioSesion, "$.tokenAcceso");
		}
		return token;
	}

	protected ResponseEntity<String> enviar(HttpMethod metodo, String ruta, Object cuerpo) {
		return ejecutar(cliente.method(metodo).uri(ruta), cuerpo);
	}

	protected ResponseEntity<String> enviarConToken(HttpMethod metodo, String ruta, Object cuerpo, String token) {
		return ejecutar(cliente.method(metodo).uri(ruta).header(HttpHeaders.AUTHORIZATION, "Bearer " + token),
				cuerpo);
	}

	private ResponseEntity<String> ejecutar(RestClient.RequestBodySpec peticion, Object cuerpo) {
		if (cuerpo != null) {
			peticion.contentType(APPLICATION_JSON).body(cuerpo);
		}
		try {
			return peticion.retrieve().toEntity(String.class);
		}
		catch (RestClientResponseException excepcion) {
			return ResponseEntity.status(excepcion.getStatusCode()).body(excepcion.getResponseBodyAsString());
		}
	}

	protected String texto(ResponseEntity<String> respuesta, String ruta) {
		Object valor = JsonPath.read(respuesta.getBody(), ruta);
		return valor == null ? null : valor.toString();
	}

	protected long numero(ResponseEntity<String> respuesta, String ruta) {
		return ((Number) JsonPath.read(respuesta.getBody(), ruta)).longValue();
	}

}
