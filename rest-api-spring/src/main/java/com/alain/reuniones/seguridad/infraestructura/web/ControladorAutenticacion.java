package com.alain.reuniones.seguridad.infraestructura.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alain.reuniones.seguridad.aplicacion.AutenticarUsuario;
import com.alain.reuniones.seguridad.aplicacion.EmisorToken;
import com.alain.reuniones.seguridad.infraestructura.web.dto.RespuestaToken;
import com.alain.reuniones.seguridad.infraestructura.web.dto.SolicitudInicioSesion;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/sesiones")
public class ControladorAutenticacion {

	private final AutenticarUsuario autenticarUsuario;

	private final EmisorToken emisorToken;

	public ControladorAutenticacion(AutenticarUsuario autenticarUsuario, EmisorToken emisorToken) {
		this.autenticarUsuario = autenticarUsuario;
		this.emisorToken = emisorToken;
	}

	@PostMapping
	public RespuestaToken iniciar(@Valid @RequestBody SolicitudInicioSesion solicitud) {
		AutenticarUsuario.UsuarioAutenticado usuario = autenticarUsuario.autenticar(solicitud.usuario(),
				solicitud.clave());
		EmisorToken.TokenEmitido token = emisorToken.emitir(usuario.usuario(), usuario.roles());
		return new RespuestaToken(token.valor(), "Bearer", token.expiraEnSegundos());
	}

}
