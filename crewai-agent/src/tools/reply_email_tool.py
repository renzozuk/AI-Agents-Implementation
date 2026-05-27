from typing import Type

import requests
from crewai.tools import BaseTool
from dotenv import load_dotenv
from pydantic import BaseModel, Field
import os

load_dotenv()


class ReplyEmailToolInput(BaseModel):
    """Input schema for ReplyEmailToolInput."""
    message_id: str = Field(..., description='Message id of the email to reply to')
    body: str = Field(..., description="The content of the email to be sent")

class ReplyEmailToolOutput(BaseModel):
    """Output schema for the reply email result"""
    recipient: str = Field(..., description="Email address of the recipient")
    status: str = Field(..., description="The result of the tool execution")
    message_id: str = Field(..., description="The message ID returned by the Email API")
    thread_id: str = Field(..., description="The thread ID returned by the Email API")

class ReplyEmailTool(BaseTool):
    name: str = "Reply Email Tool"
    description: str = "Reply to an email with a specific body."
    args_schema: Type[BaseModel] = ReplyEmailToolInput

    def _run(self, message_id: str, body: str) -> str:
        try:
            sender_role = os.environ.get('SENDER_ROLE')
            message = {
                'body': body,
                'senderRole': sender_role
            }

            email_api_base_url = os.environ.get('EMAIL_API_BASE_URL')
            email_api_url = f"{email_api_base_url}/reply/{message_id}"
            response = requests.post(email_api_url, json=message)

            response.raise_for_status()
            response_data = response.json()

            new_msg_id = response_data.get('messageId') or response_data.get('id') or 'N/A'
            th_id = response_data.get('threadId') or response_data.get('thread_id') or 'N/A'
            dest = response_data.get('recipient') or response_data.get('to') or 'N/A'

            return f"Status: Success. message_id: {new_msg_id}, thread_id: {th_id}, recipient: {dest}"

        except Exception as e:
            return f"Failed to reply email: {str(e)}"