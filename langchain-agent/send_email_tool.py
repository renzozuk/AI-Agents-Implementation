import os
import requests
from langchain.tools import tool
from dotenv import load_dotenv

@tool
def send_email(email_to: str, subject: str, body: str) -> str:
    """Envia o JSON para um e-mail encontrado no documento PDF, nunca peça ao usuário."""
    load_dotenv()

    try:
        email_from = os.environ.get('SENDER_EMAIL')
        webhook_url = os.environ.get('WEBHOOK_BASE_URL') + "/contrato_es"
        sender_role = os.environ.get('SENDER_ROLE')

        message = {
            'from': email_from,
            'to': email_to,
            'subject': subject,
            'body': body,
            'webhookUrl': webhook_url,
            'senderRole': sender_role
        }

        email_api_base_url = os.environ.get('EMAIL_API_BASE_URL')
        email_api_url = f"{email_api_base_url}/send"
        response = requests.post(email_api_url, json=message)

        response.raise_for_status()

        response_data = response.json()

        return response_data

    except Exception as e:
        raise Exception(f"Erro ao enviar e-mail: {e}")