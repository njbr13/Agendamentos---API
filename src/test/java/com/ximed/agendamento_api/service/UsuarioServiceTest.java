package com.ximed.agendamento_api.service;

import com.ximed.agendamento_api.domain.entity.Usuario;
import com.ximed.agendamento_api.domain.enums.RoleUsuario;
import com.ximed.agendamento_api.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuarioBase;

    @BeforeEach
    void setup() {
        usuarioRepository.deleteAllInBatch(); // Executa o DELETE imediatamente e evita conflito na fila do Hibernate
        usuarioBase = usuarioRepository.saveAndFlush(Usuario.builder()
                .nome("Nilton Ribeiro")
                .email("nilton.ribeiro@edu.unirio")
                .senha("senhaForte123")
                .role(RoleUsuario.PACIENTE)
                .build());
    }

    @Test
    void testAtualizarUsuario_Sucesso() {
        Usuario novosDados = Usuario.builder()
                .nome("Nilton Atualizado")
                .email("novo.email@edu.unirio")
                .role(RoleUsuario.ADMIN)
                .build();

        Usuario atualizado = usuarioService.atualizarUsuario(usuarioBase.getId(), novosDados);

        assertEquals("Nilton Atualizado", atualizado.getNome());
        assertEquals("novo.email@edu.unirio", atualizado.getEmail());
        assertEquals(RoleUsuario.ADMIN, atualizado.getRole());
        
        System.out.println("✅ Sucesso: O perfil foi atualizado com sucesso!");
    }

    @Test
    void testAtualizarUsuario_Erro_UsuarioNaoEncontrado() {
        Usuario novosDados = Usuario.builder()
                .nome("Teste")
                .email("teste@teste.com")
                .role(RoleUsuario.ADMIN)
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
                usuarioService.atualizarUsuario("ID_INVALIDO_INEXISTENTE", novosDados));
        
        assertEquals("Usuário não encontrado.", exception.getMessage());
        System.out.println("✅ Sucesso: Sistema barrou ID inexistente.");
    }

    @Test
    void testAtualizarUsuario_Erro_NomeNuloOuBranco() {
        Usuario novosDados = Usuario.builder().nome("   ").email("valido@email.com").role(RoleUsuario.ADMIN).build();
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
                usuarioService.atualizarUsuario(usuarioBase.getId(), novosDados));
        
        assertEquals("O nome não pode ser nulo ou em branco.", exception.getMessage());
        System.out.println("✅ Sucesso: Sistema barrou nome em branco.");
    }

    @Test
    void testAtualizarUsuario_Erro_EmailNuloOuBranco() {
        Usuario novosDados = Usuario.builder().nome("Nome Valido").email("").role(RoleUsuario.ADMIN).build();
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
                usuarioService.atualizarUsuario(usuarioBase.getId(), novosDados));
        
        assertEquals("O e-mail não pode ser nulo ou em branco.", exception.getMessage());
        System.out.println("✅ Sucesso: Sistema barrou e-mail em branco.");
    }

    @Test
    void testAtualizarUsuario_Erro_RoleNula() {
        Usuario novosDados = Usuario.builder().nome("Nome Valido").email("valido@email.com").role(null).build();
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
                usuarioService.atualizarUsuario(usuarioBase.getId(), novosDados));
        
        assertEquals("O papel (role) do usuário não pode ser nulo.", exception.getMessage());
        System.out.println("✅ Sucesso: Sistema barrou Role nula.");
    }

    @Test
    void testAtualizarUsuario_Erro_NenhumDadoAlterado() {
        Usuario novosDados = Usuario.builder()
                .nome(usuarioBase.getNome())
                .email(usuarioBase.getEmail())
                .role(usuarioBase.getRole())
                .build();
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
                usuarioService.atualizarUsuario(usuarioBase.getId(), novosDados));
        
        assertEquals("Nenhum dado foi alterado para atualização.", exception.getMessage());
        System.out.println("✅ Sucesso: Sistema bloqueou atualização fantasma (nenhum dado alterado).");
    }

    @Test
    void testAtualizarUsuario_Erro_EmailJaEmUso() {
        // Criando um segundo usuário (Concorrente) para ocupar um e-mail diferente
        usuarioRepository.save(Usuario.builder()
                .nome("Concorrente")
                .email("ocupado@teste.com")
                .senha("senha123")
                .role(RoleUsuario.ATENDENTE)
                .build());

        // Tentando atualizar o Nilton com o e-mail do Concorrente
        Usuario novosDados = Usuario.builder()
                .nome("Nilton Ribeiro")
                .email("ocupado@teste.com") 
                .role(RoleUsuario.PACIENTE)
                .build();
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
                usuarioService.atualizarUsuario(usuarioBase.getId(), novosDados));
        
        assertEquals("O novo e-mail já está em uso por outro usuário.", exception.getMessage());
        System.out.println("✅ Sucesso: Sistema impediu roubo de e-mail de outro usuário.");
    }
}
