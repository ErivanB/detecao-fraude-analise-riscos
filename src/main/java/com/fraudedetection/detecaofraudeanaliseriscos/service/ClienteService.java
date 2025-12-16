package com.fraudedetection.detecaofraudeanaliseriscos.service;

import com.fraudedetection.detecaofraudeanaliseriscos.model.Cliente;
import com.fraudedetection.detecaofraudeanaliseriscos.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente criarCliente(Cliente cliente) {
        cliente.setId(UUID.randomUUID().toString());
        cliente.setAtivo(true);
        if (cliente.getScoreRisco() == null) {
            cliente.setScoreRisco(50); // Score padrão
        }
        return clienteRepository.salvar(cliente);
    }

    public Cliente buscarClientePorCpf(String cpf) {
        return clienteRepository.buscarPorCpf(cpf);
    }

    public List<Cliente> listarTodosClientes() {
        return clienteRepository.buscarTodos();
    }

    public Cliente atualizarCliente(String cpf, Cliente cliente) {
        Cliente existente = clienteRepository.buscarPorCpf(cpf);
        if (existente != null) {
            cliente.setId(existente.getId());
            return clienteRepository.atualizar(cpf, cliente);
        }
        return null;
    }

    public boolean deletarCliente(String cpf) {
        return clienteRepository.deletar(cpf);
    }

    public List<Cliente> buscarClientesPorRisco(Integer scoreMinimo, Integer scoreMaximo) {
        return listarTodosClientes().stream()
                .filter(c -> c.getScoreRisco() >= scoreMinimo && c.getScoreRisco() <= scoreMaximo)
                .toList();
    }
}
