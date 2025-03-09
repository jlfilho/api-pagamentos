package uea.edu.dsw.api_pagamentos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uea.edu.dsw.api_pagamentos.model.Categoria;
import uea.edu.dsw.api_pagamentos.model.Pessoa;
import uea.edu.dsw.api_pagamentos.model.TipoLancamento;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LancamentoDTO {
    private Long codigo;
    
    @NotBlank(message = "A descrição é obrigatória")
    @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
    private String descricao;

    @NotNull(message = "O valor é obrigatório")
    @Positive(message = "O valor deve ser positivo")
    private BigDecimal valor;

    @NotNull(message = "A data de vencimento é obrigatória")
    private LocalDate dataVencimento;

    private LocalDate dataPagamento;

    @Size(max = 500, message = "A observação deve ter no máximo 500 caracteres")
    private String observacao;

    @NotNull(message = "O tipo de lançamento é obrigatório")
    private TipoLancamento tipo;

    @NotNull(message = "A categoria é obrigatória")
    private Categoria categoria;

    @NotNull(message = "A pessoa é obrigatória")
    private Pessoa pessoa;
}
