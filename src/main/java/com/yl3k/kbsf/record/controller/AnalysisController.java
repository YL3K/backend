package com.yl3k.kbsf.record.controller;

import com.yl3k.kbsf.record.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/record/analysis")
public class AnalysisController {

    private final AnalysisService analysisService;

    @GetMapping("/age")
    public ResponseEntity<Map<String, Long>> getConsultationCountByAgeGroup() {
        Map<String, Long> ageGroupCounts = analysisService.calculateConsultationCountByAgeGroup();
        return ResponseEntity.ok(ageGroupCounts);
    }

    @GetMapping("/age/range/{startDate}/{endDate}")
    public ResponseEntity<Map<String, Long>> getConsultationCountByAgeRange(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        Map<String, Long> ageGroupCounts = analysisService.calculateConsultationCountByAgeRange(startDateTime, endDateTime);
        return ResponseEntity.ok(ageGroupCounts);
    }

    @GetMapping("/time")
    public ResponseEntity<Map<String, Long>> getCounsultationCountsBy30Min(){
        Map<String, Long> countsByInterval = analysisService.getCounsultationCountBy30Min();
        return ResponseEntity.ok(countsByInterval);
    }

    @GetMapping("/time/range/{startDate}/{endDate}")
    public ResponseEntity<Map<String, Long>> getCounsultationCountsBy30MinRange(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        Map<String, Long> countsByInterval = analysisService.getConsultationCountBy30MinRange(startDateTime, endDateTime);
        return ResponseEntity.ok(countsByInterval);
    }

//    @GetMapping("/runtime")
//    public ResponseEntity<Duration> getAverageConsultationTime(){
//        Duration totalDuration = analysisService.getAverageConsultationTime();
//        return ResponseEntity.ok(totalDuration);
//    }
//
//    @GetMapping("/runtime/range/{startDate}/{endDate}")
//    public ResponseEntity<Duration> getAverageConsultationTimeRange(
//            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){
//        LocalDateTime startDateTime = startDate.atStartOfDay();
//        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
//
//        Duration totalDuration = analysisService.getAverageCounsultationTimeRange(startDateTime, endDateTime);
//        return ResponseEntity.ok(totalDuration);
//    }

    @GetMapping("/runtime/range/{startYearMonth}/{endYearMonth}")
    public ResponseEntity<Map<YearMonth, Duration>> getMonthlyAverageConsultation(
            @PathVariable @DateTimeFormat(pattern = "yyyy.MM") YearMonth startYearMonth,
            @PathVariable @DateTimeFormat(pattern = "yyyy.MM") YearMonth endYearMonth) {
        Map<YearMonth, Duration> response = analysisService.getMonthlyAverageConsultationTimes(startYearMonth, endYearMonth);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/count/range/{startYearMonth}/{endYearMonth}")
    public ResponseEntity<Map<YearMonth, Long>> getMonthlyConsultationCount(
            @PathVariable @DateTimeFormat(pattern = "yyyy.MM") YearMonth startYearMonth,
            @PathVariable @DateTimeFormat(pattern = "yyyy.MM") YearMonth endYearMonth) {
        Map<YearMonth, Long> response = analysisService.getMonthlyConsultationCount(startYearMonth, endYearMonth);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/keywords")
    public ResponseEntity<List<Map<String, Object>>> getKeywordCounts() {
        List<Map<String, Object>> response = analysisService.getKeywordCounts();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/keywords/range/{startDate}/{endDate}")
    public ResponseEntity<List<Map<String, Object>>> getKeywordCountsRange(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Map<String, Object>> response = analysisService.getKeywordCountsRange(startDateTime, endDateTime);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/keywords/top5/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getTop5KeywordsWithUrl(@PathVariable Long userId){
        List<Map<String, Object>> topKeywords = analysisService.getTop5Keywords(userId);
        return ResponseEntity.ok(topKeywords);
    }

    @GetMapping("/keywords/recent/{userId}")
    public ResponseEntity<List<Object[]>> getKeywordRecent(@PathVariable Long userId) {
        List<Object[]> keywords = analysisService.getKeywordsRecent(userId);

        if (keywords.isEmpty()) {
            return ResponseEntity.noContent().build(); // 데이터가 없을 경우 204 반환
        }

        return ResponseEntity.ok(keywords); // 데이터가 있을 경우 200 반환
    }
}
