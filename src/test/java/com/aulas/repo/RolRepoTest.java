package com.aulas.repo;

import com.aulas.model.Rol;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect.storage_engine=",
        "spring.jpa.show-sql=true"
})
@DisplayName("RolRepo - pruebas de acceso a datos (@DataJpaTest)")
class RolRepoTest {

    @Autowired
    private IRolRepo rolRepo;

    // =========================================================
    // 1. CRUD básico
    // =========================================================

    @Test
    @DisplayName("01. save: persiste un nuevo rol y le asigna ID auto-generado")
    void save_asignaId() {
        Rol guardado = rolRepo.save(new Rol(null, "ADMIN"));

        assertNotNull(guardado.getIdRol(), "El ID no debe ser nulo después de guardar");
        assertTrue(guardado.getIdRol() > 0, "El ID debe ser positivo");
    }

    @Test
    @DisplayName("02. save: preserva el nombreRol exactamente como se envió")
    void save_preservaDatos() {
        Rol guardado = rolRepo.save(new Rol(null, "DOCENTE"));

        assertEquals("DOCENTE", guardado.getNombreRol());
    }

    @Test
    @DisplayName("03. findAll: retorna todos los roles guardados")
    void findAll_devuelveTodos() {
        rolRepo.save(new Rol(null, "ADMIN"));
        rolRepo.save(new Rol(null, "DOCENTE"));
        rolRepo.save(new Rol(null, "ALUMNO"));

        List<Rol> roles = rolRepo.findAll();

        assertEquals(3, roles.size());
    }

    @Test
    @DisplayName("04. findAll: repo vacío retorna lista vacía (no null)")
    void findAll_repoVacio_listaVacia() {
        List<Rol> roles = rolRepo.findAll();

        assertNotNull(roles);
        assertTrue(roles.isEmpty());
    }

    // =========================================================
    // 2. findById / Optional
    // =========================================================

    @Test
    @DisplayName("05. findById: ID existente retorna Optional con el rol")
    void findById_existente_presente() {
        Rol guardado = rolRepo.save(new Rol(null, "ADMIN"));

        Optional<Rol> encontrado = rolRepo.findById(guardado.getIdRol());

        assertTrue(encontrado.isPresent());
        assertEquals("ADMIN", encontrado.get().getNombreRol());
    }

    @Test
    @DisplayName("06. findById: ID inexistente retorna Optional.empty()")
    void findById_inexistente_empty() {
        Optional<Rol> encontrado = rolRepo.findById(99999);

        assertFalse(encontrado.isPresent());
    }

    // =========================================================
    // 3. Actualización
    // =========================================================

    @Test
    @DisplayName("07. UPDATE: save con ID existente conserva el mismo ID")
    void update_mantieneId() {
        Rol rol = rolRepo.save(new Rol(null, "ALUMNO"));
        Integer idOriginal = rol.getIdRol();

        rol.setNombreRol("ALUMNO_PREMIUM");
        Rol actualizado = rolRepo.save(rol);

        assertEquals(idOriginal, actualizado.getIdRol(), "El ID no debe cambiar al actualizar");
    }

    @Test
    @DisplayName("08. UPDATE: save modifica efectivamente el campo en la BD")
    void update_modificaCampo() {
        Rol rol = rolRepo.save(new Rol(null, "ALUMNO"));

        rol.setNombreRol("ALUMNO_PREMIUM");
        rolRepo.save(rol);

        Optional<Rol> recargado = rolRepo.findById(rol.getIdRol());
        assertTrue(recargado.isPresent());
        assertEquals("ALUMNO_PREMIUM", recargado.get().getNombreRol());
    }

    // =========================================================
    // 4. Eliminación e identidad
    // =========================================================

    @Test
    @DisplayName("09. deleteById: el rol deja de existir tras eliminar")
    void deleteById_elimina() {
        Rol rol = rolRepo.save(new Rol(null, "TEMPORAL"));
        Integer id = rol.getIdRol();

        rolRepo.deleteById(id);

        assertFalse(rolRepo.findById(id).isPresent(),
                "El rol no debe existir después de eliminar");
    }

    @Test
    @DisplayName("10. equals/hashCode: identidad de Rol basada solo en idRol (Lombok @Include)")
    void entity_equalsBasadoEnId() {
        Rol r1 = rolRepo.save(new Rol(null, "ADMIN"));
        Rol r2 = new Rol(r1.getIdRol(), "NOMBRE_DIFERENTE");

        // Por @EqualsAndHashCode(onlyExplicitlyIncluded = true) + @Include solo en idRol
        assertEquals(r1, r2, "Dos Rol con mismo idRol deben ser iguales");
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    // =========================================================
    // 5. count y existsById
    // =========================================================

    @Test
    @DisplayName("11. count: repo vacío retorna 0")
    void count_repoVacio_cero() {
        assertEquals(0, rolRepo.count());
    }

    @Test
    @DisplayName("12. count: refleja correctamente la cantidad insertada")
    void count_reflejaCantidad() {
        rolRepo.save(new Rol(null, "ADMIN"));
        rolRepo.save(new Rol(null, "DOCENTE"));

        assertEquals(2, rolRepo.count());
    }

    @Test
    @DisplayName("13. existsById: true para un ID que sí existe")
    void existsById_existente_true() {
        Rol rol = rolRepo.save(new Rol(null, "ADMIN"));

        assertTrue(rolRepo.existsById(rol.getIdRol()));
    }

    @Test
    @DisplayName("14. existsById: false para un ID inexistente")
    void existsById_inexistente_false() {
        assertFalse(rolRepo.existsById(99999));
    }

    // =========================================================
    // 6. Operaciones por lote
    // =========================================================

    @Test
    @DisplayName("15. saveAll: persiste todos los roles de la lista con IDs asignados")
    void saveAll_persisteLista() {
        List<Rol> lote = List.of(
                new Rol(null, "ADMIN"),
                new Rol(null, "DOCENTE"),
                new Rol(null, "ALUMNO")
        );

        List<Rol> guardados = rolRepo.saveAll(lote);

        assertEquals(3, guardados.size());
        assertTrue(guardados.stream().allMatch(r -> r.getIdRol() != null),
                "Todos los roles deben tener un ID asignado");
    }

    @Test
    @DisplayName("16. findAllById: retorna solo los roles cuyo ID está en la lista")
    void findAllById_filtraIds() {
        Rol r1 = rolRepo.save(new Rol(null, "ADMIN"));
        Rol r2 = rolRepo.save(new Rol(null, "DOCENTE"));
        rolRepo.save(new Rol(null, "ALUMNO")); // este no se pide

        List<Rol> resultado = rolRepo.findAllById(List.of(r1.getIdRol(), r2.getIdRol()));

        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("17. deleteAll: vacía completamente el repositorio")
    void deleteAll_vaciaRepo() {
        rolRepo.save(new Rol(null, "ADMIN"));
        rolRepo.save(new Rol(null, "DOCENTE"));

        rolRepo.deleteAll();

        assertEquals(0, rolRepo.count());
    }

    // =========================================================
    // 7. Constraints de columna
    // =========================================================

    @Test
    @DisplayName("18. CONSTRAINT: nombreRol null lanza DataIntegrityViolationException")
    void constraint_nombreNull_lanzaExcepcion() {
        // @Column(name = "nombre_rol", length = 50, nullable = false)
        // El flush fuerza el INSERT a llegar a la BD y dispara la restricción.
        Rol invalido = new Rol(null, null);

        assertThrows(DataIntegrityViolationException.class,
                () -> rolRepo.saveAndFlush(invalido),
                "Persistir un nombreRol null debe violar la restricción NOT NULL");
    }

    @Test
    @DisplayName("19. CONSTRAINT: nombreRol cadena vacía SÍ es aceptado (nullable=false ≠ @NotBlank)")
    void constraint_nombreVacio_seAcepta() {
        // nullable=false impide NULL, pero "" (cadena vacía) no es null,
        // así que se persiste sin problema. Esto demuestra por qué se usa
        // @NotBlank de Bean Validation cuando se quiere bloquear "".
        Rol rol = new Rol(null, "");

        assertDoesNotThrow(() -> rolRepo.saveAndFlush(rol));
    }

    @Test
    @DisplayName("20. CONSTRAINT: dos roles con mismo nombre son permitidos (no es UNIQUE)")
    void multipleNombresIguales_permitido() {
        // nombreRol no tiene unique = true, así que duplicados son válidos.
        rolRepo.save(new Rol(null, "ADMIN"));
        rolRepo.save(new Rol(null, "ADMIN"));

        assertEquals(2, rolRepo.count());
    }
}