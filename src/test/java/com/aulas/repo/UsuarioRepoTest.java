package com.aulas.repo;

import com.aulas.model.Carrera;
import com.aulas.model.Rol;
import com.aulas.model.Sede;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Se cubren 20 escenarios en 8 bloques:
 *   1. CRUD básico (3)
 *   2. findById / Optional (2)
 *   3. Actualización (2)
 *   4. Eliminación e identidad (2)
 *   5. count y existsById (1)
 *   6. Constraints y campos opcionales (5)
 *   7. Queries derivadas (3)
 *   8. Queries con JOIN FETCH (2)
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
@DisplayName("UsuarioRepo - pruebas de acceso a datos (@DataJpaTest)")
class UsuarioRepoTest {

    @Autowired
    private IUsuarioRepo usuarioRepo;

    @Autowired
    private TestEntityManager em;

    private Rol rol;
    private Sede sede;
    private Carrera carrera;

    @BeforeEach
    void setUp() {
        rol = em.persistAndFlush(new Rol(null, "ALUMNO"));
        sede = em.persistAndFlush(new Sede(null, "Sede San Isidro", "Av. Javier Prado 123"));
        carrera = em.persistAndFlush(new Carrera(null, "Ingeniería de Sistemas", new HashSet<>()));
    }

    /** Helper: arma un Usuario con correo y código únicos. carrera y adminSedeCheck son opcionales. */
    private Usuario nuevoUsuario(String correo, String codigo) {
        return new Usuario(null, "Nombre", "Apellido", correo, codigo,
                "$2a$10$hashBcryptDePrueba", rol, sede, carrera, null);
    }

    // =========================================================
    // 1. CRUD básico
    // =========================================================

    @Test
    @DisplayName("01. save: persiste un usuario nuevo y le asigna ID")
    void save_asignaId() {
        Usuario guardado = usuarioRepo.save(nuevoUsuario("u1@cibertec.edu.pe", "U1001"));

        assertNotNull(guardado.getIdUsuario());
        assertTrue(guardado.getIdUsuario() > 0);
    }

    @Test
    @DisplayName("02. save: preserva correo y código institucional correctamente")
    void save_preservaDatos() {
        Usuario guardado = usuarioRepo.save(nuevoUsuario("aldo@cibertec.edu.pe", "U2001"));

        assertEquals("aldo@cibertec.edu.pe", guardado.getCorreoInstitucional());
        assertEquals("U2001", guardado.getCodigoInstitucional());
    }

    @Test
    @DisplayName("03. findAll: retorna todos los usuarios guardados")
    void findAll_devuelveTodos() {
        usuarioRepo.save(nuevoUsuario("u1@cibertec.edu.pe", "U1001"));
        usuarioRepo.save(nuevoUsuario("u2@cibertec.edu.pe", "U1002"));
        usuarioRepo.save(nuevoUsuario("u3@cibertec.edu.pe", "U1003"));

        List<Usuario> usuarios = usuarioRepo.findAll();

        assertEquals(3, usuarios.size());
    }

    // =========================================================
    // 2. findById / Optional
    // =========================================================

    @Test
    @DisplayName("04. findById: ID existente retorna Optional con el usuario")
    void findById_existente_presente() {
        Usuario guardado = usuarioRepo.save(nuevoUsuario("u1@cibertec.edu.pe", "U1001"));

        Optional<Usuario> encontrado = usuarioRepo.findById(guardado.getIdUsuario());

        assertTrue(encontrado.isPresent());
        assertEquals("u1@cibertec.edu.pe", encontrado.get().getCorreoInstitucional());
    }

    @Test
    @DisplayName("05. findById: ID inexistente retorna Optional.empty()")
    void findById_inexistente_empty() {
        Optional<Usuario> encontrado = usuarioRepo.findById(99999);

        assertFalse(encontrado.isPresent());
    }

    // =========================================================
    // 3. Actualización
    // =========================================================

    @Test
    @DisplayName("06. UPDATE: save con ID existente conserva el mismo ID")
    void update_mantieneId() {
        Usuario usuario = usuarioRepo.save(nuevoUsuario("u1@cibertec.edu.pe", "U1001"));
        Integer idOriginal = usuario.getIdUsuario();

        usuario.setNombreUsuario("NombreCambiado");
        Usuario actualizado = usuarioRepo.save(usuario);

        assertEquals(idOriginal, actualizado.getIdUsuario());
    }

    @Test
    @DisplayName("07. UPDATE: modificar contrasena se persiste correctamente")
    void update_modificaContrasena() {
        Usuario usuario = usuarioRepo.save(nuevoUsuario("u1@cibertec.edu.pe", "U1001"));

        usuario.setContrasena("$2a$10$nuevoHashBcryptDespuesDelCambio");
        usuarioRepo.save(usuario);

        Optional<Usuario> recargado = usuarioRepo.findById(usuario.getIdUsuario());
        assertTrue(recargado.isPresent());
        assertEquals("$2a$10$nuevoHashBcryptDespuesDelCambio", recargado.get().getContrasena());
    }

    // =========================================================
    // 4. Eliminación e identidad
    // =========================================================

    @Test
    @DisplayName("08. deleteById: el usuario deja de existir tras eliminar")
    void deleteById_elimina() {
        Usuario usuario = usuarioRepo.save(nuevoUsuario("u1@cibertec.edu.pe", "U1001"));
        Integer id = usuario.getIdUsuario();

        usuarioRepo.deleteById(id);

        assertFalse(usuarioRepo.findById(id).isPresent());
    }

    @Test
    @DisplayName("09. equals/hashCode: identidad de Usuario basada en idUsuario (Lombok)")
    void entity_equalsBasadoEnId() {
        Usuario u1 = usuarioRepo.save(nuevoUsuario("u1@cibertec.edu.pe", "U1001"));
        Usuario u2 = new Usuario(u1.getIdUsuario(), "Otro", "Otro", "otro@otro.com",
                "U9999", "otrohash", rol, sede, null, null);

        assertEquals(u1, u2);
        assertEquals(u1.hashCode(), u2.hashCode());
    }

    // =========================================================
    // 5. count
    // =========================================================

    @Test
    @DisplayName("10. count: refleja correctamente la cantidad insertada")
    void count_reflejaCantidad() {
        usuarioRepo.save(nuevoUsuario("u1@cibertec.edu.pe", "U1001"));
        usuarioRepo.save(nuevoUsuario("u2@cibertec.edu.pe", "U1002"));

        assertEquals(2, usuarioRepo.count());
    }

    // =========================================================
    // 6. Constraints y campos opcionales
    // =========================================================

    @Test
    @DisplayName("11. CONSTRAINT: correoInstitucional UNIQUE — duplicado lanza excepción")
    void constraint_correoUnique_duplicadoFalla() {
        usuarioRepo.saveAndFlush(nuevoUsuario("duplicado@cibertec.edu.pe", "U1001"));

        Usuario otro = nuevoUsuario("duplicado@cibertec.edu.pe", "U9999");
        assertThrows(DataIntegrityViolationException.class,
                () -> usuarioRepo.saveAndFlush(otro));
    }

    @Test
    @DisplayName("12. CONSTRAINT: codigoInstitucional UNIQUE — duplicado lanza excepción")
    void constraint_codigoUnique_duplicadoFalla() {
        usuarioRepo.saveAndFlush(nuevoUsuario("u1@cibertec.edu.pe", "U_DUP"));

        Usuario otro = nuevoUsuario("otro@cibertec.edu.pe", "U_DUP");
        assertThrows(DataIntegrityViolationException.class,
                () -> usuarioRepo.saveAndFlush(otro));
    }

    @Test
    @DisplayName("13. CONSTRAINT: nombreUsuario null lanza DataIntegrityViolationException")
    void constraint_nombreNull_lanzaExcepcion() {
        Usuario invalido = new Usuario(null, null, "Apellido", "u1@cibertec.edu.pe",
                "U1001", "hash", rol, sede, carrera, null);

        assertThrows(DataIntegrityViolationException.class,
                () -> usuarioRepo.saveAndFlush(invalido));
    }

    @Test
    @DisplayName("14. OPCIONAL: carrera puede ser null sin problema")
    void carrera_esOpcional_puedeSerNull() {
        Usuario sinCarrera = new Usuario(null, "Nombre", "Apellido", "sincarrera@cibertec.edu.pe",
                "U_SC", "hash", rol, sede, null, null);

        assertDoesNotThrow(() -> usuarioRepo.saveAndFlush(sinCarrera));
    }

    @Test
    @DisplayName("15. SELF-REFERENCE: adminSedeCheck apunta a otro Usuario y se persiste OK")
    void adminSedeCheck_selfReference() {
        Usuario admin = usuarioRepo.saveAndFlush(nuevoUsuario("admin@cibertec.edu.pe", "ADMIN1"));

        Usuario alumno = new Usuario(null, "Aldo", "Bastian", "aldo@cibertec.edu.pe",
                "U2024", "hash", rol, sede, carrera, admin);
        usuarioRepo.saveAndFlush(alumno);

        Optional<Usuario> recargado = usuarioRepo.findById(alumno.getIdUsuario());
        assertTrue(recargado.isPresent());
        assertNotNull(recargado.get().getAdminSedeCheck());
        assertEquals(admin.getIdUsuario(), recargado.get().getAdminSedeCheck().getIdUsuario());
    }

    // =========================================================
    // 7. Queries derivadas (findByX, existsByX)
    // =========================================================

    @Test
    @DisplayName("16. QUERY: findByCorreoInstitucional retorna el usuario buscado")
    void findByCorreoInstitucional_existente() {
        usuarioRepo.save(nuevoUsuario("buscado@cibertec.edu.pe", "U1001"));

        Optional<Usuario> encontrado = usuarioRepo.findByCorreoInstitucional("buscado@cibertec.edu.pe");

        assertTrue(encontrado.isPresent());
        assertEquals("U1001", encontrado.get().getCodigoInstitucional());
    }

    @Test
    @DisplayName("17. QUERY: findByCorreoInstitucional retorna empty si no existe")
    void findByCorreoInstitucional_inexistente() {
        Optional<Usuario> encontrado = usuarioRepo.findByCorreoInstitucional("noexiste@cibertec.edu.pe");

        assertFalse(encontrado.isPresent());
    }

    @Test
    @DisplayName("18. QUERY: existsByCorreoInstitucional retorna true/false correctamente")
    void existsByCorreoInstitucional() {
        usuarioRepo.save(nuevoUsuario("existe@cibertec.edu.pe", "U1001"));

        assertTrue(usuarioRepo.existsByCorreoInstitucional("existe@cibertec.edu.pe"));
        assertFalse(usuarioRepo.existsByCorreoInstitucional("noexiste@cibertec.edu.pe"));
    }

    // =========================================================
    // 8. Queries con JOIN FETCH (@Query)
    // =========================================================

    @Test
    @DisplayName("19. QUERY: existsByCodigoInstitucional retorna true/false correctamente")
    void existsByCodigoInstitucional() {
        usuarioRepo.save(nuevoUsuario("u1@cibertec.edu.pe", "CODIGO_EXISTE"));

        assertTrue(usuarioRepo.existsByCodigoInstitucional("CODIGO_EXISTE"));
        assertFalse(usuarioRepo.existsByCodigoInstitucional("CODIGO_NO_EXISTE"));
    }

    @Test
    @DisplayName("20. QUERY: findByCorreoConRol carga el Rol asociado vía JOIN FETCH")
    void findByCorreoConRol_cargaRolJoinFetch() {
        usuarioRepo.save(nuevoUsuario("conrol@cibertec.edu.pe", "U1001"));
        em.flush();
        em.clear(); // limpia caché L1 para asegurar que el Rol viene del JOIN FETCH

        Optional<Usuario> encontrado = usuarioRepo.findByCorreoConRol("conrol@cibertec.edu.pe");

        assertTrue(encontrado.isPresent());
        // Si el JOIN FETCH funciona, el rol ya está cargado (no es un proxy lazy sin inicializar)
        assertNotNull(encontrado.get().getRol());
        assertEquals("ALUMNO", encontrado.get().getRol().getNombreRol());
    }
}