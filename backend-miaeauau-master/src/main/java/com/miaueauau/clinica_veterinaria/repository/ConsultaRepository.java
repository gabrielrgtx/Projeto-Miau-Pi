package com.miaueauau.clinica_veterinaria.repository;

import com.miaueauau.clinica_veterinaria.model.Consulta;
import com.miaueauau.clinica_veterinaria.model.Veterinario;
import com.miaueauau.clinica_veterinaria.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    // Método para buscar consultas por Veterinario e período
    List<Consulta> findByVeterinarioAndDataHoraBetween(Veterinario veterinario, LocalDateTime inicio, LocalDateTime fim);

    // Método para buscar consultas por Paciente
    List<Consulta> findByPaciente(Paciente paciente);

    // NOVO: Método para buscar consultas por período E que estejam confirmadas
    List<Consulta> findByDataHoraBetweenAndConfirmadaTrue(LocalDateTime inicio, LocalDateTime fim);
}