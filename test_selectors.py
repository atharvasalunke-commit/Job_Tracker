from playwright.sync_api import sync_playwright
import json

with sync_playwright() as p:
    browser = p.chromium.launch(headless=True)
    page = browser.new_page()
    page.goto('https://internshala.com/internships/keywords-python/', wait_until='domcontentloaded', timeout=60000)
    page.wait_for_timeout(5000)
    
    cards = page.query_selector_all('.individual_internship')
    print(f"Cards found: {len(cards)}")
    
    for i, card in enumerate(cards[:2]):
        title_elem = card.query_selector('a.job-title-href')
        comp_elem = card.query_selector('p.company-name')
        print(f"Card {i+1}:")
        print(f"  Title: {title_elem.inner_text().strip() if title_elem else 'NONE'}")
        print(f"  Company: {comp_elem.inner_text().strip() if comp_elem else 'NONE'}")
        
    browser.close()
