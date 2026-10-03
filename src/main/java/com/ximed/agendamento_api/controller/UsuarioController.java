package com.ximed.agendamento_api.controller;

import com.ximed.agendamento_api.domain.entity.Usuario;
import com.ximed.agendamento_api.dto.usuario.RedefinirSenhaRequestDTO;
import com.ximed.agendamento_api.dto.usuario.UsuarioCreateRequestDTO;
import com.ximed.agendamento_api.dto.usuario.UsuarioResponseDTO;
import com.ximed.agendamento_api.dto.usuario.UsuarioUpdateRequestDTO;
import com.ximed.agendamento_api.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criar(@RequestBody @Valid UsuarioCreateRequestDTO dto) {
        Usuario novoUsuario = Usuario.builder()
                .nome(dto.nome())
                .email(dto.email())
                .senha(dto.senha())
                .role(dto.role())
                .build();

        Usuario usuarioSalvo = usuarioService.criarUsuario(novoUsuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(new UsuarioResponseDTO("Usuário criado com sucesso", usuarioSalvo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable String id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(new UsuarioResponseDTO("Usuário recuperado com sucesso", usuario));
    }

    @PutMapping("/atualizar-perfil/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(
            @PathVariable String id,
            @RequestBody @Valid UsuarioUpdateRequestDTO dto,
            @AuthenticationPrincipal UserDetails usuarioLogado) {
        
        Usuario dadosParaAtualizar = Usuario.builder()
                .nome(dto.nome())
                .email(dto.email())
                .role(dto.role())
                .build();

        Usuario usuarioAtualizado = usuarioService.atualizarUsuario(id, dadosParaAtualizar);
        
        return ResponseEntity.ok(new UsuarioResponseDTO("Perfil atualizado com sucesso", usuarioAtualizado));
    }

    @DeleteMapping("/inativar/{id}")
    public ResponseEntity<UsuarioResponseDTO> inativar(@PathVariable String id) {
        Usuario usuario = usuarioService.inativarUsuario(id);
        return ResponseEntity.ok(new UsuarioResponseDTO("Usuário inativado com sucesso", usuario));
    }

    @PatchMapping("/ativar/{id}")
    public ResponseEntity<UsuarioResponseDTO> ativar(@PathVariable String id) {
        Usuario usuario = usuarioService.ativarUsuario(id);
        return ResponseEntity.ok(new UsuarioResponseDTO("Usuário ativado com sucesso", usuario));
    }

    @PostMapping("/esqueci-minha-senha")
    public ResponseEntity<Map<String, String>> solicitarReset(@RequestParam String email) {
        usuarioService.solicitarResetDeSenha(email);
        return ResponseEntity.ok(Map.of("mensagem", "E-mail de recuperação enviado com sucesso (se o e-mail estiver cadastrado)"));
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<Map<String, String>> redefinirSenha(@RequestBody @Valid RedefinirSenhaRequestDTO dto) {
        usuarioService.redefinirSenha(dto.token(), dto.novaSenha());
        return ResponseEntity.ok(Map.of("mensagem", "Senha redefinida com sucesso. Faça login com sua nova senha."));
    }
}
