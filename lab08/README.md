# Lab 08 - Observer Pattern

Requires JDK 23 and Maven 3.9.x. Run from this folder:

```cmd
mvn clean package exec:java
```

The program runs ten simulated temperature/humidity updates, one second apart.
Each sensor notifies both the dashboard and logger through `update(Subject)`.
Both observers print updates using printf; the logger also writes to the log file.
A fixed random seed makes demonstrations reproducible.
For the same demonstration without delays:
`mvn package exec:java -Dexec.args=--fast`.

## Design and reuse
The handout's Observer and Subject interface signatures are preserved.
Abstract `Sensor` implements Subject and Cloneable and centralizes readings,
registration, removal, change detection, notification, and clone behavior.
TemperatureSensor and HumiditySensor provide the units and typed clone methods.
SensorObserver shares naming and subject validation; concrete observers supply
their own update behavior. This demonstrates inheritance and polymorphism.

Registrations are unique and notified in registration order. Unregistering an
absent observer is harmless. Unchanged readings do not automatically notify.
Explicit notifyObservers still sends the current state.
Callbacks operate on a snapshot; registration changes apply to the next cycle.
This is a single-threaded simulation, not a concurrent sensor framework.

## Cloning and removal
A clone preserves the concrete sensor type, name, unit, and reading but receives
a new, empty observer collection. Neither readings nor registrations are shared.
The demo updates an unobserved clone, attaches its own dashboard and logger,
then verifies that the original reading stayed unchanged.
It also unregisters the original dashboard from humidity and shows only the logger
receiving the next humidity update.

## Logging and tests
Start/stop and logger-observer readings go to `logs/App/log4j/log.out`.
Each run replaces the previous file; generated logs and target output are ignored.
The annex's main-class path is corrected to `edu.psu.se411.lab08.App`.
The required slf4j-log4j12 dependency redirects to Reload4j, as in prior labs.
Run `mvn test` for ten tests covering notifications, removal, duplicate registration,
unchanged readings, explicit notification, cloning isolation, callback changes,
invalid readings, observer output, and required inputs.
