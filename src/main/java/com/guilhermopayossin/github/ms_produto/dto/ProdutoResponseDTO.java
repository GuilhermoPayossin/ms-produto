package com.guilhermopayossin.github.ms_produto.dto;

import com.guilhermopayossin.github.ms_produto.enities.Produto;

import java.util.List;

public class ProdutoResponseDTO {

    private Long id;
    private String nome;
    private String descricao;
    private Double valor;

    public ProdutoResponseDTO(Long id, String nome, String descricao, Double valor) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.valor = valor;
    }

    public static List<ProdutoResponseDTO> createMock() {
        return List.of(
                new ProdutoResponseDTO(1L, "Smart TV", "TV LG LED 50 Polegadas", 2250.0),
                new ProdutoResponseDTO(2L, "Mouse Logitech", "Mouse Sem Fio", 250.0),
                new ProdutoResponseDTO(3L, "Teclado Razer", "Teclado Mecânico", 349.99)
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }
}
