# Expense Tracker API

A backend REST API project I built as part of my learning with Java and Spring Boot. The idea was to create something practical — a system where users can track their daily expenses, set monthly budgets per category, and see how much they've spent vs how much they planned.

---

## Why I built this

I wanted to go beyond basic CRUD and actually implement something with real business logic — like budget warnings, category ownership checks, and proper pagination. This project helped me understand how Spring Boot, JPA, and MySQL work together in a real application.

---

## Tech I used

- Java 17
- Spring Boot 3.x
- Spring Data JPA + Hibernate
- MySQL 8
- Maven
- JUnit 5 + Mockito
- Lombok

---

## What it can do

- Register users with email validation and uniqueness check
- Create expense categories with optional monthly budgets
- Add, update, delete and view expenses
- Paginate and sort expense listings
- Search expenses by category, date range, and amount range
- Get a monthly budget summary per category with status like UNDER_BUDGET, WARNING, OVER_BUDGET, or NO_BUDGET

---

## How to run it locally

### You'll need

- Java 17 or above
- MySQL 8
- Maven

### Steps

1. Clone the repo
```bash
git clone https://github.com/richa9325/expense-tracker.git
cd expense-tracker
```

2. Create the database in MySQL
```sql
CREATE DATABASE expensetracker;
```

3. Run the schema file
```bash
mysql -u root -p expensetracker < schema.sql
```

4. Copy the env example and fill in your credentials
```bash
cp .env.example .env
```

Open `.env` and update:
```
DB_URL=jdbc:mysql://localhost:3306/expensetracker
DB_USERNAME=your_mysql_username
DB_PASSWORD=your_mysql_password
```

5. Run the app
```bash
./mvnw spring-boot:run
```

It'll start on `http://localhost:8080`

---

## API Overview

### Users
```
POST   /api/users              → register new user
GET    /api/users/{id}         → get user by id
```

### Categories
```
POST   /api/users/{userId}/categories           → create category
GET    /api/users/{userId}/categories           → list all categories
PUT    /api/users/{userId}/categories/{id}      → update category
DELETE /api/users/{userId}/categories/{id}      → delete category
```

### Expenses
```
POST   /api/users/{userId}/expenses             → add expense
GET    /api/users/{userId}/expenses             → list expenses (paginated)
GET    /api/users/{userId}/expenses/{id}        → get single expense
PUT    /api/users/{userId}/expenses/{id}        → update expense
DELETE /api/users/{userId}/expenses/{id}        → delete expense
GET    /api/users/{userId}/expenses/search      → search/filter expenses
GET    /api/users/{userId}/summary              → monthly budget summary
```

---

## Pagination & Sorting

When listing expenses you can pass these query params:

| Param | Default | Notes |
|-------|---------|-------|
| page | 0 | zero-indexed |
| size | 10 | between 1-50 |
| sortBy | date | date or amount |
| direction | desc | asc or desc |

Example:
```
GET /api/users/1/expenses?page=0&size=5&sortBy=amount&direction=asc
```

---

## Search Filters

All optional, can be combined:

```
GET /api/users/1/expenses/search?categoryId=1&startDate=2026-05-01&endDate=2026-05-31
GET /api/users/1/expenses/search?minAmount=500&maxAmount=2000
```

---

## Budget Summary Logic

| Status | When |
|--------|------|
| NO_BUDGET | No budget set for that category |
| UNDER_BUDGET | Spent less than 80% of budget |
| WARNING | Spent 80% or more but not exceeded |
| OVER_BUDGET | Spent more than the budget |

Only current calendar month expenses are counted.

---

## Some things I was careful about

- Category ownership is validated before adding/updating an expense — you can't link an expense to someone else's category
- Deleting a category that has expenses is blocked with a proper error message
- The monthly budget filter is applied inside the JPQL query, not in Java after fetching everything
- All responses use DTOs — no raw entity objects are returned from any API
- Custom `@Query` annotation used for the search endpoint

---

## Error Responses

All errors follow the same format:

```json
{
  "success": false,
  "timestamp": "2026-05-15T10:00:00",
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "amount": "must be greater than 0"
  }
}
```

---

## Tests

I wrote unit tests for the expense service using JUnit 5 and Mockito:

- Happy path — expense gets created when category belongs to the user
- Failure path — BusinessException is thrown when category belongs to a different user
- Used `Mockito.verify()` to confirm save was called
- Used `assertThrows()` for the exception case

```bash
./mvnw test
```

---

## Postman Collection

Import `Expense_Tracker_API.postman_collection.json` into Postman — all endpoints are there with sample request bodies ready to use.

---

## Database

Check `schema.sql` for the full schema with sample data included.
