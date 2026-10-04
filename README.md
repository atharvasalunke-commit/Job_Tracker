# JobTracker

Search LinkedIn and Indeed from one form, save the results to your account, and track each application from "Not applied" to "Offer".

Spring Boot + MySQL + JWT backend, with a separate Python FastAPI scraper (JobSpy), all started by one `docker compose up`.

<!-- Add a dashboard screenshot here: ![Dashboard](docs/dashboard.png) -->

## Major Features

```text
                                                  ┌──────────────────────────────────┐
                      ┌────────────────────────┐  │       Register and log in        │
                   ┌──│    1. USER ACCOUNTS    │──│    Secure JWT authentication     │
                   │  └────────────────────────┘  │  Users only see their own jobs   │
                   │                              └──────────────────────────────────┘
                   │
                   │                              ┌──────────────────────────────────┐
                   │  ┌────────────────────────┐  │       LinkedIn and Indeed        │
                   ├──│     2. JOB SEARCH      │──│    Keyword, skills, location     │
                   │  └────────────────────────┘  │    Job type and remote filter    │
                   │                              └──────────────────────────────────┘
                   │
                   │                              ┌──────────────────────────────────┐
┌────────────┐     │  ┌────────────────────────┐  │     Python FastAPI + JobSpy      │
│ JOBTRACKER │─────┼──│   3. SCRAPER SERVICE   │──│   Separate from the main app     │
└────────────┘     │  └────────────────────────┘  │   Focus on recent job postings   │
                   │                              └──────────────────────────────────┘
                   │
                   │                              ┌──────────────────────────────────┐
                   │  ┌────────────────────────┐  │       Jobs saved to MySQL        │
                   ├──│ 4. TRACK APPLICATIONS  │──│      No duplicate listings       │
                   │  └────────────────────────┘  │ Update status: Applied to Offer  │
                   │                              └──────────────────────────────────┘
                   │
                   │                              ┌──────────────────────────────────┐
                   │  ┌────────────────────────┐  │        One-command setup         │
                   └──│       5. DOCKER        │──│      App, scraper and MySQL      │
                      └────────────────────────┘  │    docker compose up --build     │
                                                  └──────────────────────────────────┘
```

## Tech Stack

Java 21 · Spring Boot · Spring Security · JPA/Hibernate · MySQL 8.4 · Python 3.12 · FastAPI · JobSpy · Docker Compose

## Quick Start

```bash
git clone <your-repo-url>
cd JobTracker
```

Create a `.env` file (never commit it):

```env
DB_PASSWORD=choose_a_password
MYSQL_ROOT_PASSWORD=choose_a_root_password
JWT_SECRET=<output of: openssl rand -base64 48>
```

```bash
docker compose up --build
```

Open **http://localhost:8080**, create an account and search.
Reset the database with `docker compose down -v`.

## API

Everything except Register and Login needs `Authorization: Bearer <token>`.

| Method | Endpoint | What it does |
|---|---|---|
| POST | `/api/account/Register` | Create account, returns JWT |
| POST | `/api/account/Login` | Log in, returns JWT |
| POST | `/api/scrape` | Search and save new jobs |
| GET | `/api/jobs?page=0&size=10` | List your applications |
| GET | `/api/jobs/{id}` | Get one |
| PUT | `/api/jobs/{id}` | Update details or status |
| PUT | `/api/jobs/softdelete/{id}` | Soft delete |
| POST | `/api/jobs` | Add manually (admin only) |

Search body:

```json
{
  "site_name": ["linkedin", "indeed"],
  "search_term": "Backend Developer",
  "location": "Mumbai",
  "user_skills": ["Java", "Spring Boot"],
  "job_type": "internship",
  "is_remote": null
}
```

## Design Decisions

- **Stateless JWT:** a filter validates the token on each request, no server sessions.
- **Isolated data:** every query is scoped to the logged-in user.
- **No duplicates:** each job stores a SHA-256 hash of its URL, checked per user.
- **Separate scraper:** scraping is slow and depends on third-party sites, so failures show up as a clean `503` instead of crashing the app.

## Limitations

- LinkedIn rate-limits heavily, so a search can return few or no results.
- Recency is best-effort. If Indeed has nothing from the last day, older results are kept.
- Only LinkedIn and Indeed are supported.

## Author

**Atharva Salunke**, second-year BCA student building backend projects with Spring Boot, Python and Docker.
