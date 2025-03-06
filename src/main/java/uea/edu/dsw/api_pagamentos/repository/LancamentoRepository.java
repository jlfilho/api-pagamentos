package uea.edu.dsw.api_pagamentos.repository;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import uea.edu.dsw.api_pagamentos.dto.ResumoLancamentoDTO;
import uea.edu.dsw.api_pagamentos.model.Lancamento;

@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {
       @Query("SELECT l FROM Lancamento l " +
                     "WHERE (:descricao IS NULL OR lower(l.descricao) LIKE lower(concat('%', :descricao, '%'))) " +
                     "AND (:dataVencimentoDe IS NULL OR l.dataVencimento >= :dataVencimentoDe) " +
                     "AND (:dataVencimentoAte IS NULL OR l.dataVencimento <= :dataVencimentoAte)")
       Page<Lancamento> filtrar(@Param("descricao") String descricao,
                     @Param("dataVencimentoDe") LocalDate dataVencimentoDe,
                     @Param("dataVencimentoAte") LocalDate dataVencimentoAte,
                     Pageable pageable);

       @Query("SELECT new uea.edu.dsw.api_pagamentos.dto.ResumoLancamentoDTO(" +
                     "l.codigo, " +
                     "l.descricao, " +
                     "l.dataVencimento, " +
                     "l.dataPagamento, " +
                     "l.valor, " +
                     "l.tipo, " +
                     "l.categoria.nome, " +
                     "l.pessoa.nome) " +
                     "FROM Lancamento l " +
                     "WHERE (:descricao IS NULL OR lower(l.descricao) LIKE lower(concat('%', :descricao, '%'))) " +
                     "AND (:dataVencimentoDe IS NULL OR l.dataVencimento >= :dataVencimentoDe) " +
                     "AND (:dataVencimentoAte IS NULL OR l.dataVencimento <= :dataVencimentoAte)")
       Page<ResumoLancamentoDTO> resumir(@Param("descricao") String descricao,
                     @Param("dataVencimentoDe") LocalDate dataVencimentoDe,
                     @Param("dataVencimentoAte") LocalDate dataVencimentoAte,
                     Pageable pageable);
}
