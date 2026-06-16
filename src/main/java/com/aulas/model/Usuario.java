package com.aulas.model;
import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tbl_usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    @EqualsAndHashCode.Include
    private Integer idUsuario;

    @Column(name = "nombre_usuario", length = 100, nullable = false)
    private String nombreUsuario;

    @Column(name = "apellido_usuario", length = 100, nullable = false)
    private String apellidoUsuario;

    @Column(name = "correo_institucional", length = 60, nullable = false, unique = true)
    private String correoInstitucional;

    @Column(name = "codigo_institucional", length = 20, nullable = false, unique = true)
    private String codigoInstitucional;

    @Column(name = "contrasena", length = 200, nullable = false)
    private String contrasena; // BCrypt hash, NO el password plano

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sede", nullable = false)
    private Sede sede;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrera")
    private Carrera carrera;

    // Self-reference: admin que valida/aprueba a este usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_admin_sede_check",foreignKey = @ForeignKey(name = "FK_User_Admin_Validate"))
    private Usuario adminSedeCheck;
}
