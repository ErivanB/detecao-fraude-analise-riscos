package com.fraudedetection.detecaofraudeanaliseriscos.controller;

import com.fraudedetection.detecaofraudeanaliseriscos.model.Cliente;
import com.fraudedetection.detecaofraudeanaliseriscos.service.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    public ResponseEntity<Cliente> criarCliente(@Valid @RequestBody Cliente cliente) {
        Cliente novoCliente = clienteService.criarCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoCliente);
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<Cliente> buscarCliente(@PathVariable String cpf) {
        Cliente cliente = clienteService.buscarClientePorCpf(cpf);
        return cliente != null ?
                ResponseEntity.ok(cliente) :
                ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes() {
        List<Cliente> clientes = clienteService.listarTodosClientes();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/risco")
    public ResponseEntity<List<Cliente>> buscarPorFaixaRisco(
            @RequestParam(defaultValue = "0") Integer minimo,
            @RequestParam(defaultValue = "100") Integer maximo) {
        List<Cliente> clientes = clienteService.buscarClientesPorRisco(minimo, maximo);
        return ResponseEntity.ok(clientes);
    }

    @PutMapping("/{cpf}")
    public ResponseEntity<Cliente> atualizarCliente(
            @PathVariable String cpf,
            @Valid @RequestBody Cliente cliente) {
        Cliente atualizado = clienteService.atualizarCliente(cpf, cliente);
        return atualizado != null ?
                ResponseEntity.ok(atualizado) :
                ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> deletarCliente(@PathVariable String cpf) {
        boolean deletado = clienteService.deletarCliente(cpf);
        return deletado ?
                ResponseEntity.noContent().build() :
                ResponseEntity.notFound().build();
    }
}
