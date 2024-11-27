package com.identifierservice.verve.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.identifierservice.verve.dto.request.IdentifierFireRequest;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProducerService {

    private static final Logger logger = LoggerFactory.getLogger(ProducerService.class);
    private static ObjectMapper mapper = new ObjectMapper();
    private final KafkaTemplate<String, IdentifierFireRequest> kafkaTemplate;

    @Value("${verve.unique.identifier.count.publish.topic:unique-identifiers}")
    private String publishTopic;

    @Value("${verve.third.extension.enabled:false}")
    private boolean isExtension3enabled;

    @PostConstruct
    void init() {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Async("verve-async-thread")
    public void publishUniqueCount(Long uniqueCount) {
        logger.info("Unique request count in the last minute: {}", uniqueCount);
        IdentifierFireRequest request = IdentifierFireRequest.builder().uniqueCount(uniqueCount).build();
        try {
            if(isExtension3enabled) {
                kafkaTemplate.send(publishTopic, request);
            }
        } catch(Exception e) {
            logger.error("Failed to publish unique count with errorMessage: {}", e.getMessage());
        }
    }

}
