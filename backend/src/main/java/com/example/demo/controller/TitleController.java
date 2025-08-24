package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.repository.TitleVersionRepository;
import com.example.demo.model.titlever.TitleVersion;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/titles")
public class TitleController {
    
    @Autowired
    private TitleVersionRepository titleVersionRepository;
    
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
