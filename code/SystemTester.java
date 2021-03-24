public class SystemTester {
    SIS system = new SIS();
    MockSIS mockSIS = new MockSIS();
    int studentIDs[] = {8245926, 5714649, 4769547, 4269471} ;

    void test_get_grade(){
        System.out.println(mockSIS.getGrade(8245926,2300105,20101)
                ==system.getGrade(8245926,2300105,20101));
        System.out.println(mockSIS.getGrade(5714649,2300105,20111)
                ==system.getGrade(5714649,2300105,20111));
        System.out.println(mockSIS.getGrade(4769547,2300105,20111)
                ==system.getGrade(4769547,2300105,20111));
        System.out.println(mockSIS.getGrade(4269471,2300105,20201)
                ==system.getGrade(4269471,2300105,20201));
    }


}
