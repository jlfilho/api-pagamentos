package uea.edu.dsw.api_pagamentos.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import uea.edu.dsw.api_pagamentos.model.Pessoa;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
    // Se necessário, adicione métodos de consulta customizados aqui.
    @Query("SELECT p FROM Pessoa p " +
           "WHERE (:nome IS NULL OR TRIM(:nome) = '' " +
           "   OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%')))")
    Page<Pessoa> findAllByNomeOptional(
        @Param("nome") String nome,
        Pageable pageable
    );
}
