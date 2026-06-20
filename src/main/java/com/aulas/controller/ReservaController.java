package com.aulas.controller;

import com.aulas.dto.ReservaDTO;
import com.aulas.model.Reserva;
import com.aulas.service.IReservaService;
import com.aulas.service.ICrud;
import com.aulas.util.MapperUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaController extends CRUDController<Reserva, ReservaDTO, Integer> {

    private final IReservaService service;
    private final MapperUtil mapperUtil;

    @Override protected ICrud<Reserva, Integer> getService() { return service; }
    @Override protected MapperUtil getMapperUtil() { return mapperUtil; }



    @Override
    @PostMapping
    public ResponseEntity<ReservaDTO> save(@Valid @RequestBody ReservaDTO dto) throws Exception {
        Reserva entity = mapperUtil.map(dto, Reserva.class);
        ReservaDTO created = service.crearReserva(entity);
        return new ResponseEntity<>(created, org.springframework.http.HttpStatus.CREATED);
    }

    @GetMapping("/mis-reservas")
    public ResponseEntity<List<ReservaDTO>> misReservas(
            org.springframework.security.core.Authentication auth) throws Exception {
        // auth.getName() devuelve el "subject" del JWT = correo institucional
        return ResponseEntity.ok(service.findMisReservas(auth.getName()));
    }

    @GetMapping("/filtrar")
    public ResponseEntity<List<ReservaDTO>> filtrar(@RequestParam(required = false)
                                                    @DateTimeFormat( iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                                                    @RequestParam(required = false) Integer idSede,
                                                    @RequestParam(required = false) String estado) throws Exception{

        return ResponseEntity.ok(service.findFiltradas(fecha, idSede, estado));
    }

    @PatchMapping("/{id}/aprobar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReservaDTO> aprobar(@PathVariable("id") Integer id) throws Exception {
        return ResponseEntity.ok(service.aprobar(id));
    }

    @PatchMapping("/{id}/rechazar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReservaDTO> rechazar(@PathVariable("id") Integer id) throws Exception {
        return ResponseEntity.ok(service.rechazar(id));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ReservaDTO> cancelar(@PathVariable("id") Integer id, Authentication auth) throws Exception {

        return ResponseEntity.ok(service.cancelar(id, auth.getName()));
    }

}