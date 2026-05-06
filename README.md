<p align="center">
  <img src="https://spring.io/img/spring.svg" width="100" alt="Spring Boot Logo" />
</p>

<h1 align="center">Multi-Tenant SaaS API</h1>

<p align="center">
  A production-ready multi-tenant authentication API built with Spring Boot 3.5, PostgreSQL, and JWT.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen?style=flat-square&logo=springboot" />
  <img src="https://img.shields.io/badge/PostgreSQL-16-blue?style=flat-square&logo=postgresql" />
  <img src="https://img.shields.io/badge/Docker-ready-2496ED?style=flat-square&logo=docker" />
</p>


## Overview

This project provides a solid foundation for building SaaS applications that need to serve multiple independent customers (tenants) from a single deployment. Each tenant's users are fully isolated from one another, and the API ships with everything you'd expect in a production system — JWT auth, token refresh, rate limiting, OTP-based password reset, and email notifications.


## Features

| Category | Details |
|---|---|
| **Multi-tenancy** | Each tenant has fully isolated users and data |
| **Authentication** | JWT access + refresh tokens |
| **Security** | Token blacklisting on logout and password change |
| **Authorization** | Role-based access control (`ROLE_ADMIN`, `ROLE_USER`) |
| **Password Reset** | OTP-based flow with email delivery |
| **Email** | Welcome and password-change notifications via Gmail SMTP |
| **Rate Limiting** | IP-based, 20 requests/min (Bucket4j) |
| **Auditing** | `createdAt`, `updatedAt`, `createdBy`, `updatedBy` via JPA |
| **Observability** | Request/response logging |
| **API Docs** | Swagger UI via springdoc OpenAPI 3 + Postman Collection |
| **Deployment** | Docker & Docker Compose ready |
| **Testing** | Unit tests with Mockito |


## Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.5 · Spring Security 6 |
| **Database** | PostgreSQL 16 · JPA / Hibernate |
| **Auth** | JWT — jjwt 0.11.5 |
| **Rate Limiting** | Bucket4j |
| **Email** | Gmail SMTP |
| **API Docs** | Swagger / OpenAPI 3 — springdoc 2.8.6 |
| **Utilities** | Lombok · Maven |
| **Deployment** | Docker · Docker Compose |


## Project Structure

```
src/main/java/com/saasauth/multitenant/
├── config/        # AuditConfig, SwaggerConfig
├── controller/    # Auth, User, Tenant, PasswordReset controllers
├── dto/           # Request and response DTOs
├── exception/     # GlobalExceptionHandler and custom exceptions
├── model/         # User, Tenant, Role, RefreshToken, BlacklistedToken, PasswordResetOtp, BaseEntity
├── repository/    # JPA repositories
├── security/      # JwtUtil, JwtFilter, RateLimitFilter, SecurityConfig, CustomUserDetailsService
└── service/       # Auth, User, Tenant, RefreshToken, TokenBlacklist, Email, PasswordReset services
```


## Getting Started

### Prerequisites

- Java 21
- Maven
- PostgreSQL 16
- Docker *(optional)*

### 1. Clone the repository

```bash
git clone https://github.com/devMohamed-Hassan/multitenant-saas-api.git
cd multitenant-saas-api
```

### 2. Configure environment variables

Create a `.env` file in the root directory:

```env
DB_URL=jdbc:postgresql://localhost:5432/multitenant_db
DB_USERNAME=postgres
DB_PASSWORD=postgres123
JWT_SECRET=super-secret-key-must-be-at-least-32-characters-long
JWT_EXPIRATION=86400000
APP_MESSAGE=Hello from env file!
SPRING_PROFILE=dev

MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_gmail_address@gmail.com
MAIL_PASSWORD=your_gmail_app_password
MAIL_FROM=your_gmail_address@gmail.com
```

> **Note:** `JWT_SECRET` must be at least 32 characters for HMAC-SHA256 signing.

### 3. Run locally

```bash
./mvnw spring-boot:run
```

### 4. Run with Docker

```bash
docker compose up --build
```

## API Documentation

Two interactive documentation options are available:

### Swagger UI

Served locally at:

```
http://localhost:8080/swagger-ui/index.html
```

### Postman Collection

A fully documented Postman collection with example requests and responses is available here:

[![Run in Postman](https://run.pstmn.io/button.svg)](https://documenter.getpostman.com/view/45449526/2sBXqMHzTG)

> Environment variable support for `baseUrl`, `accessToken`, `refreshToken`, and more


## API Reference

### Authentication — `/api/v1/auth`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/register` | Register a new user | Public |
| `POST` | `/login` | Login and receive tokens | Public |
| `POST` | `/refresh` | Refresh access token | `Bearer <refreshToken>` |
| `POST` | `/logout` | Logout and revoke tokens | `Bearer <accessToken>` |

### Password Reset — `/api/v1/auth/password`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/forgot` | Send OTP to email | Public |
| `POST` | `/confirm-otp` | Verify OTP | `OTP-Token` header |
| `POST` | `/reset` | Reset password | `Reset-Token` header |
| `POST` | `/resend-otp` | Resend OTP | Public |

### Users — `/api/v1/users`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/me` | Get current user profile | `Bearer <accessToken>` |
| `PUT` | `/me` | Update current user profile | `Bearer <accessToken>` |
| `PUT` | `/me/password` | Change password | `Bearer <accessToken>` |
| `GET` | `/` | List all users in tenant | Admin only |
| `PUT` | `/{id}/role` | Update a user's role | Admin only |
| `DELETE` | `/{id}` | Delete a user | Admin only |

### Tenant — `/api/v1/tenant`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/` | Get current tenant info | `Bearer <accessToken>` |
| `PUT` | `/` | Update tenant | Admin only |
| `DELETE` | `/` | Delete tenant | Admin only |


## Flows

### Authentication Flow

```
POST /register  →  returns { accessToken, refreshToken }
                ↓
Use accessToken in:  Authorization: Bearer <accessToken>
                ↓
Token expired?  →  POST /refresh with refreshToken  →  new accessToken
                ↓
Done?           →  POST /logout  →  blacklists accessToken + revokes refreshToken
```

### Password Reset Flow

```
1. POST /api/v1/auth/password/forgot
   Body:             { "email": "john@example.com" }
   Response header:  OTP-Token: <token>
   Action:           OTP sent to email

2. POST /api/v1/auth/password/confirm-otp
   Header:           OTP-Token: <token>
   Body:             { "otp": "123456" }
   Response header:  Reset-Token: <token>

3. POST /api/v1/auth/password/reset
   Header:           Reset-Token: <token>
   Body:             { "newPassword": "newpass123", "confirmPassword": "newpass123" }
   Response:         { "success": true, "message": "Password has been reset" }
```

## Role-Based Access

| Role | Permissions |
|------|-------------|
| `ROLE_USER` | View and update own profile, change own password |
| `ROLE_ADMIN` | All of the above + manage all tenant users + update/delete tenant |

To promote a user to admin, update directly in the database and re-login to receive a fresh token:

```sql
UPDATE users SET role = 'ROLE_ADMIN' WHERE email = 'admin@example.com';
```


## Rate Limiting

All endpoints are limited to **20 requests per minute per IP address**. Exceeding the limit returns HTTP `429`:

```json
{
  "success": false,
  "message": "Too many requests. Please try again later.",
  "data": null
}
```

The remaining request budget is exposed in the `X-Rate-Limit-Remaining` response header.


## Environment Profiles

| Profile | Behaviour |
|---------|-----------|
| `dev` | SQL logging enabled, debug log level |
| `prod` | No SQL logging, optimised for production |

Set the active profile in your `.env`:

```env
SPRING_PROFILE=prod
```

## Running Tests

```bash
./mvnw test
```

Current coverage:

| Test Class | Tests |
|---|---|
| `AuthServiceTest` | 5 tests |
| `UserServiceTest` | 6 tests |


## Docker

```bash
# Start
docker compose up --build

# Stop
docker compose down

# Follow logs
docker compose logs -f app
```
