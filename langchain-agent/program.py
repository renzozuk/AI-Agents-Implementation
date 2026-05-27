import json
import os
import re

from dotenv import load_dotenv

import contract_object
import db
import send_email_tool

load_dotenv()

from langchain.agents import create_agent
from langchain_community.callbacks import get_openai_callback
from langchain_openai import ChatOpenAI
import pdf_reader_tool

openai_model = ChatOpenAI(model="gpt-5-nano", api_key=os.getenv("OPENAI_API_KEY"))
gemini_model = ChatOpenAI(base_url="https://generativelanguage.googleapis.com/v1beta/openai", model="gemini-2.5-flash", api_key=os.getenv("GEMINI_API_KEY"))

agent = create_agent(openai_model, tools=[pdf_reader_tool.read_pdf, send_email_tool.send_email])

schema_str = json.dumps(contract_object.FullExtractionSchema.model_json_schema(), indent=2)

prompt = f"""
    ### FERRAMENTAS DISPONÍVEIS
    1. 'pdf_reader': Faz uma busca semântica no PDF. Requer uma query em linguagem natural.
    2. 'send_email_tool': Envia o JSON final por e-mail.
    
    ### OBJETIVO
    Seu objetivo é extrair informações de um PDF para preencher um objeto JSON. 
    O esquema (estrutura) que você deve seguir é: {schema_str}
    
    ### DIRETRIZES DE EXECUÇÃO
    1. **DADOS, NÃO SCHEMA**: Não retorne as definições de tipo ($defs, properties, type). Retorne um JSON populado com os valores reais encontrados no documento.
    2. **BUSCA ATIVA**: Se um campo estiver vazio, use 'pdf_reader' para encontrá-lo.
    3. **FINALIZAÇÃO**: Quando coletar os dados ou atingir 15 tentativas, monte o JSON final preenchido e use a ferramenta 'send_email_tool'. 
    4. **COMPLETUDE**: Caso haja algum campo 'null' ou não preenchido, defina 'status_extracao' como 'INCOMPLETE', caso contrário defina como 'COMPLETE'.
    5. **FORMATO DO EMAIL**: O input da ferramenta 'send_email_tool' deve ser estritamente o objeto JSON com os dados extraídos, sem explicações adicionais.
    
    ### FORMATO DE RESPOSTA
    Pensamento: [Seu raciocínio sobre o que falta]
    Ação: [Qual ferramenta usar]
    Ação Input: [O que enviar para a ferramenta]
    Observação: [Resultado da ferramenta]
    ... (Repita até concluir)
"""

with get_openai_callback() as callback:
    response = agent.invoke({
        "messages": [{"role": "user", "content": prompt}]
    })
    print(f"Tokens Totais: {callback.total_tokens}")
    print(f"Tokens de Prompt: {callback.prompt_tokens}")
    print(f"Tokens de Resposta: {callback.completion_tokens}")
    print(f"Custo Total (USD): ${callback.total_cost}")

final_message = response["messages"][-1]
print(final_message)

ultimo_conteudo = response["messages"][-1].content

match = re.search(r'(\{.*\})', ultimo_conteudo, re.DOTALL)

if match:
    json_str = match.group(1).strip()

    last_brace = json_str.rfind('}')
    if last_brace != -1:
        json_str = json_str[:last_brace + 1]

    try:
        dados_da_acao = json.loads(json_str)

        if "body" in dados_da_acao:
            if isinstance(dados_da_acao["body"], str):
                json_extraido = json.loads(dados_da_acao["body"])
            else:
                json_extraido = dados_da_acao["body"]
        else:
            json_extraido = dados_da_acao

        print("✅ JSON Extraído com sucesso!")

        db.save_contract(json_extraido)

    except json.JSONDecodeError as e:
        print(f"❌ Erro de sintaxe no JSON: {e}")
        print(f"Trecho do erro: {json_str[:100]}...")
else:
    print("❌ Nenhum JSON encontrado na resposta.")
