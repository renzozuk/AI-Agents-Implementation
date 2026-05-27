import ast
import logging
import os

import requests
from crewai import Agent, LLM, Task, Crew, Process
from crewai.project import CrewBase, before_kickoff, agent, task, crew
from dotenv import load_dotenv

from src.utils.callbacks import update_pdf_json_data_callback
from src.models.contract_object import FullExtractionSchema
from src.database.db import get_email_data_by_pdf_title, get_pdf_json
from src.tools.reply_email_tool import ReplyEmailTool

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger()

load_dotenv()

@CrewBase
class WebhookProcessorCrew:

    agents_config = '../config/agents.yaml'
    tasks_config = '../config/task_completion.yaml'

    openai_api_key = os.environ.get("OPENAI_API_KEY")
    env_model = os.environ.get("OPENAI_MODEL")
    openai_llm = LLM(
        model=env_model,
        api_key=openai_api_key,
    )

    @before_kickoff
    def fetch_data(self, inputs):
        pdf_title = inputs['pdf_title']
        logger.info(f"Fetching data for pdf: {pdf_title}")

        email_data = get_email_data_by_pdf_title(pdf_title)
        if not email_data:
            logger.error(f"Nenhum dado encontrado no banco para o pdf: {pdf_title}")
            return inputs

        thread_id = email_data.get('thread_id')
        recipient = email_data.get('recipient')

        if not thread_id or thread_id == 'N/A' or not recipient or recipient == 'N/A':
            logger.error(f"Dados de e-mail incompletos no banco para o pdf: {pdf_title}. Thread ID: {thread_id}, Recipient: {recipient}")
            return inputs

        email_api_url = f"{os.environ.get('EMAIL_API_BASE_URL')}/{thread_id}/last-response"
        params = {'from': recipient}

        try:
            response = requests.get(email_api_url, params=params)
            response.raise_for_status()
            data = response.json()

            logger.info(f"Resposta da API: {data}")

        except Exception as e:
            logger.error(f"Falha na chamada da API para {email_api_url}: {e}")
            return inputs

        db_json = get_pdf_json(pdf_title)
        if not db_json or not db_json.get('content'):
            logger.error(f"Nenhum conteúdo JSON encontrado para o pdf: {pdf_title}")
            return inputs

        try:
            content = db_json['content']
            if isinstance(content, str):
                partial_json = ast.literal_eval(content)
            else:
                partial_json = content
        except Exception as e:
            logger.error(f"Erro ao parsear o conteúdo JSON: {e}")
            return inputs

        logger.info("Data fetched successfully.")

        inputs['partial_json'] = partial_json
        inputs['last_response_email'] = data
        return inputs

    @agent
    def data_completion_agent(self) -> Agent:
        return Agent(
            config=self.agents_config['data_completion_agent'],
            tools=[ReplyEmailTool()],
            llm=self.openai_llm,
            max_rpm=5,
            verbose=True
    )

    @task
    def data_completion_task(self) -> Task:
        return Task(
            config=self.tasks_config['data_completion_task'],
            output_file='data/data_completion.json',
            output_json=FullExtractionSchema,
            callback=update_pdf_json_data_callback
        )

    @crew
    def crew(self) -> Crew:
        return Crew(
            agents=self.agents,
            tasks=self.tasks,
            process=Process.sequential,
        )