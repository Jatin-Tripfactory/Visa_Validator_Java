# Config-Driven Visa Rule Evaluation System (Core Java)

## Overview

This project is a **config-driven rule evaluation system** built using **core Java only**.
It determines whether a traveler requires a visa based on:

- Destination country
- Passport country
- Travel purpose
- Intended stay duration

All rules are defined externally using **YAML configuration**, with no hardcoded country logic in Java.

---

## Objectives

This project was built as part of a **Core Java brush-up assignment**, focusing on:

- Java fundamentals
- Clean OOP design
- Enums and collections
- Defensive coding
- Config-driven behavior
- Testability without frameworks

---

## Inputs

The evaluator takes the following inputs:

- `DestinationCountry` (enum)
- `PassportCountry` (enum)
- `TravelPurpose` (enum)
- `stayDuration` (int, days)

---

## Output

The system always returns a `VisaDecision` object containing:

- `boolean visaRequired`
- `VisaType visaType`
- `List<DocumentType> documents`
- `int estimatedProcessingDays`
- `List<String> warnings`

The output object is **immutable**.

---

## Design Overview

### Components

- **RuleLoader**  
  Loads visa rules from a YAML configuration file.

- **RuleRepository**  
  Stores parsed rules and provides lookup functionality.

- **VisaRuleEvaluator**  
  Core logic that evaluates rules and produces a `VisaDecision`.

- **VisaDecision**  
  Immutable DTO representing the evaluation result.

- **ConfigValidator**  
  Performs fail-fast validation on configuration at startup.

---

## Configuration

Rules are defined in:

```
src/visa-rules.yaml
```

Example structure:

```yaml
countries:
  JP:
    passportRules:
      IN:
        TOURISM:
          required: true
          visaType: TOURIST
          documents:
            - PASSPORT
            - PHOTO
          processingDays: 7
          maxStayDays: 90
          warnings:
            - "Apply at least one week in advance"
```

---

## Defensive Behavior

The system is designed defensively:

- **No rule found** → returns a safe decision with warnings
- **Conflicting rules** → returns a safe decision with warnings
- **Invalid config** → fails fast during startup
- **No guessing** → evaluator never assumes correctness

Exceptions are used only for **invalid system states**, not for rule ambiguity.

---

## Testing

Testing is done using **core Java (no JUnit)** via:

```
src/VisaRuleEvaluatorTest.java
```

### Covered Scenarios

- Valid rule match
- No rule found
- Multiple rule conflict
- Missing config fields

Tests are executed via a simple `main()` method with assertions enabled.

---

## How to Compile and Run

From the project root:

### Compile
```bash
javac -cp "lib/*" -d . src/*.java
```

### Run Tests
```bash
java -ea -cp "lib/*;." Visa_Validator_Java.Assignment.src.VisaRuleEvaluatorTest
```

Expected output:
```
ALL TESTS PASSED
```
### Note
Ensure that the top level directory's name is "Visa_Validator_Java" after downloading
