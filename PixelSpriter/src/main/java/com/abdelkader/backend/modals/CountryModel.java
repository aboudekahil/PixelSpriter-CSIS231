package com.abdelkader.backend.modals;

public record CountryModel(int id, String name) {
    @Override
    public String toString() {
        return name;
    }
}