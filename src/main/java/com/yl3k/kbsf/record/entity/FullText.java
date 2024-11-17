package com.yl3k.kbsf.record.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "fullTextDB")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FullText {
    @Id
    private String  id;

    private Long roomId;
    private String fullText;
}
