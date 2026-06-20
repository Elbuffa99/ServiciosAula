package com.aulas.repo;

import com.aulas.model.Aula;
import com.aulas.model.Clases;
import com.aulas.model.DiaSemana;
import com.aulas.model.Horario;
import com.aulas.model.Sede;
import com.aulas.model.TipoAula;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Se cubren 20 escenarios en 8 bloques:
 *   1. CRUD básico (4)
 *   2. findById / Optional (2)
 *   3. Actualización (2)
 *   4. Eliminación e identidad (2)
 *   5. Métodos heredados de JpaRepository (3)
 *   6. Operaciones por lote (2)
 *   7. Constraints y enum (3)
 *   8. Query derivada del repo (2)
 */
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
@DisplayName("ClasesRepo - pruebas de acceso a datos (@DataJpaTest)")
class ClasesRepoTest {

    @Autowired
    private IClasesRepo clasesRepo;

    @Autowired
    private TestEntityManager em;

    private Aula aula;
    private Horario horario;
    private Sede sede;

    @BeforeEach
    void setUp() {
        TipoAula tipoAula = em.persistAndFlush(new TipoAula(null, "Laboratorio"));
        sede = em.persistAndFlush(new Sede(null, "Sede San Isidro", "Av. Javier Prado 123"));
        aula = em.persistAndFlush(new Aula(null, "A-101", 30, tipoAula, sede));
        horario = em.persistAndFlush(new Horario(null, LocalTime.of(8, 0), LocalTime.of(10, 0), "MAÑANA"));
    }

    private Clases nuevaClase(String nombreCurso, DiaSemana dia) {
        return new Clases(null, nombreCurso, dia, aula, horario);
    }

    // =========================================================
    // 1. CRUD básico
    // =========================================================

    @Test
    @DisplayName("01. save: persiste una clase nueva y le asigna ID")
    void save_asignaId() {
        Clases guardada = clasesRepo.save(nuevaClase("Programación Java", DiaSemana.LUNES));

        assertNotNull(guardada.getIdClases());
        assertTrue(guardada.getIdClases() > 0);
    }

    @Test
    @DisplayName("02. save: preserva nombreCurso y diaSemana correctamente")
    void save_preservaDatos() {
        Clases guardada = clasesRepo.save(nuevaClase("Estructura de Datos", DiaSemana.MIERCOLES));

        assertEquals("Estructura de Datos", guardada.getNombreCurso());
        assertEquals(DiaSemana.MIERCOLES, guardada.getDiaSemana());
    }

    @Test
    @DisplayName("03. findAll: retorna todas las clases guardadas")
    void findAll_devuelveTodos() {
        clasesRepo.save(nuevaClase("Java", DiaSemana.LUNES));
        clasesRepo.save(nuevaClase("Python", DiaSemana.MARTES));
        clasesRepo.save(nuevaClase("Bases de Datos", DiaSemana.MIERCOLES));

        List<Clases> clases = clasesRepo.findAll();

        assertEquals(3, clases.size());
    }

    @Test
    @DisplayName("04. findAll: repo vacío retorna lista vacía")
    void findAll_repoVacio_listaVacia() {
        List<Clases> clases = clasesRepo.findAll();

        assertNotNull(clases);
        assertTrue(clases.isEmpty());
    }

    // =========================================================
    // 2. findById / Optional
    // =========================================================

    @Test
    @DisplayName("05. findById: ID existente retorna Optional con la clase")
    void findById_existente_presente() {
        Clases guardada = clasesRepo.save(nuevaClase("Java", DiaSemana.LUNES));

        Optional<Clases> encontrada = clasesRepo.findById(guardada.getIdClases());

        assertTrue(encontrada.isPresent());
        assertEquals("Java", encontrada.get().getNombreCurso());
    }

    @Test
    @DisplayName("06. findById: ID inexistente retorna Optional.empty()")
    void findById_inexistente_empty() {
        Optional<Clases> encontrada = clasesRepo.findById(99999);

        assertFalse(encontrada.isPresent());
    }

    // =========================================================
    // 3. Actualización
    // =========================================================

    @Test
    @DisplayName("07. UPDATE: save con ID existente conserva el mismo ID")
    void update_mantieneId() {
        Clases clase = clasesRepo.save(nuevaClase("Java", DiaSemana.LUNES));
        Integer idOriginal = clase.getIdClases();

        clase.setDiaSemana(DiaSemana.VIERNES);
        Clases actualizada = clasesRepo.save(clase);

        assertEquals(idOriginal, actualizada.getIdClases());
    }

    @Test
    @DisplayName("08. UPDATE: save modifica el día de la semana en la BD")
    void update_cambiaDiaSemana() {
        Clases clase = clasesRepo.save(nuevaClase("Java", DiaSemana.LUNES));

        clase.setDiaSemana(DiaSemana.VIERNES);
        clasesRepo.save(clase);

        Optional<Clases> recargada = clasesRepo.findById(clase.getIdClases());
        assertTrue(recargada.isPresent());
        assertEquals(DiaSemana.VIERNES, recargada.get().getDiaSemana());
    }

    // =========================================================
    // 4. Eliminación e identidad
    // =========================================================

    @Test
    @DisplayName("09. deleteById: la clase deja de existir tras eliminar")
    void deleteById_elimina() {
        Clases clase = clasesRepo.save(nuevaClase("Java", DiaSemana.LUNES));
        Integer id = clase.getIdClases();

        clasesRepo.deleteById(id);

        assertFalse(clasesRepo.findById(id).isPresent());
    }

    @Test
    @DisplayName("10. equals/hashCode: identidad de Clases basada en idClases (Lombok)")
    void entity_equalsBasadoEnId() {
        Clases c1 = clasesRepo.save(nuevaClase("Java", DiaSemana.LUNES));
        Clases c2 = new Clases(c1.getIdClases(), "OTRO_CURSO", DiaSemana.DOMINGO, aula, horario);

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    // =========================================================
    // 5. Métodos heredados de JpaRepository
    // =========================================================

    @Test
    @DisplayName("11. count: refleja correctamente la cantidad insertada")
    void count_reflejaCantidad() {
        clasesRepo.save(nuevaClase("Java", DiaSemana.LUNES));
        clasesRepo.save(nuevaClase("Python", DiaSemana.MARTES));

        assertEquals(2, clasesRepo.count());
    }

    @Test
    @DisplayName("12. existsById: true para un ID que sí existe")
    void existsById_existente_true() {
        Clases clase = clasesRepo.save(nuevaClase("Java", DiaSemana.LUNES));

        assertTrue(clasesRepo.existsById(clase.getIdClases()));
    }

    @Test
    @DisplayName("13. existsById: false para un ID inexistente")
    void existsById_inexistente_false() {
        assertFalse(clasesRepo.existsById(99999));
    }

    // =========================================================
    // 6. Operaciones por lote
    // =========================================================

    @Test
    @DisplayName("14. saveAll: persiste todas las clases con IDs asignados")
    void saveAll_persisteLista() {
        List<Clases> lote = List.of(
                nuevaClase("Java", DiaSemana.LUNES),
                nuevaClase("Python", DiaSemana.MARTES),
                nuevaClase("SQL", DiaSemana.MIERCOLES)
        );

        List<Clases> guardadas = clasesRepo.saveAll(lote);

        assertEquals(3, guardadas.size());
        assertTrue(guardadas.stream().allMatch(c -> c.getIdClases() != null));
    }

    @Test
    @DisplayName("15. deleteAll: vacía completamente el repositorio")
    void deleteAll_vaciaRepo() {
        clasesRepo.save(nuevaClase("Java", DiaSemana.LUNES));
        clasesRepo.save(nuevaClase("Python", DiaSemana.MARTES));

        clasesRepo.deleteAll();

        assertEquals(0, clasesRepo.count());
    }

    // =========================================================
    // 7. Constraints y enum
    // =========================================================

    @Test
    @DisplayName("16. ENUM: diaSemana se persiste como String (por @Enumerated(EnumType.STRING))")
    void enum_diaSemana_persisteComoString() {
        Clases guardada = clasesRepo.save(nuevaClase("Java", DiaSemana.JUEVES));
        em.flush();
        em.clear(); // limpia caché L1, fuerza lectura desde BD

        Clases recargada = em.find(Clases.class, guardada.getIdClases());
        assertEquals(DiaSemana.JUEVES, recargada.getDiaSemana());
    }

    @Test
    @DisplayName("17. CONSTRAINT: nombreCurso null lanza DataIntegrityViolationException")
    void constraint_nombreCursoNull_lanzaExcepcion() {
        Clases invalida = new Clases(null, null, DiaSemana.LUNES, aula, horario);

        assertThrows(DataIntegrityViolationException.class,
                () -> clasesRepo.saveAndFlush(invalida));
    }

    @Test
    @DisplayName("18. CONSTRAINT: aula null lanza DataIntegrityViolationException (FK obligatoria)")
    void constraint_aulaNull_lanzaExcepcion() {
        Clases invalida = new Clases(null, "Java", DiaSemana.LUNES, null, horario);

        assertThrows(DataIntegrityViolationException.class,
                () -> clasesRepo.saveAndFlush(invalida));
    }

    // =========================================================
    // 8. Query derivada del repositorio
    // =========================================================

    @Test
    @DisplayName("19. QUERY: findByDiaSemanaAndHorarioAndSede filtra correctamente las clases del slot")
    void customQuery_filtraPorDiaHorarioYSede() {
        // 2 clases que SÍ deben aparecer (LUNES + mismo horario + misma sede)
        clasesRepo.save(nuevaClase("Java", DiaSemana.LUNES));
        clasesRepo.save(nuevaClase("Python", DiaSemana.LUNES));
        // 1 clase con día distinto: no debe aparecer
        clasesRepo.save(nuevaClase("SQL", DiaSemana.MARTES));

        List<Clases> resultado = clasesRepo
                .findByDiaSemanaAndHorario_IdHorarioAndAula_Sede_IdSede(
                        DiaSemana.LUNES,
                        horario.getIdHorario(),
                        sede.getIdSede()
                );

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(c -> c.getDiaSemana() == DiaSemana.LUNES));
    }

    @Test
    @DisplayName("20. QUERY: findByDiaSemanaAndHorarioAndSede retorna lista vacía si no hay matches")
    void customQuery_sinMatches_listaVacia() {
        clasesRepo.save(nuevaClase("Java", DiaSemana.LUNES));

        List<Clases> resultado = clasesRepo
                .findByDiaSemanaAndHorario_IdHorarioAndAula_Sede_IdSede(
                        DiaSemana.DOMINGO,
                        horario.getIdHorario(),
                        sede.getIdSede()
                );

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}