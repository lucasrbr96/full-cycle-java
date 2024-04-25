package com.fullcycle.admin.catalogo.infrastructure.genre.persistence;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<GenreJpaEntity, String> {
}
