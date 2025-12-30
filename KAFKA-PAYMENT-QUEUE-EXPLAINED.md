# Kafka Payment Queue: Current Implementation Explained

This document explains how Kafka is used in this project for bank payments **as it currently works**, including what happens when the bank is unavailable and why payments do or do not auto-complete later.

## Big Picture: Why Kafka Is Here

Kafka is used as a **temporary queue** when the bank service is unavailable. The goal is to **avoid failing the user request immediately**, and instead **retry asynchronously**.

Important: In the current code, Kafka **does not retry while the bank is disabled**. When the bank is re-enabled, queued invoices are re-sent to Kafka and processed.

## Main Flow (Bank Payment)

1) User calls `POST /api/factures/{id}/pay`
2) `FacturationService.pay()` tries to process the payment directly with the bank.
3) If direct payment succeeds → status becomes `PAID`.
4) If direct payment fails → an event is sent to Kafka and the invoice is set to `QUEUED`.
5) Kafka consumer receives the event and tries to pay (only if the bank is enabled).
6) If it fails while the bank is enabled, it retries **up to 3 times** (by re-sending to Kafka). After 3 failures, it marks the invoice `FAILED`.

## Current Behavior With the Bank Flag

The bank availability is controlled by `BankService.bankAvailable`:

- `bankAvailable = false` → **bank is always considered unavailable**.
- `bankAvailable = true` → bank is available only **70% of the time** (random).

### If you do this:
1) Set `bankAvailable=false`
2) Call `pay`
3) Set `bankAvailable=true`

**Result today:**
- The payment is sent to Kafka but the consumer skips processing while the bank is disabled.
- When you enable the bank (via `POST /api/bank-control/enable` or toggle), all `QUEUED` invoices are re-sent to Kafka.
- Those queued invoices are then processed (with the normal retry logic).

## Code References (Current Implementation)

- Bank availability and payment logic: `src/main/java/hh/inpt/smet/payment/service/BankService.java`
- Direct payment + fallback to Kafka: `src/main/java/hh/inpt/smet/billing/service/FacturationService.java`
- Kafka consumer + retry logic: `src/main/java/hh/inpt/smet/billing/service/PaymentStreamConsumer.java`
- Kafka producer: `src/main/java/hh/inpt/smet/billing/service/PaymentStreamProducer.java`
- Manual toggle endpoints: `src/main/java/hh/inpt/smet/payment/controller/BankControlController.java`

## Payment Statuses

- `PENDING`: Invoice created, not paid yet
- `QUEUED`: Sent to Kafka because bank was unavailable
- `PAID`: Payment succeeded (directly or via Kafka)
- `FAILED`: Kafka retries exhausted (max 3)

## Detailed Step-by-Step

### 1) Direct Payment Attempt
- `FacturationService.pay()` checks the invoice is owned by the user and is `PENDING`.
- If payment method is a `BankAccount`, it calls `BankService.processBankPayment()`.

### 2) BankService Behavior
- `processBankPayment()` calls `isBankAvailable()`.
- `isBankAvailable()` returns **false** if the flag is false.
- If flag is true, it randomly returns true **70% of the time**.

### 3) When Bank Fails
- A `PaymentEvent` is sent to Kafka using `PaymentStreamProducer`.
- The invoice is updated to `QUEUED`.

### 4) Kafka Consumer
- The consumer receives the event immediately.
- It tries the payment again using the same bank logic.
- If payment fails:
  - It increments `retryCount` and re-sends the event to Kafka.
  - After 3 retries, it marks the invoice `FAILED`.

## Important Consequence

Because the consumer ignores events while the bank is disabled, **Kafka acts as a queue until the bank is re-enabled**. Re-enabling the bank explicitly re-sends queued invoices so they are processed.

## Summary

- Kafka is used to retry failed bank payments asynchronously.
- It retries a maximum of 3 times, immediately.
- After that, the invoice is marked `FAILED`.
- Turning the bank flag back to true later **does not auto-pay** old invoices.
