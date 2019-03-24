package com.example.justeating;

public class Authority{
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private Integer id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    private String region;

    private String name;

    public Authority(Integer id, String name, String region) {
        this.id = id;
        this.name = name;
        this.region = region;
    }

    @Override
    public String toString(){
        return name;
    }

}