from urllib.parse import urljoin
from playwright.sync_api import sync_playwright
import json
import re
def scraper(url,rules,user_skills,job_type):

    with sync_playwright() as p:
        print("1.Opening chromium browser")
        browser=p.chromium.launch(headless=True)#headless makes browser invisble from bot or script detection of sites
        print("setting up stealth user agent")
        context=browser.new_context(user_agent="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
        print(f"2.navigating to {url}")
        page=context.new_page()
        job_list=[]
        try:
            page.goto(url,wait_until="domcontentloaded",timeout=60000)
            print("waiting 2 seconds for react/js to hydrate")
            page.wait_for_timeout(2000)
            print(f"3.extracting some basic data from{url}")
            job_cards=page.query_selector_all(rules["cardSelector"])
            for i,card in enumerate(job_cards[:5]):
                title_elem=card.query_selector(rules["titleSelector"])
                company_name_elem=card.query_selector(rules["companySelector"])
                href_elem=card.query_selector(rules["linkSelector"])
                if title_elem and company_name_elem:
                    title=title_elem.inner_text().strip()
                    company_name=company_name_elem.inner_text().strip()
                else:
                     print("no job found")
                     continue
                if href_elem:
                    raw_link=href_elem.get_attribute("href")
                    full_link=urljoin(url,raw_link)

                else:
                    print("no job link")
                    continue
                tag_elements=card.query_selector_all(rules["requirementsSelector"])
                requirement_list=[]
                for tag in tag_elements:
                    requirement_list.append(tag.inner_text().strip())
                requirement_string="|".join(requirement_list)
                if not requirement_string:
                    requirement_string="no job_description found"
                req_lower=requirement_string.lower()
                title_low=title.lower()
                type_low=job_type.lower()
                is_valid=False
                is_internship = re.search(r'\b(intern|internship)\b', title_low)
                if type_low=="internship" and is_internship:
                     is_valid=True
                elif type_low=="job" and not is_internship:
                     is_valid=True

                has_matched=False
                if len(user_skills)==0:
                    has_matched=True
                else:
                    for skill in user_skills:
                          pattern = r'\b' + re.escape(skill.lower()) + r'\b'
                          if re.search(pattern,req_lower):
                              has_matched=True
                              break
                if is_valid and has_matched:
                     job_data = {
                                       "job_title": title,
                                       "company_name": company_name,
                                       "application_url": full_link,
                                       "requirements": requirement_string
                                   }
                     job_list.append(job_data)

        except TimeoutError:
             print(f" ERROR: The website {url} took too long to load (Cloudflare block or slow network).")
        except Exception as  e:
             print(f"Unknown error{e}")
        print("5.closing browser")
        browser.close()
        return job_list
if __name__=="__main__":
    remoteok_rules = {
                "cardSelector": "tr.job",
                "titleSelector": "h2",
                "companySelector": "h3",
                "linkSelector": "a",
                "requirementsSelector": ".tag h3"
            }
skills = ["react", "marketing", "javascript", "sales", "engineer","neural network"]
#skills=[]
job_type="job"
final_data=scraper("https://remoteok.com/",remoteok_rules,skills,job_type)
print(json.dumps(final_data,indent=4))