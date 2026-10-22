package co.edu.uniajc.agrovalle.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Comprueba que un valor pertenezca al catálogo municipal del Valle del Cauca. */
@Documented
@Constraint(validatedBy = MunicipioValleValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface MunicipioValle {

  /** Mensaje utilizado en las respuestas de validación. */
  String message() default "debe pertenecer al catálogo de municipios del Valle del Cauca";

  /** Grupos de validación compatibles con Jakarta Validation. */
  Class<?>[] groups() default {};

  /** Información adicional compatible con Jakarta Validation. */
  Class<? extends Payload>[] payload() default {};
}
