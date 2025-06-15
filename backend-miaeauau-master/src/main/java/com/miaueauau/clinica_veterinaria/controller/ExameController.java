package com.miaueauau.clinica_veterinaria.controller;

import com.miaueauau.clinica_veterinaria.model.Exame;
import com.miaueauau.clinica_veterinaria.repository.ExameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/exames")
public class ExameController {

    @Autowired
    private ExameRepository exameRepository;

    @PostMapping("/consultar")
    public ResponseEntity<List<Exame>> consultarExames(@RequestParam(required = false) Long id,
                                                       @RequestParam(required = false) String tipo,
                                                       @RequestParam(required = false) LocalDateTime dataRealizacao) {
        if (id != null) {
            Optional<Exame> exame = exameRepository.findById(id);
            return exame.map(exame1 -> ResponseEntity.ok(List.of(exame1)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } else if (tipo != null) {
            List<Exame> exames = exameRepository.findByTipo(tipo);
            return ResponseEntity.ok(exames);
        } else if (dataRealizacao != null) {
            List<Exame> exames = exameRepository.findByDataRealizacao(dataRealizacao);
            return ResponseEntity.ok(exames);
        } else {
            return ResponseEntity.badRequest().build(); // Nenhum critério fornecido
        }
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<Exame> cadastrarExame(@RequestBody Exame novoExame) {
        Exame exameSalvo = exameRepository.save(novoExame);
        return new ResponseEntity<>(exameSalvo, org.springframework.http.HttpStatus.CREATED);
    }
}