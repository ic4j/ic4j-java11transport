# IC4J Java 11 Transport

Read complete documentation here <a href="https://docs.ic4j.com">https://docs.ic4j.com</a>.

IC4J Java 11 Transport is a `ReplicaTransport` implementation built on the Java 11 built-in HTTP client (`java.net.http.HttpClient`). It is part of the IC4J open source library suite for interacting with the <a href="https://dfinity.org/">Internet Computer</a> network from Java applications.

# License

IC4J Java 11 Transport is available under Apache License 2.0.

# Documentation

## Usage

Use this transport for Java applications running on JDK 11 or higher. Full documentation <a href="https://docs.ic4j.com/reference/api-reference/replicatransport#java-11-http-client-transport-implementation">here</a>.

```java
ReplicaTransport transport = ReplicaJavaHttpTransport.create(icLocation);
Agent agent = new AgentBuilder().transport(transport).identity(identity).build();
```

# Downloads / Accessing Binaries

To add IC4J Java 11 Transport library to your Java project use Maven or Gradle import from Maven Central.

<a href="https://search.maven.org/artifact/org.ic4j/ic4j-java11transport/0.8.0/jar">
https://search.maven.org/artifact/org.ic4j/ic4j-java11transport/0.8.0/jar
</a>

**Maven:**
```xml
<dependency>
  <groupId>org.ic4j</groupId>
  <artifactId>ic4j-java11transport</artifactId>
  <version>0.8.0</version>
</dependency>
```

**Gradle:**
```groovy
implementation 'org.ic4j:ic4j-java11transport:0.8.0'
```

# Build

You need JDK 11 or JDK 21 to build IC4J Java 11 Transport.

```
gradle build
```

## Running Tests

Tests run against a local Internet Computer replica (dfx). Deploy the test canister first:

```
./scripts/deploy_local_tests.sh
```

Then run the tests:

```
gradle test
```

BLS certificate verification is disabled for local replica testing (configured automatically via `jvmArgs` in `build.gradle`).
