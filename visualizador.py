from falkordb import FalkorDB
from pyvis.network import Network
# 1. Conectar e Buscar Dados
db = FalkorDB(host='localhost', port=6379)
graph = db.select_graph('anti_fraude')

# Vamos visualizar apenas a FRAUDE DO DEVICE FARM
print("🎨 Gerando visualização da Fraude...")
query = """
    MATCH (u:Usuario)-[r:USA_DEVICE]->(d:Device {id: 'IPHONE_DO_CRIME_01'})
    RETURN u.nome, d.id
"""
result = graph.query(query)

# 2. Configurar o Desenho (PyVis)
net = Network(height='750px', width='100%', bgcolor='#222222', font_color='white')

# 3. Adicionar Nós e Arestas
for record in result.result_set:
    user_name = record[0]
    device_id = record[1]
    
    # Adiciona o Nó do Celular (Vermelho e Grande)
    net.add_node(device_id, label="DEVICE FRAUDULENTO", color='red', size=30, shape='box')
    
    # Adiciona o Nó do Usuário (Azul)
    net.add_node(user_name, label=user_name, color='#00ff41', size=10)
    
    # Liga os dois
    net.add_edge(user_name, device_id, title="Acessou")

# 4. Salvar e Abrir
net.force_atlas_2based() # Algoritmo de física para espalhar bonitinho
net.show('fraude_report.html', notebook=False)