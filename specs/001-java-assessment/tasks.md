# Amway Java Coding Assessment — Tasks

## 1. Purpose

This document breaks down the implementation defined in `spec.md` and `plan.md` into small, executable development tasks.

Each task should be:

* Independently understandable
* Small enough to implement and review
* Verifiable through tests or observable behavior
* Completed before dependent tasks begin

Task status convention:

```text
[ ] Not started
[x] Completed
```

---

# Phase 0 — Project Bootstrap

## [X] T001 — Create Maven Multi-Module Project

**Goal**

Create the root Maven project and two modules:

```text
calculator
lucky-draw
```

**Deliverables**

```text
pom.xml
calculator/pom.xml
lucky-draw/pom.xml
```

**Acceptance Criteria**

* `mvn clean verify` can run from project root
* Both modules are discovered
* Java 21 is configured
* No implementation is required yet

---

## [X] T002 — Add Root Documentation Files

Create:

```text
README.md
spec.md
plan.md
tasks.md
```

**Acceptance Criteria**

* All documents are committed at repository root
* README links to `spec.md`, `plan.md`, and `tasks.md`

---

## [X] T003 — Configure Common Build Settings

Configure common Maven properties:

```text
Java 21
UTF-8
JUnit 5
compiler configuration
Surefire
```

**Acceptance Criteria**

* Compilation uses Java 21
* Unit tests execute through Maven

---

# Phase 1 — Calculator Domain

## [X] T010 — Create Calculator Module Structure

Create package structure:

```text
com.amway.calculator
├── command
├── domain
└── exception
```

**Acceptance Criteria**

* Calculator module compiles

---

## [X] T011 — Define Calculator Interface

Create:

```java
Calculator
```

Operations:

```text
add
subtract
multiply
divide
clear
undo
redo
getResult
```

Use:

```java
BigDecimal
```

**Acceptance Criteria**

* Interface matches calculator functional requirements
* No floating-point primitive is used for calculation APIs

---

## [X] T012 — Define CalculatorCommand Interface

Create:

```java
CalculatorCommand
```

Suggested contract:

```java
BigDecimal execute(BigDecimal currentValue);
```

**Acceptance Criteria**

* Commands can transform the current calculator state
* Interface does not depend on concrete calculator implementation

---

## [X] T013 — Implement AddCommand

Implement:

```text
AddCommand
```

**Acceptance Criteria**

```text
10 + 5 = 15
```

Unit test included.

---

## [X] T014 — Implement SubtractCommand

Implement:

```text
SubtractCommand
```

**Acceptance Criteria**

```text
10 - 5 = 5
```

Unit test included.

---

## [X] T015 — Implement MultiplyCommand

Implement:

```text
MultiplyCommand
```

**Acceptance Criteria**

```text
10 × 5 = 50
```

Unit test included.

---

## [X] T016 — Implement DivideCommand

Implement:

```text
DivideCommand
```

Use:

```text
scale >= 6
RoundingMode.HALF_UP
```

**Acceptance Criteria**

```text
1 / 3
```

returns at least:

```text
0.333333
```

Division by zero is rejected.

---

## [X] T017 — Implement ClearCommand

Implement:

```text
ClearCommand
```

**Acceptance Criteria**

After clear:

```text
result = 0
```

---

# Phase 2 — Calculator History

## [X] T020 — Create DefaultCalculator

Implement:

```java
DefaultCalculator
```

Responsibilities:

```text
current result
command execution
undo history
redo history
```

**Acceptance Criteria**

* Arithmetic commands update current result
* Current result is retrievable

---

## [X] T021 — Introduce Undo Stack

Use:

```java
Deque<BigDecimal>
```

or equivalent state object.

Before each successful normal operation:

```text
store previous state
```

**Acceptance Criteria**

```text
0
add 10
multiply 2
undo
```

returns:

```text
10
```

---

## [X] T022 — Implement Multiple Undo

**Acceptance Criteria**

Given:

```text
0
add 10
multiply 2
subtract 5
```

Then:

```text
undo → 20
undo → 10
undo → 0
```

---

## [X] T023 — Introduce Redo Stack

Maintain a second history stack.

**Acceptance Criteria**

```text
add 10
multiply 2
undo
redo
```

returns:

```text
20
```

---

## [X] T024 — Implement Multiple Redo

**Acceptance Criteria**

Multiple undo operations can be reapplied in the correct LIFO order.

---

## [X] T025 — Clear Redo History After New Operation

When a new normal command executes after `undo()`:

```text
redo history must be cleared
```

**Acceptance Criteria**

```text
add 10
multiply 2
undo
add 5
redo
```

must not restore `20`.

---

## [X] T026 — Handle Empty Undo History

Create:

```text
NoUndoHistoryException
```

or equivalent behavior.

**Acceptance Criteria**

Calling undo on an empty history does not corrupt current state.

---

## [X] T027 — Handle Empty Redo History

Create:

```text
NoRedoHistoryException
```

or equivalent behavior.

---

## [X] T028 — Add DivisionByZeroException

Create:

```text
DivisionByZeroException
```

**Acceptance Criteria**

* Exception is raised before calculator state changes
* Previous result remains intact

---

# Phase 3 — Calculator Testing

## [X] T030 — Add Basic Arithmetic Tests

Test:

```text
addition
subtraction
multiplication
division
clear
```

---

## [X] T031 — Add Negative Number Tests

Test:

```text
-5 + 3
5 + -3
-5 × -3
3 - 5
```

---

## [X] T032 — Add Precision Tests

Verify precision of at least six decimal places.

Examples:

```text
1 / 3
2 / 7
10 / 6
```

---

## [X] T033 — Add Undo Tests

Test:

```text
single undo
multiple undo
```

---

## [X] T034 — Add Redo Tests

Test:

```text
single redo
multiple redo
```

---

## [X] T035 — Add Redo Invalidation Test

Test:

```text
operation
operation
undo
new operation
redo
```

Redo must be unavailable.

---

## [X] T036 — Add Error Tests

Test:

```text
division by zero
empty undo
empty redo
```

---

# Phase 4 — Lucky Draw Project Bootstrap

## [X] T100 — Add Spring Boot Dependencies

Add:

```text
spring-boot-starter-web
spring-boot-starter-data-jpa
spring-boot-starter-validation
spring-boot-starter-security
mysql-connector-j
springdoc-openapi
```

Testing:

```text
spring-boot-starter-test
testcontainers
```

**Acceptance Criteria**

Spring Boot application starts.

---

## [X] T101 — Create Lucky Draw Application

Create:

```java
LuckyDrawApplication
```

**Acceptance Criteria**

Application context loads successfully.

---

## [X] T102 — Create Package Structure

Create:

```text
com.amway.luckydraw
├── campaign
│   ├── api
│   ├── application
│   ├── domain
│   └── infrastructure
├── draw
│   ├── api
│   ├── application
│   ├── domain
│   └── infrastructure
├── prize
├── security
└── common
```

---

# Phase 5 — Database and Domain Model

## [X] T110 — Create Campaign Entity

Fields:

```text
id
name
maxDrawPerUser
status
startTime
endTime
createdAt
updatedAt
```

**Acceptance Criteria**

Entity can be persisted through JPA.

---

## [X] T111 — Create Prize Entity

Fields:

```text
id
campaignId
name
totalQuantity
remainingQuantity
probability
version
createdAt
updatedAt
```

Use:

```text
BigDecimal probability
```

**Acceptance Criteria**

No `double` or `float` is used for configured probabilities.

---

## [X] T112 — Add Optimistic Version Field to Prize

Use:

```java
@Version
```

or maintain version if required by implementation.

**Acceptance Criteria**

Prize entity supports optimistic concurrency metadata.

---

## [X] T113 — Create UserCampaignDraw Entity

Fields:

```text
id
userId
campaignId
drawCount
version
```

Add unique constraint:

```text
(userId, campaignId)
```

---

## [X] T114 — Create DrawRecord Entity

Fields:

```text
id
userId
campaignId
prizeId
result
createdAt
```

Results:

```text
WIN
NO_PRIZE
```

---

## [X] T115 — Add Database Indexes

Add indexes for:

```text
prize.campaign_id
user_campaign_draw(user_id, campaign_id)
draw_record(user_id, campaign_id)
```

---

# Phase 6 — Repository Layer

## [X] T120 — Create CampaignRepository

Support:

```text
findById
save
```

---

## [X] T121 — Create PrizeRepository

Support:

```text
findByCampaignId
save
```

---

## [X] T122 — Add Atomic Inventory Update

Create repository operation equivalent to:

```sql
UPDATE prize
SET remaining_quantity = remaining_quantity - 1
WHERE id = :id
AND remaining_quantity > 0;
```

**Acceptance Criteria**

Return affected row count.

```text
1 = success
0 = unavailable
```

---

## [X] T123 — Create UserCampaignDrawRepository

Support lookup by:

```text
userId
campaignId
```

---

## [X] T124 — Add Pessimistic Draw Counter Lock

Create:

```text
findForUpdate(userId, campaignId)
```

with:

```java
PESSIMISTIC_WRITE
```

**Acceptance Criteria**

Concurrent requests for the same user/campaign serialize draw-count modification.

---

## [X] T125 — Create DrawRecordRepository

Support creation and lookup needed by tests.

---

# Phase 7 — Campaign Validation

## [X] T130 — Implement Campaign Availability Validation

Validate:

```text
campaign exists
campaign is active
current time is inside valid period
```

where applicable.

---

## [X] T131 — Implement Prize Configuration Validation

Validate:

```text
quantity >= 0
probability >= 0
```

---

## [X] T132 — Implement Probability Sum Validation

Validate:

```text
sum(prize probabilities)
+
no-prize probability
=
100%
```

Use:

```java
BigDecimal.compareTo()
```

**Acceptance Criteria**

These are equivalent:

```text
100
100.0
100.00
```

---

## [X] T133 — Reject Invalid Probability Configuration

Test:

```text
99%
101%
negative probability
```

---

# Phase 8 — Probability Engine

## [X] T140 — Define RandomNumberGenerator

Create:

```java
RandomNumberGenerator
```

Contract:

```java
int nextInt(int bound);
```

---

## [X] T141 — Implement Production Random Generator

Create:

```text
ThreadLocalRandomNumberGenerator
```

---

## [X] T142 — Implement Fixed Random Generator for Tests

Create:

```text
FixedRandomNumberGenerator
```

or use a mock.

**Acceptance Criteria**

Probability tests can deterministically select specific ranges.

---

## [X] T143 — Define PrizeSelector

Create:

```java
PrizeSelector
```

Input:

```text
campaign
prizes
```

Output:

```text
selected prize
or NO_PRIZE
```

---

## [X] T144 — Implement Weighted Prize Selection

Use integer probability buckets.

Recommended scale:

```text
0..9999
```

**Acceptance Criteria**

Supports at least:

```text
0.01% probability resolution
```

---

## [X] T145 — Implement No-Prize Selection

Any probability range not allocated to a physical prize maps to:

```text
NO_PRIZE
```

---

## [X] T146 — Test Probability Boundaries

Test exact boundary values.

Example:

```text
Prize A: 0-999
Prize B: 1000-2999
```

Verify:

```text
999 → Prize A
1000 → Prize B
```

---

# Phase 9 — Draw Limit

## [X] T150 — Implement User Draw Counter Initialization

If no record exists for:

```text
userId + campaignId
```

create one with:

```text
drawCount = 0
```

---

## [X] T151 — Implement Draw Limit Validation

Validate:

```text
drawCount < campaign.maxDrawPerUser
```

---

## [X] T152 — Increment Draw Count Safely

Increment within transaction while row is protected by database locking.

---

## [X] T153 — Create DrawLimitExceededException

Map to business error:

```text
DRAW_LIMIT_EXCEEDED
```

---

## [X] T154 — Add Draw Limit Tests

Test:

```text
below limit
exactly reaches limit
exceeds limit
```

---

# Phase 10 — Single Draw Application Service

## [X] T160 — Define DrawService

Create:

```java
DrawService
```

Primary method:

```text
draw(userId, campaignId)
```

---

## [X] T161 — Implement Single Draw Transaction

Add:

```java
@Transactional
```

Flow:

```text
load campaign
validate campaign
lock user draw counter
validate draw limit
increment draw count
load prizes
select outcome
allocate prize if applicable
persist draw record
return result
```

---

## [X] T162 — Define DrawResult

Represent:

```text
WIN
NO_PRIZE
```

For win include:

```text
prizeId
prizeName
```

---

## [X] T163 — Persist No-Prize Result

When no prize is selected:

```text
create draw_record
result = NO_PRIZE
```

---

## [X] T164 — Allocate Prize Inventory

If a prize is selected:

```text
atomic inventory decrement
```

If decrement succeeds:

```text
WIN
```

---

## [X] T165 — Handle Exhausted Prize

If atomic decrement returns:

```text
0
```

then selected prize became unavailable.

Implement bounded retry.

---

## [X] T166 — Add Draw Retry Limit

Define:

```text
MAX_DRAW_RETRY
```

Suggested:

```text
3
```

After exhausting retries:

```text
NO_PRIZE
```

---

## [X] T167 — Persist Winning Result

When allocation succeeds:

```text
create draw_record
result = WIN
prizeId = selected prize
```

---

# Phase 11 — Multi-Draw

## [X] T170 — Define Multi-Draw Request

Request:

```json
{
  "count": 10
}
```

Validation:

```text
1 <= count <= 100
```

---

## [X] T171 — Implement MultiDrawService

Execute multiple calls to single-draw logic.

---

## [X] T172 — Ensure Independent Transaction per Draw

Avoid wrapping all N draws in one large transaction.

Ensure:

```text
drawOnce()
```

has independent transaction semantics.

---

## [X] T173 — Return Multiple Results

Response includes ordered results.

Example:

```json
{
  "results": [
    {
      "result": "WIN"
    },
    {
      "result": "NO_PRIZE"
    }
  ]
}
```

---

## [X] T174 — Stop When Draw Limit Is Reached

Define consistent behavior.

Recommended:

```text
stop processing remaining draws
return successful results plus business error
```

or reject request before execution if remaining quota is insufficient.

Document chosen behavior in README and API documentation.

---

# Phase 12 — REST API

## [X] T180 — Create CampaignController

Implement:

```http
GET /api/v1/campaigns/{campaignId}
```

---

## [X] T181 — Create DrawController

Implement:

```http
POST /api/v1/campaigns/{campaignId}/draws
```

---

## [X] T182 — Use Authenticated User Identity

Do not accept:

```text
userId
```

from query/body for draw ownership.

Use authenticated principal.

---

## [X] T183 — Add Request Validation

Validate:

```text
count
campaignId
request body
```

using Bean Validation.

---

## [X] T184 — Define DrawResponse DTO

Do not expose JPA entities directly.

---

# Phase 13 — Error Handling

## [X] T190 — Define Business Error Codes

Create constants or enum:

```text
CAMPAIGN_NOT_FOUND
CAMPAIGN_NOT_AVAILABLE
DRAW_LIMIT_EXCEEDED
INVALID_DRAW_COUNT
INVALID_PROBABILITY_CONFIGURATION
UNAUTHORIZED
FORBIDDEN
```

---

## [X] T191 — Create GlobalExceptionHandler

Use:

```java
@RestControllerAdvice
```

---

## [X] T192 — Create Standard Error Response

Example:

```json
{
  "code": "DRAW_LIMIT_EXCEEDED",
  "message": "Maximum draw limit has been reached",
  "timestamp": "..."
}
```

---

## [X] T193 — Map Business Errors to HTTP Statuses

Recommended:

```text
CAMPAIGN_NOT_FOUND → 404
CAMPAIGN_NOT_AVAILABLE → 409
DRAW_LIMIT_EXCEEDED → 409
INVALID_DRAW_COUNT → 400
INVALID_PROBABILITY_CONFIGURATION → 400
```

---

# Phase 14 — Security

## [X] T200 — Configure Spring Security

Protected API requests require authentication.

---

## [X] T201 — Implement JWT Authentication Filter

Read:

```http
Authorization: Bearer <token>
```

---

## [X] T202 — Create Authenticated Principal

Principal includes:

```text
userId
roles
```

---

## [X] T203 — Protect User Draw API

Require:

```text
ROLE_USER
```

or equivalent authenticated role.

---

## [X] T204 — Add Admin Role

Support:

```text
ROLE_ADMIN
```

for administrative APIs.

---

## [X] T205 — Add Authentication Failure Test

Verify unauthenticated draw request returns:

```text
401
```

---

## [X] T206 — Add Authorization Failure Test

Verify user without permission receives:

```text
403
```

---

# Phase 15 — Administrative Configuration

## [X] T210 — Create Campaign Creation API

Suggested:

```http
POST /api/v1/admin/campaigns
```

---

## [X] T211 — Create Prize Configuration API

Suggested:

```http
POST /api/v1/admin/campaigns/{campaignId}/prizes
```

---

## [X] T212 — Create Prize Update API

Suggested:

```http
PUT /api/v1/admin/campaigns/{campaignId}/prizes/{prizeId}
```

---

## [X] T213 — Validate Probability After Configuration Change

Configuration update must not leave an invalid final probability distribution.

---

# Phase 16 — Environment Configuration

## [X] T220 — Externalize DataSource Settings

Use:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
```

---

## [X] T221 — Externalize HikariCP Settings

Use environment variables for:

```text
minimumIdle
maximumPoolSize
connectionTimeout
```

---

## [X] T222 — Add Local Development Configuration

Provide reasonable development defaults.

Do not commit production secrets.

---

# Phase 17 — OpenAPI

## [X] T230 — Add Springdoc OpenAPI

Configure Swagger/OpenAPI documentation.

---

## [X] T231 — Document Draw Endpoint

Document:

```text
path
request
response
authentication
business errors
```

---

## [X] T232 — Document Admin APIs

Document admin authorization requirements.

---

# Phase 18 — Unit Tests

## [X] T240 — Add PrizeSelector Tests

Test deterministic selection for all prize ranges.

---

## [X] T241 — Add No-Prize Tests

Force random values into no-prize range.

---

## [X] T242 — Add Probability Validation Tests

Test:

```text
100%
< 100%
> 100%
negative value
```

---

## [X] T243 — Add LuckyDrawService Win Test

Mock selected prize and successful inventory update.

Assert:

```text
WIN
draw record created
draw count incremented
```

---

## [X] T244 — Add LuckyDrawService No-Prize Test

Assert:

```text
NO_PRIZE
draw record created
draw count incremented
```

---

## [X] T245 — Add Out-of-Stock Retry Test

First selected prize fails inventory decrement.

Verify retry behavior.

---

## [X] T246 — Add Draw Limit Service Test

Verify exceeding limit is rejected.

---

# Phase 19 — Integration Tests

## [X] T250 — Configure Testcontainers MySQL

Use real MySQL-compatible behavior during integration tests.

---

## [X] T251 — Add Repository Integration Tests

Test:

```text
campaign repository
prize repository
draw repository
user draw repository
```

---

## [X] T252 — Test Atomic Inventory Update

Initial:

```text
remainingQuantity = 1
```

First update:

```text
1 row
```

Second update:

```text
0 rows
```

Final quantity:

```text
0
```

---

## [X] T253 — Test Transaction Rollback

Force exception during draw after counter change.

Verify transaction rolls back relevant state.

---

## [X] T254 — Add Draw API Integration Test

Test valid authenticated request from:

```text
HTTP
↓
Controller
↓
Service
↓
Database
```

---

# Phase 20 — Concurrency Tests

## [X] T260 — Create Inventory Concurrency Test

Setup:

```text
Prize inventory = 1
```

Run many concurrent allocation attempts.

**Acceptance Criteria**

```text
successful allocation <= 1
remaining quantity >= 0
```

---

## [X] T261 — Create Draw Limit Concurrency Test

Setup:

```text
maxDrawPerUser = 3
```

Run many concurrent requests for the same user.

**Acceptance Criteria**

```text
successful draws <= 3
```

---

## [X] T262 — Verify Multiple Application Threads

Tests must not depend on:

```java
synchronized
```

for correctness.

Database concurrency control must enforce invariants.

---

# Phase 21 — Logging and Observability

## [X] T270 — Add Business Event Logging

Log:

```text
drawId
campaignId
userId
result
prizeId
```

when available.

---

## [X] T271 — Avoid Sensitive Logging

Ensure logs do not contain:

```text
JWT
password
authorization header
```

---

# Phase 22 — README and Submission

## [X] T280 — Write Project Overview

README explains:

```text
Calculator
Lucky Draw
```

and project goals.

---

## [X] T281 — Document Calculator Design Pattern

Explain why Command Pattern was selected.

---

## [X] T282 — Document Lucky Draw Architecture

Explain:

```text
Spring Boot
JPA
MySQL
stateless app instances
database consistency
```

---

## [X] T283 — Document Concurrency Strategy

Explain:

```text
PESSIMISTIC_WRITE
atomic conditional update
database transaction
```

Also explain why:

```text
synchronized
```

is insufficient for multiple application instances.

---

## [X] T284 — Document Why Redis Lock Is Not Required

Explain that DB-level concurrency control satisfies current requirements and avoids unnecessary infrastructure.

---

## [X] T285 — Add Build Instructions

Example:

```bash
mvn clean verify
```

---

## [X] T286 — Add Run Instructions

Document required environment variables.

---

## [X] T287 — Add API Examples

Include example requests for:

```text
draw once
draw multiple times
campaign lookup
```

---

## [X] T288 — Add Swagger URL

Document API documentation location.

---

# Phase 23 — Final Verification

## [X] T290 — Run Full Build

Run:

```bash
mvn clean verify
```

**Acceptance Criteria**

Build succeeds.

---

## [X] T291 — Verify Calculator Acceptance Criteria

Check:

```text
add
subtract
multiply
divide
clear
negative numbers
six-decimal precision
undo
redo
redo invalidation
errors
```

---

## [X] T292 — Verify Lucky Draw Functional Criteria

Check:

```text
multiple prizes
configurable quantity
configurable probability
No Prize
single draw
multiple draw
draw limits
```

---

## [X] T293 — Verify Concurrency Invariants

Must always hold:

```text
remaining_quantity >= 0
```

and:

```text
draw_count <= campaign.max_draw_per_user
```

---

## [X] T294 — Verify API Requirements

Check:

```text
REST API
authentication
authorization
input validation
error handling
OpenAPI
```

---

## [X] T295 — Verify Environment Configuration

Confirm application can change database and connection pool settings without recompilation.

---

## [X] T296 — Review Repository Cleanliness

Remove:

```text
unused classes
dead code
debug logs
temporary files
hardcoded credentials
IDE-generated unnecessary files
```

---

## [X] T297 — Final Code Review

Review specifically for:

```text
race conditions
transaction boundaries
BigDecimal correctness
JPA N+1 issues
exception consistency
test determinism
naming clarity
over-engineering
```

---

# Phase 24 — Submission Checklist

Before submitting, confirm:

```text
[ ] spec.md complete
[ ] plan.md complete
[x] tasks.md complete

[x] calculator builds
[x] calculator tests pass

[x] lucky-draw builds
[x] unit tests pass
[x] integration tests pass
[x] concurrency tests pass

[x] Swagger works
[x] JWT works

[x] README complete

[x] no secrets committed
[x] mvn clean verify passes
```

---

# Recommended Agent Execution Order

When using a coding agent, execute tasks in this sequence:

```text
T001-T003

T010-T036

T100-T125

T130-T146

T150-T174

T180-T193

T200-T213

T220-T232

T240-T262

T270-T288

T290-T297
```

Do not ask the agent to implement the entire project in one prompt.

Prefer:

```text
Implement T010-T017 according to spec.md and plan.md.
Do not work on later tasks.
Run relevant tests before finishing.
```

Then:

```text
Review T010-T017 against acceptance criteria.
Fix any violations before moving to T020.
```

This keeps implementation context small and makes deviations from the specification easier to detect.
