package com.example.kafkasender;

import lombok.Data;

@Data
public class TarantoolLogsDto {
    private String traceContext;
    private String typeCode;
    private Integer proxyId;
    private Integer ackPriority;
    private String messageId;
    private String daEntityId;
}
