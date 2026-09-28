# FitLog Project Blueprint & Process Guide (`PROCESS.md`)

> **Academic Assessment:** Project Leap — Java & DBMS Examination (Regulation 2023)  
> **Course:** U28CS491 — Java Programming  
> **Institution:** Sri Eshwar College of Engineering, Coimbatore  
> **Student Register Number:** `722825243194`  
> **Repository:** [https://github.com/shamilsj2025aids/fitlog.git](https://github.com/shamilsj2025aids/fitlog.git)  

---

## 1. What is FitLog and What Does It Do?

### The Problem
Fitness beginners often start logging workouts and calorie intakes in notebooks or spreadsheet apps (like Excel or Google Sheets). Within days or weeks, they abandon the habit because manually calculating daily net calories, tracking weekly trends, and preventing errors (like negative numbers or missing data) is cumbersome.

### The Solution: FitLog
**FitLog** is an enterprise-grade backend application built with **Spring Boot** and **MySQL**. It serves as an automated digital fitness diary with five core functions:
1. **Log Workouts:** Record physical exercises with workout type, duration in minutes, and estimated calories burnt.
2. **Log Meals:** Record food consumption with food name, meal category (Breakfast, Lunch, Dinner, Snack), quantity, and calories.
3. **Daily Calorie Balance Summary:** Calculate total calories consumed (Calories In) versus calories burnt (Calories Out), computing the net balance and whether the user is in a `CALORIE DEFICIT`, `CALORIE SURPLUS`, or `BALANCED` state.
4. **Weekly Trend of Workouts Completed:** Analyze completed workouts day-by-day (Monday through Sunday) for any given week.
5. **Set Personal Weekly Workout Goals:** Allow users to set a target number of workouts per week (e.g., 5 workouts/week) and automatically track real-time progress toward achieving that goal.

---

## 2. Technology Stack & Architectural Decisions

Why was each tool chosen? Here is the exact technical rationale:

### A. Java 17 & Java 26
- **What it is:** The programming language used to build the entire backend.
- **Why we use it:** Java provides strict static typing, platform independence ("write once, run anywhere"), and deep ecosystem support for high-throughput enterprise systems. Java 17 is the modern Long-Term Support (LTS) industry standard.

### B. Apache Maven
- **What it is:** A build automation and dependency management tool driven by `pom.xml`.
- **Why we use it:** Instead of manually searching, downloading, and adding 50+ `.jar` files to a classpath, Maven automatically downloads and links libraries (Spring Boot, Hibernate, MySQL Driver, Jackson, JUnit) from the Maven Central repository.

### C. Spring Boot (v3.3.4)
- **What it is:** An opinionated, production-ready framework built on top of the Spring Framework.
- **Why we use it:**
  - **Embedded Web Server:** Includes Tomcat out-of-the-box on port `8080` (no external Tomcat installation required).
  - **Inversion of Control (IoC) & Dependency Injection (DI):** Spring automatically instantiates and wires together services, repositories, and controllers via constructor injection.
  - **Convention over Configuration:** Minimizes boilerplate XML and configuration code.

### D. Spring Data JPA & Hibernate
- **What it is:**
  - **JPA (Jakarta Persistence API):** The standard Java specification for Object-Relational Mapping (ORM).
  - **Hibernate:** The underlying ORM implementation engine.
  - **Spring Data JPA:** An abstraction layer providing pre-built CRUD methods (`findAll()`, `save()`, `findById()`, `delete()`) just by creating an interface extending `JpaRepository`.
- **Why we use it:** Instead of writing raw SQL strings (`"SELECT * FROM users WHERE..."`) and manually mapping `ResultSet` objects, JPA maps Java objects (`User`, `Workout`, `Meal`, `Goal`) directly to relational database tables.

### E. MySQL 8.0 Database (Running on Port 3307)
- **What it is:** A relational database management system (RDBMS).
- **Why we use it:** FitLog requires relational integrity (Foreign Keys linking each workout, meal, and goal to a specific user), ACID transactions (Atomicity, Consistency, Isolation, Durability), and unique indexing (preventing duplicate email accounts).

### F. Jakarta Validation API & Hibernate Validator
- **What it is:** Annotations like `@NotNull`, `@NotBlank`, `@Min(0)`, `@Positive`.
- **Why we use it:** Validates client HTTP requests *before* the request even reaches the service or database layer.

### G. SpringDoc OpenAPI 3 (Swagger UI)
- **What it is:** The vendor-neutral industry standard specification for describing RESTful APIs (formerly known as Swagger). In FitLog, it automatically inspects Java controllers and renders an interactive web UI at `http://localhost:8080/swagger-ui.html`.
- **Why we use it:** Allows examiners and developers to test every single REST endpoint (User, Workout, Meal, Goal, Summary, AI Chat) right from a web browser without needing third-party tools like Postman or Curl.
- **Critical Clarification — OpenAPI is NOT OpenAI:**
  - **OpenAPI:** An open API description standard managed by the Linux Foundation. It acts as an architectural "blueprint" or "contract" for REST web services.
  - **OpenAI:** A private artificial intelligence research and deployment company that created ChatGPT and GPT models.
  - *They are two completely different things that sound similar.* OpenAPI is about API documentation, while OpenAI is about generative artificial intelligence.

### H. Groq AI & Google Gemini AI Fitness Coach
- **What it is:** A generative AI integration using Groq's high-speed inference engine (`openai/gpt-oss-120b` and `openai/gpt-oss-20b`) as well as Google Gemini 1.5 Flash via Spring Boot's modern `RestClient`.
- **Why we use it:** To provide real-time, personalized fitness, nutrition, and workout advice directly within FitLog (Tab 7: `🤖 7. AI Coach`). Groq delivers sub-second inference speed for instant response times. It also includes an intelligent built-in fallback rules engine so the chatbot responds even when offline.

---

## 3. Jargon Buster: Master These for Your Viva / Q&A

| Jargon Term | Simple Beginner Explanation | Why It Matters in FitLog |
| :--- | :--- | :--- |
| **Layered Architecture** | Dividing code into distinct responsibilities: Controller $\to$ Service $\to$ Repository $\to$ Entity. | Keeps code neat, maintainable, and prevents UI code from directly touching database tables. |
| **Entity** | A Java class mapped directly to a database table via `@Entity`. | `User.java`, `Workout.java`, `Meal.java`, and `Goal.java`. |
| **DTO (Data Transfer Object)** | A plain Java object used only to transfer data over HTTP. | Protects database entities from exposure, prevents infinite circular loops when serializing JSON, and validates inputs. |
| **Repository** | A data access layer interface extending `JpaRepository`. | Executes SQL queries and saves records without writing boilerplate SQL. |
| **Service Layer** | The "brain" of the application where all business logic and calculations live. | Enforces business rules (like rejecting negative calories) before saving. |
| **Controller** | The gateway that receives HTTP requests (`GET`, `POST`, `PUT`, `DELETE`) and returns HTTP responses. | Exposes the REST API to frontend clients, Postman, or Swagger. |
| **`@RestControllerAdvice`** | A global error interceptor for all controllers. | Converts Java exceptions into neat, clean JSON responses instead of scary 500 stack traces. |
| **Foreign Key (`@ManyToOne`)** | A database column that points to the primary key of another table. | Connects workouts, meals, and goals to a specific `user_id`. |
| **Cascade (`CascadeType.ALL`)** | An ORM instruction: if a User is deleted, automatically delete all their workouts, meals, and goals. | Prevents orphaned data in the database. |
| **OpenAPI vs OpenAI** | OpenAPI is an API specification standard for REST docs; OpenAI is a company making AI models. | Prevents confusion during viva when faculty ask about Swagger/OpenAPI vs chatbots. |
| **RestClient** | Spring Boot 3's modern, synchronous HTTP client for calling external web services. | Used in `GeminiChatServiceImpl` to communicate with Google's Gemini AI endpoint. |

---

## 4. Phase-by-Phase Roadmap

```
+-------------------------------------------------------------------------------+
|  PHASE 1: Project Setup & Environment Configuration              [COMPLETED]  |
|  PHASE 2: Database Design, JPA Entities & MySQL Schema           [COMPLETED]  |
|  PHASE 3: Spring Data Repositories & Custom Queries              [COMPLETED]  |
|  PHASE 4: DTOs, Custom Exceptions & Service Layer Logic          [COMPLETED]  |
|  PHASE 5: REST Controllers & Global Exception Handling           [COMPLETED]  |
|  PHASE 6: Interactive Web UI & OpenAPI Swagger Documentation     [COMPLETED]  |
|  PHASE 7: Automated Unit Testing & Edge Case Verification        [COMPLETED]  |
|  PHASE 8: Git Version Control, IntelliJ Integration & Delivery   [COMPLETED]  |
|  PHASE 9: AI Fitness Coach (Google Gemini & Fallback Engine)     [COMPLETED]  |
+-------------------------------------------------------------------------------+
```

---

## 5. Detailed Breakdown of Completed Phases

### Phase 1: Project Setup & Environment Configuration
- **What was done:**
  - Configured `pom.xml` with Spring Boot 3.3.4, Java 17 release target, Spring Data JPA, Spring Web, Validation, MySQL Connector, and SpringDoc OpenAPI.
  - Configured `application.properties` with database connection string pointing to local MySQL on port `3307`, username `root`, password `75Sh@milSJ`, and schema `fitlog_db`.
  - Configured `.gitignore` to prevent committing build artifacts (`target/`) or IDE cache (`.idea/`).
- **Examiner Question:** *"Why did you set `spring.jpa.hibernate.ddl-auto=update`?"*
  - **Answer:** *"With `update`, Hibernate automatically inspects our Java `@Entity` classes and creates or modifies the MySQL tables, columns, and foreign keys without wiping existing test data."*

---

### Phase 2: Database Design & JPA Entities
- **What was done:**
  - Created 4 JPA Entities:
    1. `User`: `id`, `name`, `email` (unique), `age`, `weightKg`, `heightCm`, `gender`, `createdAt`.
    2. `Workout`: `id`, `user` (`@ManyToOne`), `workoutType`, `durationMinutes`, `caloriesBurnt`, `workoutDate`, `notes`, `loggedAt`.
    3. `Meal`: `id`, `user` (`@ManyToOne`), `foodItem`, `mealType`, `quantity`, `calories`, `mealDate`, `loggedAt`.
    4. `Goal`: `id`, `user` (`@ManyToOne`), `targetWorkoutCount`, `weekStartDate`, `weekEndDate`, `status`, `createdAt`.
  - Enforced bidirectional relationships using `@OneToMany` with `cascade = CascadeType.ALL` and `orphanRemoval = true` in `User.java`.
- **Examiner Question:** *"Why do we use `@JoinColumn(name = "user_id")` on `@ManyToOne`?"*
  - **Answer:** *"It explicitly names the foreign key column in the child table (e.g. `workouts.user_id`) referencing the primary key `users.id`."*

---

### Phase 3: Spring Data JPA Repositories
- **What was done:**
  - Created `UserRepository`, `WorkoutRepository`, `MealRepository`, and `GoalRepository` extending `JpaRepository`.
  - Added custom JPQL aggregation queries to compute total calories burnt and consumed over specific dates or date ranges:
    ```java
    @Query("SELECT COALESCE(SUM(w.caloriesBurnt), 0.0) FROM Workout w WHERE w.user.id = :userId AND w.workoutDate = :date")
    Double sumCaloriesBurntByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);
    ```
- **Examiner Question:** *"What does `COALESCE(...)` do in your JPQL query?"*
  - **Answer:** *"If there are no records for that date, SQL `SUM()` returns `NULL`. `COALESCE` replaces `NULL` with `0.0` so our Java service never gets a `NullPointerException`."*

---

### Phase 4: DTOs & Service Layer with Business Rules
- **What was done:**
  - Created Request and Response DTOs to separate the HTTP payload from database entities.
  - Implemented the Service interfaces and implementation classes: `UserServiceImpl`, `WorkoutServiceImpl`, `MealServiceImpl`, `GoalServiceImpl`, `SummaryServiceImpl`, and `DashboardServiceImpl`.
  - **Strictly enforced Business Rule 1 (Non-negative calories):**
    ```java
    if (request.getCaloriesBurnt() == null || request.getCaloriesBurnt() < 0) {
        throw new BusinessRuleViolationException("Calories burnt must be a non-negative number.");
    }
    ```
  - **Strictly enforced Business Rule 2 (Weekly summary minimum entry requirement):**
    ```java
    long workoutEntriesCount = workoutRepository.countByUserIdAndWorkoutDateBetween(userId, monday, sunday);
    long mealEntriesCount = mealRepository.countByUserIdAndMealDateBetween(userId, monday, sunday);

    if (workoutEntriesCount == 0 && mealEntriesCount == 0) {
        throw new BusinessRuleViolationException(
            "No workout or meal entries exist for the week of " + monday + " to " + sunday +
            ". A weekly summary is only generated once at least one entry exists for that week."
        );
    }
    ```
- **Examiner Question:** *"Why enforce business rules in the service layer if the database has check constraints?"*
  - **Answer:** *"Service-layer validation stops invalid data immediately at the application boundary, returning a friendly, actionable error message to the user rather than bubbling up an ugly database driver exception."*

---

### Phase 5: REST Controllers & Global Exception Handling
- **What was done:**
  - Implemented 6 REST controllers:
    - `UserController`: CRUD operations for user profiles.
    - `WorkoutController`: Core 1 (Log workout) + CRUD + type/date filter.
    - `MealController`: Core 2 (Log meal) + CRUD + type/date filter.
    - `SummaryController`: Core 3 (Daily summary) & Core 4 (Weekly trend).
    - `GoalController`: Core 5 (Weekly goals) & active goal tracking.
    - `DashboardController`: System-wide counts and recent activity metrics.
  - Created `GlobalExceptionHandler.java` annotated with `@RestControllerAdvice`. It catches:
    - `BusinessRuleViolationException` $\to$ Returns HTTP 400 Bad Request.
    - `ResourceNotFoundException` $\to$ Returns HTTP 404 Not Found.
    - `DuplicateResourceException` $\to$ Returns HTTP 409 Conflict.
    - `MethodArgumentNotValidException` $\to$ Returns HTTP 400 with a map of field errors.
- **Examiner Question:** *"What HTTP status code is returned when a user logs a new workout?"*
  - **Answer:** *"HTTP 201 Created, which is the RESTful standard for successful resource creation, accompanied by the created object in the response body."*

---

### Phase 6: Interactive Web UI & Swagger Documentation
- **What was done:**
  - Built a responsive single-page visualizer at `src/main/resources/static/index.html`.
  - Configured SpringDoc OpenAPI 3 for automated interactive Swagger UI at `/swagger-ui.html`.
- **Examiner Question:** *"How does Spring Boot serve the `index.html` file?"*
  - **Answer:** *"Spring Boot automatically maps any static assets located in `src/main/resources/static/` to the root context path (`/`)."*

---

### Phase 7: Automated Unit Testing & Edge Case Verification
- **What was done:**
  - Built unit tests using JUnit 5 and Mockito in `src/test/java/com/fitlog/service/`:
    - `WorkoutServiceTest`: Verifies normal workout logging and asserts that negative calories or zero duration throw `BusinessRuleViolationException`.
    - `MealServiceTest`: Asserts that negative calories consumed are rejected.
    - `SummaryServiceTest`: Asserts that requesting a weekly summary for a week with zero entries throws `BusinessRuleViolationException`, and tests correct daily net calorie calculation.
    - `FitLogApplicationTests`: Verifies full Spring context bootstrapping and MySQL table creation.
  - Result: **9 tests run, 0 failures, 0 errors, 100% success rate.**

---

### Phase 8: Git Version Control & IntelliJ Integration
- **What was done:**
  - Configured git repository and created atomic, descriptive commits following Conventional Commits (`feat: ...`).
  - Integrated full compatibility with IntelliJ IDEA for running, debugging, and git pushes.
  - Exported `fitlog_postman_collection.json` containing ready-to-test requests.

---

### Phase 9: AI Fitness & Nutrition Coach Integration (Groq & Google Gemini)
- **What was done:**
  - Designed `ChatRequest` and `ChatResponse` DTOs.
  - Implemented `ChatService` and `ChatServiceImpl` with support for Groq (`openai/gpt-oss-120b`) and Google Gemini.
  - Built real-time token-by-token streaming endpoint `POST /api/chat/stream` using Spring MVC's `ResponseBodyEmitter` and Java's `java.net.http.HttpClient` SSE consumer.
  - Implemented full Markdown and GitHub Flavored Markdown (GFM) table parser using `marked.js` and an offline regex fallback parser:
    - Tables render inside responsive Bootstrap wrappers with clean alternating row stripes and headers.
    - Bullet points, bold asterisks (`**`), headings (`###`), and line breaks are beautifully styled.
    - Real-time animated typing cursor simulates live generative AI responses.
  - Maintained backward-compatible non-streaming endpoint `POST /api/chat` for Swagger UI / OpenAPI testing.
  - Added browser-stored key configuration in `index.html` allowing instant switching between Groq and Gemini keys.


---

## 6. How to Run & Demonstrate to the Examiner

1. **Start the Application in IntelliJ:**
   - Open `FitLogApplication.java` $\to$ Click **Run**.
2. **Open the Web Dashboard:**
   - In any browser, open: [http://localhost:8080/](http://localhost:8080/)
3. **Open the Swagger UI:**
   - Open: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
4. **Demonstrate Business Rules:**
   - **Show Rule 1 Rejection:** In "Log Workout", enter `-200` in calories. Watch the red alert appear: `"Calories burnt must be a non-negative number."`
   - **Show Rule 2 Rejection:** In "Weekly Trend", select a week in the past with no entries. Watch the alert appear: `"A weekly summary is only generated once at least one entry exists for that week."`
   - **Show Normal Flow:** Log a valid workout and meal. Show the daily summary automatically computing the net calories and balance status (`SURPLUS`/`DEFICIT`).
5. **Demonstrate the AI Fitness Coach:**
   - Click tab **7. AI Coach** in the dashboard.
   - Click one of the quick chips (e.g. *"Give me a high protein vegetarian meal plan"*).
   - Show how the backend interacts with Google Gemini 1.5 Flash (or the offline coach) and streams intelligent, formatted advice back in real-time.
   - Explain the difference between **OpenAPI** (the Swagger documentation contract) and **AI Chat** (Generative AI).

