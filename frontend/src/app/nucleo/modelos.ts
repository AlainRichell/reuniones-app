export interface Pagina<T> {
  contenido: T[];
  pagina: number;
  tamano: number;
  totalElementos: number;
  totalPaginas: number;
}

export interface Sala {
  id: string;
  nombre: string;
  capacidad: number;
  ubicacion: string | null;
  creadaEn: string;
}

export interface CrearSalaSolicitud {
  nombre: string;
  capacidad: number;
  ubicacion: string | null;
}

export type EstadoReserva = 'ACTIVA' | 'CANCELADA';

export interface Reserva {
  id: string;
  sala: { id: string; nombre: string };
  nombreResponsable: string;
  correoResponsable: string;
  inicio: string;
  fin: string;
  estado: EstadoReserva;
  canceladaEn: string | null;
  creadaEn: string;
}

export interface CrearReservaSolicitud {
  salaId: string;
  nombreResponsable: string;
  correoResponsable: string;
  inicio: string;
  fin: string;
}

export interface Problema {
  title?: string;
  detail?: string;
  status?: number;
  codigo?: string;
  errores?: { campo: string; codigo: string; mensaje: string }[];
}

export interface SesionRespuesta {
  tokenAcceso: string;
  tipoToken: string;
  expiraEn: number;
}
