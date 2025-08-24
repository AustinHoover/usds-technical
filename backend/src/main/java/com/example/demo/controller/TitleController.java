package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.repository.TitleVersionRepository;
import com.example.demo.repository.TitleSummaryRepository;
import com.example.demo.model.titlever.TitleVersion;
import com.example.demo.model.titlesummary.TitleSummaryEntry;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/titles")
public class TitleController {
    
    @Autowired
    private TitleVersionRepository titleVersionRepository;
    
    @Autowired
    private TitleSummaryRepository titleSummaryRepository;
    
    @GetMapping
    public ResponseEntity<List<TitleSummaryEntry>> getAllTitles() {
        try {
            List<TitleSummaryEntry> allTitles = titleSummaryRepository.findAll();
            return ResponseEntity.ok(allTitles);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/summary")
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
}
