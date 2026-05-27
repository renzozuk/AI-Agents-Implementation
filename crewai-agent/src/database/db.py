import logging

import psycopg2

from src.database.db_config import load_db_config

config = load_db_config()
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger()

from psycopg2.extras import Json


def save_extracted_data(data):
    if data is None:
        logger.error("No data provided to save_extracted_data")
        return None

    result_id = None
    try:
        pdf_title = data.get('pdf_title')
        content = str(data)

        sql_query = """
                    INSERT INTO processed_extractions (content, pdf_title, metadata)
                    VALUES (%s, %s, %s) RETURNING id; \
                    """

        try:
            with psycopg2.connect(**config) as conn:
                with conn.cursor() as cur:
                    cur.execute(sql_query, (content, pdf_title, Json(data)))

                    row = cur.fetchone()
                    if row:
                        result_id = row[0]

                    conn.commit()

                    logger.info(f"Data saved successfully. ID: {result_id}")
        except (Exception, psycopg2.DatabaseError) as error:
            logger.error(f"Database error: {error}")

    except Exception as e:
        logger.error(f"Failed to prepare data: {str(e)}")

    return result_id

def save_email_data(data):
    result_id = None

    try:
        pdf_title = data.get('pdf_title')
        message_id = data.get('message_id')
        message_type = "EMAIL_SENT"
        recipient = data.get('recipient')
        status = "WAITING_RESPONSE"
        thread_id = data.get('thread_id')

        sql_query = """
                    INSERT INTO communications (pdf_title, message_id, message_type, status,recipient, thread_id)
                    VALUES (%s, %s, %s, %s, %s, %s) RETURNING id;
                    """

        try:
            with psycopg2.connect(**config) as conn:
                with conn.cursor() as cur:
                    cur.execute(sql_query, (pdf_title, message_id, message_type, status, recipient, thread_id))
                    row = cur.fetchone()
                    if row:
                        result_id = row[0]

            logger.info(f"Email data saved successfully. ID: {result_id}")

        except (Exception, psycopg2.DatabaseError) as error:
            logger.error(f"Erro ao inserir no Postgres: {error}")

    except Exception as e:
        logger.error(f"Falha geral no callback: {str(e)}")

    finally:
        return result_id


from psycopg2.extras import RealDictCursor


def get_email_data_by_pdf_title(pdf_title):
    sql_query = "SELECT * FROM communications WHERE pdf_title = %s"
    with psycopg2.connect(**config) as conn:
        with conn.cursor(cursor_factory=RealDictCursor) as cur:
            cur.execute(sql_query, (pdf_title,))
            return cur.fetchone()

def get_pdf_json(pdf_title: str):
    sql_query = "SELECT content FROM processed_extractions WHERE pdf_title = %s"
    with psycopg2.connect(**config) as conn:
        with conn.cursor(cursor_factory=RealDictCursor) as cur:
            cur.execute(sql_query, (pdf_title,))
            return cur.fetchone()


def update_pdf_json_data(pdf_title, pdf_json):
    """
    Atualiza os dados de conteúdo e metadados no banco de dados.
    """
    if not pdf_title or not pdf_json:
        logger.error("pdf_title ou pdf_json não foram fornecidos.")
        return False

    sql_query = """
                UPDATE processed_extractions
                SET content    = %s,
                    metadata   = %s,
                    updated_at = CURRENT_TIMESTAMP
                WHERE pdf_title = %s;
                """

    success = False
    try:
        content_str = str(pdf_json)

        metadata_json = Json(pdf_json)

        with psycopg2.connect(**config) as conn:
            with conn.cursor() as cur:
                cur.execute(sql_query, (content_str, metadata_json, pdf_title))

                if cur.rowcount > 0:
                    conn.commit()
                    logger.info(f"Dados atualizados com sucesso: {pdf_title}")
                    success = True
                else:
                    logger.warning(f"Nenhum registro encontrado para o título: {pdf_title}")

    except (Exception, psycopg2.DatabaseError) as error:
        logger.error(f"Erro ao atualizar dados: {error}")

    return success