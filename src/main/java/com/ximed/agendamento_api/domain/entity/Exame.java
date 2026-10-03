package com.ximed.agendamento_api.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "exames")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Exame {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "O nome do exame é obrigatório")
    @Size(min = 3, max = 100, message = "O nome do exame deve ter entre 3 e 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nome;

    @Size(max = 1000, message = "A descrição não pode exceder 1000 caracteres")
    @Column(columnDefinition = "TEXT")
    private String descricao;

    @NotNull(message = "A duração do exame é obrigatória")
    @Min(value = 5, message = "A duração mínima do exame é de 5 minutos")
    @Max(value = 120, message = "A duração máxima permitida é de 120 minutos (2 horas)")
    @Column(name = "duracao_minutos")
    private Integer duracaoMinutos;

    @NotNull(message = "O preço do exame é obrigatório")
    @PositiveOrZero(message = "O preço deve ser maior ou igual a zero")
    @Digits(integer = 8, fraction = 2, message = "O preço deve possuir no máximo 8 casas inteiras e 2 decimais")
    @Column(precision = 10, scale = 2)
    private BigDecimal preco;

    
    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDateTime.now();
        if (this.ativo == null) {
            this.ativo = true;
        }
    }
}
