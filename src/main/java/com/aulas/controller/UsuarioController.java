package com.aulas.controller;

import com.aulas.dto.UsuarioDTO;
import com.aulas.exception.ModelNotFoundException;
import com.aulas.model.Usuario;
import com.aulas.service.IUsuarioService;
import com.aulas.util.MapperUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final IUsuarioService service;
    private final MapperUtil mapperUtil;

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<UsuarioDTO>> readAll() throws Exception {

        return ResponseEntity.ok(mapperUtil.mapList(service.readAll(), UsuarioDTO.class));
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<UsuarioDTO> readById(@PathVariable("id") Integer id) throws Exception {
        Usuario obj = service.readByid(id);
        return ResponseEntity.ok(mapperUtil.map(obj, UsuarioDTO.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> update(@PathVariable("id") Integer id,
                                             @Valid @RequestBody UsuarioDTO dto) throws Exception {
        Usuario entity = mapperUtil.map(dto, Usuario.class);
        Usuario updated = service.update(entity, id);
        return ResponseEntity.ok(mapperUtil.map(updated, UsuarioDTO.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) throws Exception {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}