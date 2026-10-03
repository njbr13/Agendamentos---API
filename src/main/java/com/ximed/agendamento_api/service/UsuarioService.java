package com.ximed.agendamento_api.service;

import com.ximed.agendamento_api.domain.entity.Usuario;
import com.ximed.agendamento_api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public Usuario criarUsuario(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("E-mail já está em uso.");
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    public Usuario buscarPorId(String id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
    }

    public Usuario atualizarUsuario(String id, Usuario dadosAtualizados) {
        Usuario usuarioExistente = buscarPorId(id);

        if (dadosAtualizados.getNome() == null || dadosAtualizados.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome não pode ser nulo ou em branco.");
        }
        if (dadosAtualizados.getEmail() == null || dadosAtualizados.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("O e-mail não pode ser nulo ou em branco.");
        }
        if (dadosAtualizados.getRole() == null) {
            throw new IllegalArgumentException("O papel (role) do usuário não pode ser nulo.");
        }

        boolean mudouNome = !usuarioExistente.getNome().equals(dadosAtualizados.getNome());
        boolean mudouEmail = !usuarioExistente.getEmail().equals(dadosAtualizados.getEmail());
        boolean mudouRole = usuarioExistente.getRole() != dadosAtualizados.getRole();

        if (!mudouNome && !mudouEmail && !mudouRole) {
            throw new IllegalArgumentException("Nenhum dado foi alterado para atualização.");
        }

        if (mudouEmail && usuarioRepository.existsByEmail(dadosAtualizados.getEmail())) {
            throw new IllegalArgumentException("O novo e-mail já está em uso por outro usuário.");
        }

        usuarioExistente.setNome(dadosAtualizados.getNome());
        usuarioExistente.setEmail(dadosAtualizados.getEmail());
        usuarioExistente.setRole(dadosAtualizados.getRole());

        return usuarioRepository.save(usuarioExistente);
    }

    public Usuario inativarUsuario(String id) {
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(false);
        return usuarioRepository.save(usuario);
    }

    public Usuario ativarUsuario(String id) {
        Usuario usuario = buscarPorId(id);
        if (usuario.getAtivo()) {
            throw new IllegalArgumentException("O usuário já está ativo.");
        }
        usuario.setAtivo(true);
        return usuarioRepository.save(usuario);
    }

    public void solicitarResetDeSenha(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com este e-mail."));

        String token = UUID.randomUUID().toString();
        usuario.setTokenResetSenha(token);
        usuario.setTokenExpiracao(LocalDateTime.now().plusHours(1));

        usuarioRepository.save(usuario);

        emailService.enviarEmailDeReset(usuario.getEmail(), token);
    }

    public void redefinirSenha(String token, String novaSenha) {
        Usuario usuario = usuarioRepository.findByTokenResetSenha(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido ou não encontrado."));

        if (usuario.getTokenExpiracao().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token expirado. Solicite um novo reset de senha.");
        }

        if (novaSenha == null || novaSenha.trim().isEmpty()) {
            throw new IllegalArgumentException("A nova senha não pode ser em branco.");
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuario.setTokenResetSenha(null);
        usuario.setTokenExpiracao(null);

        usuarioRepository.save(usuario);
    }
}
