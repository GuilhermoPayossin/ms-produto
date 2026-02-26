package com.guilhermopayossin.github.ms_produto.controller;

import com.guilhermopayossin.github.ms_produto.dto.ProdutoDTO;
import com.guilhermopayossin.github.ms_produto.enities.Produto;
import com.guilhermopayossin.github.ms_produto.services.ProdutoService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    public ResponseEntity<List<ProdutoDTO>> getAllProdutos() {
        List<ProdutoDTO> list = produtoService.findAllProdutos();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoDTO> getProdutoById(@PathVariable Long id) {
        ProdutoDTO produtoDTO = produtoService.findProdutoById(id);
        return ResponseEntity.ok(produtoDTO);
    }

    @PostMapping
    public ResponseEntity<ProdutoDTO> createProduto(@RequestBody ProdutoDTO produtoDTO) {
        produtoDTO = produtoService.saveProduto(produtoDTO);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(produtoDTO.getId())
                .toUri();

        return ResponseEntity.created(uri).body(produtoDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoDTO> updateProduto(@PathVariable Long id,
                                                    @RequestBody ProdutoDTO produtoDto) {
        produtoDto = produtoService.updateProduto(id, produtoDto);
        return ResponseEntity.ok(produtoDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduto(@PathVariable Long id){
        produtoService.deleteProdutoById(id);
        return ResponseEntity.noContent().build();
    }
}
