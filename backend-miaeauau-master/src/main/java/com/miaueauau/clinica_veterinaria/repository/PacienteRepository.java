package com.miaueauau.clinica_veterinaria.repository;

import com.miaueauau.clinica_veterinaria.model.Paciente;
import com.miaueauau.clinica_veterinaria.model.Tutor; // Importe Tutor
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    // Método para buscar pacientes associados a um Tutor específico
    List<Paciente> findByTutor(Tutor tutor);

    // Métodos de consulta específicos para Paciente no futuro, se necessário
    // Por exemplo:
    // List<Paciente> findByEspecie(String especie);
}