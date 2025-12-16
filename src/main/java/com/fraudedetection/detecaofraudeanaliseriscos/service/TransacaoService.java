package com.fraudedetection.detecaofraudeanaliseriscos.service;

import com.fraudedetection.detecaofraudeanaliseriscos.model.Transacao;
import com.fraudedetection.detecaofraudeanaliseriscos.repository.TransacaoRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final AnaliseRiscoService analiseRiscoService;

    public TransacaoService(TransacaoRepository transacaoRepository,
                            AnaliseRiscoService analiseRiscoService) {
        this.transacaoRepository = transacaoRepository;
        this.analiseRiscoService = analiseRiscoService;
    }

    public Transacao criarTransacao(Transacao transacao) {
        transacao.setId(UUID.randomUUID().toString());
        transacao.setDataHora(LocalDateTime.now());

        // Analisar transação para detectar fraudes
        analisarTransacao(transacao);

        return transacaoRepository.salvar(transacao);
    }

    private void analisarTransacao(Transacao transacao) {
        double probabilidade = 0.0;

        // Regras básicas de detecção de fraude
        if (transacao.getValor() > 10000) {
            probabilidade += 40;
        }

        if ("INTERNACIONAL".equalsIgnoreCase(transacao.getLocalizacao())) {
            probabilidade += 30;
        }

        if (transacao.getDispositivo().contains("desconhecido")) {
            probabilidade += 20;
        }

        if (transacao.getIp() != null && transacao.getIp().startsWith("192.168")) {
            probabilidade -= 10; // IP interno reduz risco
        }

        transacao.setProbabilidadeFraude(Math.min(100, Math.max(0, probabilidade)));
        transacao.setSuspeita(probabilidade > 50);
        transacao.setStatus(probabilidade > 50 ? "EM_ANALISE" : "APROVADA");
    }

    public Transacao buscarTransacaoPorId(String id) {
        return transacaoRepository.buscarPorId(id);
    }

    public List<Transacao> buscarTransacoesPorCliente(String cpf) {
        return transacaoRepository.buscarPorCliente(cpf);
    }

    public List<Transacao> listarTodasTransacoes() {
        return transacaoRepository.buscarTodas();
    }

    public List<Transacao> listarTransacoesSuspeitas() {
        return transacaoRepository.buscarSuspeitas();
    }

    public Transacao atualizarTransacao(String id, Transacao transacao) {
        return transacaoRepository.atualizar(id, transacao);
    }

    public boolean deletarTransacao(String id) {
        return transacaoRepository.deletar(id);
    }

    public Transacao marcarComoAnalisada(String id, boolean ehFraude) {
        Transacao transacao = transacaoRepository.buscarPorId(id);
        if (transacao != null) {
            transacao.setStatus(ehFraude ? "REJEITADA" : "APROVADA");
            transacao.setSuspeita(ehFraude);
            return transacaoRepository.atualizar(id, transacao);
        }
        return null;
    }
}
