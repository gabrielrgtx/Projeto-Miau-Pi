package com.miaueauau.clinica_veterinaria.repository;

import com.miaueauau.clinica_veterinaria.model.Exame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExameRepository extends JpaRepository<Exame, Long> {

    Optional<Exame> findById(Long id);
    List<Exame> findByTipo(String tipo);
    List<Exame> findByDataRealizacao(LocalDateTime dataRealizacao);

    // Para buscar pelo nome do paciente, precisaremos usar uma Query JPA ou um método derivado mais complexo
    // Exemplo usando Query JPA:
    //@Query("SELECT e FROM Exame e WHERE e.consultaProntuario.prontuario.nomePaciente = :nomePaciente")
    //List<Exame> findByConsultaProntuario_Prontuario_NomePaciente(@Param("nomePaciente") String nomePaciente);
}