JVM Doctor is a CLI tool for diagnosing JVM health. This guide covers how to build, run, and verify the tool.

Build
mvn package -DskipTests
The executable jar is located at 
target/jvm-doctor-1.0-SNAPSHOT-jar-with-dependencies.jar
.

Running the Tool
List Processes
java -jar target/jvm-doctor-1.0-SNAPSHOT-jar-with-dependencies.jar list
Live Diagnosis
java -jar target/jvm-doctor-1.0-SNAPSHOT-jar-with-dependencies.jar live --pid <PID>
Options:

--duration 10s: Sample for 10 seconds.
--interval 1s: Sample every 1 second.
--format console,json: Output both console report and JSON.
--profile spring: Use Spring Boot specific thresholds.
Verification
Automated Tests
Run unit tests:

mvn test
Manual Integration Test
Compile Test Target

javac src/test/java/com/jvm/doctor/integration/TestTarget.java
Run Test Target (Scenario: Deadlock)

java -cp src/test/java com.jvm.doctor.integration.TestTarget deadlock
Note the PID printed.

Run JVM Doctor Open a new terminal:

java -jar target/jvm-doctor-1.0-SNAPSHOT-jar-with-dependencies.jar live --pid <PID> --duration 5s
Verify Output

Check if THREAD_DEADLOCK finding appears in the report with ERROR severity.
Check JSON output for structure.
Thread Explosion Scenario
Run target: java ... TestTarget threads
Run doctor.
Verify THREAD_COUNT_HIGH finding.
