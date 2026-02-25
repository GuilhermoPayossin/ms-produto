package com.guilhermopayossin.github.ms_produto.repository;

import com.guilhermopayossin.github.ms_produto.enities.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
