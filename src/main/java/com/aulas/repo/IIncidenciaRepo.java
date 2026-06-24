package com.aulas.repo;
import com.aulas.model.Incidencia;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IIncidenciaRepo extends IGenericRepo<Incidencia, Integer> {

    @Query(value = "SELECT i FROM Incidencia i " +
            "LEFT JOIN FETCH i.aula a " +
            "LEFT JOIN FETCH a.sede " +
            "LEFT JOIN FETCH a.tipoAula " +
            "LEFT JOIN FETCH i.usuario u " +
            "LEFT JOIN FETCH u.rol " +
            "LEFT JOIN FETCH u.sede " +
            "LEFT JOIN FETCH u.carrera")
    List<Incidencia> findAllConRelaciones();

    @Query("SELECT i FROM Incidencia i " +
            "LEFT JOIN FETCH i.aula a " +
            "LEFT JOIN FETCH a.sede " +
            "LEFT JOIN FETCH a.tipoAula " +
            "LEFT JOIN FETCH i.usuario u " +
            "LEFT JOIN FETCH u.rol " +
            "LEFT JOIN FETCH u.sede " +
            "LEFT JOIN FETCH u.carrera " +
            "WHERE i.idIncidencia = :id")
    Optional<Incidencia> findByIdConRelaciones(@Param("id") Integer id);
}