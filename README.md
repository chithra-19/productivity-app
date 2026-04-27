<div align="center">

<br/>

# 🧗 ClimbUp

### Gamified Productivity & Goal Tracking System

*A production-grade backend platform built around real-world engineering challenges —*
*not just CRUD.*

<br/>

![Java](https://img.shields.io/badge/Java_17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white)

<br/>

[![Live Demo](https://img.shields.io/badge/🌐_Live_Demo-000000?style=for-the-badge)](https://your-live-link.com)
[![GitHub Repo](https://img.shields.io/badge/📦_Source_Code-181717?style=for-the-badge&logo=github)](https://github.com/your-username/climbup)

<br/>

</div>

---

## What Is ClimbUp?

ClimbUp is a full-stack productivity platform that goes beyond basic CRUD to simulate the **business logic, security, and scalability challenges** found in real production systems.

Users set goals, complete tasks, earn XP, maintain streaks, and unlock achievements — all backed by a modular, service-oriented backend with secure session-based authentication.

> **Core focus:** Backend system design, service interdependency, state consistency, and security — not just feature delivery.

---

## Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Java 17 |
| **Framework** | Spring Boot |
| **Persistence** | Spring Data JPA + MySQL |
| **Security** | Spring Security + BCrypt |
| **Frontend** | Thymeleaf + Bootstrap + Axios |
| **API Style** | REST / MVC Architecture |
| **Auth** | Session-based (JSESSIONID) |

---

## Features

### 🎯 Goal Management
- Create, update, and delete goals
- Priority classification: `LOW` / `MEDIUM` / `HIGH`
- Status tracking: `ACTIVE` / `COMPLETED`
- Filtering and goal lifecycle management

### ⚡ Gamification Engine
- XP system that rewards user actions
- Daily streak tracking to encourage habit consistency

### 🏆 Achievement System
Dynamic achievement unlocking based on:
- Completed goals and tasks
- Total XP earned
- Current streak count

### 🔄 Real-Time Sync
- Axios-powered REST communication
- Backend is the **single source of truth** — no client-side state trust
- UI updates instantly after every operation

---

## Architecture

ClimbUp uses a **service-based modular architecture** where each responsibility is isolated into its own service layer:

```
┌─────────────────────────────────────────────┐
│               User Action                   │
│         (create / complete goal)            │
└───────────────────┬─────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────┐
│              GoalService                    │
│         Goal lifecycle management           │
└───────────────────┬─────────────────────────┘
          ┌─────────┴──────────┐
          ▼                    ▼
┌──────────────────┐  ┌──────────────────────┐
│   XPService      │  │  StreakTrackerService │
│  XP calculation  │  │   Streak computation  │
└────────┬─────────┘  └──────────┬───────────┘
         └────────────┬──────────┘
                      ▼
┌─────────────────────────────────────────────┐
│       AchievementEvaluationService          │
│        Centralized achievement engine       │
└───────────────────┬─────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────┐
│         Updated State → Frontend            │
└─────────────────────────────────────────────┘
```

**Design benefits:**
- Clear separation of concerns
- Each service independently testable and extensible
- Adding a new feature (e.g. leaderboards) doesn't disturb existing services

---

## Security & Authentication

ClimbUp uses **session-based authentication** built on Spring Security.

### Authentication Flow

```
User Login
    │
    ▼
Spring Security authenticates credentials
    │
    ▼
BCrypt verifies hashed password
    │
    ▼
Server creates session → JSESSIONID cookie issued
    │
    ▼
Client sends cookie with every request
    │
    ▼
Server validates session → request processed
```

### Security Implementation

| Concern | Solution |
|---|---|
| Password storage | `BCryptPasswordEncoder` — never plain text |
| Route protection | Spring Security config — protected endpoints |
| Session management | Server-side JSESSIONID |
| CSRF protection | Handled natively by Spring Security |
| Data isolation | Users can only access their own data |

---

## System Flow

```
1. User performs action (create / complete goal)
2. Goal state updated in MySQL via JPA
3. XPService calculates and awards XP
4. StreakTrackerService updates daily streak
5. AchievementEvaluationService evaluates all criteria
6. Updated state returned to frontend via REST response
```

---

## API Reference

### Get a Goal

```http
GET /api/goals/{id}
Cookie: JSESSIONID=<session_id>
```

**Response**

```json
{
  "id": 1,
  "title": "Complete DSA Practice",
  "status": "ACTIVE",
  "priority": "HIGH"
}
```

> All endpoints are session-protected. Unauthenticated requests are rejected before reaching business logic.

---

## Key Engineering Decisions

**1. Centralized Achievement Evaluation**
All achievement logic lives in `AchievementEvaluationService`. This prevents duplicated evaluation logic and ensures consistency regardless of which action triggered the check.

**2. Backend-Driven State (No Frontend Trust)**
The frontend never assumes state. Every operation fetches the updated state from the backend, preventing stale data bugs and maintaining consistency.

**3. Session-Based Auth**
Chosen over JWT for simplicity and reliability in a monolithic MVC setup. Server-managed sessions provide easy invalidation and reduced attack surface.

**4. Modular Service Separation**
Each concern (`XP`, `Streak`, `Achievement`, `Goal`) is owned by a dedicated service. This makes the codebase navigable and each unit independently maintainable.

---

## Scalability Analysis

### Current Bottlenecks

- **Repeated aggregation queries** — `COUNT` operations run on every request
- **Synchronous achievement recalculation** — blocks response time as criteria grow

### Planned Improvements

- **Redis caching** for frequently accessed user stats
- **Event-driven architecture** — async achievement evaluation via message queue
- **Precomputed statistics** — background jobs to maintain running totals
- **Pagination** — for large goal and task datasets

---

## What This Project Demonstrates

| Skill | How It Shows |
|---|---|
| Backend system design | Modular services, flow orchestration |
| Security engineering | Spring Security, BCrypt, session management |
| State consistency | Backend-authoritative design |
| Scalability thinking | Identified bottlenecks with concrete solutions |
| API design | RESTful endpoints, clean response contracts |

---

## Installation & Setup

### Prerequisites
- Java 17+
- Maven
- MySQL

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/climbup.git
cd climbup
```

### 2. Configure Environment Variables

```properties
SPRING_DATASOURCE_URL=your_db_url
SPRING_DATASOURCE_USERNAME=your_db_username
SPRING_DATASOURCE_PASSWORD=your_db_password
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

Visit `http://localhost:8080`

---

## Roadmap

- [ ] JWT-based stateless authentication
- [ ] Role-based access control (`ADMIN` / `USER`)
- [ ] Microservices architecture
- [ ] Advanced analytics dashboard
- [ ] Docker containerization & CI/CD pipeline

---

## Outcome & Learnings

This project represents a shift from **feature implementation → system design thinking**.

Building ClimbUp meant learning to ask different questions: *How do interdependent services stay consistent? Where do bottlenecks emerge at scale? What does "secure" actually mean in implementation?*

These are the questions that matter in production engineering.

---

<div align="center">

**Built with focus on backend engineering, scalability, and real-world system design.**

⭐ Star this repo if you found it useful

</div>
