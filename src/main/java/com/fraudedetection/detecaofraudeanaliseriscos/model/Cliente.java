package com.fraudedetection.detecaofraudeanaliseriscos.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cliente {
    private String id;
    private String cpf;
    private String nome;
    private String email;
    private String telefone;
    private Double rendaMensal;
    private Integer scoreRisco; // 0-100 (0 = alto risco, 100 = baixo risco)
    private String endereco;
    private String profissao;
    private Boolean ativo;

    // Métodos para criar nós no grafo
    public String toNodeProperties() {
        return String.format(
                "cpf: '%s', nome: '%s', email: '%s', telefone: '%s', " +
                        "rendaMensal: %f, scoreRisco: %d, endereco: '%s', " +
                        "profissao: '%s', ativo: %b",
                cpf, nome, email, telefone,
                rendaMensal, scoreRisco, endereco,
                profissao, ativo
        );
    }
}
