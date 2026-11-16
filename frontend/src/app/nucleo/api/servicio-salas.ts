import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { CrearSalaSolicitud, Pagina, Sala } from '../modelos';

const BASE = '/api/v1/salas';

@Injectable({ providedIn: 'root' })
export class ServicioSalas {
  private readonly http = inject(HttpClient);

  listar(pagina: number, tamano: number): Observable<Pagina<Sala>> {
    return this.http.get<Pagina<Sala>>(BASE, { params: { pagina, tamano } });
  }

  crear(solicitud: CrearSalaSolicitud): Observable<Sala> {
    return this.http.post<Sala>(BASE, solicitud);
  }
}
