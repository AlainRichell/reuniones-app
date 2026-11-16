package com.alain.reuniones.seguridad.infraestructura;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Devuelve el mismo contrato de error que el resto de la API cuando falta el token. */
@Component
public class PuntoEntradaNoAutenticado implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest peticion, HttpServletResponse respuesta,
			AuthenticationException excepcion) throws IOException {
		respuesta.setStatus(HttpStatus.UNAUTHORIZED.value());
		respuesta.setContentType("application/problem+json");
		respuesta.setCharacterEncoding(StandardCharsets.UTF_8.name());
		respuesta.getWriter()
			.write("""
					{"type":"about:blank","title":"No autenticado","status":401,\
					"detail":"Se requiere un token válido","codigo":"NO_AUTENTICADO","marcaDeTiempo":"%s"}"""
				.formatted(Instant.now()));
	}

}
