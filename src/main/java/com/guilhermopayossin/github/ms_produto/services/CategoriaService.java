package com.guilhermopayossin.github.ms_produto.services;

import com.guilhermopayossin.github.ms_produto.dto.CategoriaDTO;
import com.guilhermopayossin.github.ms_produto.enities.Categoria;
import com.guilhermopayossin.github.ms_produto.exeptions.ResourceNotFoundException;
import com.guilhermopayossin.github.ms_produto.repository.CategoriaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<CategoriaDTO> findAllCategorias() {
        return categoriaRepository.findAll()
                .stream().map(CategoriaDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public CategoriaDTO findCategoriaById(Long id) {
        Categoria categoria = categoriaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado ID: " + id)
        );
        return new CategoriaDTO(categoria);
    }

    @Transactional
    public CategoriaDTO saveCategoria(CategoriaDTO inputDTO) {
        Categoria categoria = new Categoria();
        copyDtoToCategoria(inputDTO, categoria);
        categoria = categoriaRepository.save(categoria);
        return new CategoriaDTO(categoria);
    }

    private void copyDtoToCategoria(CategoriaDTO inputDTO, Categoria categoria) {
        categoria.setNome(inputDTO.getNome());
    }

    @Transactional
    public CategoriaDTO updateCategoria(Long id, CategoriaDTO inputDTO) {
        try {
            Categoria categoria = categoriaRepository.getReferenceById(id);
            copyDtoToCategoria(inputDTO, categoria);
            categoria = categoriaRepository.save(categoria);
            return new CategoriaDTO(categoria);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Recurso não encontrado ID: " + id);
        }
    }

    @Transactional
    public void deleteCategoriaById(Long id) {
        if(!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado: ID - "+ id);
        }
        categoriaRepository.deleteById(id);
    }

}
