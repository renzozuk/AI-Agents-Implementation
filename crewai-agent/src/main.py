import os
import warnings

import agentops
from src.crews.data_extractor_crew import DataExtractorCrew


warnings.filterwarnings("ignore", category=SyntaxWarning, module="pysbd")

def run():
    """
    Run the crew.
    """
    agentops_api_key = os.environ.get("AGENTOPS_API_KEY")
    agentops.init(agentops_api_key)

    agentops.start_trace()
    result = DataExtractorCrew().crew().kickoff()
    agentops.end_trace()

    print(f"Token usage: {result.token_usage}")

run()