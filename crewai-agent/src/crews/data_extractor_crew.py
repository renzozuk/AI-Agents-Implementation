import os
from pathlib import Path

from crewai import LLM, Agent, Task, Crew, Process
from crewai.project import CrewBase, agent, task, crew
from dotenv import load_dotenv

import logging
from src.utils.callbacks import save_extracted_data_callback, save_email_data_callback
from src.models.contract_object import FullExtractionSchema
from src.tools.email_send_tool import EmailSendingTool, EmailToolOutput
from src.tools.search_tool import SearchTool
from src.tools.uuid_generator_tool import UUIDGeneratorTool

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger()
load_dotenv()

@CrewBase
class DataExtractorCrew():

    agents_config = '../config/agents.yaml'
    tasks_config = '../config/tasks.yaml'

    openai_api_key = os.environ.get("OPENAI_API_KEY")
    env_model = os.environ.get("OPENAI_MODEL")
    openai_llm = LLM(
        model= env_model,
        api_key=openai_api_key,
    )

    @agent
    def extraction_agent(self) -> Agent:
        return Agent(
            config=self.agents_config['extraction_agent'],
            tools=[SearchTool(), UUIDGeneratorTool(), EmailSendingTool()],
            llm=self.openai_llm,
            max_rpm=5,
            verbose=True
        )

    @task
    def data_extractor_task(self) -> Task:
        base_dir = Path(__file__).resolve().parent.parent.parent
        output_path = base_dir / "data" / "full_extraction.json"
        return Task(
            config=self.tasks_config['data_extractor_task'],
            output_json=FullExtractionSchema,
            output_file=str(output_path),
            callback=save_extracted_data_callback
        )

    @task
    def notification_task(self) -> Task:
        base_dir = Path(__file__).resolve().parent.parent.parent
        output_path = base_dir / "data" / "notification_result.json"
        return Task(
            config=self.tasks_config['notification_task'],
            context=[self.data_extractor_task()],
            output_json=EmailToolOutput,
            output_file=str(output_path),
            callback=save_email_data_callback
        )

    @crew
    def crew(self) -> Crew:
        return Crew(
            agents=self.agents,
            tasks=self.tasks,
            process=Process.sequential,
            memory=False,
            cache=True,
        )