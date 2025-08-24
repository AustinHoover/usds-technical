package com.example.demo.service;

import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.titlesummary.TitleSummary;
import com.example.demo.model.titlesummary.TitleSummaryEntry;
import com.example.demo.model.titlever.TitleVersions;
import com.example.demo.repository.TitleSummaryRepository;
import com.example.demo.repository.TitleVersionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ScraperService {

    private final TitleSummaryRepository titleSummaryRepository;
    private final TitleVersionRepository titleVersionRepository;

    @Autowired
    public ScraperService(TitleSummaryRepository titleSummaryRepository, TitleVersionRepository titleVersionRepository) {
        this.titleSummaryRepository = titleSummaryRepository;
        this.titleVersionRepository = titleVersionRepository;
    }

    @Transactional
    public void scrapeAndPersist() {

        TitleSummary titleSummary = this.getAllTitles();

        //for each title out there, get the versions and save them
        for(TitleSummaryEntry titleSummaryEntry : titleSummary.getTitles()){
            titleSummaryRepository.save(titleSummaryEntry);
            TitleVersions titleVersions = this.getVersionsOfTitle(titleSummaryEntry.getNumber());
            //for each version, save it
            if(titleVersions != null && titleVersions.getContent_versions() != null){
                log.info("Saving versions for title: " + titleSummaryEntry.getName());
                log.info("Found " + titleVersions.getContent_versions().size() + " versions");
                int rowsInserted = titleVersionRepository.bulkInsertIgnoreConflicts(titleVersions.getContent_versions());
                log.info("Inserted " + rowsInserted + " rows");
                titleVersionRepository.flush();
            } else {
                log.info("No versions found for title: " + titleSummaryEntry.getName() + " - " + titleSummaryEntry.getNumber());
            }
        }
    }


    /**
     * Gets the summary data for all titles
     * @return The summary data for all titles
     */
    private TitleSummary getAllTitles(){
        log.info("Getting all titles");
        String content = null;
        TitleSummary titleSummary = null;
        URLConnection connection = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            connection =  new URL("https://www.ecfr.gov/api/versioner/v1/titles.json").openConnection();
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\Z");
            content = scanner.next();
            scanner.close();
            titleSummary = objectMapper.readValue(content, TitleSummary.class);
            System.out.println(titleSummary);
        }catch ( Exception ex ) {
            ex.printStackTrace();
        }
        log.info("All titles fetched");
        return titleSummary;
    }

    /**
     * Gets a title at a specific date
     * @param titleNumber The title number
     * @param date The date to get the title at
     * @return The title at the specific date
     */
    private String getTitleAtTime(Integer titleNumber, String date){
        log.info("Getting title at time");
        String content = null;
        URLConnection connection = null;
        try {
            connection =  new URL("https://www.ecfr.gov/api/versioner/v1/full/" + date + "/title-" + titleNumber + ".xml").openConnection();
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\Z");
            content = scanner.next();
            scanner.close();
        }catch ( Exception ex ) {
            ex.printStackTrace();
        }
        log.info("Title at time fetched");
        return content;
    }

    /**
     * Gets the versions of a title
     * @param titleNumber The title number
     * @return The versions of a title
     */
    private TitleVersions getVersionsOfTitle(Integer titleNumber){
        log.info("Getting versions of title " + titleNumber);
        String content = null;
        URLConnection connection = null;
        ObjectMapper objectMapper = new ObjectMapper();
        TitleVersions titleVersions = null;
        try {
            connection =  new URL("https://www.ecfr.gov/api/versioner/v1/versions/title-" + titleNumber + ".json").openConnection();
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\Z");
            content = scanner.next();
            scanner.close();
            titleVersions = objectMapper.readValue(content, TitleVersions.class);
        }catch ( Exception ex ) {
            ex.printStackTrace();
        }
        log.info("Versions of title fetched");
        return titleVersions;
    }


}