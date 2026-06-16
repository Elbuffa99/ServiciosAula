package com.aulas.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "tbl_reserva_integrante")
public class ReservaIntegrante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva_integrante")
    @EqualsAndHashCode.Include
    private Integer idReservaIntegrante;

    @Column(name = "codigo_alumno", length = 20, nullable = false)
    private String codigoAlumno;

    @Column(name = "nombre_alumno", length = 100, nullable = false)
    private String nombreAlumno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reserva", nullable = false,foreignKey = @ForeignKey(name = "FK_RESERVA_INTEGRANTE_RESERVA"))
    private Reserva reserva;
}
