package com.miaueauau.clinica_veterinaria.controller;

import com.miaueauau.clinica_veterinaria.model.Prontuario;
import com.miaueauau.clinica_veterinaria.service.ProntuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/prontuarios")
public class ProntuarioController {

    @Autowired
    private ProntuarioService prontuarioService;

    @GetMapping
    public ResponseEntity<List<Prontuario>> listarTodosProntuarios() {
        List<Prontuario> prontuarios = prontuarioService.listarTodosProntuarios();
        return new ResponseEntity<>(prontuarios, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Prontuario> buscarProntuarioPorId(@PathVariable Long id) {
        Optional<Prontuario> prontuario = prontuarioService.buscarProntuarioPorId(id);
        return prontuario.map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Prontuario> salvarProntuario(@RequestBody Prontuario prontuario) {
        Prontuario novoProntuario = prontuarioService.salvarProntuario(prontuario);
        return new ResponseEntity<>(novoProntuario, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Prontuario> atualizarProntuario(@PathVariable Long id, @RequestBody Prontuario prontuarioAtualizado) {
        Optional<Prontuario> prontuarioExistente = prontuarioService.buscarProntuarioPorId(id);
        if (prontuarioExistente.isPresent()) {
            prontuarioAtualizado.setId(id);
            Prontuario prontuarioSalvo = prontuarioService.salvarProntuario(prontuarioAtualizado);
            return new ResponseEntity<>(prontuarioSalvo, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProntuario(@PathVariable Long id) {
        Optional<Prontuario> prontuarioExistente = prontuarioService.buscarProntuarioPorId(id);
        if (prontuarioExistente.isPresent()) {
            prontuarioService.deletarProntuario(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}