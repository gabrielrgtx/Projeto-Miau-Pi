package com.miaueauau.clinica_veterinaria.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
// REMOVER: JsonIdentityInfo, ObjectIdGenerators, JsonIgnoreProperties imports

@Entity
@Table(name = "consultas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER) // MUDANÇA AQUI: EAGER para Paciente
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.EAGER) // MUDANÇA AQUI: EAGER para Veterinario
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Veterinario veterinario;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    private String motivo;

    private String diagnostico;

    private String tratamento;

    private boolean confirmada;

    @Column(nullable = false)
    private String tipoAtendimento;

    @ManyToMany(fetch = FetchType.LAZY) // Manter LAZY
    @JoinTable(
            name = "consulta_procedimento",
            joinColumns = @JoinColumn(name = "consulta_id"),
            inverseJoinColumns = @JoinColumn(name = "procedimento_id")
    )
    private List<Procedimento> procedimentos;
}