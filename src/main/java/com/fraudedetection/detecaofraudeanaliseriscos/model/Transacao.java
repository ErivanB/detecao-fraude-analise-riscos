package com.fraudedetection.detecaofraudeanaliseriscos.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transacao {
    private String id;
    private String clienteCpf;
    private Double valor;
    private String tipo; // "CREDITO", "DEBITO", "TRANSFERENCIA", "PAGAMENTO"
    private String categoria; // "ALIMENTACAO", "TRANSPORTE", "EDUCACAO", "SAUDE", "LAZER"
    private String estabelecimento;
    private String localizacao;
    private LocalDateTime dataHora;
    private String dispositivo;
    private String ip;
    private Boolean suspeita;
    private Double probabilidadeFraude;
    private String status; // "APROVADA", "REJEITADA", "EM_ANALISE"

    public String toNodeProperties() {
        return String.format(
                "valor: %f, tipo: '%s', categoria: '%s', " +
                        "estabelecimento: '%s', localizacao: '%s', " +
                        "dataHora: '%s', dispositivo: '%s', ip: '%s', " +
                        "suspeita: %b, probabilidadeFraude: %f, status: '%s'",
                valor, tipo, categoria,
                estabelecimento, localizacao,
                dataHora.toString(), dispositivo, ip,
                suspeita, probabilidadeFraude, status
        );
    }
}
