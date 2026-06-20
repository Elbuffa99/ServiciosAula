package com.aulas.repo;

import com.aulas.model.Aula;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Se cubren 20 escenarios en 7 bloques:
 *   1. CRUD básico (4)
 *   2. findById / Optional (2)
 *   3. Actualización (2)
 *   4. Eliminación e identidad (2)
 *   5. count y existsById (4)
 *   6. Operaciones por lote (3)
 *   7. Constraints de columna (3)
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
@DisplayName("AulaRepo - pruebas de acceso a datos (@DataJpaTest)")
class AulaRepoTest {

    @Autowired
    private IAulaRepo aulaRepo;

    @Autowired
    private TestEntityManager em;

    private TipoAula tipoAula;
    private Sede sede;

    @BeforeEach
    void setUp() {
        tipoAula = em.persistAndFlush(new TipoAula(null, "Laboratorio de cómputo"));
        sede = em.persistAndFlush(new Sede(null, "Sede San Isidro", "Av. Javier Prado 123"));
    }

    /** Helper: arma un Aula nueva (sin persistir) con FKs ya válidas. */
    private Aula nuevaAula(String codigo, Integer capacidad) {
        return new Aula(null, codigo, capacidad, tipoAula, sede);
    }

    // =========================================================
    // 1. CRUD básico
    // =========================================================

    @Test
    @DisplayName("01. save: persiste un aula nueva y le asigna ID auto-generado")
    void save_asignaId() {
        Aula guardada = aulaRepo.save(nuevaAula("A-101", 30));

        assertNotNull(guardada.getIdAula());
        assertTrue(guardada.getIdAula() > 0);
    }

    @Test
    @DisplayName("02. save: preserva codigoAula y capacidad correctamente")
    void save_preservaDatos() {
        Aula guardada = aulaRepo.save(nuevaAula("LAB-205", 25));

        assertEquals("LAB-205", guardada.getCodigoAula());
        assertEquals(25, guardada.getCapacidad());
    }

    @Test
    @DisplayName("03. findAll: retorna todas las aulas guardadas")
    void findAll_devuelveTodos() {
        aulaRepo.save(nuevaAula("A-101", 30));
        aulaRepo.save(nuevaAula("A-102", 35));
        aulaRepo.save(nuevaAula("A-103", 40));

        List<Aula> aulas = aulaRepo.findAll();

        assertEquals(3, aulas.size());
    }

    @Test
    @DisplayName("04. findAll: repo vacío retorna lista vacía (no null)")
    void findAll_repoVacio_listaVacia() {
        List<Aula> aulas = aulaRepo.findAll();

        assertNotNull(aulas);
        assertTrue(aulas.isEmpty());
    }

    // =========================================================
    // 2. findById / Optional
    // =========================================================

    @Test
    @DisplayName("05. findById: ID existente retorna Optional con el aula")
    void findById_existente_presente() {
        Aula guardada = aulaRepo.save(nuevaAula("A-101", 30));

        Optional<Aula> encontrada = aulaRepo.findById(guardada.getIdAula());

        assertTrue(encontrada.isPresent());
        assertEquals("A-101", encontrada.get().getCodigoAula());
    }

    @Test
    @DisplayName("06. findById: ID inexistente retorna Optional.empty()")
    void findById_inexistente_empty() {
        Optional<Aula> encontrada = aulaRepo.findById(99999);

        assertFalse(encontrada.isPresent());
    }

    // =========================================================
    // 3. Actualización
    // =========================================================

    @Test
    @DisplayName("07. UPDATE: save con ID existente conserva el mismo ID")
    void update_mantieneId() {
        Aula aula = aulaRepo.save(nuevaAula("A-101", 30));
        Integer idOriginal = aula.getIdAula();

        aula.setCapacidad(45);
        Aula actualizada = aulaRepo.save(aula);

        assertEquals(idOriginal, actualizada.getIdAula());
    }

    @Test
    @DisplayName("08. UPDATE: save modifica efectivamente la capacidad en la BD")
    void update_modificaCapacidad() {
        Aula aula = aulaRepo.save(nuevaAula("A-101", 30));

        aula.setCapacidad(45);
        aulaRepo.save(aula);

        Optional<Aula> recargada = aulaRepo.findById(aula.getIdAula());
        assertTrue(recargada.isPresent());
        assertEquals(45, recargada.get().getCapacidad());
    }

    // =========================================================
    // 4. Eliminación e identidad
    // =========================================================

    @Test
    @DisplayName("09. deleteById: el aula deja de existir tras eliminar")
    void deleteById_elimina() {
        Aula aula = aulaRepo.save(nuevaAula("A-101", 30));
        Integer id = aula.getIdAula();

        aulaRepo.deleteById(id);

        assertFalse(aulaRepo.findById(id).isPresent());
    }

    @Test
    @DisplayName("10. equals/hashCode: identidad de Aula basada en idAula (Lombok)")
    void entity_equalsBasadoEnId() {
        Aula a1 = aulaRepo.save(nuevaAula("A-101", 30));
        Aula a2 = new Aula(a1.getIdAula(), "OTRO_CODIGO", 99, tipoAula, sede);

        assertEquals(a1, a2);
        assertEquals(a1.hashCode(), a2.hashCode());
    }

    // =========================================================
    // 5. count y existsById
    // =========================================================

    @Test
    @DisplayName("11. count: repo vacío retorna 0")
    void count_repoVacio_cero() {
        assertEquals(0, aulaRepo.count());
    }

    @Test
    @DisplayName("12. count: refleja correctamente la cantidad insertada")
    void count_reflejaCantidad() {
        aulaRepo.save(nuevaAula("A-101", 30));
        aulaRepo.save(nuevaAula("A-102", 35));

        assertEquals(2, aulaRepo.count());
    }

    @Test
    @DisplayName("13. existsById: true para un ID que sí existe")
    void existsById_existente_true() {
        Aula aula = aulaRepo.save(nuevaAula("A-101", 30));

        assertTrue(aulaRepo.existsById(aula.getIdAula()));
    }

    @Test
    @DisplayName("14. existsById: false para un ID inexistente")
    void existsById_inexistente_false() {
        assertFalse(aulaRepo.existsById(99999));
    }

    // =========================================================
    // 6. Operaciones por lote
    // =========================================================

    @Test
    @DisplayName("15. saveAll: persiste todas las aulas con IDs asignados")
    void saveAll_persisteLista() {
        List<Aula> lote = List.of(
                nuevaAula("A-101", 30),
                nuevaAula("A-102", 35),
                nuevaAula("A-103", 40)
        );

        List<Aula> guardadas = aulaRepo.saveAll(lote);

        assertEquals(3, guardadas.size());
        assertTrue(guardadas.stream().allMatch(a -> a.getIdAula() != null));
    }

    @Test
    @DisplayName("16. findAllById: retorna solo las aulas cuyo ID está en la lista")
    void findAllById_filtraIds() {
        Aula a1 = aulaRepo.save(nuevaAula("A-101", 30));
        Aula a2 = aulaRepo.save(nuevaAula("A-102", 35));
        aulaRepo.save(nuevaAula("A-103", 40));

        List<Aula> resultado = aulaRepo.findAllById(List.of(a1.getIdAula(), a2.getIdAula()));

        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("17. deleteAll: vacía completamente el repositorio")
    void deleteAll_vaciaRepo() {
        aulaRepo.save(nuevaAula("A-101", 30));
        aulaRepo.save(nuevaAula("A-102", 35));

        aulaRepo.deleteAll();

        assertEquals(0, aulaRepo.count());
    }

    // =========================================================
    // 7. Constraints de columna
    // =========================================================

    @Test
    @DisplayName("18. CONSTRAINT: codigoAula null lanza DataIntegrityViolationException")
    void constraint_codigoAulaNull_lanzaExcepcion() {
        Aula invalida = new Aula(null, null, 30, tipoAula, sede);

        assertThrows(DataIntegrityViolationException.class,
                () -> aulaRepo.saveAndFlush(invalida));
    }

    @Test
    @DisplayName("19. CONSTRAINT: capacidad null lanza DataIntegrityViolationException")
    void constraint_capacidadNull_lanzaExcepcion() {
        Aula invalida = new Aula(null, "A-101", null, tipoAula, sede);

        assertThrows(DataIntegrityViolationException.class,
                () -> aulaRepo.saveAndFlush(invalida));
    }

    @Test
    @DisplayName("20. CONSTRAINT: tipoAula null lanza DataIntegrityViolationException (FK obligatoria)")
    void constraint_tipoAulaNull_lanzaExcepcion() {
        Aula invalida = new Aula(null, "A-101", 30, null, sede);

        assertThrows(DataIntegrityViolationException.class,
                () -> aulaRepo.saveAndFlush(invalida));
    }
}