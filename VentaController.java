package com.petsalud.backendpetsalud.controller;

import com.petsalud.backendpetsalud.entity.Venta;
import com.petsalud.backendpetsalud.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    // Solo el personal interno registra ventas en el sistema
    @PreAuthorize("hasAnyRole('EMPLEADO', 'GERENTE')")
    @PostMapping
    public ResponseEntity<Venta> realizarVenta(
            @RequestParam Long usuarioId,
            @RequestBody List<Long> productosIds) {
        Venta nuevaVenta = ventaService.realizarVenta(usuarioId, productosIds);
        return ResponseEntity.ok(nuevaVenta);
    }

    // Todos los roles pueden ver el detalle de una venta específica
    @PreAuthorize("hasAnyRole('CLIENTE', 'EMPLEADO', 'GERENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<Venta> obtenerVentaPorId(@PathVariable Long id) {
        return ventaService.obtenerVentaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Los clientes pueden ver su propio historial, al igual que el personal
    @PreAuthorize("hasAnyRole('CLIENTE', 'EMPLEADO', 'GERENTE')")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Venta>> listarVentasPorUsuario(@PathVariable Long usuarioId) {
        List<Venta> ventas = ventaService.listarVentasPorUsuario(usuarioId);
        return ResponseEntity.ok(ventas);
    }

    // Solo gerencia y empleados pueden auditar todas las ventas globales
    @PreAuthorize("hasAnyRole('EMPLEADO', 'GERENTE')")
    @GetMapping
    public ResponseEntity<List<Venta>> listarTodasLasVentas() {
        List<Venta> ventas = ventaService.listarTodasLasVentas();
        return ResponseEntity.ok(ventas);
    }
}