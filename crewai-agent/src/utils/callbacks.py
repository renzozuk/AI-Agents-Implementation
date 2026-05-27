from dotenv import load_dotenv
import logging

from src.database.db import save_extracted_data, save_email_data, update_pdf_json_data

logger = logging.getLogger()

load_dotenv()

def save_extracted_data_callback(task_output):
    """
        Callback to persist extracted data to Postgres.
    """
    if task_output.pydantic:
        data = task_output.pydantic.model_dump()
    elif task_output.json_dict:
        data = task_output.json_dict
    else:
        logger.error("Invalid pydantic or JSON output.")
        return None

    return save_extracted_data(data)


def save_email_data_callback(task_output):
    """
        Callback to persist email data to Postgres.
    """
    if task_output.pydantic:
        data = task_output.pydantic.model_dump()
    elif task_output.json_dict:
        data = task_output.json_dict
    else:
        logger.error("Invalid pydantic or JSON output.")
        return None

    return save_email_data(data)

def update_pdf_json_data_callback(task_output):
    if task_output.pydantic:
        data = task_output.pydantic.model_dump()
    elif task_output.json_dict:
        data = task_output.json_dict
    else:
        logger.error("Invalid pydantic or JSON output.")
        return None

    return update_pdf_json_data(data.get('pdf_title'), data)