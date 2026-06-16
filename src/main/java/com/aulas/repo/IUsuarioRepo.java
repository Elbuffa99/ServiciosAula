package com.aulas.repo;

import com.aulas.model.Usuario;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IUsuarioRepo extends IGenericRepo<Usuario, Integer> {

    Optional<Usuario> findByCorreoInstitucional(String correoInstitucional);

    boolean existsByCorreoInstitucional(String correoInstitucional);

    boolean existsByCodigoInstitucional(String codigoInstitucional);

    @Query("SELECT u FROM Usuario u JOIN FETCH u.rol WHERE u.correoInstitucional = :correo")
    Optional<Usuario> findByCorreoConRol(String correo);

    @Query("SELECT u FROM Usuario u " +
            "LEFT JOIN FETCH u.rol " +
            "LEFT JOIN FETCH u.sede " +
            "LEFT JOIN FETCH u.carrera")
    List<Usuario> findAllConRelaciones();

}