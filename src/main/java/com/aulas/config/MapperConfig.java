package com.aulas.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean("defaultMapper")
    public ModelMapper defaultMapper() {
        return new ModelMapper();
    }

    // Cuando renombres un campo entre entity y DTO, agrega un bean específico aquí.
    // Ejemplo (NO descomentar, plantilla para el futuro):
    /*
    @Bean("ejemploMapper")
    public ModelMapper ejemploMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.createTypeMap(Entity.class, EntityDTO.class)
              .addMapping(Entity::getCampoA, (dest, v) -> dest.setCampoB((String) v));
        return mapper;
    }
    */
}