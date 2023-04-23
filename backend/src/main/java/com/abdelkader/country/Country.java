package com.abdelkader.country;

import jakarta.persistence.*;

@Entity(name = "countries")
@Table(name = "countries")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String  name;
    private String  iso;


    public Country() {
    }

    public Country(Integer id, String name, String iso) {
        this.id = id;
        this.name = name;
        this.iso = iso;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }
}
