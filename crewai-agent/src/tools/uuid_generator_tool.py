import uuid
from typing import Type

import psycopg2
from crewai.tools import BaseTool
from pydantic import BaseModel

from src.database.db_config import load_db_config


class UUIDGeneratorToolInput(BaseModel):
    pdf_title: str

class UUIDGeneratorToolOutput(BaseModel):
    uuid: str


def pdf_title_already_exists(pdf_title) -> bool:
    config = load_db_config()
    conn = psycopg2.connect(**config)
    cur = conn.cursor()

    query = "SELECT id FROM processed_extractions WHERE pdf_title = %s LIMIT 1;"
    cur.execute(query, (pdf_title,))
    result = cur.fetchone()

    cur.close()
    conn.close()

    return True if result else False

class UUIDGeneratorTool(BaseTool):
    name: str = "UUIDGeneratorTool"
    description: str = "Returns a pdf title with a generated UUID."
    args_schema: Type[BaseModel] = UUIDGeneratorToolInput

    def _run(self, pdf_title: str) -> str:

        uuid_generated = str(uuid.uuid4())
        while pdf_title_already_exists((pdf_title + "_" + uuid_generated)):
            uuid_generated = str(uuid.uuid4())

        return pdf_title + "_" + uuid_generated