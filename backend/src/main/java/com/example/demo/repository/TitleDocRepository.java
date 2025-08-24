package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.titledoc.TitleDoc;

public interface TitleDocRepository extends JpaRepository<TitleDoc, String> {

    TitleDoc findByTitle(String title);
}
