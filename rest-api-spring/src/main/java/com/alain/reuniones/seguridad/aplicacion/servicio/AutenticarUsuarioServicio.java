package com.alain.reuniones.seguridad.aplicacion.servicio;

import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alain.reuniones.seguridad.aplicacion.AutenticarUsuario;
import com.alain.reuniones.seguridad.aplicacion.CredencialesInvalidasException;

@Service
public class AutenticarUsuarioServicio implements AutenticarUsuario {

	private final AuthenticationManager gestor;

	public AutenticarUsuarioServicio(AuthenticationManager gestor) {
		this.gestor = gestor;
	}

	@Override
	@Transactional(readOnly = true)
	public UsuarioAutenticado autenticar(String usuario, String clave) {
		try {
			Authentication autenticacion = gestor
				.authenticate(new UsernamePasswordAuthenticationToken(usuario, clave));
			List<String> roles = autenticacion.getAuthorities()
				.stream()
				.map(GrantedAuthority::getAuthority)
				.filter(rol -> rol.startsWith("ROLE_"))
				.map(rol -> rol.substring("ROLE_".length()))
				.toList();
			return new UsuarioAutenticado(autenticacion.getName(), roles);
		}
		catch (AuthenticationException excepcion) {
			throw new CredencialesInvalidasException();
		}
	}

}
