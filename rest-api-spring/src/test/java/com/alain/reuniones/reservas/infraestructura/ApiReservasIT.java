package com.alain.reuniones.reservas.infraestructura;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.alain.reuniones.PruebaIntegracionBase;

class ApiReservasIT extends PruebaIntegracionBase {

	@Test
	void deberia_gestionar_el_ciclo_completo_de_una_reserva() {
		UUID salaId = crearSala("Aurora IT " + UUID.randomUUID());

		ResponseEntity<String> creada = reservar(salaId, "2026-11-20T10:00:00Z", "2026-11-20T12:00:00Z");
		assertThat(creada.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		UUID reservaId = UUID.fromString(texto(creada, "$.id"));

		ResponseEntity<String> solapada = reservar(salaId, "2026-11-20T11:00:00Z", "2026-11-20T13:00:00Z");
		assertThat(solapada.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
		assertThat(texto(solapada, "$.codigo")).isEqualTo("SALA_NO_DISPONIBLE");

		ResponseEntity<String> listado = enviar(HttpMethod.GET,
				"/api/v1/reservas?salaId=" + salaId + "&fecha=2026-11-20&tamano=5", null);
		assertThat(listado.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(numero(listado, "$.totalElementos")).isEqualTo(1);
		assertThat(texto(listado, "$.contenido[0].sala.nombre")).startsWith("Aurora IT");

		ResponseEntity<String> cancelada = enviar(HttpMethod.DELETE, "/api/v1/reservas/" + reservaId, null);
		assertThat(cancelada.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<String> segundaCancelacion = enviar(HttpMethod.DELETE, "/api/v1/reservas/" + reservaId, null);
		assertThat(segundaCancelacion.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
		assertThat(texto(segundaCancelacion, "$.codigo")).isEqualTo("RESERVA_YA_CANCELADA");

		assertThat(reservar(salaId, "2026-11-20T10:00:00Z", "2026-11-20T12:00:00Z").getStatusCode())
			.isEqualTo(HttpStatus.CREATED);
	}

}
