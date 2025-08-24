package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.ScraperService;
import com.example.demo.repository.TitleVersionRepository;
import com.example.demo.model.titlever.TitleVersion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RestController
@RequestMapping("/api/scraper")
@RequiredArgsConstructor
@Slf4j
public class ScraperController {
    
    private final ScraperService scraperService;
    private final TitleVersionRepository titleVersionRepository;
    
    /**
     * Triggers the scraper service to run immediately
     * @return ResponseEntity with success/error status
     */
    @PostMapping("/trigger")
    public ResponseEntity<String> triggerScraper() {
        try {
            log.info("Manual scraper trigger requested");
            scraperService.scrapeAndPersist();
            log.info("Manual scraper execution completed successfully");
            return ResponseEntity.ok("Scraper executed successfully");
        } catch (Exception e) {
            log.error("Error during manual scraper execution", e);
            return ResponseEntity.internalServerError()
                .body("Error executing scraper: " + e.getMessage());
        }
    }
    
    /**
     * Debug endpoint to test TitleVersion repository methods
     * @param date The date to search for
     * @param identifier The identifier to search for
     * @param title The title to search for
     * @return ResponseEntity with search results
     */
    @GetMapping("/debug/search")
    public ResponseEntity<String> debugSearch(
            @RequestParam String date,
            @RequestParam String identifier,
            @RequestParam String title) {
        try {
            log.info("Debug search requested for: date='{}', identifier='{}', title='{}'", date, identifier, title);
            
            // Test the original method
            List<TitleVersion> results1 = titleVersionRepository.findByDateAndIdentifierAndTitle(date, identifier, title);
            log.info("Original method found {} results", results1.size());
            
            // Test the custom query method
            List<TitleVersion> results2 = titleVersionRepository.findTitleVersionsByDateIdentifierAndTitle(date, identifier, title);
            log.info("Custom query method found {} results", results2.size());
            
            // Test the exists method
            boolean exists = titleVersionRepository.existsByDateAndIdentifierAndTitle(date, identifier, title);
            log.info("Exists method returned: {}", exists);
            
            // Get all TitleVersions for comparison
            List<TitleVersion> allVersions = titleVersionRepository.findAll();
            log.info("Total TitleVersions in database: {}", allVersions.size());
            
            StringBuilder response = new StringBuilder();
            response.append("Debug Search Results:\n");
            response.append("Original method: ").append(results1.size()).append(" results\n");
            response.append("Custom query method: ").append(results2.size()).append(" results\n");
            response.append("Exists method: ").append(exists).append("\n");
            response.append("Total TitleVersions in DB: ").append(allVersions.size()).append("\n\n");
            
            if (!results1.isEmpty()) {
                response.append("Results from original method:\n");
                for (TitleVersion tv : results1) {
                    response.append("  ").append(tv.toString()).append("\n");
                }
            }
            
            if (!results2.isEmpty()) {
                response.append("Results from custom query method:\n");
                for (TitleVersion tv : results2) {
                    response.append("  ").append(tv.toString()).append("\n");
                }
            }
            
            return ResponseEntity.ok(response.toString());
            
        } catch (Exception e) {
            log.error("Error during debug search", e);
            return ResponseEntity.internalServerError()
                .body("Error during debug search: " + e.getMessage());
        }
    }
    
    /**
     * Debug endpoint to list all TitleVersions
     * @return ResponseEntity with all TitleVersions
     */
    @GetMapping("/debug/all")
    public ResponseEntity<String> debugAllTitleVersions() {
        try {
            List<TitleVersion> allVersions = titleVersionRepository.findAll();
            StringBuilder response = new StringBuilder();
            response.append("All TitleVersions (").append(allVersions.size()).append(" total):\n\n");
            
            for (TitleVersion tv : allVersions) {
                response.append(tv.toString()).append("\n");
            }

            return ResponseEntity.ok(response.toString());
            
        } catch (Exception e) {
            log.error("Error retrieving all TitleVersions", e);
            return ResponseEntity.internalServerError()
                .body("Error retrieving all TitleVersions: " + e.getMessage());
        }
    }
}
