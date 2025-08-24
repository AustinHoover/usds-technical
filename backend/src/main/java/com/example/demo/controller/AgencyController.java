package com.example.demo.controller;

import com.example.demo.model.agency.AgencyModel;
import com.example.demo.repository.AgencyRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/agencies")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@Slf4j
public class AgencyController {

    @Autowired
    private AgencyRepository agencyRepository;

    /**
     * Get all agency IDs
     * @return List of all agency IDs
     */
    @GetMapping("/ids")
    public ResponseEntity<List<Integer>> getAllAgencyIds() {
        try {
            List<Integer> agencyIds = agencyRepository.findAll()
                .stream()
                .map(AgencyModel::getId)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(agencyIds);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get information about an agency from its ID
     * @param id The agency ID
     * @return Agency information
     */
    @GetMapping("/{id}")
    public ResponseEntity<AgencyModel> getAgencyById(@PathVariable Integer id) {
        try {
            log.info("Getting agency by id: {}", id);
            return agencyRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get all agencies where parent is null (top-level agencies)
     * @return List of top-level agencies
     */
    @GetMapping("/top-level")
    public ResponseEntity<List<AgencyModel>> getTopLevelAgencies() {
        try {
            List<AgencyModel> topLevelAgencies = agencyRepository.findAll()
                .stream()
                .filter(agency -> agency.getParent() == -1)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(topLevelAgencies);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get summary information for all agencies: id, parent, display_name
     * @return List of agency summaries
     */
    @GetMapping("/summary")
    public ResponseEntity<List<Map<String, Object>>> getAgencySummaries() {
        log.info("Getting agency summaries");
        try {
            List<Map<String, Object>> summaries = agencyRepository.findAll()
                .stream()
                .map(agency -> {
                    Map<String, Object> summary = Map.of(
                        "id", agency.getId(),
                        "parent", agency.getParent(),
                        "display_name", agency.getDisplay_name()
                    );
                    return summary;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(summaries);
        } catch (Exception e) {
            log.error("Error getting agency summaries", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
