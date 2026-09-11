import os
import json
import google.generativeai as genai
from playwright.sync_api import sync_playwright
def generate_dom_patterns(url :str):
    api_key = os.getenv("GEMINI_API_KEY")
    if not api_key:
        raise ValueError("GEMINI_API_KEY environment variables is not set")
    genai.configure(api_key=api_key)
    print(f"browsing to {url} to extract HTML...")
    html_content=None
    with sync_playwright() as p:
        browser=p.chromium.launch(headless=True)
        context=browser.new_context(user_agent="Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
        page=context.new_page()
        try:
            page.goto(url,wait_until="domcontentloaded",timeout=60000)
            page.wait_for_timeout(3000)#waiting for react/js to hydrate
            html_content=page.content()
            print(html_content+"hi")
        finally:
            browser.close()
    print("Html extracted now asking gemini to find CSS selectors")
    model = genai.GenerativeModel('gemini-3.6-flash')
    prompt = f"""
            You are an expert web scraper. Analyze the following HTML of a job board and figure out the best CSS selectors to extract job data.

            Return a strict JSON object with EXACTLY these keys:
            - "card_selector": Wrapper element for a single job post
            - "title_selector": CSS selector for the job title inside the card
            - "company_selector": CSS selector for the company name
            - "link_selector": CSS selector for the 'a' tag containing the job link
            - "requirements_selector": CSS selector for the skills/requirements

            Return ONLY valid JSON.

     HTML snippet:
    {html_content[:150000]}
    """
    response=model.generate_content(prompt)
    print(response)
    raw_text=response.text.strip()
    start_idx=-1
    end_idx=-1
    start_idx=raw_text.find("{")
    end_idx=raw_text.rfind("}")
    if start_idx!=-1 and end_idx!=-1:
        json_string=raw_text[start_idx:end_idx+1]
        print(json_string)
        print("Got CSS selectors")
        return json.loads(json_string)
    else:
        raise ValueError(f"Gemini did not return a valid JSON object. Raw output: {raw_text}")
