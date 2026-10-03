package com.ximed.agendamento_api.dto.usuario;

import com.ximed.agendamento_api.domain.enums.RoleUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioUpdateRequestDTO(
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        String nome,
       
        @Email(message = "Formato de e-mail inválido")
        String email,

        RoleUsuario role
) {
}
