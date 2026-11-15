package com.alain.reuniones.salas.infraestructura.web;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.alain.reuniones.compartido.dominio.Pagina;
import com.alain.reuniones.salas.aplicacion.CrearSala;
import com.alain.reuniones.salas.aplicacion.ListarSalas;
import com.alain.reuniones.salas.aplicacion.ObtenerSala;
import com.alain.reuniones.salas.dominio.IdSala;
import com.alain.reuniones.salas.dominio.Sala;

@WebMvcTest(ControladorSalas.class)
class ControladorSalasTest {

	private static final UUID ID = UUID.fromString("2f1c9d3a-4b5e-4c6d-8f7a-9b0c1d2e3f40");

	private static final Sala SALA = Sala.crear(new IdSala(ID), "Aurora", 8, "Piso 2",
			Instant.parse("2026-11-15T14:00:00Z"));

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CrearSala crearSala;

	@MockitoBean
	private ListarSalas listarSalas;

	@MockitoBean
	private ObtenerSala obtenerSala;

	@Test
	void deberia_devolver_201_cuando_la_sala_se_crea() throws Exception {
		given(crearSala.crear("Aurora", 8, "Piso 2")).willReturn(SALA);

		mockMvc
			.perform(post("/api/v1/salas").contentType(APPLICATION_JSON)
				.content("""
						{"nombre":"Aurora","capacidad":8,"ubicacion":"Piso 2"}
						"""))
			.andExpect(status().isCreated())
			.andExpect(header().exists("Location"))
			.andExpect(jsonPath("$.id").value(ID.toString()))
			.andExpect(jsonPath("$.nombre").value("Aurora"));
	}

	@Test
	void deberia_devolver_400_con_errores_cuando_la_solicitud_es_invalida() throws Exception {
		mockMvc
			.perform(post("/api/v1/salas").contentType(APPLICATION_JSON)
				.content("""
						{"nombre":"ab","capacidad":0}
						"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.codigo").value("DATOS_INVALIDOS"))
			.andExpect(jsonPath("$.errores[*].campo", hasItem("nombre")))
			.andExpect(jsonPath("$.errores[*].campo", hasItem("capacidad")));
	}

	@Test
	void deberia_devolver_200_con_la_pagina_cuando_lista() throws Exception {
		given(listarSalas.listar(0, 20)).willReturn(new Pagina<>(List.of(SALA), 0, 20, 1));

		mockMvc
			.perform(get("/api/v1/salas"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.contenido[0].nombre").value("Aurora"))
			.andExpect(jsonPath("$.totalElementos").value(1))
			.andExpect(jsonPath("$.totalPaginas").value(1));
	}

}
