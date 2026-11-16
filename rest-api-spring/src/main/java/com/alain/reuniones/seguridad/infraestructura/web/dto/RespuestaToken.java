package com.alain.reuniones.seguridad.infraestructura.web.dto;

public record RespuestaToken(String tokenAcceso, String tipoToken, long expiraEn) {
}
