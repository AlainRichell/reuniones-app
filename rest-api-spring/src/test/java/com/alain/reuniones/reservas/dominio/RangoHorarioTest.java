package com.alain.reuniones.reservas.dominio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class RangoHorarioTest {

	private static final Instant DIEZ = Instant.parse("2026-11-15T10:00:00Z");

	private static final Instant ONCE = Instant.parse("2026-11-15T11:00:00Z");

	private static final Instant DOCE = Instant.parse("2026-11-15T12:00:00Z");

	private static final Instant DIEZ_Y_MEDIA = Instant.parse("2026-11-15T10:30:00Z");

	private static final Instant ONCE_Y_MEDIA = Instant.parse("2026-11-15T11:30:00Z");

	@Test
	void deberia_solaparse_cuando_los_rangos_se_cruzan() {
		RangoHorario primero = new RangoHorario(DIEZ, ONCE);
		RangoHorario segundo = new RangoHorario(DIEZ_Y_MEDIA, ONCE_Y_MEDIA);

		assertThat(primero.seSolapaCon(segundo)).isTrue();
		assertThat(segundo.seSolapaCon(primero)).isTrue();
	}

	@Test
	void no_deberia_solaparse_cuando_son_adyacentes() {
		RangoHorario primero = new RangoHorario(DIEZ, ONCE);
		RangoHorario segundo = new RangoHorario(ONCE, DOCE);

		assertThat(primero.seSolapaCon(segundo)).isFalse();
		assertThat(segundo.seSolapaCon(primero)).isFalse();
	}

	@Test
	void deberia_solaparse_cuando_un_rango_contiene_al_otro() {
		RangoHorario amplio = new RangoHorario(DIEZ, DOCE);
		RangoHorario corto = new RangoHorario(DIEZ_Y_MEDIA, ONCE);

		assertThat(amplio.seSolapaCon(corto)).isTrue();
		assertThat(corto.seSolapaCon(amplio)).isTrue();
	}

	@Test
	void deberia_solaparse_cuando_los_rangos_son_identicos() {
		RangoHorario primero = new RangoHorario(DIEZ, ONCE);

		assertThat(primero.seSolapaCon(new RangoHorario(DIEZ, ONCE))).isTrue();
	}

	@Test
	void deberia_rechazar_un_rango_sin_duracion() {
		assertThatThrownBy(() -> new RangoHorario(ONCE, ONCE)).isInstanceOf(RangoHorarioInvalidoException.class)
			.hasMessageContaining("posterior");
	}

	@Test
	void deberia_rechazar_un_rango_invertido() {
		assertThatThrownBy(() -> new RangoHorario(ONCE, DIEZ)).isInstanceOf(RangoHorarioInvalidoException.class);
	}

}
