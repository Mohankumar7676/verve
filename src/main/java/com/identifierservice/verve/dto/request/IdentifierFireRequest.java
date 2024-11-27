package com.identifierservice.verve.dto.request;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdentifierFireRequest {

    private Long uniqueCount;

    @Builder.Default
    private Date currentTime = new Date();
    
}