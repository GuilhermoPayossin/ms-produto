package com.guilhermopayossin.github.ms_produto.controller;

import com.guilhermopayossin.github.ms_produto.dto.ProdutoDTO;
import com.guilhermopayossin.github.ms_produto.services.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
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

    @Profile("test")
    @GetMapping("/--demo/500")
    public String force500() {
        throw new RuntimeException("Erro 500 forçado para demonstração");
    }

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
    public ResponseEntity<ProdutoDTO> createProduto(@RequestBody @Valid ProdutoDTO produtoDTO) {
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
                                                    @RequestBody @Valid ProdutoDTO produtoDto) {
        produtoDto = produtoService.updateProduto(id, produtoDto);
        return ResponseEntity.ok(produtoDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduto(@PathVariable Long id){
        produtoService.deleteProdutoById(id);
        return ResponseEntity.noContent().build();
    }
}
