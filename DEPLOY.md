# Deploy

This file is intentionally ignored by git.

## Prerequisites

- Use JDK 21 for publishing tasks.
- `build-export.gradle` is the publishing build file.
- Signing can come from `gradle.properties` or environment variables.
- Central credentials can come from either:
  - `centralUsername` and `centralPassword`
  - `ossrhToken` and `ossrhTokenPassword`
  - `CENTRAL_PORTAL_USERNAME` and `CENTRAL_PORTAL_PASSWORD`

## Local Env Scripts

Local helper scripts live under `scripts/`.
Credentials are read from `~/.m2/maven-central.properties` using `ossrhUsername` and `ossrhPassword`.

| Script | Purpose |
|---|---|
| `load-maven-env.sh` | Reads `~/.m2/maven-central.properties`, exports `CENTRAL_PORTAL_USERNAME`, `CENTRAL_PORTAL_PASSWORD`, `SIGNING_PASSWORD`, sets `JAVA_HOME` |
| `release-preflight.sh` | Checks env vars, Maven settings, and verifies the Central Portal token |
| `central-auth-check.py` | Verifies credentials against the Central Portal API |

Load the environment before any publish step:

```bash
source scripts/load-maven-env.sh
```

Run preflight checks before a real release:

```bash
source scripts/load-maven-env.sh
scripts/release-preflight.sh
```

## Dry Runs

Run dry runs first to validate task wiring and credentials without uploading artifacts:

```bash
source scripts/load-maven-env.sh
gradle -b build-export.gradle --no-daemon -PcentralUsername="$CENTRAL_PORTAL_USERNAME" -PcentralPassword="$CENTRAL_PORTAL_PASSWORD" publishAggregationToCentralPortal --dry-run --console=plain
gradle -b build-export.gradle --no-daemon -PcentralUsername="$CENTRAL_PORTAL_USERNAME" -PcentralPassword="$CENTRAL_PORTAL_PASSWORD" publishAggregationToCentralSnapshots --dry-run --console=plain
```

## Test Matrix

### JDK 11 compile and test

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 11)
export PATH="$JAVA_HOME/bin:$PATH"
gradle clean test --console=plain
```

### JDK 21 test

```bash
source scripts/load-maven-env.sh
./scripts/deploy_local_tests.sh
gradle cleanTest test --console=plain
```

## Full Local Validation

```bash
source scripts/load-maven-env.sh
./scripts/deploy_local_tests.sh
gradle cleanTest test
```

## Local Maven

```bash
source scripts/load-maven-env.sh
gradle -b build-export.gradle --no-daemon -PcentralUsername="$CENTRAL_PORTAL_USERNAME" -PcentralPassword="$CENTRAL_PORTAL_PASSWORD" publishToMavenLocal --console=plain
```

Artifacts are published under:

```bash
~/.m2/repository/org/ic4j/ic4j-java11transport/0.8.0
```

## Central Portal Release

```bash
source scripts/load-maven-env.sh
scripts/release-preflight.sh
gradle -b build-export.gradle --no-daemon -PcentralUsername="$CENTRAL_PORTAL_USERNAME" -PcentralPassword="$CENTRAL_PORTAL_PASSWORD" publishAggregationToCentralPortal --console=plain
```

## Central Snapshots

```bash
source scripts/load-maven-env.sh
gradle -b build-export.gradle --no-daemon -PcentralUsername="$CENTRAL_PORTAL_USERNAME" -PcentralPassword="$CENTRAL_PORTAL_PASSWORD" publishAggregationToCentralSnapshots --console=plain
```

## Useful Checks

```bash
gradle -b build-export.gradle tasks --console=plain
gradle -b build-export.gradle publishToMavenLocal --info
```

Release checklist for 0.8.0 and later:

1. Run `gradle clean test` on JDK 11.
2. Run `source scripts/load-maven-env.sh && ./scripts/deploy_local_tests.sh && gradle cleanTest test` on JDK 21.
3. Run `source scripts/load-maven-env.sh && gradle -b build-export.gradle -PcentralUsername="$CENTRAL_PORTAL_USERNAME" -PcentralPassword="$CENTRAL_PORTAL_PASSWORD" publishToMavenLocal` on JDK 21.
4. Run `source scripts/load-maven-env.sh && scripts/release-preflight.sh`.
5. Publish only after the JDK 11 and 21 validation steps all pass.
