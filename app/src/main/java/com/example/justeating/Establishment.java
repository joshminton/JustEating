package com.example.justeating;

public class Establishment {
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

    public Establishment(String name){
        this.name = name;
    }
}
