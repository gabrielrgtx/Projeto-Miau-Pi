package com.miaueauau.clinica_veterinaria.controller;

import com.miaueauau.clinica_veterinaria.model.Procedimento;
import com.miaueauau.clinica_veterinaria.service.ProcedimentoService;
import jakarta.validation.Valid; // Importe para validação
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult; // Importe para capturar erros de validação
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // Importe para coletar mensagens de erro

@RestController
@RequestMapping("/api/procedimentos") // Define o endpoint base para este controller
public class ProcedimentoController {

    @Autowired
    private ProcedimentoService procedimentoService;

    @GetMapping // GET /api/procedimentos
    public ResponseEntity<List<Procedimento>> listarTodosProcedimentos() {
        List<Procedimento> procedimentos = procedimentoService.listarTodosProcedimentos();
        return new ResponseEntity<>(procedimentos, HttpStatus.OK);
    }

    @GetMapping("/{id}") // GET /api/procedimentos/{id}
    public ResponseEntity<Procedimento> buscarProcedimentoPorId(@PathVariable Long id) {
        Optional<Procedimento> procedimento = procedimentoService.buscarProcedimentoPorId(id);
        return procedimento.map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)); // Retorna 404 se não encontrado
    }

    @PostMapping // POST /api/procedimentos
    public ResponseEntity<?> salvarProcedimento(@Valid @RequestBody Procedimento procedimento, BindingResult result) {
        if (result.hasErrors()) {
            // Se houver erros de validação (@NotBlank, @Size, @NotNull, @Positive)
            List<String> errors = result.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST); // Retorna 400 com a lista de erros
        }
        try {
            Procedimento novoProcedimento = procedimentoService.salvarProcedimento(procedimento);
            return new ResponseEntity<>(novoProcedimento, HttpStatus.CREATED); // Retorna 201 Created
        } catch (IllegalArgumentException e) {
            // Captura exceções como "Já existe um procedimento com este nome."
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // Retorna 409 Conflict
        } catch (Exception e) {
            // Captura outras exceções inesperadas
            return new ResponseEntity<>("Erro interno do servidor ao salvar procedimento.", HttpStatus.INTERNAL_SERVER_ERROR); // Retorna 500 Internal Server Error
        }
    }

    @PutMapping("/{id}") // PUT /api/procedimentos/{id}
    public ResponseEntity<?> atualizarProcedimento(@PathVariable Long id, @Valid @RequestBody Procedimento procedimentoAtualizado, BindingResult result) {
        if (result.hasErrors()) {
            // Se houver erros de validação
            List<String> errors = result.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST); // Retorna 400 com a lista de erros
        }
        try {
            Procedimento procedimentoSalvo = procedimentoService.atualizarProcedimento(id, procedimentoAtualizado);
            return new ResponseEntity<>(procedimentoSalvo, HttpStatus.OK); // Retorna 200 OK
        } catch (IllegalArgumentException e) {
            // Captura exceções como "Nome do procedimento já cadastrado para outro."
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // Retorna 409 Conflict
        } catch (RuntimeException e) {
            // Captura exceções como "Procedimento não encontrado com o ID: "
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // Retorna 404 Not Found
        } catch (Exception e) {
            // Captura outras exceções inesperadas
            return new ResponseEntity<>("Erro interno do servidor ao atualizar procedimento.", HttpStatus.INTERNAL_SERVER_ERROR); // Retorna 500 Internal Server Error
        }
    }

    @DeleteMapping("/{id}") // DELETE /api/procedimentos/{id}
    public ResponseEntity<Void> deletarProcedimento(@PathVariable Long id) {
        try {
            procedimentoService.deletarProcedimento(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Retorna 204 No Content
        } catch (IllegalArgumentException e) {
            // Captura exceções como "Procedimento com ID X não encontrado para deleção."
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Retorna 404 Not Found
        } catch (Exception e) {
            // Captura outras exceções inesperadas
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Retorna 500 Internal Server Error
        }
    }
}