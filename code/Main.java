import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void printStudents(List<Student> students){
        for(Student s  : students){
            System.out.print(s.getStudentID()+ "|" );
            System.out.print(Arrays.toString(s.getNames())+ "|");
            System.out.print(s.getSurname()+ "|\n");
            for(Course c : s.getTakenCourses()){
                printCourse(c);
            }
        }
    }
    public static void printStudent(Student s){

            System.out.print(s.getStudentID()+ "|" );
            System.out.print(Arrays.toString(s.getNames())+ "|");
            System.out.print(s.getSurname()+ "|\n");
            for(Course c : s.getTakenCourses()){
                printCourse(c);
            }

    }
    public static void printCourse (Course c){
        System.out.print("\t" + c.getCourseCode()+ "|");
        System.out.print( c.getCredit()+ "|");
        System.out.print( c.getExamType()+ "|");
        System.out.print(c.getYear() + "|" );
        System.out.print(c.getGrade() + "|\n" );

    }
    public static void main(String[] args) {

        SIS sis = new SIS();

        System.out.println("Transcript of student with ID (6683962)\n\n");
        sis.createTranscript(6683962);
        System.out.println("\n\n Finding course with ID (5710232)\n\n");
        sis.findCourse(5710232);
        System.out.println("\n\n Histogram of the course with ID (5710232) and term (20112)\n\n\n");
        sis.createHistogram(5710232,20112);

    }

}
