package co.edu.uniajc.agrovalle.validation;

import java.text.Normalizer;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/** Catálogo y normalización de municipios del Valle del Cauca. */
public final class MunicipiosValle {

  private static final Map<String, String> MUNICIPIOS = crearCatalogo();

  private MunicipiosValle() {}

  /** Indica si el texto representa un municipio permitido. */
  public static boolean contiene(String value) {
    return value != null && MUNICIPIOS.containsKey(clave(value));
  }

  /** Devuelve el nombre canónico del municipio cuando existe en el catálogo. */
  public static Optional<String> canonicalizar(String value) {
    return value == null ? Optional.empty() : Optional.ofNullable(MUNICIPIOS.get(clave(value)));
  }

  private static Map<String, String> crearCatalogo() {
    Map<String, String> municipios = new LinkedHashMap<>();
    registrar(municipios, "Alcalá", "Andalucía", "Ansermanuevo", "Argelia", "Bolívar",
        "Buenaventura", "Bugalagrande", "Caicedonia", "Cali", "Calima", "Candelaria",
        "Cartago", "Dagua", "El Águila", "El Cairo", "El Cerrito", "El Dovio", "Florida",
        "Ginebra", "Guacarí", "Guadalajara de Buga", "Jamundí", "La Cumbre", "La Unión",
        "La Victoria", "Obando", "Palmira", "Pradera", "Restrepo", "Riofrío", "Roldanillo",
        "San Pedro", "Sevilla", "Toro", "Trujillo", "Tuluá", "Ulloa", "Versalles", "Vijes",
        "Yotoco", "Yumbo", "Zarzal");
    alias(municipios, "Santiago de Cali", "Cali");
    alias(municipios, "Buga", "Guadalajara de Buga");
    alias(municipios, "Calima El Darién", "Calima");
    alias(municipios, "El Darién", "Calima");
    return Collections.unmodifiableMap(municipios);
  }

  private static void registrar(Map<String, String> municipios, String... nombres) {
    for (String nombre : nombres) {
      municipios.put(clave(nombre), nombre);
    }
  }

  private static void alias(Map<String, String> municipios, String alias, String canonical) {
    municipios.put(clave(alias), canonical);
  }

  private static String clave(String value) {
    String withoutAccents = Normalizer.normalize(value.trim(), Normalizer.Form.NFD)
        .replaceAll("\\p{M}+", "");
    return withoutAccents.toUpperCase(Locale.ROOT).replaceAll("\\s+", " ");
  }
}
