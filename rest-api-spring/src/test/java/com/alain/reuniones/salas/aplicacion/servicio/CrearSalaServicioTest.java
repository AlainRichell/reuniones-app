package com.alain.reuniones.salas.aplicacion.servicio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alain.reuniones.compartido.aplicacion.GeneradorId;
import com.alain.reuniones.compartido.aplicacion.Reloj;
import com.alain.reuniones.salas.dominio.NombreSalaDuplicadoException;
import com.alain.reuniones.salas.dominio.RepositorioSalas;
import com.alain.reuniones.salas.dominio.Sala;

@ExtendWith(MockitoExtension.class)
class CrearSalaServicioTest {

	private static final UUID ID = UUID.fromString("2f1c9d3a-4b5e-4c6d-8f7a-9b0c1d2e3f40");

	private static final Instant AHORA = Instant.parse("2026-11-15T14:00:00Z");

	@Mock
	private RepositorioSalas repositorio;

	@Mock
	private Reloj reloj;

	@Mock
	private GeneradorId generadorId;

	@InjectMocks
	private CrearSalaServicio servicio;

	@Test
	void deberia_guardar_la_sala_cuando_el_nombre_esta_libre() {
		given(repositorio.existePorNombre("Aurora")).willReturn(false);
		given(generadorId.nuevo()).willReturn(ID);
		given(reloj.ahora()).willReturn(AHORA);
		given(repositorio.guardar(any(Sala.class))).willAnswer(invocacion -> invocacion.getArgument(0));

		Sala sala = servicio.crear("Aurora", 8, "Piso 2");

		assertThat(sala.id().valor()).isEqualTo(ID);
		assertThat(sala.nombre()).isEqualTo("Aurora");
		assertThat(sala.capacidad()).isEqualTo(8);
		assertThat(sala.creadaEn()).isEqualTo(AHORA);
		then(repositorio).should().guardar(any(Sala.class));
	}

	@Test
	void deberia_lanzar_conflicto_cuando_el_nombre_ya_existe() {
		given(repositorio.existePorNombre("Aurora")).willReturn(true);

		assertThatThrownBy(() -> servicio.crear("Aurora", 8, null))
			.isInstanceOf(NombreSalaDuplicadoException.class)
			.hasMessageContaining("Aurora");
		then(repositorio).should(never()).guardar(any());
	}

}
