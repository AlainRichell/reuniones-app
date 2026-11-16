package com.alain.reuniones.reservas.infraestructura.web.dto;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CrearReservaSolicitud(

		@NotNull(message = "es obligatoria") UUID salaId,

		@NotBlank(message = "es obligatorio") @Size(min = 2, max = 120, message = "debe tener entre 2 y 120 caracteres") String nombreResponsable,

		@NotBlank(message = "es obligatorio") @Email(message = "no tiene un formato válido") @Size(max = 180, message = "no puede superar 180 caracteres") String correoResponsable,

		@NotNull(message = "es obligatorio") Instant inicio,

		@NotNull(message = "es obligatorio") Instant fin) {
}
