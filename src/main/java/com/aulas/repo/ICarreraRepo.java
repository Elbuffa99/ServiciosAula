package com.aulas.repo;
import com.aulas.model.Carrera;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ICarreraRepo extends IGenericRepo<Carrera, Integer> {

    @Query("SELECT DISTINCT c FROM Carrera c " +
            "LEFT JOIN FETCH c.tiposAula")
    List<Carrera> findAllConRelaciones();

    @Query("SELECT c FROM Carrera c " +
            "LEFT JOIN FETCH c.tiposAula " +
            "WHERE c.idCarrera = :id")
    Optional<Carrera> findByIdConRelaciones(@Param("id") Integer id);
}