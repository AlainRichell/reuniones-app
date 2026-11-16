package com.alain.reuniones.seguridad.infraestructura;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import com.alain.reuniones.compartido.aplicacion.Reloj;
import com.alain.reuniones.seguridad.aplicacion.EmisorToken;

@Component
public class AdaptadorTokenJwt implements EmisorToken {

	private final JwtEncoder codificador;

	private final Reloj reloj;

	private final Duration duracion;

	public AdaptadorTokenJwt(JwtEncoder codificador, Reloj reloj,
			@Value("${aplicacion.jwt.duracion:PT1H}") Duration duracion) {
		this.codificador = codificador;
		this.reloj = reloj;
		this.duracion = duracion;
	}

	@Override
	public TokenEmitido emitir(String usuario, List<String> roles) {
		Instant ahora = reloj.ahora();
		JwtClaimsSet reclamaciones = JwtClaimsSet.builder()
			.subject(usuario)
			.issuedAt(ahora)
			.expiresAt(ahora.plus(duracion))
			.claim("roles", roles)
			.build();
		JwsHeader cabecera = JwsHeader.with(MacAlgorithm.HS256).build();
		String token = codificador.encode(JwtEncoderParameters.from(cabecera, reclamaciones)).getTokenValue();
		return new TokenEmitido(token, duracion.toSeconds());
	}

}
