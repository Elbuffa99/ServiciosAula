package com.aulas.controller;

import com.aulas.service.ICrud;
import com.aulas.util.MapperUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class CRUDController<T, D, ID extends Serializable> {

    protected abstract ICrud<T, ID> getService();
    protected abstract MapperUtil getMapperUtil();

    @SuppressWarnings("unchecked")
    private Class<T> getEntityClass() {
        return (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @SuppressWarnings("unchecked")
    private Class<D> getDtoClass() {
        return (Class<D>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[1];
    }

    @GetMapping
    public ResponseEntity<List<D>> readAll() throws Exception {
        List<T> entities = getService().readAll();
        return ResponseEntity.ok(getMapperUtil().mapList(entities, getDtoClass()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<D> readById(@PathVariable("id") ID id) throws Exception {
        T entity = getService().readByid(id);
        return ResponseEntity.ok(getMapperUtil().map(entity, getDtoClass()));
    }

    @PostMapping
    public ResponseEntity<D> save(@Valid @RequestBody D dto) throws Exception {
        T entity = getMapperUtil().map(dto, getEntityClass());
        T saved = getService().save(entity);
        return new ResponseEntity<>(getMapperUtil().map(saved, getDtoClass()), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<D> update(@PathVariable("id") ID id,
                                    @Valid @RequestBody D dto) throws Exception {
        T entity = getMapperUtil().map(dto, getEntityClass());
        T updated = getService().update(entity, id);
        return ResponseEntity.ok(getMapperUtil().map(updated, getDtoClass()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") ID id) throws Exception {
        getService().delete(id);
        return ResponseEntity.noContent().build();
    }
}