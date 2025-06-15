package com.miaueauau.clinica_veterinaria.controller;

import com.miaueauau.clinica_veterinaria.model.Veterinario;
import com.miaueauau.clinica_veterinaria.service.VeterinarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import jakarta.validation.Valid; // Importe este
import org.springframework.validation.BindingResult; // Importe este
import java.util.HashMap; // Para Map
import java.util.Map; // Para Map

@RestController // Indica que é um controlador REST
@RequestMapping("/api/veterinarios") // Mapeia as requisições para /api/veterinarios
public class VeterinarioController {

    @Autowired
    private VeterinarioService veterinarioService;

    // Construtor (opcional, Spring pode injetar via @Autowired)
    // public VeterinarioController(VeterinarioService veterinarioService) {
    //     this.veterinarioService = veterinarioService;
    // }

    @GetMapping // Mapeia GET /api/veterinarios
    public ResponseEntity<List<Veterinario>> listarTodosVeterinarios() {
        List<Veterinario> veterinarios = veterinarioService.listarTodosVeterinarios();
        return new ResponseEntity<>(veterinarios, HttpStatus.OK);
    }

    @GetMapping("/{id}") // Mapeia GET /api/veterinarios/{id}
    public ResponseEntity<Veterinario> buscarVeterinarioPorId(@PathVariable Long id) {
        Optional<Veterinario> veterinario = veterinarioService.buscarVeterinarioPorId(id);
        return veterinario.map(v -> new ResponseEntity<>(v, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping // Mapeia POST /api/veterinarios
    public ResponseEntity<?> salvarVeterinario(@Valid @RequestBody Veterinario veterinario, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        try {
            Veterinario novoVeterinario = veterinarioService.salvarVeterinario(veterinario);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Veterinário adicionado com sucesso!");
            response.put("id", novoVeterinario.getId().toString()); // Retorna o ID
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Erro ao salvar veterinário: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}") // Mapeia PUT /api/veterinarios/{id}
    public ResponseEntity<?> atualizarVeterinario(@PathVariable Long id, @Valid @RequestBody Veterinario veterinarioAtualizado, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        try {
            Veterinario veterinarioSalvo = veterinarioService.atualizarVeterinario(id, veterinarioAtualizado);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Veterinário atualizado com sucesso!");
            response.put("id", veterinarioSalvo.getId().toString());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Erro ao atualizar veterinário: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}") // Mapeia DELETE /api/veterinarios/{id}
    public ResponseEntity<Void> deletarVeterinario(@PathVariable Long id) {
        try {
            veterinarioService.deletarVeterinario(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content para deleção bem-sucedida
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 se não encontrado
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}