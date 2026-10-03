package com.ximed.agendamento_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void enviarEmailDeReset(String destinatario, String token) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo(destinatario);
        mensagem.setSubject("XIMED - Redefinição de Senha");
        mensagem.setText("Você solicitou a redefinição de senha.\n\n" +
                "Utilize o token abaixo para redefinir sua senha:\n" +
                token + "\n\n" +
                "Este token é válido por 1 hora.");

        mailSender.send(mensagem);
    }
}
