package com.miaueauau.clinica_veterinaria.service;

import com.miaueauau.clinica_veterinaria.model.ConsultaProntuario;
import com.miaueauau.clinica_veterinaria.repository.ConsultaProntuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConsultaProntuarioService {

    @Autowired
    private ConsultaProntuarioRepository consultaProntuarioRepository;

    public List<ConsultaProntuario> listarTodasConsultaProntuarios() {
        return consultaProntuarioRepository.findAll();
    }

    public Optional<ConsultaProntuario> buscarConsultaProntuarioPorId(Long id) {
        return consultaProntuarioRepository.findById(id);
    }

    public ConsultaProntuario salvarConsultaProntuario(ConsultaProntuario consultaProntuario) {
        return consultaProntuarioRepository.save(consultaProntuario);
    }

    public void deletarConsultaProntuario(Long id) {
        consultaProntuarioRepository.deleteById(id);
    }

    // Outros métodos de lógica de negócios, se necessário
}