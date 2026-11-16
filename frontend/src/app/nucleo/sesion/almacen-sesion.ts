import { Injectable, computed, signal } from '@angular/core';

const CLAVE = 'reuniones.token';

@Injectable({ providedIn: 'root' })
export class AlmacenSesion {
  private readonly token = signal<string | null>(localStorage.getItem(CLAVE));

  readonly autenticado = computed(() => this.token() !== null);

  tokenActual(): string | null {
    return this.token();
  }

  guardar(token: string): void {
    localStorage.setItem(CLAVE, token);
    this.token.set(token);
  }

  cerrar(): void {
    localStorage.removeItem(CLAVE);
    this.token.set(null);
  }
}
