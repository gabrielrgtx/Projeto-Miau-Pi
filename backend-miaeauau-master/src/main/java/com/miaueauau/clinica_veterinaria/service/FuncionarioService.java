package com.miaueauau.clinica_veterinaria.service;

import com.miaueauau.clinica_veterinaria.model.Funcionario;
import com.miaueauau.clinica_veterinaria.model.User;
import com.miaueauau.clinica_veterinaria.repository.FuncionarioRepository;
import com.miaueauau.clinica_veterinaria.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importe Transactional
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@Service
public class FuncionarioService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;


    public List<Funcionario> listarTodosFuncionarios() {
        return funcionarioRepository.findAll();
    }

    public Optional<Funcionario> buscarFuncionarioPorId(Long id) {
        return funcionarioRepository.findById(id);
    }

    @Transactional
    public Funcionario salvarFuncionario(Funcionario funcionario) {
        System.out.println("DEBUG (salvarFuncionario): Iniciando. Funcionario ID: " + funcionario.getId() + ", User ID: " + (funcionario.getUser() != null ? funcionario.getUser().getId() : "null"));

        if (funcionario.getUser() == null) {
            throw new IllegalArgumentException("Funcionário deve estar associado a um Usuário.");
        }

        User userToSave = funcionario.getUser();

        if (userToSave.getId() != null) {
            System.out.println("DEBUG (salvarFuncionario): User ID não é nulo. Assumindo atualização ou User existente.");
            Optional<User> existingUserOpt = userRepository.findById(userToSave.getId());
            if (existingUserOpt.isPresent()) {
                User existingUser = existingUserOpt.get();
                System.out.println("DEBUG (salvarFuncionario): User existente encontrado. ID: " + existingUser.getId());
                existingUser.setNome(userToSave.getNome());
                existingUser.setSobrenome(userToSave.getSobrenome());
                existingUser.setEmail(userToSave.getEmail());
                existingUser.setCpf(userToSave.getCpf());
                if (userToSave.getPassword() != null && !userToSave.getPassword().isEmpty()) {
                    existingUser.setPassword(userToSave.getPassword());
                }
                existingUser.setRole(userToSave.getRole());
                userRepository.save(existingUser);
                funcionario.setUser(existingUser);
                funcionario.setId(existingUser.getId());
                System.out.println("DEBUG (salvarFuncionario): Funcionario ID setado para existente User ID: " + funcionario.getId());
            } else {
                throw new IllegalArgumentException("Usuário associado com ID " + userToSave.getId() + " não encontrado.");
            }
        } else {
            System.out.println("DEBUG (salvarFuncionario): User ID é nulo. Criando novo User.");
            if (userRepository.findByEmail(userToSave.getEmail()).isPresent()) {
                throw new IllegalArgumentException("E-mail já cadastrado para outro usuário.");
            }
            if (userRepository.findByCpf(userToSave.getCpf()).isPresent()) {
                throw new IllegalArgumentException("CPF já cadastrado para outro usuário.");
            }
            User savedUser = userRepository.save(userToSave);
            System.out.println("DEBUG (salvarFuncionario): Novo User salvo. ID gerado: " + savedUser.getId());
            funcionario.setUser(savedUser);
            funcionario.setId(savedUser.getId());
            System.out.println("DEBUG (salvarFuncionario): Funcionario ID setado para novo User ID: " + funcionario.getId());

            System.out.println("DEBUG (salvarFuncionario): Usando entityManager.persist() para o novo Funcionario.");
            entityManager.persist(funcionario);
            return funcionario;
        }

        System.out.println("DEBUG (salvarFuncionario): Tentando salvar Funcionario com repository.save(). Final Funcionario ID: " + funcionario.getId());
        return funcionarioRepository.save(funcionario);
    }

    @Transactional
    public Funcionario atualizarFuncionario(Long id, Funcionario funcionarioAtualizado) {
        System.out.println("DEBUG (atualizarFuncionario): Iniciando. ID da URL: " + id + ", Funcionario ID no corpo: " + funcionarioAtualizado.getId() + ", User ID no corpo: " + (funcionarioAtualizado.getUser() != null ? funcionarioAtualizado.getUser().getId() : "null"));

        if (!id.equals(funcionarioAtualizado.getId())) {
            throw new IllegalArgumentException("ID do funcionário na URL não corresponde ao ID no corpo da requisição.");
        }
        if (funcionarioAtualizado.getUser() == null || !id.equals(funcionarioAtualizado.getUser().getId())) {
            throw new IllegalArgumentException("Dados do usuário associado inválidos ou ID não correspondente.");
        }

        return salvarFuncionario(funcionarioAtualizado);
    }

    @Transactional // Garante que a operação de deleção seja transacional
    public void deletarFuncionario(Long id) {
        System.out.println("DEBUG (deletarFuncionario): Tentando deletar Funcionario ID: " + id);
        Optional<Funcionario> funcionarioOptional = funcionarioRepository.findById(id);

        if (!funcionarioOptional.isPresent()) {
            System.out.println("DEBUG (deletarFuncionario): Funcionario ID " + id + " NÃO encontrado.");
            throw new IllegalArgumentException("Funcionário com ID " + id + " não encontrado para deleção.");
        }

        Funcionario funcionarioToDelete = funcionarioOptional.get();
        User userToDelete = funcionarioToDelete.getUser(); // Pega o User associado

        System.out.println("DEBUG (deletarFuncionario): Encontrado Funcionario ID: " + funcionarioToDelete.getId() + ", User ID associado: " + (userToDelete != null ? userToDelete.getId() : "null"));

        // A deleção do Funcionario vai cascatear para o User se o @OneToOne em Funcionario tiver CascadeType.ALL e orphanRemoval=true.
        // O @MapsId implica que o ID do Funcionario é o mesmo do User.
        funcionarioRepository.delete(funcionarioToDelete); // Use delete(entity) em vez de deleteById(id)

        System.out.println("DEBUG (deletarFuncionario): Tentativa de exclusão de Funcionario ID " + id + " completa.");
    }


}