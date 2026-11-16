package com.alain.reuniones.reservas.infraestructura.web;

import java.net.URI;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.alain.reuniones.compartido.dominio.Pagina;
import com.alain.reuniones.compartido.infraestructura.web.RespuestaPagina;
import com.alain.reuniones.reservas.aplicacion.BuscarReservas;
import com.alain.reuniones.reservas.aplicacion.CancelarReserva;
import com.alain.reuniones.reservas.aplicacion.CrearReserva;
import com.alain.reuniones.reservas.aplicacion.ObtenerReserva;
import com.alain.reuniones.reservas.dominio.EstadoReserva;
import com.alain.reuniones.reservas.dominio.OrdenReserva;
import com.alain.reuniones.reservas.dominio.Reserva;
import com.alain.reuniones.reservas.infraestructura.web.dto.CrearReservaSolicitud;
import com.alain.reuniones.reservas.infraestructura.web.dto.ReservaRespuesta;
import com.alain.reuniones.salas.aplicacion.api.BuscadorSalas;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/v1/reservas")
@Validated
public class ControladorReservas {

	private final CrearReserva crearReserva;

	private final CancelarReserva cancelarReserva;

	private final BuscarReservas buscarReservas;

	private final ObtenerReserva obtenerReserva;

	private final BuscadorSalas buscadorSalas;

	public ControladorReservas(CrearReserva crearReserva, CancelarReserva cancelarReserva, BuscarReservas buscarReservas,
			ObtenerReserva obtenerReserva, BuscadorSalas buscadorSalas) {
		this.crearReserva = crearReserva;
		this.cancelarReserva = cancelarReserva;
		this.buscarReservas = buscarReservas;
		this.obtenerReserva = obtenerReserva;
		this.buscadorSalas = buscadorSalas;
	}

	@Operation(summary = "Crea una reserva", description = "Devuelve 409 si la sala ya está ocupada en ese horario. Requiere token JWT")
	@SecurityRequirement(name = "bearer-jwt")
	@ApiResponse(responseCode = "409", description = "La sala no está disponible en el horario solicitado")
	@PostMapping
	public ResponseEntity<ReservaRespuesta> crear(@Valid @RequestBody CrearReservaSolicitud solicitud) {
		Reserva reserva = crearReserva.crear(solicitud.salaId(), solicitud.nombreResponsable(),
				solicitud.correoResponsable(), solicitud.inicio(), solicitud.fin());
		String nombreSala = buscadorSalas.buscar(reserva.salaId()).nombre();
		URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(reserva.id())
			.toUri();
		return ResponseEntity.created(ubicacion).body(ReservaRespuesta.de(reserva, nombreSala));
	}

	@Operation(summary = "Lista reservas con filtros",
			description = "El filtro por fecha cubre el día completo en la zona horaria configurada")
	@GetMapping
	public RespuestaPagina<ReservaRespuesta> listar(@RequestParam(required = false) UUID salaId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
			@RequestParam(required = false) EstadoReserva estado,
			@RequestParam(defaultValue = "INICIO") OrdenReserva orden,
			@RequestParam(defaultValue = "0") @Min(value = 0, message = "no puede ser negativo") int pagina,
			@RequestParam(defaultValue = "20") @Min(value = 1, message = "debe ser al menos 1") @Max(value = 100, message = "no puede superar 100") int tamano) {
		Pagina<Reserva> resultado = buscarReservas.buscar(salaId, fecha, estado, orden, pagina, tamano);
		Map<UUID, String> nombres = buscadorSalas
			.nombresDe(resultado.contenido().stream().map(Reserva::salaId).collect(Collectors.toSet()));
		return RespuestaPagina.de(resultado,
				reserva -> ReservaRespuesta.de(reserva, nombres.get(reserva.salaId().valor())));
	}

	@Operation(summary = "Obtiene una reserva por su identificador")
	@GetMapping("/{id}")
	public ReservaRespuesta obtener(@PathVariable UUID id) {
		Reserva reserva = obtenerReserva.obtener(id);
		return ReservaRespuesta.de(reserva, buscadorSalas.buscar(reserva.salaId()).nombre());
	}

	@Operation(summary = "Cancela una reserva", description = "Requiere token JWT")
	@SecurityRequirement(name = "bearer-jwt")
	@ApiResponse(responseCode = "409", description = "La reserva ya estaba cancelada")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> cancelar(@PathVariable UUID id) {
		cancelarReserva.cancelar(id);
		return ResponseEntity.noContent().build();
	}

}
