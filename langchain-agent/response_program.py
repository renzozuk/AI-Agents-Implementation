import json
import os

from dotenv import load_dotenv

load_dotenv()

from fastapi import FastAPI, BackgroundTasks, status, Body
from langchain.agents import create_agent
from langchain_community.callbacks import get_openai_callback
from langchain_openai import ChatOpenAI

import contract_object
import db

import send_email_tool

app = FastAPI()

openai_model = ChatOpenAI(model="gpt-5-nano", api_key=os.getenv("OPENAI_API_KEY"))
agent = create_agent(openai_model, tools=[send_email_tool.send_email])

schema_str = json.dumps(contract_object.FullExtractionSchema.model_json_schema(), indent=2)

@app.post("/webhook", status_code=status.HTTP_202_ACCEPTED)
async def response(content: str = Body(..., embed=True)):


    print(f"Processando email")

    contract_data = db.get_contract_data_from_last_email()

    prompt = f"""
        ### FERRAMENTA DISPONÍVEL
        1. 'send_email_tool': Envia o JSON final por e-mail.

        ### DADOS
        O esquema (estrutura) que você deve seguir é: {schema_str}
        Contrato: {contract_data}
        O último email enviado pelo usuário é: {content}

        ### PRÉ-OBJETIVO
        Você irá receber um contrato com dados presentes e dados faltantes.
        Você também irá receber um email de um usuário explicando o que falta no contrato.

        ### OBJETIVO
        O seu objetivo é estruturar um JSON com os dados já presentes e o dado recebido no email do usuário.

        ### DIRETRIZES DE EXECUÇÃO
        1. **DADOS, NÃO SCHEMA**: Não retorne as definições de tipo ($defs, properties, type). Retorne um JSON populado com os valores reais encontrados no documento.
        2. **BUSCA ATIVA**: Se um campo estiver vazio, use 'pdf_reader' para encontrá-lo.
        3. **COMPLETUDE**: Caso haja algum campo 'null' ou não preenchido, defina 'status_extracao' como 'INCOMPLETE', caso contrário defina como 'COMPLETE'.
        4. **FORMATO DO EMAIL**: O input da ferramenta 'send_email_tool' deve ser estritamente o objeto JSON com os dados extraídos, sem explicações adicionais.

        ### FORMATO DE RESPOSTA
        Pensamento: [Seu raciocínio sobre o que falta]
        Ação: [Qual ferramenta usar]
        Ação Input: [O que enviar para a ferramenta]
        Observação: [Resultado da ferramenta]
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

    return
