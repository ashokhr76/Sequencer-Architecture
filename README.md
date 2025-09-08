# Aeron RSM - Java Cluster Example

This project demonstrates a single-node Aeron cluster setup using Java.  
It includes a simple clustered service (`TradeClusterService`) and a launcher (`TradeSingleNodeLauncher`) for local development and testing.

## Features

- Aeron MediaDriver, Archive, ConsensusModule, and ClusteredServiceContainer setup
- Example clustered service for trade state management
- Maven build and dependency management
- Ready for extension to multi-node clusters

## Prerequisites

- Java 17 or newer (Temurin recommended)
- Maven 3.6+
- Mac, Linux, or Windows

## Build Instructions

1. **Clone the repository:**
   ```sh
   git clone https://github.com/ashokhr76/Sequencer-Architecture.git
   cd aeron-rsm/aeron
   ```

2. **Check and update dependencies:**
   - Ensure your pom.xml uses compatible Aeron and Agrona versions:
     ```xml
     <dependency>
       <groupId>io.aeron</groupId>
       <artifactId>aeron-client</artifactId>
       <version>1.48.6</version>
     </dependency>
     <dependency>
       <groupId>io.aeron</groupId>
       <artifactId>aeron-driver</artifactId>
       <version>1.48.6</version>
     </dependency>
     <dependency>
       <groupId>io.aeron</groupId>
       <artifactId>aeron-archive</artifactId>
       <version>1.48.6</version>
     </dependency>
     <dependency>
       <groupId>org.agrona</groupId>
       <artifactId>agrona</artifactId>
       <version>1.21.1</version>
     </dependency>
     ```

3. **Build the project:**
   ```sh
   mvn clean install
   mvn clean package
   mvn dependency:copy-dependencies
   ```

## How to Run

1. **Open a terminal in the project directory.**

2. **Run the single-node launcher:**
   ```sh
   java --add-opens java.base/sun.nio.ch=ALL-UNNAMED \
     -cp "target/aeron-1.0-SNAPSHOT.jar:target/dependency/*" \
     dev.poc.aeron_cluster.TradeSingleNodeLauncher
   ```

   - If you see errors about missing classes, check your classpath and Maven build.
   - If you see errors about Agrona or Aeron versions, ensure you are using the recommended versions above.

3. **Stop the cluster:**
   - Press `Ctrl+C` in the terminal.


## Troubleshooting

- **Version errors:**  
  Ensure Aeron and Agrona versions are compatible (see above).
- **Class not found:**  
  Run `mvn clean install` and check your classpath.
- **Port conflicts:**  
  Each run uses a unique directory and ports for isolation.

