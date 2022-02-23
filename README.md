## Implicit Java HTTP Client ReplicaTransport implementation

You can use this implementation for Java applications running on JDK 11 or higher.

```
ReplicaTransport transport = ReplicaJavaHttpTransport.create(icLocation);
Agent agent = new AgentBuilder().transport(transport).identity(identity).build();
```


# Downloads / Accessing Binaries

To add IC4J Java 11 Transport library to your Java project use Maven or Gradle import from Maven Central.

<a href="https://search.maven.org/artifact/ic4j/ic4j-java11transport/0.6.5/jar">
https://search.maven.org/artifact/ic4j/ic4j-java11transport/0.6.5/jar
</a>

```
<dependency>
  <groupId>org.ic4j</groupId>
  <artifactId>ic4j-java11transport</artifactId>
  <version>0.6.5</version>
</dependency>
```

```
implementation 'org.ic4j:ic4j-java11transport:0.6.5'
```


# Build

You need JDK 11+ to build IC4J Java 11 Transport.