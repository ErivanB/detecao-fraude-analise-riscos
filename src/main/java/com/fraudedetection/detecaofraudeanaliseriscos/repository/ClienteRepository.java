package com.fraudedetection.detecaofraudeanaliseriscos.repository;

import com.redislabs.redisgraph.RedisGraphContext;
import com.redislabs.redisgraph.ResultSet;
import com.redislabs.redisgraph.Record;
import com.redislabs.redisgraph.graph_entities.Node;
import com.fraudedetection.model.Cliente;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ClienteRepository {

    private final RedisGraphContext redisGraph;
    private static final String GRAPH_NAME = "fraude";

    public ClienteRepository(RedisGraphContext redisGraph) {
        this.redisGraph = redisGraph;
        criarIndices();
    }

    private void criarIndices() {
        String query = "CREATE INDEX ON :Cliente(cpf)";
        redisGraph.query(GRAPH_NAME, query);
    }

    public Cliente salvar(Cliente cliente) {
        String query = String.format(
                "CREATE (c:Cliente {id: '%s', %s}) RETURN c",
                cliente.getId(),
                cliente.toNodeProperties()
        );

        ResultSet resultSet = redisGraph.query(GRAPH_NAME, query);
        if (resultSet.hasNext()) {
            Record record = resultSet.next();
            Node node = record.getValue("c");
            return nodeToCliente(node);
        }
        return null;
    }

    public Cliente buscarPorCpf(String cpf) {
        String query = String.format(
                "MATCH (c:Cliente {cpf: '%s'}) RETURN c",
                cpf
        );

        ResultSet resultSet = redisGraph.query(GRAPH_NAME, query);
        if (resultSet.hasNext()) {
            Record record = resultSet.next();
            Node node = record.getValue("c");
            return nodeToCliente(node);
        }
        return null;
    }

    public List<Cliente> buscarTodos() {
        String query = "MATCH (c:Cliente) RETURN c";
        ResultSet resultSet = redisGraph.query(GRAPH_NAME, query);

        List<Cliente> clientes = new ArrayList<>();
        while (resultSet.hasNext()) {
            Record record = resultSet.next();
            Node node = record.getValue("c");
            clientes.add(nodeToCliente(node));
        }
        return clientes;
    }

    public Cliente atualizar(String cpf, Cliente cliente) {
        String query = String.format(
                "MATCH (c:Cliente {cpf: '%s'}) SET c += {%s} RETURN c",
                cpf,
                cliente.toNodeProperties()
        );

        ResultSet resultSet = redisGraph.query(GRAPH_NAME, query);
        if (resultSet.hasNext()) {
            Record record = resultSet.next();
            Node node = record.getValue("c");
            return nodeToCliente(node);
        }
        return null;
    }

    public boolean deletar(String cpf) {
        String query = String.format(
                "MATCH (c:Cliente {cpf: '%s'}) DELETE c",
                cpf
        );

        ResultSet resultSet = redisGraph.query(GRAPH_NAME, query);
        return resultSet.getStatistics().nodesDeleted() > 0;
    }

    private Cliente nodeToCliente(Node node) {
        Cliente cliente = new Cliente();
        cliente.setId(node.getProperty("id").getValue().toString());
        cliente.setCpf(node.getProperty("cpf").getValue().toString());
        cliente.setNome(node.getProperty("nome").getValue().toString());
        cliente.setEmail(node.getProperty("email").getValue().toString());
        cliente.setTelefone(node.getProperty("telefone").getValue().toString());
        cliente.setRendaMensal(Double.parseDouble(node.getProperty("rendaMensal").getValue().toString()));
        cliente.setScoreRisco(Integer.parseInt(node.getProperty("scoreRisco").getValue().toString()));
        cliente.setEndereco(node.getProperty("endereco").getValue().toString());
        cliente.setProfissao(node.getProperty("profissao").getValue().toString());
        cliente.setAtivo(Boolean.parseBoolean(node.getProperty("ativo").getValue().toString()));
        return cliente;
    }
}
