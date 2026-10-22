package co.edu.uniajc.agrovalle.dto;

import co.edu.uniajc.agrovalle.validation.MunicipioValle;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Datos de entrada para registrar una cuenta. */
public record RegistroAgricultorRequest(
    @NotBlank String nombre,
    @NotBlank @Email String correo,
    @NotBlank @Size(min = 6) String contrasena,
    @NotBlank String cedula,
    @JsonAlias({"ubicacion_valle", "municipio"})
    @NotBlank @MunicipioValle String ubicacionValle,
    String rol) {}
