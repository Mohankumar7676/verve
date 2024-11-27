package com.identifierservice.verve.service;

import com.identifierservice.verve.dto.request.IdentifierRequest;
import com.identifierservice.verve.dto.response.IdentifierResponse;

public interface IdentifierService {

    IdentifierResponse processAcceptRequest(IdentifierRequest idRequest);
    void manageUniqueIdentifierCount();
    
}
