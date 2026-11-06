package com.petsalud.backendpetsalud.controller;

import com.petsalud.backendpetsalud.entity.Cita;
import com.petsalud.backendpetsalud.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    @Autowired
    private CitaService citaService;

    // Los clientes agendan citas; el personal también puede hacerlo por ellos
    @PreAuthorize("hasAnyRole('CLIENTE', 'EMPLEADO', 'GERENTE')")
    @PostMapping
    public ResponseEntity<Cita> agendarCita(
            @RequestParam Long mascotaId,
            @RequestParam Long veterinarioId,
            @RequestParam String fechaHora, // Formato ISO 8601
            @RequestParam String motivo) {
        LocalDateTime fechaHoraParsed = LocalDateTime.parse(fechaHora);
        Cita nuevaCita = citaService.agendarCita(mascotaId, veterinarioId, fechaHoraParsed, motivo);
        return ResponseEntity.ok(nuevaCita);
    }

    // Acceso general para consultar el detalle de una cita
    @PreAuthorize("hasAnyRole('CLIENTE', 'EMPLEADO', 'GERENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<Cita> obtenerCitaPorId(@PathVariable Long id) {
        return citaService.obtenerCitaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Solo el personal veterinario actualiza el estado (ej. "Completada", "En proceso")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'GERENTE')")
    @PutMapping("/{id}/estado")
    public ResponseEntity<Cita> actualizarEstadoCita(@PathVariable Long id, @RequestParam String estado) {
        Cita citaActualizada = citaService.actualizarEstadoCita(id, estado);
        return ResponseEntity.ok(citaActualizada);
    }

    // El cliente puede cancelar su cita, o el personal por fuerza mayor
    @PreAuthorize("hasAnyRole('CLIENTE', 'EMPLEADO', 'GERENTE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarCita(@PathVariable Long id) {
        citaService.cancelarCita(id);
        return ResponseEntity.noContent().build();
    }

    // El cliente revisa el historial de citas de su mascota
    @PreAuthorize("hasAnyRole('CLIENTE', 'EMPLEADO', 'GERENTE')")
    @GetMapping("/mascota/{mascotaId}")
    public ResponseEntity<List<Cita>> listarCitasPorMascota(@PathVariable Long mascotaId) {
        List<Cita> citas = citaService.listarCitasPorMascota(mascotaId);
        return ResponseEntity.ok(citas);
    }

    // Exclusivo para que el veterinario revise su propia agenda de trabajo
    @PreAuthorize("hasAnyRole('EMPLEADO', 'GERENTE')")
    @GetMapping("/veterinario/{veterinarioId}")
    public ResponseEntity<List<Cita>> listarCitasPorVeterinario(@PathVariable Long veterinarioId) {
        List<Cita> citas = citaService.listarCitasPorVeterinario(veterinarioId);
        return ResponseEntity.ok(citas);
    }

    // Reportes de gestión clínica entre rangos de fecha
    @PreAuthorize("hasAnyRole('EMPLEADO', 'GERENTE')")
    @GetMapping("/entre-fechas")
    public ResponseEntity<List<Cita>> listarCitasEntreFechas(
            @RequestParam String inicio,
            @RequestParam String fin) {
        LocalDateTime inicioParsed = LocalDateTime.parse(inicio);
        LocalDateTime finParsed = LocalDateTime.parse(fin);
        List<Cita> citas = citaService.listarCitasEntreFechas(inicioParsed, finParsed);
        return ResponseEntity.ok(citas);
    }
}