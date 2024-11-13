package com.yl3k.kbsf.record.controller;

import com.yl3k.kbsf.record.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
}
