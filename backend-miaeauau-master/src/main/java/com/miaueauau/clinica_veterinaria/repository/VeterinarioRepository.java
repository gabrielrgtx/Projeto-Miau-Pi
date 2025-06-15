package com.miaueauau.clinica_veterinaria.repository;

import com.miaueauau.clinica_veterinaria.model.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Importe Optional
import java.util.List; // Importe List (se for usar findByEspecialidade)

@Repository
public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    // --- NOVO: Métodos para buscar e verificar Veterinários por CRMV ---
    Optional<Veterinario> findByCrmv(String crmv); // Busca um veterinário por CRMV
    boolean existsByCrmv(String crmv); // Verifica se um veterinário com o CRMV já existe

    // Exemplo de consulta por especialidade (como você sugeriu)
    List<Veterinario> findByEspecialidade(String especialidade);

    // Você pode querer buscar um veterinário pelo ID do funcionário associado
    // Optional<Veterinario> findByFuncionarioId(Long funcionarioId);
}