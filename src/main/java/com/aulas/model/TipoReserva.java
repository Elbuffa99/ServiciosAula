package com.aulas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_tipo_reserva")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class TipoReserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_reserva")
    @EqualsAndHashCode.Include
    private Integer idTipoReserva;

    @Column(name = "descripcion", length = 50)
    private String descripcion;
}
