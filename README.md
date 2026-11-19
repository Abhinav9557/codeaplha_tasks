# Student Grade Tracker (Java Console Application with File Handling)

A complete, beginner-friendly, and robust **Java Console Application** designed to manage, calculate, analyze, and permanently persist student marks and grades. Built adhering to core **Object-Oriented Programming (OOP)** principles and standard Java SDK components (`java.io`, `java.util.ArrayList`, `java.util.Scanner`).

---

## 📋 Table of Contents
1. [Project Overview](#-project-overview)
2. [Key Features & Upgrades](#-key-features--upgrades)
3. [File Handling & Persistence Architecture](#-file-handling--persistence-architecture)
4. [Duplicate Student ID Prevention](#-duplicate-student-id-prevention)
5. [Object-Oriented Programming (OOP) Architecture](#-object-oriented-programming-oop-architecture)
6. [Grading Scale](#-grading-scale)
7. [Project Structure](#-project-structure)
8. [Prerequisites & How to Run](#-prerequisites--how-to-run)
9. [10 Menu Options & User Guide](#-10-menu-options--user-guide)
10. [Sample Execution & Console Output](#-sample-execution--console-output)
11. [Exception Handling & Robust Validation](#-exception-handling--robust-validation)

---

## 🌟 Project Overview
The **Student Grade Tracker** allows educators, instructors, and academic administrators to record, evaluate, and track student academic performance. 

In this upgraded version, all student records are **permanently saved to a flat file (`students.txt`)** and **automatically loaded on application startup**. Changes made through adding, updating, or deleting students are immediately synchronized to disk, preventing data loss across sessions.

---

## ✨ Key Features & Upgrades

- **Permanent File Handling (`students.txt`)**: 
  - Automatically loads existing records into memory when the program starts.
  - Automatically persists new, updated, and deleted records to disk.
  - Safely saves all records upon exiting.
- **Duplicate Student ID Prevention**:
  - Enforces uniqueness for every Student ID.
  - Prevents users from manually entering an existing ID.
  - Auto-increments and auto-assigns guaranteed unique IDs if user prefers automatic numbering.
  - Filters out duplicate records during file loading and logs descriptive warnings.
- **Complete 10-Option Menu**:
  - Preserves all 6 analytical and reporting features.
  - Adds dedicated options to **Search Student by ID**, **Update Student Record**, and **Delete Student Record**.
  - Provides a safe **Exit** option that confirms file synchronization.
- **Automated Grade Recalculation**:
  - Computes standard letter grades (`A+`, `A`, `B`, `C`, `D`, `F`) based on marks.
  - Automatically recalculates grades whenever marks are updated.
- **Class Analytics & Summary Reports**:
  - Computes class average marks.
  - Identifies top scorer(s) and lowest scorer(s), gracefully handling ties.
  - Generates grade distribution tables and overall passing percentages.
- **Comprehensive Exception Handling**:
  - Uses `try-with-resources` to prevent resource leaks (`BufferedReader`, `BufferedWriter`).
  - Catches and recovers from `FileNotFoundException`, `IOException`, `SecurityException`, `NumberFormatException`, and `IllegalArgumentException`.
  - Tolerates corrupted lines or invalid values in `students.txt` without crashing.

---

## 💾 File Handling & Persistence Architecture

### File Specification
- **File Name**: `students.txt`
- **Location**: Root directory of the application
- **Format**: Comma-Separated Values (CSV)
  ```csv
  id,name,marks,grade
  ```

### Example `students.txt`
```text
101,Alice Johnson,94.50,A+
102,Bob Smith,82.00,A
103,Charlie Brown,73.50,B
104,David Miller,45.00,F
```

### Persistence Lifecycle
1. **Startup (Load)**: When `Main` initializes, `loadStudentsFromFile()` reads `students.txt` line by line, parses valid records via `Student.fromFileString(line)`, skips malformed or duplicate lines with descriptive warnings, and populates `studentList`.
2. **Runtime Sync**: Whenever a student is added (Option 1), edited (Option 8), or deleted (Option 9), `saveStudentsToFile()` immediately writes the entire list to `students.txt`.
3. **Shutdown (Exit)**: When Option 10 (Exit) is selected, `saveStudentsToFile()` performs a final write pass to guarantee file integrity.

---

## 🛡 Duplicate Student ID Prevention

Student ID is the unique primary identifier in the system. Duplicate IDs are strictly prevented at multiple levels:

1. **Manual Entry Validation**: When entering a Student ID during Option 1, the program checks `GradeCalculator.isIdDuplicate(studentList, id)`. If an ID is already taken:
   ```text
   [!] Error: Student with ID 101 already exists! Duplicate student IDs are not allowed. Please enter a different ID.
   ```
2. **Auto-Assignment Option**: Pressing `Enter` at the ID prompt automatically assigns the next available unique ID (e.g. `105`), calculated by finding the lowest available ID not present in the system.
3. **File Load Verification**: If `students.txt` is externally edited or corrupted with a duplicate ID, `loadStudentsFromFile()` detects it, logs a warning with the exact line number, and skips the duplicate record.

---

## 🏛 Object-Oriented Programming (OOP) Architecture

The project adheres to clean software design and OOP principles:

### 1. Model & Encapsulation (`Student.java`)
- Attributes (`id`, `name`, `marks`, `grade`) are declared `private`.
- Accessed and modified strictly through public getters and setters.
- Implements:
  - `toFileString()`: Formats student attributes into CSV records for file storage.
  - `fromFileString(String line)`: Static factory method parsing and validating CSV records from `students.txt`.
  - `toString()`: Overridden to format student data as a clean tabular row.

### 2. Service & Business Logic (`GradeCalculator.java`)
- Separates analytical logic from UI and storage.
- Static methods include:
  - `calculateGrade(double marks)`: Maps numerical marks (0–100) to letter grades.
  - `calculateAverage(ArrayList<Student> students)`: Calculates class mean score.
  - `getHighestMarks(ArrayList<Student> students)` & `getLowestMarks(ArrayList<Student> students)`.
  - `getHighestScorers(ArrayList<Student> students)` & `getLowestScorers(ArrayList<Student> students)`.
  - `findStudentById(ArrayList<Student> students, int id)`: Performs fast student ID lookups.
  - `isIdDuplicate(ArrayList<Student> students, int id)`: Checks ID uniqueness.
  - `printStudentTable(ArrayList<Student> students)` & `displaySummaryReport(ArrayList<Student> students)`.

### 3. Controller & Presentation (`Main.java`)
- Coordinates the console lifecycle and user interaction.
- Handles file I/O operations (`loadStudentsFromFile()`, `saveStudentsToFile()`).
- Houses user input validation helper methods using `java.util.Scanner`.

---

## 📊 Grading Scale

| Marks Range (%) | Letter Grade | Performance Level | Status |
| :---: | :---: | :---: | :---: |
| **90.00 – 100.00** | **A+** | Outstanding | Pass |
| **80.00 – 89.99**  | **A**  | Excellent   | Pass |
| **70.00 – 79.99**  | **B**  | Very Good   | Pass |
| **60.00 – 69.99**  | **C**  | Good        | Pass |
| **50.00 – 59.99**  | **D**  | Pass        | Pass |
| **Below 50.00**    | **F**  | Fail        | Fail |

---

## 📁 Project Structure

```text
StudentGradeTracker/
│
├── Student.java           # Model class (Encapsulation, file serialization)
├── GradeCalculator.java   # Business logic, query methods, and analytics
├── Main.java              # Console UI, menu dispatch, file handling, and validation
├── students.txt           # Permanent flat-file storage for student records
└── README.md              # Complete project documentation and guide
```

---

## 🚀 Prerequisites & How to Run

### Prerequisites
- **Java Development Kit (JDK 8 or later)** installed.
- Verify installation by opening a terminal / Command Prompt:
  ```bash
  javac -version
  java -version
  ```

> **Note for Windows Users**: If `javac` or `java` is not recognized, configure your `PATH` environment variable or point to your JDK directory:
> ```powershell
> # Example for PowerShell with JDK in user home:
> $env:PATH += ";$env:JAVA_HOME\bin"
> ```

### Compilation
Navigate to the project root directory and compile all Java source files:
```bash
javac *.java
```
This produces `Student.class`, `GradeCalculator.class`, and `Main.class`.

### Execution
Run the compiled application:
```bash
java Main
```

---

## 📖 10 Menu Options & User Guide

When launched, the program displays the 10-option main menu:

```text
-------------------- MAIN MENU --------------------
 1. Add Student
 2. View All Students
 3. Calculate Average
 4. Find Highest Marks
 5. Find Lowest Marks
 6. Display Summary Report
 7. Search Student by ID
 8. Update Student Record
 9. Delete Student Record
 10. Exit
---------------------------------------------------
Enter your choice (1-10): 
```

| Option | Feature | Description |
| :---: | :--- | :--- |
| **1** | **Add Student** | Enter a unique ID (or press Enter to auto-assign), student name, and marks (0–100). The grade is assigned instantly and saved to `students.txt`. |
| **2** | **View All Students** | Displays a formatted tabular view of all registered students (ID, Name, Marks, Grade). |
| **3** | **Calculate Average** | Calculates and prints class average score and overall letter grade. |
| **4** | **Find Highest Marks** | Identifies the top score and lists all students who achieved it. |
| **5** | **Find Lowest Marks** | Identifies the lowest score and lists all students who received it. |
| **6** | **Display Summary Report** | Generates an analytical report with enrollment count, average, highest/lowest, pass rate (%), and grade distribution. |
| **7** | **Search Student by ID** | Searches for a student by their ID and displays their complete record. |
| **8** | **Update Student Record** | Modifies a student's Name, Marks, or Both. Marks updates automatically recalculate the grade and persist changes to `students.txt`. |
| **9** | **Delete Student Record** | Removes a student record by ID after user confirmation (`y/n`) and syncs changes to `students.txt`. |
| **10** | **Exit** | Saves all current records to `students.txt` and closes the application safely. |

---

## 🖥 Sample Execution & Console Output

### 1. Application Launch & Auto-Loading
```text
=======================================================
           STUDENT GRADE TRACKER SYSTEM               
       Object-Oriented Java Console Application        
=======================================================
[INFO] Successfully loaded 4 student record(s) from 'students.txt'.
```

### 2. Option 1: Adding a Student with Duplicate ID Prevention
```text
Enter your choice (1-10): 1

--- [1] Add New Student ---
Enter Student ID (or press Enter to auto-assign [105]): 101
[!] Error: Student with ID 101 already exists! Duplicate student IDs are not allowed. Please enter a different ID.
Enter Student ID (or press Enter to auto-assign [105]): 
No ID entered. Auto-assigned Student ID: 105
Enter Student Name: Emily Watson
Enter Marks (0 - 100): 88.5

[SUCCESS] Student added successfully and saved to 'students.txt'!
    ID: 105 | Name: Emily Watson | Marks: 88.50 | Grade: A
```

### 3. Option 2: Viewing All Students (Tabular Format)
```text
Enter your choice (1-10): 2

--- [2] View All Students ---
Total records: 5

+--------+------------------------+----------+--------+
| ID     | Name                   | Marks    | Grade  |
+--------+------------------------+----------+--------+
| 101    | Alice Johnson          | 94.50    | A+     |
| 102    | Bob Smith              | 82.00    | A      |
| 103    | Charlie Brown          | 73.50    | B      |
| 104    | David Miller           | 45.00    | F      |
| 105    | Emily Watson           | 88.50    | A      |
+--------+------------------------+----------+--------+
```

### 4. Option 7: Searching Student by ID
```text
Enter your choice (1-10): 7

--- [7] Search Student by ID ---
Enter Student ID to search: 103

[RESULT] Student Record Found:
+--------+------------------------+----------+--------+
| ID     | Name                   | Marks    | Grade  |
+--------+------------------------+----------+--------+
| 103    | Charlie Brown          | 73.50    | B      |
+--------+------------------------+----------+--------+
```

### 5. Option 8: Updating Student Record
```text
Enter your choice (1-10): 8

--- [8] Update Student Record ---
Enter Student ID to update: 104

Current Details:
  ID: 104 | Name: David Miller | Marks: 45.00 | Grade: F

What would you like to update?
 1. Update Name
 2. Update Marks (Recalculates Grade)
 3. Update Both Name and Marks
 4. Cancel
Enter your choice (1-4): 2
Enter New Marks (0 - 100): 55.0

[SUCCESS] Student record updated successfully and saved to 'students.txt'!
    Updated Record -> ID: 104 | Name: David Miller | Marks: 55.00 | Grade: D
```

### 6. Option 9: Deleting a Student Record
```text
Enter your choice (1-10): 9

--- [9] Delete Student Record ---
Enter Student ID to delete: 103
Found: Charlie Brown (ID: 103 | Marks: 73.50 | Grade: B)
Are you sure you want to delete this record? (y/n): y

[SUCCESS] Student ID 103 ('Charlie Brown') removed successfully and changes saved to 'students.txt'!
```

### 7. Option 6: Comprehensive Performance Summary
```text
=======================================================
            STUDENT PERFORMANCE SUMMARY REPORT         
=======================================================
 Total Students Enrolled : 4
 Class Average Marks     : 80.00 / 100 (Grade: A)
 Highest Marks Scored    : 94.50 / 100
 Lowest Marks Scored     : 55.00 / 100
 Passing Percentage      : 100.00% (4 Passed, 0 Failed)
-------------------------------------------------------
 Top Scorer(s):
   - Alice Johnson (ID: 101) -> 94.50 Marks [Grade A+]
 Lowest Scorer(s):
   - David Miller (ID: 104) -> 55.00 Marks [Grade D]
-------------------------------------------------------
 Grade Distribution:
   A+ (90-100) : 1 student(s)
   A  (80-89)  : 2 student(s)
   B  (70-79)  : 0 student(s)
   C  (60-69)  : 0 student(s)
   D  (50-59)  : 1 student(s)
   F  (<50)    : 0 student(s)
=======================================================
```

---

## 🛡 Exception Handling & Robust Validation

The program is engineered to withstand invalid inputs and file corruption without crashing:

| Exception / Scenario | Trigger Condition | Handled Response |
| :--- | :--- | :--- |
| **Missing `students.txt`** | Program run for the first time without an existing file | Informs user gracefully (`[INFO] 'students.txt' not found`) and creates the file on first save. |
| **File I/O Error (`IOException`)** | Read or write failure due to disk or locked file | Catches exception and prints clear error diagnostic without halting the program. |
| **Permission Denied (`SecurityException`)** | Read/write permission restricted on filesystem | Caught and reported cleanly to the user. |
| **Corrupted Line in File** | Line with missing fields or non-numeric tokens | Line is skipped, exact line number and reason logged, and remaining valid records are loaded. |
| **Duplicate ID in File** | File edited to contain two identical IDs | Duplicate record skipped, warning logged with line number, unique record preserved. |
| **Duplicate ID on Input** | User enters an ID already assigned to a student | Rejects input and requests a unique ID. |
| **Non-Numeric Menu / Marks** | Entering text like `abc` or symbols for numbers | Handled via `NumberFormatException` loops without crashing. |
| **Marks Out of Bounds** | Values `< 0.0` or `> 100.0` | Rejected with range error (`Marks must be between 0.00 and 100.00`). |
| **Empty Student Name** | Entering blank spaces or empty line | Rejected with prompt to enter a non-empty name. |
| **Empty Record Action** | Selecting Options 2–9 when no records exist | Displays user-friendly message (`No student records found`). |

---

## 👨‍💻 Submission Notes
- **Language**: Java (SE 8+)
- **Storage**: Persistent flat-file storage (`students.txt`) with in-memory `ArrayList<Student>`
- **Architecture**: Object-Oriented Programming (Model-Service-Controller)
- **Dependencies**: None (Pure Java Standard SDK)
