package com.miaueauau.clinica_veterinaria.repository;

import com.miaueauau.clinica_veterinaria.model.Procedimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Importe para Optional

@Repository
public interface ProcedimentoRepository extends JpaRepository<Procedimento, Long> {

    // NOVO: Método para buscar um procedimento pelo nome
    Optional<Procedimento> findByNome(String nome);

    // NOVO: Método para verificar se um procedimento com o nome já existe
    boolean existsByNome(String nome);
}