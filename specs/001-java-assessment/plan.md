# Amway Java Coding Assessment — Implementation Plan

## 1. Technical Overview

This document defines the implementation approach for the requirements described in `spec.md`.

The solution contains two independent modules:

1. Calculator
2. Lucky Draw System

The primary goals are:

* Correctness
* Clear object-oriented design
* Appropriate use of design patterns
* Testability
* Concurrency safety
* Reasonable production-oriented architecture
* Avoiding unnecessary complexity for a coding assessment

---

# 2. Technology Stack

## Core

* Java 21
* Spring Boot 3.x
* Maven
* JUnit 5
* AssertJ
* Mockito

## Lucky Draw Backend

* Spring Web
* Spring Data JPA
* MySQL 8
* HikariCP
* Bean Validation
* Spring Security
* JWT
* Springdoc OpenAPI

## Testing

* JUnit 5
* Mockito
* Spring Boot Test
* MockMvc
* Testcontainers where useful

---

# 3. Repository Structure

```text
amway-java-assessment/
├── README.md
├── pom.xml
├── spec.md
├── plan.md
├── tasks.md
│
├── calculator/
│   ├── pom.xml
│   └── src/
│       ├── main/java/
│       └── test/java/
│
└── lucky-draw/
    ├── pom.xml
    └── src/
        ├── main/java/
        ├── main/resources/
        └── test/java/
```

Use a Maven multi-module project.

Root:

```xml
<modules>
    <module>calculator</module>
    <module>lucky-draw</module>
</modules>
```

---

# Part I — Calculator

# 4. Calculator Architecture

The Calculator will use the **Command Pattern**.

Reason:

* Each mathematical operation becomes an independent command
* Command objects naturally support operation history
* Undo/redo behavior becomes easier to model
* New operations can be introduced without modifying the calculator core

Conceptually:

```text
Calculator
    |
    +-- Comman
```
