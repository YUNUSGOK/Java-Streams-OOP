import javax.xml.stream.events.StartDocument;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class MockSIS {
    private static String fileSep = File.separator;
    private static String lineSep = System.lineSeparator();
    private static String space   = " ";

    private List<Student> studentList = new ArrayList<>();

    public MockSIS(){ processOptics(); }

    private void processOptics(){

        List<Stream<String>> inputs = new ArrayList<>() ;
        try(Stream<Path> paths = Files.list(Paths.get("input"))) {
            paths.forEach(s-> {
                try {
                    inputs.add(Files.lines(s));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch(IOException ioe) {
            System.err.println("IOException: " + ioe);}

        inputs.stream().forEach(i->{
            List<String> input = i.collect(Collectors.toList());
            String[] studentInfo = input.get(0).split(" ");
            String[] names = Arrays.copyOfRange(studentInfo,0,studentInfo.length-2);
            String surname = studentInfo[studentInfo.length-2];
            int studentID = Integer.parseInt(studentInfo[studentInfo.length-1]);
            Optional<Student> opStudent = studentList.stream().filter(
                    s -> s.getStudentID() == studentID).findAny();
            Student currentStudent= null;
            if(opStudent.isPresent()){
                currentStudent = opStudent.get();
            }
            else{
                currentStudent = new Student(names,surname,studentID);
                studentList.add(currentStudent);
            }
            String[] courseInfo = input.get(1).split(" ");
            int courseCode = Integer.parseInt(courseInfo[1]);
            int year = Integer.parseInt(courseInfo[0]);
            int credit= Integer.parseInt(courseInfo[2]);
            String examType = input.get(2);;
            String answers = input.get(3);
            double grade = answers.chars().filter(s->s==Character.valueOf('T')).count()*100/answers.length();
            Course newCourse = new Course(courseCode, year, examType, credit, grade);
            currentStudent.getTakenCourses().add(newCourse);
        });

        //Main.printStudents(studentList);

    }

    public double getGrade(int studentID, int courseCode, int year){
        double mt1=.0, mt2= .0, fnl=.0;
        List<Course> cList;
        Student student = null;
        for(Student s: studentList){
            if(s.getStudentID() == studentID){
                student = s;
                break;
            }
        }
        if(student ==null) return 0;
        for(Course c: student.getTakenCourses()){
            if(c.getCourseCode() == courseCode){
                if(c.getExamType().equals("Midterm1")) mt1 = c.getGrade();
                if(c.getExamType().equals("Midterm2")) mt2 = c.getGrade();
                if(c.getExamType().equals("Final")) fnl = c.getGrade();
            }
        }
        return 0.25 * mt1 + 0.25 * mt2 + 0.5 * fnl;
    }

    public void createTranscript(int studentID){
        double totalGrade = 0;
        double totalCredit = 0;

        List<Integer> years = new ArrayList<>();
        List<Integer> calculatedGrades = new ArrayList<>();
        Student student = null;
        for(Student s: studentList){
            if(s.getStudentID() == studentID){
                student = s;
                break;
            }
        }
        Map<Integer, Double> courseGradePairs = new HashMap<>();
        Map<Integer, Integer> courseCreditPairs = new HashMap<>();
        List<Course> courses = student.getTakenCourses().stream().sorted((c1,c2)-> (c1.getYear()- c2.getYear())).collect(Collectors.toList());
        for(Course c: courses){
            if(!c.getExamType().equals("Final")) continue;

            courseCreditPairs.put(c.getCourseCode(),c.getCredit());
            courseGradePairs.put(c.getCourseCode(),getGrade(studentID,c.getCourseCode(),c.getYear()));
            double grade = getGrade(studentID,c.getCourseCode(),c.getYear());
            String lGrade;
            double contribution;
            if(grade<50) {
                lGrade = "FF";
                contribution = .0;
            }
            else if(grade<59.5) {
                contribution = .5;
                lGrade = "FD";
            }
            else if(grade<64.5) {
                contribution = 1.0;
                lGrade = "DD";
            }
            else if(grade<69.5) {
                contribution = 1.5;
                lGrade = "DC";
            }
            else if(grade<74.5) {
                contribution = 2.0;
                lGrade = "CC";
            }
            else if(grade<79.5) {
                contribution = 2.5;
                lGrade = "CB";
            }
            else if(grade<84.5) {
                contribution = 3.0;
                lGrade = "BB";
            }
            else if(grade<89.5) {
                contribution = 3.5;
                lGrade = "BA";
            }
            else  {
                contribution = 4.0;
                lGrade = "AA";
            }

            System.out.println(c.getCourseCode() + " " + lGrade );

        }



    }

    public void findCourse(int courseCode){
        Map<Integer,Integer> yearCountPair = new HashMap<>();
        studentList.stream().forEach(s->{
            s.getTakenCourses().stream().
                    filter(course -> course.getCourseCode()==(courseCode)).
                    filter(course -> course.getExamType().equals("Final")).
                    forEach(course->{
                        if(yearCountPair.get(course.getYear())==null){
                            yearCountPair.put(course.getYear(),1);
                        }
                        else{
                            yearCountPair.compute(course.getYear(),(year,count)->count+1);
                        }

                    });

        });
        yearCountPair.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getKey)).
                forEach(e->System.out.println(e.getKey() +" "+e.getValue()));
    }

    public void createHistogram(int courseCode, int year){

        int intervals[] = new int[10]  ;
        studentList.stream().forEach(student -> {
            Integer studentID = student.getStudentID();
            student.getTakenCourses().stream()
                    .filter(course -> course.getYear() == year)
                    .filter(course -> course.getCourseCode() == courseCode)
                    .filter(course -> course.getExamType().equals("Final"))
                    .forEach(course -> {
                        double grade = getGrade(studentID,courseCode,year);

                        intervals[(int) Math.floor(grade/10)]++;
                    });
        });

        List<Integer> integers = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        integers.stream()
                .forEach(i -> System.out.println(i*10 + "-" + (i+1)*10 + " " + intervals[i]));
    }
}