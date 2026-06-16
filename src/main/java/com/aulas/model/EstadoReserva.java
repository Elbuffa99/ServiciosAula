package com.aulas.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_estado_reserva")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EstadoReserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado_reserva")
    @EqualsAndHashCode.Include
    private Integer idEstadoReserva;

    @Column(name = "descripcion", length = 30)
    private String descripcion;
}
