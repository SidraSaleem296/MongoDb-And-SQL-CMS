package Final_DBMS_Project.src.main.java.DB_project.Model;

import Final_DBMS_Project.src.main.java.DB_project.Entities.Admin;
import Final_DBMS_Project.src.main.java.DB_project.Entities.Librarian;
import Final_DBMS_Project.src.main.java.DB_project.Entities.Teacher;
import Final_DBMS_Project.src.main.java.DB_project.Entities.User;
import Final_DBMS_Project.src.main.java.DB_project.checks;

import javax.swing.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Model {

    private static Connection con;

    public static void connect(){
        try{
            Class.forName("oracle.jdbc.OracleDriver");
            con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "system", "mypassword");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void close_connection(){
        try{
            con.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private static Admin get_admin(int uid){
        try{
            connect();
            PreparedStatement query = con.prepareStatement("SELECT * FROM ADMIN WHERE USER_ID=?");
            query.setInt(1, uid);
            ResultSet admin_res = query.executeQuery();

            if(admin_res.next()){
                return new Admin(
                        uid,
                        admin_res.getString("PHONE"),
                        admin_res.getInt("SALARY"),
                        admin_res.getString("SHIFT").charAt(0)
                );
            }
            else
                throw new Exception("Admin not found");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static Teacher get_teacher(int uid){
        try{
            connect();
            PreparedStatement query = con.prepareStatement("SELECT * FROM FACULTY WHERE USER_ID=?");
            query.setInt(1, uid);
            ResultSet teacher_res = query.executeQuery();

            if(teacher_res.next()){
                return new Teacher(
                        uid,
                        teacher_res.getInt("ADMIN_USER_ID"),
                        teacher_res.getString("PHONE"),
                        teacher_res.getInt("SALARY"),
                        teacher_res.getString("QUALIFICATION"),
                        teacher_res.getDate("JOINING_DATE")
                );
            }
            else
                throw new Exception("Faculty member not found");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static Librarian get_librarian(int uid){
        try{
            connect();
            PreparedStatement query = con.prepareStatement("SELECT * FROM LIBRARIAN WHERE USER_ID=?");
            query.setInt(1, uid);
            ResultSet librarian_res = query.executeQuery();

            if(librarian_res.next()){
                return new Librarian(
                        uid,
                        librarian_res.getInt("ADMIN_USER_ID"),
                        librarian_res.getString("PHONE"),
                        librarian_res.getInt("SALARY"),
                        librarian_res.getInt("WORKING_HOURS"),
                        librarian_res.getInt("LIBRARY_COUNTRYWISE_CODE")
                );
            }
            else
                throw new Exception("Librarian not found");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static User authenticate_user(String username, String password){
        if(checks.validInteger(username)) {
            int userID = Integer.parseInt(username);
            try {
                PreparedStatement query;
                query = con.prepareStatement("SELECT USER_ID, PASSWORD, TYPE FROM \"user\" WHERE USER_ID='" + userID + "' AND PASSWORD='" + password + "'");
            query.setString(1, username);
            query.setString(2, password);
                ResultSet res = query.executeQuery();
                if (res.next()) {
                    char user_type = res.getString("TYPE").charAt(0);
                    int uid = res.getInt("USER_ID");
                    switch (user_type) {
                        case 'A':
                            return get_admin(uid);
                        case 'F':
                            return get_teacher(uid);
                        case 'L':
                            return get_librarian(uid);
                        default:
                            System.out.println("Invalid user type");
                            break;
                    }
                } else {
                    System.out.println("");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;

    }

    public static ArrayList<Object[]> get_section_students(String section){
        ArrayList<Object[]> students = new ArrayList<Object[]>();
        try{
            PreparedStatement query = con.prepareStatement("SELECT S.ROLL_NO AS ROLL_NO, S.NAME AS STUDENT_NAME FROM STUDENT S JOIN SECTION SE ON S.SECTION_ROOMNO=SE.ROOMNO WHERE SE.NAME='"+section+"'");
//            query.setString(1, section);
            ResultSet res = query.executeQuery();
            while(res.next()){
                students.add(new Object[]{res.getInt("ROLL_NO"), res.getString("STUDENT_NAME")});
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return students;
    }

    public static Object [][] get_class_attendance(String section, String course){
        try {
            PreparedStatement count_query = con.prepareStatement("SELECT COUNT(*) AS NUM_ATT FROM STUDENT_ATTENDANCE JOIN STUDENT ON STUDENT_ROLL_NO=ROLL_NO JOIN SECTION ON SECTION_ROOMNO=ROOMNO WHERE SECTION.NAME=? AND COURSE=?");
            count_query.setString(1, section);
            count_query.setString(2, course);
            ResultSet count_att_res = count_query.executeQuery();
            int count_att = 0, i = 0;
            if (count_att_res.next())
                count_att = count_att_res.getInt("NUM_ATT");
            PreparedStatement query = con.prepareStatement("SELECT S.NAME, SA.STATUS FROM STUDENT_ATTENDANCE SA JOIN STUDENT S ON STUDENT_ROLL_NO=ROLL_NO JOIN SECTION ON SECTION_ROOMNO=ROOMNO WHERE SECTION.NAME=? AND COURSE=?");
            query.setString(1, section);
            query.setString(2, course);
            ResultSet att_res = query.executeQuery();

            Object[][] att_data = new Object[count_att][3];

            while (i < count_att) {
                att_res.next();
                att_data[i] = new Object[]{i + 1, att_res.getString("NAME"), att_res.getString("STATUS")};
                ++i;
            }
            return att_data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Object[][]{{}};
    }

    public static ArrayList<String> get_sections(int teacher_id){
        ArrayList<String> sections = new ArrayList<String>();
        try{
            PreparedStatement query = con.prepareStatement("SELECT name FROM SECTION JOIN TEACHV1 ON roomNo=section_roomNo WHERE faculty_User_ID=?");
            query.setInt(1, teacher_id);
            ResultSet section_res = query.executeQuery();
            while(section_res.next())
                sections.add(section_res.getString("name"));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return sections;
    }

    public static ArrayList<String> get_courses(int teacher_id, String section){
        ArrayList<String> courses = new ArrayList<String>();
        try {
            PreparedStatement query = con.prepareStatement("SELECT C.name AS courses_name FROM COURSES C JOIN TEACH T ON C.general_ID=T.courses_general_ID JOIN FACULTY F ON T.faculty_User_ID=F.User_ID JOIN TEACHV1 ON F.User_ID=TEACHV1.faculty_User_ID JOIN SECTION S ON TEACHV1.section_roomNo=S.roomNo WHERE F.USER_ID=? AND S.name='"+section+"'");
            query.setInt(1, teacher_id);
            ResultSet course_res = query.executeQuery();
            while (course_res.next())
                courses.add(course_res.getString("courses_name"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return courses;
    }

    public static ArrayList<String> get_students(String section){
        ArrayList<String> students = new ArrayList<String>();
        if(section != null) {
            try {
                PreparedStatement query = con.prepareStatement("SELECT S.NAME AS STUDENT_NAME FROM STUDENT S JOIN SECTION SE ON S.SECTION_ROOMNO=SE.ROOMNO WHERE SE.NAME='"+section+"'");
//                query.setString(1, section);
                ResultSet student_res = query.executeQuery();
                while (student_res.next())
                    students.add(student_res.getString("STUDENT_NAME"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return students;
    }

    public static int get_student_result(String student, String course, String section){
        int marks = 0;
        try{
            PreparedStatement marks_query = con.prepareStatement("Select OBTAINED_MARKS FROM RESULT_PARTICULAR RP \n" +
                    "JOIN RESULT R ON RP.RESULT_ID=R.id \n" +
                    "JOIN STUDENT S ON R.student_roll_no=S.roll_no\n" +
                    "JOIN SECTION SE ON S.section_roomNo=SE.roomNo\n" +
                    "WHERE RP.COURSE='"+course+"' AND S.NAME='"+student+"' AND SE.NAME='"+section+"'");
            ResultSet marks_res = marks_query.executeQuery();
            if(marks_res.next())
                marks = marks_res.getInt("OBTAINED_MARKS");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return marks;
    }

    public static void update_student_result(String student, String course, String section, int marks){
        try{
            PreparedStatement rp_query = con.prepareStatement("Select rp_id FROM RESULT_PARTICULAR RP \n" +
                    "JOIN RESULT R ON RP.RESULT_ID=R.id \n" +
                    "JOIN STUDENT S ON R.student_roll_no=S.roll_no\n" +
                    "JOIN SECTION SE ON S.section_roomNo=SE.roomNo\n" +
                    "WHERE RP.COURSE='"+course+"' AND S.NAME='"+student+"' AND SE.NAME='"+section+"'");
            ResultSet rp_res = rp_query.executeQuery();
            rp_res.next();
            int rp_id = rp_res.getInt("rp_id");
            PreparedStatement update_query = con.prepareStatement("UPDATE RESULT_PARTICULAR SET OBTAINED_MARKS=? WHERE RP_ID=?");
            update_query.setInt(1, marks);
            update_query.setInt(2, rp_id);
            int rows_updated = update_query.executeUpdate();
            if(rows_updated == 1)
                System.out.println("Result updated");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static ArrayList<Object[]> get_date_attendance(String section, String course, String dt){
        // Return {student_id, attendance_id, status}
        ArrayList<Object[]> att_data = new ArrayList<Object[]>();
        try{
            PreparedStatement query = con.prepareStatement("SELECT SA.ID AS ATT_ID, SA.STUDENT_ROLL_NO AS ST_ROLL, SA.STATUS AS ATT_STATUS FROM STUDENT_ATTENDANCE SA JOIN STUDENT S ON SA.STUDENT_ROLL_NO=S.ROLL_NO JOIN SECTION SN ON S.SECTION_ROOMNO=SN.ROOMNO WHERE SN.NAME=? AND SA.COURSE=? AND TO_DATE(SA.ATT_DATE)=?");
            query.setString(1, section);
            query.setString(2, course);
            query.setString(3, dt);
            ResultSet res = query.executeQuery();

            while(res.next())
                att_data.add(new Object[]{res.getInt("ST_ROLL"), res.getInt("ATT_ID"), res.getString("ATT_STATUS")});
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return att_data;
    }

    public static void update_attendance(int teacher_id, String course, java.sql.Date dt, HashMap<Integer, String> updates){
        for(Map.Entry<Integer, String> set : updates.entrySet()){
            int roll_no = set.getKey();
            String status = set.getValue();
            try{
                PreparedStatement exists_query = con.prepareStatement("SELECT ID FROM STUDENT_ATTENDANCE WHERE STUDENT_ROLL_NO=? AND ATT_DATE=? AND COURSE=?");
                exists_query.setInt(1, roll_no);
                exists_query.setDate(2, dt);
                exists_query.setString(3, course);
                ResultSet exists_res = exists_query.executeQuery();
                if(exists_res.next()){
                    int att_id = exists_res.getInt("ID");
                    PreparedStatement update_query = con.prepareStatement("UPDATE STUDENT_ATTENDANCE SET STATUS=? WHERE ID=?");
                    update_query.setString(1, status);
                    update_query.setInt(2, att_id);
                    int ru = update_query.executeUpdate();

                    System.out.println("NUMBER OF ROWS UPDATED: " + ru);
                }
                else{

                    PreparedStatement st_des = con.prepareStatement("SELECT DESCIPLINE FROM STUDENT WHERE ROLL_NO=?");
                    st_des.setInt(1, roll_no);
                    ResultSet st_des_res = st_des.executeQuery();
                    st_des_res.next();
                    String descipline = st_des_res.getString("DESCIPLINE");

                    PreparedStatement max_query = con.prepareStatement("SELECT MAX(ID) AS MAX_ID FROM STUDENT_ATTENDANCE");
                    ResultSet max_res = max_query.executeQuery();
                    max_res.next();
                    int max_id = max_res.getInt("MAX_ID");

                    PreparedStatement insert_att = con.prepareStatement("INSERT INTO STUDENT_ATTENDANCE(ID, STUDENT_ROLL_NO, ATT_DATE, STATUS, COURSE, STUDENT_DESCIPLINE) VALUES (?,?,?,?,?,?)");
                    insert_att.setInt(1, max_id+1);
                    insert_att.setInt(2, roll_no);
                    insert_att.setDate(3, dt);
                    insert_att.setString(4, status);
                    insert_att.setString(5, course);
                    insert_att.setString(6, descipline);
                    int ru = insert_att.executeUpdate();

                    System.out.println("NUMBER OF ROWS INSERTED IN STUDENT ATTENDANCE : " + ru);

                    PreparedStatement update_query = con.prepareStatement("INSERT INTO UPDATES(FACULTY_USER_ID, STUDENT_ATTENDANCE_ID) VALUES (?,?)");
                    update_query.setInt(1, teacher_id);
                    update_query.setInt(2, max_id+1);
                    ru = update_query.executeUpdate();

                    System.out.println("NUMBER OF ROWS INSERTED IN UPDATES : " + ru);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

}
