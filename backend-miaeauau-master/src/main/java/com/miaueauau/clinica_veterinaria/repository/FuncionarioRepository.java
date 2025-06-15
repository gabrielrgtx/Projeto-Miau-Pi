package com.miaueauau.clinica_veterinaria.repository;

import com.miaueauau.clinica_veterinaria.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    // Este método busca um Funcionario através do email do User associado
    Optional<Funcionario> findByUserEmail(String email);

    // Se você tinha um findByEmail(String email) aqui, ele deve ser removido
    // Optional<Funcionario> findByEmail(String email); // REMOVER se não existir mais em Funcionario
}