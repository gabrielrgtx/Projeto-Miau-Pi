package com.miaueauau.clinica_veterinaria.service;

import com.miaueauau.clinica_veterinaria.model.Prescricao;
import com.miaueauau.clinica_veterinaria.repository.PrescricaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PrescricaoService {

    @Autowired
    private PrescricaoRepository prescricaoRepository;

    public List<Prescricao> listarTodasPrescricoes() {
        return prescricaoRepository.findAll();
    }

    public Optional<Prescricao> buscarPrescricaoPorId(Long id) {
        return prescricaoRepository.findById(id);
    }

    public Prescricao salvarPrescricao(Prescricao prescricao) {
        return prescricaoRepository.save(prescricao);
    }

    public void deletarPrescricao(Long id) {
        prescricaoRepository.deleteById(id);
    }

    // Outros métodos de lógica de negócios, se necessário
}