# Local Windows Run (No Docker)

This project runs locally with MySQL 8.4, Apache Kafka, Java 21/Maven, and the included Next.js frontend.

## Ports

- Frontend: `3000`
- Order service: `8081`
- Inventory service: `8082`
- Payment service: `8083`
- Notification service: `8084`
- Kafka: `9092`
- MySQL 8.4: `3306`

## 1. Start MySQL 8.4

Do not run XAMPP MariaDB on port 3306 at the same time.

Initialize the schemas once:

```powershell
cd "C:\Users\Anonymous\Desktop\event-driven"
Get-Content .\scripts\init-mysql-local.sql | mysql -u root -p
```

## 2. Start Kafka

```powershell
cd C:\kafka\kafka_2.13-4.3.1
.\bin\windows\kafka-server-start.bat .\config\server.properties
```

Keep this terminal open.

Create the project topics once (safe to re-run):

```powershell
cd "C:\Users\Anonymous\Desktop\event-driven"
.\scripts\create-topics.ps1
```

## 3. Build the Java project

Use Java 21 when possible.

```powershell
cd "C:\Users\Anonymous\Desktop\event-driven"
mvn clean install -DskipTests
```

## 4. Start backend services

```powershell
.\scripts\start-services-local.ps1
```

This opens one PowerShell window for each Spring Boot service.

## 5. Start frontend

```powershell
.\scripts\start-frontend-local.ps1
```

The script installs npm dependencies the first time and opens the frontend at:

`http://localhost:3000`

Alternatively, after the initial setup you can start backends and frontend together:

```powershell
.\scripts\start-all-local.ps1
```

## Backend API additions for the UI

The frontend uses these read endpoints in addition to the original single-record endpoints:

- `GET /api/orders`
- `GET /api/inventory`
- `GET /api/payments`
- `GET /api/notifications`

The original event-driven write path remains unchanged.
