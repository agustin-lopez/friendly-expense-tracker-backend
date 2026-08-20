# Friendly Expense Tracker — Backend

A REST API for a personal finance tracker, built with Spring Boot. It handles authentication, transaction and category management, and financial summaries for the [Friendly Expense Tracker frontend](https://github.com/agustin-lopez/friendly-expense-tracker-frontend).

**Live demo:** https://friendly-expense-tracker-frontend.vercel.app

**BEFORE GIVING THE DEMO A TRY**: Please note that this demo is hosted on Render's free tier, which includes a cold start of ~1 minute after being inactive for 15 minutes.

**Frontend repo:** https://github.com/agustin-lopez/friendly-expense-tracker-frontend

---

## About this project

This is my first full end-to-end project: designed, built, and deployed on my own, from the database schema to a live production deployment. I built it to move past tutorials and actually work through the problems that only show up when you build something real — normalizing a schema after the fact, debugging a CORS issue in production, deciding how much validation belongs in the frontend versus the backend, choosing between two designs for a security flow and living with the tradeoff.

It started as a simple expense tracker and grew, feature by feature, into something with real authentication, email-based account verification, rate limiting, and many useful details for a polished UX. It's not a big or complex system — it's a small one, built carefully.

## Tech stack

- **Java 17** + **Spring Boot**
- **Spring Data JPA** / **Hibernate**
- **Spring Security** + **JWT** for stateless authentication
- **PostgreSQL** ([Neon](https://neon.tech), serverless)
- **Bucket4j** for rate limiting
- **Resend** for transactional email
- **Thymeleaf** for custom email templates
- **Maven**
- Deployed on **Render** (Docker)

## Features

**Authentication & accounts**
- Registration with email verification (no login until the account is confirmed)
- JWT-based login, stateless sessions
- Password recovery via emailed, time-limited, single-use tokens
- Password changes (from within the app) require re-confirmation via email before taking effect
- Rate limiting on login and registration endpoints (per IP) to mitigate brute-force and mass account creation

**Transactions & categories**
- Full CRUD on transactions and categories, scoped per user
- User-defined categories with custom icons, seeded with sensible defaults on signup
- Transactions can be uncategorized (categories can be safely deleted without orphaning data — `ON DELETE SET NULL`)
- Server-side pagination, grouped by month — pages never split a month across two pages
- Full-text style search across description and category name
- Aggregate endpoints (balance summary, totals by category) computed server-side, independent of pagination

**Engineering details worth mentioning**
- Centralized exception handling (`@RestControllerAdvice`) translating database constraint violations and validation errors into meaningful HTTP responses
- Bean Validation (`@Valid`) on all write endpoints
- A unified `temporary_tokens` table (with a `type` discriminator) backing all three token-based flows — email verification, password reset, and password-change confirmation
- A scheduled job (`@Scheduled`) that purges expired/used tokens automatically

## Getting started locally

### Prerequisites
- Java 17+
- Maven
- A local PostgreSQL instance
- A [Resend](https://resend.com) account (free tier) if you want outgoing email to work locally

### Setup

1. Clone the repo and create a local database:
   ```sql
   CREATE DATABASE friendly_expense_tracker;
   ```

2. Create `src/main/resources/application-local.properties` (this file is gitignored) with:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/friendly_expense_tracker
   spring.datasource.username=postgres
   spring.datasource.password=your_local_password

   app.jwt.secret=any_long_random_string_for_local_dev
   app.jwt.expiration-ms=86400000

   app.resend.api-key=your_resend_api_key
   app.resend.from-email=onboarding@resend.dev

   app.frontend.url=http://localhost:5173

   spring.jpa.show-sql=true
   ```

3. Run with the `local` profile active:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   ```

   The schema is created automatically on startup (`schema.sql`), no manual migration step needed.

### Environment variables (production)

| Variable | Description |
|---|---|
| `DATABASE_URL` | JDBC connection string (`jdbc:postgresql://...`) |
| `DATABASE_USERNAME` | Database user |
| `DATABASE_PASSWORD` | Database password |
| `JWT_SECRET` | Secret key for signing JWTs (long, random) |
| `EMAIL_API_KEY` | API key for transactional email |
| `FROM_EMAIL` | Verified sender address |
| `FRONTEND_URL` | Deployed frontend URL, used to build email links and configure CORS |

## Known limitations

- **Cold starts**: hosted on Render's free tier, which spins down after 15 minutes of inactivity. The first request after idle time can take up to ~1 minute.
- **Email delivery**: transactional email currently runs through Resend without a verified custom domain, which limits deliverability to certain addresses. Migrating to a verified domain is planned.
- **No automated backups**: the database is on Neon's free tier, which doesn't include managed backups.

## License

MIT
