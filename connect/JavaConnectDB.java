package Final_DBMS_Project.src.main.java.DB_project.connect;

import Final_DBMS_Project.src.main.java.DB_project.Admin.fee_challan;
import Final_DBMS_Project.src.main.java.DB_project.Tables.*;
import Final_DBMS_Project.src.main.java.DB_project.checks;

import java.sql.*;
import javax.swing.*;

public class JavaConnectDB
{
    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement preparedStatement;
    private static ResultSet resultSet;

    public static void connectDB() {
        try {

            Class.forName("oracle.jdbc.OracleDriver");
            connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "mypassword");
            JFrame f = new JFrame();
            JOptionPane.showMessageDialog(f, "Connected to  DB");
        }
        catch(Exception e)
        {
            JFrame f = new JFrame();
            JOptionPane.showMessageDialog(f, "Error in Connecting to  DB");
        }
    }

    // This one is vulnerable to sql injection and therefore is replaced with the prepared Statements
    // Should only be used when executed by the programmer
    public static ResultSet executeQuery(String query) {
        try {
            connectDB();
            statement = connection.createStatement();
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
        }
        catch(Exception e)
        {
            JFrame f = new JFrame();
            JOptionPane.showMessageDialog(f, "Error Executing the Query");
        }
        return resultSet;
    }
    public ResultSet executePreparedQuery(String query) {
        try {
            preparedStatement = connection.prepareStatement(query);
            resultSet = statement.executeQuery(query);
        }
        catch(Exception e)
        {
            System.out.println("Error Executing the Prepared Query");
        }

        return resultSet;
    }

    //queries functions start from here
    //query function to display all admins joining admin and user table

    //admin side queries
    public static void adminList(){
        String query = "SELECT user_id, user_name, admin.salary, admin.phone, admin.shift\n" +
                "FROM ADMIN NATURAL JOIN \"user\"";
        try {
            connectDB();
            statement = connection.createStatement();
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            new all_admins(resultSet);
        }catch (SQLException e){
            JFrame f = new JFrame();
            JOptionPane.showMessageDialog(f, e.getMessage());
        }
    }
    //query function to display all librarians through a view created joining user and librarian
    public static void librarians_list(){
        String query = "SELECT * \n" +
                "FROM librarian_data";
        try {
            connectDB();
            statement = connection.createStatement();
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            new librarian_list(resultSet);
        }catch (SQLException e){
            JFrame f = new JFrame();
            JOptionPane.showMessageDialog(f, e.getMessage());
        }
    }
//query to insert new admin user
    public static boolean addAdmin(String name, String pass, String phone, String salary, char shift){
        if(!checks.validInteger(salary) || !checks.validStringWithoutNumbers(name)){
            JFrame f = new JFrame();
            JOptionPane.showMessageDialog(f, "Kindly Enter valid credentials");
        }else{
            String user= "SELECT MAX(user_id)\n" +
                    "FROM \"user\"";
            try {
                connectDB();
                statement = connection.createStatement();
                preparedStatement = connection.prepareStatement(user);
                resultSet = preparedStatement.executeQuery();
                int id = 0;
                while (resultSet.next()) {
                    id += Integer.parseInt(resultSet.getString(1)) + 1;
                }
                String user_insert = "INSERT INTO \"user\" (user_id, password, user_name, type) VALUES ('" + id + "', '" + pass + "', '" + name + "', 'A')";
                String admin_insert = "INSERT INTO ADMIN (user_id, salary, phone, shift) VALUES ('" + id + "', '" + salary + "', '" + phone + "', '" + shift + "')";
                preparedStatement = connection.prepareStatement(user_insert);
                resultSet = preparedStatement.executeQuery();
                preparedStatement = connection.prepareStatement(admin_insert);
                resultSet = preparedStatement.executeQuery();
                JFrame f = new JFrame();
                JOptionPane.showMessageDialog(f, "Admin Successfully added");
                return true;
            }catch (SQLException e){
                JFrame f = new JFrame();
                JOptionPane.showMessageDialog(f, e.getMessage());
            }
        }
        return false;
    }
    //query function to insert librarian
    public static boolean addLibrarian(int adminID, String name, String pass, String salary, String hours, String phone){
        if(!checks.validInteger(salary) || !checks.validStringWithoutNumbers(name) ||!checks.validInteger(hours)){
            JFrame f = new JFrame();
            JOptionPane.showMessageDialog(f, "Kindly Enter valid credentials");
        }else{
            String user= "SELECT MAX(user_id)\n" +
                    "FROM \"user\"";
            try {
                connectDB();
                statement = connection.createStatement();
                preparedStatement = connection.prepareStatement(user);
                resultSet = preparedStatement.executeQuery();
                int id = 0;
                while (resultSet.next()) {
                    id += Integer.parseInt(resultSet.getString(1)) + 1;
                }
                String user_insert = "INSERT INTO \"user\" (user_id, password, user_name, type) VALUES ('" + id + "', '" + pass + "', '" + name + "', 'L')";
                String lib_insert = "INSERT INTO Librarian (user_id, salary, working_hours, admin_user_id, library_countrywise_code, phone) VALUES ('" + id + "', '" + salary + "', '" + hours + "', '" + adminID + "', '156745', '" + phone + "')";
                preparedStatement = connection.prepareStatement(user_insert);
                resultSet = preparedStatement.executeQuery();
                preparedStatement = connection.prepareStatement(lib_insert);
                resultSet = preparedStatement.executeQuery();
                JFrame f = new JFrame();
                JOptionPane.showMessageDialog(f, "Librarian Successfully added");
                return true;
            }catch (SQLException e){
                JFrame f = new JFrame();
                JOptionPane.showMessageDialog(f, e.getMessage());
            }


        }
        return false;
    }
    //query function to view all students
    public static void viewAllStudents(){
        String query = "SELECT roll_no, name, father_name, descipline, section_roomno\n" +
                "FROM student";
        resultSet = executeQuery(query);
        new students_list(resultSet);
    }

    //query function to add student
    private static int section;
    private static int rollNo;
    private static String rollQuery;
    public static boolean addStudent(int admin, String name, String fatherName, char gender, int Age, String descipline){
        String query;

        if(descipline.equalsIgnoreCase("Science")) {
            query = "SELECT student.section_roomno, COUNT(student.roll_no) AS no_of_students\n" +
                    "FROM student, section\n" +
                    "WHERE student.section_roomno = section.roomno AND section.discipline_name = 'Science'  \n" +
                    "GROUP BY student.section_roomno";
        }
        else {
            query = "SELECT student.section_roomno, COUNT(student.roll_no) AS no_of_students\n" +
                    "FROM student, section\n" +
                    "WHERE student.section_roomno = section.roomno AND section.discipline_name = 'Arts'  \n" +
                    "GROUP BY student.section_roomno";
        }
        int[] capacity = new int[2];
        try {
            connectDB();
            int i = 0;
            resultSet = executeQuery(query);
            while (resultSet.next()) {
                capacity[i] = resultSet.getInt(i);
                i++;

            }
        }

//        try {
//            connectDB();
//            statement = connection.createStatement();
//            preparedStatement = connection.prepareStatement(query);
//            resultSet = preparedStatement.executeQuery();
//            new all_admins(resultSet);
//        }catch (SQLException e){
//            JFrame f = new JFrame();
//            JOptionPane.showMessageDialog(f, e.getMessage());
//        }
//    }
            catch (SQLException e){
            JFrame f = new JFrame();
            JOptionPane.showMessageDialog(f, e.getMessage());
        }

        if(descipline.equalsIgnoreCase("Science")){
            if(capacity[0] < 25){
                section = 101;
            }
            else if (capacity.length != 1 && capacity[1] < 25){
                section = 103;
            }
        }
        else{
            if(capacity[0] < 30){
                section = 102;
            }
            else if (capacity.length != 1 && capacity[1] < 30){
                section = 104;
            }
        }
        if(descipline.equalsIgnoreCase("Science")){
            rollQuery = "SELECT MAX(roll_no) \n" +
                    "FROM student\n" +
                    "WHERE student.descipline = 'Science' OR student.descipline = 'science'";

        }
        else{
            rollQuery = "SELECT MAX(roll_no) \n" +
                    "FROM student\n" +
                    "WHERE student.descipline = 'Arts' OR student.descipline = 'arts'";
        }
        try {
            connectDB();
            resultSet = executeQuery(rollQuery);
            while (resultSet.next()){
                rollNo = resultSet.getInt(1) + 1;
            }

        }catch (SQLException e){
            JFrame f = new JFrame();
            JOptionPane.showMessageDialog(f, e.getMessage());
        }
        String insertStudent = "INSERT INTO STUDENT (admin_user_id, name, roll_no, " +
                "father_name, gender, age, section_roomno, descipline) " +
                "VALUES ( '" + admin + "', '" + name + "', '" + rollNo + "', '" + fatherName + "', '" + gender + "', '" + Age + "', '" + section + "', '" +descipline +"')";
        try {

            resultSet = executeQuery(insertStudent);
            JFrame f = new JFrame();
            JOptionPane.showMessageDialog(f, "Student Successfully added");
            return true;
        }catch (Exception e){
            JFrame f = new JFrame();
            JOptionPane.showMessageDialog(f, e.getMessage());
        }
        return false;
    }

    //query function to delete student
    public static boolean deleteStudent (int rollNo, String discipline){
        String query = "SELECT roll_no, descipline FROM student WHERE " +
                "roll_no = "+ rollNo + " AND (descipline = 'Arts' OR descipline = 'arts' OR " +
                "descipline = 'Science' OR descipline = 'science')";
        String query1 = "DELETE FROM student\n" +
                "WHERE roll_no = "+ rollNo + " AND (descipline = 'Arts' OR descipline = 'arts')";
        String query2 = "DELETE FROM student\n" +
                "WHERE roll_no = "+ rollNo + " AND (descipline = 'Science' OR descipline = 'science')";
        try {
            resultSet = executeQuery(query);
            while(resultSet.next()){
                if (discipline.equalsIgnoreCase("science")) {
                    resultSet = executeQuery(query1);
                } else if (discipline.equalsIgnoreCase("arts")) {
                    resultSet = executeQuery(query2);
                }
                return true;
            }
        }
        catch (SQLException e) {
            JFrame f = new JFrame();
            JOptionPane.showMessageDialog(f, e.getMessage());
        }
        return false;
    }

    //query function for adding a course
    private static int course_id;
    public static boolean addCourse(int adminID, String name, String discipline, String Category){
        String id_query = "SELECT MAX(general_id)\n" +
                "FROM courses";
        resultSet = executeQuery(id_query);
        try {
            while(resultSet.next()) {
               course_id = Integer.parseInt(resultSet.getString(1)) + 1;
            }
            String query = "INSERT INTO courses (general_id, admin_user_id, name, category)" +
                    " VALUES ('" + course_id + "', '" + adminID + "', '" + name +"', '" + Category +"')";
            resultSet = executeQuery(query);
            String disc = "";
            if(discipline.equalsIgnoreCase("arts")){
                disc += "Arts";
            }
            else{
                disc += "Science";
            }
            String add_to_contains = "INSERT INTO contains(discipline_name, courses_general_id) VALUES ('" + disc + "', '" + course_id +"')";
            resultSet = executeQuery(add_to_contains);
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        closeConnection();

        return false;
    }
    //function to view all courses
    public static void viewAllCourses(){
        String query = "SELECT general_id, name, category, contains.discipline_name\n" +
                "FROM courses, contains\n" +
                "WHERE contains.courses_general_id = courses.general_id";
        resultSet = executeQuery(query);
        new courses_list(resultSet);
    }


    //query function to view all faculty
    public static void viewAllFaculty(){
        String query = "SELECT user_id,user_name, phone, qualification, joining_data\n" +
                "FROM faculty NATURAL JOIN \"user\"";
        try {
            connectDB();
            statement = connection.createStatement();
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            new all_faculty(resultSet);
        }catch (SQLException e){
            JFrame f = new JFrame();
            JOptionPane.showMessageDialog(f, e.getMessage());
        }
    }

    //query function to add faculty
    public static boolean addFaculty(int adminID, String name, String pass, String phone, int salary, String qualification){
        String user= "SELECT MAX(user_id)\n" +
                "FROM \"user\"";
        try {
            resultSet = executeQuery(user);
            int id = 0;
            while (resultSet.next()) {
                id += Integer.parseInt(resultSet.getString(1)) + 1;
            }
            String user_insert = "INSERT INTO \"user\" (user_id, password, user_name, type) VALUES ('" + id + "', '" + pass + "', '" + name + "', 'F')";
            String faculty_insert = "INSERT INTO faculty(user_id, admin_user_id, phone, salary, qualification) VALUES ('" + id + "', '" + adminID + "', '" +phone + "', '" + salary + "', '" + qualification + "')";
            resultSet = executeQuery(user_insert);
            resultSet = executeQuery(faculty_insert);
            JFrame f = new JFrame();
            JOptionPane.showMessageDialog(f, "Faculty Successfully added");
            return true;
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return false;
    }

    //function to delete a faculty
    public static void deleteFaculty(int userID){
        String delFac = "DELETE FROM faculty WHERE user_id = "  + userID ;
        try{
            resultSet = executeQuery(delFac);
        }catch (Exception e){
            JFrame f = new JFrame();
            JOptionPane.showMessageDialog(f, e.getMessage());
            return;
        }
        String delUser = "DELETE FROM \"user\" WHERE user_id = "  + userID;
        try{
            resultSet = executeQuery(delUser);
        }catch (Exception e){
            JFrame f = new JFrame();
            JOptionPane.showMessageDialog(f, e.getMessage());
            return;
        }
        JFrame f = new JFrame();
        JOptionPane.showMessageDialog(f, "User successfully deleted");

    }

    //function to assign course to faculty
    public static boolean assignCourse(int id, int course_id, int sectionRoom){
        String courseAssign = "INSERT INTO TEACH (faculty_user_id, courses_general_id) VALUES('" + id + "', '" + course_id + "')";
        String sectionAssign = "INSERT INTO teachv1 (faculty_user_id, section_roomno) VALUES ('" + id +"', '" + sectionRoom + "')";
        try{
            resultSet = executeQuery(courseAssign);
            resultSet = executeQuery(sectionAssign);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    //function to view all fees records
    public static void feeRecords(){
        String record = "SELECT challanno, feechallan.student_roll_no, student.name, student.father_name, student.descipline, amount, feechallan.due_date, past_due_fine, is_submitted\n" +
                "FROM student NATURAL JOIN feechallan\n" +
                "WHERE student.roll_no = feechallan.student_roll_no AND student.descipline = feechallan.student_descipline";
            resultSet = executeQuery(record);
                new feeRecords(resultSet);
    }

    //function to add a fee challan
    public static boolean addChallan(int adminID, int id, int amount, String disc, String due, int fine){
        String studentExist = studentExist(id, disc);
        String challan = "SELECT MAX(challanno)\n" +
                "FROM feechallan";
        int challanNo = 0;
        try{
            resultSet = executeQuery(challan);
            if(resultSet.next()){
                challanNo = Integer.parseInt(resultSet.getString(1)) + 1;
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        if(studentExist!=null){
            String challanQuery = "INSERT INTO feechallan (challanno, admin_user_id, student_roll_no, amount, due_date, past_due_fine, student_descipline, is_submitted) " +
                    "VALUES('" + challanNo + "', '" + adminID + "', '" + id + "', '" + amount + "', '" + due + "', '" + fine + "', '" + disc + "', 'N')";
            try{
                resultSet = executeQuery(challanQuery);
                if(resultSet.next()){
                    new fee_challan(challanNo, id, studentExist, amount, disc, due, fine).setVisible(true);
                    return true;
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    //function to accept fee and update fee record
    public static boolean acceptFee(int id, String disc){
        String stuExist = studentExist(id, disc);
        if (stuExist!=null) {
            String query = "UPDATE feechallan\n" +
                    "SET is_submitted = 'Y'\n" +
                    "WHERE student_roll_no = " + id + " AND student_descipline = '" + disc + "'";
            try {
                resultSet = executeQuery(query);
                if (resultSet.next()) {
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //student exists function
    private static String query = "";
    public static String studentExist(int id, String disc){
        if(disc.equalsIgnoreCase("science")) {
            query += "SELECT roll_no, descipline , name\n" +
                    "FROM student\n" +
                    "WHERE roll_no = " + id + " AND (descipline = 'Science' OR descipline = 'science')";
        }
        else{
            query += "SELECT roll_no, descipline, name\n" +
                    "FROM student\n" +
                    "WHERE roll_no = " + id + " AND (descipline = 'Arts' OR descipline = 'arts')";
        }
        try{
            resultSet = executeQuery(query);
            if(resultSet.next()){
                return resultSet.getString(3);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    //available books
    public static ResultSet availableBooks(){
        String query = "SELECT name, category\n" +
                "FROM books\n" +
                "WHERE is_available = 'Y'";
        resultSet = executeQuery(query);
        return resultSet;
    }

    //all books
    public static ResultSet allBooks(){
        String query = "SELECT name, category\n" +
                "FROM books";
        resultSet = executeQuery(query);
        return resultSet;
    }

    //issue book
    public static void issueBook(String name,  int roll, String issue, String returnDate, String disc){
        String dis = "";
        if(disc.equalsIgnoreCase("science")){
            dis += "Science";
        }
        else{
            dis += "Arts";
        }
        String bookCategory = "SELECT name, category\n" +
                "FROM books\n" +
                "WHERE name = '" + name +"'";
        String category = "";
        try {
            resultSet = executeQuery(bookCategory);
            while (resultSet.next()){
                category = resultSet.getString("category");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        String query = "INSERT INTO ISSUED_TO (books_name, books_category, student_roll_no, date_of_issue, to_be_returned, student_descipline) " +
                "VALUES ('"+ name +"', '"+ category +"', '" + roll + "', '"+ issue +"', '"+returnDate+"', '" + dis +"')";
        try {
            resultSet = executeQuery(query);
            while (resultSet.next()){
                JFrame f = new JFrame();
                JOptionPane.showMessageDialog(f, "Book issued successfully");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        String update = "UPDATE books\n" +
                "SET is_available = 'N'\n" +
                "WHERE name = '" + name + "'";
        resultSet = executeQuery(update);

    }

    //not available books
    public static ResultSet notAvailableBooks(){
        String query = "SELECT name, category\n" +
                "FROM books\n" +
                "WHERE is_available = 'N'";
        resultSet = executeQuery(query);
        return resultSet;
    }

    //return book
    public static void returnBook(String name){
        String book = "DELETE FROM issued_to\n" +
                "WHERE books_name = '" + name + "'";
        resultSet = executeQuery(book);
        String update = "UPDATE books\n" +
                "SET is_available = 'Y'\n" +
                "WHERE name = '" + name + "'";
        resultSet = executeQuery(update);
        JFrame f = new JFrame();
        JOptionPane.showMessageDialog(f, "Book successfully returned");
    }

    public static void closeConnection() {
        try
        {
            connection.close();
        }
        catch(Exception e)
        {
            System.out.println("Error in closing the connection");
        }
    }
    public static void main(String[] args)
    {
        try
        {
            JavaConnectDB.connectDB();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }




}