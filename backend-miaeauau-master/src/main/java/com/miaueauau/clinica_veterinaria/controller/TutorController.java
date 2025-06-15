package com.miaueauau.clinica_veterinaria.controller;

import com.miaueauau.clinica_veterinaria.model.Tutor;
import com.miaueauau.clinica_veterinaria.service.TutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tutores")
public class TutorController {

    @Autowired
    private TutorService tutorService;

    @GetMapping
    public ResponseEntity<List<Tutor>> listarTodosTutores() {
        List<Tutor> tutores = tutorService.listarTodosTutores();
        return new ResponseEntity<>(tutores, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tutor> buscarTutorPorId(@PathVariable Long id) {
        Optional<Tutor> tutor = tutorService.buscarTutorPorId(id);
        return tutor.map(t -> new ResponseEntity<>(t, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Tutor> salvarTutor(@RequestBody Tutor tutor) {
        try {
            Tutor novoTutor = tutorService.salvarTutor(tutor);
            return new ResponseEntity<>(novoTutor, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tutor> atualizarTutor(@PathVariable Long id, @RequestBody Tutor tutorAtualizado) {
        try {
            Tutor tutorSalvo = tutorService.atualizarTutor(id, tutorAtualizado);
            return new ResponseEntity<>(tutorSalvo, HttpStatus.OK);
        } catch (IllegalArgumentException e) { // <-- MUDOU PARA CIMA: CAPTURA PRIMEIRO AS VALIDAÇÕES ESPECÍFICAS
            // Captura exceções de validação, como CPF/e-mail já existente para outro tutor
            return new ResponseEntity<>(HttpStatus.CONFLICT); // Retorna 409 Conflict
        } catch (RuntimeException e) { // <-- MUDOU PARA BAIXO: CAPTURA EXCEÇÕES GENÉRICAS DE RUNTIME
            // Captura exceções como "Tutor não encontrado"
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Retorna 404 Not Found
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTutor(@PathVariable Long id) {
        try {
            tutorService.deletarTutor(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}