package com.example.weatherapp;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class OwmClient {
    private static final String apiKey = "&APPID=6818219c6c0d1bfb83279d20f1865c0f";
    private static final String metric = "&units=metric";
    private static final String imperial = "&units=imperial";

    //OWM API call for forecast weather
    //api.openweathermap.org/data/2.5/forecast/daily?q={city name},{country code}&cnt={cnt}
    private static final String forecastURL = "https://api.openweathermap.org/data/2.5/forecast?q=";
    private static final String forecastCount = "&cnt=5";

    //OWM API call for current weather
    //api.openweathermap.org/data/2.5/weather?q={city name}
    //api.openweathermap.org/data/2.5/weather?q={city name},{country code}
    private static final String currentURL = "https://api.openweathermap.org/data/2.5/weather?q=";

    //OWM API call for hourly weather
    //api.openweathermap.org/data/2.5/forecast/hourly?q={city name},{country code}
    private static final String hourlyURL = "https://api.openweathermap.org/data/2.5/forecast?q=";
    private String myLocation = "Denver,US";
    private String iconURL = "http://openweathermap.org/img/wn/";
    private boolean units = true;
    private final String fahren = " F";
    private final String celsius = " C";


    public String currURL() throws MalformedURLException {
        //if units true use imperial
        if (units == false) {
            URL url = new URL(currentURL + myLocation + apiKey + metric);
            return url.toString();
        } else {
            URL url = new URL(currentURL + myLocation + apiKey + imperial);
            return url.toString();
        }

    }

    public String forecastURL() throws MalformedURLException {
        if (units == false) {
            URL url = new URL(forecastURL + myLocation  + apiKey + metric);
            return url.toString();
        } else {
            URL url = new URL(forecastURL + myLocation + apiKey + imperial);
            return url.toString();
        }
    }

    public String hourlyURL() throws MalformedURLException {
        if (units == false) {
            URL url = new URL(hourlyURL + myLocation + apiKey + forecastCount + metric);
            return url.toString();
        } else {
            URL url = new URL(hourlyURL + myLocation + apiKey + forecastCount + imperial);
            return url.toString();
        }
    }

    public boolean setLocation(String aLocation) {
        if (aLocation != null) {
            myLocation = aLocation;
            return true;
        } else
            return false;
    }
    public void setUnits(boolean entry){
        if(entry == true){
            units = true;
        }
        else if( entry == false){
            units = false;
        }
    }
    public String getLocation() {
        return myLocation;
    }

    public String getUnits() {
        if(units == true){
            return fahren;
        }
        else{
            return celsius;
        }
    }
    public boolean unitsBool(){
        return units;
    }

    public boolean setIconURL(String iconID){
        if(iconID == ""){
            return false;
        }
        else{
            iconURL = iconURL + iconID + ".png";
            return true;
        }
    }
    public String getIconURL(){
        return iconURL;
    }

}

