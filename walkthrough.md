# Walkthrough - SPaRMS Campus Recruitment Platform

The **Smart Placement & Recruitment Management System (SPaRMS)** has been successfully developed, compiled, and programmatically verified. The project delivers a desktop application utilizing **Java 17**, a **3NF-normalized MySQL 8.0 database**, and **FlatLaf** for a professional, corporate presentation look-and-feel.

---

## 1. Accomplishments & Technical Overview

- **3-Tier Separation of Concerns:** Shielded presentation views (`view/`) from database operations by routing all business queries and constraints validation through services (`service/`) and prepared SQL statements in data access objects (`dao/`).
- **Programmatic Database Lifecycle:** Built self-healing setup logic in [DatabaseConnection.java](file:///c:/Projects/SPaRMs/src/database/DatabaseConnection.java) that reads the [schema.sql](file:///c:/Projects/SPaRMs/src/resources/schema.sql) resource file, creates `sparms_db` database on connection, runs table setups, and seeds departments, master skills, and a default admin.
- **Corporate Styling & Utilities:** Integrated **FlatLaf Light** look-and-feel directly at startup in [Main.java](file:///c:/Projects/SPaRMs/src/main/Main.java) to enable flat borders, customized tables, hand-cursors, and modern high-DPI sizing. Password entries are hashed securely using SHA-256 before database insertion in [PasswordHasher.java](file:///c:/Projects/SPaRMs/src/utility/PasswordHasher.java).
- **Dual-Engine Backups:** Added database backups in [DatabaseBackup.java](file:///c:/Projects/SPaRMs/src/utility/DatabaseBackup.java) which calls `mysqldump` and programmatically falls back to generating SQL insert scripts via JDBC metadata if system binaries are unavailable.

---

## 2. Directory Structure

The workspace matches the requested specifications:
- [src/model/](file:///c:/Projects/SPaRMs/src/model): Standard Java Bean/POJO entity definitions.
- [src/dao/](file:///c:/Projects/SPaRMs/src/dao): Core database queries, insertions, and transactional groupings.
- [src/service/](file:///c:/Projects/SPaRMs/src/service): Business rules validations (e.g., student CGPA eligibility check and block logic).
- [src/database/](file:///c:/Projects/SPaRMs/src/database): Connection setup and initialization.
- [src/utility/](file:///c:/Projects/SPaRMs/src/utility): Validations, security hashes, session caching, and exports.
- [src/exception/](file:///c:/Projects/SPaRMs/src/exception): Dedicated runtime exceptions.
- [src/view/](file:///c:/Projects/SPaRMs/src/view): High-fidelity customized panels, KPI scorecards, search sorting tools, and navigation sidebar.
- [src/resources/](file:///c:/Projects/SPaRMs/src/resources): SQL table declarations and lookup seeds.
- [src/main/](file:///c:/Projects/SPaRMs/src/main): Launcher main class.
- [lib/](file:///c:/Projects/SPaRMs/lib): Local jars for FlatLaf UI and MySQL Driver.

---

## 3. How to Compile & Run

We created an easy-to-use PowerShell script [build.ps1](file:///c:/Projects/SPaRMs/build.ps1) in the workspace root.

1. **Verify MySQL is Running** (Ensure service is active and listening on standard port 3306).
2. **Execute Compile & Run Command:**
   ```powershell
   ./build.ps1
   ```
   *This script cleans up old builds, copies lookup resources, compiles recursively, and boots the application.*

---

## 4. Manual Testing Walkthrough

Follow these steps to fully experience the automated recruitment process:

### Step 4.1: Placement Officer (Admin) Verification
1. Launch the application. On the login screen:
   - Select Role: **ADMIN**
   - Username: `admin`
   - Password: `admin123` *(this is hashed to match the seeded DB value)*
   - Click **Login**.
2. **Dashboard Summary:** View KPI cards (Total Students: 0, Total Companies: 0, Average Package: 0). Observe report tables dynamically load under tab layouts below the metrics.
3. **Database Backup:** Go to the "Database Backup" sidebar navigation. Click the backup button. Verify a backup file `sparms_db_backup.sql` gets created in the project folder.

### Step 4.2: Recruiter & Company Registration
1. Click **Logout** to return to the Login View.
2. Click **New Recruiter? Register here**.
3. On the Recruiter registration form, click the **`+`** button beside the "Representing Company" dropdown:
   - Name: `Google`
   - Website: `google.com`
   - Industry: `Technology`
   - Description: `Search engine and AI company`
   - Click **OK** to save the company.
4. Fill in Recruiter Details:
   - Name: `Sundar Pichai`
   - Email: `sundar@google.com`
   - Username: `google_recruiter`
   - Password: `password123`
   - Company: Ensure `Google` is selected.
   - Click **Register**.
5. Log in with the newly created recruiter profile (`google_recruiter` / `password123` with Role: **RECRUITER**).
6. **Company Profile:** View details and update the website or description.
7. **Post Placement Drive:** Fill in a new job opportunity:
   - Title: `Google STEP Internship`
   - Job Role: `Software Engineering Intern`
   - Package: `12.50` LPA
   - Minimum CGPA: `8.00`
   - Max Backlogs Allowed: `0`
   - Drive Date: `2026-07-15`
   - Job Description: `Develop core systems in Java/C++.`
   - Click **Publish Drive**.

### Step 4.3: Student Profile & Eligibility Check
1. Log out. Click **New Student? Register here**.
2. Fill out student details:
   - Name: `Alex Mercer`
   - Roll Number: `CS202601`
   - Email: `alex@sparms.edu`
   - Username: `alex_mercer`
   - Password: `password123`
   - Department: `Computer Science & Engineering`
   - CGPA: `8.80` *(meets Google's 8.00 min CGPA)*
   - Class 10/12: `92.5` / `94.0`
   - Active Backlogs: `0`
   - Click **Register**.
3. Log in as Student (`alex_mercer` / `password123` with Role: **STUDENT**).
4. **Technical Skills:** Check `Java`, `SQL / MySQL`, `Data Structures & Algorithms` and click **Save Skills**.
5. **Placement Drives:** Select `Placement Drives` tab. Review listed drives.
   - Notice the eligibility status displays **Eligible**.
   - Click **Apply to Selected Drive**. A success message will appear.
   - Go to `Applied Drives` to see the application status updated to **APPLIED**.

### Step 4.4: Shortlisting & Interview Round Scheduling
1. Log out. Log in as Recruiter (`google_recruiter` / `password123`).
2. Go to **Received Applications**:
   - See `Alex Mercer` listed with status **APPLIED**.
   - Select his row, click **Shortlist**. Status updates to **SHORTLISTED**.
   - Select his row, click **Schedule Interview**:
     - Round Number: `1`
     - Round Name: `Coding & DSA Interview`
     - Date/Time: `2026-07-02 14:00:00`
     - Click **OK**.
3. Go to **Interview Rounds**:
   - See the scheduled interview round.
   - Select the round, click **Mark Passed**, add feedback ("Excellent Java problem solving skills"), and click **OK**.
4. Repeat to schedule Round 2 ("System Design & HR Interview") or proceed directly to Selection.

### Step 4.5: Final Candidate Selection
1. Go to **Received Applications**:
   - Select `Alex Mercer`'s row, click **Offer Job Selection**:
     - Offered Package: `15.00` LPA
     - Offer Date: `2026-06-30`
     - Click **OK**.
2. Go to **Selected Candidates** (Selections List) to see the final recorded acceptance.

### Step 4.6: Admin Analytics Verification
1. Log out. Log in as Admin (`admin` / `admin123`).
2. Observe the updated stats on **Dashboard Summary**:
   - Total Students: `1`, Selected Candidates: `1`, Average Package: `15.00 LPA`, Placement Percentage: `100.0%`.
   - The department table and company table will dynamically reflect Google's hiring metrics and CSE placement rates!
