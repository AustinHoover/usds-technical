package com.example.demo.model.titlesummary;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

@Entity
@Table(name = "title_summary_entry")
public class TitleSummaryEntry {
    
    @Id
    @Column(name = "number")
    Integer number;

    @Column(name="name")
    String name;

    @Column(name="latest_amended_on")
    String latest_amended_on;

    @Column(name="latest_issue_date")
    String latest_issue_date;

    @Column(name="up_to_date_as_of")
    String up_to_date_as_of;

    @Column(name="reserved")
    Boolean reserved;

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatest_amended_on() {
        return latest_amended_on;
    }

    public void setLatest_amended_on(String latest_amended_on) {
        this.latest_amended_on = latest_amended_on;
    }

    public String getLatest_issue_date() {
        return latest_issue_date;
    }

    public void setLatest_issue_date(String latest_issue_date) {
        this.latest_issue_date = latest_issue_date;
    }

    public String getUp_to_date_as_of() {
        return up_to_date_as_of;
    }

    public void setUp_to_date_as_of(String up_to_date_as_of) {
        this.up_to_date_as_of = up_to_date_as_of;
    }

    public Boolean getReserved() {
        return reserved;
    }

    public void setReserved(Boolean reserved) {
        this.reserved = reserved;
    }

}
