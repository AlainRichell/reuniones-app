package com.alain.reuniones.compartido.infraestructura.web;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.alain.reuniones.compartido.dominio.ExcepcionDominio;

import jakarta.validation.ConstraintViolationException;

/** Traduce las excepciones a Problem Details con un código estable para el cliente. */
@RestControllerAdvice
public class ConsejoProblemas {

	private static final Logger registro = LoggerFactory.getLogger(ConsejoProblemas.class);

	@ExceptionHandler(ExcepcionDominio.class)
	public ProblemDetail errorDeNegocio(ExcepcionDominio excepcion) {
		ProblemDetail problema = problema(estadoDe(excepcion.codigo()), tituloDe(excepcion.codigo()),
				excepcion.getMessage());
		problema.setProperty("codigo", excepcion.codigo());
		return problema;
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ProblemDetail errorDeValidacion(MethodArgumentNotValidException excepcion) {
		List<Map<String, String>> errores = excepcion.getBindingResult().getFieldErrors().stream()
			.map(this::describir)
			.toList();
		return conErrores("La petición no pasó la validación", "Revisa los campos señalados en errores", errores);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ProblemDetail errorDeParametros(ConstraintViolationException excepcion) {
		List<Map<String, String>> errores = excepcion.getConstraintViolations().stream()
			.map(violacion -> Map.of("campo", ultimoSegmento(violacion.getPropertyPath().toString()), "codigo",
					"INVALIDO", "mensaje", mensajeDe(violacion.getMessage())))
			.toList();
		return conErrores("La petición no pasó la validación", "Revisa los parámetros señalados en errores", errores);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ProblemDetail errorDeTipo(MethodArgumentTypeMismatchException excepcion) {
		ProblemDetail problema = problema(HttpStatus.BAD_REQUEST, "La petición está mal formada",
				"El valor de " + excepcion.getName() + " no tiene el formato esperado");
		problema.setProperty("codigo", "DATOS_INVALIDOS");
		return problema;
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ProblemDetail errorDeLectura(HttpMessageNotReadableException excepcion) {
		ProblemDetail problema = problema(HttpStatus.BAD_REQUEST, "La petición está mal formada",
				"El cuerpo del mensaje no es un JSON válido");
		problema.setProperty("codigo", "PETICION_MALFORMADA");
		return problema;
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ProblemDetail errorDeMetodo(HttpRequestMethodNotSupportedException excepcion) {
		ProblemDetail problema = problema(HttpStatus.METHOD_NOT_ALLOWED, "Método no permitido",
				"El verbo " + excepcion.getMethod() + " no está soportado en esta ruta");
		problema.setProperty("codigo", "METODO_NO_PERMITIDO");
		return problema;
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ProblemDetail recursoNoEncontrado(NoResourceFoundException excepcion) {
		ProblemDetail problema = problema(HttpStatus.NOT_FOUND, "Recurso no encontrado",
				"La ruta solicitada no existe");
		problema.setProperty("codigo", "RECURSO_NO_ENCONTRADO");
		return problema;
	}

	@ExceptionHandler(Exception.class)
	public ProblemDetail errorInesperado(Exception excepcion) {
		registro.error("Error no controlado", excepcion);
		ProblemDetail problema = problema(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno",
				"No se pudo completar la operación");
		problema.setProperty("codigo", "ERROR_INTERNO");
		return problema;
	}

	private ProblemDetail conErrores(String titulo, String detalle, List<Map<String, String>> errores) {
		ProblemDetail problema = problema(HttpStatus.BAD_REQUEST, titulo, detalle);
		problema.setProperty("codigo", "DATOS_INVALIDOS");
		problema.setProperty("errores", errores);
		return problema;
	}

	private ProblemDetail problema(HttpStatus estado, String titulo, String detalle) {
		ProblemDetail problema = ProblemDetail.forStatusAndDetail(estado, detalle);
		problema.setType(URI.create("about:blank"));
		problema.setTitle(titulo);
		problema.setProperty("marcaDeTiempo", Instant.now());
		return problema;
	}

	private Map<String, String> describir(FieldError error) {
		return Map.of("campo", error.getField(), "codigo",
				error.getCode() == null ? "INVALIDO" : error.getCode().toUpperCase(Locale.ROOT), "mensaje",
				mensajeDe(error.getDefaultMessage()));
	}

	private String mensajeDe(String mensaje) {
		return mensaje == null ? "valor inválido" : mensaje;
	}

	private String ultimoSegmento(String ruta) {
		int punto = ruta.lastIndexOf('.');
		return punto < 0 ? ruta : ruta.substring(punto + 1);
	}

	private HttpStatus estadoDe(String codigo) {
		return switch (codigo) {
			case "SALA_NO_ENCONTRADA", "RESERVA_NO_ENCONTRADA", "RECURSO_NO_ENCONTRADO" -> HttpStatus.NOT_FOUND;
			case "NOMBRE_SALA_OCUPADO", "SALA_NO_DISPONIBLE", "RESERVA_YA_CANCELADA" -> HttpStatus.CONFLICT;
			default -> HttpStatus.BAD_REQUEST;
		};
	}

	private String tituloDe(String codigo) {
		return switch (codigo) {
			case "SALA_NO_ENCONTRADA" -> "Sala no encontrada";
			case "NOMBRE_SALA_OCUPADO" -> "El nombre de la sala ya está en uso";
			case "DATOS_INVALIDOS" -> "La petición no pasó la validación";
			default -> "No se pudo completar la operación";
		};
	}

}
