# Midas Transaction Processing Backend

A Spring Boot backend completed as part of the J.P. Morgan Advanced Software Engineering Forage program.

The project processes financial transactions through Kafka, validates account balances, persists transaction data, integrates an incentive service, and exposes user balances through a REST API.

## Tech

- Java 17
- Spring Boot
- Apache Kafka
- Spring Data JPA
- H2
- Maven
- REST APIs

## How It Works

1. Transactions are published to Kafka.
2. A Kafka consumer receives each transaction.
3. The backend validates the sender, recipient, and available balance.
4. An incentive service is called when applicable.
5. Account balances are updated.
6. Transaction records are persisted.
7. User balances are exposed through the `/balance` endpoint.

## Testing

The final integration test verifies the complete transaction pipeline.

```bash
./mvnw -Dtest=TaskFiveTests test

Expected result:
Tests run: 1, Failures: 0, Errors: 0
BUILD SUCCESS

The application also packages successfully with:
./mvnw -DskipTests package

Project Origin
This project was completed as part of the J.P. Morgan Advanced Software Engineering virtual experience on Forage.
Starter repository: vagabond-systems/forage-midas
Author
Keith Laurendine Jr.