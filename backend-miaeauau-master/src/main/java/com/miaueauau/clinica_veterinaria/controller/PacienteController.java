package com.miaueauau.clinica_veterinaria.controller;

import com.miaueauau.clinica_veterinaria.model.Paciente; // Importe a entidade Paciente
import com.miaueauau.clinica_veterinaria.model.Tutor;
import com.miaueauau.clinica_veterinaria.model.User;
import com.miaueauau.clinica_veterinaria.service.PacienteService;
import com.miaueauau.clinica_veterinaria.repository.TutorRepository;
import com.miaueauau.clinica_veterinaria.repository.UserRepository;

import com.miaueauau.clinica_veterinaria.dto.response.PacienteResponseDTO; // Importe o DTO de resposta do Paciente

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;
    @Autowired
    private TutorRepository tutorRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<PacienteResponseDTO>> listarTodosPacientes() {
        List<Paciente> pacientes = pacienteService.listarTodosPacientes();
        // Converte a lista de entidades para uma lista de DTOs antes de retornar
        return new ResponseEntity<>(PacienteResponseDTO.fromEntityList(pacientes), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> buscarPacientePorId(@PathVariable Long id) {
        Optional<Paciente> paciente = pacienteService.buscarPacientePorId(id);
        // Converte a entidade para DTO antes de retornar
        return paciente.map(p -> new ResponseEntity<>(PacienteResponseDTO.fromEntity(p), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<?> salvarPaciente(@RequestBody Paciente paciente) {
        try {
            Paciente novoPaciente = pacienteService.salvarPaciente(paciente);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Paciente adicionado com sucesso!");
            response.put("id", novoPaciente.getId().toString()); // Retorna o ID do novo paciente
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Erro ao salvar paciente: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarPaciente(@PathVariable Long id, @RequestBody Paciente pacienteAtualizado) {
        try {
            Paciente pacienteSalvo = pacienteService.atualizarPaciente(id, pacienteAtualizado);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Paciente atualizado com sucesso!");
            response.put("id", pacienteSalvo.getId().toString()); // Retorna o ID do paciente atualizado
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Erro ao atualizar paciente: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPaciente(@PathVariable Long id) {
        try {
            pacienteService.deletarPaciente(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para o tutor ver apenas seus pets
    @GetMapping("/meus-pets")
    public ResponseEntity<List<PacienteResponseDTO>> listarMeusPets() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User loggedInUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário logado não encontrado no banco de dados."));

        Tutor tutor = tutorRepository.findById(loggedInUser.getId())
                .orElseThrow(() -> new RuntimeException("Perfil de Tutor não encontrado para o usuário logado."));

        List<Paciente> meusPets = pacienteService.buscarPacientesPorTutor(tutor);

        // Converte a lista de entidades para uma lista de DTOs antes de retornar
        return new ResponseEntity<>(PacienteResponseDTO.fromEntityList(meusPets), HttpStatus.OK);
    }
}