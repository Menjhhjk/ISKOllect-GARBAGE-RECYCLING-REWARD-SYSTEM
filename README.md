# ISKOllect

ISKOllect is a JavaFX desktop application for a school-based garbage recycling rewards system. It allows students to register using their PUP webmail, submit collected bottles, earn points and badges, redeem rewards, and view their bottle and transaction history.

The system was developed as an Object-Oriented Programming project using a layered Java architecture with PostgreSQL/Supabase as the database.

## Features

- User registration and login using PUP webmail
- Password hashing with BCrypt
- Bottle submission with point calculation
- Streak and badge rewards
- Rewards catalog and coupon redemption
- Transaction history with filters
- Bottle collection records
- Profile management
- Password update validation
- PostgreSQL/Supabase database connection

## Technology Stack

- Java 21
- JavaFX 21
- Maven
- JDBC
- PostgreSQL / Supabase
- jBCrypt
- FXML and CSS for the interface

## Project Structure

```text
ISKOllect-GARBAGE-RECYCLING-REWARD-SYSTEM/
├── data/
│   └── profile_pics/
│       ├── user_2.png
│       ├── user_3.png
│       ├── user_5.png
│       ├── user_9.png
│       ├── user_10.png
│       ├── user_78.png
│       └── user_92.png
├── sql/
│   └── 00_create_core_schema_postgresql.sql
├── src/
│   └── main/
│       ├── java/
│       │   ├── module-info.java
│       │   └── com/
│       │       └── iskollect/
│       │           ├── Main.java
│       │           ├── TestDatabaseConnection.java
│       │           ├── controller/
│       │           │   ├── BadgeHistoryController.java
│       │           │   ├── BottleRecordsController.java
│       │           │   ├── BottleSubmitController.java
│       │           │   ├── DashboardController.java
│       │           │   ├── InOutController.java
│       │           │   ├── LoginController.java
│       │           │   ├── ProfileController.java
│       │           │   ├── RedeemController.java
│       │           │   ├── RegisterController.java
│       │           │   ├── RewardsController.java
│       │           │   └── TransactionController.java
│       │           ├── dao/
│       │           │   ├── BottleRecordDAO.java
│       │           │   ├── CouponDAO.java
│       │           │   ├── InOutLogDAO.java
│       │           │   ├── PointsLedgerDAO.java
│       │           │   ├── RedemptionDAO.java
│       │           │   ├── StreakDAO.java
│       │           │   └── UserDAO.java
│       │           ├── exception/
│       │           │   ├── AuthException.java
│       │           │   ├── DatabaseException.java
│       │           │   ├── DuplicateLogException.java
│       │           │   ├── InsufficientPointsException.java
│       │           │   ├── InvalidInputException.java
│       │           │   └── NavigationException.java
│       │           ├── model/
│       │           │   ├── ActivityHistory.java
│       │           │   ├── BottleRecord.java
│       │           │   ├── Coupon.java
│       │           │   ├── InOutLog.java
│       │           │   ├── LogResult.java
│       │           │   ├── RedeemResult.java
│       │           │   ├── Redemption.java
│       │           │   ├── ReportResult.java
│       │           │   ├── SubmitResult.java
│       │           │   └── User.java
│       │           ├── scheduler/
│       │           │   └── WeeklyResetScheduler.java
│       │           ├── service/
│       │           │   ├── ActivityHistoryService.java
│       │           │   ├── AuthService.java
│       │           │   ├── BadgeService.java
│       │           │   ├── BottleService.java
│       │           │   ├── CouponService.java
│       │           │   ├── InOutService.java
│       │           │   ├── PointsService.java
│       │           │   ├── ReportService.java
│       │           │   ├── SecurityCheck.java
│       │           │   └── StreakService.java
│       │           └── util/
│       │               ├── AlertUtil.java
│       │               ├── ClockUtil.java
│       │               ├── CouponGenerator.java
│       │               ├── DBConnection.java
│       │               ├── PasswordUtil.java
│       │               ├── RedirectUtil.java
│       │               ├── SceneCache.java
│       │               ├── SessionManager.java
│       │               └── UserValidator.java
│       └── resources/
│           ├── config.properties
│           └── com/
│               └── iskollect/
│                   ├── assets/
│                   │   ├── 1.png
│                   │   ├── 2.png
│                   │   ├── 3.png
│                   │   ├── 4.png
│                   │   ├── 5.png
│                   │   ├── 6.png
│                   │   ├── 7.png
│                   │   ├── bf780c16-c3ed-449f-b2ab-6ceda0ca0e53.png
│                   │   ├── chipss.png
│                   │   ├── emerald.png
│                   │   ├── img.png
│                   │   ├── meall.png
│                   │   ├── pencil.png
│                   │   ├── recycle.jpg
│                   │   ├── ssfood.png
│                   │   └── supplies.png
│                   ├── fxml/
│                   │   ├── badgehistorypopup.fxml
│                   │   ├── bottlerecords.fxml
│                   │   ├── dashboard.fxml
│                   │   ├── login.fxml
│                   │   ├── profile.fxml
│                   │   ├── rewardsCatalog.fxml
│                   │   ├── signup.fxml
│                   │   ├── submitbottlepopup.fxml
│                   │   └── transactionhistory.fxml
│                   └── style.css
├── .gitignore
├── README.md
├── SESSION_UPDATES.md
├── mvnw
├── mvnw.cmd
├── mvnw.txt
├── pom.xml
├── run.bat
├── wrapper-index.html
└── wrapper-versions.html
```

Generated build output such as `target/` and IDE metadata such as `.idea/` and `.vscode/` are intentionally excluded from this structure.

### Directory Reference

| Directory | Description |
| --- | --- |
| `data/profile_pics/` | Stores uploaded user profile pictures used by the Profile screen. |
| `sql/` | Contains PostgreSQL/Supabase schema reference files. |
| `src/main/java/` | Contains the Java source code and module declaration. |
| `src/main/java/com/iskollect/controller/` | JavaFX controller classes for FXML screens and popups. |
| `src/main/java/com/iskollect/dao/` | Data access objects that communicate with the PostgreSQL database. |
| `src/main/java/com/iskollect/exception/` | Custom exception classes for validation, database, navigation, and domain errors. |
| `src/main/java/com/iskollect/model/` | Plain Java model/result classes used across controllers, services, and DAOs. |
| `src/main/java/com/iskollect/scheduler/` | Scheduled background maintenance logic. |
| `src/main/java/com/iskollect/service/` | Business logic for authentication, bottles, badges, points, streaks, coupons, reports, and activity history. |
| `src/main/java/com/iskollect/util/` | Shared utility classes for alerts, database connections, password hashing, redirects, scene caching, sessions, and validation. |
| `src/main/resources/` | Runtime resources copied into the application classpath. |
| `src/main/resources/com/iskollect/assets/` | Image assets for badges, reward items, branding, and UI graphics. |
| `src/main/resources/com/iskollect/fxml/` | JavaFX FXML layout files for the application screens. |

### File Reference

| File | Description |
| --- | --- |
| `.gitignore` | Lists local, generated, and build files Git should ignore. |
| `README.md` | Main project documentation shown on the GitHub repository page. |
| `SESSION_UPDATES.md` | Session-by-session changelog and development notes. |
| `mvnw`, `mvnw.cmd`, `mvnw.txt` | Maven wrapper files for running Maven commands without a separately installed Maven copy. |
| `pom.xml` | Maven build configuration, dependencies, Java version, and JavaFX run settings. |
| `run.bat` | Windows helper script for launching the JavaFX application through Maven. |
| `wrapper-index.html`, `wrapper-versions.html` | Maven wrapper reference/download helper pages kept with the project. |
| `sql/00_create_core_schema_postgresql.sql` | PostgreSQL schema reference for the core ISKOllect database tables. |
| `src/main/java/module-info.java` | Java module declaration for the JavaFX application. |
| `src/main/java/com/iskollect/Main.java` | Main JavaFX application entry point. |
| `src/main/java/com/iskollect/TestDatabaseConnection.java` | Database diagnostic runner for checking Supabase connectivity and table access. |
| `src/main/resources/config.properties` | Database connection configuration loaded at runtime. |
| `src/main/resources/com/iskollect/style.css` | Shared JavaFX CSS styling for screens and reusable UI classes. |

### Java Package File Reference

| Package | Files | Purpose |
| --- | --- | --- |
| `controller` | `BadgeHistoryController.java`, `BottleRecordsController.java`, `BottleSubmitController.java`, `DashboardController.java`, `InOutController.java`, `LoginController.java`, `ProfileController.java`, `RedeemController.java`, `RegisterController.java`, `RewardsController.java`, `TransactionController.java` | Handles UI events, screen initialization, form validation feedback, and service calls. |
| `dao` | `BottleRecordDAO.java`, `CouponDAO.java`, `InOutLogDAO.java`, `PointsLedgerDAO.java`, `RedemptionDAO.java`, `StreakDAO.java`, `UserDAO.java` | Encapsulates SQL queries and database persistence. |
| `exception` | `AuthException.java`, `DatabaseException.java`, `DuplicateLogException.java`, `InsufficientPointsException.java`, `InvalidInputException.java`, `NavigationException.java` | Defines project-specific error types. |
| `model` | `ActivityHistory.java`, `BottleRecord.java`, `Coupon.java`, `InOutLog.java`, `LogResult.java`, `RedeemResult.java`, `Redemption.java`, `ReportResult.java`, `SubmitResult.java`, `User.java` | Represents database records, users, reports, and service results. |
| `scheduler` | `WeeklyResetScheduler.java` | Runs weekly reset maintenance. |
| `service` | `ActivityHistoryService.java`, `AuthService.java`, `BadgeService.java`, `BottleService.java`, `CouponService.java`, `InOutService.java`, `PointsService.java`, `ReportService.java`, `SecurityCheck.java`, `StreakService.java` | Contains application workflows and business rules. |
| `util` | `AlertUtil.java`, `ClockUtil.java`, `CouponGenerator.java`, `DBConnection.java`, `PasswordUtil.java`, `RedirectUtil.java`, `SceneCache.java`, `SessionManager.java`, `UserValidator.java` | Provides reusable support utilities. |

### Resource File Reference

| Resource Area | Files | Purpose |
| --- | --- | --- |
| `fxml` | `badgehistorypopup.fxml`, `bottlerecords.fxml`, `dashboard.fxml`, `login.fxml`, `profile.fxml`, `rewardsCatalog.fxml`, `signup.fxml`, `submitbottlepopup.fxml`, `transactionhistory.fxml` | JavaFX layouts for screens and popups. |
| `assets` | `1.png`, `2.png`, `3.png`, `4.png`, `5.png`, `6.png`, `7.png`, `bf780c16-c3ed-449f-b2ab-6ceda0ca0e53.png`, `chipss.png`, `emerald.png`, `img.png`, `meall.png`, `pencil.png`, `recycle.jpg`, `ssfood.png`, `supplies.png` | Images for badges, reward catalog items, branding, and UI decoration. |
| `data/profile_pics` | `user_2.png`, `user_3.png`, `user_5.png`, `user_9.png`, `user_10.png`, `user_78.png`, `user_92.png` | Saved profile picture files currently present in the repository. |

## Prerequisites

Before running the project, install:

- JDK 21 or later
- Git, if cloning from GitHub
- Internet connection for Supabase database access
- An IDE such as IntelliJ IDEA, VS Code, or NetBeans

Maven does not need to be installed separately because the project includes the Maven Wrapper.

Check Java with:

```bash
java -version
```

## Database Setup

ISKOllect uses PostgreSQL through Supabase.

The database configuration file is:

```text
src/main/resources/config.properties
```

Use this format:

```properties
db.url=jdbc:postgresql://<host>:5432/<database>
db.user=<database_username>
db.password=<database_password>
```

The database schema reference is located at:

```text
sql/00_create_core_schema_postgresql.sql
```

Expected tables include:

- `users`
- `badges`
- `bottle_records`
- `coupons`
- `inout_logs`
- `points_ledger`
- `redemptions`
- `streaks`
- `user_badges`

The current schema reference follows `ISKOllect_Schema_6-14-2026.docx` for the core table and column names. It also includes the documented foreign-key delete behavior, `display_name VARCHAR(50)`, and badge bonus values of `0`, `1`, `3`, `5`, and `10`.

Important: Do not commit real database credentials to a public GitHub repository. Use placeholder values or a private configuration file when sharing the project publicly.

## Installation

Clone the repository or download the project folder.

Open PowerShell or Command Prompt inside the project folder:

```text
ISKOllect-GARBAGE-RECYCLING-REWARD-SYSTEM
```

Install dependencies and build the project:

```bash
.\mvnw.cmd clean install
```

On macOS or Linux:

```bash
./mvnw clean install
```

## Running the Application

Run the JavaFX application with:

```bash
.\mvnw.cmd javafx:run
```

On macOS or Linux:

```bash
./mvnw javafx:run
```

The login screen should appear after the application starts.

## Running from an IDE

1. Open the repository root as a Maven project.
2. Wait for Maven dependencies to finish importing.
3. Run the main class:

```text
src/main/java/com/iskollect/Main.java
```

Main class:

```text
com.iskollect.Main
```

If the IDE shows a JavaFX runtime error, run the application through Maven:

```bash
.\mvnw.cmd javafx:run
```

## Testing the Database Connection

The project includes a database diagnostic class:

```text
src/main/java/com/iskollect/TestDatabaseConnection.java
```

Class name:

```text
com.iskollect.TestDatabaseConnection
```

Run this class from the IDE to verify that the application can connect to the database and access the required tables.

## Build and Test

Compile without running tests:

```bash
.\mvnw.cmd -q -DskipTests package
```

Run tests:

```bash
.\mvnw.cmd -q test
```

## Key Implementation Details

### CRUD Operations

The system uses DAO classes to perform database operations:

- `UserDAO` handles user records, profile updates, passwords, and session tokens.
- `BottleRecordDAO` handles bottle submission records.
- `CouponDAO` handles available coupon data.
- `RedemptionDAO` handles coupon redemption history.
- `PointsLedgerDAO` records point changes.

### Validation

Validation is handled in the controllers and service layer. The system validates:

- Required login and registration fields
- PUP webmail format
- Password length and required characters
- Bottle submission limits
- Age format
- Username length and allowed characters
- Matching password confirmation fields

### Database Connection

Database access is handled through:

```text
src/main/java/com/iskollect/util/DBConnection.java
```

The application uses JDBC prepared statements in DAO classes to reduce SQL injection risk and safely pass user input to database queries.

## Recent Bug Fixes

- Added a maximum bottle submission limit.
- Fixed transaction date filters.
- Separated display name and username updates.
- Added visible validation messages for profile and password updates.
- Improved PUP webmail validation during registration and login.
- Reduced repeated login/logout memory buildup by loading scenes fresh and stopping detached screen clocks.
- Aligned Maven compiler settings with Java 21.

## Known Limitations

- Account deletion and record deletion are not available through the user interface.
- Database credentials must be configured before running the application.
- The schema document defines `inout_logs.action` for account/session events such as `LOGIN`, `LOGOUT`, `SESSION_TIMEOUT`, and `LOCK`. The current system now records `LOGIN`, `LOGOUT`, and `SESSION_TIMEOUT`, but it also keeps the existing ingress/egress monitoring values `INGRESS` and `EGRESS` because that module is already implemented in the codebase.

## Authors

Developed by the ISKOllect project team for an Object-Oriented Programming course.
