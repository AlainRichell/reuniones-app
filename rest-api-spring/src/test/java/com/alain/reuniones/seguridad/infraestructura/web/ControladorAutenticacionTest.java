package com.alain.reuniones.seguridad.infraestructura.web;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.alain.reuniones.seguridad.aplicacion.AutenticarUsuario;
import com.alain.reuniones.seguridad.aplicacion.CredencialesInvalidasException;
import com.alain.reuniones.seguridad.aplicacion.EmisorToken;

@WebMvcTest(ControladorAutenticacion.class)
@AutoConfigureMockMvc(addFilters = false)
class ControladorAutenticacionTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AutenticarUsuario autenticarUsuario;

	@MockitoBean
	private EmisorToken emisorToken;

	@Test
	void deberia_devolver_el_token_cuando_las_credenciales_son_validas() throws Exception {
		given(autenticarUsuario.autenticar("admin", "admin123"))
			.willReturn(new AutenticarUsuario.UsuarioAutenticado("admin", List.of("ADMIN")));
		given(emisorToken.emitir("admin", List.of("ADMIN")))
			.willReturn(new EmisorToken.TokenEmitido("jwt-de-prueba", 3600));

		mockMvc
			.perform(post("/api/v1/sesiones").contentType(APPLICATION_JSON)
				.content("""
						{"usuario":"admin","clave":"admin123"}
						"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.tokenAcceso").value("jwt-de-prueba"))
			.andExpect(jsonPath("$.tipoToken").value("Bearer"))
			.andExpect(jsonPath("$.expiraEn").value(3600));
	}

	@Test
	void deberia_devolver_400_cuando_faltan_las_credenciales() throws Exception {
		mockMvc
			.perform(post("/api/v1/sesiones").contentType(APPLICATION_JSON)
				.content("""
						{"usuario":"","clave":""}
						"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.codigo").value("DATOS_INVALIDOS"))
			.andExpect(jsonPath("$.errores[*].campo", hasItem("usuario")))
			.andExpect(jsonPath("$.errores[*].campo", hasItem("clave")));
	}

	@Test
	void deberia_devolver_401_cuando_las_credenciales_no_son_correctas() throws Exception {
		given(autenticarUsuario.autenticar("admin", "incorrecta"))
			.willThrow(new CredencialesInvalidasException());

		mockMvc
			.perform(post("/api/v1/sesiones").contentType(APPLICATION_JSON)
				.content("""
						{"usuario":"admin","clave":"incorrecta"}
						"""))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.codigo").value("NO_AUTENTICADO"));
	}

}
