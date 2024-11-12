package com.yl3k.kbsf.stt.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yl3k.kbsf.global.openai.service.OpenAiService;
import com.yl3k.kbsf.global.response.error.ApplicationError;
import com.yl3k.kbsf.global.response.exception.ApplicationException;
import com.yl3k.kbsf.stt.collection.FullText;
import com.yl3k.kbsf.stt.dto.SttResponse;
import com.yl3k.kbsf.stt.repository.FullTextRepository;
import lombok.RequiredArgsConstructor;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class SttService {

    private final FullTextRepository fullTextRepository;

    private final OpenAiService openAiService;

    private final AmazonS3Client amazonS3Client;

    @Value("${ncloud.objectStorage.bucket}")
    private String BUCKET;

    @Value("${ncloud.clovaSpeech.secretKey}")
    private String SECRET_KEY;

    @Value("${ncloud.clovaSpeech.invokeUrl}")
    private String INVOKE_URL;

    private final Gson gson = new Gson();

    public SttResponse speechToText(MultipartFile file, Long roomId) {

        String uploadFileName = uploadFile(file);
        String text = executeStt(uploadFileName);
        insertFullText(roomId, text);

        return SttResponse.builder().text(text).build();
    }

    private String uploadFile(MultipartFile file) {

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) {
            throw new ApplicationException(ApplicationError.FILE_NAME_NULL);
        }
        String uploadFileName = getUuidFileName(originalFileName);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(BUCKET, uploadFileName, inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (Exception e) {
            throw new ApplicationException(ApplicationError.FILE_UPLOAD_ERROR);
        }

        return uploadFileName;
    }

    private String executeStt(String dataKey) {

        Header[] HEADERS = new Header[] {
                new BasicHeader("Accept", "application/json"),
                new BasicHeader("X-CLOVASPEECH-API-KEY", SECRET_KEY),
        };

        HttpPost httpPost = new HttpPost(INVOKE_URL + "/recognizer/object-storage");
        httpPost.setHeaders(HEADERS);

        Map<String, Object> body = new HashMap<>();
        body.put("dataKey", dataKey);
        body.put("language", "ko-KR");
        body.put("completion", "sync");
        body.put("fullText", false);

        StringEntity httpEntity = new StringEntity(gson.toJson(body), ContentType.APPLICATION_JSON);
        httpPost.setEntity(httpEntity);

        try (final CloseableHttpResponse httpResponse = HttpClients.createDefault().execute(httpPost)) {
            return formatSttResponse(EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new ApplicationException(ApplicationError.STT_ERROR);
        }
    }

    private void insertFullText(Long roomId, String text) {

        try {
            FullText fullText = FullText.builder()
                    .roomId(roomId)
                    .fullText(text)
                    .build();
            fullTextRepository.insert(fullText);
        } catch (Exception e) {
            throw new ApplicationException(ApplicationError.MONGO_INSERT_ERROR);
        }
    }

    private String formatSttResponse(String jsonResponse) {

        StringBuilder formattedConversation = new StringBuilder();

        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonArray segments = jsonObject.getAsJsonArray("segments");
        JsonArray speakers = jsonObject.getAsJsonArray("speakers");

        String roles = getSpeakerRoles(segments);
        Map<String, String> speakerMap = makeSpeakerMap(speakers, roles);

        segments.forEach(segmentElement -> {
            JsonObject segment = segmentElement.getAsJsonObject();
            String speakerLabel = segment.getAsJsonObject("diarization").get("label").getAsString();
            String speakerRole = speakerMap.getOrDefault(speakerLabel, "알 수 없음");
            String textEdited = segment.get("textEdited").getAsString();
            formattedConversation.append(speakerRole).append(": ").append(textEdited).append("\n");
        });

        return formattedConversation.toString().trim();
    }

    private String getUuidFileName(String fileName) {

        String ext = fileName.substring(fileName.indexOf(".") + 1);
        return UUID.randomUUID() + "." + ext;
    }

    private String getSpeakerRoles(JsonArray segments) {

        if (segments.size() < 2) {
            throw new ApplicationException(ApplicationError.INSUFFICIENT_SEGMENTS);
        }

        JsonObject firstSegment = segments.get(0).getAsJsonObject();
        JsonObject secondSegment = segments.get(1).getAsJsonObject();

        String firstText = firstSegment.get("textEdited").getAsString();
        String secondText = secondSegment.get("textEdited").getAsString();

        String question = "다음 대화 내용에서 1과 2 중 누가 상담사이고 누가 고객인지 알려줘." +
                "1: " + firstText +  ", 2: " + secondText +
                "다른 말, 기호 붙이지 말고 JSON 형식으로 둘 중 하나 골라서 대답해." +
                "{'1': '상담사', '2': '고객'} 또는 {'1': '고객', '2': '상담사'}";

        return openAiService.askOpenAi(question);
    }

    private Map<String, String> makeSpeakerMap(JsonArray speakers, String roles) {

        Map<String, String> speakerRoleMap = new HashMap<>();
        try {
            JsonObject answerObject = JsonParser.parseString(roles.replace("'", "\"")).getAsJsonObject();
            answerObject.entrySet().forEach(entry -> {
                String speakerNumber = entry.getKey();
                String role = entry.getValue().getAsString();
                speakerRoleMap.put(speakerNumber, role);
            });
        } catch (Exception e) {
            throw new ApplicationException(ApplicationError.PARSING_ERROR);
        }

        Map<String, String> speakerMap = new HashMap<>();
        speakers.forEach(speakerElement -> {
            JsonObject speaker = speakerElement.getAsJsonObject();
            String label = speaker.get("label").getAsString();
            String role = speakerRoleMap.getOrDefault(label, "알 수 없음");
            speakerMap.put(label, role);
        });

        return speakerMap;
    }
}
