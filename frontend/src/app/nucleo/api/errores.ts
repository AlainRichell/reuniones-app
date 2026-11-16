import { HttpErrorResponse } from '@angular/common/http';
import { Problema } from '../modelos';

const MENSAJES: Record<string, string> = {
  SALA_NO_DISPONIBLE: 'Esa sala ya está ocupada en el horario elegido.',
  NOMBRE_SALA_OCUPADO: 'Ya existe una sala con ese nombre.',
  NO_AUTENTICADO: 'Tu sesión no es válida. Vuelve a iniciar sesión.',
  RESERVA_YA_CANCELADA: 'Esa reserva ya estaba cancelada.',
  DATOS_INVALIDOS: 'Revisa los datos del formulario.',
  SALA_NO_ENCONTRADA: 'La sala ya no existe.',
  RESERVA_NO_ENCONTRADA: 'La reserva ya no existe.',
};

export function mensajeDeError(error: HttpErrorResponse): string {
  const problema = error.error as Problema | null;
  if (problema?.codigo && MENSAJES[problema.codigo]) {
    return MENSAJES[problema.codigo];
  }
  return problema?.detail ?? 'No se pudo completar la operación.';
}
