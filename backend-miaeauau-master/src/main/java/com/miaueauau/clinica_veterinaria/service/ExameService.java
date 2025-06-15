package com.miaueauau.clinica_veterinaria.service;

import com.miaueauau.clinica_veterinaria.model.Exame;
import com.miaueauau.clinica_veterinaria.repository.ExameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExameService {

    @Autowired
    private ExameRepository exameRepository;

    public List<Exame> listarTodosExames() {
        return exameRepository.findAll();
    }

    public Optional<Exame> buscarExamePorId(Long id) {
        return exameRepository.findById(id);
    }

    public Exame salvarExame(Exame exame) {
        return exameRepository.save(exame);
    }

    public void deletarExame(Long id) {
        exameRepository.deleteById(id);
    }

    // Outros métodos de lógica de negócios, se necessário
}