package com.identifierservice.verve.enums;

public enum IdentifierResponseStatus {
    
    SUCCESS("ok"),
    FAILED("failed");
    
    public final String status;

    IdentifierResponseStatus(String status) {
        this.status = status;
    }
}
