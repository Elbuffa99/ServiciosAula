package com.aulas.repo;

import com.aulas.model.Aula;
import com.aulas.model.EstadoReserva;
import com.aulas.model.Horario;
import com.aulas.model.Reserva;
import com.aulas.model.Rol;
import com.aulas.model.Sede;
import com.aulas.model.TipoAula;
import com.aulas.model.TipoReserva;
import com.aulas.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Se cubren 20 escenarios en 6 bloques:
 *   1. CRUD básico (4)
 *   2. findById, update, delete, equals (5)
 *   3. count y métodos heredados (2)
 *   4. Constraints (3)
 *   5. Queries derivadas (2)
 *   6. Queries @Query con JOIN FETCH y reglas de negocio (4)
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
@DisplayName("ReservaRepo - pruebas de acceso a datos (@DataJpaTest)")
class ReservaRepoTest {

    @Autowired
    private IReservaRepo reservaRepo;

    @Autowired
    private TestEntityManager em;

    private Usuario usuario;
    private Aula aula;
    private Sede sede;
    private Horario horario;
    private EstadoReserva estadoAprobada;
    private EstadoReserva estadoCancelada;
    private TipoReserva tipoReserva;

    @BeforeEach
    void setUp() {
        // Grafo de entidades-padre completo (el más complejo de los 5 tests)
        Rol rol = em.persistAndFlush(new Rol(null, "ALUMNO"));
        sede = em.persistAndFlush(new Sede(null, "Sede San Isidro", "Av. Javier Prado 123"));
        TipoAula tipoAula = em.persistAndFlush(new TipoAula(null, "Laboratorio"));
        aula = em.persistAndFlush(new Aula(null, "A-101", 30, tipoAula, sede));
        horario = em.persistAndFlush(new Horario(null, LocalTime.of(8, 0), LocalTime.of(10, 0), "MAÑANA"));
        estadoAprobada = em.persistAndFlush(new EstadoReserva(null, "APROBADA"));
        estadoCancelada = em.persistAndFlush(new EstadoReserva(null, "CANCELADA"));
        tipoReserva = em.persistAndFlush(new TipoReserva(null, "INDIVIDUAL"));
        usuario = em.persistAndFlush(new Usuario(null, "Aldo", "Bastian",
                "aldo@cibertec.edu.pe", "U2024", "$2a$10$hash",
                rol, sede, null, null));
    }

    private Reserva nuevaReserva(LocalDate fecha, EstadoReserva estado) {
        return new Reserva(null, fecha, LocalDateTime.now(),
                usuario, aula, sede, horario, estado, tipoReserva);
    }

    // =========================================================
    // 1. CRUD básico
    // =========================================================

    @Test
    @DisplayName("01. save: persiste una reserva nueva y le asigna ID")
    void save_asignaId() {
        Reserva guardada = reservaRepo.save(nuevaReserva(LocalDate.now().plusDays(1), estadoAprobada));

        assertNotNull(guardada.getIdReserva());
        assertTrue(guardada.getIdReserva() > 0);
    }

    @Test
    @DisplayName("02. save: preserva fechaReserva y fechaSolicitud correctamente")
    void save_preservaFechas() {
        LocalDate fecha = LocalDate.of(2026, 7, 15);
        Reserva guardada = reservaRepo.save(nuevaReserva(fecha, estadoAprobada));

        assertEquals(fecha, guardada.getFechaReserva());
        assertNotNull(guardada.getFechaSolicitud());
    }

    @Test
    @DisplayName("03. findAll: retorna todas las reservas guardadas")
    void findAll_devuelveTodas() {
        reservaRepo.save(nuevaReserva(LocalDate.now().plusDays(1), estadoAprobada));
        reservaRepo.save(nuevaReserva(LocalDate.now().plusDays(2), estadoAprobada));
        reservaRepo.save(nuevaReserva(LocalDate.now().plusDays(3), estadoAprobada));

        List<Reserva> reservas = reservaRepo.findAll();

        assertEquals(3, reservas.size());
    }

    @Test
    @DisplayName("04. findAll: repo vacío retorna lista vacía")
    void findAll_repoVacio_listaVacia() {
        List<Reserva> reservas = reservaRepo.findAll();

        assertNotNull(reservas);
        assertTrue(reservas.isEmpty());
    }

    // =========================================================
    // 2. findById, update, delete, equals
    // =========================================================

    @Test
    @DisplayName("05. findById: ID existente retorna Optional con la reserva")
    void findById_existente_presente() {
        Reserva guardada = reservaRepo.save(nuevaReserva(LocalDate.now().plusDays(1), estadoAprobada));

        Optional<Reserva> encontrada = reservaRepo.findById(guardada.getIdReserva());

        assertTrue(encontrada.isPresent());
    }

    @Test
    @DisplayName("06. findById: ID inexistente retorna Optional.empty()")
    void findById_inexistente_empty() {
        Optional<Reserva> encontrada = reservaRepo.findById(99999);

        assertFalse(encontrada.isPresent());
    }

    @Test
    @DisplayName("07. UPDATE: save con ID existente conserva el mismo ID")
    void update_mantieneId() {
        Reserva reserva = reservaRepo.save(nuevaReserva(LocalDate.now().plusDays(1), estadoAprobada));
        Integer idOriginal = reserva.getIdReserva();

        reserva.setEstadoReserva(estadoCancelada);
        Reserva actualizada = reservaRepo.save(reserva);

        assertEquals(idOriginal, actualizada.getIdReserva());
    }

    @Test
    @DisplayName("08. UPDATE: cambiar estadoReserva se persiste correctamente")
    void update_cambiaEstadoReserva() {
        Reserva reserva = reservaRepo.save(nuevaReserva(LocalDate.now().plusDays(1), estadoAprobada));

        reserva.setEstadoReserva(estadoCancelada);
        reservaRepo.save(reserva);

        Optional<Reserva> recargada = reservaRepo.findById(reserva.getIdReserva());
        assertTrue(recargada.isPresent());
        assertEquals("CANCELADA", recargada.get().getEstadoReserva().getDescripcion());
    }

    @Test
    @DisplayName("09. deleteById: la reserva deja de existir tras eliminar")
    void deleteById_elimina() {
        Reserva reserva = reservaRepo.save(nuevaReserva(LocalDate.now().plusDays(1), estadoAprobada));
        Integer id = reserva.getIdReserva();

        reservaRepo.deleteById(id);

        assertFalse(reservaRepo.findById(id).isPresent());
    }

    // =========================================================
    // 3. count y métodos heredados
    // =========================================================

    @Test
    @DisplayName("10. count: refleja correctamente la cantidad insertada")
    void count_reflejaCantidad() {
        reservaRepo.save(nuevaReserva(LocalDate.now().plusDays(1), estadoAprobada));
        reservaRepo.save(nuevaReserva(LocalDate.now().plusDays(2), estadoAprobada));

        assertEquals(2, reservaRepo.count());
    }

    @Test
    @DisplayName("11. existsById: true para un ID que sí existe, false en otro caso")
    void existsById_trueYFalse() {
        Reserva reserva = reservaRepo.save(nuevaReserva(LocalDate.now().plusDays(1), estadoAprobada));

        assertTrue(reservaRepo.existsById(reserva.getIdReserva()));
        assertFalse(reservaRepo.existsById(99999));
    }

    // =========================================================
    // 4. Constraints
    // =========================================================

    @Test
    @DisplayName("12. CONSTRAINT: usuario null lanza DataIntegrityViolationException (FK obligatoria)")
    void constraint_usuarioNull_lanzaExcepcion() {
        Reserva invalida = new Reserva(null, LocalDate.now(), LocalDateTime.now(),
                null, aula, sede, horario, estadoAprobada, tipoReserva);

        assertThrows(DataIntegrityViolationException.class,
                () -> reservaRepo.saveAndFlush(invalida));
    }

    @Test
    @DisplayName("13. CONSTRAINT: aula null lanza DataIntegrityViolationException (FK obligatoria)")
    void constraint_aulaNull_lanzaExcepcion() {
        Reserva invalida = new Reserva(null, LocalDate.now(), LocalDateTime.now(),
                usuario, null, sede, horario, estadoAprobada, tipoReserva);

        assertThrows(DataIntegrityViolationException.class,
                () -> reservaRepo.saveAndFlush(invalida));
    }

    @Test
    @DisplayName("14. CONSTRAINT: fechaReserva null lanza DataIntegrityViolationException")
    void constraint_fechaReservaNull_lanzaExcepcion() {
        Reserva invalida = new Reserva(null, null, LocalDateTime.now(),
                usuario, aula, sede, horario, estadoAprobada, tipoReserva);

        assertThrows(DataIntegrityViolationException.class,
                () -> reservaRepo.saveAndFlush(invalida));
    }

    // =========================================================
    // 5. Queries derivadas (findByX)
    // =========================================================

    @Test
    @DisplayName("15. QUERY derivada: findByFechaHorarioSedeYEstadoNotIn — excluye CANCELADA")
    void customQuery_findByFechaHorarioSedeYEstadoNotIn() {
        LocalDate fecha = LocalDate.of(2026, 7, 15);
        // 2 reservas APROBADAS (mismo slot)
        reservaRepo.save(nuevaReserva(fecha, estadoAprobada));
        reservaRepo.save(nuevaReserva(fecha, estadoAprobada));
        // 1 reserva CANCELADA (mismo slot) → debe ser excluida
        reservaRepo.save(nuevaReserva(fecha, estadoCancelada));

        List<Reserva> resultado = reservaRepo
                .findByFechaReservaAndHorario_IdHorarioAndSede_IdSedeAndEstadoReserva_DescripcionNotIn(
                        fecha,
                        horario.getIdHorario(),
                        sede.getIdSede(),
                        List.of("CANCELADA", "RECHAZADA")
                );

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream()
                .allMatch(r -> "APROBADA".equals(r.getEstadoReserva().getDescripcion())));
    }

    @Test
    @DisplayName("16. QUERY derivada: findByUsuario_CorreoInstitucional retorna reservas ordenadas por fecha DESC")
    void customQuery_findByUsuarioCorreoOrderByFechaDesc() {
        reservaRepo.save(nuevaReserva(LocalDate.of(2026, 6, 1), estadoAprobada));
        reservaRepo.save(nuevaReserva(LocalDate.of(2026, 8, 1), estadoAprobada));
        reservaRepo.save(nuevaReserva(LocalDate.of(2026, 7, 1), estadoAprobada));

        List<Reserva> resultado = reservaRepo
                .findByUsuario_CorreoInstitucionalOrderByFechaReservaDesc("aldo@cibertec.edu.pe");

        assertEquals(3, resultado.size());
        // Verificamos orden DESC: 2026-08-01, 2026-07-01, 2026-06-01
        assertEquals(LocalDate.of(2026, 8, 1), resultado.get(0).getFechaReserva());
        assertEquals(LocalDate.of(2026, 7, 1), resultado.get(1).getFechaReserva());
        assertEquals(LocalDate.of(2026, 6, 1), resultado.get(2).getFechaReserva());
    }

    // =========================================================
    // 6. Queries @Query (JOIN FETCH y reglas de negocio)
    // =========================================================

    @Test
    @DisplayName("17. QUERY: findByIdConEstadoYUsuario carga estado y usuario vía JOIN FETCH")
    void customQuery_findByIdConEstadoYUsuario_joinFetch() {
        Reserva guardada = reservaRepo.save(nuevaReserva(LocalDate.now().plusDays(1), estadoAprobada));
        em.flush();
        em.clear(); // limpia caché L1 para forzar lectura desde BD

        Optional<Reserva> encontrada = reservaRepo.findByIdConEstadoYUsuario(guardada.getIdReserva());

        assertTrue(encontrada.isPresent());
        // Si el JOIN FETCH funciona, estado y usuario ya están cargados
        assertNotNull(encontrada.get().getEstadoReserva().getDescripcion());
        assertNotNull(encontrada.get().getUsuario().getCorreoInstitucional());
    }

    @Test
    @DisplayName("18. QUERY: contarReservasActivasEnSlot ignora reservas CANCELADAS")
    void customQuery_contarReservasActivasEnSlot() {
        LocalDate fecha = LocalDate.of(2026, 7, 15);
        reservaRepo.save(nuevaReserva(fecha, estadoAprobada));
        reservaRepo.save(nuevaReserva(fecha, estadoAprobada));
        reservaRepo.save(nuevaReserva(fecha, estadoCancelada)); // no debe contarse

        Long activas = reservaRepo.contarReservasActivasEnSlot(
                aula.getIdAula(), fecha, horario.getIdHorario());

        assertEquals(2L, activas);
    }

    @Test
    @DisplayName("19. QUERY: usuarioYaTieneReservaEnHorario detecta conflicto del mismo usuario")
    void customQuery_usuarioYaTieneReservaEnHorario() {
        LocalDate fecha = LocalDate.of(2026, 7, 15);
        reservaRepo.save(nuevaReserva(fecha, estadoAprobada));

        boolean tieneConflicto = reservaRepo.usuarioYaTieneReservaEnHorario(
                usuario.getIdUsuario(), fecha, horario.getIdHorario());
        boolean noConflicto = reservaRepo.usuarioYaTieneReservaEnHorario(
                usuario.getIdUsuario(), fecha.plusDays(1), horario.getIdHorario());

        assertTrue(tieneConflicto, "El usuario ya tiene reserva en ese slot");
        assertFalse(noConflicto, "El usuario NO tiene reserva en una fecha distinta");
    }

    @Test
    @DisplayName("20. QUERY: contarReservasUsuarioEnFecha cuenta solo reservas ACTIVAS del día")
    void customQuery_contarReservasUsuarioEnFecha() {
        LocalDate fecha = LocalDate.of(2026, 7, 15);
        reservaRepo.save(nuevaReserva(fecha, estadoAprobada));
        reservaRepo.save(nuevaReserva(fecha, estadoAprobada));
        reservaRepo.save(nuevaReserva(fecha, estadoCancelada)); // se excluye
        reservaRepo.save(nuevaReserva(fecha.plusDays(1), estadoAprobada)); // otra fecha, se excluye

        Long enFecha = reservaRepo.contarReservasUsuarioEnFecha(usuario.getIdUsuario(), fecha);

        assertEquals(2L, enFecha);
    }
}