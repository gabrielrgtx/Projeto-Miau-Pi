package com.miaueauau.clinica_veterinaria.controller;

import com.miaueauau.clinica_veterinaria.model.ConsultaProntuario;
import com.miaueauau.clinica_veterinaria.service.ConsultaProntuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/consultas-prontuarios")
public class ConsultaProntuarioController {

    @Autowired
    private ConsultaProntuarioService consultaProntuarioService;

    @GetMapping
    public ResponseEntity<List<ConsultaProntuario>> listarTodasConsultaProntuarios() {
        List<ConsultaProntuario> consultaProntuarios = consultaProntuarioService.listarTodasConsultaProntuarios();
        return new ResponseEntity<>(consultaProntuarios, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaProntuario> buscarConsultaProntuarioPorId(@PathVariable Long id) {
        Optional<ConsultaProntuario> consultaProntuario = consultaProntuarioService.buscarConsultaProntuarioPorId(id);
        return consultaProntuario.map(cp -> new ResponseEntity<>(cp, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<ConsultaProntuario> salvarConsultaProntuario(@RequestBody ConsultaProntuario consultaProntuario) {
        ConsultaProntuario novoConsultaProntuario = consultaProntuarioService.salvarConsultaProntuario(consultaProntuario);
        return new ResponseEntity<>(novoConsultaProntuario, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultaProntuario> atualizarConsultaProntuario(@PathVariable Long id, @RequestBody ConsultaProntuario consultaProntuarioAtualizado) {
        Optional<ConsultaProntuario> consultaProntuarioExistente = consultaProntuarioService.buscarConsultaProntuarioPorId(id);
        if (consultaProntuarioExistente.isPresent()) {
            consultaProntuarioAtualizado.setId(id);
            ConsultaProntuario consultaProntuarioSalvo = consultaProntuarioService.salvarConsultaProntuario(consultaProntuarioAtualizado);
            return new ResponseEntity<>(consultaProntuarioSalvo, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarConsultaProntuario(@PathVariable Long id) {
        Optional<ConsultaProntuario> consultaProntuarioExistente = consultaProntuarioService.buscarConsultaProntuarioPorId(id);
        if (consultaProntuarioExistente.isPresent()) {
            consultaProntuarioService.deletarConsultaProntuario(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}