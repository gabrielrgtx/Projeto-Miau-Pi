// src/main/java/com/miaueauau/clinica_veterinaria/service/TutorService.java
package com.miaueauau.clinica_veterinaria.service;

import com.miaueauau.clinica_veterinaria.model.Tutor;
import com.miaueauau.clinica_veterinaria.model.User; // Importe a entidade User
import com.miaueauau.clinica_veterinaria.repository.TutorRepository;
import com.miaueauau.clinica_veterinaria.repository.UserRepository; // Importe UserRepository

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TutorService {

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private UserRepository userRepository;


    public List<Tutor> listarTodosTutores() {
        return tutorRepository.findAll();
    }

    public Optional<Tutor> buscarTutorPorId(Long id) {
        return tutorRepository.findById(id);
    }

    public Tutor salvarTutor(Tutor tutor) {
        if (tutor.getUser() == null) {
            throw new IllegalArgumentException("Tutor deve estar associado a um Usuário.");
        }
        // Validação de unicidade para CPF e Email AGORA NO UserRepository
        if (userRepository.findByCpf(tutor.getUser().getCpf()).isPresent()) {
            throw new IllegalArgumentException("Já existe um usuário com este CPF.");
        }
        if (userRepository.findByEmail(tutor.getUser().getEmail()).isPresent()) {
            throw new IllegalArgumentException("Já existe um usuário com este e-mail.");
        }

        User savedUser = userRepository.save(tutor.getUser());
        tutor.setId(savedUser.getId());
        tutor.setUser(savedUser);

        return tutorRepository.save(tutor);
    }

    public Tutor atualizarTutor(Long id, Tutor tutorAtualizado) {
        return tutorRepository.findById(id)
                .map(tutorExistente -> {
                    if (tutorAtualizado.getUser() == null) {
                        throw new IllegalArgumentException("Tutor deve estar associado a um Usuário para atualização.");
                    }

                    User userExistente = tutorExistente.getUser();
                    User userAtualizado = tutorAtualizado.getUser();

                    // Validação de unicidade para CPF na atualização
                    if (!userExistente.getCpf().equals(userAtualizado.getCpf())) {
                        if (userRepository.findByCpf(userAtualizado.getCpf()).isPresent()) {
                            throw new IllegalArgumentException("CPF já cadastrado para outro usuário.");
                        }
                    }
                    // Validação de unicidade para Email na atualização
                    if (!userExistente.getEmail().equals(userAtualizado.getEmail())) {
                        if (userRepository.findByEmail(userAtualizado.getEmail()).isPresent()) {
                            throw new IllegalArgumentException("E-mail já cadastrado para outro usuário.");
                        }
                    }

                    // Atualiza os campos do USUÁRIO associado
                    userExistente.setNome(userAtualizado.getNome());
                    userExistente.setSobrenome(userAtualizado.getSobrenome());
                    userExistente.setEmail(userAtualizado.getEmail());
                    userExistente.setCpf(userAtualizado.getCpf());
                    userExistente.setPassword(userAtualizado.getPassword());
                    userExistente.setRole(userAtualizado.getRole()); // Garante que a role seja atualizada

                    userRepository.save(userExistente); // Salva as atualizações no User

                    // Atualiza os campos ESPECÍFICOS do Tutor
                    tutorExistente.setEndereco(tutorAtualizado.getEndereco());
                    tutorExistente.setTelefone(tutorAtualizado.getTelefone());
                    tutorExistente.setDataNascimento(tutorAtualizado.getDataNascimento());

                    return tutorRepository.save(tutorExistente);
                })
                .orElseThrow(() -> new RuntimeException("Tutor não encontrado com o ID: " + id));
    }

    public void deletarTutor(Long id) {
        if (!tutorRepository.existsById(id)) {
            throw new IllegalArgumentException("Tutor com ID " + id + " não encontrado para deleção.");
        }
        Optional<Tutor> tutorOptional = tutorRepository.findById(id);
        if (tutorOptional.isPresent()) {
            tutorRepository.deleteById(id);
            // Se o relacionamento Tutor (MapsId) para User tem cascade=ALL e orphanRemoval=true,
            // o User será deletado automaticamente aqui.
            // Caso contrário, você pode precisar de userRepository.deleteById(tutorOptional.get().getUser().getId());
        } else {
            throw new IllegalArgumentException("Tutor com ID " + id + " não encontrado para deleção.");
        }
    }
}