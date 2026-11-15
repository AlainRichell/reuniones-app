package com.alain.reuniones.salas.dominio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class SalaTest {

	private static final IdSala ID = new IdSala(UUID.fromString("2f1c9d3a-4b5e-4c6d-8f7a-9b0c1d2e3f40"));

	private static final Instant AHORA = Instant.parse("2026-11-15T14:00:00Z");

	@Test
	void deberia_recortar_el_nombre_y_la_ubicacion_al_crear() {
		Sala sala = Sala.crear(ID, "  Aurora  ", 8, "  Piso 2  ", AHORA);

		assertThat(sala.nombre()).isEqualTo("Aurora");
		assertThat(sala.ubicacion()).contains("Piso 2");
	}

	@Test
	void deberia_guardar_la_ubicacion_vacia_como_ausente() {
		Sala sala = Sala.crear(ID, "Aurora", 8, "   ", AHORA);

		assertThat(sala.ubicacion()).isEmpty();
	}

	@Test
	void deberia_rechazar_una_capacidad_fuera_de_rango() {
		assertThatThrownBy(() -> Sala.crear(ID, "Aurora", 0, null, AHORA))
			.isInstanceOf(SalaInvalidaException.class)
			.hasMessageContaining("capacidad");
	}

	@Test
	void deberia_rechazar_un_nombre_demasiado_corto() {
		assertThatThrownBy(() -> Sala.crear(ID, "ab", 8, null, AHORA))
			.isInstanceOf(SalaInvalidaException.class)
			.hasMessageContaining("nombre");
	}

}
