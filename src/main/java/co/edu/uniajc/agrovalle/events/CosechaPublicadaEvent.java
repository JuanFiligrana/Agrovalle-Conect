package co.edu.uniajc.agrovalle.events;

import co.edu.uniajc.agrovalle.models.Cosecha;

/** Evento de dominio emitido después de publicar una cosecha. */
public record CosechaPublicadaEvent(Cosecha cosecha) {}
