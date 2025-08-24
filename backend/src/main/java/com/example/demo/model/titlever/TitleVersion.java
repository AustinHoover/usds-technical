package com.example.demo.model.titlever;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;

@Entity
@Table(
    name = "title_version",
    uniqueConstraints = @UniqueConstraint(columnNames = {"issue_date", "identifier", "title"})
)
public class TitleVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "date")
    private String date;

    @Column(name = "amendment_date")
    private String amendment_date;

    @Column(name = "issue_date")
    private String issue_date;

    @Column(name = "identifier")
    private String identifier;

    @Column(name = "name", length = 4096)
    private String name;

    @Column(name = "part")
    private String part;

    @Column(name = "substantive")
    private Boolean substantive;

    @Column(name = "removed")
    private Boolean removed;

    @Column(name = "subpart")
    private String subpart;

    @Column(name = "title")
    private String title;

    @Column(name = "type")
    private String type;

    // Default constructor
    public TitleVersion() {}

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmendment_date() {
        return amendment_date;
    }

    public void setAmendment_date(String amendment_date) {
        this.amendment_date = amendment_date;
    }

    public String getIssue_date() {
        return issue_date;
    }

    public void setIssue_date(String issue_date) {
        this.issue_date = issue_date;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public Boolean getSubstantive() {
        return substantive;
    }

    public void setSubstantive(Boolean substantive) {
        this.substantive = substantive;
    }

    public Boolean getRemoved() {
        return removed;
    }

    public void setRemoved(Boolean removed) {
        this.removed = removed;
    }

    public String getSubpart() {
        return subpart;
    }

    public void setSubpart(String subpart) {
        this.subpart = subpart;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "TitleVersion{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", identifier='" + identifier + '\'' +
                ", title='" + title + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
