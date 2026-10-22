package co.edu.uniajc.agrovalle.validation;

import java.text.Normalizer;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/** Catálogo versionable de categorías permitidas para las cosechas. */
public final class CategoriasCosecha {

  private static final Map<String, String> CATEGORIAS = crearCatalogo();

  private CategoriasCosecha() {}

  /** Devuelve el nombre canónico de una categoría registrada. */
  public static Optional<String> canonicalizar(String value) {
    return value == null ? Optional.empty() : Optional.ofNullable(CATEGORIAS.get(clave(value)));
  }

  private static Map<String, String> crearCatalogo() {
    Map<String, String> categorias = new LinkedHashMap<>();
    registrar(categorias, "Frutas", "Hortalizas", "Tubérculos", "Granos", "Cereales",
        "Legumbres", "Hierbas y aromáticas", "Otros");
    alias(categorias, "Tuberculos", "Tubérculos");
    alias(categorias, "Hierbas", "Hierbas y aromáticas");
    return Collections.unmodifiableMap(categorias);
  }

  private static void registrar(Map<String, String> categorias, String... nombres) {
    for (String nombre : nombres) {
      categorias.put(clave(nombre), nombre);
    }
  }

  private static void alias(Map<String, String> categorias, String alias, String canonical) {
    categorias.put(clave(alias), canonical);
  }

  private static String clave(String value) {
    String withoutAccents = Normalizer.normalize(value.trim(), Normalizer.Form.NFD)
        .replaceAll("\\p{M}+", "");
    return withoutAccents.toUpperCase(Locale.ROOT).replaceAll("\\s+", " ");
  }
}
