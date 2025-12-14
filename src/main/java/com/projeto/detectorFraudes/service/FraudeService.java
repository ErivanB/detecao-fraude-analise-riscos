package com.projeto.detectorFraudes.service;

import com.projeto.detectorFraudes.dto.SuspeitoDTO;
import com.redislabs.redisgraph.Record;
import com.redislabs.redisgraph.ResultSet;
import com.redislabs.redisgraph.impl.api.RedisGraph;
import org.springframework.beans.factory.annotation.Value; // Importante!
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;

@Service
public class FraudeService {

    // Lendo valores do application.properties
    @Value("${falkordb.host}")
    private String host;

    @Value("${falkordb.port}")
    private int port;

    @Value("${falkordb.graph-name}")
    private String graphName;

    public List<SuspeitoDTO> buscarDeviceFarms() {
        List<SuspeitoDTO> lista = new ArrayList<>();

        // Usando as variáveis injetadas (host e port)
        try (JedisPool pool = new JedisPool(host, port);
             RedisGraph graph = new RedisGraph(pool)) {

            String query = """
                MATCH (u:Usuario)-[:USA_DEVICE]->(d:Device)
                WITH d, count(DISTINCT u) as total
                WHERE total > 3
                RETURN d.id as id, total
                ORDER BY total DESC
            """;

            // Usando o nome do grafo injetado
            ResultSet result = graph.query(graphName, query);

            while (result.hasNext()) {
                Record rec = result.next();
                String id = rec.getString("id");
                long total = Double.valueOf(rec.getString("total")).longValue();
                String risco = (total > 20) ? "CRÍTICO" : "ALTO";
                lista.add(new SuspeitoDTO(id, total, risco));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
    // Método para bloquear o dispositivo no banco
    public void bloquearDispositivo(String deviceId) {
        try (redis.clients.jedis.JedisPool pool = new redis.clients.jedis.JedisPool("localhost", 6379);
             com.redislabs.redisgraph.impl.api.RedisGraph graph = new com.redislabs.redisgraph.impl.api.RedisGraph(pool)) {

            // Query que marca o dispositivo com uma flag de bloqueado
            String query = "MATCH (d:Device {id: '" + deviceId + "'}) SET d.status = 'BLOCKED'";
            graph.query("anti_fraude", query);

            System.out.println("Dispositivo bloqueado com sucesso: " + deviceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public List<SuspeitoDTO> listarTudo() {
        List<SuspeitoDTO> lista = new ArrayList<>();

        // Conexão (igual ao método anterior)
        try (redis.clients.jedis.JedisPool pool = new redis.clients.jedis.JedisPool("localhost", 6379);
             com.redislabs.redisgraph.impl.api.RedisGraph graph = new com.redislabs.redisgraph.impl.api.RedisGraph(pool)) {

            // QUERY NOVA: Removemos o "WHERE total > 3"
            // Usamos OPTIONAL MATCH para garantir que traga o device mesmo se ninguém usou (caso raro)
            String query = """
            MATCH (d:Device)
            OPTIONAL MATCH (u:Usuario)-[:USA_DEVICE]->(d)
            WITH d, count(DISTINCT u) as total
            RETURN d.id as id, total
            ORDER BY total DESC
        """;

            com.redislabs.redisgraph.ResultSet result = graph.query("anti_fraude", query);

            while (result.hasNext()) {
                com.redislabs.redisgraph.Record rec = result.next();

                String id = rec.getString("id");
                long total = Double.valueOf(rec.getString("total")).longValue();

                // Lógica de Risco atualizada
                String risco;
                if (total > 20) {
                    risco = "CRÍTICO";
                } else if (total > 3) {
                    risco = "ALTO"; // Alerta Amarelo
                } else {
                    risco = "NORMAL"; // Usuário comum (1 a 3 conexões)
                }

                lista.add(new SuspeitoDTO(id, total, risco));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
    // No final da classe FraudeService.java, adicione:

    public List<com.projeto.detectorFraudes.dto.AlertaFinanceiroDTO> rastrearFraudeHibrida() {
        List<com.projeto.detectorFraudes.dto.AlertaFinanceiroDTO> alertas = new ArrayList<>();

        try (redis.clients.jedis.JedisPool pool = new redis.clients.jedis.JedisPool("localhost", 6379);
             com.redislabs.redisgraph.impl.api.RedisGraph graph = new com.redislabs.redisgraph.impl.api.RedisGraph(pool)) {

            // A QUERY DO SANTO GRAAL:
            // "Encontre usuários (u1) que usam o mesmo device (d) que outros,
            // E verifique se esses usuários transferiram dinheiro para uma conta (c2) de destino."
            String query = """
            MATCH (u1:Usuario)-[:USA_DEVICE]->(d:Device)<-[:USA_DEVICE]-(u2:Usuario)
            MATCH (u1)-[:POSSUI_CONTA]->(c1:Conta)-[t:TRANSFERIU]->(c2:Conta)
            WITH d, c2, count(DISTINCT u1) as qtd_laranjas, sum(t.valor) as total_desviado
            WHERE qtd_laranjas > 2
            RETURN d.id as device_chefe, c2.id as conta_laranja, total_desviado, qtd_laranjas
            ORDER BY total_desviado DESC
        """;

            com.redislabs.redisgraph.ResultSet result = graph.query("anti_fraude", query);

            while (result.hasNext()) {
                com.redislabs.redisgraph.Record rec = result.next();

                String device = rec.getString("device_chefe");
                String conta = rec.getString("conta_laranja");
                double valor = Double.valueOf(rec.getString("total_desviado"));
                long qtd = Double.valueOf(rec.getString("qtd_laranjas")).longValue();

                String motivo = "⚠️ FRAUDE CONFIRMADA: " + qtd + " usuários do Device " + device + " enviaram dinheiro para esta conta.";

                alertas.add(new com.projeto.detectorFraudes.dto.AlertaFinanceiroDTO(
                        "Grupo do Device: " + device,
                        "Laranja: " + conta,
                        valor,
                        motivo
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return alertas;
    }
}
