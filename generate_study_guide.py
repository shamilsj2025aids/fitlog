import os
import docx
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_ALIGN_VERTICAL
from docx.oxml import OxmlElement, parse_xml
from docx.oxml.ns import nsdecls, qn

from reportlab.lib import colors
from reportlab.lib.pagesizes import letter
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, PageBreak, KeepTogether, HRFlowable
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.pdfgen import canvas

# ==============================================================================
# DATA: ALL ANNOTATIONS USED IN FITLOG
# ==============================================================================
ANNOTATIONS_DATA = [
    # 1. CORE SPRING BOOT & CONFIGURATION
    {
        "category": "1. Spring Boot Core & Configuration",
        "name": "@SpringBootApplication",
        "function": "Bootstraps and configures the Spring Boot application context. Combines @Configuration (enables Java-based config), @EnableAutoConfiguration (auto-configures Tomcat, JPA, Jackson), and @ComponentScan (scans com.fitlog package for components).",
        "files": "FitLogApplication.java",
        "viva_tip": "The central launchpad of the application. Tell the examiner it eliminates dozens of lines of XML by automatically finding all @RestController, @Service, and @Repository classes."
    },
    {
        "category": "1. Spring Boot Core & Configuration",
        "name": "@Configuration",
        "function": "Marks a class as a source of bean definitions. Tells Spring IoC container that the class contains methods annotated with @Bean.",
        "files": "OpenApiConfig.java",
        "viva_tip": "Used for programmatic configuration. In FitLog, it defines our custom Swagger / OpenAPI title, version, and student metadata."
    },
    {
        "category": "1. Spring Boot Core & Configuration",
        "name": "@Bean",
        "function": "Indicates that a method instantiates, configures, and initializes a new object managed as a singleton bean by the Spring IoC container.",
        "files": "OpenApiConfig.java (customOpenAPI)",
        "viva_tip": "When you need a third-party class (like OpenAPI) in your Spring container, you write a method annotated with @Bean returning that instance."
    },
    {
        "category": "1. Spring Boot Core & Configuration",
        "name": "@Value",
        "function": "Injects externalized values from application.properties or system environment variables into Java fields at runtime.",
        "files": "ChatServiceImpl.java",
        "viva_tip": "Prevents hardcoding sensitive credentials or endpoints in Java code. For example, @Value(\"${groq.api.key}\") injects the Groq API key."
    },

    # 2. STEREOTYPE & ARCHITECTURAL LAYERING
    {
        "category": "2. Stereotypes & Architecture",
        "name": "@RestController",
        "function": "Specialized version of @Controller that combines @Controller and @ResponseBody. Every handler method returns data directly serialized into JSON rather than rendering an HTML view template.",
        "files": "UserController, WorkoutController, MealController, GoalController, SummaryController, DashboardController, ChatController",
        "viva_tip": "Essential for pure REST APIs. Tell the examiner it tells Spring: 'Do not look for a JSP/Thymeleaf HTML page; write the return object straight into the HTTP response body as JSON.'"
    },
    {
        "category": "2. Stereotypes & Architecture",
        "name": "@Service",
        "function": "Marks a class as a business service component in the business logic layer. Spring automatically discovers it during component scanning and manages it as a singleton bean.",
        "files": "UserServiceImpl, WorkoutServiceImpl, MealServiceImpl, GoalServiceImpl, SummaryServiceImpl, DashboardServiceImpl, ChatServiceImpl",
        "viva_tip": "Separates business logic from HTTP presentation and database access. All business rules (e.g. non-negative calories) are strictly enforced inside @Service classes."
    },
    {
        "category": "2. Stereotypes & Architecture",
        "name": "@Repository",
        "function": "Marks a data access interface/class as a Spring Data repository component. Enables automatic implementation generation and persistence exception translation.",
        "files": "UserRepository, WorkoutRepository, MealRepository, GoalRepository",
        "viva_tip": "Spring Data JPA automatically creates proxy implementations of these interfaces at runtime with complete CRUD methods (save, findById, findAll, delete)."
    },
    {
        "category": "2. Stereotypes & Architecture",
        "name": "@RestControllerAdvice",
        "function": "Defines a global, centralized exception interceptor for all @RestController classes. Intercepts exceptions and transforms them into standardized JSON error responses.",
        "files": "GlobalExceptionHandler.java",
        "viva_tip": "Crucial for clean enterprise architecture. Instead of letting ugly 500 stack traces reach the client, @RestControllerAdvice converts Java exceptions into neat HTTP 400/404 JSON error objects."
    },
    {
        "category": "2. Stereotypes & Architecture",
        "name": "@Transactional",
        "function": "Manages database transaction boundaries automatically. Opens a transaction before method execution, commits on success, and automatically rolls back if any RuntimeException occurs.",
        "files": "UserServiceImpl, WorkoutServiceImpl, MealServiceImpl, GoalServiceImpl",
        "viva_tip": "Guarantees ACID properties. If a database error occurs halfway through saving an entity, the transaction rolls back, preventing corrupt or half-saved data."
    },

    # 3. SPRING MVC REQUEST ROUTING & BINDING
    {
        "category": "3. Spring MVC Request Handling",
        "name": "@RequestMapping",
        "function": "Maps HTTP requests to controller classes or methods. In FitLog, it sets the base URI prefix for entire controller classes (e.g. @RequestMapping(\"/api/workouts\")).",
        "files": "All 7 Controller classes",
        "viva_tip": "Provides clean RESTful path versioning and hierarchy across the application."
    },
    {
        "category": "3. Spring MVC Request Handling",
        "name": "@GetMapping",
        "function": "Shortcut annotation for @RequestMapping(method = RequestMethod.GET). Handles HTTP GET requests to retrieve and read resources.",
        "files": "All 7 Controller classes",
        "viva_tip": "Used for all read-only queries (e.g. GET /api/users, GET /api/summaries/daily). It is idempotent and safe."
    },
    {
        "category": "3. Spring MVC Request Handling",
        "name": "@PostMapping",
        "function": "Shortcut annotation for @RequestMapping(method = RequestMethod.POST). Handles HTTP POST requests to create new resources.",
        "files": "UserController, WorkoutController, MealController, GoalController, ChatController",
        "viva_tip": "Used when submitting new data to the server (e.g. logging a workout, adding a meal, sending an AI chat message)."
    },
    {
        "category": "3. Spring MVC Request Handling",
        "name": "@PutMapping",
        "function": "Shortcut annotation for @RequestMapping(method = RequestMethod.PUT). Handles HTTP PUT requests to update existing resources.",
        "files": "UserController, WorkoutController, MealController, GoalController",
        "viva_tip": "Used for updating records (e.g. PUT /api/users/{id} to update weight/height, or PUT /api/workouts/{id} to edit duration)."
    },
    {
        "category": "3. Spring MVC Request Handling",
        "name": "@DeleteMapping",
        "function": "Shortcut annotation for @RequestMapping(method = RequestMethod.DELETE). Handles HTTP DELETE requests to remove resources.",
        "files": "UserController, WorkoutController, MealController, GoalController",
        "viva_tip": "Permanently deletes a resource by primary key (e.g. DELETE /api/workouts/{id})."
    },
    {
        "category": "3. Spring MVC Request Handling",
        "name": "@PathVariable",
        "function": "Extracts dynamic URI path template variables and binds them to method parameters (e.g. /api/users/{id} -> @PathVariable Long id).",
        "files": "All Controller classes",
        "viva_tip": "Allows dynamic REST URLs. If someone visits /api/users/5, the value 5 is automatically parsed into the Java Long parameter."
    },
    {
        "category": "3. Spring MVC Request Handling",
        "name": "@RequestParam",
        "function": "Extracts query string parameters from the request URL (e.g. /api/summaries/daily?userId=1&date=2026-09-28 -> @RequestParam Long userId).",
        "files": "WorkoutController, MealController, GoalController, SummaryController",
        "viva_tip": "Used for filtering, sorting, or passing auxiliary search criteria that are not primary identity paths."
    },
    {
        "category": "3. Spring MVC Request Handling",
        "name": "@RequestBody",
        "function": "Instructs Spring to read the raw HTTP request body (JSON) and deserialize it into a Java DTO object using Jackson ObjectMapper.",
        "files": "UserController, WorkoutController, MealController, GoalController, ChatController",
        "viva_tip": "Tell the examiner this is how the browser's JSON data is automatically converted into a strongly typed Java object like WorkoutRequest."
    },
    {
        "category": "3. Spring MVC Request Handling",
        "name": "@DateTimeFormat",
        "function": "Declares that a request parameter should be parsed as a date/time using a specific ISO format (e.g. @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)).",
        "files": "WorkoutController, MealController, GoalController, SummaryController",
        "viva_tip": "Prevents String-to-Date parsing errors by automatically parsing YYYY-MM-DD into a Java LocalDate instance."
    },
    {
        "category": "3. Spring MVC Request Handling",
        "name": "@ExceptionHandler",
        "function": "Designates a method in @RestControllerAdvice to handle a specific exception class or hierarchy.",
        "files": "GlobalExceptionHandler.java",
        "viva_tip": "Allows custom handling per exception type (e.g. handling ResourceNotFoundException with HTTP 404 and BusinessRuleViolationException with HTTP 400)."
    },

    # 4. JAKARTA PERSISTENCE API (JPA / HIBERNATE ORM)
    {
        "category": "4. JPA & Hibernate ORM",
        "name": "@Entity",
        "function": "Specifies that the Java class is a persistent entity mapped to a relational database table in MySQL.",
        "files": "User.java, Workout.java, Meal.java, Goal.java",
        "viva_tip": "Every row in the database table corresponds to an instance of an @Entity class. It informs Hibernate to track this class for ORM operations."
    },
    {
        "category": "4. JPA & Hibernate ORM",
        "name": "@Table",
        "function": "Specifies the target table name and database constraints (e.g. @Table(name = \"users\", uniqueConstraints = ...)).",
        "files": "User.java, Workout.java, Meal.java, Goal.java",
        "viva_tip": "Allows the Java class name (User) to map cleanly to a custom database table name (users) with specific database-level indexes."
    },
    {
        "category": "4. JPA & Hibernate ORM",
        "name": "@Id",
        "function": "Designates the primary key column of the persistent entity.",
        "files": "User.java, Workout.java, Meal.java, Goal.java",
        "viva_tip": "Every entity in relational database theory MUST have a unique primary key. @Id specifies which field serves this purpose."
    },
    {
        "category": "4. JPA & Hibernate ORM",
        "name": "@GeneratedValue",
        "function": "Configures the generation strategy for primary keys. In FitLog, GenerationType.IDENTITY delegates primary key generation to MySQL AUTO_INCREMENT.",
        "files": "User.java, Workout.java, Meal.java, Goal.java",
        "viva_tip": "Ensures that when a new record is saved, MySQL assigns the next available unique integer ID (1, 2, 3...) automatically."
    },
    {
        "category": "4. JPA & Hibernate ORM",
        "name": "@Column",
        "function": "Customizes database column mapping, including column name, nullable constraints, length limits, and precision.",
        "files": "User.java, Workout.java, Meal.java, Goal.java",
        "viva_tip": "Enforces database schema constraints at DDL generation time (e.g. nullable = false, length = 100)."
    },
    {
        "category": "4. JPA & Hibernate ORM",
        "name": "@ManyToOne",
        "function": "Defines a single-valued association to another entity class that has many-to-one multiplicity (e.g. Many workouts belong to One user).",
        "files": "Workout.java, Meal.java, Goal.java",
        "viva_tip": "Creates the relational link between child and parent tables. Configured with FetchType.LAZY for optimal database query performance."
    },
    {
        "category": "4. JPA & Hibernate ORM",
        "name": "@OneToMany",
        "function": "Defines a multi-valued association to child entities that have one-to-many multiplicity (e.g. One user has Many workouts).",
        "files": "User.java",
        "viva_tip": "Configured with cascade = CascadeType.ALL and orphanRemoval = true. If a User is deleted, all their workouts, meals, and goals are automatically deleted."
    },
    {
        "category": "4. JPA & Hibernate ORM",
        "name": "@JoinColumn",
        "function": "Specifies the physical foreign key column used for joining relational entity tables (e.g. @JoinColumn(name = \"user_id\")).",
        "files": "Workout.java, Meal.java, Goal.java",
        "viva_tip": "Creates the user_id foreign key column in the workouts/meals/goals MySQL tables referencing the id column of the users table."
    },
    {
        "category": "4. JPA & Hibernate ORM",
        "name": "@PrePersist",
        "function": "Specifies a callback method that is automatically triggered immediately before the entity is saved/inserted into the database.",
        "files": "User.java, Workout.java, Meal.java, Goal.java",
        "viva_tip": "Used for automatic timestamp auditing. Automatically sets createdAt or loggedAt to LocalDateTime.now() without requiring manual coding."
    },

    # 5. JAKARTA BEAN VALIDATION
    {
        "category": "5. Jakarta Bean Validation",
        "name": "@Valid",
        "function": "Triggers automatic validation on the annotated object argument before controller method execution starts.",
        "files": "All Controller classes",
        "viva_tip": "If any validation rule fails (e.g. negative calories), Spring immediately aborts the request and throws MethodArgumentNotValidException with HTTP 400."
    },
    {
        "category": "5. Jakarta Bean Validation",
        "name": "@NotNull",
        "function": "Validates that the annotated field is not null.",
        "files": "GoalRequest, WorkoutRequest, MealRequest, and Entity classes",
        "viva_tip": "Prevents NullPointerException by ensuring required fields (like userId, targetWorkoutCount, durationMinutes) must be provided."
    },
    {
        "category": "5. Jakarta Bean Validation",
        "name": "@NotBlank",
        "function": "Validates that the annotated character sequence is not null and contains at least one non-whitespace character.",
        "files": "UserRequest, WorkoutRequest, MealRequest, ChatRequest",
        "viva_tip": "Stricter than @NotNull. A string with just empty spaces (\"   \") is rejected. Used for names, food items, and workout types."
    },
    {
        "category": "5. Jakarta Bean Validation",
        "name": "@Size",
        "function": "Enforces minimum and maximum boundaries on string length or collection size.",
        "files": "UserRequest.java",
        "viva_tip": "Prevents buffer overflow and ensures data sanity (e.g. user name must be between 2 and 100 characters)."
    },
    {
        "category": "5. Jakarta Bean Validation",
        "name": "@Min",
        "function": "Enforces that the annotated numeric value is greater than or equal to a specified minimum value.",
        "files": "MealRequest.java, Workout.java, Meal.java",
        "viva_tip": "Directly implements FitLog's Business Rule 1: @Min(value = 0, message = \"Calories must be non-negative\")."
    },
    {
        "category": "5. Jakarta Bean Validation",
        "name": "@Positive",
        "function": "Enforces that the annotated numeric value is strictly positive (greater than 0).",
        "files": "GoalRequest.java, WorkoutRequest.java",
        "viva_tip": "Ensures that workout duration and target weekly workout counts cannot be 0 or negative numbers."
    },
    {
        "category": "5. Jakarta Bean Validation",
        "name": "@Email",
        "function": "Validates that the string matches the standard RFC compliant email format.",
        "files": "UserRequest.java, User.java",
        "viva_tip": "Guarantees that user email inputs contain valid domain structures (e.g. user@domain.com)."
    },

    # 6. JACKSON JSON SERIALIZATION
    {
        "category": "6. Jackson JSON Serialization",
        "name": "@JsonIgnore",
        "function": "Instructs Jackson JSON serializer to completely ignore and omit the annotated property from JSON output.",
        "files": "User.java",
        "viva_tip": "Crucial for preventing infinite circular loops when serializing bidirectional JPA entities (User -> Workout -> User -> Workout...)."
    },
    {
        "category": "6. Jackson JSON Serialization",
        "name": "@JsonIgnoreProperties",
        "function": "Specifies a list of properties to ignore during JSON serialization or deserialization (e.g. @JsonIgnoreProperties({\"hibernateLazyInitializer\", \"handler\"})).",
        "files": "Workout.java, Meal.java, Goal.java",
        "viva_tip": "Prevents Hibernate lazy-loading proxy serialization exceptions when Jackson serializes entities fetched from MySQL."
    },

    # 7. SPRING DATA JPA QUERIES
    {
        "category": "7. Spring Data Queries",
        "name": "@Query",
        "function": "Defines custom JPQL (Java Persistence Query Language) or native SQL queries directly on repository methods.",
        "files": "WorkoutRepository.java, MealRepository.java",
        "viva_tip": "Used in FitLog for complex date-range calorie aggregations (SUM(caloriesBurnt) between startDate and endDate)."
    },
    {
        "category": "7. Spring Data Queries",
        "name": "@Param",
        "function": "Binds a repository method parameter to a named parameter placeholder in a @Query JPQL string (e.g. :userId, :startDate).",
        "files": "WorkoutRepository.java, MealRepository.java",
        "viva_tip": "Prevents SQL injection vulnerabilities by using parameterized query substitution."
    },

    # 8. SPRINGDOC OPENAPI 3 (SWAGGER)
    {
        "category": "8. SpringDoc OpenAPI 3",
        "name": "@Tag",
        "function": "Categorizes and groups related REST endpoints into logical visual modules on the Swagger UI documentation dashboard.",
        "files": "All 7 Controller classes",
        "viva_tip": "Organizes the Swagger page into clean modules: '1. User Management', '2. Workout Logging', 'AI Chatbot', etc."
    },
    {
        "category": "8. SpringDoc OpenAPI 3",
        "name": "@Operation",
        "function": "Provides a concise summary and detailed description for a specific REST API endpoint in the OpenAPI documentation.",
        "files": "All 7 Controller classes",
        "viva_tip": "Provides human-readable endpoint documentation that examiners see when expanding endpoints on /swagger-ui.html."
    },

    # 9. JUNIT 5 & MOCKITO TESTING
    {
        "category": "9. JUnit 5 & Mockito Testing",
        "name": "@SpringBootTest",
        "function": "Boots the complete Spring application context in a test environment, loading all beans, repositories, and configurations.",
        "files": "FitLogApplicationTests.java",
        "viva_tip": "Used for end-to-end integration testing to verify that the entire Spring context and database connection initialize without errors."
    },
    {
        "category": "9. JUnit 5 & Mockito Testing",
        "name": "@ExtendWith",
        "function": "Registers third-party test extensions with JUnit 5. In FitLog, @ExtendWith(MockitoExtension.class) initializes Mockito mocks.",
        "files": "WorkoutServiceTest, MealServiceTest, SummaryServiceTest",
        "viva_tip": "Enables fast, isolated unit testing without loading the full Spring Boot context or starting MySQL."
    },
    {
        "category": "9. JUnit 5 & Mockito Testing",
        "name": "@Test",
        "function": "Marks a method as an executable test case that JUnit 5 executes.",
        "files": "All test classes",
        "viva_tip": "Identifies test methods. If any assertion fails or an unexpected exception occurs, JUnit reports the test as failed."
    },
    {
        "category": "9. JUnit 5 & Mockito Testing",
        "name": "@BeforeEach",
        "function": "Specifies that the annotated method must run before each individual @Test method in the test class.",
        "files": "WorkoutServiceTest, MealServiceTest, SummaryServiceTest",
        "viva_tip": "Resets mock objects and initializes fresh test data fixtures before each test method runs, ensuring test isolation."
    },
    {
        "category": "9. JUnit 5 & Mockito Testing",
        "name": "@DisplayName",
        "function": "Provides an expressive, readable name for a test case displayed in IDE and Maven test execution logs.",
        "files": "WorkoutServiceTest, MealServiceTest, SummaryServiceTest",
        "viva_tip": "Makes test reports readable for evaluators (e.g. 'Rule 1: Should reject workout when calories burnt is negative')."
    },
    {
        "category": "9. JUnit 5 & Mockito Testing",
        "name": "@Mock",
        "function": "Creates a simulated mock instance of a dependency (e.g. WorkoutRepository) without connecting to the database.",
        "files": "WorkoutServiceTest, MealServiceTest, SummaryServiceTest",
        "viva_tip": "Allows testing service logic in pure isolation. We configure mock behavior using Mockito.when(...)."
    },
    {
        "category": "9. JUnit 5 & Mockito Testing",
        "name": "@InjectMocks",
        "function": "Instantiates the tested class and automatically injects all mock dependencies annotated with @Mock into its constructor/fields.",
        "files": "WorkoutServiceTest, MealServiceTest, SummaryServiceTest",
        "viva_tip": "Automatically wires mock repositories into WorkoutServiceImpl so the service can be tested directly."
    },

    # 10. JAVA LANGUAGE STANDARD
    {
        "category": "10. Java Language Standard",
        "name": "@Override",
        "function": "Compiler directive indicating that a method overrides a declaration in a superclass or implements an interface method.",
        "files": "All Service Implementations",
        "viva_tip": "Provides compile-time safety. If the interface method signature changes, the Java compiler will immediately catch the mismatch."
    }
]

# ==============================================================================
# GENERATOR 1: MICROSOFT WORD (.DOCX)
# ==============================================================================
def generate_docx(filename="FitLog_Annotations_Study_Guide.docx"):
    doc = docx.Document()

    # Page Margins
    for section in doc.sections:
        section.top_margin = Inches(0.75)
        section.bottom_margin = Inches(0.75)
        section.left_margin = Inches(0.75)
        section.right_margin = Inches(0.75)

    # Document Title
    p_title = doc.add_paragraph()
    p_title.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r_title = p_title.add_run("FitLog — Annotations Reference & Viva Study Guide")
    r_title.bold = True
    r_title.font.size = Pt(22)
    r_title.font.color.rgb = RGBColor(30, 58, 138) # Deep Navy

    p_meta = doc.add_paragraph()
    p_meta.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r_sub = p_meta.add_run("Project Leap — Java & DBMS Examination (Regulation 2023) | Course: U28CS491\n")
    r_sub.font.size = Pt(10.5)
    r_sub.font.color.rgb = RGBColor(100, 116, 139)
    r_student = p_meta.add_run("Student Register No: 722825243194 | Sri Eshwar College of Engineering, Coimbatore")
    r_student.bold = True
    r_student.font.size = Pt(11)
    r_student.font.color.rgb = RGBColor(30, 58, 138)

    doc.add_paragraph() # Spacer

    # Introduction Box
    p_intro = doc.add_paragraph()
    p_intro.paragraph_format.left_indent = Inches(0.2)
    p_intro.paragraph_format.right_indent = Inches(0.2)
    r_intro_title = p_intro.add_run("Mastering Annotations for Viva & Code Walkthroughs\n")
    r_intro_title.bold = True
    r_intro_title.font.size = Pt(12)
    r_intro_title.font.color.rgb = RGBColor(15, 23, 42)
    r_intro_body = p_intro.add_run(
        "In modern enterprise Java with Spring Boot, Annotations are metadata tags that replace hundreds of lines "
        "of boilerplate code. Instead of writing XML configuration files or complex reflection code, annotations "
        "instruct Spring, Hibernate, Jackson, and JUnit on how to manage dependencies, map database tables, "
        "validate inputs, route HTTP requests, and format errors. Below is the exhaustive reference of all 42 annotations "
        "powering the FitLog system."
    )
    r_intro_body.font.size = Pt(10)
    r_intro_body.font.color.rgb = RGBColor(71, 85, 105)

    doc.add_paragraph() # Spacer

    # Group by Category
    categories = []
    for item in ANNOTATIONS_DATA:
        if item["category"] not in categories:
            categories.append(item["category"])

    for cat in categories:
        items = [i for i in ANNOTATIONS_DATA if i["category"] == cat]

        # Category Heading
        h = doc.add_paragraph()
        r_h = h.add_run(cat)
        r_h.bold = True
        r_h.font.size = Pt(14)
        r_h.font.color.rgb = RGBColor(30, 58, 138)
        h.paragraph_format.space_before = Pt(12)
        h.paragraph_format.space_after = Pt(4)

        # Table
        table = doc.add_table(rows=1, cols=4)
        table.alignment = WD_TABLE_ALIGNMENT.CENTER
        table.autofit = False

        # Set Column Widths
        col_widths = [Inches(1.5), Inches(2.3), Inches(1.3), Inches(2.2)]
        for i, width in enumerate(col_widths):
            table.columns[i].width = width

        # Table Header Row
        hdr_cells = table.rows[0].cells
        hdr_titles = ["Annotation", "Technical Function", "Used In", "Viva Tip / Why It Matters"]
        for i, title in enumerate(hdr_titles):
            hdr_cells[i].text = title
            hdr_cells[i].paragraphs[0].runs[0].font.bold = True
            hdr_cells[i].paragraphs[0].runs[0].font.size = Pt(9.5)
            hdr_cells[i].paragraphs[0].runs[0].font.color.rgb = RGBColor(255, 255, 255)
            # Shading navy
            shading = parse_xml(r'<w:shd {} w:fill="1E3A8A"/>'.format(nsdecls('w')))
            hdr_cells[i]._tc.get_or_add_tcPr().append(shading)

        # Data Rows
        for r_idx, item in enumerate(items):
            row_cells = table.add_row().cells
            row_cells[0].text = item["name"]
            row_cells[1].text = item["function"]
            row_cells[2].text = item["files"]
            row_cells[3].text = item["viva_tip"]

            # Format text
            for c_idx in range(4):
                row_cells[c_idx].paragraphs[0].runs[0].font.size = Pt(8.5)
                # Alternate row shading
                if r_idx % 2 == 1:
                    shading = parse_xml(r'<w:shd {} w:fill="F8FAFC"/>'.format(nsdecls('w')))
                    row_cells[c_idx]._tc.get_or_add_tcPr().append(shading)

            # Bold annotation name
            row_cells[0].paragraphs[0].runs[0].font.bold = True
            row_cells[0].paragraphs[0].runs[0].font.color.rgb = RGBColor(37, 99, 235)

        doc.add_paragraph() # Spacer after table

    doc.save(filename)
    print(f"Successfully generated DOCX: {filename}")

# ==============================================================================
# GENERATOR 2: PORTABLE DOCUMENT FORMAT (.PDF)
# ==============================================================================
def generate_pdf(filename="FitLog_Annotations_Study_Guide.pdf"):
    doc = SimpleDocTemplate(
        filename,
        pagesize=letter,
        leftMargin=36,
        rightMargin=36,
        topMargin=36,
        bottomMargin=36
    )

    styles = getSampleStyleSheet()

    # Custom Styles
    style_title = ParagraphStyle(
        'DocTitle',
        parent=styles['Heading1'],
        fontName='Helvetica-Bold',
        fontSize=18,
        leading=22,
        textColor=colors.HexColor('#1E3A8A'),
        alignment=1, # Center
        spaceAfter=4
    )

    style_meta = ParagraphStyle(
        'DocMeta',
        fontName='Helvetica',
        fontSize=9,
        leading=12,
        textColor=colors.HexColor('#475569'),
        alignment=1,
        spaceAfter=12
    )

    style_cat = ParagraphStyle(
        'CategoryHeading',
        fontName='Helvetica-Bold',
        fontSize=12,
        leading=16,
        textColor=colors.HexColor('#1E3A8A'),
        spaceBefore=12,
        spaceAfter=6
    )

    style_th = ParagraphStyle(
        'TableHeader',
        fontName='Helvetica-Bold',
        fontSize=8,
        leading=10,
        textColor=colors.white
    )

    style_ann = ParagraphStyle(
        'AnnCell',
        fontName='Helvetica-Bold',
        fontSize=7.5,
        leading=9.5,
        textColor=colors.HexColor('#1D4ED8')
    )

    style_td = ParagraphStyle(
        'DataCell',
        fontName='Helvetica',
        fontSize=7.5,
        leading=9.5,
        textColor=colors.HexColor('#1E293B')
    )

    style_tip = ParagraphStyle(
        'TipCell',
        fontName='Helvetica-Oblique',
        fontSize=7,
        leading=9,
        textColor=colors.HexColor('#0F172A')
    )

    elements = []

    # Title & Metadata
    elements.append(Paragraph("FitLog — Annotations Reference & Viva Study Guide", style_title))
    elements.append(Paragraph("Project Leap — Java & DBMS Examination (Regulation 2023) | Course: U28CS491<br/><b>Student Reg No: 722825243194 | Sri Eshwar College of Engineering, Coimbatore</b>", style_meta))
    elements.append(HRFlowable(width="100%", thickness=1.5, color=colors.HexColor('#1E3A8A'), spaceAfter=10))

    # Categories
    categories = []
    for item in ANNOTATIONS_DATA:
        if item["category"] not in categories:
            categories.append(item["category"])

    col_widths = [105, 175, 110, 150]

    for cat in categories:
        items = [i for i in ANNOTATIONS_DATA if i["category"] == cat]
        elements.append(Paragraph(cat, style_cat))

        table_data = [[
            Paragraph("Annotation", style_th),
            Paragraph("Technical Function", style_th),
            Paragraph("Where Used", style_th),
            Paragraph("Viva / Examiner Tip", style_th)
        ]]

        for item in items:
            table_data.append([
                Paragraph(item["name"], style_ann),
                Paragraph(item["function"], style_td),
                Paragraph(item["files"], style_td),
                Paragraph(item["viva_tip"], style_tip)
            ])

        t = Table(table_data, colWidths=col_widths, repeatRows=1)
        t_style = TableStyle([
            ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor('#1E3A8A')),
            ('ALIGN', (0, 0), (-1, -1), 'LEFT'),
            ('VALIGN', (0, 0), (-1, -1), 'TOP'),
            ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor('#CBD5E1')),
            ('TOPPADDING', (0, 0), (-1, -1), 4),
            ('BOTTOMPADDING', (0, 0), (-1, -1), 4),
            ('LEFTPADDING', (0, 0), (-1, -1), 5),
            ('RIGHTPADDING', (0, 0), (-1, -1), 5),
        ])

        # Alternating background colors
        for row_idx in range(1, len(table_data)):
            if row_idx % 2 == 0:
                t_style.add('BACKGROUND', (0, row_idx), (-1, row_idx), colors.HexColor('#F8FAFC'))

        t.setStyle(t_style)
        elements.append(t)
        elements.append(Spacer(1, 8))

    doc.build(elements)
    print(f"Successfully generated PDF: {filename}")

if __name__ == "__main__":
    generate_docx("FitLog_Annotations_Study_Guide.docx")
    generate_pdf("FitLog_Annotations_Study_Guide.pdf")
