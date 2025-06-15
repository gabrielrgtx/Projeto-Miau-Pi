package com.miaueauau.clinica_veterinaria.controller;

import com.miaueauau.clinica_veterinaria.model.Prescricao;
import com.miaueauau.clinica_veterinaria.service.PrescricaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/prescricoes")
public class PrescricaoController {

    @Autowired
    private PrescricaoService prescricaoService;

    @GetMapping
    public ResponseEntity<List<Prescricao>> listarTodasPrescricoes() {
        List<Prescricao> prescricoes = prescricaoService.listarTodasPrescricoes();
        return new ResponseEntity<>(prescricoes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Prescricao> buscarPrescricaoPorId(@PathVariable Long id) {
        Optional<Prescricao> prescricao = prescricaoService.buscarPrescricaoPorId(id);
        return prescricao.map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Prescricao> salvarPrescricao(@RequestBody Prescricao prescricao) {
        Prescricao novaPrescricao = prescricaoService.salvarPrescricao(prescricao);
        return new ResponseEntity<>(novaPrescricao, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Prescricao> atualizarPrescricao(@PathVariable Long id, @RequestBody Prescricao prescricaoAtualizada) {
        Optional<Prescricao> prescricaoExistente = prescricaoService.buscarPrescricaoPorId(id);
        if (prescricaoExistente.isPresent()) {
            prescricaoAtualizada.setId(id);
            Prescricao prescricaoSalva = prescricaoService.salvarPrescricao(prescricaoAtualizada);
            return new ResponseEntity<>(prescricaoSalva, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPrescricao(@PathVariable Long id) {
        Optional<Prescricao> prescricaoExistente = prescricaoService.buscarPrescricaoPorId(id);
        if (prescricaoExistente.isPresent()) {
            prescricaoService.deletarPrescricao(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}