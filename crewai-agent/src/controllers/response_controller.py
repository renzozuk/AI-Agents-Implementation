from fastapi import FastAPI, BackgroundTasks, status

from src.crews.webhook_processor_crew import WebhookProcessorCrew

app = FastAPI()


@app.post("/webhook/{pdf_title}", status_code=status.HTTP_202_ACCEPTED)
async def response(pdf_title: str, background_tasks: BackgroundTasks):
    background_tasks.add_task(run_crew_process, pdf_title)
    print(f"Processando email {pdf_title}")

    return {"message": "Processamento iniciado em background", "pdf_title": pdf_title}

def run_crew_process(pdf_title: str):
    inputs = {
        "pdf_title": pdf_title,
        "last_response_email": "",
        "partial_json": ""
    }
    WebhookProcessorCrew().crew().kickoff(inputs=inputs)