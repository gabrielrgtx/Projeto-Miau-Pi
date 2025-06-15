package com.miaueauau.clinica_veterinaria.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "exames")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "consulta_prontuario_id", nullable = false)
    private ConsultaProntuario consultaProntuario;

    @Column(nullable = false)
    private String tipo; // Ex: "Sangue", "Urina", "Raio-X"

    private String resultado;

    private LocalDateTime dataRealizacao;

    // Outros campos relevantes para o exame
}