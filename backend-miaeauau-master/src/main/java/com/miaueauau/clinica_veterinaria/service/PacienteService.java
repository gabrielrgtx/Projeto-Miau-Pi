package com.miaueauau.clinica_veterinaria.service;

import com.miaueauau.clinica_veterinaria.model.Paciente;
import com.miaueauau.clinica_veterinaria.model.Tutor;
import com.miaueauau.clinica_veterinaria.repository.PacienteRepository;
import com.miaueauau.clinica_veterinaria.repository.TutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// Importar Hibernate para inicialização manual
import org.hibernate.Hibernate; // NOVO IMPORT
import jakarta.persistence.EntityManager; // NOVO IMPORT
import jakarta.persistence.PersistenceContext; // NOVO IMPORT


@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private TutorRepository tutorRepository;

    @PersistenceContext // Injete o EntityManager
    private EntityManager entityManager;


    @Transactional // Garante que a operação seja transacional e a sessão aberta
    public List<Paciente> listarTodosPacientes() {
        List<Paciente> pacientes = pacienteRepository.findAll();
        // NOVO: Inicializa as coleções LAZY *antes* de retornar a lista
        for (Paciente paciente : pacientes) {
            // Inicializa a coleção de consultas do paciente (se Paciente.consultas é LAZY)
            Hibernate.initialize(paciente.getConsultas());

            // Garante que o tutor e seu user estejam carregados (já são EAGER, mas bom para consistência)
            if (paciente.getTutor() != null) {
                Hibernate.initialize(paciente.getTutor().getUser());
                // Se Tutor.pacientes também é LAZY e causou loop, pode ser necessário inicializar:
                // Hibernate.initialize(paciente.getTutor().getPacientes()); // Mas cuidado com loops aqui
            }
            // Se as consultas têm veterinário LAZY e isso causa erro ao serializar consulta:
            // for (Consulta consulta : paciente.getConsultas()) {
            //    Hibernate.initialize(consulta.getVeterinario());
            // }
        }
        return pacientes;
    }

    public Optional<Paciente> buscarPacientePorId(Long id) {
        // Para buscar por ID, você também pode precisar inicializar coleções
        Optional<Paciente> paciente = pacienteRepository.findById(id);
        paciente.ifPresent(p -> {
            Hibernate.initialize(p.getConsultas());
            if (p.getTutor() != null) {
                Hibernate.initialize(p.getTutor().getUser());
            }
        });
        return paciente;
    }


    @Transactional
    public Paciente salvarPaciente(Paciente paciente) {
        if (paciente.getTutor() == null || paciente.getTutor().getId() == null) {
            throw new IllegalArgumentException("O paciente deve estar associado a um tutor existente (informe o ID do tutor).");
        }

        Tutor tutor = tutorRepository.findById(paciente.getTutor().getId())
                .orElseThrow(() -> new RuntimeException("Tutor não encontrado com o ID: " + paciente.getTutor().getId()));

        paciente.setTutor(tutor);

        if (paciente.getDataNascimento() != null && paciente.getDataNascimento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data de nascimento do paciente não pode ser no futuro.");
        }

        // Salvar o paciente
        Paciente savedPaciente = pacienteRepository.save(paciente);

        // Opcional: Se as coleções de Paciente (como 'consultas') são LAZY e você quer retorná-las preenchidas,
        // pode ser necessário inicializá-las aqui também antes de retornar:
        // Hibernate.initialize(savedPaciente.getConsultas());
        return savedPaciente;
    }

    @Transactional
    public Paciente atualizarPaciente(Long id, Paciente pacienteAtualizado) {
        return pacienteRepository.findById(id)
                .map(pacienteExistente -> {
                    pacienteExistente.setNome(pacienteAtualizado.getNome());
                    pacienteExistente.setEspecie(pacienteAtualizado.getEspecie());
                    pacienteExistente.setRaca(pacienteAtualizado.getRaca());
                    pacienteExistente.setDataNascimento(pacienteAtualizado.getDataNascimento());
                    pacienteExistente.setPeso(pacienteAtualizado.getPeso());

                    if (pacienteAtualizado.getTutor() != null && pacienteAtualizado.getTutor().getId() != null) {
                        Tutor novoTutor = tutorRepository.findById(pacienteAtualizado.getTutor().getId())
                                .orElseThrow(() -> new RuntimeException("Novo Tutor não encontrado com o ID: " + pacienteAtualizado.getTutor().getId()));
                        pacienteExistente.setTutor(novoTutor);
                    }

                    if (pacienteExistente.getDataNascimento() != null && pacienteExistente.getDataNascimento().isAfter(LocalDate.now())) {
                        throw new IllegalArgumentException("A data de nascimento do paciente não pode ser no futuro.");
                    }

                    // Salva as alterações no paciente
                    Paciente updatedPaciente = pacienteRepository.save(pacienteExistente);

                    // Opcional: Inicializar coleções aqui também se o retorno precisar delas completas
                    // Hibernate.initialize(updatedPaciente.getConsultas());
                    return updatedPaciente;
                })
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado com o ID: " + id));
    }


    @Transactional
    public void deletarPaciente(Long id) {
        if (!pacienteRepository.existsById(id)) {
            throw new IllegalArgumentException("Paciente com ID " + id + " não encontrado para deleção.");
        }
        pacienteRepository.deleteById(id);
    }

    @Transactional
    public List<Paciente> buscarPacientesPorTutor(Tutor tutor) {
        // Esta lista também pode precisar de inicialização se as coleções dos pacientes forem acessadas fora da transação
        List<Paciente> pets = pacienteRepository.findByTutor(tutor);
        for (Paciente pet : pets) {
            Hibernate.initialize(pet.getConsultas());
            if (pet.getTutor() != null) { // Tutor já é EAGER, mas para segurança
                Hibernate.initialize(pet.getTutor().getUser());
            }
        }
        return pets;
    }
}