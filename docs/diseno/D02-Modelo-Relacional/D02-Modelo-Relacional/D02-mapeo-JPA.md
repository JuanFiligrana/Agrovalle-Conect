# D02 Mapeo previsto a entidades JPA

Este documento define la correspondencia de diseño. No modifica todavía el backend del repositorio. Las entidades usan Jakarta Persistence y tablas en el esquema agrovalle.

## Identidad y tipos

- BIGINT → Long; NUMERIC → BigDecimal; DATE → LocalDate; TIMESTAMPTZ → OffsetDateTime; BOOLEAN → Boolean o boolean.
- Las PK IDENTITY se mapean con @Id y @GeneratedValue(strategy = GenerationType.IDENTITY).
- Los nombres físicos se indican con @Table(name="...", schema="agrovalle") y @Column(name="...").
- Las relaciones FK obligatorias usan optional=false y @JoinColumn(nullable=false). Las opcionales de notificación admiten null.
- Las colecciones inversas se expresan con @OneToMany(mappedBy="..."). No se añaden copias escalares de los mismos ID si ya los representa una asociación.

## Herencia de usuarios

Usuario será una entidad abstracta con @Inheritance(strategy = InheritanceType.JOINED). Agricultor y Comprador extienden Usuario y usan @PrimaryKeyJoinColumn(name="id_usuario"). La PK se declara en Usuario, no se genera otro ID en los hijos. El rol se deduce del subtipo y no se añade una columna duplicada solo para calcularlo.

```java
@Entity
@Table(name = "usuario", schema = "agrovalle")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;
    // nombre, correo, contrasenaHash, activo y fechaRegistro
}

@Entity
@Table(name = "agricultor", schema = "agrovalle")
@PrimaryKeyJoinColumn(name = "id_usuario")
public class Agricultor extends Usuario {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_municipio", nullable = false)
    private Municipio municipio;
    // cedula
}
```

El ejemplo omite imports, constructores y accesores: ilustra el mapeo, no es un archivo Java completo. La especialización total y disjunta se valida en la operación de registro.

## Correspondencia por tabla

| Tabla | Entidad | Asociaciones propietarias principales |
|---|---|---|
| usuario | Usuario | Raíz de la herencia JOINED. |
| agricultor | Agricultor | Municipio: ManyToOne; PK heredada. |
| comprador | Comprador | PK heredada. |
| municipio | Municipio | Lado inverso de agricultores y fincas. |
| finca | Finca | Agricultor y Municipio: ManyToOne. |
| categoria | Categoria | Lado inverso de productos. |
| producto | Producto | Categoria: ManyToOne. |
| cosecha | Cosecha | Finca y Producto: ManyToOne. |
| carrito | Carrito | Comprador: ManyToOne. |
| reserva | Reserva | Carrito y Cosecha: ManyToOne. |
| pedido | Pedido | Carrito y Agricultor: ManyToOne. |
| detalle_pedido | DetallePedido | Pedido: ManyToOne; Reserva: OneToOne por UNIQUE. |
| envio | Envio | Pedido: OneToOne por UNIQUE. |
| evento_seguimiento | EventoSeguimiento | Pedido: ManyToOne. |
| contacto | Contacto | Comprador y Cosecha: ManyToOne. |
| notificacion | Notificacion | Destinatario: ManyToOne; Contacto: OneToOne opcional; Evento: ManyToOne opcional. |
| idempotencia_pedido | IdempotenciaPedido | Comprador: ManyToOne con MapsId; Pedido: OneToOne. |

## PK compuesta de idempotencia

Usar @Embeddable IdempotenciaPedidoId con Long idComprador y String clave, junto con equals y hashCode. En IdempotenciaPedido, declarar @EmbeddedId. La asociación a Comprador usa @MapsId("idComprador") y @JoinColumn(name="id_comprador"). La asociación a Pedido usa @OneToOne y una FK única id_pedido. No duplicar idComprador como un segundo atributo persistente independiente de la clave.

## Validación y cálculos

Usar @Enumerated(EnumType.STRING) para los estados que corresponden a los CHECK del DDL. Los nombres del enum deben conservar los literales del backlog, incluido CANCELADA. La unicidad del correo normalizado depende del índice de PostgreSQL; @Column(unique=true) por sí solo no implementa esa normalización.

Calcular disponibilidad, total y promedio con consultas o proyecciones; no crear columnas persistentes duplicadas. Implementar las operaciones de inventario y confirmación con @Transactional y bloqueo pesimista o actualización condicional equivalente. Ver D02-normalizacion.md para las reglas que el esquema estructural no ejecuta.

Fuentes técnicas: [Jakarta Persistence, Inheritance](https://jakarta.ee/specifications/persistence/3.1/apidocs/jakarta.persistence/jakarta/persistence/inheritance) y [Jakarta Persistence, MapsId](https://jakarta.ee/specifications/platform/11/apidocs/jakarta/persistence/mapsid). JOINED y MapsId están disponibles en las versiones de Jakarta Persistence utilizadas con Spring Boot 3.
