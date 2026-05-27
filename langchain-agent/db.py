import json
import os
import psycopg2
from psycopg2.extras import Json, RealDictCursor

from dotenv import load_dotenv


def save_contract(json_data):
    load_dotenv()

    if isinstance(json_data, str):
        json_data = json.loads(json_data)

    try:
        with psycopg2.connect(
            dbname=os.getenv("DB_DATABASE"),
            user=os.getenv("DB_USERNAME"),
            password=os.getenv("DB_PASSWORD"),
            host=os.getenv("DB_HOST"),
            port=os.getenv("DB_PORT")
        ) as conn:
            with conn.cursor() as cur:
                contrato = json_data['contrato']
                empresa = contrato['empresa']

                insert_query = """
                INSERT INTO contratos_extraidos (cnpj, razao_social, data_inicio, data_fim, status_extracao, dados_completos)
                VALUES (%s, %s, %s, %s, %s, %s)
                ON CONFLICT (cnpj) DO UPDATE SET dados_completos = EXCLUDED.dados_completos;
                """

                cur.execute(insert_query, (
                    empresa['cnpj'],
                    empresa['razaoSocial'],
                    contrato['dataInicioVigencia'],
                    contrato['dataFimVigencia'],
                    json_data['status_extracao'],
                    Json(json_data)
                ))

                print("✅ Contrato salvo com sucesso!")

    except Exception as e:
        print(f"❌ Erro de conexão ou execução: {e}")


def get_contract_data_from_last_email():
    sql_query = "SELECT * FROM contratos_extraidos ORDER BY criado_em DESC LIMIT 1;"

    with psycopg2.connect(
            dbname=os.getenv("DB_DATABASE"),
            user=os.getenv("DB_USERNAME"),
            password=os.getenv("DB_PASSWORD"),
            host=os.getenv("DB_HOST"),
            port=os.getenv("DB_PORT")
    ) as conn:
        with conn.cursor() as cur:
            with conn.cursor(cursor_factory=RealDictCursor) as cur:
                cur.execute(sql_query)
                return cur.fetchone()

