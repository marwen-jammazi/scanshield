# ScamShield

![CI](https://github.com/marwen-jammazi/scamshield/actions/workflows/ci.yml/badge.svg)

Multilingual scam and phishing detector for **Arabic, French, English and Arabizi**. Paste a suspicious message or link and get a risk score with a plain-language explanation of *why* it looks like a scam.

![ScamShield screenshot](docs/screenshot.png)

## Why this project

Most scam checkers only handle English. In Tunisia and the wider Maghreb, scam messages (fake parcel deliveries, bank alerts, telecom prizes) are written in Arabic, French, Arabizi (Arabic in Latin letters, like `7sabek mawkouf`), or a mix of all of them. ScamShield is built to catch those.

## Features

- Risk score (0-100) with LOW / MEDIUM / HIGH levels
- Explainable results: every point of the score comes with a human-readable reason
- Arabic, French, English and Arabizi keyword detection, with text normalization (accents, Arabic diacritics, alef variants)
- Link analysis: URL shorteners, raw IP addresses, and domain extensions often used in scams
- Accounts with JWT login and a private scan history for each user
- Guest scanning: anonymous messages are analyzed but never stored
- Right-to-left input support in the UI
- Unit-tested detection engine and ownership rules

## Tech stack

| Layer | Technology |
|-------|------------|
| Backend | Java 21, Spring Boot, Spring Security (JWT), Spring Data JPA, Maven |
| Database | H2 for development and tests (MySQL profile planned) |
| Frontend | React, Vite, Tailwind CSS |
| Testing | JUnit 5, AssertJ, Mockito |
| CI | GitHub Actions |

## How the detection engine works

Each rule is a small class implementing one interface:

```java
public interface DetectionRule {
    List<RuleResult> evaluate(String message);
}
```

`ScanService` runs every rule, adds up the points (capped at 100) and collects the reasons. Adding a new rule means adding one class annotated with `@Component`, and nothing else changes.

Current rules: `LinkRule`, `UrgencyRule`, `SensitiveInfoRule`.

Latin keywords are matched as whole words (so `otp` does not match `hotpot`), while Arabic keywords match as substrings because prefixes attach to Arabic words.

## Getting started

**Requirements:** Java 21, Node.js 20.19+

**Backend**

```bash
cd backend
./mvnw spring-boot:run
```

The API runs on http://localhost:8080. Development data is stored in `backend/data/` (ignored by Git).

**Frontend**

```bash
cd frontend
npm install
npm run dev
```

The app runs on http://localhost:5173.

**Run the tests**

```bash
cd backend
./mvnw test
```

## API

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | `/api/scan` | optional | Analyze a message. Saved to history only when logged in |
| GET | `/api/scans` | required | The 20 most recent scans of the logged-in user |
| POST | `/api/auth/register` | none | Create an account, returns a JWT |
| POST | `/api/auth/login` | none | Log in, returns a JWT |

Example:

```json
POST /api/scan
{ "message": "Votre colis bloqué, payez sous 24h : http://poste-tn.xyz/pay" }
```

```json
{
  "score": 65,
  "level": "MEDIUM",
  "reasons": [
    "Message contains a link",
    "Link uses a domain extension often seen in scams",
    "Uses urgency or threat language to pressure you"
  ]
}
```

## Security notes

- Passwords are hashed with BCrypt and never stored in plain text.
- Tokens are JWTs signed with HS256 and expire after 2 hours. Set the `JWT_SECRET` environment variable (at least 32 bytes) in any real deployment; the built-in default is for local development only.
- Login returns the same error for an unknown email and a wrong password.
- The frontend keeps the token in `localStorage`, a common trade-off for a project like this. A production system would prefer an HttpOnly cookie.
- CORS only allows the configured frontend origin (`scamshield.cors.allowed-origins`).

## Known limitations

- The keyword lists are a starting point. Arabizi has no standard spelling, so variants will slip through. Real scam samples are welcome as test cases.
- Detection is rule-based, so a scam that avoids all the known signals will score low. A score is a hint, not a verdict.
- Docker files are included but have not been verified end to end yet.

## Roadmap

- [x] Rule-based detection engine (Arabic, French, English)
- [x] React scanner interface
- [x] GitHub Actions CI
- [x] User accounts and private scan history (H2)
- [x] Arabizi detection
- [ ] Verify the Docker Compose setup
- [ ] MySQL profile
- [ ] Chrome extension
- [ ] ML scoring service

## Author

Marwen Jammazi - [GitHub](https://github.com/marwen-jammazi)