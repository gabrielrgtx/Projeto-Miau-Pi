package com.miaueauau.clinica_veterinaria.service;

import com.miaueauau.clinica_veterinaria.model.Consulta;
import com.miaueauau.clinica_veterinaria.model.DisponibilidadeVeterinario;
import com.miaueauau.clinica_veterinaria.model.Veterinario;
import com.miaueauau.clinica_veterinaria.model.Procedimento;
import com.miaueauau.clinica_veterinaria.model.Paciente;
import com.miaueauau.clinica_veterinaria.model.Tutor;
import com.miaueauau.clinica_veterinaria.repository.ConsultaRepository;
import com.miaueauau.clinica_veterinaria.repository.DisponibilidadeVeterinarioRepository;
import com.miaueauau.clinica_veterinaria.repository.TutorRepository;
import com.miaueauau.clinica_veterinaria.repository.PacienteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Hibernate;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ConsultaService {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private VeterinarioService veterinarioService;

    @Autowired
    private DisponibilidadeVeterinarioRepository disponibilidadeVeterinarioRepository;

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @PersistenceContext
    private EntityManager entityManager;


    @Transactional // Garante que a sessão esteja aberta para inicialização LAZY
    public List<Consulta> listarTodasConsultas() {
        List<Consulta> consultas = consultaRepository.findAll();
        // REMOVIDO: Inicializações complexas aqui. O DTO.fromEntity() no Controller fará isso.
        return consultas;
    }

    @Transactional
    public Optional<Consulta> buscarConsultaPorId(Long id) {
        Optional<Consulta> consultaOptional = consultaRepository.findById(id);
        // REMOVIDO: Inicializações complexas aqui. O DTO.fromEntity() no Controller fará isso.
        return consultaOptional;
    }

    @Transactional
    public Consulta salvarConsulta(Consulta consulta) {
        // Obter as entidades relacionadas do banco (se vierem apenas com ID no JSON)
        // Isso é importante para que o objeto "consulta" que será salvo esteja com as referências gerenciadas
        if (consulta.getPaciente() != null && consulta.getPaciente().getId() != null) {
            Paciente paciente = pacienteRepository.findById(consulta.getPaciente().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado com ID: " + consulta.getPaciente().getId()));
            consulta.setPaciente(paciente);
        }
        if (consulta.getVeterinario() != null && consulta.getVeterinario().getId() != null) {
            Veterinario veterinario = veterinarioService.buscarVeterinarioPorId(consulta.getVeterinario().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Veterinário não encontrado com ID: " + consulta.getVeterinario().getId()));
            consulta.setVeterinario(veterinario);
        }
        // Se a lista de procedimentos vier no JSON de entrada, o Hibernate vai gerenciar
        // mas aqui vamos garantir que eles estejam carregados e anexados se vierem por ID
        if (consulta.getProcedimentos() != null && !consulta.getProcedimentos().isEmpty()) {
            List<Procedimento> managedProcedimentos = new ArrayList<>();
            for (Procedimento proc : consulta.getProcedimentos()) {
                if (proc.getId() != null) {
                    Procedimento managedProc = entityManager.find(Procedimento.class, proc.getId());
                    if (managedProc == null) {
                        throw new IllegalArgumentException("Procedimento não encontrado com ID: " + proc.getId());
                    }
                    managedProcedimentos.add(managedProc);
                } else {
                    throw new IllegalArgumentException("Procedimento sem ID não pode ser associado diretamente.");
                }
            }
            consulta.setProcedimentos(managedProcedimentos);
        }


        if (isVeterinarioDisponivel(consulta.getVeterinario(), consulta.getDataHora(), consulta.getProcedimentos())) {
            Consulta novaConsulta = consultaRepository.save(consulta);
            // REMOVIDO: Inicializações complexas aqui. O DTO.fromEntity() no Controller fará isso.
            return novaConsulta;
        } else {
            throw new IllegalArgumentException("Veterinário não disponível para a duração total da consulta.");
        }
    }

    @Transactional
    public Consulta atualizarConsulta(Long id, Consulta consultaAtualizada) {
        Optional<Consulta> consultaExistenteOptional = consultaRepository.findById(id);
        if (consultaExistenteOptional.isEmpty()) {
            throw new RuntimeException("Consulta não encontrada com o ID: " + id);
        }
        Consulta consultaExistente = consultaExistenteOptional.get();

        consultaExistente.setMotivo(consultaAtualizada.getMotivo());
        consultaExistente.setDiagnostico(consultaAtualizada.getDiagnostico());
        consultaExistente.setTratamento(consultaAtualizada.getTratamento());
        consultaExistente.setConfirmada(consultaAtualizada.isConfirmada());
        consultaExistente.setTipoAtendimento(consultaAtualizada.getTipoAtendimento());
        consultaExistente.setDataHora(consultaAtualizada.getDataHora());

        if (consultaAtualizada.getPaciente() != null && consultaAtualizada.getPaciente().getId() != null) {
            Paciente paciente = pacienteRepository.findById(consultaAtualizada.getPaciente().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado com ID: " + consultaAtualizada.getPaciente().getId()));
            consultaExistente.setPaciente(paciente);
        }
        if (consultaAtualizada.getVeterinario() != null && consultaAtualizada.getVeterinario().getId() != null) {
            Veterinario veterinario = veterinarioService.buscarVeterinarioPorId(consultaAtualizada.getVeterinario().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Veterinário não encontrado com ID: " + consultaAtualizada.getVeterinario().getId()));
            consultaExistente.setVeterinario(veterinario);
        }

        if (consultaAtualizada.getProcedimentos() != null) {
            List<Procedimento> managedProcedimentos = new ArrayList<>();
            for (Procedimento proc : consultaAtualizada.getProcedimentos()) {
                if (proc.getId() != null) {
                    Procedimento managedProc = entityManager.find(Procedimento.class, proc.getId());
                    if (managedProc == null) {
                        throw new IllegalArgumentException("Procedimento não encontrado com ID: " + proc.getId());
                    }
                    managedProcedimentos.add(managedProc);
                } else {
                    throw new IllegalArgumentException("Procedimento sem ID não pode ser associado diretamente na atualização.");
                }
            }
            consultaExistente.setProcedimentos(managedProcedimentos);
        }


        if (isVeterinarioDisponivel(consultaExistente.getVeterinario(), consultaExistente.getDataHora(), consultaExistente.getProcedimentos())) {
            Consulta updatedConsulta = consultaRepository.save(consultaExistente);
            // REMOVIDO: Inicializações complexas aqui. O DTO.fromEntity() no Controller fará isso.
            return updatedConsulta;
        } else {
            throw new IllegalArgumentException("Veterinário não disponível para a duração total da consulta.");
        }
    }


    @Transactional
    public void deletarConsulta(Long id) {
        consultaRepository.deleteById(id);
    }

    @Transactional
    public List<Consulta> buscarConsultasPorVeterinarioEPeriodo(Veterinario veterinario, LocalDateTime inicio, LocalDateTime fim) {
        List<Consulta> consultas = consultaRepository.findByVeterinarioAndDataHoraBetween(veterinario, inicio, fim);
        // REMOVIDO: Inicializações complexas aqui. O DTO.fromEntity() no Controller fará isso.
        return consultas;
    }

    @Transactional
    public Consulta confirmarConsulta(Long id) {
        Optional<Consulta> consultaOptional = consultaRepository.findById(id);
        if (consultaOptional.isPresent()) {
            Consulta consulta = consultaOptional.get();
            consulta.setConfirmada(true);
            Consulta confirmedConsulta = consultaRepository.save(consulta);
            // REMOVIDO: Inicializações complexas aqui. O DTO.fromEntity() no Controller fará isso.
            return confirmedConsulta;
        }
        return null;
    }

    private boolean isVeterinarioDisponivel(Veterinario veterinario, LocalDateTime dataHora, List<Procedimento> procedimentos) {
        LocalDateTime inicioConsulta = dataHora.minusMinutes(30);
        LocalDateTime fimConsulta = dataHora.plusMinutes(30);

        if (procedimentos != null && !procedimentos.isEmpty()) {
            int duracaoProcedimentos = procedimentos.stream().mapToInt(p -> 15).sum();
            inicioConsulta = inicioConsulta.minusMinutes(duracaoProcedimentos / 2);
            fimConsulta = fimConsulta.plusMinutes(duracaoProcedimentos / 2);
        }

        // NOVO: Inicializa o veterinário e suas coleções APENAS para a verificação de disponibilidade
        if (veterinario != null) {
            Hibernate.initialize(veterinario.getUser()); // User do Veterinario
            Hibernate.initialize(veterinario.getConsultas());
            Hibernate.initialize(veterinario.getDisponibilidades());
        }

        List<DisponibilidadeVeterinario> disponibilidades = disponibilidadeVeterinarioRepository
                .findByVeterinarioAndInicioLessThanAndFimGreaterThan(veterinario, fimConsulta, inicioConsulta);

        return !disponibilidades.isEmpty();
    }

    private boolean isVeterinarioDisponivel(Veterinario veterinario, LocalDateTime dataHora) {
        return isVeterinarioDisponivel(veterinario, dataHora, null);
    }

    @Transactional
    public List<Consulta> buscarConsultasPorTutor(Tutor tutor) {
        List<Paciente> petsDoTutor = pacienteService.buscarPacientesPorTutor(tutor);
        List<Consulta> consultasDoTutor = new ArrayList<>();
        for (Paciente pet : petsDoTutor) {
            // Garante que o Paciente está carregado se a Consulta o referenciar
            Hibernate.initialize(pet); // Para evitar proxy em pet
            consultasDoTutor.addAll(consultaRepository.findByPaciente(pet));
        }
        // REMOVIDO: Inicializações complexas aqui. O DTO.fromEntity() no Controller fará isso.
        return consultasDoTutor;
    }

    @Transactional
    public List<Consulta> buscarConsultasPorPaciente(Long pacienteId) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado com o ID: " + pacienteId));
        List<Consulta> consultas = consultaRepository.findByPaciente(paciente);
        // REMOVIDO: Inicializações complexas aqui. O DTO.fromEntity() no Controller fará isso.
        return consultas;
    }

    public Double calcularFaturamentoPorPeriodo(LocalDate inicioPeriodo, LocalDate fimPeriodo) {
        LocalDateTime inicioDateTime = inicioPeriodo.atStartOfDay();
        LocalDateTime fimDateTime = fimPeriodo.atTime(LocalTime.MAX);

        List<Consulta> consultasConfirmadas = consultaRepository.findByDataHoraBetweenAndConfirmadaTrue(inicioDateTime, fimDateTime);

        // AQUI pode ser necessário inicializar Procedimentos se eles são LAZY e o DTO precisa deles
        return consultasConfirmadas.stream()
                .filter(consulta -> consulta.getProcedimentos() != null)
                .flatMap(consulta -> {
                    Hibernate.initialize(consulta.getProcedimentos()); // Inicializa os procedimentos para o stream
                    return consulta.getProcedimentos().stream();
                })
                .mapToDouble(Procedimento::getPreco)
                .sum();
    }
}