package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.titlever.TitleVersion;

public interface TitleVersionRepository extends JpaRepository<TitleVersion, Integer>, TitleVersionRepositoryCustom {

    List<TitleVersion> findByTitle(String title);

    List<TitleVersion> findByDateAndIdentifier(String date, String identifier);

    // Fixed method name to match exact field name
    List<TitleVersion> findByDateAndIdentifierAndTitle(String date, String identifier, String title);
    
    // Custom query method for better debugging
    @Query("SELECT tv FROM TitleVersion tv WHERE tv.date = :date AND tv.identifier = :identifier AND tv.title = :title")
    List<TitleVersion> findTitleVersionsByDateIdentifierAndTitle(
        @Param("date") String date, 
        @Param("identifier") String identifier, 
        @Param("title") String title
    );
    
    @Query("SELECT COUNT(tv) > 0 FROM TitleVersion tv WHERE tv.date = :date AND tv.identifier = :identifier AND tv.title = :title")
    boolean existsByDateAndIdentifierAndTitle(String date, String identifier, String title);

    @Query("SELECT COUNT(tv) > 0 FROM TitleVersion tv WHERE tv.issue_date = :issueDate AND tv.identifier = :identifier AND tv.title = :title")
    boolean existsByIssueDateAndIdentifierAndTitle(String issueDate, String identifier, String title);

}

interface TitleVersionRepositoryCustom {
    int bulkInsertIgnoreConflicts(List<TitleVersion> rows);
}
