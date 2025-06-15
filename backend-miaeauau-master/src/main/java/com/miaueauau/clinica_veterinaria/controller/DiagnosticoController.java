package com.miaueauau.clinica_veterinaria.controller;

import com.miaueauau.clinica_veterinaria.model.Diagnostico;
import com.miaueauau.clinica_veterinaria.service.DiagnosticoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/diagnosticos")
public class DiagnosticoController {

    @Autowired
    private DiagnosticoService diagnosticoService;

    @GetMapping
    public ResponseEntity<List<Diagnostico>> listarTodosDiagnosticos() {
        List<Diagnostico> diagnosticos = diagnosticoService.listarTodosDiagnosticos();
        return new ResponseEntity<>(diagnosticos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Diagnostico> buscarDiagnosticoPorId(@PathVariable Long id) {
        Optional<Diagnostico> diagnostico = diagnosticoService.buscarDiagnosticoPorId(id);
        return diagnostico.map(d -> new ResponseEntity<>(d, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Diagnostico> salvarDiagnostico(@RequestBody Diagnostico diagnostico) {
        Diagnostico novoDiagnostico = diagnosticoService.salvarDiagnostico(diagnostico);
        return new ResponseEntity<>(novoDiagnostico, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Diagnostico> atualizarDiagnostico(@PathVariable Long id, @RequestBody Diagnostico diagnosticoAtualizado) {
        Optional<Diagnostico> diagnosticoExistente = diagnosticoService.buscarDiagnosticoPorId(id);
        if (diagnosticoExistente.isPresent()) {
            diagnosticoAtualizado.setId(id);
            Diagnostico diagnosticoSalvo = diagnosticoService.salvarDiagnostico(diagnosticoAtualizado);
            return new ResponseEntity<>(diagnosticoSalvo, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarDiagnostico(@PathVariable Long id) {
        Optional<Diagnostico> diagnosticoExistente = diagnosticoService.buscarDiagnosticoPorId(id);
        if (diagnosticoExistente.isPresent()) {
            diagnosticoService.deletarDiagnostico(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}