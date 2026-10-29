package co.edu.uniajc.agrovalle.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

/** Persistencia relacional de las solicitudes de contacto. */
public interface ContactoJpaRepository extends JpaRepository<ContactoEntity, Long> {}
