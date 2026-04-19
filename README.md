# 🚀 ClimbUp – Gamified Productivity Platform

ClimbUp is a full-stack productivity system that goes beyond basic task management by introducing a **gamified backend engine** with XP, streak tracking, and achievements to improve user consistency.

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

## ✨ Features

### 🎯 Goal Management
- Create, update, delete, and filter goals
- Priority-based classification (Low / Medium / High)
- Status tracking (Active / Completed)

### ⚡ Gamification Engine
- XP system rewarding user activity
- Daily streak tracking for consistency
- Achievement unlocking based on user milestones

### 🏆 Achievement System
- Dynamic evaluation of user progress
- Based on:
  - Completed goals
  - Completed tasks
  - XP earned
  - Streak count
- Centralized evaluation service ensures consistency

### 🔄 Real-Time Updates
- Axios-based frontend communication
- Backend always acts as single source of truth
- Automatic UI refresh after operations

---

## 🧠 Architecture

The backend follows a modular service-based architecture:

- `GoalService` → goal lifecycle management  
- `XPService` → XP calculation logic  
- `StreakTrackerService` → streak computation  
- `AchievementEvaluationService` → achievement processing engine  

This separation improves maintainability and scalability.

---

## ⚡ Key Design Decisions

- Centralized achievement evaluation system
- Backend-driven state management (no frontend trust)
- Separation of business logic into services
- Identified future optimization areas:
  - Caching (Redis)
  - Event-driven architecture
  - Reducing repeated DB aggregation queries

---

## 🚀 What I Learned

- Designing real-world backend systems beyond CRUD
- Service-based architecture in Spring Boot
- Tradeoffs between simplicity and scalability
- Handling state consistency in full-stack apps

---

## 📌 Future Improvements

- Introduce Redis caching for user stats
- Move achievement system to async event processing
- Add pagination for scalability
- Improve analytics dashboard for user progress

---

## 👨‍💻 Author

Built by a developer focused on backend systems, scalability, and product thinking.
