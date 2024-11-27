package com.identifierservice.verve.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.identifierservice.verve.service.IdentifierService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IdentifierStreamerJob {

    private final IdentifierService identifierService;

    @Scheduled(fixedRateString = "${verve.unique.logInterval:60000}")
    public void logUniqueRequestCount() {
        identifierService.manageUniqueIdentifierCount();
    }

}
