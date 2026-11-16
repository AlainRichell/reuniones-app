package com.alain.reuniones.reservas.infraestructura;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import com.alain.reuniones.PruebaIntegracionBase;

class ConflictoReservaConcurrenteIT extends PruebaIntegracionBase {

	@Test
	void deberia_aceptar_solo_una_reserva_cuando_dos_clientes_compiten() throws Exception {
		UUID salaId = crearSala("Cumbre IT " + UUID.randomUUID());
		CountDownLatch listos = new CountDownLatch(2);
		CountDownLatch salida = new CountDownLatch(1);
		ExecutorService ejecutor = Executors.newFixedThreadPool(2);

		try {
			Callable<HttpStatusCode> intento = () -> {
				listos.countDown();
				salida.await(10, TimeUnit.SECONDS);
				return reservar(salaId, "2026-11-20T15:00:00Z", "2026-11-20T16:00:00Z").getStatusCode();
			};
			Future<HttpStatusCode> primero = ejecutor.submit(intento);
			Future<HttpStatusCode> segundo = ejecutor.submit(intento);

			assertThat(listos.await(10, TimeUnit.SECONDS)).isTrue();
			salida.countDown();

			List<HttpStatusCode> estados = List.of(primero.get(30, TimeUnit.SECONDS),
					segundo.get(30, TimeUnit.SECONDS));
			assertThat(estados).containsExactlyInAnyOrder(HttpStatus.CREATED, HttpStatus.CONFLICT);
		}
		finally {
			ejecutor.shutdownNow();
		}
	}

}
