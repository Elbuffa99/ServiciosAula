package com.aulas.service.impl;

import com.aulas.exception.BusinessException;
import com.aulas.exception.ModelNotFoundException;
import com.aulas.model.Usuario;
import com.aulas.repo.IGenericRepo;
import com.aulas.repo.IUsuarioRepo;
import com.aulas.service.IUsuarioService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl extends CRUDImpl<Usuario, Integer> implements IUsuarioService {

    private final IUsuarioRepo repo;
    private final PasswordEncoder passwordEncoder;

    @Override
    protected IGenericRepo<Usuario, Integer> getRepo() {
        return repo;
    }

    @Override
    public Optional<Usuario> findByCorreoInstitucional(String correoInstitucional) {
        return repo.findByCorreoInstitucional(correoInstitucional);
    }

    @Override
    public Usuario registrar(Usuario usuario) throws Exception {
        if (repo.existsByCorreoInstitucional(usuario.getCorreoInstitucional())) {
            throw new BusinessException("Ya existe un usuario con ese correo institucional");
        }
        if (repo.existsByCodigoInstitucional(usuario.getCodigoInstitucional())) {
            throw new BusinessException("Ya existe un usuario con ese código institucional");
        }
        // Ciframos el password ANTES de persistir
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        return repo.save(usuario);
    }

    @Override
    public Optional<Usuario> findByCorreoConRol(String correoInstitucional) {
        return repo.findByCorreoConRol(correoInstitucional);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> readAll() throws Exception {
        return repo.findAllConRelaciones();
    }

}