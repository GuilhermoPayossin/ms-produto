package com.guilhermopayossin.github.ms_produto.services;

import com.guilhermopayossin.github.ms_produto.dto.ProdutoDTO;
import com.guilhermopayossin.github.ms_produto.enities.Produto;
import com.guilhermopayossin.github.ms_produto.repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Transactional(readOnly = true)
    public List<ProdutoDTO> findAllProdutos() {
        List<Produto> produtos = produtoRepository.findAll();
        return produtos.stream().map(ProdutoDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public ProdutoDTO findProdutoById(Long id) {
        Produto produto = produtoRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Recurso não encontrado: ID - " + id)
        );
        return new ProdutoDTO(produto);
    }

    @Transactional
    public ProdutoDTO saveProduto(ProdutoDTO produtoDTO) {
        Produto produto = new Produto();
        copyDtoToProduto(produtoDTO, produto);
        produto = produtoRepository.save(produto);
        return new ProdutoDTO(produto);
    }

    @Transactional
    public ProdutoDTO updateProduto(Long id, ProdutoDTO produtoDTO) {
        try {
            Produto produto = produtoRepository.getReferenceById(id);
            copyDtoToProduto(produtoDTO, produto);
            produto = produtoRepository.save(produto);
            return new ProdutoDTO(produto);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Recurso não encontrado: ID - " + id);
        }
    }

    @Transactional
    public void deleteProdutoById(Long id) {
        if(!produtoRepository.existsById(id)) {
            throw new EntityNotFoundException("Recurso não encontrado: ID - "+ id);
        }
        produtoRepository.deleteById(id);
    }

    private void copyDtoToProduto(ProdutoDTO produtoDTO, Produto produto) {
        produto.setNome(produtoDTO.getNome());
        produto.setDescricao(produtoDTO.getDescricao());
        produto.setValor(produtoDTO.getValor());
    }
}
