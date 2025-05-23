package br.com.unit.tokseg.armariointeligente.controller;

import br.com.unit.tokseg.armariointeligente.exception.ResourceNotFoundException;
import br.com.unit.tokseg.armariointeligente.model.Entrega;
import br.com.unit.tokseg.armariointeligente.model.StatusEntrega;
import br.com.unit.tokseg.armariointeligente.service.EntregaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/entregas")
@Tag(name = "Entregas", description = "Endpoints para gerenciamento de entregas")
public class EntregaController {

    @Autowired
    private EntregaService entregaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('ENTREGADOR')")
    @Operation(summary = "Registrar entrega", description = "Registra uma nova entrega no sistema")
    public ResponseEntity<?> registrarEntrega(@RequestBody Entrega entrega) {
        Entrega novaEntrega = entregaService.registrarEntrega(entrega);
        return ResponseEntity.ok(novaEntrega);
    }

    @PutMapping("/{id}/retirada")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CLIENTE') or hasRole('ENTREGADOR')")
    @Operation(summary = "Registrar retirada", description = "Registra a retirada de uma entrega")
    public ResponseEntity<?> registrarRetirada(
            @Parameter(description = "ID da entrega") @PathVariable Long id, 
            @Parameter(description = "Código de acesso do compartimento") @RequestParam String codigoAcesso) {
        Entrega entregaAtualizada = entregaService.registrarRetirada(id, codigoAcesso);
        return ResponseEntity.ok(entregaAtualizada);
    }

    @PutMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('ENTREGADOR')")
    @Operation(summary = "Cancelar entrega", description = "Cancela uma entrega registrada")
    public ResponseEntity<?> cancelarEntrega(
            @Parameter(description = "ID da entrega") @PathVariable Long id) {
        Entrega entregaAtualizada = entregaService.cancelarEntrega(id);
        return ResponseEntity.ok(entregaAtualizada);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('ENTREGADOR')")
    @Operation(summary = "Listar entregas", description = "Lista todas as entregas registradas no sistema")
    public ResponseEntity<?> listarEntregas() {
        return ResponseEntity.ok(entregaService.listarEntregas());
    }

    @GetMapping("/compartimento/{compartimentoId}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('ENTREGADOR')")
    @Operation(summary = "Listar entregas por compartimento", description = "Lista entregas de um compartimento específico")
    public ResponseEntity<?> listarEntregasPorCompartimento(
            @Parameter(description = "ID do compartimento") @PathVariable Long compartimentoId) {
        return ResponseEntity.ok(entregaService.listarEntregasPorCompartimento(compartimentoId));
    }

    @GetMapping("/entregador/{entregadorId}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('ENTREGADOR')")
    @Operation(summary = "Listar entregas por entregador", description = "Lista entregas realizadas por um entregador específico")
    public ResponseEntity<?> listarEntregasPorEntregador(
            @Parameter(description = "ID do entregador") @PathVariable Long entregadorId) {
        return ResponseEntity.ok(entregaService.listarEntregasPorEntregador(entregadorId));
    }

    @GetMapping("/destinatario/{destinatarioId}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('ENTREGADOR') or @usuarioServiceImpl.isCurrentUser(#destinatarioId)")
    @Operation(summary = "Listar entregas por destinatário", description = "Lista entregas destinadas a um usuário específico")
    public ResponseEntity<?> listarEntregasPorDestinatario(
            @Parameter(description = "ID do destinatário") @PathVariable Long destinatarioId) {
        return ResponseEntity.ok(entregaService.listarEntregasPorDestinatario(destinatarioId));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('ENTREGADOR')")
    @Operation(summary = "Listar entregas por status", description = "Lista entregas filtradas por status")
    public ResponseEntity<?> listarEntregasPorStatus(
            @Parameter(description = "Status da entrega") @PathVariable StatusEntrega status) {
        return ResponseEntity.ok(entregaService.listarEntregasPorStatus(status));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('ENTREGADOR') or @entregaServiceImpl.isDestinatario(#id)")
    @Operation(summary = "Buscar entrega por ID", description = "Busca uma entrega pelo seu ID")
    public ResponseEntity<?> buscarEntregaPorId(
            @Parameter(description = "ID da entrega") @PathVariable Long id) {
        Optional<Entrega> entrega = entregaService.buscarEntregaPorId(id);
        if (entrega.isPresent()) {
            return ResponseEntity.ok(entrega.get());
        } else {
            throw new ResourceNotFoundException("Entrega", "id", id);
        }
    }

    @GetMapping("/rastreio/{codigoRastreio}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('ENTREGADOR') or hasRole('CLIENTE')")
    @Operation(summary = "Buscar entrega por código de rastreio", description = "Busca uma entrega pelo seu código de rastreio")
    public ResponseEntity<?> buscarEntregaPorCodigoRastreio(
            @Parameter(description = "Código de rastreio") @PathVariable String codigoRastreio) {
        Optional<Entrega> entrega = entregaService.buscarEntregaPorCodigoRastreio(codigoRastreio);
        if (entrega.isPresent()) {
            return ResponseEntity.ok(entrega.get());
        } else {
            throw new ResourceNotFoundException("Entrega", "código de rastreio", codigoRastreio);
        }
    }

    @GetMapping("/periodo")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('ENTREGADOR')")
    @Operation(summary = "Listar entregas por período", description = "Lista entregas realizadas em um período específico")
    public ResponseEntity<?> listarEntregasPorPeriodo(
            @Parameter(description = "Data de início (formato ISO)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @Parameter(description = "Data de fim (formato ISO)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return ResponseEntity.ok(entregaService.listarEntregasPorPeriodo(inicio, fim));
    }
}
