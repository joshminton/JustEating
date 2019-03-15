package com.example.justeating;

public class BusinessType {
    String name;
    Integer id;

    public BusinessType(String name, Integer id){
        this.name = name;
        this.id = id;
    }

    public Integer getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    @Override
    public String toString(){
        return getName();
    }
}
