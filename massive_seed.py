from falkordb import FalkorDB
from faker import Faker
import random
import time
from tqdm import tqdm

# CONFIGURAÇÕES
TOTAL_USUARIOS = 2000
TOTAL_TRANSACOES_NORMAIS = 5000
BATCH_SIZE = 500

db = FalkorDB(host='localhost', port=6379)
graph = db.select_graph('anti_fraude')
fake = Faker('pt_BR')

def batch_execute(query, data_list, desc):
    if not data_list: return
    total = len(data_list)
    with tqdm(total=total, desc=desc) as pbar:
        for i in range(0, total, BATCH_SIZE):
            batch = data_list[i : i + BATCH_SIZE]
            graph.query(query, {'batch': batch})
            pbar.update(len(batch))

print("🔥 -- GERANDO DADOS HÍBRIDOS (DEVICE + DINHEIRO) -- 🔥")

# 1. LIMPEZA
graph.query("MATCH (n) DETACH DELETE n")

# 2. GERAR USUÁRIOS E CONTAS
users_data = []
account_ids = []

print(f"👥 Gerando {TOTAL_USUARIOS} usuários...")
for i in range(TOTAL_USUARIOS):
    uid = f"U{i:05d}"
    acc_id = f"ACC{i:05d}"
    users_data.append({
        'uid': uid,
        'nome': fake.name(),
        'acc_id': acc_id,
        'device_id': fake.uuid4()
    })
    account_ids.append(acc_id)

q_nodes = """
UNWIND $batch as row
MERGE (u:Usuario {id: row.uid, nome: row.nome})
MERGE (c:Conta {id: row.acc_id})
MERGE (d:Device {id: row.device_id})
MERGE (u)-[:POSSUI_CONTA]->(c)
MERGE (u)-[:USA_DEVICE]->(d)
"""
batch_execute(q_nodes, users_data, "Criando Nós")

# 3. TRANSAÇÕES NORMAIS (RUÍDO)
transacoes_normais = []
t_base = int(time.time()) - 100000

for _ in range(TOTAL_TRANSACOES_NORMAIS):
    origem = random.choice(account_ids)
    destino = random.choice(account_ids)
    if origem == destino: continue
    transacoes_normais.append({
        'from': origem, 'to': destino,
        'val': round(random.uniform(10, 500), 2),
        'ts': t_base + random.randint(1, 100000)
    })

q_trans = """
UNWIND $batch as row
MATCH (c1:Conta {id: row.from}), (c2:Conta {id: row.to})
CREATE (c1)-[:TRANSFERIU {valor: row.val, ts: row.ts}]->(c2)
"""
batch_execute(q_trans, transacoes_normais, "Transações Normais")

# --- FRAUDES ---

print("\n🚨 INJETANDO CENÁRIOS DE FRAUDE...")

# A: DEVICE FARM SIMPLES
fraud_device = "IPHONE_DO_CRIME_01"
farm_users = users_data[:50] # Primeiros 50 users
farm_data = [{'uid': u['uid'], 'did': fraud_device} for u in farm_users]
q_farm = "UNWIND $batch as row MATCH (u:Usuario {id: row.uid}) MERGE (d:Device {id: row.did}) MERGE (u)-[:USA_DEVICE]->(d)"
batch_execute(q_farm, farm_data, "A: Device Farm")

# B: ANEL DE LAVAGEM (A->B->C->A)
accs = account_ids[-10:] # Últimas 10 contas
q_cycle = """
MATCH (c1:Conta {id: $id1}), (c2:Conta {id: $id2}), (c3:Conta {id: $id3})
CREATE (c1)-[:TRANSFERIU {valor: 50000, tipo: 'lavagem'}]->(c2)
CREATE (c2)-[:TRANSFERIU {valor: 49500, tipo: 'lavagem'}]->(c3)
CREATE (c3)-[:TRANSFERIU {valor: 49000, tipo: 'lavagem'}]->(c1)
"""
graph.query(q_cycle, {'id1': accs[0], 'id2': accs[1], 'id3': accs[2]})
print("-> B: Ciclo de Lavagem Injetado")

# C: FRAUDE HÍBRIDA (O SANTO GRAAL) 🏆
# Cenário: 15 usuários usam o MESMO celular E mandam dinheiro para a MESMA conta laranja.
# Isso prova que o dono do celular controla todas as contas.

device_hibrido = "SAMSUNG_DO_CHEFE"
conta_laranja = account_ids[100] # Uma conta alvo qualquer
grupo_criminoso = users_data[100:115] # 15 usuários do meio da lista

hibrido_data = []
transacoes_hibridas = []

for u in grupo_criminoso:
    # 1. Todos usam o mesmo device
    hibrido_data.append({'uid': u['uid'], 'did': device_hibrido})
    # 2. Todos mandam dinheiro para o Laranja
    transacoes_hibridas.append({
        'from': u['acc_id'],
        'to': conta_laranja,
        'val': 9999.00
    })

# Executa conexões de device
batch_execute(q_farm, hibrido_data, "C: Device do Chefe")
# Executa transferências financeiras
batch_execute(q_trans, transacoes_hibridas, "C: Pagamentos ao Laranja")

print("\n✅ BANCO POPULADO! AGORA RODE O JAVA.")