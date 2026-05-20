# 🏨 Hostel Management System (HMS)

A three-tier JavaFX + MySQL application implementing the Student and Maintenance Staff modules.

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────┐
│         PRESENTATION LAYER  (JavaFX)                │
│  LoginScreen  │  StudentDashboard  │  MaintenanceDashboard │
└────────────────────────┬────────────────────────────┘
                         │  calls
┌────────────────────────▼────────────────────────────┐
│         BUSINESS LOGIC LAYER  (Java)                │
│              SystemController                        │
│  (GRASP Controller — single entry point for all UCs)│
└────────────────────────┬────────────────────────────┘
                         │  delegates this to
┌────────────────────────▼────────────────────────────┐
│         DATA / PERSISTENCE LAYER  (JDBC)            │
│  DBHandler (GRASP Pure Fabrication)                  │
│  DatabaseConnection (GoF Singleton)                  │
└────────────────────────┬────────────────────────────┘
                         │
              ┌──────────▼──────────┐
              │    MySQL Database    │
              │       hms_db        │
              └─────────────────────┘
```

---

## Design Patterns Used

| Pattern             | Where                  | Why                                              |
|---------------------|------------------------|--------------------------------------------------|
| GoF **Singleton**   | `DatabaseConnection`   | Only one DB connection across the app            |
| GRASP **Controller**| `SystemController`     | Separates UI events from business logic          |
| GRASP **Pure Fabrication** | `DBHandler`   | Handles SQL so domain classes stay clean         |
| GRASP **Information Expert** | `Complaint`, `Student`, `MaintenanceTask` | Classes own their own logic |
| GRASP **Creator**   | `SystemController`     | Creates domain objects (Complaint, VisitorLog…)  |

---

## Use Cases Implemented

| Use Case | Actor             | Description                        |
|----------|-------------------|------------------------------------|
| UC-01    | All               | Login to System                    |
| UC-02    | Student           | Submit Complaint                   |
| UC-03    | Student           | Request Room Swap                  |
| UC-04    | Student           | Register Visitor                   |
| UC-05    | Student           | View Complaint Status               |
| UC-11    | Maintenance Staff | View Assigned Tasks                |
| UC-12    | Maintenance Staff | Update Task Status (Start/Complete) |
| UC-13    | Maintenance Staff | Resolve Complaint (auto on Complete)|
| UC-14    | Maintenance Staff | Close Maintenance Ticket           |

---

## Prerequisites

- **Java 17+**
- **Maven 3.8+**
- **MySQL 8.0+**
- **JavaFX 21** (handled by Maven)

---

## Setup Instructions

### 1. Database Setup

```bash
mysql -u root -p < schema.sql
```

Or run the SQL manually in MySQL Workbench. This creates the `hms_db` database,
all tables, and inserts test seed data.

### 2. Configure DB Credentials

Open `src/main/java/com/hms/database/DatabaseConnection.java` and update:

```java
private static final String DB_USER = "root";       // ← your MySQL user
private static final String DB_PASS = "password";   // ← your MySQL password
```

### 3. Build & Run

```bash
cd HMS
mvn clean javafx:run
```

Or build a fat JAR:
```bash
mvn clean package
java -jar target/hostel-management-system-1.0-SNAPSHOT.jar
```

---

## Test Credentials (from seed data)

| Role              | Email              | Password |
|-------------------|--------------------|----------|
| Student           | ali@hms.com        | pass123  |
| Student           | sara@hms.com       | pass123  |
| Maintenance Staff | zaid@hms.com       | pass123  |
| Maintenance Staff | raza@hms.com       | pass123  |

---

## Project File Structure

```
HMS/
├── pom.xml
├── schema.sql
├── README.md
└── src/main/java/com/hms/
    ├── Main.java
    ├── database/
    │   ├── DatabaseConnection.java   ← GoF Singleton
    │   └── DBHandler.java            ← GRASP Pure Fabrication
    ├── models/
    │   ├── User.java
    │   ├── Student.java              ← GRASP Information Expert
    │   ├── MaintenanceStaff.java     ← GRASP Information Expert
    │   ├── Complaint.java            ← GRASP Information Expert
    │   ├── MaintenanceTask.java
    │   ├── RoomSwapRequest.java
    │   └── VisitorLog.java
    ├── controllers/
    │   └── SystemController.java     ← GRASP Controller
    └── ui/
        ├── HmsStyle.java             ← Shared storm-palette styling
        ├── LoginScreen.java
        ├── StudentDashboard.java
        └── MaintenanceDashboard.java
```

---

## Color Palette (Storm Theme)

| Token   | Hex       | Used For                     |
|---------|-----------|------------------------------|
| NAVY    | `#0D1F2A` | Main background              |
| TEAL_DK | `#1B3D4A` | Cards, sidebar               |
| SAGE    | `#6A9090` | Borders, secondary text      |
| MINT    | `#7BC4BE` | Primary accent, buttons      |
| SILVER  | `#D4DDE0` | Primary text                 |

---

## Warden Module (Added)

### New Use Cases

| Use Case | Actor  | Description                                              |
|----------|--------|----------------------------------------------------------|
| UC-06    | Warden | View & filter all complaints by priority/status          |
| UC-07    | Warden | Assign complaint → creates task + notifies staff         |
| UC-08    | Warden | Approve/Reject room swap with availability check         |
| UC-09    | Warden | Issue fine auto-calculated from policy table             |
| UC-10    | Warden | Emergency reallocation + marks old room UNAVAILABLE      |

### New Test Credentials

| Role   | Email              | Password |
|--------|--------------------|----------|
| Warden | warden@hms.com     | pass123  |

### New Database Objects

| Object                      | Purpose                                            |
|-----------------------------|----------------------------------------------------|
| `rooms`                     | Room inventory with availability status            |
| `fine_policy`               | Policy table: violation type → fine amount         |
| `fines`                     | Issued fine records; updates student dues atomically|
| `notifications`             | In-app notification log for staff and students     |
| `emergency_reallocations`   | Audit log for emergency reallocation events        |

### New Files Added

```
schema_warden_additions.sql           ← run after original schema.sql
src/main/java/com/hms/
├── models/
│   ├── Warden.java                   ← Domain entity
│   ├── Room.java                     ← Domain entity (availability logic)
│   └── Fine.java                     ← Domain entity
├── database/DBHandler.java           ← 15 new methods added
├── controllers/SystemController.java ← UC-06..UC-10 + currentWarden session
└── ui/
    ├── LoginScreen.java              ← Updated to route Warden role
    └── WardenDashboard.java          ← Full warden UI (new file)
```

### Setup (Warden Module)

```bash
mysql -u root -p < schema_warden_additions.sql
```
Then run as before:
```bash
mvn clean javafx:run
```
