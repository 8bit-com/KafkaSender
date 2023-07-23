package com.example.kafkasender;

import com.github.f4b6a3.uuid.UuidCreator;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Service;

@Service
public class MainRoute extends RouteBuilder {

    private Long id = 7L;

    @Override
    public void configure() throws Exception {
        //from("timer://" + "name" + "?repeatCount=2")
        from("timer://" + "name" + "?fixedRate=true&period=1&repeatCount=2")
                .process(exchange -> {
                    MessageDto messageDto = new MessageDto();
                    messageDto.setTo("eyJzaWQiOjYxMTM4OCwibWlkIjoiODg1ODM1YWMtZmUwMC0xMWVjLTg0MTgtNTI1NDAwMDFhYzMyIiwidGNkIjoiNGQ2ZmNkZTItY2IyNC00YjE5LWFjMDktODkyNGFmMThiN2E0IiwiZW9sIjowLCJzbGMiOiJpaXMuZWNwLnJ1X3pucF8xLjAuMF9SZXF1ZXN0IiwibW5tIjoiVTQ4MjkwMSIsIm5zIjoidXJuOi8vaWlzLmVjcC5ydS96bnAvMS4wLjAifQ==");
                    messageDto.setDtoMessage("ru.gov.pfr.ecp.rostrud.test.root.TestRostrudUchetRequestFromScmzToWso");
                    messageDto.setMessageId(UuidCreator.getTimeBased().toString());
                    messageDto.setTaskId(id);
                    messageDto.setDaEntityId("7c1f9b25-5737-4b7d-a151-b488807c0500");
                    messageDto.setSendXmlGuid("5886b447-4093-417d-bf8e-9be2bba1ace9");
                    if (id==8L)
                        id = 7L;
                    else id = 8L;
                    exchange.getIn().setBody(messageDto);
                })
                .marshal().json(JsonLibrary.Jackson, MessageDto.class)
                .log("Send kafka messsage: ${body}")
                .to("kafka:ecp_smev_adapter_send_service?brokers=172.18.32.223:9092");
    }
}
