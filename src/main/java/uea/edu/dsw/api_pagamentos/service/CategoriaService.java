package uea.edu.dsw.api_pagamentos.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import uea.edu.dsw.api_pagamentos.model.Categoria;
import uea.edu.dsw.api_pagamentos.repository.CategoriaRepository;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    // Injeção via construtor
    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    public Optional<Categoria> buscarPorCodigo(Long codigo) {
        return categoriaRepository.findById(codigo);
    }

    public Categoria salvar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public Categoria atualizar(Long codigo, Categoria categoria) {
        // Verifica se a categoria existe
        Optional<Categoria> categoriaExistente = categoriaRepository.findById(codigo);
        if (categoriaExistente.isPresent()) {
            // Atualize os atributos necessários
            Categoria catAtualizada = categoriaExistente.get();
            catAtualizada.setNome(categoria.getNome());
            return categoriaRepository.save(catAtualizada);
        } else {
            // Lógica de tratamento para categoria não encontrada
            throw new RuntimeException("Categoria não encontrada!");
        }
    }

    public void deletar(Long codigo) {
        categoriaRepository.deleteById(codigo);
    }
}
