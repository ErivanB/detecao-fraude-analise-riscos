# 📊 Projeto de Detecção de Fraude e Análise de Riscos

https://img.shields.io/badge/Java-17-orange
https://img.shields.io/badge/Spring%2520Boot-3.1.5-green
https://img.shields.io/badge/FalkorDB-Latest-blue
https://img.shields.io/badge/Docker-%E2%9C%93-blue
https://img.shields.io/badge/License-MIT-lightgrey

Sistema completo de detecção de fraudes e análise de riscos utilizando grafos para identificar padrões suspeitos em transações financeiras.

## 🎯 Objetivo
Desenvolver uma solução que utilize um banco de dados em grafo (FalkorDB) para:

Detectar transações fraudulentas em tempo real

Analisar riscos de clientes e operações

Identificar relacionamentos suspeitos entre entidades

Fornecer insights para prevenção de fraudes

## 🏗️ Arquitetura

text
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   Aplicação     │────▶│    FalkorDB     │────▶│     Docker      │
│   Spring Boot   │     │   (RedisGraph)  │     │   Containers    │
└─────────────────┘     └─────────────────┘     └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                          ┌─────────────────┐
                          │  API REST CRUD  │
                          │   + Análise     │
                          └─────────────────┘
📁 Estrutura do Projeto
text
detecao-fraude-analise-riscos/
├── docker-compose.yml          # Orquestração de containers
├── Dockerfile                  # Build da aplicação
├── pom.xml                     # Dependências Maven
├── src/
│   ├── main/
│   │   ├── java/com/fraudedetection/
│   │   │   ├── FraudDetectionApplication.java
│   │   │   ├── config/           # Configurações
│   │   │   ├── model/            # Entidades (Cliente, Transacao)
│   │   │   ├── repository/       # Acesso ao FalkorDB
│   │   │   ├── service/          # Lógica de negócio
│   │   │   └── controller/       # Endpoints REST
│   │   └── resources/
│   │       └── application.properties
└── README.md
🚀 Funcionalidades
🔍 CRUD Completo
Clientes: Cadastro, consulta, atualização e remoção

Transações: Registro, análise e monitoramento

Análises de Risco: Score de risco e recomendações

## 🛡️ Detecção de Fraudes
Regras de negócio para identificar padrões suspeitos

Score de probabilidade de fraude (0-100%)

Alertas automáticos para transações suspeitas

Análise de relacionamentos entre entidades

## 📊 Análise de Riscos
Score de risco por cliente (0-100)

Classificação (BAIXO, MÉDIO, ALTO, CRÍTICO)

Fatores de risco identificados

Recomendações personalizadas

## 🛠️ Tecnologias Utilizadas

Tecnologia	Versão	Finalidade
Java	17	Linguagem principal
Spring Boot	3.1.5	Framework backend
FalkorDB	Latest	Banco de dados em grafo
Maven	3.8+	Gerenciamento de dependências
Docker	Latest	Containerização
Docker Compose	Latest	Orquestração
Jedis	4.4.3	Cliente Redis
JRedisGraph	2.6.0	Driver FalkorDB
Lombok	1.18.30	Redução de boilerplate
SpringDoc	2.2.0	Documentação OpenAPI


## 📋 Pré-requisitos
Docker e Docker Compose instalados

Java JDK 17 (se for executar localmente)

Maven 3.8+ (se for executar localmente)

Git (para clonar o repositório)

4GB RAM mínima recomendada

## 🚀 Como Executar
Método 1: Docker Compose (Recomendado)
bash
# 1. Clone o projeto (ou extraia os arquivos)
git clone [url-do-projeto]
cd detecao-fraude-analise-riscos

# 2. Execute com Docker Compose
docker-compose up -d

# 3. Acesse a aplicação
# API: http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
Método 2: Execução Local com Maven
bash
# 1. Inicie o FalkorDB
docker-compose up -d falkordb

# 2. Execute a aplicação
mvn clean spring-boot:run

# 3. Acesse em http://localhost:8080
Método 3: Build Docker Manual
bash
# 1. Build da imagem
docker build -t fraude-app .

# 2. Execute o container
docker run -p 8080:8080 \
  -e SPRING_FALKORDB_HOST=localhost \
  -e SPRING_FALKORDB_PORT=6379 \
  fraude-app
## 📡 Endpoints da API

### 👤 Clientes

Método	Endpoint	Descrição
POST	/api/clientes	Criar novo cliente
GET	/api/clientes	Listar todos os clientes
GET	/api/clientes/{cpf}	Buscar cliente por CPF
PUT	/api/clientes/{cpf}	Atualizar cliente
DELETE	/api/clientes/{cpf}	Remover cliente
GET	/api/clientes/risco	Buscar por faixa de risco

💳 Transações

Método	Endpoint	Descrição
POST	/api/transacoes	Registrar nova transação
GET	/api/transacoes	Listar todas as transações
GET	/api/transacoes/{id}	Buscar transação por ID
GET	/api/transacoes/cliente/{cpf}	Transações por cliente
GET	/api/transacoes/suspeitas	Listar transações suspeitas
PUT	/api/transacoes/{id}	Atualizar transação
POST	/api/transacoes/{id}/analise	Marcar análise de fraude

🎮 Testando a API

Usando cURL:
bash
# 1. Criar um cliente
curl -X POST http://localhost:8080/api/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "cpf": "12345678901",
    "nome": "Maria Santos",
    "email": "maria@email.com",
    "telefone": "11988887777",
    "rendaMensal": 7500.00,
    "scoreRisco": 85,
    "endereco": "Av. Paulista, 1000",
    "profissao": "Arquiteta",
    "ativo": true
  }'

# 2. Listar clientes
curl http://localhost:8080/api/clientes

# 3. Criar transação
curl -X POST http://localhost:8080/api/transacoes \
  -H "Content-Type: application/json" \
  -d '{
    "clienteCpf": "12345678901",
    "valor": 12000.00,
    "tipo": "TRANSFERENCIA",
    "categoria": "INVESTIMENTO",
    "estabelecimento": "Corretora ABC",
    "localizacao": "Exterior",
    "dispositivo": "Web Browser",
    "ip": "200.150.100.50"
  }'

# 4. Ver transações suspeitas
curl http://localhost:8080/api/transacoes/suspeitas
Usando Swagger UI:
Acesse http://localhost:8080/swagger-ui.html para interface interativa.

## 🔧 Regras de Detecção de Fraude
O sistema aplica as seguintes regras para identificar transações suspeitas:

Valor elevado: Transações acima de R$ 10.000 (+40% risco)

Localização internacional: Operações no exterior (+30% risco)

Dispositivo desconhecido: (+20% risco)

IP interno: (-10% risco - redutor)

Classificação:

0-50%: Baixo risco → Aprovação automática

51-70%: Risco moderado → Análise recomendada

71-100%: Alto risco → Bloqueio automático

## 📊 Modelo de Dados no Grafo
text
┌──────────┐       ┌─────────────┐
│ Cliente  │──────▶│ Transação   │
│ (Nó)     │       │ (Nó)        │
└──────────┘       └─────────────┘
     │                  │
     │ REALIZOU         │ TEM
     ↓                  ↓
┌──────────┐       ┌─────────────┐
│ Histórico│       │  Análise    │
│          │       │  de Risco   │
└──────────┘       └─────────────┘
Exemplo de Consulta Cypher no FalkorDB:
cypher
-- Buscar clientes com transações suspeitas
MATCH (c:Cliente)-[:REALIZOU]->(t:Transacao)
WHERE t.suspeita = true
RETURN c.nome, t.valor, t.probabilidadeFraude
ORDER BY t.probabilidadeFraude DESC

-- Análise de relacionamentos suspeitos
MATCH (c1:Cliente)-[:REALIZOU]->(t:Transacao)<-[:REALIZOU]-(c2:Cliente)
WHERE c1 <> c2 AND t.suspeita = true
RETURN c1.nome, c2.nome, count(t) as transacoes_suspeitas

🐛 Solução de Problemas
Problema: FalkorDB não inicia
bash
# Verificar logs
docker-compose logs falkordb

# Verificar portas em uso
sudo lsof -i :6379

# Reiniciar container
docker-compose down
docker-compose up -d falkordb
Problema: Aplicação não conecta ao FalkorDB
bash
# Verificar se o FalkorDB está respondendo
docker exec falkordb redis-cli -a SenhaForte123 PING

# Verificar variáveis de ambiente
docker-compose config
Problema: Erro de compilação Maven
bash
# Limpar cache do Maven
mvn clean

# Baixar dependências novamente
mvn dependency:resolve

# Ignorar testes
mvn clean install -DskipTests
📈 Próximas Melhorias
Dashboard Web para visualização de dados

Machine Learning para detecção mais precisa

WebSocket para alertas em tempo real

Integração com sistemas bancários

Relatórios PDF/Excel

Autenticação JWT

Cache Redis para performance

🤝 Contribuindo
Fork o projeto

Crie uma branch (git checkout -b feature/nova-funcionalidade)

Commit suas mudanças (git commit -m 'Add nova funcionalidade')

Push para a branch (git push origin feature/nova-funcionalidade)

Abra um Pull Request

📝 Licença
Este projeto está licenciado sob a licença MIT. Veja o arquivo LICENSE para detalhes.

👥 Autores
Desenvolvedor - Implementação do sistema

Analista de Fraude - Regras de negócio

DBA - Otimização de consultas no grafo
