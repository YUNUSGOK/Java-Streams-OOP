public class Course {
    private int courseCode;
    private int year;
    private String examType;
    private int credit;
    private double grade;

    public Course(int courseCode, int year, String examType, int credit, double grade){
        this.courseCode = courseCode;
        this.year = year;
        this.examType = examType;
        this.credit = credit;
        this.grade = grade;
    }

    public int getCourseCode() { return courseCode; }
    public int getYear() { return year; }
    public String getExamType() { return examType; }
    public int getCredit() { return credit; }
    public double getGrade() { return grade; }

    public void setGrade(double grade) { this.grade = grade; }
}