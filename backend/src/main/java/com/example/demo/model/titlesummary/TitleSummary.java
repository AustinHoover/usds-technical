package com.example.demo.model.titlesummary;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TitleSummary {
    private List<TitleSummaryEntry> titles;

    public List<TitleSummaryEntry> getTitles() {
        return titles;
    }

    public void setTitles(List<TitleSummaryEntry> titles) {
        this.titles = titles;
    }
}
