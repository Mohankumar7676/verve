package com.identifierservice.verve.service.impl;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.identifierservice.verve.dto.request.IdentifierFireRequest;
import com.identifierservice.verve.dto.request.IdentifierRequest;
import com.identifierservice.verve.dto.response.IdentifierResponse;
import com.identifierservice.verve.enums.IdentifierResponseStatus;
import com.identifierservice.verve.service.CacheService;
import com.identifierservice.verve.service.IdentifierService;
import com.identifierservice.verve.service.ProducerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class IdentifierServiceImpl implements IdentifierService {

    private static final Logger logger = LoggerFactory.getLogger(IdentifierService.class);
    private final CacheService cacheService;
    private final RestTemplate restTemplate;
    private final ProducerService producerService;
    private final Set<Integer> uniqueIdentifiers = ConcurrentHashMap.newKeySet();

    @Value("${verve.first.extension.enabled:false}")
    private boolean isExtension1enabled;

    @Value("${verve.second.extension.enabled:false}")
    private boolean isExtension2enabled;

    @Override
    public IdentifierResponse processAcceptRequest(IdentifierRequest identifierRequest) {
        IdentifierResponse response = IdentifierResponse.builder().status(IdentifierResponseStatus.SUCCESS.status).build();
        try {
            extensionSwitchHandlerForSetCount(identifierRequest);
            if (identifierRequest.getEndPoint() != null) {
                extensionSwitchHandlerForRequest(identifierRequest);
            }
        }  catch(Exception e) {
            log.error("[processAcceptRequest] request failed to process with errorMessage: {}", e.getMessage());
            response.setStatus(IdentifierResponseStatus.FAILED.status);
        }
        return response;
    }

    @Override
    public void manageUniqueIdentifierCount() {
        producerService.publishUniqueCount(getCountByextensionSwitchHandler());
        uniqueIdentifiers.clear();
    }

    private void extensionSwitchHandlerForRequest(IdentifierRequest identifierRequest) {
        if(isExtension1enabled) {
            sendUniqueCountPost(identifierRequest);
            return;
        }
        sendUniqueCountGet(identifierRequest);
    }

    private void extensionSwitchHandlerForSetCount(IdentifierRequest identifierRequest) {
        if(isExtension2enabled) {
            cacheService.addUniqueIdentifiers(identifierRequest.getId());
            return;
        }
        uniqueIdentifiers.add(identifierRequest.getId());
    }

    private Long getCountByextensionSwitchHandler() {
        if(isExtension2enabled) {
            return cacheService.getUniqueIdentifiersCount();
        }
        return (long) uniqueIdentifiers.size();
    }

    private void sendUniqueCountGet(IdentifierRequest identifierRequest) {
        String endPoint = identifierRequest.getEndPoint() + "?uniqueCount=" + getCountByextensionSwitchHandler();
        ResponseEntity<Object> response = restTemplate.getForEntity(endPoint, Object.class);
        logger.info("[sendUniqueCountGet] response Status Code: {}", response.getStatusCode());
    }

    private void sendUniqueCountPost(IdentifierRequest identifierRequest) {
        IdentifierFireRequest request = IdentifierFireRequest.builder().uniqueCount(getCountByextensionSwitchHandler()).build();
        ResponseEntity<Object> response = restTemplate.postForEntity(identifierRequest.getEndPoint(), request, Object.class);
        logger.info("[sendUniqueCountPost] response Status Code: {}", response.getStatusCode());
    }

}
