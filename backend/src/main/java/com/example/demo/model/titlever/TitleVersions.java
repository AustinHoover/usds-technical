package com.example.demo.model.titlever;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TitleVersions {
    
    private List<TitleVersion> content_versions;

    public List<TitleVersion> getContent_versions() {
        return content_versions;
    }

    public void setContent_versions(List<TitleVersion> content_versions) {
        this.content_versions = content_versions;
    }

}
