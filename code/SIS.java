import javax.xml.stream.events.StartDocument;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SIS {
    private static String fileSep = File.separator;
    private static String lineSep = System.lineSeparator();
    private static String space   = " ";

    private List<Student> studentList = new ArrayList<>();

    public SIS(){ processOptics(); }



    private void processOptics(){

        List<Stream<String>> inputs = new ArrayList<>() ; //Input file streams
        try(Stream<Path> paths = Files.list(Paths.get("input"))) {//Input file paths in input folder
            paths.forEach(s-> {
                try {
                    inputs.add(Files.lines(s)); // add a input file to stream list
                } catch (IOException e) {
                    e.printStackTrace(); // file can not be found.
                }
            });
        } catch(IOException ioe) {// "input" folder can not be found
            System.err.println("IOException: " + ioe);}

        inputs.stream().forEach(i->{ //parse every input file
            List<String> input = i.collect(Collectors.toList()); //stream to list to read index-wise
            String[] studentInfo = input.get(0).split(" "); // first line gives student info
            int studentID = Integer.parseInt(studentInfo[studentInfo.length-1]);// Student id is at the end of the line
            String surname = studentInfo[studentInfo.length-2]; // // student surname is before student id
            String[] names = Arrays.copyOfRange(studentInfo,0,studentInfo.length-2); //names are before surname and can be more
            Optional<Student> opStudent = studentList.stream().filter(
                    s -> s.getStudentID() == studentID).findAny(); //find from student list if student is registered or not
            Student currentStudent= null;
            if(opStudent.isPresent()){ // if student is on the list, continue with registered one
                currentStudent = opStudent.get();
            }
            else{
                currentStudent = new Student(names,surname,studentID); // add new student to list with read student info
                studentList.add(currentStudent);
            }
            String[] courseInfo = input.get(1).split(" "); // second line gives course info(course code, year, credit)
            int year = Integer.parseInt(courseInfo[0]); // given year of the course
            int courseCode = Integer.parseInt(courseInfo[1]); // course code
            int credit= Integer.parseInt(courseInfo[2]);// credit of course
            String examType = input.get(2); //third line gives exam type which ca be Midterm1, Midterm2, Final
            String answers = input.get(3); // fourth and last line gives answers.
            //Answers are strings contains T-F-E. Grade is calculated by count('T')*100/length
            double grade = 100 * answers.chars().filter(s->s==Character.valueOf('T')).count()/((double)answers.chars().count());
            Course newCourse = new Course(courseCode, year, examType, credit, grade); // create course with processed course info
            currentStudent.getTakenCourses().add(newCourse); // added to students takenCourse list
        });

    }

    public double getGrade(int studentID, int courseCode, int year){
        double mt1, mt2, fnl; //all exams will be read to calculate grade
        List<Course> cList;
        //Student with given student ID
        Student student = studentList.stream().filter(s->(s.getStudentID() == studentID)).findAny().orElse(null);
        if(student == null) {
            System.out.println("Student does not exists...");
            return 0;
        }
        //List of courses with given course code and year
        cList = student.getTakenCourses().stream()
                    .filter(c->c.getCourseCode() == courseCode)
                    .filter(c-> c.getYear() == year)
                    .collect(Collectors.toList());
        // Midterm1 in exams
        mt1 = cList.stream()
                .filter(c-> c.getExamType().equals("Midterm1"))
                .findFirst().orElse(new Course(0,0,"",0,0)).getGrade();
        // Midterm2 in exams
        mt2 = cList.stream()
                .filter(c-> c.getExamType().equals("Midterm2"))
                .findFirst().orElse(new Course(0,0,"",0,0)).getGrade();
        // Final in exams
        fnl = cList.stream()
                .filter(c-> c.getExamType().equals("Final"))
                .findFirst().orElse(new Course(0,0,"",0,0)).getGrade();

        return 0.25 * mt1 + 0.25 * mt2 + 0.5 * fnl;// Calculated grade will return
    }

    public void updateExam(int studentID, int courseCode, String examType, double newGrade){
        //Student with given student ID
        Student student = studentList.stream().filter(s->(s.getStudentID() == studentID)).findAny().orElse(null);
        if(student == null) {
            System.out.println("Student does not exists...");
            return;
        }
        //Recent Exam that satisfies the given parameters
        Course recentExam = student.getTakenCourses().stream().
                filter(c -> c.getCourseCode() == courseCode).
                filter(c -> c.getExamType().equals(examType))
                .max(Comparator.comparingInt(Course::getYear))
                .orElse(new Course(0,0,"",0,0));

        recentExam.setGrade(newGrade); //update exam with given new grade

    }

    public void createTranscript(int studentID){
        double totalGrade = 0;
        double totalCredit = 0;
        //Student with given student ID
        Student student = studentList.stream().filter(s->(s.getStudentID() == studentID)).findAny().orElse(null);
        if(student == null){
            System.out.println("Student does not exists...");
            return;
        }
        List<Integer> years = new ArrayList<>(); // years  that student took courses
        List<Integer> courseCodes = new ArrayList<>(); //course id's that student took
        Map<Integer, Double> courseGradePairs = new HashMap<>(); // grades that  student got recent in a course
        Map<Integer, Integer> courseCreditPairs = new HashMap<>(); // course credits that student took

        student.getTakenCourses().forEach(c-> years.add(c.getYear()));// years of each course will be added

        //Courses in transcript will be listed by years
        years.stream().sorted().distinct().forEach(y->{
            System.out.println(y); //year info
            courseCodes.clear();//course of previous years will be dropped
            //Course taken that year and credit will be added
            student.getTakenCourses().stream().filter(c->c.getYear() ==y).forEach(c-> {
                        courseCodes.add(c.getCourseCode());
                        courseCreditPairs.put(c.getCourseCode(),c.getCredit());
                    }
            );
            //courses in a year will be listed by course codes.
            courseCodes.stream().distinct().sorted().forEach(c-> {
                String lGrade; //letter grade
                double contribution; // value of letter grade
                double grade = getGrade(studentID,c,y); //calculated grade
                if(grade<49.5) {
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
                System.out.println(c + " " + lGrade );//course code and letter grade will be listed
                //if previous courses is exits, it overrides and updates the grade with new one
                courseGradePairs.put(c,contribution);
            });
        });

        //total grade -> sum of  credit * grade of each courses last grade
        totalGrade = courseGradePairs.entrySet().stream()
                .map( e-> courseCreditPairs.get(e.getKey()) * e.getValue() )
                .reduce(0.00, Double::sum);
        //total credit -> sum of  credits of each taken course
        totalCredit = courseGradePairs.entrySet().stream()
                .map( e-> courseCreditPairs.get(e.getKey()))
                .reduce(0, Integer::sum);
        //CGPA ->weighted average of letter grades
        double CGPA = totalGrade/totalCredit;
        //Will be printed with dot
        System.out.printf(Locale.US, "CGPA: %.2f%n", CGPA);
    }

    public void findCourse(int courseCode){
        Map<Integer,Integer> yearCountPair = new HashMap<>();//Students count of each year for course
        studentList.forEach(s->{ //for every student
            s.getTakenCourses().stream()// for every course student took
                    .filter(course -> course.getCourseCode()==(courseCode)) //check if student took to course
                    .filter(course -> course.getExamType().equals("Final")) // display only once for each year
                    .forEach(course->{ //increment for every year when student took the course
                        if(yearCountPair.get(course.getYear())==null){ //if its not in the map, add
                            yearCountPair.put(course.getYear(),1);
                        }
                        else{
                            yearCountPair.compute(course.getYear(),(year,count)->count+1); //in the map, increment
                        }
                    });

        });
        //for every value in map print the year and count. Sorted by year
        yearCountPair.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getKey)).
                forEach(e->System.out.println(e.getKey() +" "+e.getValue()));
    }
    public void createHistogram(int courseCode, int year){
        int intervals[] = new int[10]; //histogram intervals with range 10 from 0 up to 100
        studentList.stream().forEach(student -> { //for every student check if student took the course in given year
            Integer studentID = student.getStudentID();
            student.getTakenCourses().stream()
                    .filter(course -> course.getYear() == year)
                    .filter(course -> course.getCourseCode() == courseCode)
                    .filter(course -> course.getExamType().equals("Final"))
                    .forEach(course -> {
                    double grade = getGrade(studentID,courseCode,year);//calculate the grade

                    intervals[(int) Math.floor(grade/10)]++; //increment the interval that student's grade is in
                    });
        });

        List<Integer> integers = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        //print intervals and counts of the intervals.
        integers.stream()
                .forEach(i -> System.out.println(i*10 + "-" + (i+1)*10 + " " + intervals[i]));
    }


}