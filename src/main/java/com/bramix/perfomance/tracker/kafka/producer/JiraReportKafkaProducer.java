package com.bramix.perfomance.tracker.kafka.producer;


import com.atlassian.jira.rest.client.api.domain.Issue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JiraReportKafkaProducer {

    @Value("${topic.name.producer}")
    private String topicName;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void send(List<Iterable<Issue>> resultOfIteration){
        log.info("Payload enviado: {}", resultOfIteration);
        try {
            kafkaTemplate.send(topicName, objectMapper.writeValueAsString(resultOfIteration));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}