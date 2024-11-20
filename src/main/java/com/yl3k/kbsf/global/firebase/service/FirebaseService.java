package com.yl3k.kbsf.global.firebase.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yl3k.kbsf.global.firebase.dto.*;
import com.yl3k.kbsf.global.response.error.ApplicationError;
import com.yl3k.kbsf.global.response.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class FirebaseService {

    @Value("${google.refreshToken}")
    private String GOOGLE_REFRESH_TOKEN;

    @Value("${google.clientId}")
    private String GOOGLE_CLIENT_ID;

    @Value("${google.clientSecret}")
    private String GOOGLE_CLIENT_SECRET;

    @Value("${google.firebase.projectId}")
    private String PROJECT_ID;

    private final RestTemplate restTemplate;

    public void sendSummaryCompleteNotification(NotificationRequest notificationRequest) throws IOException {
        sendNotification(notificationRequest, "요약 완료", " 고객님의 상담 요약이 완료되었어요.");
    }

    public void sendWaitingCompleteNotification(NotificationRequest notificationRequest) throws IOException {
        sendNotification(notificationRequest, "대기 완료", " 고객님의 상담 순서가 되었습니다. 3분 내로 입장하지 않으면 상담이 취소됩니다.");
    }

    private void sendNotification(NotificationRequest notificationRequest, String title, String notificationMessage) throws IOException {

        String fcmToken = notificationRequest.getFcmToken();
        String userName = notificationRequest.getUserName();
        String googleAccessToken = getGoogleAccessToken();

        HttpHeaders headers = createHeaders(googleAccessToken);
        Notification notification = createNotification(title, userName + notificationMessage);

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

    private Notification createNotification(String  title, String body) {

        return Notification.builder()
                .title(title)
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

    private String getGoogleAccessToken() throws IOException {

        HttpURLConnection connection = getHttpURLConnection();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }

            ObjectMapper objectMapper = new ObjectMapper();
            GoogleOauth2Response googleOauth2Response = objectMapper.readValue(response.toString(), GoogleOauth2Response.class);

            return googleOauth2Response.getAccessToken();
        } catch (Exception e) {
            throw new ApplicationException(ApplicationError.GOOGLE_ACCESS_TOKEN_ERROR);
        }
    }

    private HttpURLConnection getHttpURLConnection() throws IOException {

        String redirectUri = "http://localhost:8080/authcode";
        String grantType = "refresh_token";

        URL url = new URL("https://oauth2.googleapis.com/token");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        String body = "refresh_token=" + GOOGLE_REFRESH_TOKEN +
                "&client_id=" + GOOGLE_CLIENT_ID +
                "&client_secret=" + GOOGLE_CLIENT_SECRET +
                "&redirect_uri=" + redirectUri +
                "&grant_type=" + grantType;

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = body.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        return connection;
    }
}
