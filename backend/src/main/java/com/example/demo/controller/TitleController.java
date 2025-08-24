package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.repository.TitleVersionRepository;

import lombok.extern.slf4j.Slf4j;

import com.example.demo.repository.TitleSummaryRepository;
import com.example.demo.model.titlever.TitleVersion;
import com.example.demo.model.titlesummary.TitleSummaryEntry;
import com.example.demo.model.titledoc.TitleDoc;
import com.example.demo.repository.TitleDocRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/titles")
@Slf4j
public class TitleController {
    
    @Autowired
    private TitleVersionRepository titleVersionRepository;

    @Autowired
    private TitleDocRepository titleDocRepository;
    
    @Autowired
    private TitleSummaryRepository titleSummaryRepository;
    
    @GetMapping
    @Cacheable(value = "titles", key = "#root.methodName")
    public ResponseEntity<List<TitleSummaryEntry>> getAllTitles() {
        try {
            List<TitleSummaryEntry> allTitles = titleSummaryRepository.findAll();
            return ResponseEntity.ok(allTitles);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/summary")
    @Cacheable(value = "titleSummaries", key = "#root.methodName")
    public ResponseEntity<List<Map<String, Object>>> getTitleSummaries() {
        try {
            List<TitleSummaryEntry> allTitles = titleSummaryRepository.findAll();
            
            List<Map<String, Object>> summariesWithCounts = allTitles.stream()
                .map(title -> {
                    Map<String, Object> summary = new HashMap<>();
                    
                    // Add all the original TitleSummaryEntry fields
                    summary.put("number", title.getNumber());
                    summary.put("name", title.getName());
                    summary.put("latest_amended_on", title.getLatest_amended_on());
                    summary.put("latest_issue_date", title.getLatest_issue_date());
                    summary.put("up_to_date_as_of", title.getUp_to_date_as_of());
                    summary.put("reserved", title.getReserved());
                    
                    // Add the version count by querying TitleVersion repository
                    List<TitleVersion> titleVersions = titleVersionRepository.findByTitle(title.getNumber().toString());
                    long versionCount = titleVersions.stream()
                        .map(TitleVersion::getIssue_date)
                        .filter(date -> date != null && !date.trim().isEmpty())
                        .distinct()
                        .count();
                    
                    summary.put("version_count", versionCount);
                    
                    return summary;
                })
                .collect(Collectors.toList());
            return ResponseEntity.ok(summariesWithCounts);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/{titleNumber}/issue-dates")
    public ResponseEntity<List<String>> getUniqueIssueDates(@PathVariable String titleNumber) {
        try {
            // Find all TitleVersion entries for the given title
            List<TitleVersion> titleVersions = titleVersionRepository.findByTitle(titleNumber);
            
            // Extract unique issue dates, filtering out null values
            List<String> uniqueIssueDates = titleVersions.stream()
                .map(TitleVersion::getIssue_date)
                .filter(date -> date != null && !date.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(uniqueIssueDates);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{titleNumber}/advanced-stats")
    @Cacheable(value = "advancedStats", key = "#titleNumber")
    public ResponseEntity<Map<String, Object>> getAdvancedStats(@PathVariable String titleNumber) {
        try {
            Map<String, Object> stats = new HashMap<>();
            TitleDoc titleDoc = titleDocRepository.findByTitle(titleNumber);
            stats.put("size", titleDoc.getSize());
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
