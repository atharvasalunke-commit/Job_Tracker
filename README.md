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

Open [**http://localhost:8080**](http://localhost:8080), create an account and search.

To stop the application:

```bash
docker compose down
```

To completely reset the database:

```bash
docker compose down -v
```

> `docker compose down -v` removes the MySQL volume and deletes the stored database data.

## API

Everything except Register and Login needs `Authorization: Bearer <token>`.

| Method | Endpoint | What it does |
| --- | --- | --- |
| POST | `/api/account/Register` | Create account, returns JWT |
| POST | `/api/account/Login` | Log in, returns JWT |
| POST | `/api/scrape` | Search and save new jobs |
| GET | `/api/jobs?page=0&size=10` | List your applications |
| GET | `/api/jobs/{id}` | Get one |
| PUT | `/api/jobs/{id}` | Update details or status |
| PUT | `/api/jobs/softdelete/{id}` | Soft delete |
| POST | `/api/jobs` | Add manually (admin only) |

### Search request

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

### Separate scraping service

The application separates job scraping from the main Spring Boot backend.

**Spring Boot** is responsible for authentication, users, saved jobs, application tracking, database operations, and REST APIs.

**Python FastAPI** is responsible for interacting with JobSpy and returning normalized job results to the backend.

This keeps scraper-specific logic isolated from the core application and makes the two parts easier to change independently.

### JobSpy instead of the previous custom scraper

The current version uses **JobSpy** for supported job-board searches.

The earlier custom Python scraper and LLM-based approach were removed from the current implementation. Using JobSpy keeps the scraping layer smaller and allows the project to focus on the application-management side of the system.

### Stateless JWT authentication

The backend uses JWT authentication rather than server-side sessions.

Each protected request sends:

```http
Authorization: Bearer <token>
```

The JWT filter extracts the username, loads the corresponding user, and places the authenticated user into Spring Security's context.

### User-scoped application data

Saved jobs are associated with the logged-in user. This means application tracking is performed per account rather than using one shared global job list.

### Persistent MySQL storage

MySQL stores user and job/application data. Docker Compose uses a persistent volume so normal container restarts do not remove the database.

### Best-effort job recency

LinkedIn and Indeed do not expose posting information in exactly the same way.

The scraper therefore handles them differently:

- LinkedIn uses a recent-job window.
- Indeed results are sorted by `date_posted`.
- Recent Indeed results are preferred when available.
- If no recent Indeed results are available, older results are retained instead of returning nothing.

### Docker Compose

The project runs as three services:

```text
app       → Spring Boot
scraper   → FastAPI + JobSpy
mysql     → MySQL
```

Docker Compose provides one setup for the complete application and also gives the services stable internal hostnames such as `mysql` and `scraper`.

## How the Request Flows

```text
User
  ↓
Frontend
  ↓
Spring Boot API
  ↓
JWT Authentication
  ↓
POST /api/scrape
  ↓
Python FastAPI
  ↓
JobSpy
  ↓
LinkedIn / Indeed
  ↓
Cleaned job results
  ↓
Spring Boot
  ↓
MySQL
  ↓
Frontend
```

## Project Structure

```text
JobTracker/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/JobTracker/
│   │   └── resources/
│   │       └── static/
│   └── test/
│
├── scraper-service/
│   ├── main.py
│   ├── scraper.py
│   ├── requirements.txt
│   └── Dockerfile
│
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Limitations

- LinkedIn can return few or no results because availability and rate limits can change.
- Job recency is best-effort.
- Search results depend on what the supported job sites make available through JobSpy.
- Only LinkedIn and Indeed are currently supported.
- The application is currently packaged primarily for local/Dockerized use rather than a production deployment with HTTPS, monitoring, and cloud infrastructure.

## Future Improvements

- Support for additional job sites
- More advanced search and sorting
- Scheduled job searches
- Job alerts and notifications
- Improved scraper retry/error handling
- Production deployment
- HTTPS and domain configuration
- Better monitoring and observability

## Author

**Atharva Salunke**

Second-year BCA student building backend-focused projects with Spring Boot, Python, MySQL, Docker, and REST APIs.
