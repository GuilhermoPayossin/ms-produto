package com.guilhermopayossin.github.ms_produto.controller;

import com.guilhermopayossin.github.ms_produto.dto.ProdutoInputDTO;
import com.guilhermopayossin.github.ms_produto.dto.ProdutoResponseDTO;
import com.guilhermopayossin.github.ms_produto.enities.Produto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> getProduto() {
        List<ProdutoResponseDTO> dto = ProdutoResponseDTO.createMock();
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> createProduto(@RequestBody ProdutoInputDTO inputDTO) {
        ProdutoResponseDTO dto = new ProdutoResponseDTO(1L,
                inputDTO.getNome(), inputDTO.getDescricao(), inputDTO.getValor());
        return ResponseEntity.created(null).body(dto);
    }
}
