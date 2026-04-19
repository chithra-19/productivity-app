# 🚀 ClimbUp – Gamified Productivity & Goal Tracking System

ClimbUp is a full-stack productivity platform designed to simulate real-world backend systems with **gamification, state management, and modular service architecture**.

Instead of a simple CRUD app, it implements a **business-logic-driven backend** with XP, streak tracking, and an achievement engine.

---

## ⚙️ Tech Stack
- Java 17
- Spring Boot
- Spring Data JPA
- MySQL
- Thymeleaf
- REST APIs
- Axios
- Bootstrap

---

## 🧠 System Highlights

### 🎯 Goal Management System
- Create, update, delete, and filter goals
- Priority-based classification (LOW / MEDIUM / HIGH)
- Status tracking (ACTIVE / COMPLETED)

---

### ⚡ Gamification Engine
- XP system rewarding user actions
- Daily streak tracking for consistency
- Achievement unlocking based on user milestones

---

### 🏆 Achievement Evaluation System
- Centralized evaluation service
- Computes user progress using:
  - Completed goals
  - Completed tasks
  - XP earned
  - Streak count
- Ensures consistency after every user action

---

### 🔄 Real-Time Backend Sync
- Axios-based REST communication
- Backend is the single source of truth
- UI refreshes after every mutation (create/update/delete)

---

## 🏗️ Architecture Design

- `GoalService` → goal lifecycle management  
- `XPService` → reward system  
- `StreakTrackerService` → consistency tracking  
- `AchievementEvaluationService` → gamification logic engine  

👉 Clear separation of concerns for scalability and maintainability.

---

## ⚡ Key Engineering Decisions

- Centralized achievement evaluation system instead of scattered logic
- Backend-driven state management (frontend is stateless)
- Modular service architecture for extensibility

---

## 🚨 Scalability Considerations

Identified performance bottlenecks:
- Repeated database aggregation queries (COUNT operations)
- Synchronous achievement recalculation

Future improvements:
- Introduce caching (Redis / in-memory stats)
- Event-driven architecture for async processing
- Precomputed user statistics instead of runtime aggregation

---

## 🧪 Engineering Insight

This project was designed to simulate real backend complexity:
- Multiple services interacting
- Business logic layering
- State consistency challenges
- Performance tradeoffs

---

## 🚀 Outcome

ClimbUp helped strengthen my understanding of:
- Backend system design
- Service-based architecture
- Tradeoffs between simplicity vs scalability
- Real-world application structuring beyond CRUD

---
