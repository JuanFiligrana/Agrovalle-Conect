package co.edu.uniajc.agrovalle.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/** Valida nombres y alias usuales de los 42 municipios del Valle del Cauca. */
public class MunicipioValleValidator implements ConstraintValidator<MunicipioValle, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return value == null || value.isBlank() || MunicipiosValle.contiene(value);
  }
}
