package com.miaueauau.clinica_veterinaria.service;

import com.miaueauau.clinica_veterinaria.model.Paciente;
import com.miaueauau.clinica_veterinaria.model.Prontuario;
import com.miaueauau.clinica_veterinaria.repository.PacienteRepository;
import com.miaueauau.clinica_veterinaria.repository.ProntuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class ProntuarioService {

    private final ProntuarioRepository prontuarioRepository;
    private final PacienteRepository pacienteRepository;

    @Autowired
    public ProntuarioService(ProntuarioRepository prontuarioRepository, PacienteRepository pacienteRepository) {
        this.prontuarioRepository = prontuarioRepository;
        this.pacienteRepository = pacienteRepository;
    }

    @Transactional
    public Prontuario salvarProntuario(Prontuario prontuario) {
        Long pacienteId = prontuario.getPaciente().getId();
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado com o ID: " + pacienteId));

        prontuario.setPaciente(paciente);
        return prontuarioRepository.save(prontuario);
    }

    public List<Prontuario> listarTodosProntuarios() {
        return prontuarioRepository.findAll();
    }

    public Optional<Prontuario> buscarProntuarioPorId(Long id) {
        return prontuarioRepository.findById(id);
    }

    public void deletarProntuario(Long id) {
        prontuarioRepository.deleteById(id);
    }
}