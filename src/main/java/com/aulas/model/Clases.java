package com.aulas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "tbl_clases")
public class Clases {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_clases")
    @EqualsAndHashCode.Include
    private Integer idClases;

    @Column(name = "nombre_curso", length = 100, nullable = false)
    private String nombreCurso;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", length = 15, nullable = false)
    private DiaSemana diaSemana;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aula", nullable = false)
    private Aula aula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_horario", nullable = false,foreignKey = @ForeignKey(name = "FK_Clases_Horario"))
    private Horario horario;
}
