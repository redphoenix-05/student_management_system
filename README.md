# Student Management System

A comprehensive Spring Boot application for managing students, teachers, departments, and courses with complete entity relationships.

## System Architecture

### Entities and Relationships

1. **Student**
   - Fields: id, name, roll, email
   - Relationships:
     - Many-to-One with Department
     - Many-to-Many with Teachers
     - Many-to-Many with Courses (enrollments)

2. **Teacher**
   - Fields: id, name, email, phone
   - Relationships:
     - Many-to-One with Department
     - Many-to-Many with Students
     - One-to-Many with Courses (as creator)

3. **Department**
   - Fields: id, name, description
   - Relationships:
     - One-to-Many with Students
     - One-to-Many with Teachers
     - One-to-Many with Courses

4. **Course**
   - Fields: id, name, code, description, credits
   - Relationships:
     - Many-to-One with Department
     - Many-to-One with Teacher (creator)
     - Many-to-Many with Students (enrollments)

## Technology Stack

- **Backend**: Spring Boot 4.0.1
- **Database**: PostgreSQL 16
- **ORM**: Hibernate/JPA
- **Template Engine**: Thymeleaf
- **Build Tool**: Maven
- **Container**: Docker

## Prerequisites

- Java 17 or higher
- Docker Desktop
- Maven (wrapper included)

## Getting Started

### 1. Start the Database

```bash
docker-compose up -d postgres
```

This will start a PostgreSQL container with:
- Database: admindb
- Username: admin
- Password: admin
- Port: 5432

### 2. Run the Application

```bash
./mvnw spring-boot:run
```

Or on Windows:
```bash
mvnw.cmd spring-boot:run
```

The application will start on http://localhost:8080

## Application URLs

- **Home**: http://localhost:8080/
- **Students**: http://localhost:8080/students
- **Teachers**: http://localhost:8080/teachers
- **Departments**: http://localhost:8080/departments
- **Courses**: http://localhost:8080/courses

## REST API Endpoints

### Students
- `GET /students/api` - List all students
- `GET /students/api/{id}` - Get student by ID

### Teachers
- `GET /teachers/api` - List all teachers
- `GET /teachers/api/{id}` - Get teacher by ID

### Departments
- `GET /departments/api` - List all departments
- `GET /departments/api/{id}` - Get department by ID

### Courses
- `GET /courses/api` - List all courses
- `GET /courses/api/{id}` - Get course by ID

## Features

- Complete CRUD operations for all entities
- Entity relationship management
- Department-wise organization
- Teacher-created courses
- Student-course enrollments
- Teacher-student associations
- Responsive web interface
- RESTful API support
- Auto-schema generation with JPA

## Database Schema

The application uses Hibernate to automatically create the following tables:
- `students`
- `teachers`
- `departments`
- `courses`
- `teacher_student` (join table for M:N relationship)
- `student_course` (join table for M:N relationship)

## Configuration

Database configuration can be modified in `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/admindb
    username: admin
    password: admin
  jpa:
    hibernate:
      ddl-auto: update
```

## Building for Production

```bash
./mvnw clean package
```

This creates a JAR file in `target/webapp-0.0.1-SNAPSHOT.jar`

## Docker Deployment

To run the entire stack with Docker:

```bash
docker-compose up -d
```

This will start both the database and application containers.

## Stopping the Application

1. Stop the Spring Boot application: `Ctrl+C` in the terminal
2. Stop the database: `docker-compose down`

## Project Structure

```
WebApp/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/webapp/
│   │   │       ├── controller/    # REST and Web Controllers
│   │   │       ├── entity/        # JPA Entities
│   │   │       ├── repository/    # Spring Data Repositories
│   │   │       ├── service/       # Business Logic
│   │   │       └── dto/           # Data Transfer Objects
│   │   └── resources/
│   │       ├── templates/         # Thymeleaf HTML templates
│   │       └── application.yml    # Configuration
│   └── test/
├── compose.yaml                   # Docker Compose config
├── Dockerfile                     # Container definition
└── pom.xml                        # Maven dependencies
```

## Development

The application uses Spring Boot DevTools for hot reload during development. Any changes to the code will automatically restart the application.

## License

This project is for educational purposes.

