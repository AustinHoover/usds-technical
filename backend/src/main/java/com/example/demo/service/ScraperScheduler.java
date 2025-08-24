package com.example.demo.service;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScraperScheduler {
    private final ScraperService scraperService;

    private final AtomicBoolean running = new AtomicBoolean(false);

    // Every 10 minutes, with initial delay
    @Scheduled(fixedRateString = "${scrape.fixed-rate-ms:600000}",
               initialDelayString = "${scrape.initial-delay-ms:15000}")
    public void run() {
        log.info("ScraperScheduler running");
        if(!running.compareAndSet(false, true)) {
            return;
        }
        try {
            scraperService.scrapeAndPersist();
        } finally {
            running.set(false);
        }
        log.info("ScraperScheduler completed");
    }
}