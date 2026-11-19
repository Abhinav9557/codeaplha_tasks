/**
 * Student.java
 * Model class representing an individual Student.
 * Demonstrates Object-Oriented Programming (OOP) principles:
 * - Encapsulation: Private member variables with public getters/setters and constructor.
 */
public class Student {
    // Encapsulated private attributes
    private int id;
    private String name;
    private double marks;
    private String grade;

    /**
     * Parameterized constructor to initialize a Student object.
     *
     * @param id    Unique ID / Roll Number of the student
     * @param name  Full name of the student
     * @param marks Marks obtained (0 to 100)
     * @param grade Letter grade assigned to the student
     */
    public Student(int id, String name, double marks, String grade) {
        this.id = id;
        this.name = name;
        this.marks = marks;
        this.grade = grade;
    }

    // Getters and Setters (Encapsulation)

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMarks() {
        return marks;
    }

    public void setMarks(double marks) {
        this.marks = marks;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    /**
     * Returns a string representation of the student in tabular row format.
     */
    @Override
    public String toString() {
        return String.format("| %-6d | %-22s | %-8.2f | %-6s |", id, name, marks, grade);
    }

    /**
     * Converts the Student object into a comma-delimited string for permanent file persistence.
     * Record Format: id,name,marks,grade
     *
     * @return Formatted CSV record string
     */
    public String toFileString() {
        return String.format(java.util.Locale.US, "%d,%s,%.2f,%s", id, name, marks, grade);
    }

    /**
     * Creates a Student instance by parsing a comma-delimited string record from file.
     *
     * @param line CSV line from students.txt (e.g., "101,Alice Johnson,94.50,A+")
     * @return A newly constructed Student object
     * @throws IllegalArgumentException If line is malformed, has missing fields, or empty name
     * @throws NumberFormatException    If ID or marks cannot be parsed into numerical values
     */
    public static Student fromFileString(String line) throws IllegalArgumentException, NumberFormatException {
        if (line == null || line.trim().isEmpty()) {
            throw new IllegalArgumentException("Record line is empty or null.");
        }
        String[] parts = line.split(",", -1);
        if (parts.length < 3) {
            throw new IllegalArgumentException("Invalid record format. Expected at least 3 fields (id,name,marks).");
        }
        int id = Integer.parseInt(parts[0].trim());
        String name = parts[1].trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be empty in record.");
        }
        double marks = Double.parseDouble(parts[2].trim());
        if (marks < 0.0 || marks > 100.0) {
            throw new IllegalArgumentException("Marks must be between 0.00 and 100.00. Found: " + marks);
        }
        String grade;
        if (parts.length >= 4 && !parts[3].trim().isEmpty()) {
            grade = parts[3].trim();
        } else {
            grade = GradeCalculator.calculateGrade(marks);
        }
        return new Student(id, name, marks, grade);
    }
}
