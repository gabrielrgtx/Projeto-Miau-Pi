package com.miaueauau.clinica_veterinaria.service;

import com.miaueauau.clinica_veterinaria.model.Procedimento;
import com.miaueauau.clinica_veterinaria.repository.ProcedimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProcedimentoService {

    @Autowired
    private ProcedimentoRepository procedimentoRepository;

    public List<Procedimento> listarTodosProcedimentos() {
        return procedimentoRepository.findAll();
    }

    public Optional<Procedimento> buscarProcedimentoPorId(Long id) {
        return procedimentoRepository.findById(id);
    }

    public Procedimento salvarProcedimento(Procedimento procedimento) {
        // NOVO: Validação de unicidade para o nome do procedimento antes de salvar
        if (procedimentoRepository.existsByNome(procedimento.getNome())) {
            throw new IllegalArgumentException("Já existe um procedimento com este nome.");
        }
        return procedimentoRepository.save(procedimento);
    }

    public Procedimento atualizarProcedimento(Long id, Procedimento procedimentoAtualizado) {
        return procedimentoRepository.findById(id)
                .map(procedimentoExistente -> {
                    // NOVO: Validação de unicidade para o nome do procedimento na atualização
                    // Verifica se o nome foi alterado E se o novo nome já existe para OUTRO procedimento
                    if (!procedimentoExistente.getNome().equals(procedimentoAtualizado.getNome()) &&
                            procedimentoRepository.existsByNome(procedimentoAtualizado.getNome())) {
                        throw new IllegalArgumentException("Nome do procedimento já cadastrado para outro.");
                    }

                    // Atualiza os campos relevantes
                    procedimentoExistente.setNome(procedimentoAtualizado.getNome());
                    procedimentoExistente.setDescricao(procedimentoAtualizado.getDescricao());
                    procedimentoExistente.setPreco(procedimentoAtualizado.getPreco());

                    return procedimentoRepository.save(procedimentoExistente);
                })
                .orElseThrow(() -> new RuntimeException("Procedimento não encontrado com o ID: " + id));
    }

    public void deletarProcedimento(Long id) {
        // NOVO: Validação para garantir que o procedimento existe antes de deletar
        if (!procedimentoRepository.existsById(id)) {
            throw new IllegalArgumentException("Procedimento com ID " + id + " não encontrado para deleção.");
        }
        procedimentoRepository.deleteById(id);
    }

    // Você pode adicionar outros métodos de lógica de negócios aqui, se necessário.
}