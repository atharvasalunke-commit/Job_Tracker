package com.example.JobTracker.urlbuilder;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
@Component
public class JobSiteUrlBuilder {
    private Map<String,Map<String,String>>siteSlugs;
    @PostConstruct
    public void init()throws Exception{
        ObjectMapper mapper=new ObjectMapper();
        try(InputStream is=getClass().getResourceAsStream("/site-slugs.json")){
            siteSlugs=mapper.readValue(is,new TypeReference<Map<String, Map<String, String>>>() {});
        }
    }
    public String buildRemoteOkUrl(List<String>user_skills,String domain_name,Map<String,String>urls){
        String UrlTemplate =urls.get(domain_name);
        int n=user_skills.size();
        String skills="developer";
        String final_url="";
        if(n==0){
            final_url=String.format(UrlTemplate,skills);
            return final_url;
        }
        else{
            Map<String,String>skills2=siteSlugs.get(domain_name);
            if(skills2 != null && skills2.containsKey(user_skills.getFirst())) {
                skills=skills2.get(user_skills.getFirst());
            } else {
                skills=user_skills.getFirst();
            }
            final_url=String.format(UrlTemplate,skills);
        }
        return final_url;
    }
}
