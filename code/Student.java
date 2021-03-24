import java.util.ArrayList;
import java.util.List;

public class Student {
    private String[] names;
    private String surname;
    private int studentID;
    private List<Course> takenCourses;

    public Student(String[] names, String surname, int studentID){
        this.names = names;
        this.surname = surname;
        this.studentID = studentID;
        this.takenCourses = new ArrayList<>();
    }

    public String[] getNames() { return names; }
    public String getSurname() { return surname; }
    public int getStudentID() { return studentID; }
    public List<Course> getTakenCourses() { return takenCourses; }
}