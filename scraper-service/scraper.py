from urllib.parse import urljoin
from playwright.sync_api import sync_playwright
from playwright_stealth import Stealth
import re

TECH_SKILLS = {
    "python", "java", "javascript", "typescript", "c++", "c#", "go", "golang", "rust", "ruby",
    "swift", "kotlin", "scala", "php", "perl", "r", "matlab", "dart", "lua", "haskell",
    "react", "angular", "vue", "svelte", "next.js", "nuxt", "node.js", "express", "django",
    "flask", "spring", "spring boot", ".net", "rails", "laravel", "fastapi",
    "aws", "azure", "gcp", "docker", "kubernetes", "terraform", "jenkins", "ci/cd",
    "linux", "git", "github", "gitlab", "devops", "ansible", "nginx",
    "sql", "mysql", "postgresql", "mongodb", "redis", "elasticsearch", "cassandra",
    "dynamodb", "firebase", "graphql", "rest", "api",
    "machine learning", "deep learning", "ai", "nlp", "tensorflow", "pytorch", "opencv",
    "data science", "data engineering", "etl", "spark", "hadoop", "kafka", "airflow",
    "html", "css", "sass", "tailwind", "bootstrap", "figma",
    "ios", "android", "flutter", "react native",
    "blockchain", "solidity", "web3", "smart contract",
    "cybersecurity", "penetration testing", "soc", "siem",
    "agile", "scrum", "jira", "microservices", "rabbitmq", "grpc"
}


def scraper(url,rules,user_skills,job_type):

    with sync_playwright() as p:
        print("1.Opening chromium browser")
        browser=p.chromium.launch(headless=True)

        print("setting up stealth user agent")
        context=browser.new_context(
            user_agent="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"
        )

        print(f"2.navigating to {url}")
        page=context.new_page()
        Stealth().apply_stealth_sync(page)

        job_list=[]

        try:
            page.goto(
                url,
                wait_until="domcontentloaded",
                timeout=60000
            )

            print("waiting 20 seconds for react/js to hydrate")
            page.wait_for_timeout(20000)

            print(f"3.extracting some basic data from {url}")

            job_cards=page.query_selector_all(
                rules["card_selector"]
            )

            print(f"cards found ({len(job_cards)}) ->")

            if not job_cards:
                print("No job cards found. Selectors may need to be regenerated.")
                return []

            extracted_count=0

            for i,card in enumerate(job_cards[:len(job_cards)]):

                title_elem=card.query_selector(
                    rules["title_selector"]
                )

                company_name_elem=card.query_selector(
                    rules["company_selector"]
                )

                href_elem=card.query_selector(
                    rules["link_selector"]
                )

                if title_elem and company_name_elem:
                    title=title_elem.inner_text().strip()
                    company_name=company_name_elem.inner_text().strip().split('\n')[0]
                else:
                    print("no job found")
                    continue

                if href_elem:
                    raw_link=href_elem.get_attribute("href")

                    if not raw_link:
                        print("job link is empty")
                        continue

                    full_link=urljoin(url,raw_link)

                else:
                    print("no job link")
                    continue

                tag_elements=card.query_selector_all(
                    rules["requirements_selector"]
                )

                requirement_list=[]

                for tag in tag_elements:
                    requirement_list.append(
                        tag.inner_text().strip()
                    )

                requirement_string="|".join(
                    requirement_list
                )

                if not requirement_string:
                    requirement_string="no job_description found"

                req_lower=requirement_string.lower()
                title_low=title.lower()
                type_low=job_type.lower()

                extracted_count+=1

                is_valid=True

                is_internship=re.search(
                    r'\b(intern|internship)\b',
                    title_low
                )

                if type_low=="internship" and not is_internship and "internshala" not in url:
                    is_valid=False

                elif type_low=="job" and is_internship:
                    is_valid=False

                has_matched=False

                if len(user_skills)==0:
                    has_matched=True

                else:
                    searchable_text=(
                        card.inner_text().lower()
                        + "|"
                        + req_lower
                        + "|"
                        + title_low
                    )

                    user_skill_found=False

                    for skill in user_skills:
                        pattern=(
                            r'(?<!\w)'
                            + re.escape(skill.lower())
                            + r'(?!\w)'
                        )

                        if re.search(
                            pattern,
                            searchable_text
                        ):
                            user_skill_found=True
                            break

                    tech_hit_count=0

                    for tech in TECH_SKILLS:
                        pattern=(
                            r'(?<!\w)'
                            + re.escape(tech)
                            + r'(?!\w)'
                        )

                        if re.search(
                            pattern,
                            searchable_text
                        ):
                            tech_hit_count+=1

                    if user_skill_found and tech_hit_count>=1:
                        has_matched=True

                if is_valid and has_matched:

                    job_data={
                        "job_title":title,
                        "company_name":company_name,
                        "application_url":full_link,
                        "job_description":requirement_string
                    }

                    job_list.append(job_data)

        except TimeoutError:

            print(
                f"ERROR: The website {url} took too long to load "
                f"(Cloudflare block or slow network)."
            )

        except Exception as e:

            print(f"Unknown error: {e}")

        print(
            f"jobs extracted ({extracted_count}) "
            f"-> jobs after skill filtering ({len(job_list)})"
        )

        print("5.closing browser")
        browser.close()

        return job_list