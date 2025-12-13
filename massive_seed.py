from falkordb import FalkorDB
from faker import Faker
import random
import time
from tqdm import tqdm # Barra de progresso (pip install tqdm)

# CONFIGURAÇÕES
TOTAL_USUARIOS = 2000
TOTAL_TRANSACOES_NORMAIS = 10000
BATCH_SIZE = 500 # Tamanho do lote de inserção

db = FalkorDB(host='localhost', port=6379)
graph = db.select_graph('anti_fraude')
fake = Faker('pt_BR')

def batch_execute(query, data_list, desc):
    """Função auxiliar para inserir dados em lotes (Massive Insert)"""
    if not data_list: return
    
    total = len(data_list)
    with tqdm(total=total, desc=desc) as pbar:
        for i in range(0, total, BATCH_SIZE):
            batch = data_list[i : i + BATCH_SIZE]
            # UNWIND desenrola a lista dentro do banco (muito rápido)
            graph.query(query, {'batch': batch})
            pbar.update(len(batch))

print("🔥 -- INICIANDO GERAÇÃO DE DADOS MASSIVOS -- 🔥")

# 1. LIMPEZA
print("🧹 Limpando grafo antigo...")
graph.query("MATCH (n) DETACH DELETE n")

# 2. GERAR USUÁRIOS E CONTAS (Nós Base)
users_data = []
account_ids = [] # Para referência rápida

print(f"👥 Gerando {TOTAL_USUARIOS} usuários na memória...")
for i in range(TOTAL_USUARIOS):
    uid = f"U{i:05d}"
    acc_id = f"ACC{i:05d}"
    users_data.append({
        'uid': uid,
        'nome': fake.name(),
        'acc_id': acc_id,
        'device_id': fake.uuid4() # A maioria tem device único
    })
    account_ids.append(acc_id)

# Query de inserção de nós
q_nodes = """
UNWIND $batch as row
MERGE (u:Usuario {id: row.uid, nome: row.nome})
MERGE (c:Conta {id: row.acc_id})
MERGE (d:Device {id: row.device_id})
MERGE (u)-[:POSSUI_CONTA]->(c)
MERGE (u)-[:USA_DEVICE]->(d)
"""
batch_execute(q_nodes, users_data, "Inserindo Usuários/Contas")

# 3. GERAR TRANSAÇÕES LEGAIS (Ruído)
print("💸 Gerando transações legítimas aleatórias...")
transacoes_normais = []
t_base = int(time.time()) - 100000

for _ in range(TOTAL_TRANSACOES_NORMAIS):
    origem = random.choice(account_ids)
    destino = random.choice(account_ids)
    if origem == destino: continue
    
    transacoes_normais.append({
        'from': origem,
        'to': destino,
        'val': round(random.uniform(10, 5000), 2),
        'ts': t_base + random.randint(1, 100000)
    })

q_trans = """
UNWIND $batch as row
MATCH (a:Conta {id: row.from}), (b:Conta {id: row.to})
CREATE (a)-[:TRANSFERIU {valor: row.val, ts: row.ts, tipo: 'normal'}]->(b)
"""
batch_execute(q_trans, transacoes_normais, "Inserindo Transações Normais")

# --- AQUI COMEÇAM AS FRAUDES ESPECÍFICAS ---

print("\n🚨 INJETANDO CENÁRIOS DE FRAUDE...")

# CENÁRIO A: DEVICE FARM (1 Device <- 50 Usuários)
# Escolhemos um ID fixo e fazemos 50 usuários aleatórios usarem ele
fraud_device = "IPHONE_DO_CRIME_01"
farm_users = random.sample(users_data, 50) # Pega 50 users já existentes
farm_data = [{'uid': u['uid'], 'did': fraud_device} for u in farm_users]

q_farm = """
UNWIND $batch as row
MATCH (u:Usuario {id: row.uid})
MERGE (d:Device {id: row.did})
MERGE (u)-[:USA_DEVICE {risco: 'alto'}]->(d)
"""
batch_execute(q_farm, farm_data, "Criando Device Farm")

# CENÁRIO B: LAVAGEM EM ANEL (Ciclo Fechado)
# Cria 3 anéis diferentes de 5 pessoas
q_cycle = """
MATCH (c1:Conta {id: $id1}), (c2:Conta {id: $id2}), (c3:Conta {id: $id3}), (c4:Conta {id: $id4}), (c5:Conta {id: $id5})
CREATE (c1)-[:TRANSFERIU {valor: 50000, ts: $t}]->(c2)
CREATE (c2)-[:TRANSFERIU {valor: 49000, ts: $t+100}]->(c3)
CREATE (c3)-[:TRANSFERIU {valor: 48000, ts: $t+200}]->(c4)
CREATE (c4)-[:TRANSFERIU {valor: 47000, ts: $t+300}]->(c5)
CREATE (c5)-[:TRANSFERIU {valor: 46000, ts: $t+400}]->(c1)
"""
# Pegamos contas do fim da lista para não misturar muito
cycle_accs = account_ids[-20:] 
params = {
    'id1': cycle_accs[0], 'id2': cycle_accs[1], 'id3': cycle_accs[2], 
    'id4': cycle_accs[3], 'id5': cycle_accs[4], 't': int(time.time())
}
graph.query(q_cycle, params)
print("-> Ciclo de lavagem injetado (Contas finais da lista)")

# CENÁRIO C: SMURFING (Fan-Out -> Fan-In)
# A Conta X manda para 20 Mulas, que mandam para a Conta Y
mestre_origem = account_ids[10]
mestre_destino = account_ids[11]
mulas = account_ids[100:120] # 20 mulas

smurf_data = []
ts = int(time.time())
for mula in mulas:
    # Passo 1: Mestre -> Mula
    smurf_data.append({'from': mestre_origem, 'to': mula, 'val': 1000, 'ts': ts})
    # Passo 2: Mula -> Destino Final
    smurf_data.append({'from': mula, 'to': mestre_destino, 'val': 950, 'ts': ts + 60})

batch_execute(q_trans, smurf_data, "Criando Smurfing")

print("\n✅ CONCLUÍDO! Banco populado com sucesso.")