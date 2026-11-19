import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Main.java
 * Console-based Entry Point for the Student Grade Tracker Application.
 *
 * Implements a robust, menu-driven interface with comprehensive input
 * validation, file handling (permanent storage in students.txt), duplicate ID
 * prevention, and complete performance analytics.
 */
public class Main {

    // Persistent storage file name
    private static final String FILE_NAME = "students.txt";

    // In-memory data store using ArrayList
    private static final ArrayList<Student> studentList = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);
    private static int nextStudentId = 101; // Auto-incrementing student ID tracker

    public static void main(String[] args) {
        boolean running = true;

        printWelcomeBanner();

        // Load previously saved student records on application startup
        loadStudentsFromFile();

        while (running) {
            displayMenu();
            int choice = readIntegerInput("Enter your choice (1-10): ", 1, 10);

            switch (choice) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    viewAllStudents();
                    break;
                case 3:
                    calculateAverageMarks();
                    break;
                case 4:
                    findHighestMarks();
                    break;
                case 5:
                    findLowestMarks();
                    break;
                case 6:
                    displaySummaryReport();
                    break;
                case 7:
                    searchStudentById();
                    break;
                case 8:
                    updateStudent();
                    break;
                case 9:
                    deleteStudent();
                    break;
                case 10:
                    running = false;
                    saveStudentsToFile();
                    printExitMessage();
                    break;
                default:
                    System.out.println("[!] Invalid choice. Please select an option between 1 and 10.");
            }

            if (running) {
                System.out.print("\nPress Enter to return to the main menu...");
                scanner.nextLine();
            }
        }

        scanner.close();
    }

    /**
     * Displays application welcome banner.
     */
    private static void printWelcomeBanner() {
        System.out.println("=======================================================");
        System.out.println("           STUDENT GRADE TRACKER SYSTEM               ");
        System.out.println("       Object-Oriented Java Console Application        ");
        System.out.println("=======================================================");
    }

    /**
     * Prints the primary 10 menu options.
     */
    private static void displayMenu() {
        System.out.println("\n-------------------- MAIN MENU --------------------");
        System.out.println(" 1. Add Student");
        System.out.println(" 2. View All Students");
        System.out.println(" 3. Calculate Average");
        System.out.println(" 4. Find Highest Marks");
        System.out.println(" 5. Find Lowest Marks");
        System.out.println(" 6. Display Summary Report");
        System.out.println(" 7. Search Student by ID");
        System.out.println(" 8. Update Student Record");
        System.out.println(" 9. Delete Student Record");
        System.out.println(" 10. Exit");
        System.out.println("---------------------------------------------------");
    }

    // -------------------------------------------------------------
    //                     FILE HANDLING METHODS
    // -------------------------------------------------------------

    /**
     * Loads student records from students.txt into memory on startup.
     * Uses Exception Handling to handle missing file, I/O errors,
     * corrupted line entries, and duplicate student IDs.
     */
    private static void loadStudentsFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.printf("[INFO] '%s' not found. A new file will be created when records are saved.\n", FILE_NAME);
            return;
        }

        int loadedCount = 0;
        int skippedCount = 0;
        int lineNumber = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue; // Skip blank lines or comment lines
                }

                try {
                    Student student = Student.fromFileString(line);

                    // Prevent duplicate student IDs from being loaded
                    if (GradeCalculator.isIdDuplicate(studentList, student.getId())) {
                        System.out.printf("[WARNING] Duplicate student ID %d detected at line %d for '%s'. Skipping duplicate record.\n",
                                student.getId(), lineNumber, student.getName());
                        skippedCount++;
                        continue;
                    }

                    studentList.add(student);

                    // Keep nextStudentId ahead of all loaded IDs
                    if (student.getId() >= nextStudentId) {
                        nextStudentId = student.getId() + 1;
                    }
                    loadedCount++;

                } catch (IllegalArgumentException e) {
                    System.out.printf("[WARNING] Corrupted or invalid data on line %d in '%s': %s (Skipping line)\n",
                            lineNumber, FILE_NAME, e.getMessage());
                    skippedCount++;
                }
            }

            System.out.printf("[INFO] Successfully loaded %d student record(s) from '%s'", loadedCount, FILE_NAME);
            if (skippedCount > 0) {
                System.out.printf(" (%d record(s) skipped due to errors/duplicates)", skippedCount);
            }
            System.out.println(".");

        } catch (FileNotFoundException e) {
            System.out.printf("[!] Warning: File '%s' was not found: %s\n", FILE_NAME, e.getMessage());
        } catch (IOException e) {
            System.out.printf("[!] Error: I/O problem encountered while reading '%s': %s\n", FILE_NAME, e.getMessage());
        } catch (SecurityException e) {
            System.out.printf("[!] Error: Access denied when reading '%s': %s\n", FILE_NAME, e.getMessage());
        } catch (Exception e) {
            System.out.printf("[!] Error: Unexpected error while loading '%s': %s\n", FILE_NAME, e.getMessage());
        }
    }

    /**
     * Saves all student records from memory permanently into students.txt.
     * Uses try-with-resources to guarantee file streams are closed safely.
     *
     * @return true if successfully saved, false if an error occurred
     */
    private static boolean saveStudentsToFile() {
        File file = new File(FILE_NAME);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Student student : studentList) {
                writer.write(student.toFileString());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.out.printf("[!] Error: Failed to write student records to '%s': %s\n", FILE_NAME, e.getMessage());
            return false;
        } catch (SecurityException e) {
            System.out.printf("[!] Error: Permission denied when writing to '%s': %s\n", FILE_NAME, e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.printf("[!] Error: Unexpected error while saving to '%s': %s\n", FILE_NAME, e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------------
    //                     MENU ACTION METHODS
    // -------------------------------------------------------------

    /**
     * Handles adding a new student with full input validation,
     * duplicate ID prevention, and permanent auto-saving to students.txt.
     */
    private static void addStudent() {
        System.out.println("\n--- [1] Add New Student ---");

        int autoId = getNextAvailableId();
        int id = readStudentIdInput("Enter Student ID (or press Enter to auto-assign [" + autoId + "]): ", autoId);

        String name = readNonEmptyString("Enter Student Name: ");
        double marks = readMarksInput("Enter Marks (0 - 100): ");

        // Determine letter grade
        String grade = GradeCalculator.calculateGrade(marks);

        // Create new Student instance (OOP Encapsulation)
        Student newStudent = new Student(id, name, marks, grade);
        studentList.add(newStudent);

        // Advance nextStudentId if necessary
        if (id >= nextStudentId) {
            nextStudentId = id + 1;
        }

        // Persist immediately to file
        saveStudentsToFile();

        System.out.println("\n[SUCCESS] Student added successfully and saved to 'students.txt'!");
        System.out.printf("    ID: %d | Name: %s | Marks: %.2f | Grade: %s\n",
                newStudent.getId(), newStudent.getName(), newStudent.getMarks(), newStudent.getGrade());
    }

    /**
     * Displays all student records in a structured table.
     */
    private static void viewAllStudents() {
        System.out.println("\n--- [2] View All Students ---");
        if (studentList.isEmpty()) {
            System.out.println("[!] No student records found. Please add students first (Option 1).");
            return;
        }

        System.out.printf("Total records: %d\n\n", studentList.size());
        GradeCalculator.printStudentTable(studentList);
    }

    /**
     * Calculates and displays the overall class average marks.
     */
    private static void calculateAverageMarks() {
        System.out.println("\n--- [3] Calculate Average Marks ---");
        if (studentList.isEmpty()) {
            System.out.println("[!] No student records available. Please add students first.");
            return;
        }

        double average = GradeCalculator.calculateAverage(studentList);
        String averageGrade = GradeCalculator.calculateGrade(average);

        System.out.printf("Total Students : %d\n", studentList.size());
        System.out.printf("Class Average  : %.2f / 100\n", average);
        System.out.printf("Overall Grade  : %s\n", averageGrade);
    }

    /**
     * Displays the highest marks and the student(s) who scored them.
     */
    private static void findHighestMarks() {
        System.out.println("\n--- [4] Find Highest Marks ---");
        if (studentList.isEmpty()) {
            System.out.println("[!] No student records available. Please add students first.");
            return;
        }

        double highestMarks = GradeCalculator.getHighestMarks(studentList);
        ArrayList<Student> topScorers = GradeCalculator.getHighestScorers(studentList);

        System.out.printf("Highest Marks: %.2f / 100\n", highestMarks);
        System.out.println("Scored by:");
        for (Student s : topScorers) {
            System.out.printf("  - [ID: %d] %-20s (Marks: %.2f, Grade: %s)\n",
                    s.getId(), s.getName(), s.getMarks(), s.getGrade());
        }
    }

    /**
     * Displays the lowest marks and the student(s) who scored them.
     */
    private static void findLowestMarks() {
        System.out.println("\n--- [5] Find Lowest Marks ---");
        if (studentList.isEmpty()) {
            System.out.println("[!] No student records available. Please add students first.");
            return;
        }

        double lowestMarks = GradeCalculator.getLowestMarks(studentList);
        ArrayList<Student> lowScorers = GradeCalculator.getLowestScorers(studentList);

        System.out.printf("Lowest Marks: %.2f / 100\n", lowestMarks);
        System.out.println("Scored by:");
        for (Student s : lowScorers) {
            System.out.printf("  - [ID: %d] %-20s (Marks: %.2f, Grade: %s)\n",
                    s.getId(), s.getName(), s.getMarks(), s.getGrade());
        }
    }

    /**
     * Displays a complete analytical performance summary.
     */
    private static void displaySummaryReport() {
        System.out.println("\n--- [6] Display Summary Report ---");
        GradeCalculator.displaySummaryReport(studentList);
    }

    /**
     * Searches for a student by their unique ID and prints their details.
     */
    private static void searchStudentById() {
        System.out.println("\n--- [7] Search Student by ID ---");
        if (studentList.isEmpty()) {
            System.out.println("[!] No student records available. Please add students first.");
            return;
        }

        int id = readIntegerInput("Enter Student ID to search: ", 1, Integer.MAX_VALUE);
        Student student = GradeCalculator.findStudentById(studentList, id);

        if (student != null) {
            System.out.println("\n[RESULT] Student Record Found:");
            GradeCalculator.printDivider();
            System.out.printf("| %-6s | %-22s | %-8s | %-6s |\n", "ID", "Name", "Marks", "Grade");
            GradeCalculator.printDivider();
            System.out.println(student);
            GradeCalculator.printDivider();
        } else {
            System.out.printf("[!] Error: No student found with ID %d.\n", id);
        }
    }

    /**
     * Updates an existing student's name, marks, or both,
     * automatically recalculating the grade and persisting to students.txt.
     */
    private static void updateStudent() {
        System.out.println("\n--- [8] Update Student Record ---");
        if (studentList.isEmpty()) {
            System.out.println("[!] No student records available to update. Please add students first.");
            return;
        }

        int id = readIntegerInput("Enter Student ID to update: ", 1, Integer.MAX_VALUE);
        Student student = GradeCalculator.findStudentById(studentList, id);

        if (student == null) {
            System.out.printf("[!] Error: No student found with ID %d.\n", id);
            return;
        }

        System.out.println("\nCurrent Details:");
        System.out.printf("  ID: %d | Name: %s | Marks: %.2f | Grade: %s\n",
                student.getId(), student.getName(), student.getMarks(), student.getGrade());

        System.out.println("\nWhat would you like to update?");
        System.out.println(" 1. Update Name");
        System.out.println(" 2. Update Marks (Recalculates Grade)");
        System.out.println(" 3. Update Both Name and Marks");
        System.out.println(" 4. Cancel");

        int choice = readIntegerInput("Enter your choice (1-4): ", 1, 4);
        boolean modified = false;

        switch (choice) {
            case 1:
                String newName = readNonEmptyString("Enter New Student Name: ");
                student.setName(newName);
                modified = true;
                break;
            case 2:
                double newMarks = readMarksInput("Enter New Marks (0 - 100): ");
                student.setMarks(newMarks);
                student.setGrade(GradeCalculator.calculateGrade(newMarks));
                modified = true;
                break;
            case 3:
                String updatedName = readNonEmptyString("Enter New Student Name: ");
                double updatedMarks = readMarksInput("Enter New Marks (0 - 100): ");
                student.setName(updatedName);
                student.setMarks(updatedMarks);
                student.setGrade(GradeCalculator.calculateGrade(updatedMarks));
                modified = true;
                break;
            case 4:
                System.out.println("[INFO] Update cancelled. No changes made.");
                return;
        }

        if (modified) {
            saveStudentsToFile();
            System.out.println("\n[SUCCESS] Student record updated successfully and saved to 'students.txt'!");
            System.out.printf("    Updated Record -> ID: %d | Name: %s | Marks: %.2f | Grade: %s\n",
                    student.getId(), student.getName(), student.getMarks(), student.getGrade());
        }
    }

    /**
     * Deletes a student record by ID after user confirmation,
     * and updates students.txt permanently.
     */
    private static void deleteStudent() {
        System.out.println("\n--- [9] Delete Student Record ---");
        if (studentList.isEmpty()) {
            System.out.println("[!] No student records available to delete.");
            return;
        }

        int id = readIntegerInput("Enter Student ID to delete: ", 1, Integer.MAX_VALUE);
        Student student = GradeCalculator.findStudentById(studentList, id);

        if (student == null) {
            System.out.printf("[!] Error: No student found with ID %d.\n", id);
            return;
        }

        System.out.printf("Found: %s (ID: %d | Marks: %.2f | Grade: %s)\n",
                student.getName(), student.getId(), student.getMarks(), student.getGrade());
        boolean confirmed = readYesNoInput("Are you sure you want to delete this record? (y/n): ");

        if (confirmed) {
            studentList.remove(student);
            saveStudentsToFile();
            System.out.printf("\n[SUCCESS] Student ID %d ('%s') removed successfully and changes saved to '%s'!\n",
                    id, student.getName(), FILE_NAME);
        } else {
            System.out.println("[INFO] Deletion cancelled. Record was kept.");
        }
    }

    /**
     * Prints exit and closing message.
     */
    private static void printExitMessage() {
        System.out.println("\n=======================================================");
        System.out.println(" All student records have been preserved in students.txt.");
        System.out.println(" Thank you for using the Student Grade Tracker System!");
        System.out.println(" Have a great day. Goodbye!");
        System.out.println("=======================================================");
    }

    // -------------------------------------------------------------
    //                     INPUT VALIDATION HELPERS
    // -------------------------------------------------------------

    /**
     * Prompts for and validates an integer within a specified range [min, max].
     * Handles non-integer inputs gracefully without throwing uncaught exceptions.
     */
    private static int readIntegerInput(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.printf("[!] Error: Value must be between %d and %d. Please try again.\n", min, max);
                }
            } catch (NumberFormatException e) {
                System.out.println("[!] Error: Invalid input. Please enter a valid whole number.");
            }
        }
    }

    /**
     * Prompts for a Student ID and prevents duplicate student IDs.
     * Allows pressing Enter to auto-assign the next unique ID.
     *
     * @param prompt User prompt text
     * @param autoId Suggested auto-increment ID
     * @return Validated, unique student ID
     */
    private static int readStudentIdInput(String prompt, int autoId) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.printf("No ID entered. Auto-assigned Student ID: %d\n", autoId);
                return autoId;
            }

            try {
                int id = Integer.parseInt(input);
                if (id <= 0) {
                    System.out.println("[!] Error: Student ID must be a positive number greater than 0.");
                    continue;
                }
                if (GradeCalculator.isIdDuplicate(studentList, id)) {
                    System.out.printf("[!] Error: Student with ID %d already exists! Duplicate student IDs are not allowed. Please enter a different ID.\n", id);
                    continue;
                }
                return id;
            } catch (NumberFormatException e) {
                System.out.println("[!] Error: Invalid ID format. Please enter a valid whole number (or press Enter to auto-assign).");
            }
        }
    }

    /**
     * Finds the next unique available integer ID not present in studentList.
     *
     * @return Next available unique ID >= nextStudentId
     */
    private static int getNextAvailableId() {
        int candidate = nextStudentId;
        while (GradeCalculator.isIdDuplicate(studentList, candidate)) {
            candidate++;
        }
        return candidate;
    }

    /**
     * Prompts for and validates double marks within [0.0, 100.0].
     * Handles non-numeric inputs and out-of-range values gracefully.
     */
    private static double readMarksInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                double marks = Double.parseDouble(input);
                if (marks >= 0.0 && marks <= 100.0) {
                    return marks;
                } else {
                    System.out.println("[!] Error: Marks must be between 0.00 and 100.00. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("[!] Error: Invalid numerical value. Please enter numeric marks (e.g., 85.5).");
            }
        }
    }

    /**
     * Prompts for a non-blank string (such as student name).
     */
    private static String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            } else {
                System.out.println("[!] Error: Student name cannot be empty. Please enter a valid name.");
            }
        }
    }

    /**
     * Prompts for a yes/no confirmation response.
     *
     * @param prompt Confirmation prompt text
     * @return true if user typed 'y' or 'yes', false if 'n' or 'no'
     */
    private static boolean readYesNoInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            } else {
                System.out.println("[!] Error: Invalid response. Please enter 'y' for yes or 'n' for no.");
            }
        }
    }
}
