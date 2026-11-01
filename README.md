# UniTrack University Academic Management System

UniTrack is a web-based academic management system developed to make university record management easier and more organized. The project brings important academic information together in one place, including student records, courses, teachers, enrollments, and grades.

I built this project to practise my Java development skills and understand how a web application connects its interface, backend logic, and database.

## Features

- **Student Management:** Add, view, edit, and delete student records.
- **Course Management:** Manage course-related information.
- **Teacher Management:** Organize teacher records.
- **Enrollment Management:** Maintain student course enrollment records.
- **Grade Management:** Store and manage students' academic results.
- **Dashboard:** View academic information and key statistics.
- **Search and Filtering:** Find student records using available search and department filters.
- **Record Protection:** Prevent student deletion when grades or course enrollments are linked to the record.

## Technologies Used

- Java
- Spring Boot
- Spring MVC
- Thymeleaf
- PostgreSQL
- Spring Data JPA / Hibernate
- Maven
- HTML and CSS

## Project Structure

The project follows a standard Spring Boot structure:

- `src/main/java` – Java source code, controllers, entities, and repositories.
- `src/main/resources/templates` – Thymeleaf HTML templates.
- `src/main/resources/static` – Static resources such as CSS and JavaScript, if included.
- `pom.xml` – Project dependencies and Maven configuration.

## Getting Started

### Prerequisites

Before running the project, make sure you have:

- Java version compatible with the project configuration
- PostgreSQL
- Maven, or the Maven Wrapper included in the project

### Setup

1. Clone this repository:

   ```bash
   git clone https://github.com/YOUR-GITHUB-USERNAME/UniTrack-Academic-Management-System.git
   ```

2. Open the project folder in your preferred IDE, such as VS Code.

3. Create a PostgreSQL database named `unitrack`, or update the database name in the application configuration to match your setup.

4. Configure your database credentials using environment variables. The application expects `DB_USERNAME` and `DB_PASSWORD` if these variables are referenced in `application.properties`.

5. Run the application from the project directory:

   **Windows:**

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

   **macOS/Linux:**

   ```bash
   ./mvnw spring-boot:run
   ```

6. Open the application in your browser:

   `http://localhost:8080`
   

## Database and Academic Records

UniTrack uses PostgreSQL to store its academic data. Student records can be deleted when no related academic records prevent deletion. If a student has linked grades or course enrollments, the application blocks deletion to preserve those records.

## What I Learned

Working on UniTrack helped me practise building a Java-based web application, connecting it to a relational database, organizing application code into separate components, and handling relationships between academic records.




