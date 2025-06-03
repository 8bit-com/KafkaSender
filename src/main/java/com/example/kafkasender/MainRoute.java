package com.example.kafkasender;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangeProperty;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.component.kafka.KafkaEndpoint;
import org.apache.camel.component.kafka.consumer.KafkaManualCommit;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;
import ru.gov.pfr.ecp.iis.smev.adapter.springtarantoolapi.model.SmevMessageReceived.*;
import ru.gov.pfr.ecp.iis.smev.adapter.springtarantoolapi.service.SmevMessageReceivedService;

import java.sql.Array;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

@Service
@AllArgsConstructor
public class MainRoute extends RouteBuilder {

    private static Integer id = 1;
    private static final String ROUTE_ID = "18";

    private final SmevMessageReceivedService smevMessageReceivedService;

    private ExecutorService customExecutorService;


    
    

    @Override
    public void configure() throws Exception {
        AtomicLong counter = new AtomicLong();
        AtomicLong startTime = new AtomicLong(System.currentTimeMillis());

//        from("kafka:ecp_smev_adapter_send_service_test?brokers=172.18.32.223:9092" +
//                "&groupId=" + ROUTE_ID + "&consumersCount=30" +
//                "&autoCommitEnable=false&allowManualCommit=true&maxPollIntervalMs=1000")
//                .id(ROUTE_ID)
////                .process(exchange -> {
////                    Long kafkaOffset = exchange.getIn().getHeader(KafkaConstants.OFFSET, Long.class);
////                    Integer kafkaPartition = exchange.getIn().getHeader(KafkaConstants.PARTITION, Integer.class);
////                    System.out.println(kafkaOffset);
////                    dequeKafkaOffset.add(kafkaOffset);
////                    dequeKafkaPartition.add(kafkaPartition);
////                })
//                .log("${body}")
//                .process(exchange -> {
//                    Thread.sleep(10000);
//                })
//                .bean(this, "commitKafka");
//
////        from("timer://" + ROUTE_ID + "?period=10000")
////                .bean(this, "commitKafka")
////                .stop();

//        from("timer://" + "name" + "?period=3000") //fixedRate=true&period=1000&repeatCount=100
//                .process(exchange -> {
//                    KafkaDto messageDto = new KafkaDto();
//                    messageDto.setMessageId(UuidCreator.getTimeBased().toString()); //     79710dc8-f98f-11ee-a3ac-971e796cbf64
//                    messageDto.setTaskId(252L);
//                    messageDto.setDtoMessage("ru.gov.pfr.ecp.ebr.rpu.fns_ecp_ip_2_01.iis.DohflSuperRequestFromScmzToWso");
//                    messageDto.setTraceContext("a10c66c6adb2a734-101b5d297fbb9388-0-e20a70a46922d3fc");
//                    messageDto.setMessageContent("CpQCCiYKJDAwM2ViNjc2LTFiNDUtNDM1OC1hMGFhLWE3MjJhZDdkZjc1OBIDCOcPGpIBCg4KDDc0MDQxNzU4ODUzNBIQCg4xNjQtNDE1LTQyNSA1NBoMCgoxOTk2LTEwLTA5IjwKFAoS0J/QoNCe0KXQntCg0J7QktCQEgwKCtCc0JDQoNCY0K8aFgoU0JLQmNCi0JDQm9Cs0JXQktCd0JAqIgoECgIyMRIMCgo3NTE2ODg4MTg3GgwKCjIwMTYtMTAtMTMiUAomCiQwMDNlYjY3Ni0xYjQ1LTQzNTgtYTBhYS1hNzIyYWQ3ZGY3NTgaJgokMDAzZWI2NzYtMWI0NS00MzU4LWEwYWEtYTcyMmFkN2RmNzU4");
//                    messageDto.setSmevNamespaceId(37);
//                    exchange.getIn().setBody(messageDto);
//                })
//                .marshal().json(JsonLibrary.Jackson, KafkaDto.class)
//                .to("kafka:ecp_smev_adapter_send_service_test?brokers=172.18.32.223:9092")
//                .log("Send kafka messsage: ${body}");

//        from("timer://" + "name" + "?period=1") //fixedRate=true&period=1000&repeatCount=100
//                .process(exchange -> {
//                    TarantoolLogsDto dto = new TarantoolLogsDto();
//                    dto.setMessageId(UuidCreator.getTimeBased().toString());
//                    dto.setAckPriority(85);
//                    dto.setProxyId(58);
//                    dto.setTraceContext("8330b241d17a7010-e355e7076d4301ae-0-4dfb213595fc7d39");
//                    dto.setTypeCode("IIS_SMEV3_INPUT");
//                    dto.setDaEntityId("2fdb607a-fe44-11ee-92c8-0242ac120002");
//                    exchange.getIn().setBody(dto);
//                })
//                .marshal().json(JsonLibrary.Jackson, TarantoolLogsDto.class)
//                .to("kafka:ecp_smev_adapter_ack_service_test_idea?brokers=172.18.32.223:9092")
//                .log("Send kafka messsage: ${body}");

        from("timer://loadgen?fixedRate=true&period=1")
                .setBody().constant("Trigger")
                .to("seda:parallel-processing");

        from("seda:parallel-processing?concurrentConsumers=1000&limitConcurrentConsumers=false")
                .threads().executorService(customExecutorService)
                .process(exchange -> {
                    long count = counter.incrementAndGet();

                    // Каждые 1000 сообщений логгируем скорость
                    if (count % 1000 == 0) {
                        long now = System.currentTimeMillis();
                        long elapsed = now - startTime.getAndSet(now); // в миллисекундах
                        double rate = 1000.0 / (elapsed / 1000.0); // msg/sec
                        System.out.println("Processed 1000 messages in " + elapsed + " ms. Rate: " + rate + " msg/sec");
                    }


                    Attachment[] attachments = new Attachment[0];
                    SaveSMEVMessageOutputModel dto = SaveSMEVMessageOutputModel.builder()
                            .messageId(UuidCreator.getTimeBased().toString())
                            .proxyId(41L)
                            .smevMethodId(9)
                            .deliveryTimeStamp(ZonedDateTime.now())
                            .originalMessageId("4fce19a2-3fb4-11f0-a415-ef3fc7d7caf3")
                            .smevNamespace("urn://x-artefacts-mchs/mchs-ubvo-info-rights-boat/root/1.0.1")
                            .traceContext("4ed82e0a7d679810-373ee67a4331ea59-1-4ed82e0a7d679810")
                            .dtoMessage("ru.gov.pfr.ecp.ebr.rpu.mchs.mchs_ubvo_info_rights_boat_no_projection.root._1_0_1.MchsUbvoInfoRightsBoatNoProjectionResponseFromWsoToScmz")
                            .daEntityId("123d2357-211c-11f0-837f-8364e29fedac")
                            .smevNamespaceId(19L)
                            .processingTopicName("ecp_smev_base_processing_to_IIS")
                            .smevXmlGuid("s01_202506p05-da-newtest_d38353b7-f3ec-47cd-aa28-da8fc3f62967")
                            .attachments(attachments)
                            .build();
                    Boolean exist = smevMessageReceivedService.checkExistSMEVMessage(new CheckExistSMEVMessageModel(dto.getMessageId(), dto.getTraceContext()));

                    //smevMessageReceivedService.saveSMEVMessageOutput(dto);

                    CompletableFuture.runAsync(() -> {
                        smevMessageReceivedService.saveSMEVMessageOutput(dto);
                    });

                    exchange.getIn().setBody(dto);
                });
    }

    public void commitKafka(Exchange exchange){
        log.info("Commit offset");
        var manualCommit = exchange.getIn().getHeader(KafkaConstants.MANUAL_COMMIT, KafkaManualCommit.class);
        manualCommit.commit();
    }
}
