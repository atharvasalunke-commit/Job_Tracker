# JobTracker

A job application tracker with an automated scraping pipeline — Spring Boot backend with JWT authentication and role-based access, and a decoupled Python microservice that scrapes job boards using Playwright, falling back to an LLM (Gemini) to generate CSS selectors for sites it hasn't seen before.

Built as a backend-focused portfolio project — the frontend is intentionally minimal (vanilla JS/HTML/CSS) since the goal was to demonstrate backend architecture, not frontend framework skill.

## Architecture

```
                    ┌──────────────────────┐
   Browser  ─────▶  │   Spring Boot API     │ ─────▶  MySQL
  (static JS/HTML)  │  (JWT auth, REST API) │
                    └──────────┬────────────┘
                               │  WebClient (HTTP)
                               ▼
                    ┌──────────────────────┐
                    │  Python / FastAPI     │
                    │  Playwright scraper   │ ─────▶  Job board (Internshala, AuthenticJobs)
                    │  + Gemini fallback    │ ─────▶  Gemini API (selector generation)
                    └──────────────────────┘
```

The frontend is served directly from Spring Boot's static resources, so it talks to the API same-origin — no separate frontend server, no CORS configuration needed.

## Tech stack

**Backend**
- Java, Spring Boot
- Spring Security + JWT (stateless auth, role-based authorization)
- Spring Data JPA / Hibernate
- MySQL
- MapStruct (DTO ↔ entity mapping)
- WebClient (Spring's reactive HTTP client, used for synchronous calls to the Python service)

**Scraper microservice**
- Python, FastAPI
- Playwright (headless browser automation)
- Google Gemini API (LLM-assisted CSS selector generation for unconfigured sites)

**Frontend**
- Vanilla JavaScript, HTML, CSS — no build step, no framework

## What it does

- Register/login with JWT-based auth; roles (`USER` / `ADMIN`) gate different parts of the API
- Trigger a scrape against a supported job board (currently Internshala and AuthenticJobs), filtered by skills and job type
- Scraped listings are deduplicated and saved as job applications, with soft-delete and status tracking (Applied / Interview / Offer / Rejected)
- Manually add, update, and track applications outside of scraping too
- For a job site with no saved CSS-selector configuration yet, the backend calls the Python service, which asks Gemini to infer selectors from the page's HTML — then that configuration is cached for future scrapes of that domain

## Interesting technical decisions

- **Stateless JWT auth with authorization enforced independently on client and server.** The frontend hides admin-only UI based on the role stored client-side, but that's convenience only — every request is re-authorized server-side via Spring Security's role checks, regardless of what the UI shows or hides.
- **Strategy pattern for per-site scraping logic**, with an LLM-based fallback. Known sites use verified, hardcoded selectors; unknown sites go through a Gemini-assisted selector-generation path, and the result is cached so the LLM call only happens once per domain.
- **Deduplication is keyed on the application URL**, not on scraped text like the job description. Descriptions and titles are volatile and often repeat generic boilerplate across different postings — a stable identifier (the specific posting's URL) avoids false-positive "duplicate" detections that a text-based hash would produce.
- **A self-healing scrape retry**: if a site's markup changes and selectors start returning malformed data (invalid URLs, empty titles), the backend detects that automatically and triggers Gemini to regenerate the selector configuration for that domain before retrying, instead of just failing.

## Running it locally

### Prerequisites
- Java 21+, Maven
- Python 3.10+
- MySQL running locally
- A Gemini API key (for the selector-generation fallback)

### Backend
```bash
# set required environment variables first
export DB_USERNAME=root
export DB_PASSWORD=your_mysql_password
export JWT_SECRET=$(openssl rand -hex 32)

mvn spring-boot:run
```
The API runs on `http://localhost:8080` and also serves the frontend at that same address.

### Scraper microservice
```bash
cd scraper-service
python -m venv venv
source venv/bin/activate        # Windows: venv\Scripts\activate
pip install -r requirements.txt
playwright install chromium

export GEMINI_API_KEY=your_key_here
python main.py
```
Runs on `http://localhost:8001`.

Open `http://localhost:8080` in a browser once both are running.

## What's next

- Docker Compose to run both services together with one command
- Unit test coverage for the core service and validation logic
- Expanding scraping support to new job sites 
