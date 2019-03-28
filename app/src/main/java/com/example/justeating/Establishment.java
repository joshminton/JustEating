package com.example.justeating;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity
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

    @PrimaryKey
    @NonNull  private Integer id;
    private String authority;

    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "type")
    private String type;
    @ColumnInfo(name = "addr1")
    private String addr1;
    @ColumnInfo(name = "addr2")
    private String addr2;
    @ColumnInfo(name = "addr3")
    private String addr3;
    @ColumnInfo(name = "addr4")
    private String addr4;
    @ColumnInfo(name = "postcode")
    private String postcode;
    @ColumnInfo(name = "phoneNo")
    private String phoneNo;

    @ColumnInfo(name = "rating")
    private String rating;
    @ColumnInfo(name = "hygieneScore")
    private String hygieneScore;
    @ColumnInfo(name = "structuralScore")
    private String structuralScore;
    @ColumnInfo(name = "confidenceScore")
    private String confidenceScore;

    private boolean favourite;

    @ColumnInfo(name = "latitude")
    private double latitude;
    @ColumnInfo(name = "longitude")
    private double longitude;

    private Date dateRated;

    private String schemeType;

    public Establishment(String name, Integer id){
        this.name = name;
        this.id = id;
        favourite = false;
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
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
            setLatitude(Double.MAX_VALUE);
        } else {
            setLatitude(Double.parseDouble(latitude));
        }
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        if(longitude == "null"){
            setLongitude(Double.MAX_VALUE);
        } else {
            setLongitude(Double.parseDouble(longitude));
        }
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Date getDateRated() {
        return dateRated;
    }

    public void setDateRated(String dateRated) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'mm:HH:ss", Locale.ENGLISH);
        try {
            setDateRated(format.parse(dateRated));
        } catch (ParseException e) {
            System.out.println("Broken date parsing for " + name);
        }
    }

    public void setDateRated(Date dateRated){
        this.dateRated = dateRated;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isFavourite(){
        return favourite;
    }

    public void addFavourite(){
        this.favourite = true;
    }

    public void removeFavourite(){
        this.favourite = false;
    }

    public String getSchemeType() {
        return schemeType;
    }

    public void setSchemeType(String schemeType) {
        this.schemeType = schemeType;
    }
}
