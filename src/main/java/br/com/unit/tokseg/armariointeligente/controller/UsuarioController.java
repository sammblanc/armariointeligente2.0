package br.com.unit.tokseg.armariointeligente.controller;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.com.unit.tokseg.armariointeligente.exception.ResourceNotFoundException;
import br.com.unit.tokseg.armariointeligente.model.Usuario;
import br.com.unit.tokseg.armariointeligente.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

// Adicionar a importação para as anotações de segurança
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuários", description = "Endpoints para gerenciamento de usuários")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Adicionar anotações de segurança aos métodos
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Criar usuário", description = "Cria um novo usuário no sistema (requer permissão de ADMINISTRADOR)")
    public ResponseEntity<?> criarUsuario(@RequestBody Usuario usuario) {
        Usuario novoUsuario = usuarioService.criarUsuario(usuario);
        return ResponseEntity.ok(novoUsuario);
    }

    @GetMapping
    @Operation(summary = "Listar usuários", description = "Lista todos os usuários cadastrados no sistema")
    public ResponseEntity<?> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @GetMapping(value = "/ativos")
    @Operation(summary = "Listar usuários ativos", description = "Lista todos os usuários ativos no sistema")
    public ResponseEntity<?> listarUsuariosAtivos() {
        return ResponseEntity.ok(usuarioService.listarUsuariosAtivos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or @usuarioServiceImpl.isCurrentUser(#id)")
    @Operation(summary = "Buscar usuário por ID", description = "Busca um usuário pelo seu ID")
    public ResponseEntity<?> buscarUsuarioPorId(
            @Parameter(description = "ID do usuário") @PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.buscarUsuarioPorId(id);
        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        } else {
            throw new ResourceNotFoundException("Usuário", "id", id);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or @usuarioServiceImpl.isCurrentUser(#id)")
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente")
    public ResponseEntity<?> atualizarUsuario(
            @Parameter(description = "ID do usuário") @PathVariable Long id, 
            @RequestBody Usuario usuario) {
        Usuario usuarioAtualizado = usuarioService.atualizarUsuario(id, usuario);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    @PutMapping("/{id}/desativar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Desativar usuário", description = "Desativa um usuário no sistema (requer permissão de ADMINISTRADOR)")
    public ResponseEntity<?> desativarUsuario(
            @Parameter(description = "ID do usuário") @PathVariable Long id) {
        Usuario usuarioDesativado = usuarioService.desativarUsuario(id);
        return ResponseEntity.ok(usuarioDesativado);
    }

    @PutMapping("/{id}/ativar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Ativar usuário", description = "Ativa um usuário no sistema (requer permissão de ADMINISTRADOR)")
    public ResponseEntity<?> ativarUsuario(
            @Parameter(description = "ID do usuário") @PathVariable Long id) {
        Usuario usuarioAtivado = usuarioService.ativarUsuario(id);
        return ResponseEntity.ok(usuarioAtivado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Deletar usuário", description = "Remove um usuário do sistema (requer permissão de ADMINISTRADOR)")
    public ResponseEntity<?> deletarUsuario(
            @Parameter(description = "ID do usuário") @PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
