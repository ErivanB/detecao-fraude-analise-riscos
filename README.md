# 🕵️‍♂️ Detector de Fraudes Financeiras com Grafos (FalkorDB + Spring Boot)

![Status](https://img.shields.io/badge/Status-Concluído-success)
![Java](https://img.shields.io/badge/Backend-Java%2017%20%7C%20Spring%20Boot-orange)
![Redis](https://img.shields.io/badge/DB-Redis%20%7C%20FalkorDB-red)
![Python](https://img.shields.io/badge/Data-Python%20Faker-blue)

Este projeto é uma prova de conceito (PoC) de um sistema de detecção de fraudes em tempo real. Ele utiliza um **Banco de Dados Orientado a Grafos** para identificar padrões complexos de relacionamento entre Usuários, Dispositivos e Contas Bancárias que seriam invisíveis em bancos SQL tradicionais.

---

## 🚨 Cenários de Fraude Detectados

O sistema foi programado para identificar automaticamente 3 tipologias criminosas:

1.  **📱 Device Farm (Fraude de Identidade):**
    * **O Padrão:** Um único dispositivo físico sendo utilizado por dezenas de usuários diferentes.
    * **Visualização no Grafo:** Formato de "Estrela" (Um nó central com vários nós periféricos).
2.  **💸 Lavagem de Dinheiro (Ciclos):**
    * **O Padrão:** O dinheiro sai da Conta A, passa pela B e C, e retorna para A, numa tentativa de ocultar a origem.
    * **Visualização no Grafo:** Formato de "Triângulo" ou Ciclo Fechado.
3.  **🕵️ Fraude Híbrida (O Chefe e os Laranjas):**
    * **O Padrão:** Usuários que compartilham o dispositivo do "Chefe" enviam dinheiro sistematicamente para uma "Conta Laranja".
    * **Visualização no Grafo:** Formato de "Funil" (Vários usuários convergindo financeiramente para um ponto).

---

## 🛠️ Tecnologias

* **Banco de Dados:** Redis Stack (Módulo FalkorDB/RedisGraph).
* **Backend/Frontend:** Java 17, Spring Boot 3, Vaadin Flow.
* **Injeção de Dados:** Python 3 (Script gerador de massa de dados).
* **Linguagem de Consulta:** Cypher Query Language.

---

## 🚀 Como Executar o Projeto

### Passo 1: Subir o Banco de Dados (Docker)
```bash
docker run -p 6379:6379 -it --rm falkordb/falkordb

Passo 2: Gerar a Massa de Dados (Python)
Bash
# Instale as dependências
py -m install falkordb faker tqdm

# Rode o gerador
python massive_seed.py
Passo 3: Rodar o Dashboard (Java)

Bash
./mvnw spring-boot:run
Acesse o painel em: http://localhost:8081

🔎 Manual da Investigação (Queries)
Abaixo estão os comandos para visualizar os grafos diretamente no terminal (redis-cli) ou no RedisInsight.

1.Visão Geral (Amostra do Caos)
GRAPH.QUERY anti_fraude "MATCH p=(u:Usuario)-[:POSSUI_CONTA]->(c:Conta)-[:TRANSFERIU]->(dest:Conta) RETURN p LIMIT 1000"

2.Grafo de Dispositivos (Quem usa qual aparelho) 📱
GRAPH.QUERY anti_fraude "MATCH p=(u:Usuario)-[:USA_DEVICE]->(d:Device) RETURN p LIMIT 1000"

3. Grafo de Transferências (O Fluxo do Dinheiro) 💸
GRAPH.QUERY anti_fraude "MATCH p=(c1:Conta)-[:TRANSFERIU]->(c2:Conta) RETURN p LIMIT 1000"

4.Filtrar usuarios que Apontando parao Chefe do "Device Farm"
GRAPH.QUERY anti_fraude "MATCH p=(u:Usuario)-[:USA_DEVICE]->(d:Device {id: 'IPHONE_DO_CRIME_01'}) RETURN p"

5.Visualizando a Lavagem de Dinheiro (O Triângulo)
GRAPH.QUERY anti_fraude "MATCH p=(c1:Conta)-[:TRANSFERIU]->(c2:Conta)-[:TRANSFERIU]->(c3:Conta)-[:TRANSFERIU]->(c1) WHERE c1 <> c2 AND c2 <> c3 RETURN p"

6. O Grafo "Laranjas e Chefe" (Formato de Funil/Ponte)
GRAPH.QUERY anti_fraude "MATCH p1=(u:Usuario)-[:USA_DEVICE]->(d:Device {id: 'SAMSUNG_DO_CHEFE'}) MATCH p2=(u)-[:POSSUI_CONTA]->(c:Conta)-[:TRANSFERIU]->(laranja:Conta) RETURN p1, p2"

7. Visualizando a Fraude Híbrida (O Chefe e os Laranjas)
Esta query cruza duas informações: conexões no celular do chefe (p1) E transferências para laranjas (p2).
GRAPH.QUERY anti_fraude "MATCH p1=(u:Usuario)-[:USA_DEVICE]->(d:Device {id: 'SAMSUNG_DO_CHEFE'}) MATCH p2=(u)-[:POSSUI_CONTA]->(c:Conta)-[:TRANSFERIU]->(laranja:Conta) RETURN p1, p2"

8.Grafo dos Bloqueados (A Lista Negra) 🚫
GRAPH.QUERY anti_fraude "MATCH (n) WHERE n.status = 'BLOQUEADO' OR n.status = 'BLOCKED' RETURN n"

📸 Screenshots
<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/dd8968c7-1a1d-469b-9a10-b4e4cde81190" />
<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/00e9d709-857d-4bc6-8573-403f396ec19b" />
<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/d79ff6ad-3798-4b28-a834-244936d5e974" />
<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/9cec68f6-a44b-4864-8f6d-2d45855c8d99" />
<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/624d5925-691f-4200-9f86-7f4fbcfa6932" />
<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/fc09965b-96f2-45f1-ad52-fd2b716f7bd0" />
<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/9eb90d48-cd25-4edf-a726-b0fe9ee981c1" />
<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/628821e1-831f-4328-9e38-065ed2daf0fb" />







