package com.miaueauau.clinica_veterinaria.controller;

import com.miaueauau.clinica_veterinaria.model.Consulta;
import com.miaueauau.clinica_veterinaria.model.Paciente;
import com.miaueauau.clinica_veterinaria.service.ConsultaService;
import com.miaueauau.clinica_veterinaria.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat; // Importe este
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap; // NOVO: Importe HashMap
import java.util.List;
import java.util.Map;
// import java.util.Optional; // Já não precisamos mais do Optional neste trecho específico

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private final PacienteService pacienteService;
    private final ConsultaService consultaService;

    @Autowired
    public RelatorioController(PacienteService pacienteService, ConsultaService consultaService) {
        this.pacienteService = pacienteService;
        this.consultaService = consultaService;
    }

    @GetMapping("/faturamento")
    // CORRIGIDO: O tipo de retorno agora é Map<String, Double> para o faturamento total
    public ResponseEntity<Map<String, Double>> obterFaturamentoPorPeriodo(
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam("fim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        Double faturamentoTotal = consultaService.calcularFaturamentoPorPeriodo(inicio, fim);

        Map<String, Double> response = new HashMap<>();
        response.put("faturamentoTotal", faturamentoTotal); // Adiciona o valor ao mapa
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/pacientes")
    public ResponseEntity<List<Paciente>> listarPacientes() {
        List<Paciente> pacientes = pacienteService.listarTodosPacientes();
        return new ResponseEntity<>(pacientes, HttpStatus.OK);
    }

    @GetMapping("/pacientes/{pacienteId}/consultas")
    public ResponseEntity<List<Consulta>> historicoConsultasPorPaciente(@PathVariable Long pacienteId) {
        // CORRIGIDO: Passando o 'pacienteId' (Long) diretamente para o serviço
        // O serviço já é responsável por buscar o paciente pelo ID e lançar erro se não encontrar.
        // Assumindo que consultaService.buscarConsultasPorPaciente agora recebe Long e não Paciente
        List<Consulta> consultas = consultaService.buscarConsultasPorPaciente(pacienteId);
        return new ResponseEntity<>(consultas, HttpStatus.OK);
    }
}