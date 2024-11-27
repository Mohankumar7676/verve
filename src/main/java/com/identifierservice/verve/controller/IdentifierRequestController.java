package com.identifierservice.verve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.identifierservice.verve.dto.request.IdentifierRequest;
import com.identifierservice.verve.dto.response.IdentifierResponse;
import com.identifierservice.verve.service.IdentifierService;

@RestController
public class IdentifierRequestController {

    @Autowired
    private IdentifierService identifierService;

    @GetMapping("accept")
    public ResponseEntity<String> accept(
        @RequestParam(value = "id") Integer id,
        @RequestParam(value = "endpoint", required = false) String endpoint) {

            IdentifierRequest request = IdentifierRequest.builder()
            .id(id)
            .endPoint(endpoint)
            .build();

        IdentifierResponse response = identifierService.processAcceptRequest(request);

        return ResponseEntity.ok(response.getStatus());
    }

}
