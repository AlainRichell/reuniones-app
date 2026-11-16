package com.alain.reuniones.seguridad.aplicacion.servicio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.alain.reuniones.seguridad.aplicacion.AutenticarUsuario;
import com.alain.reuniones.seguridad.aplicacion.CredencialesInvalidasException;

@ExtendWith(MockitoExtension.class)
class AutenticarUsuarioServicioTest {

	@Mock
	private AuthenticationManager gestor;

	@InjectMocks
	private AutenticarUsuarioServicio servicio;

	@Test
	void deberia_devolver_el_usuario_y_sus_roles_cuando_las_credenciales_son_validas() {
		Authentication autenticacion = new UsernamePasswordAuthenticationToken("admin", null,
				List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("FACTOR_PASSWORD")));
		given(gestor.authenticate(any(Authentication.class))).willReturn(autenticacion);

		AutenticarUsuario.UsuarioAutenticado resultado = servicio.autenticar("admin", "admin123");

		assertThat(resultado.usuario()).isEqualTo("admin");
		assertThat(resultado.roles()).containsExactly("ADMIN");
	}

	@Test
	void deberia_lanzar_no_autenticado_cuando_las_credenciales_fallan() {
		given(gestor.authenticate(any(Authentication.class)))
			.willThrow(new BadCredentialsException("credenciales incorrectas"));

		assertThatThrownBy(() -> servicio.autenticar("admin", "incorrecta"))
			.isInstanceOf(CredencialesInvalidasException.class)
			.hasMessageContaining("no son correctos");
	}

}
