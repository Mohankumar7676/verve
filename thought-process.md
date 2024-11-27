# Verve Identifier Service Implementation

## Overview

This document outlines the high-level implementation approach, design considerations, and extensions for the **Verve Identifier Service**. The service is designed to handle high requests, manage unique request identifiers, and interact with external endpoints.

---

## Design Approach

### 1. REST API Endpoints

The application exposes a single endpoint:

#### `GET /api/verve/accept`

- **Parameters**:
  - `id` (integer) - A mandatory query parameter.
  - `endpoint` (string) - An optional query parameter.

- **Behavior**:
  - The service processes the request, checks for the uniqueness of the `id`, and returns:
    - `"ok"` if the request is successful.
    - `"failed"` if any error occurs.

- The service also sends the unique request count to an external endpoint, either using a `GET` or `POST` request.

### 2. Request Deduplication

To ensure that requests with the same `id` are not counted multiple times in a minute, the service uses **Redis Set** to track unique identifiers. The Redis set data structure ensures that only unique values are stored.

### 3. Unique Count Logging

The service tracks the number of unique `id` values received within a minute using Redis. Every minute, the application:
- Logs the unique request count using a standard logger.
- Writes to a log file: `logs/unique-requests.log`.
- If we need to increase from current one minute to n minute we can modify using `verve.unique.logInterval`

Each log entry includes the number of unique identifiers received in the last minute.

### 4. External Request Handling

Once the count of unique requests is calculated, the service fires an HTTP request to the provided endpoint (if any) with the unique count as a query parameter.

- **Asynchronous Processing**: The service uses `RestTemplate` for making HTTP requests in a non-blocking manner (via `@Async` annotation), ensuring that the service remains responsive even while waiting for external responses.

### 5. Handling High Throughput (10K requests per second)

- **Redis**: Ensures fast in-memory operations, minimizing latency when checking and storing unique identifiers.
- **Asynchronous Processing**: Ensures non-blocking operations when sending HTTP requests, improving throughput.
- **Stateless Architecture**: The application is stateless in terms of `id` tracking, allowing for horizontal scaling behind a load balancer without affecting deduplication.

---

## Extensions Implementation

### Extension 1: POST Request to External Endpoint

Instead of a `GET` request, the service can be configured to send a `POST` request. The request body contains the unique request count, and the endpoint URL is still provided as a query parameter in the `GET /api/verve/accept` request.

- The content of the `POST` request can be structured in a simple format, such as JSON.
- This extension can be enabled/disabled using the flag `verve.first.extension.enabled`.

### Extension 2: Handling Load Balancer and Deduplication

The application ensures that unique `id` deduplication works across multiple instances of the service running behind a load balancer. This is achieved by using **Redis** as a centralized store for unique `id` tracking, ensuring that all instances of the service share the same data.

- This extension can be enabled/disabled using the flag `verve.second.extension.enabled`.

### Extension 3: Distributed Streaming with Kafka

Instead of logging unique request counts to a file, the application sends the count to a distributed streaming service, **Kafka**. The service publishes messages to a defined Kafka topic, which can be consumed by other services.
- This extension can be enabled/disabled using the flag `verve.third.extension.enabled`.
- Topic to publish message can be set using `verve.unique.identifier.count.publish.topic`

---

## Key Design Considerations

### 1. Performance

- **Redis**: Used for high-performance storage of unique `id` values.
- **Asynchronous HTTP Requests**: Prevents blocking operations and ensures high throughput.

### 2. Scalability

- The service is designed to scale horizontally, with load balancing handled at the infrastructure level.
- **Redis** ensures consistent `id` deduplication across multiple instances of the service.

### 3. Fault Tolerance

- **Kafka**: Provides a reliable distributed streaming service, ensuring that messages are not lost and can be consumed by other services.
- Proper exception handling and logging ensure that errors are captured and addressed.

### 4. Maintainability

- The service is modular, with clear separation of concerns between the controller, service, cache management, and producer layers.
- The use of configuration properties makes it easy to modify external system interactions and behavior without changing the code.
