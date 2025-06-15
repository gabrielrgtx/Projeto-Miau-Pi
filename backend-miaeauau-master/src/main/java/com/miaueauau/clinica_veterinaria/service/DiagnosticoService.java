package com.miaueauau.clinica_veterinaria.service;

import com.miaueauau.clinica_veterinaria.model.Diagnostico;
import com.miaueauau.clinica_veterinaria.repository.DiagnosticoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiagnosticoService {

    @Autowired
    private DiagnosticoRepository diagnosticoRepository;

    public List<Diagnostico> listarTodosDiagnosticos() {
        return diagnosticoRepository.findAll();
    }

    public Optional<Diagnostico> buscarDiagnosticoPorId(Long id) {
        return diagnosticoRepository.findById(id);
    }

    public Diagnostico salvarDiagnostico(Diagnostico diagnostico) {
        return diagnosticoRepository.save(diagnostico);
    }

    public void deletarDiagnostico(Long id) {
        diagnosticoRepository.deleteById(id);
    }

    // Outros métodos de lógica de negócios, se necessário
}