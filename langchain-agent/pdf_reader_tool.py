import httpx
from langchain.tools import tool
from urllib.parse import quote


@tool
def read_pdf(query: str) -> str:
    """Faz uma requisição para uma API de terceiros para ler um PDF."""
    try:
        url = f"http://localhost:8282/api/search?query={quote(query)}"

        with httpx.Client() as client:
            response = client.get(url)
            response.raise_for_status()
            print(response.text)
            return response.text

    except httpx.RequestError as e:
        raise Exception(f"Erro na requisição HTTP: {e}")
    except Exception as e:
        raise Exception(f"Erro ao obter conteúdo PDF: {e}")
