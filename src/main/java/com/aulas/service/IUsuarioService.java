package com.aulas.service;

import com.aulas.model.Usuario;

import java.util.Optional;

public interface IUsuarioService extends ICrud<Usuario, Integer>{


    Optional<Usuario> findByCorreoInstitucional(String correoInstitucional);


    Usuario registrar(Usuario usuario) throws Exception;

    Optional<Usuario> findByCorreoConRol(String correoInstitucional);


}
