package com.example.JobTracker.globalexception;

import java.util.HashMap;

public class InValidUrl extends RuntimeException{
    public InValidUrl(String msg){
        super(msg);
    }
}
