# EduManage NG — Nigerian School Management System

A production-ready SaaS school management platform for **creche, nursery, primary and secondary schools** in Nigeria. Built with Java Spring Boot microservices, React, and MySQL.

---

## Features

- **Multi-tenant school registration** — each school is isolated
- **Student enrolment** — admission numbers auto-generated per level
- **Academic results** — Nigerian grading system (A1–F9), FIRST/SECOND/THIRD TERM
- **Guardian portal** — parents/guardians view their ward's published results
- **Fee management** — tuition, levies, Paystack integration (NGN)
- **File uploads** — student documents, profile images
- **Notifications** — email (SMTP) + SMS (Termii API)
- **Role-based access** — SUPER_ADMIN, SCHOOL_ADMIN, TEACHER, GUARDIAN

---

## Architecture

```
┌────────────┐       ┌─────────────┐       ┌──────────────┐
│  React App │──────▶│ API Gateway │──────▶│ Microservices│
│  (port 3000│       │  (port 8080)│       │  8081–8088   │
└────────────┘       └─────────────┘       └──────────────┘
                            │
                   ┌────────▼────────┐
                   │ Service Registry│
                   │ Eureka (8761)   │
                   └────────┬────────┘
                            │
                   ┌────────▼────────┐
                   │  Config Server  │
                   │   (port 8888)   │
                   └─────────────────┘
```

### Microservices

| Service | Port | Database |
|---------|------|----------|
| service-registry | 8761 | — |
| config-server | 8888 | — |
| api-gateway | 8080 | — |
| auth-service | 8081 | edumanage_auth |
| school-service | 8082 | edumanage_school |
| student-service | 8083 | edumanage_student |
| academic-service | 8084 | edumanage_academic |
| guardian-service | 8085 | edumanage_guardian |
| file-service | 8086 | edumanage_file |
| financial-service | 8087 | edumanage_financial |
| notification-service | 8088 | edumanage_notification |

---

## Prerequisites

- Docker 20+ and Docker Compose
- (For manual dev) Java 21, Maven 3.9+, Node.js 20+, MySQL 8.0

---

## Quick Start (Docker Compose)

```bash
# 1. Clone the repo
git clone <repo-url>
cd efecsglobal_school_Management_system

# 2. Create environment file
cp .env.example .env
# Edit .env with your values (MySQL password, Paystack key, SMTP, Termii)

# 3. Start all services
docker-compose up -d

# 4. Open the app
open http://localhost:3000
```

Wait ~60 seconds for all services to initialize. Check health:
```bash
docker-compose ps
```

---

## Manual Development Setup

### Backend

```bash
# Ensure Java 21 and Maven are installed
java -version     # 21.x.x
mvn -version      # 3.9.x

# Create MySQL databases (or use the init script)
mysql -u root -p < scripts/init-databases.sql

# Start services in order:
cd backend

# 1. Service Registry
cd service-registry && mvn spring-boot:run &

# 2. Config Server (wait for registry)
cd ../config-server && mvn spring-boot:run &

# 3. API Gateway
cd ../api-gateway && mvn spring-boot:run &

# 4. Business services (in any order)
cd ../auth-service && mvn spring-boot:run &
cd ../school-service && mvn spring-boot:run &
# ... repeat for remaining services
```

### Frontend

```bash
cd frontend
npm install
npm run dev
# Open http://localhost:3000
```

---

## Default Credentials

| Role | Email | Password |
|------|-------|----------|
| Super Admin | superadmin@edumanageng.com | Admin@1234 |

> Change the super admin password immediately after first login.

---

## API Reference

All requests go through the **API Gateway** at `http://localhost:8080`.

### Auth (`/api/v1/auth`)
| Method | Path | Description |
|--------|------|-------------|
| POST | `/login` | Login, returns JWT tokens |
| POST | `/register` | Register a new user |
| GET | `/me` | Get current user profile |
| POST | `/refresh` | Refresh access token |

### Schools (`/api/v1/schools`)
| Method | Path | Description |
|--------|------|-------------|
| POST | `/` | Register a new school |
| GET | `/{id}` | Get school details |
| GET | `/` | List all schools (SUPER_ADMIN) |
| POST | `/{id}/sessions` | Create academic session |
| GET | `/{id}/sessions` | List sessions |
| POST | `/{id}/classes` | Create a class |
| GET | `/{id}/classes` | List classes |

### Students (`/api/v1/students`)
| Method | Path | Description |
|--------|------|-------------|
| POST | `/` | Enroll student |
| GET | `/{id}` | Get student |
| GET | `/school/{schoolId}` | List students by school |
| GET | `/school/{schoolId}/search?q=` | Search students |

### Academic (`/api/v1/academic`)
| Method | Path | Description |
|--------|------|-------------|
| POST | `/subjects` | Create subject |
| POST | `/results` | Record result |
| GET | `/results/student/{id}` | Get all student results |
| GET | `/results/student/{id}/term` | Get term results |
| POST | `/results/publish` | Publish results (school/session/term) |

### Guardians (`/api/v1/guardians`)
| Method | Path | Description |
|--------|------|-------------|
| POST | `/` | Register guardian |
| GET | `/{id}/wards` | Get linked wards |
| POST | `/{id}/wards` | Link ward to guardian |

### Payments (`/api/v1/payments`)
| Method | Path | Description |
|--------|------|-------------|
| POST | `/fees` | Create fee structure |
| POST | `/payments/initiate` | Initiate Paystack payment |
| POST | `/payments/cash` | Record cash payment |
| GET | `/payments/student/{studentId}` | Student payment history |

### Files (`/api/v1/files`)
| Method | Path | Description |
|--------|------|-------------|
| POST | `/upload` | Upload file (multipart) |
| GET | `/{id}/download` | Download file |
| DELETE | `/{id}` | Delete file |

### Notifications (`/api/v1/notifications`)
| Method | Path | Description |
|--------|------|-------------|
| POST | `/send/email` | Send email notification |
| POST | `/send/sms` | Send SMS via Termii |

---

## Nigerian-Specific Features

### Grading System (WAEC-aligned)
| Score | Grade | Remark |
|-------|-------|--------|
| 75–100 | A1 | Excellent |
| 70–74 | B2 | Very Good |
| 65–69 | B3 | Good |
| 60–64 | C4 | Credit |
| 55–59 | C5 | Credit |
| 50–54 | C6 | Credit |
| 45–49 | D7 | Pass |
| 40–44 | E8 | Pass |
| 0–39 | F9 | Fail |

**Score breakdown**: CA1 (10) + CA2 (10) + CA3 (10) + Exam (70) = 100

### School Levels
- **CRECHE** — ages 0–2
- **NURSERY** — ages 3–5
- **PRIMARY** — JSS 1–6
- **SECONDARY** — SSS 1–3

### Academic Terms
- FIRST_TERM, SECOND_TERM, THIRD_TERM
- Sessions in `YYYY/YYYY` format (e.g., `2024/2025`)

### Currency
All amounts in **Nigerian Naira (₦ / NGN)**

### Phone Numbers
Format: `+234XXXXXXXXX` or `0XXXXXXXXX` (MTN, Glo, Airtel, 9mobile)

---

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `MYSQL_ROOT_PASSWORD` | MySQL root password | — |
| `JWT_SECRET` | JWT signing secret (64+ hex chars) | provided |
| `PAYSTACK_SECRET_KEY` | Paystack live/test key | sk_test_placeholder |
| `SMTP_HOST` | Email SMTP host | smtp.gmail.com |
| `SMTP_USER` | Email sender address | — |
| `SMTP_PASSWORD` | Email app password | — |
| `TERMII_API_KEY` | Termii SMS API key | — |
| `FILE_UPLOAD_DIR` | File upload directory | ./uploads |

---

## License

Proprietary — EfecsGlobal. All rights reserved.
