package uea.edu.dsw.api_pagamentos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uea.edu.dsw.api_pagamentos.model.Lancamento;

@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {
    // Adicione métodos customizados, se necessário.
}
