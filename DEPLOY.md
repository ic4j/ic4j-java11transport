# Deploy

This file is intentionally ignored by git.

## Prerequisites

- Compile and test compatibility must be verified on JDK 11 first, then re-tested on JDK 21.
- Use JDK 21 for publishing tasks.
- `build-export.gradle` is the publishing build.
- Signing can come from `gradle.properties` or environment variables.
- Central credentials can come from either:
  - `centralUsername` and `centralPassword`
  - `ossrhToken` and `ossrhTokenPassword`
  - `CENTRAL_PORTAL_USERNAME` and `CENTRAL_PORTAL_PASSWORD`

## Test Matrix

### JDK 11 compile and test

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 11)
export PATH="$JAVA_HOME/bin:$PATH"
gradle clean test --console=plain
```

### JDK 21 test

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH="$JAVA_HOME/bin:$PATH"
./scripts/deploy_local_tests.sh && gradle cleanTest test --console=plain
```

## Full Local Validation

```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"
./scripts/deploy_local_tests.sh && gradle cleanTest test
```

## Local Maven

```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"
gradle -b build-export.gradle --no-daemon publishToMavenLocal --console=plain
```

Artifacts are published under:

```bash
~/.m2/repository/org/ic4j/ic4j-java11transport/0.8.0
```

## Central Portal Release

```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"
gradle -b build-export.gradle --no-daemon publishAggregationToCentralPortal --console=plain
```

## Central Snapshots

```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"
gradle -b build-export.gradle --no-daemon publishAggregationToCentralSnapshots --console=plain
```

## Useful Checks

```bash
gradle -b build-export.gradle tasks --console=plain
gradle -b build-export.gradle publishToMavenLocal --info
```

Release checklist for 0.8.0 and later:

1. Run `gradle clean test` on JDK 11.
2. Run `./scripts/deploy_local_tests.sh && gradle cleanTest test` on JDK 21.
3. Run `gradle -b build-export.gradle publishToMavenLocal` on JDK 21.
4. Publish only after the JDK 11 and 21 validation steps all pass.
