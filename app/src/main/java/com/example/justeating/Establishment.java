package com.example.justeating;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Establishment implements Serializable {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    private String name;
    private String type;
    private String addr1;
    private String addr2;
    private String addr3;
    private String addr4;
    private String postcode;
    private String phoneNo;

    private String rating;
    private String hygieneScore;
    private String structuralScore;
    private String confidenceScore;

    private double latitude;
    private double longitude;

    private Date dateRated;

    public Establishment(String name){
        this.name = name;
    }

    public String getAddr1() {
        return addr1;
    }

    public void setAddr1(String addr1) {
        this.addr1 = addr1;
    }

    public String getAddr2() {
        return addr2;
    }

    public void setAddr2(String addr2) {
        this.addr2 = addr2;
    }

    public String getAddr3() {
        return addr3;
    }

    public void setAddr3(String addr3) {
        this.addr3 = addr3;
    }

    public String getAddr4() {
        return addr4;
    }

    public void setAddr4(String addr4) {
        this.addr4 = addr4;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getHygieneScore() {
        return hygieneScore;
    }

    public void setHygieneScore(String hygieneScore) {
        this.hygieneScore = hygieneScore;
    }

    public String getStructuralScore() {
        return structuralScore;
    }

    public void setStructuralScore(String structuralScore) {
        this.structuralScore = structuralScore;
    }

    public String getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(String confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        if(latitude == "null"){
            this.latitude = Double.MAX_VALUE;
        } else {
            this.latitude = Double.parseDouble(latitude);
        }
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        if(longitude == "null"){
            this.longitude = Double.MAX_VALUE;
        } else {
            this.longitude = Double.parseDouble(longitude);
        }
    }

    public Date getDateRated() {
        return dateRated;
    }

    public void setDateRated(String dateRated) {
        DateFormat format = new SimpleDateFormat("YYYY-MM-DD'T'MM:HH:SS", Locale.ENGLISH);
        try {
            this.dateRated = format.parse(dateRated);
        } catch (ParseException e) {
            System.out.println("Broken date parsing for " + name);
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
