package com.example.demo.service;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.agency.AgenciesDTO;
import com.example.demo.model.agency.Agency;
import com.example.demo.model.agency.AgencyModel;
import com.example.demo.model.titledoc.TitleDoc;
import com.example.demo.model.titlesummary.TitleSummary;
import com.example.demo.model.titlesummary.TitleSummaryEntry;
import com.example.demo.model.titlever.TitleVersion;
import com.example.demo.model.titlever.TitleVersions;
import com.example.demo.repository.AgencyRepository;
import com.example.demo.repository.TitleSummaryRepository;
import com.example.demo.repository.TitleVersionRepository;
import com.example.demo.repository.TitleDocRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ScraperService {

    private final TitleSummaryRepository titleSummaryRepository;
    private final TitleVersionRepository titleVersionRepository;
    private final TitleDocRepository titleDocRepository;
    private final AgencyRepository agencyRepository;

    @Autowired
    public ScraperService(TitleSummaryRepository titleSummaryRepository, TitleVersionRepository titleVersionRepository, TitleDocRepository titleDocRepository, AgencyRepository agencyRepository) {
        this.titleSummaryRepository = titleSummaryRepository;
        this.titleVersionRepository = titleVersionRepository;
        this.titleDocRepository = titleDocRepository;
        this.agencyRepository = agencyRepository;
    }

    @Transactional
    public void scrapeAndPersist() {

        TitleSummary titleSummary = this.getAllTitles();

        //for each title out there, get the versions and save them
        for(TitleSummaryEntry titleSummaryEntry : titleSummary.getTitles()){
            if(titleSummaryRepository.findByNumber(titleSummaryEntry.getNumber()) != null){
                continue;
            }
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

                //probably a reserved title, skip it
                continue;
            }
            log.info("Getting summary for title: " + titleSummaryEntry.getNumber());
            String content = this.getTitleAtTime(titleSummaryEntry.getNumber(), titleSummaryEntry.getLatest_issue_date());
            TitleDoc titleDoc = new TitleDoc();
            titleDoc.setTitle(titleSummaryEntry.getNumber().toString());
            titleDoc.setDate(titleSummaryEntry.getLatest_issue_date());
            titleDoc.setContent(content);
            titleDoc.setUrl("https://www.ecfr.gov/api/versioner/v1/structure/" + titleSummaryEntry.getLatest_issue_date() + "/title-" + titleSummaryEntry.getNumber() + ".json");
            titleDoc.setSize(0);
            //parse data from the structure
            if(content != null){
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    JsonNode parsed = objectMapper.readTree(content);
                    titleDoc.setSize(parsed.get("size").asInt(0));
                } catch (JsonProcessingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }


            titleDocRepository.save(titleDoc);
        }


        //get data for all agencies
        List<AgencyModel> agencies = this.getAgencies();
        agencyRepository.saveAll(agencies);
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
     * @return The title at the specific date
     */
    private String getTitleAtTime(Integer titleNumber, String date){
        String content = null;
        URLConnection connection = null;
        try {
            connection =  new URL("https://www.ecfr.gov/api/versioner/v1/structure/" + date + "/title-" + titleNumber + ".json").openConnection();
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\Z");
            content = scanner.next();
            scanner.close();
        }catch ( Exception ex ) {
            ex.printStackTrace();
        }
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

    private List<AgencyModel> getAgencies(){
        log.info("Get the data for all agencies");
        String content = null;
        URLConnection connection = null;
        ObjectMapper objectMapper = new ObjectMapper();
        AgenciesDTO agencyDTO = null;
        List<AgencyModel> agencies = new ArrayList<>();
        try {
            connection =  new URL("https://www.ecfr.gov/api/admin/v1/agencies.json").openConnection();
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\Z");
            content = scanner.next();
            scanner.close();
            agencyDTO = objectMapper.readValue(content, AgenciesDTO.class);
            AtomicInteger idIncrementer = new AtomicInteger(1);
            for(Agency agency : agencyDTO.getAgencies()){
                assignIdsToAgencies(null, agency, idIncrementer, agencies);
            }
        }catch ( Exception ex ) {
            ex.printStackTrace();
        }
        log.info("Agency data fetched");
        return agencies;
    }

    private void assignIdsToAgencies(AgencyModel parent, Agency dtoObj, AtomicInteger idIncrementer, List<AgencyModel> agencies){
        AgencyModel agencyModel = new AgencyModel();
        agencyModel.setId(idIncrementer.getAndIncrement());
        agencyModel.setName(dtoObj.getName());
        agencyModel.setShort_name(dtoObj.getShort_name());
        agencyModel.setDisplay_name(dtoObj.getDisplay_name());
        agencyModel.setSortable_name(dtoObj.getSortable_name());
        agencyModel.setSlug(dtoObj.getSlug());
        if(parent != null){
            agencyModel.setParent(parent.getId());
        } else {
            agencyModel.setParent(-1);
        }
        agencies.add(agencyModel);
        if(dtoObj.getChildren() != null){
            for(Agency child : dtoObj.getChildren()){
                assignIdsToAgencies(agencyModel, child, idIncrementer, agencies);
            }
        }
    }


}