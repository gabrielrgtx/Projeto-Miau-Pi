package com.miaueauau.clinica_veterinaria.repository;

import com.miaueauau.clinica_veterinaria.model.DisponibilidadeVeterinario;
import com.miaueauau.clinica_veterinaria.model.Veterinario; // Importe Veterinario
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DisponibilidadeVeterinarioRepository extends JpaRepository<DisponibilidadeVeterinario, Long> {

    // NOVO/CORRIGIDO: Método para buscar disponibilidades de um veterinário que se sobrepõem a um período
    // O nome do método é crucial para o Spring Data JPA criar a query corretamente.
    List<DisponibilidadeVeterinario> findByVeterinarioAndInicioLessThanAndFimGreaterThan(
            Veterinario veterinario, LocalDateTime fim, LocalDateTime inicio);

    // Podemos adicionar outros métodos de consulta específicos no futuro
}