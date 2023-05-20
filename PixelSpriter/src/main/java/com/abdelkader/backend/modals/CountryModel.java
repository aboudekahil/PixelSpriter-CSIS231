package com.abdelkader.backend.modals;

public record CountryModel(int id, String name, String iso) {
    @Override
    public String toString() {
        return name;
    }
}