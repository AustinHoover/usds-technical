package com.example.demo.model.agency;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "agencies"
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgencyModel {
    
    @Id
    @Column(name = "id")
    Integer id;

    @Column(name = "name")
    String name;

    @Column(name = "short_name")
    String short_name;

    @Column(name = "display_name")
    String display_name;

    @Column(name = "sortable_name")
    String sortable_name;

    @Column(name = "slug")
    String slug;

    @Column(name = "parent")
    Integer parent = -1;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getSortable_name() {
        return sortable_name;
    }

    public void setSortable_name(String sortable_name) {
        this.sortable_name = sortable_name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

}
