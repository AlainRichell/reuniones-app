package com.alain.reuniones.seguridad.infraestructura;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

@Configuration
public class ConfiguracionJwt {

	@Bean
	JwtDecoder decodificador(@Value("${aplicacion.jwt.secreto}") String secreto) {
		return NimbusJwtDecoder.withSecretKey(clave(secreto)).macAlgorithm(MacAlgorithm.HS256).build();
	}

	@Bean
	JwtEncoder codificador(@Value("${aplicacion.jwt.secreto}") String secreto) {
		return new NimbusJwtEncoder(new ImmutableSecret<>(clave(secreto)));
	}

	private static SecretKey clave(String secreto) {
		if (secreto == null || secreto.length() < 32) {
			throw new IllegalStateException("JWT_SECRETO debe tener al menos 32 caracteres");
		}
		return new SecretKeySpec(secreto.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
	}

}
