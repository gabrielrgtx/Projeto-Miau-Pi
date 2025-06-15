package com.miaueauau.clinica_veterinaria.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "arquivos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Arquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "consulta_prontuario_id") // Pode ser nulo se o arquivo for geral do prontuário
    private ConsultaProntuario consultaProntuario;

    @ManyToOne
    @JoinColumn(name = "prontuario_id") // Pode ser nulo se o arquivo for específico da consulta
    private Prontuario prontuario;

    @Column(nullable = false)
    private String nomeArquivo;

    @Column(nullable = false)
    private String caminhoArquivo;

    @Column(nullable = false)
    private String tipoArquivo;

    // Outros campos relevantes para o arquivo
}