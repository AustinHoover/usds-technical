package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.titlesummary.TitleSummaryEntry;

public interface TitleSummaryRepository extends JpaRepository<TitleSummaryEntry, Integer> {

    @Query("SELECT t FROM TitleSummaryEntry t WHERE t.number = :number")
    TitleSummaryEntry findByNumber(@Param("number") Integer number);

}
