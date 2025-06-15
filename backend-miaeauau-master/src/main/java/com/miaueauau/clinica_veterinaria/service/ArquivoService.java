package com.miaueauau.clinica_veterinaria.service;

import com.miaueauau.clinica_veterinaria.model.Arquivo;
import com.miaueauau.clinica_veterinaria.repository.ArquivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArquivoService {

    @Autowired
    private ArquivoRepository arquivoRepository;

    public List<Arquivo> listarTodosArquivos() {
        return arquivoRepository.findAll();
    }

    public Optional<Arquivo> buscarArquivoPorId(Long id) {
        return arquivoRepository.findById(id);
    }

    public Arquivo salvarArquivo(Arquivo arquivo) {
        return arquivoRepository.save(arquivo);
    }

    public void deletarArquivo(Long id) {
        arquivoRepository.deleteById(id);
    }

    // Outros métodos de lógica de negócios, se necessário
}