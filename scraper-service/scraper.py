import pandas as pd
from jobspy import scrape_jobs

SUPPORTED_SITES = {"linkedin", "indeed"}
MAX_RESULTS = 20

def _clean(value):
    if value is None or (isinstance(value, float) and pd.isna(value)):
        return ""
    return str(value).strip()

def scraper(site_name, search_term=None, location=None, user_skills=None, job_type=None, is_remote=None):
    sites = []

    for site in site_name or []:
        site = _clean(site).lower()

        if site and site not in SUPPORTED_SITES:
            raise ValueError(f"Unsupported job site: {site}")

        if site and site not in sites:
            sites.append(site)

    if not sites:
        raise ValueError("Select at least one supported job site.")

    query = [_clean(search_term)] if search_term else []

    for skill in user_skills or []:
        skill = _clean(skill)
        if skill and skill not in query:
            query.append(skill)

    search_query = " ".join(query)

    if not search_query:
        raise ValueError("Provide a search term or at least one skill.")

    all_jobs = []

    for site in sites:
        kwargs = {
            "site_name": [site],
            "search_term": search_query,
            "location": location or None,
            "results_wanted": MAX_RESULTS,
            "verbose": 1
        }

        if site == "linkedin":
            kwargs["hours_old"] = 24
            kwargs["linkedin_fetch_description"] = True

            if job_type:
                kwargs["job_type"] = job_type

            if is_remote is not None:
                kwargs["is_remote"] = bool(is_remote)

        try:
            jobs = scrape_jobs(**kwargs)
        except Exception as e:
            print(f"[SCRAPER] {site} failed: {e}")
            continue

        if jobs is None or jobs.empty:
            continue

        if site == "indeed" and "date_posted" in jobs.columns:
            jobs["date_posted"] = pd.to_datetime(
                jobs["date_posted"],
                errors="coerce"
            )

            jobs = jobs.sort_values(
                "date_posted",
                ascending=False,
                na_position="last"
            )

            yesterday = pd.Timestamp.now().normalize() - pd.Timedelta(days=1)
            recent = jobs[jobs["date_posted"] >= yesterday]

            if not recent.empty:
                jobs = recent

            if job_type and "job_type" in jobs.columns:
                jobs = jobs[
                    jobs["job_type"].astype(str).str.lower()
                    == str(job_type).lower()
                ]

            if is_remote is not None and "is_remote" in jobs.columns:
                jobs = jobs[jobs["is_remote"] == bool(is_remote)]

        all_jobs.append(jobs)

    if not all_jobs:
        return []

    jobs = pd.concat(all_jobs, ignore_index=True)
    result = []

    for _, job in jobs.iterrows():
        title = _clean(job.get("title"))
        company = _clean(job.get("company"))
        url = _clean(job.get("job_url"))
        description = _clean(job.get("description"))

        if title and company and url:
            result.append({
                "company_name": company,
                "job_title": title,
                "job_description": description,
                "application_url": url
            })

    print(f"[SCRAPER] Jobs found: {len(jobs)} | Jobs sent to Java: {len(result)}")
    return result