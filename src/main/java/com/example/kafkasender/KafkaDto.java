package com.example.kafkasender;

import lombok.Data;

@Data
public class KafkaDto {
    private String messageId;
    private Long taskId;
    private String dtoMessage;
    private String traceContext;
    private Integer smevNamespaceId;
    private String messageContent;
}
