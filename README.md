# Mess Management System

A Spring Boot web application for managing students (mess members) and their meal-plan payments. It tracks who has an active or expired plan, lets you record payments, and generates Excel/PDF reports — all from a simple Thymeleaf dashboard, with a matching JSON REST API for building other frontends (mobile app, SPA, etc.).

## Features

- **Student management** — add, edit, soft-delete, and search students by name
- **Payment tracking** — record payments for a billing period (start date → end date) per student
- **Dashboard** — see every student's current status (`ACTIVE`, `EXPIRED`, or `NO_PAYMENT`) at a glance, with days remaining and an "expiring soon" flag (within 5 days), filterable by status and name
- **Reports** — export the dashboard to Excel (`.xlsx`) or PDF with the current filters applied
- **Branding** — upload a custom logo shown across the app
- **REST API** — full JSON API under `/api` for students, payments, and the dashboard

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.3.4 (Web, Data JPA, Validation) |
| Templating | Thymeleaf |
| Database | MySQL |
| Reports | Apache POI (Excel), OpenPDF (PDF) |
| Build tool | Maven |

## Project Structure

```
src/main/java/com/messmanagement/
├── controller/          # Thymeleaf page controllers (Dashboard, Student, Payment, Settings, Export)
│   └── api/              # JSON REST controllers (Student, Payment)
├── dto/                 # StudentStatusDTO – derived dashboard row
├── exception/           # Global exception handling
├── model/                # JPA entities: Student, Payment
├── repository/           # Spring Data JPA repositories
└── service/              # Business logic: Dashboard, Student, Payment, Export, Logo

src/main/resources/
├── templates/            # Thymeleaf views (dashboard, students, payments, settings)
├── static/                # CSS and images
└── application.properties
```

## Prerequisites

- Java 17+
- Maven 3.6+
- MySQL 8.x running locally

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/CoderSujjya/mess-management.git
cd mess-management
```

### 2. Configure the database

The app auto-creates the `mess_management` database on startup, so you only need MySQL running and reachable. Update the credentials in `src/main/resources/application.properties` to match your local setup:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mess_management?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
```

Tables are created/updated automatically via `spring.jpa.hibernate.ddl-auto=update` — no manual schema file needed.

### 3. Build and run

```bash
mvn spring-boot:run
```

Or build a jar and run it directly:

```bash
mvn clean package
java -jar target/mess-management.jar
```

### 4. Open the app

The server starts on port **2323**:

```
http://localhost:2323
```

## Configuration Reference

| Property | Default | Description |
|---|---|---|
| `server.port` | `2323` | HTTP port |
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/mess_management` | MySQL connection URL |
| `spring.jpa.hibernate.ddl-auto` | `update` | Auto create/update schema from entities |
| `app.upload.dir` | `uploads` | Folder where the uploaded logo is stored |
| `spring.servlet.multipart.max-file-size` | `5MB` | Max upload size for the logo |

## Web Routes

| Method | Path | Description |
|---|---|---|
| GET | `/` | Dashboard with status/name filters |
| GET | `/students` | List / search students |
| GET | `/students/new` | New student form |
| POST | `/students` | Create student |
| GET | `/students/edit/{id}` | Edit student form |
| POST | `/students/edit/{id}` | Update student |
| POST | `/students/delete/{id}` | Soft-delete student |
| GET | `/students/{id}/history` | Payment history for a student |
| GET | `/payments/new/{studentId}` | New payment form |
| POST | `/payments/{studentId}` | Record a payment |
| GET | `/settings` | Logo settings page |
| POST | `/settings/logo` | Upload a new logo |
| GET | `/export/excel` | Download dashboard as Excel |
| GET | `/export/pdf` | Download dashboard as PDF |

## REST API

| Method | Path | Description |
|---|---|---|
| GET | `/api/students` | List / search students by name |
| GET | `/api/students/{id}` | Get a single student |
| POST | `/api/students` | Create a student |
| PUT | `/api/students/{id}` | Update a student |
| DELETE | `/api/students/{id}` | Delete a student |
| GET | `/api/payments/student/{studentId}` | Payment history for a student |
| POST | `/api/payments/student/{studentId}` | Record a payment |
| GET | `/api/dashboard` | Dashboard rows, filterable by `status` and `name` |
