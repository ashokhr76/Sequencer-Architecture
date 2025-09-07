# Sequencer Architecture – Aeron Cluster POC (Single Node)
This project is a **Java-based proof-of-concept** for building a **replicated state machine** using [Aeron Cluster](https://github.com/aeron-io/aeron). 
It models a simplified post-trade processing pipeline with deterministic state transitions across multiple nodes.

## What It Demonstrates

- Aeron Cluster setup with a single-node configuration (can be extended to multi-node)
- Trade lifecycle: `Capture → Enrich → Validate → CalculateCharges → Distribute`
- Deterministic state machine per trade
- Leader-only external API simulation (commission calculation)
- Foundation for offset generation and cancel/correct workflows


## How to Run Locally

### 1. Clone the Repository

```bash
git clone https://github.com/ashokhr76/Sequencer-Architecture.git
cd Sequencer-Architecture
mvn clean install
mvn clean package
mvn dependency:copy-dependencies

Opwn two terminals & run below java command
java --add-opens java.base/sun.nio.ch=ALL-UNNAMED -cp "target/aeron-1.0-SNAPSHOT.jar:target/dependency/*" dev.poc.cluster.SingleNodeLauncher
java --add-opens java.base/sun.nio.ch=ALL-UNNAMED -cp "target/aeron-1.0-SNAPSHOT.jar:target/dependency/*" dev.poc.client.TradeClient


