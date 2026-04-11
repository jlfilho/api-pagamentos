package uea.edu.dsw.api_pagamentos.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import uea.edu.dsw.api_pagamentos.model.Lancamento;

@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {
       interface CategoriaTotalProjection {
              String getCategoria();

              BigDecimal getTotal();
       }

       @Query("SELECT l FROM Lancamento l " +
                     "WHERE (:descricao IS NULL OR lower(l.descricao) LIKE lower(concat('%', :descricao, '%'))) " +
                     "AND (:dataVencimentoDe IS NULL OR l.dataVencimento >= :dataVencimentoDe) " +
                     "AND (:dataVencimentoAte IS NULL OR l.dataVencimento <= :dataVencimentoAte)")
       Page<Lancamento> filtrar(@Param("descricao") String descricao,
                     @Param("dataVencimentoDe") LocalDate dataVencimentoDe,
                     @Param("dataVencimentoAte") LocalDate dataVencimentoAte,
                     Pageable pageable);

       @Query("""
                     SELECT l.categoria.nome AS categoria, SUM(l.valor) AS total
                     FROM Lancamento l
                     WHERE l.tipo = uea.edu.dsw.api_pagamentos.model.TipoLancamento.RECEITA
                       AND l.dataVencimento BETWEEN :dataInicio AND :dataFim
                     GROUP BY l.categoria.nome
                     ORDER BY SUM(l.valor) DESC
                     """)
       List<CategoriaTotalProjection> somarReceitasPorCategoria(
                     @Param("dataInicio") LocalDate dataInicio,
                     @Param("dataFim") LocalDate dataFim);

       @Query("""
                     SELECT COALESCE(SUM(l.valor), 0)
                     FROM Lancamento l
                     WHERE l.tipo = uea.edu.dsw.api_pagamentos.model.TipoLancamento.RECEITA
                       AND l.dataVencimento BETWEEN :dataInicio AND :dataFim
                     """)
       BigDecimal somarTotalReceitas(
                     @Param("dataInicio") LocalDate dataInicio,
                     @Param("dataFim") LocalDate dataFim);

       @Query("""
                     SELECT l.categoria.nome AS categoria, SUM(l.valor) AS total
                     FROM Lancamento l
                     WHERE l.tipo = uea.edu.dsw.api_pagamentos.model.TipoLancamento.DESPESA
                       AND l.dataVencimento BETWEEN :dataInicio AND :dataFim
                     GROUP BY l.categoria.nome
                     ORDER BY SUM(l.valor) DESC
                     """)
       List<CategoriaTotalProjection> somarDespesasPorCategoria(
                     @Param("dataInicio") LocalDate dataInicio,
                     @Param("dataFim") LocalDate dataFim);

       @Query("""
                     SELECT COALESCE(SUM(l.valor), 0)
                     FROM Lancamento l
                     WHERE l.tipo = uea.edu.dsw.api_pagamentos.model.TipoLancamento.DESPESA
                       AND l.dataVencimento BETWEEN :dataInicio AND :dataFim
                     """)
       BigDecimal somarTotalDespesas(
                     @Param("dataInicio") LocalDate dataInicio,
                     @Param("dataFim") LocalDate dataFim);
}
