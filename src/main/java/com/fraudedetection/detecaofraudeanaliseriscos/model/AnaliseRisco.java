package com.fraudedetection.detecaofraudeanaliseriscos.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnaliseRisco {
    private String id;
    private String clienteCpf;
    private Integer scoreFinal;
    private String nivelRisco; // "BAIXO", "MEDIO", "ALTO", "CRITICO"
    private List<String> alertas;
    private Map<String, Double> fatoresRisco;
    private LocalDateTime dataAnalise;
    private String recomendacao;

    public String toNodeProperties() {
        return String.format(
                "scoreFinal: %d, nivelRisco: '%s', " +
                        "dataAnalise: '%s', recomendacao: '%s'",
                scoreFinal, nivelRisco,
                dataAnalise.toString(), recomendacao
        );
    }
}
