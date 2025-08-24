package com.example.demo.model.agency;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Agency {
    
    Integer id;

    String name;

    String short_name;

    String display_name;

    String sortable_name;

    String slug;

    List<Agency> children;

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

    public List<Agency> getChildren() {
        return children;
    }

    public void setChildren(List<Agency> children) {
        this.children = children;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
