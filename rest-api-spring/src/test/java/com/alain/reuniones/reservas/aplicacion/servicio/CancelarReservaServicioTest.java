package com.alain.reuniones.reservas.aplicacion.servicio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alain.reuniones.compartido.aplicacion.Reloj;
import com.alain.reuniones.reservas.dominio.RangoHorario;
import com.alain.reuniones.reservas.dominio.RepositorioReservas;
import com.alain.reuniones.reservas.dominio.Reserva;
import com.alain.reuniones.reservas.dominio.ReservaYaCanceladaException;
import com.alain.reuniones.salas.dominio.IdSala;

@ExtendWith(MockitoExtension.class)
class CancelarReservaServicioTest {

	private static final UUID ID_RESERVA = UUID.fromString("7b6c5d4e-3f2a-4b1c-9d8e-7f6a5b4c3d2e");

	private static final UUID ID_SALA = UUID.fromString("2f1c9d3a-4b5e-4c6d-8f7a-9b0c1d2e3f40");

	private static final Instant INICIO = Instant.parse("2026-11-15T10:00:00Z");

	private static final Instant FIN = Instant.parse("2026-11-15T11:00:00Z");

	private static final Instant AHORA = Instant.parse("2026-11-15T12:00:00Z");

	private static final Instant CANCELACION = Instant.parse("2026-11-15T13:00:00Z");

	@Mock
	private RepositorioReservas repositorio;

	@Mock
	private Reloj reloj;

	@InjectMocks
	private CancelarReservaServicio servicio;

	@Test
	void deberia_cancelar_la_reserva_cuando_esta_activa() {
		given(repositorio.buscarPorId(ID_RESERVA)).willReturn(Optional.of(reserva()));
		given(reloj.ahora()).willReturn(CANCELACION);
		given(repositorio.guardar(any(Reserva.class))).willAnswer(invocacion -> invocacion.getArgument(0));

		Reserva cancelada = servicio.cancelar(ID_RESERVA);

		assertThat(cancelada.estado().name()).isEqualTo("CANCELADA");
		assertThat(cancelada.canceladaEn()).contains(CANCELACION);
	}

	@Test
	void deberia_lanzar_conflicto_cuando_ya_estaba_cancelada() {
		given(repositorio.buscarPorId(ID_RESERVA)).willReturn(Optional.of(reserva().cancelar(CANCELACION)));

		assertThatThrownBy(() -> servicio.cancelar(ID_RESERVA))
			.isInstanceOf(ReservaYaCanceladaException.class)
			.hasMessageContaining(ID_RESERVA.toString());
		then(repositorio).should(never()).guardar(any());
	}

	private Reserva reserva() {
		return Reserva.programar(ID_RESERVA, new IdSala(ID_SALA), "Ana Pérez", "ana@example.com",
				new RangoHorario(INICIO, FIN), AHORA);
	}

}
