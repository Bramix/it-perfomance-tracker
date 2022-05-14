package com.bramix.perfomance.tracker.configuration;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfiguration {

    @Bean
    public ObjectMapper createObjectMapper() {
        return new ObjectMapper()
                .setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL)
                .disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);
    }
}
