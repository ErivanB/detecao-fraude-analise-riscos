package com.fraudedetection.detecaofraudeanaliseriscos.service;

import com.fraudedetection.detecaofraudeanaliseriscos.model.AnaliseRisco;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AnaliseRiscoService {

    public AnaliseRisco analisarCliente(String cpf, List<Map<String, Object>> historicoTransacoes) {
        AnaliseRisco analise = new AnaliseRisco();
        analise.setId(UUID.randomUUID().toString());
        analise.setClienteCpf(cpf);
        analise.setDataAnalise(LocalDateTime.now());

        int score = 100; // Começa com score máximo
        List<String> alertas = new ArrayList<>();
        Map<String, Double> fatores = new HashMap<>();

        // Análise de frequência de transações
        if (historicoTransacoes != null) {
            long transacoes24h = historicoTransacoes.stream()
                    .filter(t -> {
                        LocalDateTime data = (LocalDateTime) t.get("dataHora");
                        return data.isAfter(LocalDateTime.now().minusHours(24));
                    })
                    .count();

            if (transacoes24h > 10) {
                score -= 20;
                alertas.add("Muitas transações nas últimas 24 horas: " + transacoes24h);
                fatores.put("frequencia_transacoes", 0.7);
            }

            // Análise de valor médio
            double valorMedio = historicoTransacoes.stream()
                    .mapToDouble(t -> (Double) t.get("valor"))
                    .average()
                    .orElse(0.0);

            if (valorMedio > 5000) {
                score -= 15;
                fatores.put("valor_medio_alto", 0.6);
            }
        }

        // Determinar nível de risco
        String nivelRisco;
        if (score >= 80) {
            nivelRisco = "BAIXO";
        } else if (score >= 60) {
            nivelRisco = "MEDIO";
        } else if (score >= 40) {
            nivelRisco = "ALTO";
        } else {
            nivelRisco = "CRITICO";
        }

        analise.setScoreFinal(score);
        analise.setNivelRisco(nivelRisco);
        analise.setAlertas(alertas);
        analise.setFatoresRisco(fatores);
        analise.setRecomendacao(gerarRecomendacao(nivelRisco));

        return analise;
    }

    private String gerarRecomendacao(String nivelRisco) {
        return switch (nivelRisco) {
            case "BAIXO" -> "Cliente de baixo risco. Aprovação automática para transações comuns.";
            case "MEDIO" -> "Cliente com risco moderado. Recomenda-se verificação adicional para transações acima de R$ 5.000.";
            case "ALTO" -> "Cliente de alto risco. Necessária aprovação manual para transações acima de R$ 1.000.";
            case "CRITICO" -> "Cliente com risco crítico. Todas as transações devem ser analisadas manualmente.";
            default -> "Análise necessária.";
        };
    }
}
