import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { CrearReservaSolicitud, EstadoReserva, Pagina, Reserva } from '../modelos';

const BASE = '/api/v1/reservas';

export interface FiltrosReservas {
  salaId?: string;
  fecha?: string;
  estado?: EstadoReserva;
  orden?: 'INICIO' | 'CREADA_EN';
  pagina: number;
  tamano: number;
}

@Injectable({ providedIn: 'root' })
export class ServicioReservas {
  private readonly http = inject(HttpClient);

  buscar(filtros: FiltrosReservas): Observable<Pagina<Reserva>> {
    let parametros = new HttpParams()
      .set('pagina', filtros.pagina)
      .set('tamano', filtros.tamano);

    if (filtros.salaId) {
      parametros = parametros.set('salaId', filtros.salaId);
    }
    if (filtros.fecha) {
      parametros = parametros.set('fecha', filtros.fecha);
    }
    if (filtros.estado) {
      parametros = parametros.set('estado', filtros.estado);
    }
    if (filtros.orden) {
      parametros = parametros.set('orden', filtros.orden);
    }

    return this.http.get<Pagina<Reserva>>(BASE, { params: parametros });
  }

  crear(solicitud: CrearReservaSolicitud): Observable<Reserva> {
    return this.http.post<Reserva>(BASE, solicitud);
  }

  cancelar(id: string): Observable<void> {
    return this.http.delete<void>(`${BASE}/${id}`);
  }
}
