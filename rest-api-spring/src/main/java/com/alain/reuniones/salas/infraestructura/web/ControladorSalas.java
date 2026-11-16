package com.alain.reuniones.salas.infraestructura.web;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.alain.reuniones.compartido.infraestructura.web.RespuestaPagina;
import com.alain.reuniones.salas.aplicacion.CrearSala;
import com.alain.reuniones.salas.aplicacion.ListarSalas;
import com.alain.reuniones.salas.aplicacion.ObtenerSala;
import com.alain.reuniones.salas.dominio.IdSala;
import com.alain.reuniones.salas.dominio.Sala;
import com.alain.reuniones.salas.infraestructura.web.dto.CrearSalaSolicitud;
import com.alain.reuniones.salas.infraestructura.web.dto.SalaRespuesta;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/v1/salas")
@Validated
public class ControladorSalas {

	private final CrearSala crearSala;

	private final ListarSalas listarSalas;

	private final ObtenerSala obtenerSala;

	public ControladorSalas(CrearSala crearSala, ListarSalas listarSalas, ObtenerSala obtenerSala) {
		this.crearSala = crearSala;
		this.listarSalas = listarSalas;
		this.obtenerSala = obtenerSala;
	}

	@Operation(summary = "Crea una sala", description = "Requiere token JWT")
	@SecurityRequirement(name = "bearer-jwt")
	@ApiResponse(responseCode = "409", description = "Ya existe una sala con ese nombre")
	@PostMapping
	public ResponseEntity<SalaRespuesta> crear(@Valid @RequestBody CrearSalaSolicitud solicitud) {
		Sala sala = crearSala.crear(solicitud.nombre(), solicitud.capacidad(), solicitud.ubicacion());
		URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(sala.id().valor())
			.toUri();
		return ResponseEntity.created(ubicacion).body(SalaRespuesta.de(sala));
	}

	@Operation(summary = "Lista las salas con paginación")
	@GetMapping
	public RespuestaPagina<SalaRespuesta> listar(
			@RequestParam(defaultValue = "0") @Min(value = 0, message = "no puede ser negativo") int pagina,
			@RequestParam(defaultValue = "20") @Min(value = 1, message = "debe ser al menos 1") @Max(value = 100, message = "no puede superar 100") int tamano) {
		return RespuestaPagina.de(listarSalas.listar(pagina, tamano), SalaRespuesta::de);
	}

	@Operation(summary = "Obtiene una sala por su identificador")
	@GetMapping("/{id}")
	public SalaRespuesta obtener(@PathVariable UUID id) {
		return SalaRespuesta.de(obtenerSala.obtener(new IdSala(id)));
	}

}
