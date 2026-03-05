package com.guilhermopayossin.github.ms_produto.controller;

import com.guilhermopayossin.github.ms_produto.dto.CategoriaDTO;
import com.guilhermopayossin.github.ms_produto.dto.ProdutoDTO;
import com.guilhermopayossin.github.ms_produto.services.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> getAllCategorias() {
        List<CategoriaDTO> categorias = categoriaService.findAllCategorias();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> getCategoriaById(@PathVariable Long id) {
        CategoriaDTO categoriaDTO = categoriaService.findCategoriaById(id);
        return ResponseEntity.ok(categoriaDTO);
    }

    @PostMapping
    public ResponseEntity<CategoriaDTO> createCategoria(@RequestBody @Valid CategoriaDTO categoriaDTO) {
        categoriaDTO = categoriaService.saveCategoria(categoriaDTO);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(categoriaDTO.getId())
                .toUri();
        return ResponseEntity.created(uri).body(categoriaDTO);
    }
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTO> updateCategoria(@PathVariable Long id,
                                                    @RequestBody @Valid CategoriaDTO categoriaDTO) {
        categoriaDTO = categoriaService.updateCategoria(id, categoriaDTO);
        return ResponseEntity.ok(categoriaDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoria(@PathVariable Long id){
        categoriaService.deleteCategoriaById(id);
        return ResponseEntity.noContent().build();
    }
}
