# JVM Doctor

A developer-facing CLI tool that attaches to a running JVM process (by PID) and produces a doctor-style diagnosis. It creates a "health report" by analyzing threads, memory, garbage collection, and VM flags.

## Purpose

When a Java application is misbehaving (slowness, high memory, unresponsiveness), **JVM Doctor** provides a quick, automated "Level 1" diagnosis without requiring complex profilers or flight recordings.

It answers the question: **"Is there something obviously wrong with this JVM right now?"**

## Features

### Diagnostic Rules
The tool checks for common instability patterns:
- **Deadlock Detection**: Identifies Java-level thread deadlocks.
- **Thread Explosion**: Warns if thread count exceeds safe limits (generic or Spring profiles).
- **Blocked Thread Ratio**: flags high contention where threads are waiting on locks.
- **Full GC Storms**: Detects increasing Full GC events during sampling.
- **Old Gen Saturation**: Alerts if Old Generation memory usage > 85%.
- **Metaspace Constraints**: Checks if `MaxMetaspaceSize` is dangerously low.
- **Misconfiguration**: Detects conflicting heap flags (e.g., `Xmx` + `MaxRAMPercentage`).
- **Observability Gaps**: Warns if GC logging is disabled in production.

### Data Collection
- Uses standard JDK tools: `jcmd` (required) and `jstat` (optional).
- No agent installation required.
- Attaches to local processes using the Attach API machinery via `jcmd`.

### Reporting
- **Console**: Human-readable summary with color-coded severity.
- **JSON**: Machine-parsable format for automation.
- **Markdown**: Shareable reports for issue tracking.

## Requirements
- **Host (Tool)**: JDK 11+ installed.
- **Target (App)**: JDK 8+ running process.
- **OS**: Linux or macOS (Windows not officially active).

## Build
Build the project using Maven:

```bash
mvn package -DskipTests
```

The executable JAR will be created at:
`target/jvm-doctor-1.0-SNAPSHOT-jar-with-dependencies.jar`

## Usage

### 1. List Java Processes
Find the PID of your target application:
```bash
java -jar target/jvm-doctor-1.0-SNAPSHOT-jar-with-dependencies.jar list
```

### 2. Live Diagnosis
Attach to a process by PID. By default, it runs for 10 seconds:

```bash
java -jar target/jvm-doctor-1.0-SNAPSHOT-jar-with-dependencies.jar live --pid <PID>
```

**Options:**
- `--duration <time>`: Sampling window (e.g., `30s`, `1m`). Default: `10s`.
- `--interval <time>`: Sampling frequency (e.g., `500ms`, `2s`). Default: `1s`.
- `--format <list>`: Output formats (`console`, `json`, `md`). Default: `console`.
- `--out-dir <path>`: Directory to save report files.
- `--profile <name>`: Tuning profile (`generic` or `spring`). Default: `generic`.
- `--no-jstat`: Disable `jstat` sampling if the tool is missing or failing.

**Examples:**

*Generate a Markdown report for a Spring Boot app:*
```bash
jvm-doctor live --pid 12345 --profile spring --format md --out report.md
```

*Quick interactive selection:*
```bash
jvm-doctor live --pick
```

## Interpreting Reports

Findings are ranked by **Severity** and **Score**:
- **[ERROR]**: Critical stability risks (Deadlocks, Full GC loops). Immediate action required.
- **[WARN]**: High resource pressure (High thread count, Old Gen filling up).
- **[INFO]**: Configuration advice or minor observations.

Each finding includes **Evidence** (why it triggered) and **Recommendations** (how to fix it).
