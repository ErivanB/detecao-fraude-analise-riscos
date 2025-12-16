package com.fraudedetection.detecaofraudeanaliseriscos.controller;

import com.fraudedetection.detecaofraudeanaliseriscos.model.Transacao;
import com.fraudedetection.detecaofraudeanaliseriscos.service.TransacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transacoes")
@CrossOrigin(origins = "*")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @PostMapping
    public ResponseEntity<Transacao> criarTransacao(@Valid @RequestBody Transacao transacao) {
        Transacao novaTransacao = transacaoService.criarTransacao(transacao);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaTransacao);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transacao> buscarTransacao(@PathVariable String id) {
        Transacao transacao = transacaoService.buscarTransacaoPorId(id);
        return transacao != null ?
                ResponseEntity.ok(transacao) :
                ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<Transacao>> listarTransacoes() {
        List<Transacao> transacoes = transacaoService.listarTodasTransacoes();
        return ResponseEntity.ok(transacoes);
    }

    @GetMapping("/cliente/{cpf}")
    public ResponseEntity<List<Transacao>> buscarTransacoesCliente(@PathVariable String cpf) {
        List<Transacao> transacoes = transacaoService.buscarTransacoesPorCliente(cpf);
        return ResponseEntity.ok(transacoes);
    }

    @GetMapping("/suspeitas")
    public ResponseEntity<List<Transacao>> listarTransacoesSuspeitas() {
        List<Transacao> transacoes = transacaoService.listarTransacoesSuspeitas();
        return ResponseEntity.ok(transacoes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transacao> atualizarTransacao(
            @PathVariable String id,
            @Valid @RequestBody Transacao transacao) {
        Transacao atualizada = transacaoService.atualizarTransacao(id, transacao);
        return atualizada != null ?
                ResponseEntity.ok(atualizada) :
                ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTransacao(@PathVariable String id) {
        boolean deletado = transacaoService.deletarTransacao(id);
        return deletado ?
                ResponseEntity.noContent().build() :
                ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/analise")
    public ResponseEntity<Transacao> marcarAnalise(
            @PathVariable String id,
            @RequestBody Map<String, Boolean> request) {
        Boolean ehFraude = request.get("ehFraude");
        if (ehFraude == null) {
            return ResponseEntity.badRequest().build();
        }

        Transacao atualizada = transacaoService.marcarComoAnalisada(id, ehFraude);
        return atualizada != null ?
                ResponseEntity.ok(atualizada) :
                ResponseEntity.notFound().build();
    }
}
