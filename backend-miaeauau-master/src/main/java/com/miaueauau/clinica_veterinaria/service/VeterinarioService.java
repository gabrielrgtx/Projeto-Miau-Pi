package com.miaueauau.clinica_veterinaria.service;

import com.miaueauau.clinica_veterinaria.model.Veterinario;
import com.miaueauau.clinica_veterinaria.model.User;
import com.miaueauau.clinica_veterinaria.repository.VeterinarioRepository;
import com.miaueauau.clinica_veterinaria.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@Service
public class VeterinarioService {

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;


    public List<Veterinario> listarTodosVeterinarios() {
        return veterinarioRepository.findAll();
    }

    public Optional<Veterinario> buscarVeterinarioPorId(Long id) {
        return veterinarioRepository.findById(id);
    }

    @Transactional
    public Veterinario salvarVeterinario(Veterinario veterinario) {
        System.out.println("DEBUG (salvarVeterinario): Iniciando. Veterinario ID: " + veterinario.getId() + ", User ID: " + (veterinario.getUser() != null ? veterinario.getUser().getId() : "null"));

        if (veterinario.getUser() == null) {
            throw new IllegalArgumentException("Veterinário deve estar associado a um Usuário.");
        }

        User userToSave = veterinario.getUser();
        User processedUser; // O User que será associado ao Veterinario

        // --- LÓGICA DE SALVAR/ATUALIZAR O USUÁRIO ---
        if (userToSave.getId() != null) {
            System.out.println("DEBUG (salvarVeterinario): User ID não é nulo. Assumindo atualização de User existente.");
            Optional<User> existingUserOpt = userRepository.findById(userToSave.getId());
            if (!existingUserOpt.isPresent()) {
                throw new IllegalArgumentException("Usuário associado com ID " + userToSave.getId() + " não encontrado para atualização.");
            }
            User existingUser = existingUserOpt.get(); // User gerenciado

            // Atualiza os dados do User gerenciado
            existingUser.setNome(userToSave.getNome());
            existingUser.setSobrenome(userToSave.getSobrenome());
            existingUser.setEmail(userToSave.getEmail());
            existingUser.setCpf(userToSave.getCpf());
            if (userToSave.getPassword() != null && !userToSave.getPassword().isEmpty()) {
                existingUser.setPassword(userToSave.getPassword());
            }
            existingUser.setRole(userToSave.getRole());

            processedUser = userRepository.save(existingUser); // Salva as atualizações no User existente

        } else {
            // Se é um NOVO User (User.id é nulo) - este é o caminho para CRIAR um novo veterinário
            System.out.println("DEBUG (salvarVeterinario): User ID é nulo. Criando novo User.");
            if (userRepository.findByEmail(userToSave.getEmail()).isPresent()) {
                throw new IllegalArgumentException("E-mail já cadastrado para outro usuário.");
            }
            if (userRepository.findByCpf(userToSave.getCpf()).isPresent()) {
                throw new IllegalArgumentException("CPF já cadastrado para outro usuário.");
            }
            processedUser = userRepository.save(userToSave); // Salva o novo User, ID é gerado aqui
            System.out.println("DEBUG (salvarVeterinario): Novo User salvo. ID gerado: " + processedUser.getId());
        }

        // Associa o Veterinario ao User (o objeto 'processedUser' é o User gerenciado)
        veterinario.setUser(processedUser);
        veterinario.setId(processedUser.getId()); // Define o ID do Veterinario com o ID do User

        // --- LÓGICA DE SALVAR/ATUALIZAR O VETERINÁRIO ---
        // Usar repository.save() para ambos os casos (novo e atualização)
        // O .save() do JpaRepository faz o merge internamente se o ID não for nulo, ou persist se for.
        System.out.println("DEBUG (salvarVeterinario): Tentando salvar/atualizar Veterinario com repository.save(). Final Veterinario ID: " + veterinario.getId());
        return veterinarioRepository.save(veterinario); // Salva ou atualiza o Veterinario
    }

    @Transactional
    public Veterinario atualizarVeterinario(Long id, Veterinario veterinarioAtualizado) {
        System.out.println("DEBUG (atualizarVeterinario): Iniciando. ID da URL: " + id + ", Veterinario ID no corpo: " + veterinarioAtualizado.getId() + ", User ID no corpo: " + (veterinarioAtualizado.getUser() != null ? veterinarioAtualizado.getUser().getId() : "null"));

        if (!id.equals(veterinarioAtualizado.getId())) {
            throw new IllegalArgumentException("ID do veterinário na URL não corresponde ao ID no corpo da requisição.");
        }
        if (veterinarioAtualizado.getUser() == null || !id.equals(veterinarioAtualizado.getUser().getId())) {
            throw new IllegalArgumentException("Dados do usuário associado inválidos ou ID não correspondente.");
        }

        // Reutiliza a lógica de salvarVeterinario, que agora lida com a atualização.
        return salvarVeterinario(veterinarioAtualizado);
    }

    @Transactional
    public void deletarVeterinario(Long id) {
        System.out.println("DEBUG (deletarVeterinario): Tentando deletar Veterinario ID: " + id);
        Optional<Veterinario> veterinarioOptional = veterinarioRepository.findById(id);

        if (!veterinarioOptional.isPresent()) {
            System.out.println("DEBUG (deletarVeterinario): Veterinario ID " + id + " NÃO encontrado.");
            throw new IllegalArgumentException("Veterinário com ID " + id + " não encontrado para deleção.");
        }

        Veterinario veterinarioToDelete = veterinarioOptional.get();
        System.out.println("DEBUG (deletarVeterinario): Encontrado Veterinario ID: " + veterinarioToDelete.getId() + ", User ID associado: " + (veterinarioToDelete.getUser() != null ? veterinarioToDelete.getUser().getId() : "null"));

        veterinarioRepository.delete(veterinarioToDelete);

        System.out.println("DEBUG (deletarVeterinario): Tentativa de exclusão de Veterinario ID " + id + " completa.");
    }
}