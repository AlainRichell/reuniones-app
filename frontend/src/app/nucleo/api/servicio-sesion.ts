import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { SesionRespuesta } from '../modelos';

@Injectable({ providedIn: 'root' })
export class ServicioSesion {
  private readonly http = inject(HttpClient);

  iniciar(usuario: string, clave: string): Observable<SesionRespuesta> {
    return this.http.post<SesionRespuesta>('/api/v1/sesiones', { usuario, clave });
  }
}
