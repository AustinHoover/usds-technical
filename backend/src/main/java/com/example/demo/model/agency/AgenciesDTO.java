package com.example.demo.model.agency;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AgenciesDTO {
    List<Agency> agencies;

    public List<Agency> getAgencies() {
        return agencies;
    }
    public void setAgencies(List<Agency> agencies) {
        this.agencies = agencies;
    }
}
