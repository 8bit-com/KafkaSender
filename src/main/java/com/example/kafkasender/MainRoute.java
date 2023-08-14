package com.example.kafkasender;

import com.github.f4b6a3.uuid.UuidCreator;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Service;

@Service
public class MainRoute extends RouteBuilder {

    private Long id = 32L;

    @Override
    public void configure() throws Exception {
        from("timer://" + "name" + "?fixedRate=true&period=1&repeatCount=1")
                .process(exchange -> {
                    KafkaDto messageDto = new KafkaDto();
                    messageDto.setTo("eyJzaWQiOjYxMTM4OCwibWlkIjoiODg1ODM1YWMtZmUwMC0xMWVjLTg0MTgtNTI1NDAwMDFhYzMyIiwidGNkIjoiNGQ2ZmNkZTItY2IyNC00YjE5LWFjMDktODkyNGFmMThiN2E0IiwiZW9sIjowLCJzbGMiOiJpaXMuZWNwLnJ1X3pucF8xLjAuMF9SZXF1ZXN0IiwibW5tIjoiVTQ4MjkwMSIsIm5zIjoidXJuOi8vaWlzLmVjcC5ydS96bnAvMS4wLjAifQ==");
                    messageDto.setDtoMessage("ru.gov.pfr.ecp.rostrud.test.root.request.TestRostrudUchetRequestFromScmzToWso");
                    messageDto.setMessageId(UuidCreator.getTimeBased().toString());
                    messageDto.setTaskId(id);
                    messageDto.setDaEntityId("32b480f6-2fa6-11ee-8012-510ae52db55f");
                    messageDto.setSendXmlGuid("cf9f4f57-f9c9-4b25-8514-46ba7428be14");
//                    if (id==32L)
//                        id = 47L;
//                    else id = 32L;
                    exchange.getIn().setBody(messageDto);
                })
                .marshal().json(JsonLibrary.Jackson, KafkaDto.class)
                .log("Send kafka messsage: ${body}")
                .to("kafka:ecp_smev_adapter_send_service?brokers=172.18.32.223:9092");
    }
}
