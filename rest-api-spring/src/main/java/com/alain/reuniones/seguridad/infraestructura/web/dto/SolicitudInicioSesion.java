package com.alain.reuniones.seguridad.infraestructura.web.dto;

import jakarta.validation.constraints.NotBlank;

public record SolicitudInicioSesion(

		@NotBlank(message = "es obligatorio") String usuario,

		@NotBlank(message = "es obligatoria") String clave) {
}
