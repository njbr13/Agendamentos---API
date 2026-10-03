package com.ximed.agendamento_api.dto.usuario;

import com.ximed.agendamento_api.domain.entity.Usuario;
import com.ximed.agendamento_api.domain.enums.RoleUsuario;

public record UsuarioResponseDTO(
        String mensagem,
        String id,
        String nome,
        String email,
        RoleUsuario role,
        Boolean ativo
) {
    public UsuarioResponseDTO(String mensagem, Usuario usuario) {
        this(mensagem, usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getRole(), usuario.getAtivo());
    }
}
