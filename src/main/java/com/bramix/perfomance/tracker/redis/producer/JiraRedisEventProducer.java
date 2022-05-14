package com.bramix.perfomance.tracker.redis.producer;

import com.atlassian.jira.rest.client.api.domain.Issue;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JiraRedisEventProducer {

    @Value("jira.stream.name")
    private String streamKey;

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @SneakyThrows
    public void send(List<Issue> resultOfIteration){

        ObjectRecord<String, String> record = StreamRecords.newRecord()
                .in(streamKey)
                .ofObject(objectMapper.writeValueAsString(resultOfIteration))
                .withId(RecordId.autoGenerate());

        redisTemplate.opsForStream()
                .add(record);
    }

}