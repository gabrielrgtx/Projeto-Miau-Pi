package com.miaueauau.clinica_veterinaria.repository;

import com.miaueauau.clinica_veterinaria.model.User; // Importa sua nova entidade User
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email); // Método para buscar usuário por email (para login e validação)
    Optional<User> findByCpf(String cpf);     // Método para buscar usuário por CPF (para validação)
}