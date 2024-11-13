package com.yl3k.kbsf.record.service;

import com.yl3k.kbsf.counsel.entity.CounselRoom;
import com.yl3k.kbsf.counsel.entity.UserCounselRoom;
import com.yl3k.kbsf.counsel.repository.CounselRoomRepository;
import com.yl3k.kbsf.counsel.repository.UserCounselRoomRepository;
import com.yl3k.kbsf.summary.repository.SummaryRepository;
import com.yl3k.kbsf.user.entity.User;
import com.yl3k.kbsf.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AnalysisService {

    private final UserRepository userRepository;
    private final UserCounselRoomRepository userCounselRoomRepository;
    private final CounselRoomRepository counselRoomRepository;
    private final SummaryRepository summaryRepository;

    //연령대 분석 - 전체
    public Map<String, Long> calculateConsultationCountByAgeGroup() {
        List<UserCounselRoom> consultations = userCounselRoomRepository.findAll();

        // 각 상담 기록에서 User의 birth_date를 사용하여 나이를 계산하고 연령대를 나눕니다.
        Map<String, Long> ageGroupCounts = consultations.stream()
                .map(consultation -> calculateAgeGroup(consultation.getUser().getBirthDate()))
                .collect(Collectors.groupingBy(ageGroup -> ageGroup, Collectors.counting()));

        return ageGroupCounts;
    }

    //연령대 분석 - 날짜별
    public Map<String, Long> calculateConsultationCountByAgeRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<UserCounselRoom> consultations = userCounselRoomRepository.findByConsultationDateRange(startDateTime, endDateTime);

        Map<String, Long> ageGroupCounts = consultations.stream()
                .map(consultation -> calculateAgeGroup(consultation.getUser().getBirthDate()))
                .collect(Collectors.groupingBy(ageGroup -> ageGroup, Collectors.counting()));

        return ageGroupCounts;
    }

    //상담 발생 시간 분석 - 전체
    public Map<String, Long> getCounsultationCountBy30Min(){
        List<CounselRoom> consultations = counselRoomRepository.findAll();

        Map<String, Long> countsByInterval = consultations.stream()
                .collect(Collectors.groupingBy(consultation -> {
                    LocalDateTime createdAt = consultation.getCreatedAt();
                    int minute = createdAt.getMinute();
                    int intervalStart = (minute / 30) * 30;
                    LocalDateTime interval = createdAt.withMinute(intervalStart).withSecond(0).withNano(0);
                    return interval.format(DateTimeFormatter.ofPattern("HH:mm"));
                }, Collectors.counting()));

        return countsByInterval;
    }

    //상담 발생 시간 분석 - 날짜별
    public Map<String, Long> getConsultationCountBy30MinRange(LocalDateTime startDateTime, LocalDateTime endDateTime){
        List<CounselRoom> consultations = counselRoomRepository.findByConsultationDateRange(startDateTime, endDateTime);

        Map<String, Long> countsByInterval = consultations.stream()
                .collect(Collectors.groupingBy(consultation -> {
                    LocalDateTime createdAt = consultation.getCreatedAt();
                    int minute = createdAt.getMinute();
                    int intervalStart = (minute / 30) * 30;
                    LocalDateTime interval = createdAt.withMinute(intervalStart).withSecond(0).withNano(0);
                    return interval.format(DateTimeFormatter.ofPattern("HH:mm"));
                }, Collectors.counting()));

        return countsByInterval;
    }

    //상담 평균 시간 분석 - 전체
    public Duration getAverageConsultationTime(){
        List<CounselRoom> consultations = counselRoomRepository.findAll();

        List<Duration> durations = consultations.stream()
                .map(consultation -> Duration.between(consultation.getStartedAt(), consultation.getClosedAt()))
                .collect(Collectors.toList());

        Duration totalDuration = durations.stream().reduce(Duration.ZERO, Duration::plus);
        long averageSeconds = durations.isEmpty()? 0 : totalDuration.getSeconds()/durations.size();
        return Duration.ofSeconds(averageSeconds);
    }

    //상담 평균 시간 분석 - 날짜별
    public Duration getAverageCounsultationTimeRange(LocalDateTime startDateTime, LocalDateTime endDateTime){
        List<CounselRoom> consultations = counselRoomRepository.findByConsultationDateRange(startDateTime,endDateTime);

        List<Duration> durations = consultations.stream()
                .map(consultation -> Duration.between(consultation.getStartedAt(), consultation.getClosedAt()))
                .collect(Collectors.toList());

        Duration totalDuration = durations.stream().reduce(Duration.ZERO, Duration::plus);
        long averageSeconds = durations.isEmpty()? 0 : totalDuration.getSeconds()/durations.size();
        return Duration.ofSeconds(averageSeconds);
    }
    

    private String calculateAgeGroup(LocalDate birthDate) {
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < 20) return "20세 미만";
        else if (age < 30) return "20대";
        else if (age < 40) return "30대";
        else if (age < 50) return "40대";
        else return "50세 이상";
    }
}
