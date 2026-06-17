# Iskollect Session Updates

This document summarizes the changes made in the latest development session. It is written as a focused changelog for advisor review.

## 2026-06-17 7:24 PM (GMT+8) Teammate Update Merge

### Session Scope

This session reviewed the teammate update contained in `ISKOllect (6_17_2026)` and merged the relevant source changes into the current root repository while preserving the earlier repository-root cleanup and JavaFX 21 alignment.

### Source Reviewed

- `ISKOllect (6_17_2026)/DocumentationOfWhatChanged part 2.md`
- `ISKOllect (6_17_2026)/OOP_Complete_filter_fixed_streak_badge_help/OOP_Complete_Project`

The teammate project copy still used the older nested `OOP_Complete_Project` packaging, old Maven/module identifiers, and several FXML files saved with JavaFX 26 namespaces. Those packaging differences were treated as source-copy artifacts and were not reintroduced into the root project.

### Changes Merged

- Added `badgeJustEarned` tracking to `SubmitResult`.
- Updated `BottleService` so it records which badge tier was newly unlocked during a bottle submission.
- Updated `BottleSubmitController` so the popup success message distinguishes between:
  - a newly unlocked badge, and
  - the member's current badge when no promotion occurred.
- Updated `BadgeService.awardReachedBadges` so Bronze is only considered newly reached when the user moves from zero lifetime bottles to at least one bottle.
- Merged the Submit Bottles popup status-label layout update:
  - wider centered status label,
  - bold status text,
  - adjusted safety note spacing.
- Kept the bottle count field prompt text as `Bottle count (e.g., 12)` because the teammate documentation said that placeholder was intended.
- Fixed a leftover VS Code launch configuration entry so it uses the current root project name and workspace root.

### Preserved From Current Root Project

- Kept Maven artifact/name as the current repository/project name.
- Kept the Java module as `com.iskollect`.
- Kept the JavaFX Maven plugin main class as:

```text
com.iskollect/com.iskollect.Main
```

- Kept all active FXML files on JavaFX 21:

```text
xmlns="http://javafx.com/javafx/21"
```

- Did not copy the nested update folder into the active source tree.

### Verification Performed

The following commands completed successfully:

```powershell
.\mvnw.cmd -q -DskipTests package
.\mvnw.cmd -q test
```

Additional checks confirmed all active FXML files still use JavaFX 21. Searches for old nested-folder/module strings only matched historical notes in this session log, not active source/config files.

## 2026-06-17 Repository Root Cleanup and JavaFX 21 Alignment

### Session Scope

This session cleaned up references left behind after moving the project files from the old nested `OOP_Complete_Project` folder into the repository root. It also restored the FXML JavaFX namespace version back to JavaFX 21 to match the Maven dependencies and project target.

### Changes Made

- Updated all remaining FXML files that used `xmlns="http://javafx.com/javafx/26"` back to `xmlns="http://javafx.com/javafx/21"`.
- Updated Maven project identity from the old `OOP_Complete_Project` name to the current repository/project name.
- Renamed the Java module from `com.iskollect.oop_complete_project` to `com.iskollect`.
- Updated the JavaFX Maven plugin main class from:

```text
com.iskollect.oop_complete_project/com.iskollect.Main
```

to:

```text
com.iskollect/com.iskollect.Main
```

- Fixed VS Code launch and task configuration so commands run from `${workspaceFolder}` instead of the old nested path.
- Fixed IntelliJ project metadata so it points to the root `pom.xml`, root `src/main/java`, root `src/main/resources`, and Java 21.
- Renamed the IntelliJ module file from the old `OOP_complete_updated.iml` naming to `iskollect-garbage-recycling-reward-system.iml`.
- Updated README setup instructions to tell users to open the repository root as the Maven project.
- Updated packaging notes to reflect that the current repository now contains the project directly at the root.

### Verification Performed

The following commands completed successfully:

```powershell
.\mvnw.cmd -q -DskipTests package
.\mvnw.cmd -q test
```

Additional searches confirmed there were no remaining active project/config references to:

- `OOP_Complete_Project`
- `OOP_complete_updated`
- `javafx/26`
- `com.iskollect.oop_complete_project`
- `liberica-full-26`

## 2026-06-17 Bug Fixes, Badge Logic, and Dashboard Refresh

### Session Scope

This session fixed several user-facing issues around profile pictures, badge history, member dates, reward badge display, bottle record grouping, and dashboard refresh behavior. It also included UI wording and alignment cleanup.

### Bug Fixes

- Fixed profile pictures not showing consistently.
  - The app now falls back to re-fetching the photo path from the database if it is missing from the current session.
  - Profile image loading now checks absolute paths, paths relative to the working directory, and files inside `data/profile_pics` by filename.
  - Newly uploaded profile photos update immediately in memory.
- Fixed duplicate badge history entries.
  - Badge insertion now skips badges a member already earned.
  - The app can backfill missing earned badges from the member's full submission history using the date when the tier was first reached.
- Fixed inaccurate "Member Since" dates.
  - Added support for a real `users.created_at` value.
  - Existing installs can auto-create/backfill the column from the member's earliest known activity.
  - The Profile page now reads the real signup/member date instead of using last activity.
- Fixed the Rewards Catalog badge icon always showing Bronze.
  - The static FXML image is now driven by the member's actual current badge tier.

### Behavior Changes

- Badges now behave as lifetime achievements based on total bottles submitted, not weekly bottle count.
- Badge bonus points are now paid only once, when a member first reaches a new badge tier.
- Bottle Records filters were updated:
  - `All` and `Day` show individual submissions.
  - `Week` shows grouped daily summaries.
  - `Month` shows grouped weekly summaries.
  - `Year` shows grouped monthly summaries.
- Dashboard now refreshes session data on load so it pulls current database values instead of relying on cached login data.

### UI Improvements

- Added short explanatory subtitles to the Dashboard summary boxes.
- Removed the non-functional `View all` button from Recent Transactions.
- Improved alignment across Dashboard, Bottle Records, and Transaction History.
- Updated Submit Bottles popup wording to ask for the clean bottle count.
- Added placeholder text to the bottle count input.
- Removed the hardcoded `September 2024` Profile page placeholder before the real member date loads.

### Files Touched

- Java:
  - `BadgeHistoryController.java`
  - `BottleRecordsController.java`
  - `DashboardController.java`
  - `ProfileController.java`
  - `RewardsController.java`
  - `UserDAO.java`
  - `BottleRecord.java`
  - `User.java`
  - `BadgeService.java`
  - `BottleService.java`
- FXML:
  - `bottlerecords.fxml`
  - `dashboard.fxml`
  - `profile.fxml`
  - `rewardsCatalog.fxml`
  - `submitbottlepopup.fxml`
  - `transactionhistory.fxml`
- SQL:
  - `sql/00_create_core_schema_postgresql.sql`

## 2026-06-16 UI Cleanup and Project Comparison

### Session Scope

This session compared the older uploaded project with the latest project state and documented UI refinements, removed features, inherited differences, packaging changes, and build verification results.

### Requested Changes Implemented

- Removed the Forgot Password feature.
  - Removed the `Forgot password?` clickable label from the login UI.
  - Removed the `goToForgotPassword()` FXML handler.
  - Removed the `TextInputDialog` password reset request flow.
  - Removed unused `TextInputDialog` and `Optional` imports.
  - Removed the README limitation about Forgot Password only collecting reset requests.
- Tightened Login screen spacing and alignment.
  - Matched the Login logo/header position to the Sign Up screen.
  - Matched the left recycle image height with the Sign Up screen.
  - Corrected subtitle text to `Garbage Recycling Rewards System`.
  - Reduced the gap between the password field and Login button.
  - Expanded the Login button to better match Sign Up.
  - Moved the footer text upward.
  - Expanded the error label area after removing Forgot Password.
- Tightened Sign Up screen spacing and alignment.
  - Corrected subtitle text to `Garbage Recycling Rewards System`.
  - Adjusted error label, button, and footer spacing.
  - Corrected footer capitalization to `Already have an account?`.

### Other Differences Found

- Build configuration:
  - Maven compiler settings now use `<release>${maven.compiler.release}</release>`.
  - The Maven wrapper is more complete because `.mvn/wrapper/maven-wrapper.jar` is included.
- Module configuration:
  - Removed unnecessary `opens com.iskollect.fxml to javafx.fxml;`.
  - Added a final newline to `module-info.java`.
- Profile picture display:
  - Added circular profile image rendering.
  - Added center-cropping behavior for uploaded profile pictures.
  - Centered display name, username, and badge level labels.
  - Cleaned profile text and prompt capitalization.
- Bottle Records screen:
  - Changed `0 of bottles` / `X of bottles` to `0 bottles` / `X bottles`.
  - Centered bottle count and badge progress labels.
  - Reworked filter controls into a centered filter bar.
  - Renamed the Day filter button label to `Today` during that cleanup.
  - Added reusable `.filter-bar` and `.filter-button` CSS classes.
  - Corrected white background values from `ffffff` to `#ffffff`.
- Rewards screen:
  - Centered the current points balance area.
  - Reduced the points font size slightly so larger totals fit.
  - Centered the status message under the points balance.
- Other FXML cleanup:
  - Minor layout/style polish was found in `dashboard.fxml`, `submitbottlepopup.fxml`, and `transactionhistory.fxml`.

### Verification Performed

The project was verified with:

```powershell
.\mvnw.cmd test
```

Result:

- Maven completed successfully.
- Java source and FXML resources compiled/copied without build errors.
- No test source files were present at that point, so no unit tests were executed.

### Meaningful Changed Files

- `pom.xml`
- `README.md`
- `src/main/java/com/iskollect/controller/BottleRecordsController.java`
- `src/main/java/com/iskollect/controller/LoginController.java`
- `src/main/java/com/iskollect/controller/ProfileController.java`
- `src/main/java/module-info.java`
- `src/main/resources/com/iskollect/fxml/bottlerecords.fxml`
- `src/main/resources/com/iskollect/fxml/dashboard.fxml`
- `src/main/resources/com/iskollect/fxml/login.fxml`
- `src/main/resources/com/iskollect/fxml/profile.fxml`
- `src/main/resources/com/iskollect/fxml/rewardsCatalog.fxml`
- `src/main/resources/com/iskollect/fxml/signup.fxml`
- `src/main/resources/com/iskollect/fxml/submitbottlepopup.fxml`
- `src/main/resources/com/iskollect/fxml/transactionhistory.fxml`
- `src/main/resources/com/iskollect/style.css`

## 2026-06-10 Supabase Alignment and Documentation Refresh

### Session Scope

This session aligned the backend code and reference documentation with the live Supabase PostgreSQL database. Supabase is now treated as the source of truth for table and column names.

### Database and Schema Findings

- Confirmed that the live Supabase database is reachable through JDBC.
- Confirmed all expected tables are individually reachable:
  - `badges`
  - `bottle_records`
  - `coupons`
  - `inout_logs`
  - `points_ledger`
  - `redemptions`
  - `streaks`
  - `user_badges`
  - `users`
- Confirmed `coupons` only has:
  - `coupon_id`
  - `coupon_name`
  - `points_required`
- Removed code assumptions for non-existent `coupons.description` and `coupons.coupon_type`.
- Confirmed `users` uses `password_hash`, not `password`.
- Confirmed `users` does not currently contain `name`, `course`, or `year_level`.
- Confirmed `inout_logs` uses:
  - `log_id`
  - `user_id`
  - `action`
  - `performed_at`
  - `ip_address`
  - `notes`
- Removed SQL assumptions around legacy in/out columns such as `event_type`, `entry_method`, `timestamp`, `staff_note`, and `status`.

### Java Files Updated

- `CouponDAO.java`
  - Now inserts and maps only `coupon_name` and `points_required`.
  - No longer reads or writes `description` or `coupon_type`.
- `Coupon.java`
  - Removed coupon description and coupon type fields.
- `UserDAO.java`
  - Registration now writes to `users.password_hash`.
  - User mapping reads `password_hash`.
  - Profile update now updates `username` only, because Supabase does not contain `name`, `course`, or `year_level`.
- `InOutLogDAO.java`
  - SQL now targets `action`, `performed_at`, and `notes`.
  - Date filtering now uses `performed_at::date`.
  - Duplicate checks now compare `action`.
- `InOutLog.java`
  - Updated to represent the Supabase-backed fields while keeping compatibility accessors for existing service code.
- `RedemptionDAO.java`
  - Redemption status values now use lowercase `pending` and `claimed`.
- `ReportService.java`
  - Removed references to missing `users.name`.
  - Redemption report filters now use lowercase status values.
- `WeeklyResetScheduler.java`
  - Removed dependency on missing `system_config`.
  - Last reset date is currently kept in memory during the application session.
- `TestDatabaseConnection.java`
  - Expanded from a basic connection test into a read-only Supabase diagnostics runner.
  - It checks each table, prints column metadata, queries sample rows, and masks credentials in the printed JDBC URL.

### SQL Reference Files Updated

- `sql/00_create_core_schema_postgresql.sql`
- `sql/01_create_inout_logs.sql`

These files are kept as Supabase-aligned references. They are not the source of truth if they ever disagree with the live Supabase database.

### Documentation Updated

- `ISKOLLECT - System Architecture.docx`
  - Updated from the older MySQL/Student/Reward/Transaction terminology to the current JavaFX + Java services/DAOs + JDBC + Supabase PostgreSQL architecture.
  - Added the current Supabase tables, DAO/service mappings, diagnostics runner, known gaps, and revised workflows.
  - A backup was saved as `ISKOLLECT - System Architecture.backup-20260610.docx`.
- `README.md`
  - Updated to reflect the current source tree, modules, Supabase schema notes, diagnostics runner, and known integration notes.
- `SESSION_UPDATES.md`
  - This section was added to document the new Supabase alignment work.

### Current Live Data Observed

- `coupons` contains 4 rows:
  - School Supplies, 10 points
  - Snack Voucher V1, 30 points
  - Snack Voucher V2, 50 points
  - Lunch Voucher, 100 points
- `badges` contains 5 rows:
  - Bronze, 5 points
  - Silver, 10 points
  - Emerald, 20 points
  - Gold, 35 points
  - Constellation, 50 points
- Other checked tables were reachable but empty at the time of diagnostics.

### Verification Performed

The project was compiled and tested successfully after the changes:

```bash
mvn test
```

### Remaining Work

- Replace `UserValidator` stub with real `UserDAO` validation for in/out logging.
- Review the mismatch between badge bonus logic in Java and the larger bonus values currently stored in Supabase.
- Decide whether profile fields such as course and year level should be added to Supabase or removed from the UI.
- Add focused DAO/service integration tests against a test database.
- Keep `resources/config.properties` out of public sharing because it contains database credentials.

## Session Scope

The session implemented the PostgreSQL-oriented backend and controller scaffolding for the Iskollect bottle-based recycling rewards system. The work followed the provided SAD instructions while adapting the database layer to PostgreSQL instead of MySQL.

## Major Decisions

- The project was changed to PostgreSQL-specific JDBC configuration.
- Maven was configured as the build system for Java 17, JavaFX, and the PostgreSQL JDBC driver.
- Existing ingress and egress logic was preserved, but its DAO date queries were adjusted for PostgreSQL.
- JavaFX controllers were kept thin: they call services and update UI fields only.
- Registration and authentication were not fully implemented because they are identified as a separate module.

## Files Added

### Models

- `Student.java`
- `Transaction.java`
- `Reward.java`
- `RedeemedReward.java`
- `SubmitResult.java`
- `RedeemResult.java`
- `TransactionHistory.java`
- `ReportResult.java`

These classes represent the main database-backed entities and immutable result objects used by services and controllers.

### DAOs

- `StudentDAO.java`
- `TransactionDAO.java`
- `RewardDAO.java`
- `RedeemedRewardDAO.java`

The DAO classes use `PreparedStatement` and `DBConnection.getInstance().getConnection()`. SQL was written for PostgreSQL compatibility.

### Services

- `PointsService.java`
- `StreakService.java`
- `BadgeService.java`
- `BottleService.java`
- `RewardService.java`
- `TransactionService.java`
- `ReportService.java`

These services implement point calculation, bottle submission, badge and streak handling, redemption, transaction history, and reporting workflows.

### Utilities

- `SessionManager.java`
- `CouponGenerator.java`

`SessionManager` stores the current student in memory. `CouponGenerator` creates 12-character uppercase coupon codes from UUID values.

### Scheduler

- `WeeklyResetScheduler.java`

The scheduler reads and writes `system_config.last_weekly_reset`, resets weekly student stats, and uses PostgreSQL `ON CONFLICT` for the configuration upsert.

### Controllers

- `DashboardController.java`
- `BottleSubmitController.java`
- `RewardsController.java`
- `RedeemController.java`
- `TransactionController.java`
- `ProfileController.java`
- `InOutController.java`

The controllers are JavaFX-facing classes. They contain no SQL and delegate business workflows to the service or DAO layer.

### Exceptions

- `InsufficientPointsException.java`
- `AuthException.java`

`AuthException` is intentionally a stub for the future registration and authentication module.

### Configuration and Build Files

- `pom.xml`
- `.vscode/settings.json`
- `resources/config.properties`
- `sql/00_create_core_schema_postgresql.sql`

The Maven file now includes JavaFX and PostgreSQL dependencies. VS Code settings were added so the Java language server imports Maven dependencies automatically.

## Files Updated

### `DBConnection.java`

Updated from MySQL driver loading to PostgreSQL driver loading:

```java
Class.forName("org.postgresql.Driver");
```

The expected JDBC URL is now:

```text
jdbc:postgresql://localhost:5432/iskollect_db
```

### `InOutLogDAO.java`

Updated date filtering queries to PostgreSQL-compatible syntax:

```sql
timestamp::date
```

The table documentation was also adjusted away from MySQL-specific types such as `AUTO_INCREMENT` and `DATETIME`.

### `InOutServiceTest.java` (Removed)

This was an earlier empty test placeholder. It has since been deleted along with its test folder because it had no active test coverage or use.

Earlier in the project, its package declaration had been fixed from:

```java
package test.com.iskollect;
```

to:

```java
package com.iskollect;
```

This matches the test source root and folder path.

## PostgreSQL Schema

The new PostgreSQL schema file is:

```text
sql/00_create_core_schema_postgresql.sql
```

It creates:

- `students`
- `transactions`
- `rewards`
- `redeemed_rewards`
- `inout_logs`
- `system_config`

It also seeds the reward catalog:

- Supplies Coupon
- Snack V1 Coupon
- Snack V2 Coupon
- Lunch Coupon

## Verification Performed

The following commands were run successfully:

```bash
mvn -q -DskipTests compile
mvn -q test
```

A search was also performed for common MySQL leftovers such as:

- `jdbc:mysql`
- `com.mysql`
- `AUTO_INCREMENT`
- `DATETIME`
- `TINYINT`

No remaining matches were found in the checked source, SQL, resource, or Maven files.

## Remaining Work

- Build or connect the JavaFX FXML views.
- Replace `StudentValidator` with real student lookup logic after the registration module is available.
- Implement the full authentication and registration module separately.
- Confirm local PostgreSQL credentials in `resources/config.properties`.
- Run the PostgreSQL schema against the actual development database.
- Add broader unit and integration tests for DAO and service behavior.

## Notes

The session produced a compiling backend-oriented implementation. The database direction is now PostgreSQL-specific, and the Maven build verifies that the source compiles with the declared JavaFX and PostgreSQL dependencies. The main intentional gap is authentication and registration, which remains separate by project instruction.
