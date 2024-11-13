package com.yl3k.kbsf.global.firebase.service;

import com.yl3k.kbsf.global.firebase.dto.MessageRequest;
import com.yl3k.kbsf.global.firebase.dto.Message;
import com.yl3k.kbsf.global.firebase.dto.Notification;
import com.yl3k.kbsf.global.firebase.dto.NotificationRequest;
import com.yl3k.kbsf.global.response.error.ApplicationError;
import com.yl3k.kbsf.global.response.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class FirebaseService {

    @Value("${google.firebase.projectId}")
    private String PROJECT_ID;

    private final RestTemplate restTemplate;

    public void sendSummaryCompleteNotification(NotificationRequest notificationRequest) {
        sendNotification(notificationRequest, " 고객님의 상담 요약이 완료되었어요.");
    }

    public void sendWaitingCompleteNotification(NotificationRequest notificationRequest) {
        sendNotification(notificationRequest, " 고객님의 상담 순서가 되었습니다. 3분 내로 입장하지 않으면 상담이 취소됩니다.");
    }

    private void sendNotification(NotificationRequest notificationRequest, String notificationMessage) {

        String fcmToken = notificationRequest.getFcmToken();
        String userName = notificationRequest.getUserName();
        String googleAccessToken = getGoogleAccessToken();

        HttpHeaders headers = createHeaders(googleAccessToken);
        Notification notification = createNotification(userName + notificationMessage);

        Message messageRequest = Message.builder()
                .token(fcmToken)
                .notification(notification)
                .build();

        MessageRequest message = MessageRequest.builder().message(messageRequest).build();

        sendNotification(message, headers);
    }

    private HttpHeaders createHeaders(String googleAccessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + googleAccessToken);
        return headers;
    }

    private Notification createNotification(String body) {

        return Notification.builder()
                .title("KB 스타후르츠뱅크")
                .body(body)
                .build();
    }

    private void sendNotification(MessageRequest message, HttpHeaders headers) {

        String FCM_API_URL = "https://fcm.googleapis.com/v1/projects/" + PROJECT_ID + "/messages:send";
        HttpEntity<MessageRequest> request = new HttpEntity<>(message, headers);

        try {
            restTemplate.exchange(
                    FCM_API_URL,
                    HttpMethod.POST,
                    request,
                    String.class
            );
        } catch (Exception e) {
            throw new ApplicationException(ApplicationError.FIREBASE_NOTIFICATION_ERROR);
        }
    }

    private String getGoogleAccessToken() {
        return "googleAccessToken";
    }
}
