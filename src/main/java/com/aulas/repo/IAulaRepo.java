package com.aulas.repo;
import com.aulas.model.Aula;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IAulaRepo extends IGenericRepo<Aula, Integer> {

    @Query("SELECT a FROM Aula a " +
            "LEFT JOIN FETCH a.sede " +
            "LEFT JOIN FETCH a.tipoAula")
    List<Aula> findAllConRelaciones();

    @Query("SELECT a FROM Aula a " +
            "LEFT JOIN FETCH a.sede " +
            "LEFT JOIN FETCH a.tipoAula " +
            "WHERE a.idAula = :id")
    Optional<Aula> findByIdConRelaciones(@Param("id") Integer id);
}