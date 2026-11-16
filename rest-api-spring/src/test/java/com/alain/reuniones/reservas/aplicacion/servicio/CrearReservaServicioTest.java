package com.alain.reuniones.reservas.aplicacion.servicio;

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
import com.alain.reuniones.reservas.dominio.RepositorioReservas;
import com.alain.reuniones.reservas.dominio.Reserva;
import com.alain.reuniones.reservas.dominio.SalaNoDisponibleException;
import com.alain.reuniones.salas.aplicacion.api.BuscadorSalas;
import com.alain.reuniones.salas.dominio.IdSala;
import com.alain.reuniones.salas.dominio.SalaNoEncontradaException;

@ExtendWith(MockitoExtension.class)
class CrearReservaServicioTest {

	private static final UUID ID_SALA = UUID.fromString("2f1c9d3a-4b5e-4c6d-8f7a-9b0c1d2e3f40");

	private static final UUID ID_RESERVA = UUID.fromString("7b6c5d4e-3f2a-4b1c-9d8e-7f6a5b4c3d2e");

	private static final Instant INICIO = Instant.parse("2026-11-15T10:00:00Z");

	private static final Instant FIN = Instant.parse("2026-11-15T11:00:00Z");

	private static final Instant AHORA = Instant.parse("2026-11-15T09:00:00Z");

	@Mock
	private RepositorioReservas repositorio;

	@Mock
	private BuscadorSalas buscadorSalas;

	@Mock
	private Reloj reloj;

	@Mock
	private GeneradorId generadorId;

	@InjectMocks
	private CrearReservaServicio servicio;

	@Test
	void deberia_guardar_la_reserva_cuando_la_sala_esta_libre() {
		given(buscadorSalas.existe(new IdSala(ID_SALA))).willReturn(true);
		given(repositorio.existeSolapada(any(), any())).willReturn(false);
		given(generadorId.nuevo()).willReturn(ID_RESERVA);
		given(reloj.ahora()).willReturn(AHORA);
		given(repositorio.guardar(any(Reserva.class))).willAnswer(invocacion -> invocacion.getArgument(0));

		Reserva reserva = servicio.crear(ID_SALA, "Ana Pérez", "ana@example.com", INICIO, FIN);

		assertThat(reserva.id()).isEqualTo(ID_RESERVA);
		assertThat(reserva.salaId().valor()).isEqualTo(ID_SALA);
		assertThat(reserva.estado().name()).isEqualTo("ACTIVA");
		assertThat(reserva.rango().inicio()).isEqualTo(INICIO);
		then(repositorio).should().guardar(any(Reserva.class));
	}

	@Test
	void deberia_lanzar_conflicto_cuando_la_sala_ya_esta_ocupada() {
		given(buscadorSalas.existe(new IdSala(ID_SALA))).willReturn(true);
		given(repositorio.existeSolapada(any(), any())).willReturn(true);

		assertThatThrownBy(() -> servicio.crear(ID_SALA, "Ana Pérez", "ana@example.com", INICIO, FIN))
			.isInstanceOf(SalaNoDisponibleException.class)
			.hasMessageContaining(ID_SALA.toString());
		then(repositorio).should(never()).guardar(any());
	}

	@Test
	void deberia_lanzar_sala_no_encontrada_cuando_la_sala_no_existe() {
		given(buscadorSalas.existe(new IdSala(ID_SALA))).willReturn(false);

		assertThatThrownBy(() -> servicio.crear(ID_SALA, "Ana Pérez", "ana@example.com", INICIO, FIN))
			.isInstanceOf(SalaNoEncontradaException.class);
		then(repositorio).should(never()).guardar(any());
	}

}
