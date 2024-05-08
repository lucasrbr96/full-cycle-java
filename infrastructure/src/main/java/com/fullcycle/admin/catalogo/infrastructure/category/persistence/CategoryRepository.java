package com.fullcycle.admin.catalogo.infrastructure.category.persistence;

import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryJpaEntity, String> {
    Page<CategoryJpaEntity> findAll(Specification<CategoryJpaEntity> whereClause, Pageable page);

    @Query("select c.id from Category c where c.id in :ids")
    List<String> existsByIds(@Param("ids") List<String> ids);
}
