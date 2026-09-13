# Amway Java Coding Assessment Specification

## 1. Overview

This project contains two independent Java programming assignments:

1. **Calculator with Undo/Redo**
2. **E-Commerce Lucky Draw System**

The implementation should demonstrate:

* Clean object-oriented design
* Appropriate use of design patterns
* Maintainable and extensible code
* Correct handling of edge cases
* Comprehensive automated testing

For the Lucky Draw System, the solution must additionally demonstrate:

* RESTful API design
* Authentication and authorization
* Concurrency safety
* Transaction consistency
* Horizontal scalability
* Environment-based configuration

---

# Part I — Calculator

## 2. Goal

Design and implement a calculator capable of performing basic arithmetic operations while supporting undo and redo functionality.

The implementation should use appropriate design patterns and should be designed for maintainability and future extensibility.

---

## 3. Functional Requirements

### FR-CALC-001 — Addition

The calculator shall support addition of two numbers.

Example:

```text
2 + 3 = 5
```

### FR-CALC-002 — Subtraction

The calculator shall support subtraction of two numbers.

Example:

```text
5 - 3 = 2
```

### FR-CALC-003 — Multiplication

The calculator shall support multiplication of two numbers.

Example:

```text
3 × 4 = 12
```

### FR-CALC-004 — Division

The calculator shall support division of two numbers.

Example:

```text
10 ÷ 4 = 2.5
```

Division by zero must not produce an invalid calculator state.

The implementation shall return or raise an appropriate error.

### FR-CALC-005 — Clear

The calculator shall provide a clear operation.

After clearing, the calculator shall return to its initial state.

### FR-CALC-006 — Display Result

The calculator shall allow the current result to be retrieved and displayed.

---

## 4. Precision Requirements

### FR-CALC-007 — Decimal Precision

Calculation results shall maintain precision to at least six decimal places.

The implementation must avoid unnecessary floating-point precision errors where practical.

Example:

```text
1 / 3

Result:
0.333333...
```

The result must be accurate to at least:

```text
0.333333
```

---

## 5. Negative Number Support

### FR-CALC-008 — Negative Operands

The calculator shall correctly process negative operands.

Examples:

```text
-5 + 3 = -2
5 + -3 = 2
-5 × -3 = 15
```

### FR-CALC-009 — Negative Results

The calculator shall correctly represent negative results.

Example:

```text
3 - 5 = -2
```

---

# 6. Undo / Redo

The calculator shall maintain operation history.

Undo and redo operations shall follow Last-In-First-Out (LIFO) semantics.

## FR-CALC-010 — Undo

`undo()` shall revert the most recently executed operation.

Example:

```text
Initial value: 0

add(10)
→ 10

multiply(2)
→ 20

undo()
→ 10
```

## FR-CALC-011 — Multiple Undo

Multiple undo operations shall be supported while operation history exists.

Example:

```text
add(10)
multiply(2)
subtract(5)

Current:
15

undo()
→ 20

undo()
→ 10

undo()
→ 0
```

## FR-CALC-012 — Redo

`redo()` shall reapply the most recently undone operation.

Example:

```text
add(10)
multiply(2)

undo()
→ 10

redo()
→ 20
```

## FR-CALC-013 — Redo Invalidation

When a new calculation is performed after an undo, previously redoable operations shall no longer be redoable.

Example:

```text
add(10)
multiply(2)

undo()
→ 10

add(5)
→ 15

redo()
→ no operation available
```

---

# 7. Calculator Validation and Error Handling

The calculator shall gracefully handle invalid operations.

At minimum, the following cases must be considered:

* Division by zero
* Undo when no history exists
* Redo when no redo history exists
* Invalid or unsupported operation
* Invalid numeric input where applicable

Errors must not corrupt the calculator state.

---

# 8. Calculator Design Requirements

The solution shall use an appropriate design pattern for representing calculator operations and/or operation history.

The implementation should make it possible to introduce additional operations in the future without requiring significant changes to existing calculator logic.

Examples of possible future operations include:

```text
percentage
power
square root
```

The specific design pattern is intentionally not prescribed by this specification.

The implementation should justify the selected pattern through its structure and code clarity.

---

# 9. Calculator Testing Requirements

Comprehensive unit tests shall be provided.

Tests shall cover at least:

### Arithmetic

* Addition
* Subtraction
* Multiplication
* Division

### Precision

* Decimal calculations
* Results requiring at least six decimal places

### Negative Numbers

* Negative operands
* Negative results
* Operations involving two negative numbers

### History

* Single undo
* Multiple undo
* Single redo
* Multiple redo
* Undo followed by new operation
* Empty undo history
* Empty redo history

### Errors

* Division by zero
* Invalid operation/input where applicable

---

# Part II — E-Commerce Lucky Draw System

## 10. Goal

Design and implement an e-commerce lucky draw system.

The system shall support multiple prizes with configurable inventory and winning probabilities.

A "No Prize" result shall also participate in the probability distribution.

The combined probability of all prizes and the "No Prize" result must equal 100%.

The system must support both single and multiple draws while preventing:

* Duplicate or excessive draw attempts
* Prize inventory overselling

---

# 11. Core Domain Concepts

The system shall represent at least the following concepts.

## Campaign

A lucky draw campaign.

A campaign defines:

* Available prizes
* Probability configuration
* Maximum number of allowed draws
* Other configuration required by the implementation

## Prize

A prize available within a campaign.

A prize contains at least:

* Name
* Available quantity
* Winning probability

## No Prize

A valid draw outcome representing:

```text
No Prize / Better Luck Next Time
```

It has no physical inventory but participates in the probability distribution.