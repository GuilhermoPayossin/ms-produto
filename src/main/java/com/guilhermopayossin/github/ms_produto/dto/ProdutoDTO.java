package com.guilhermopayossin.github.ms_produto.dto;

import com.guilhermopayossin.github.ms_produto.enities.Produto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProdutoDTO {
    private Long id;

    @NotBlank(message = "Campo nome é obrigatório")
    @Size(min = 3, max = 100, message = "O nome deve conter entre 3 e 100 caractéres")
    private String nome;
    @NotBlank(message = "Campo descrição é obrigatório")
    @Size(min = 10, message = "a descrição deve conter no mínimo 10 caractéres")
    private String descricao;
    @NotBlank(message = "Campo valor é obrigatório")
    @Positive(message = "O campo valor não pode ser menor que 0")
    private Double valor;

    public ProdutoDTO(Produto produto) {
        id = produto.getId();
        nome = produto.getNome();
        descricao = produto.getDescricao();
        valor = produto.getValor();
    }
}
