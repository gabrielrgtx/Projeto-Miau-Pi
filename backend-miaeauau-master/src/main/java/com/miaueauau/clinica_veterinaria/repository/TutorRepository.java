// src/main/java/com/miaueauau/clinica_veterinaria/repository/TutorRepository.java
package com.miaueauau.clinica_veterinaria.repository;

import com.miaueauau.clinica_veterinaria.model.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TutorRepository extends JpaRepository<Tutor, Long> {

    // REMOVA ESTES MÉTODOS SE EXISTIREM AQUI:
    // Optional<Tutor> findByEmail(String email);
    // Optional<Tutor> findByCpf(String cpf);
    // boolean existsByEmail(String email);
    // boolean existsByCpf(String cpf);

    // Mantenha apenas métodos que busquem por campos que REALMENTE estão na entidade Tutor
    // Por exemplo, você pode ter:
    // List<Tutor> findByEndereco(String endereco);
    // Optional<Tutor> findByTelefone(String telefone);
}