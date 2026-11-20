import csv
import sys
from pathlib import Path

import joblib
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import classification_report
from sklearn.model_selection import StratifiedKFold, cross_val_predict
from sklearn.pipeline import make_pipeline

HERE = Path(__file__).parent
DATA = HERE / "data" / "samples.csv"
MODEL = HERE / "model.joblib"
MIN_RELIABLE = 200


def load():
    texts, labels = [], []
    with DATA.open(encoding="utf-8", newline="") as f:
        for row in csv.DictReader(f):
            text = row["text"].strip()
            if text:
                texts.append(text)
                labels.append(int(row["label"]))
    return texts, labels


def build_model():
    return make_pipeline(
        # Character n-grams cope with Arabic, French, English and Arabizi spelling variants.
        # strip_accents="unicode" also removes Arabic diacritics and unifies alef forms.
        TfidfVectorizer(
            analyzer="char_wb",
            ngram_range=(2, 5),
            lowercase=True,
            strip_accents="unicode",
            sublinear_tf=True,
        ),
        LogisticRegression(max_iter=1000, class_weight="balanced"),
    )


def main():
    texts, labels = load()
    scams = sum(labels)
    legit = len(labels) - scams
    print(f"Loaded {len(labels)} samples: {scams} scam, {legit} legitimate")

    if min(scams, legit) < 2:
        sys.exit("Need at least 2 samples of each class.")

    folds = min(5, scams, legit)
    predicted = cross_val_predict(
        build_model(), texts, labels,
        cv=StratifiedKFold(n_splits=folds, shuffle=True, random_state=42),
    )
    print(f"\nCross-validated results ({folds} folds):")
    print(classification_report(
        labels, predicted, target_names=["legitimate", "scam"], digits=2, zero_division=0))

    if len(labels) < MIN_RELIABLE:
        print(f"WARNING: only {len(labels)} samples (fewer than {MIN_RELIABLE}). "
              "These numbers are not reliable. Add real messages to data/samples.csv.")

    model = build_model().fit(texts, labels)
    joblib.dump(model, MODEL)
    print(f"\nModel saved to {MODEL.name}")


if __name__ == "__main__":
    main()