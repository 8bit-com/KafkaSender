package com.example.kafkasender;

import lombok.Data;

@Data
public class KafkaDto {
    private String messageId;
    private Long taskId;
    private String to;
    private String daEntityId;
    private String sendXmlGuid;
    private String dtoMessage;
}
