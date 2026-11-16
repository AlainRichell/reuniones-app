package com.alain.reuniones.reservas.infraestructura.web;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.alain.reuniones.compartido.dominio.Pagina;
import com.alain.reuniones.reservas.aplicacion.BuscarReservas;
import com.alain.reuniones.reservas.aplicacion.CancelarReserva;
import com.alain.reuniones.reservas.aplicacion.CrearReserva;
import com.alain.reuniones.reservas.aplicacion.ObtenerReserva;
import com.alain.reuniones.reservas.dominio.OrdenReserva;
import com.alain.reuniones.reservas.dominio.RangoHorario;
import com.alain.reuniones.reservas.dominio.Reserva;
import com.alain.reuniones.reservas.dominio.SalaNoDisponibleException;
import com.alain.reuniones.salas.aplicacion.api.BuscadorSalas;
import com.alain.reuniones.salas.aplicacion.api.BuscadorSalas.SalaBreve;
import com.alain.reuniones.salas.dominio.IdSala;

@WebMvcTest(ControladorReservas.class)
@AutoConfigureMockMvc(addFilters = false)
class ControladorReservasTest {

	private static final UUID ID_SALA = UUID.fromString("2f1c9d3a-4b5e-4c6d-8f7a-9b0c1d2e3f40");

	private static final UUID ID_RESERVA = UUID.fromString("7b6c5d4e-3f2a-4b1c-9d8e-7f6a5b4c3d2e");

	private static final Instant INICIO = Instant.parse("2026-11-15T10:00:00Z");

	private static final Instant FIN = Instant.parse("2026-11-15T11:00:00Z");

	private static final Reserva RESERVA = Reserva.programar(ID_RESERVA, new IdSala(ID_SALA), "Ana Pérez",
			"ana@example.com", new RangoHorario(INICIO, FIN), Instant.parse("2026-11-15T09:00:00Z"));

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CrearReserva crearReserva;

	@MockitoBean
	private CancelarReserva cancelarReserva;

	@MockitoBean
	private BuscarReservas buscarReservas;

	@MockitoBean
	private ObtenerReserva obtenerReserva;

	@MockitoBean
	private BuscadorSalas buscadorSalas;

	@Test
	void deberia_devolver_201_cuando_la_reserva_se_crea() throws Exception {
		given(crearReserva.crear(eq(ID_SALA), eq("Ana Pérez"), eq("ana@example.com"), any(), any()))
			.willReturn(RESERVA);
		given(buscadorSalas.buscar(any())).willReturn(new SalaBreve(ID_SALA, "Aurora"));

		mockMvc
			.perform(post("/api/v1/reservas").contentType(APPLICATION_JSON).content("""
					{"salaId":"%s","nombreResponsable":"Ana Pérez","correoResponsable":"ana@example.com",
					 "inicio":"2026-11-15T10:00:00Z","fin":"2026-11-15T11:00:00Z"}
					""".formatted(ID_SALA)))
			.andExpect(status().isCreated())
			.andExpect(header().exists("Location"))
			.andExpect(jsonPath("$.sala.nombre").value("Aurora"))
			.andExpect(jsonPath("$.estado").value("ACTIVA"));
	}

	@Test
	void deberia_devolver_400_con_errores_cuando_la_solicitud_es_invalida() throws Exception {
		mockMvc
			.perform(post("/api/v1/reservas").contentType(APPLICATION_JSON).content("""
					{"nombreResponsable":"","correoResponsable":"no-es-correo"}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.codigo").value("DATOS_INVALIDOS"))
			.andExpect(jsonPath("$.errores[*].campo", hasItem("salaId")))
			.andExpect(jsonPath("$.errores[*].campo", hasItem("correoResponsable")));
	}

	@Test
	void deberia_devolver_409_cuando_la_sala_esta_ocupada() throws Exception {
		given(crearReserva.crear(any(), any(), any(), any(), any()))
			.willThrow(new SalaNoDisponibleException("La sala ya está reservada"));

		mockMvc
			.perform(post("/api/v1/reservas").contentType(APPLICATION_JSON).content("""
					{"salaId":"%s","nombreResponsable":"Ana Pérez","correoResponsable":"ana@example.com",
					 "inicio":"2026-11-15T10:00:00Z","fin":"2026-11-15T11:00:00Z"}
					""".formatted(ID_SALA)))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.codigo").value("SALA_NO_DISPONIBLE"));
	}

	@Test
	void deberia_devolver_200_con_la_pagina_cuando_lista() throws Exception {
		given(buscarReservas.buscar(null, null, null, OrdenReserva.INICIO, 0, 20))
			.willReturn(new Pagina<>(List.of(RESERVA), 0, 20, 1));
		given(buscadorSalas.nombresDe(any())).willReturn(Map.of(ID_SALA, "Aurora"));

		mockMvc
			.perform(get("/api/v1/reservas"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.contenido[0].sala.nombre").value("Aurora"))
			.andExpect(jsonPath("$.totalElementos").value(1))
			.andExpect(jsonPath("$.totalPaginas").value(1));
	}

}
