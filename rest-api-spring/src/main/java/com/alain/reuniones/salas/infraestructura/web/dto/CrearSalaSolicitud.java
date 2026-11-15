package com.alain.reuniones.salas.infraestructura.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CrearSalaSolicitud(

		@NotBlank(message = "es obligatorio") @Size(min = 3, max = 80, message = "debe tener entre 3 y 80 caracteres") String nombre,

		@Min(value = 1, message = "debe ser al menos 1") @Max(value = 100, message = "no puede superar 100") int capacidad,

		@Size(max = 120, message = "no puede superar 120 caracteres") String ubicacion) {
}
