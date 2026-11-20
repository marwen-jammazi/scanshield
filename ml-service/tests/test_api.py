import pytest
from fastapi.testclient import TestClient

import train
from app import app

SCAM = "Votre colis est bloqué, payez les frais de livraison sous 24h : http://poste-tn.xyz/pay"
LEGIT = "Salut, on se voit demain au café ?"


@pytest.fixture(scope="module")
def client():
    texts, labels = train.load()
    model = train.build_model().fit(texts, labels)
    with TestClient(app) as test_client:
        app.state.model = model
        yield test_client


def test_health_reports_loaded_model(client):
    body = client.get("/health").json()
    assert body == {"status": "ok", "model_loaded": True}


def test_probability_is_between_zero_and_one(client):
    response = client.post("/predict", json={"message": SCAM})
    assert response.status_code == 200
    assert 0.0 <= response.json()["scam_probability"] <= 1.0


def test_scam_scores_higher_than_normal_message(client):
    # These two messages are in the training data, so this is a wiring check
    # (right column, right direction), not a measurement of accuracy.
    scam = client.post("/predict", json={"message": SCAM}).json()["scam_probability"]
    legit = client.post("/predict", json={"message": LEGIT}).json()["scam_probability"]
    assert scam > legit


def test_arabic_message_is_accepted(client):
    response = client.post("/predict", json={"message": "عاجل: حسابك موقوف"})
    assert response.status_code == 200


@pytest.mark.parametrize("message", ["", "   "])
def test_blank_message_is_rejected(client, message):
    assert client.post("/predict", json={"message": message}).status_code == 422


def test_too_long_message_is_rejected(client):
    assert client.post("/predict", json={"message": "a" * 5001}).status_code == 422


def test_missing_model_returns_503(client, monkeypatch):
    monkeypatch.setattr(app.state, "model", None)
    assert client.post("/predict", json={"message": SCAM}).status_code == 503
    assert client.get("/health").json()["model_loaded"] is False