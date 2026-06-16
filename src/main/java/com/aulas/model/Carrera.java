package com.aulas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "tbl_carrera")
public class Carrera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrera")
    @EqualsAndHashCode.Include
    private Integer idCarrera;

    @Column(name = "nombre_carrera", length = 200, nullable = false)
    private String nombreCarrera;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tbl_carrera_tipo_aula",
            joinColumns = @JoinColumn(name = "id_carrera"),
            inverseJoinColumns = @JoinColumn(name = "id_tipo_aula")
    )
    private Set<TipoAula> tiposAula = new HashSet<>();
}
