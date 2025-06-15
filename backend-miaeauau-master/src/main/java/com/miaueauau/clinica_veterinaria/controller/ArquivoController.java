package com.miaueauau.clinica_veterinaria.controller;

import com.miaueauau.clinica_veterinaria.model.Arquivo;
import com.miaueauau.clinica_veterinaria.service.ArquivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/arquivos")
public class ArquivoController {

    @Autowired
    private ArquivoService arquivoService;

    @GetMapping
    public ResponseEntity<List<Arquivo>> listarTodosArquivos() {
        List<Arquivo> arquivos = arquivoService.listarTodosArquivos();
        return new ResponseEntity<>(arquivos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Arquivo> buscarArquivoPorId(@PathVariable Long id) {
        Optional<Arquivo> arquivo = arquivoService.buscarArquivoPorId(id);
        return arquivo.map(a -> new ResponseEntity<>(a, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Arquivo> salvarArquivo(@RequestBody Arquivo arquivo) {
        Arquivo novoArquivo = arquivoService.salvarArquivo(arquivo);
        return new ResponseEntity<>(novoArquivo, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Arquivo> atualizarArquivo(@PathVariable Long id, @RequestBody Arquivo arquivoAtualizado) {
        Optional<Arquivo> arquivoExistente = arquivoService.buscarArquivoPorId(id);
        if (arquivoExistente.isPresent()) {
            arquivoAtualizado.setId(id);
            Arquivo arquivoSalvo = arquivoService.salvarArquivo(arquivoAtualizado);
            return new ResponseEntity<>(arquivoSalvo, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarArquivo(@PathVariable Long id) {
        Optional<Arquivo> arquivoExistente = arquivoService.buscarArquivoPorId(id);
        if (arquivoExistente.isPresent()) {
            arquivoService.deletarArquivo(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}