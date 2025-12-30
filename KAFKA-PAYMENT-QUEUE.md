# Kafka Payment Queue Implementation

This document explains the Spring Cloud Stream Kafka implementation for queueing bank payments when the bank service is unavailable.

## Architecture Overview

### Flow Diagram
```
User Payment Request
        ↓
FacturationService.pay()
        ↓
    [Check Bank Available?]
        ↓
    YES ──→ Direct Payment ──→ Status: PAID
        ↓
    NO
        ↓
Send to Kafka Stream ──→ Status: QUEUED
        ↓
   [bank-payment-queue]
        ↓
PaymentStreamConsumer
        ↓
   [Bank Enabled?]
        ↓
    YES ──→ Process + Retry (max 3)
        ↓
    NO ──→ Skip (remains QUEUED)
        ↓
Success ──→ Status: PAID
Fail ──→ Retry (max 3) ──→ Status: FAILED
```

## Components

### 1. **PaymentEvent** (`billing/dto/PaymentEvent.java`)
DTO for payment messages sent through Kafka:
- `factureId`: Invoice ID
- `paymentMethodId`: Bank account ID
- `amount`: Payment amount
- `retryCount`: Number of retry attempts

### 2. **BankService** (`payment/service/BankService.java`)
Simulates bank payment processing:
- **70% availability rate** (for testing)
- `processBankPayment()`: Attempts to process payment
- `setBankAvailable(boolean)`: Manual toggle for testing

### 3. **PaymentStreamProducer** (`billing/service/PaymentStreamProducer.java`)
Sends payment events to Kafka using Spring Cloud Stream's `StreamBridge`:
- Uses binding: `paymentProducer-out-0`
- Topic: `bank-payment-queue`

### 4. **PaymentStreamConsumer** (`billing/service/PaymentStreamConsumer.java`)
Consumes payment events from Kafka using functional programming model:
- Implements `Consumer<PaymentEvent>` bean
- Retries failed payments up to **3 times**
- Marks as `FAILED` after max retries

### 5. **FacturationService** (Updated)
Integrates payment logic:
- Tries direct bank payment first
- If bank unavailable → sends to Kafka stream → sets status to `QUEUED`
- Non-bank payments (Telecom) → processed directly

## Setup Instructions

### 1. Start Kafka with Docker Compose

```bash
# Start Kafka and Zookeeper
docker-compose up -d

# Verify containers are running
docker ps

# Expected output:
# - smet-kafka (port 9092)
# - smet-zookeeper (port 2181)
```

### 2. Build and Run the Application

```bash
# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

The application will:
- Connect to Kafka at `localhost:9092`
- Auto-create the `bank-payment-queue` topic
- Start consuming messages

## Testing the Payment Queue

### Using Postman

Import `TourismWorkflow.postman_collection.json` and follow the workflow:

#### 1. Register & Login
```
POST /api/auth/register
POST /api/auth/login
```

#### 2. Set Preferences & Add Payment Method
```
PUT /api/profiles/me/preferences
POST /api/profiles/me/payments/bank
```

#### 3. Create Reservation
```
POST /api/hotels/reserve
```

#### 4. Pay Invoice (This triggers Kafka logic)
```
POST /api/factures/{{factureId}}/pay
```

### Expected Behavior

**Scenario 1: Bank Available (70% chance)**
- Direct payment succeeds immediately
- Status: `PENDING` → `PAID`
- No Kafka message sent

**Scenario 2: Bank Unavailable**
- Payment sent to Kafka queue
- Status: `PENDING` → `QUEUED`
- Consumer skips processing while the bank is disabled
- When the bank is enabled, queued invoices are re-sent and processed
- On success: `QUEUED` → `PAID`
- On failure (while enabled): Retries up to 3 times → `FAILED`

### Monitoring Logs

Watch the application logs to see the queue in action:

```bash
# Successful direct payment
2025-01-01 10:00:00 INFO  FacturationService - Attempting bank payment for facture 1
2025-01-01 10:00:00 INFO  BankService - Bank availability check: AVAILABLE
2025-01-01 10:00:00 INFO  BankService - Bank payment SUCCESSFUL - IBAN: DE89..., Amount: 450.0
2025-01-01 10:00:00 INFO  FacturationService - Bank payment successful for facture 1

# Queued payment
2025-01-01 10:00:00 INFO  FacturationService - Attempting bank payment for facture 2
2025-01-01 10:00:00 INFO  BankService - Bank availability check: UNAVAILABLE
2025-01-01 10:00:00 WARN  FacturationService - Bank unavailable for facture 2. Sending to stream queue.
2025-01-01 10:00:00 INFO  PaymentStreamProducer - Sending payment event to stream: factureId=2
2025-01-01 10:00:00 INFO  PaymentStreamConsumer - Received payment event from stream: factureId=2, retryCount=0
2025-01-01 10:00:00 INFO  PaymentStreamConsumer - Payment processed successfully via stream: factureId=2
```

## Manual Testing

### Toggle Bank Availability

You can manually control bank availability for testing:

```java
// In a test controller or during debugging
@Autowired
private BankService bankService;

// Disable bank service
bankService.setBankAvailable(false);

// Now all payments will go to Kafka queue and remain QUEUED

// Re-enable bank service (queued invoices are re-sent to Kafka)
bankService.setBankAvailable(true);
```

### Check Kafka Topics

```bash
# Access Kafka container
docker exec -it smet-kafka bash

# List topics
kafka-topics --list --bootstrap-server localhost:9092

# Expected output:
# bank-payment-queue

# View messages in topic
kafka-console-consumer --bootstrap-server localhost:9092 \
  --topic bank-payment-queue \
  --from-beginning
```

## Configuration

### Spring Cloud Stream Bindings (`application.yml`)

```yaml
spring:
  cloud:
    function:
      definition: paymentConsumer  # Name of consumer bean
    stream:
      bindings:
        paymentProducer-out-0:     # Producer binding
          destination: bank-payment-queue
          content-type: application/json
        paymentConsumer-in-0:      # Consumer binding
          destination: bank-payment-queue
          group: smet-payment-group
      kafka:
        binder:
          brokers: localhost:9092
          auto-create-topics: true
```

### Key Configuration Points

- **Function Definition**: Must match the bean name (`paymentConsumer`)
- **Binding Names**: Follow pattern `<functionName>-<in|out>-<index>`
- **Group**: Ensures only one consumer processes each message
- **Content Type**: JSON serialization/deserialization

## Payment States

| State | Description |
|-------|-------------|
| `PENDING` | Invoice created, awaiting payment |
| `QUEUED` | Bank unavailable, payment queued in Kafka |
| `PAID` | Payment successful (direct or via queue) |
| `FAILED` | Payment failed after 3 retries |

## Troubleshooting

### Kafka Not Connecting
```
Error: Connection refused: localhost:9092
```
**Solution**: Ensure Kafka is running
```bash
docker-compose ps
docker-compose up -d
```

### Messages Not Being Consumed
```
No consumer processing messages
```
**Solution**: Check function definition matches bean name
```yaml
spring.cloud.function.definition: paymentConsumer
```

### Serialization Errors
```
Could not deserialize payload
```
**Solution**: Verify content-type is `application/json` in both bindings

## Cleanup

```bash
# Stop Kafka and Zookeeper
docker-compose down

# Remove volumes (clears all data)
docker-compose down -v
```

## Academic Notes

This implementation demonstrates:
- **Event-Driven Architecture**: Asynchronous message processing
- **Resilience Patterns**: Retry logic, graceful degradation
- **Spring Cloud Stream**: Abstraction over messaging systems
- **Kafka**: Distributed message broker
- **Functional Programming**: Consumer bean pattern

Perfect for understanding microservices communication patterns!
