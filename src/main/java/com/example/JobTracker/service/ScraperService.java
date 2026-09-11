package com.example.JobTracker.service;

import com.example.JobTracker.dto.PythonScraperRequest;
import com.example.JobTracker.dto.ScrapedJobDto;
import com.example.JobTracker.dto.ScraperConfigsDto;
import com.example.JobTracker.dto.UserRequestDto;
import com.example.JobTracker.entity.ScraperConfig;
import com.example.JobTracker.globalexception.InValidUrl;
import com.example.JobTracker.mapper.Mapper;
import com.example.JobTracker.repository.ScraperRepository;
import com.example.JobTracker.urlbuilder.JobSiteUrlBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ScraperService {

    @Autowired
    private ScraperRepository repo;

    private final WebClient webClient;
    private Pattern Url_Pattern = Pattern.compile("^(http|https)://[^\\s]+$");
    
    @Autowired
    private Mapper mp;
    @Autowired
    private JobSiteUrlBuilder jsub;
    public ScraperService(WebClient webClient) {
        this.webClient = webClient;
    }

    public ScraperConfig configure(ScraperConfig body) {
        Optional<ScraperConfig>target=repo.findByDomainName(body.getDomain_name());
        if(target.isPresent()){
            ScraperConfig final_target=target.get();
        mp.updateScrapedConfigs(body,final_target);
        return repo.save(final_target);
        }
        return repo.save(body);
    }

    public List<ScrapedJobDto> triggerScraper(PythonScraperRequest request,Map<String,String>map) {
        ArrayList<String> application_urls = new ArrayList<>();
        HashMap<String, String> map2 = new HashMap<>();
        
        ScrapedJobDto[] response = webClient.post()
                .uri("/api/scrape")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ScrapedJobDto[].class)
                .timeout(Duration.ofSeconds(60))
                .block();
                
        List<ScrapedJobDto> scrapedJobs = Arrays.asList(response);
        
        // Use a lambda to pass the local lists to isValid
        List<ScrapedJobDto> ValidJobs = scrapedJobs.stream()
                .filter(job -> isValid(job, application_urls, map2))
                .collect(Collectors.toList());

         if(!application_urls.isEmpty()){
            for(int i=0;i<application_urls.size();i++) {
                String url=application_urls.get(i);
                String  domain_name="";
                ScraperConfigsDto configs=triggerAgentDomScrapper(url);
                ScraperConfig final_configs=mp.toScraperConfigs(configs);

                if(domain_name.equals("")){
                    continue;
                }
                final_configs.setDomain_name(domain_name);
                Optional<ScraperConfig>target=repo.findByDomainName(final_configs.getDomain_name());
                if(target.isPresent()){
                    ScraperConfig final_target=target.get();
                    mp.updateScrapedConfigs(final_configs,final_target);
                    repo.save(final_target);
                } else {
                    repo.save(final_configs);
                }
            }
        }
        
         if(!map2.isEmpty()){
                 StringBuilder sb=new StringBuilder();
                 for(HashMap.Entry<String,String>x:map2.entrySet()) {
                     sb.append(x.getKey() + ":" + x.getValue()+"\n");
                 }
                 String body=new String(sb);
             throw new InValidUrl(body);
         }
        return ValidJobs;
    }
    
    public ScraperConfigsDto triggerAgentDomScrapper(String application_url){
        ScraperConfigsDto response = webClient.post()
                .uri("/api/generate-config")
                .bodyValue(Collections.singletonMap("url", application_url))
                .retrieve()
                .bodyToMono(ScraperConfigsDto.class)
                .timeout(Duration.ofSeconds(600))
                .block();
            return response;
    }
    
    // Accept map as a parameter here so it uses the thread-safe local one
    public boolean isValid(ScrapedJobDto scrapedJob, ArrayList<String> application_urls, HashMap<String, String> map2) {
        if (scrapedJob.getApplication_url() == null || !Url_Pattern.matcher(scrapedJob.getApplication_url()).matches()) {
            map2.put("Invalid Url",scrapedJob.getApplication_url());
            return false;
        }
        if (scrapedJob.getJob_title() == null || scrapedJob.getJob_title().length() > 100) {
            application_urls.add(scrapedJob.getApplication_url());
            return false;
        }
        if (scrapedJob.getCompany_name() == null || scrapedJob.getCompany_name().isEmpty()) {
            application_urls.add(scrapedJob.getApplication_url());
            return false;
        }
        return true;
    }
    public PythonScraperRequest requestCompleter(UserRequestDto request,Map<String,String>map){
        String domain=request.getDomain_name();

        if(!map.containsKey(domain)){
            throw new InValidUrl(map.get(request.getDomain_name())+"Invalid url");
        }
        List<String>skills=request.getUser_skills();
        String final_url=jsub.buildRemoteOkUrl(skills,domain,map);
        System.out.println(final_url);
        Optional<ScraperConfig> rules=repo.findByDomainName(domain);
        PythonScraperRequest response=new PythonScraperRequest();
        if(rules.isPresent()){
            ScraperConfig final_rules=rules.get();
            response.setRules(mp.toScraperConfigsDto(final_rules));
            response.setUser_skills(request.getUser_skills());
            response.setJob_type(request.getJob_type());
            response.setUrl(final_url);
        }
        else if(map.containsKey(domain)){
            ScraperConfigsDto rules2 =triggerAgentDomScrapper(final_url);
            ScraperConfig final_rules2=mp.toScraperConfigs(rules2);
            final_rules2.setDomain_name((request.getDomain_name()));
            repo.save(final_rules2);
            response.setRules(rules2);
            response.setUser_skills(request.getUser_skills());
            response.setJob_type(request.getJob_type());
            response.setUrl(final_url);
        }
        return response;
    }
}
