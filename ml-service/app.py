from contextlib import asynccontextmanager
from pathlib import Path
from typing import Annotated

import joblib
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, StringConstraints

MODEL_PATH = Path(__file__).parent / "model.joblib"

Message = Annotated[str, StringConstraints(strip_whitespace=True, min_length=1, max_length=5000)]


@asynccontextmanager
async def lifespan(app: FastAPI):
    # Only ever load a model file that train.py produced on this machine (it is a pickle).
    app.state.model = joblib.load(MODEL_PATH) if MODEL_PATH.exists() else None
    yield


app = FastAPI(title="ScamShield ML service", lifespan=lifespan)


class PredictRequest(BaseModel):
    message: Message


class PredictResponse(BaseModel):
    scam_probability: float


@app.get("/health")
def health():
    return {"status": "ok", "model_loaded": app.state.model is not None}


@app.post("/predict", response_model=PredictResponse)
def predict(request: PredictRequest):
    model = app.state.model
    if model is None:
        raise HTTPException(status_code=503, detail="Model not loaded. Run train.py first.")
    scam_index = list(model.classes_).index(1)
    probability = float(model.predict_proba([request.message])[0][scam_index])
    return PredictResponse(scam_probability=round(probability, 4))