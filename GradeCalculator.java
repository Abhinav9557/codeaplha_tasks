import java.util.ArrayList;

/**
 * GradeCalculator.java
 * Service and utility class for grade evaluations, statistics, and reports.
 * Encapsulates the core business logic of the Student Grade Tracker.
 */
public class GradeCalculator {

    /**
     * Determines the letter grade based on marks obtained.
     * Grading Scale:
     *   90 - 100 : A+ (Outstanding)
     *   80 - 89  : A  (Excellent)
     *   70 - 79  : B  (Very Good)
     *   60 - 69  : C  (Good)
     *   50 - 59  : D  (Pass)
     *   Below 50 : F  (Fail)
     *
     * @param marks Marks obtained (0 - 100)
     * @return Letter grade as a String
     */
    public static String calculateGrade(double marks) {
        if (marks >= 90.0) {
            return "A+";
        } else if (marks >= 80.0) {
            return "A";
        } else if (marks >= 70.0) {
            return "B";
        } else if (marks >= 60.0) {
            return "C";
        } else if (marks >= 50.0) {
            return "D";
        } else {
            return "F";
        }
    }

    /**
     * Calculates the class average marks.
     *
     * @param students List of students
     * @return Average marks, or 0.0 if list is empty
     */
    public static double calculateAverage(ArrayList<Student> students) {
        if (students == null || students.isEmpty()) {
            return 0.0;
        }
        double totalMarks = 0.0;
        for (Student student : students) {
            totalMarks += student.getMarks();
        }
        return totalMarks / students.size();
    }

    /**
     * Finds the maximum marks among all students.
     *
     * @param students List of students
     * @return Highest marks, or 0.0 if list is empty
     */
    public static double getHighestMarks(ArrayList<Student> students) {
        if (students == null || students.isEmpty()) {
            return 0.0;
        }
        double highest = students.get(0).getMarks();
        for (Student student : students) {
            if (student.getMarks() > highest) {
                highest = student.getMarks();
            }
        }
        return highest;
    }

    /**
     * Finds the minimum marks among all students.
     *
     * @param students List of students
     * @return Lowest marks, or 0.0 if list is empty
     */
    public static double getLowestMarks(ArrayList<Student> students) {
        if (students == null || students.isEmpty()) {
            return 0.0;
        }
        double lowest = students.get(0).getMarks();
        for (Student student : students) {
            if (student.getMarks() < lowest) {
                lowest = student.getMarks();
            }
        }
        return lowest;
    }

    /**
     * Retrieves all students who scored the highest marks.
     *
     * @param students List of students
     * @return List of highest scoring students
     */
    public static ArrayList<Student> getHighestScorers(ArrayList<Student> students) {
        ArrayList<Student> topScorers = new ArrayList<>();
        if (students == null || students.isEmpty()) {
            return topScorers;
        }
        double highest = getHighestMarks(students);
        for (Student student : students) {
            if (student.getMarks() == highest) {
                topScorers.add(student);
            }
        }
        return topScorers;
    }

    /**
     * Retrieves all students who scored the lowest marks.
     *
     * @param students List of students
     * @return List of lowest scoring students
     */
    public static ArrayList<Student> getLowestScorers(ArrayList<Student> students) {
        ArrayList<Student> lowScorers = new ArrayList<>();
        if (students == null || students.isEmpty()) {
            return lowScorers;
        }
        double lowest = getLowestMarks(students);
        for (Student student : students) {
            if (student.getMarks() == lowest) {
                lowScorers.add(student);
            }
        }
        return lowScorers;
    }

    /**
     * Prints a formatted table of all students.
     *
     * @param students List of students
     */
    public static void printStudentTable(ArrayList<Student> students) {
        printDivider();
        System.out.printf("| %-6s | %-22s | %-8s | %-6s |\n", "ID", "Name", "Marks", "Grade");
        printDivider();
        for (Student student : students) {
            System.out.println(student);
        }
        printDivider();
    }

    /**
     * Displays a complete summary report including average, highest, lowest,
     * pass/fail statistics, and grade distribution.
     *
     * @param students List of students
     */
    public static void displaySummaryReport(ArrayList<Student> students) {
        if (students == null || students.isEmpty()) {
            System.out.println("\n[!] No student records available to generate a summary report.");
            return;
        }

        double average = calculateAverage(students);
        double highest = getHighestMarks(students);
        double lowest = getLowestMarks(students);
        ArrayList<Student> topScorers = getHighestScorers(students);
        ArrayList<Student> lowScorers = getLowestScorers(students);

        int countAplus = 0, countA = 0, countB = 0, countC = 0, countD = 0, countF = 0;
        int passCount = 0;

        for (Student s : students) {
            switch (s.getGrade()) {
                case "A+": countAplus++; passCount++; break;
                case "A":  countA++; passCount++; break;
                case "B":  countB++; passCount++; break;
                case "C":  countC++; passCount++; break;
                case "D":  countD++; passCount++; break;
                case "F":  countF++; break;
            }
        }

        double passRate = ((double) passCount / students.size()) * 100.0;

        System.out.println("\n=======================================================");
        System.out.println("            STUDENT PERFORMANCE SUMMARY REPORT         ");
        System.out.println("=======================================================");
        System.out.printf(" Total Students Enrolled : %d\n", students.size());
        System.out.printf(" Class Average Marks     : %.2f / 100 (Grade: %s)\n", average, calculateGrade(average));
        System.out.printf(" Highest Marks Scored    : %.2f / 100\n", highest);
        System.out.printf(" Lowest Marks Scored     : %.2f / 100\n", lowest);
        System.out.printf(" Passing Percentage      : %.2f%% (%d Passed, %d Failed)\n", passRate, passCount, countF);
        System.out.println("-------------------------------------------------------");
        System.out.println(" Top Scorer(s):");
        for (Student s : topScorers) {
            System.out.printf("   - %s (ID: %d) -> %.2f Marks [Grade %s]\n", s.getName(), s.getId(), s.getMarks(), s.getGrade());
        }
        System.out.println(" Lowest Scorer(s):");
        for (Student s : lowScorers) {
            System.out.printf("   - %s (ID: %d) -> %.2f Marks [Grade %s]\n", s.getName(), s.getId(), s.getMarks(), s.getGrade());
        }
        System.out.println("-------------------------------------------------------");
        System.out.println(" Grade Distribution:");
        System.out.printf("   A+ (90-100) : %d student(s)\n", countAplus);
        System.out.printf("   A  (80-89)  : %d student(s)\n", countA);
        System.out.printf("   B  (70-79)  : %d student(s)\n", countB);
        System.out.printf("   C  (60-69)  : %d student(s)\n", countC);
        System.out.printf("   D  (50-59)  : %d student(s)\n", countD);
        System.out.printf("   F  (<50)    : %d student(s)\n", countF);
        System.out.println("=======================================================");
    }

    /**
     * Helper method to print clean tabular divider lines.
     */
    public static void printDivider() {
        System.out.println("+--------+------------------------+----------+--------+");
    }

    /**
     * Searches for a student by their unique ID.
     *
     * @param students List of students to search within
     * @param id       Student ID to find
     * @return The Student object if found, or null if not found
     */
    public static Student findStudentById(ArrayList<Student> students, int id) {
        if (students == null) {
            return null;
        }
        for (Student s : students) {
            if (s.getId() == id) {
                return s;
            }
        }
        return null;
    }

    /**
     * Checks if a student ID already exists in the given student list.
     *
     * @param students List of students
     * @param id       Student ID to check
     * @return true if ID already exists, false otherwise
     */
    public static boolean isIdDuplicate(ArrayList<Student> students, int id) {
        return findStudentById(students, id) != null;
    }
}
