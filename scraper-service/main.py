from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
from scraper import scraper
app = FastAPI()
class ScrapeRequest(BaseModel):
    site_name: list[str]
    search_term: str | None = None
    location: str | None = None
    user_skills: list[str] = Field(default_factory=list)
    job_type: str | None = None
    is_remote: bool | None = None

@app.get("/health")
def health():
    return {
        "status": "ok"
    }

@app.post("/api/scrape")
def trigger_scraper(request: ScrapeRequest):
    if not request.site_name:
        raise HTTPException(
            status_code=400,
            detail="Select at least one job site."
        )
    if not request.search_term and not request.user_skills:
        raise HTTPException(
            status_code=400,
            detail="Provide a search term or at least one skill."
        )
    try:
        return scraper(
            site_name=request.site_name,
            search_term=request.search_term,
            location=request.location,
            user_skills=request.user_skills,
            job_type=request.job_type,
            is_remote=request.is_remote,
        )
    except ValueError as e:
        raise HTTPException(
            status_code=400,
            detail=str(e)
        )
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=str(e)
        )