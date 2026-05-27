from typing import Type

import requests
from crewai.tools import BaseTool
from dotenv import load_dotenv
from pydantic import BaseModel, Field
import os

load_dotenv()


class EmailToolInput(BaseModel):
    """Input schema for EmailSendingTool."""
    email_to: str = Field(..., description='E-mail address of the recipient')
    subject: str = Field(..., description='Subject of the email')
    body: str = Field(..., description="The content of the email to be sent")
    pdf_title: str = Field(..., description="Title of the PDF file")

class EmailToolOutput(BaseModel):
    """Output schema for the email sending result"""
    pdf_title: str = Field(..., description="Title of the PDF file")
    action_taken: str = Field(..., description="Whether an email was sent or skipped")
    recipient: str = Field(..., description="Email address of the recipient")
    status: str = Field(..., description="The result of the tool execution")
    message_id: str = Field(..., description="The message ID returned by the Email API")
    thread_id: str = Field(..., description="The thread ID returned by the Email API")

class EmailSendingTool(BaseTool):
    name: str = "Email Sending Tool"
    description: str = "Sends an email to a specific recipient. Useful for requesting missing contract data."
    args_schema: Type[BaseModel] = EmailToolInput

    def _run(self, email_to: str, subject: str, body: str, pdf_title: str) -> str:
        try:
            email_from = os.environ.get('SENDER_EMAIL')
            webhook_url = os.environ.get('WEBHOOK_BASE_URL') + "/" + pdf_title
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

            msg_id = response_data.get('messageId') or response_data.get('id') or 'N/A'
            th_id = response_data.get('threadId') or response_data.get('thread_id') or 'N/A'

            return f"Status: Success. message_id: {msg_id}, thread_id: {th_id}, recipient: {email_to}"

        except Exception as e:
            return f"Failed to send email: {str(e)}"