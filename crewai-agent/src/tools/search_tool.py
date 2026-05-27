import os
from typing import Type

import requests
from crewai.tools import BaseTool
from dotenv import load_dotenv
from pydantic import BaseModel, Field

load_dotenv()

class SearchToolInput(BaseModel):
    query: str = Field(..., description="A query that defines what to search for.")

class SearchTool(BaseTool):
    name: str = "SearchContractAPI"
    description: str = ("Search for specific clauses and data within contract documents using semantic search.")
    args_schema: Type[BaseModel] = SearchToolInput

    def _run(self, query: str) -> str:
        url = "http://localhost:8282/api/search"
        try:
            response = requests.get(url, params={"query": query}, timeout=10)
            response.raise_for_status()
            results = response.json()

            if not results:
                return "Nenhuma informação encontrada."

            context = ""
            for res in results:
                context += f"[Documento: {res['metadata'].get('file_name')}] [Score: {res['score']}]\n"
                context += f"Conteúdo: {res['content']}\n\n"
            return context
        except Exception as e:
            return f"Erro ao acessar API: {str(e)}"