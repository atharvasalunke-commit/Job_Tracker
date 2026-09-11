from fastapi import FastAPI,HTTPException
from pydantic import BaseModel
from typing import List,Dict
import uvicorn
from scraper import scraper
from agent import generate_dom_patterns
app=FastAPI()
class ScrapeRequest(BaseModel):
    url:str
    rules:Dict[str,str]
    user_skills:List[str]
    job_type:str
class ConfigRequest(BaseModel):
    url:str
@app.post("/api/scrape")
def triggerScraper(request:ScrapeRequest):
       try:
            job_list = scraper(
                    url=request.url,
                    rules=request.rules,
                    user_skills=request.user_skills,
                    job_type=request.job_type
                )
            return job_list
       except Exception as e:
           raise HTTPException(status_code=500,detail=str(e))
@app.post("/api/generate-config")
def trigger_agent(request:ConfigRequest):
    print("hello")
    try:
        selectors=generate_dom_patterns(url=request.url)
        return selectors
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8001)
