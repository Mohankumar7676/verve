package com.identifierservice.verve.service;

public interface CacheService {
    boolean addUniqueIdentifiers(Integer identifier);
    long getUniqueIdentifiersCount();
}
