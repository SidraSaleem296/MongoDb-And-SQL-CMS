package Final_DBMS_Project.src.main.java.DB_project.Controller;

import Final_DBMS_Project.src.main.java.DB_project.Entities.User;
import Final_DBMS_Project.src.main.java.DB_project.Model.Model;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class Controller {
    private static User user;
    private static javax.swing.JTable attendance_table;
    private static javax.swing.JComboBox<String> sectionbox;
    private static javax.swing.JComboBox<String> coursebox;
    private static javax.swing.JComboBox<String> result_section_box;
    private static javax.swing.JComboBox<String> student_dropdown;
    private static javax.swing.JComboBox<String> result_course_menu;
    private static javax.swing.JTextField student_marks;
    private static ArrayList<Object[]> students;
    private static HashMap<Integer, Object[]> attendance;
    private static HashMap<Integer, String> att_updates;

    public static void setUser(User user) {
        Controller.user = user;
    }

    public static User getUser() {
        return user;
    }

    public static void setAttendance_table(JTable attendance_table) {
        Controller.attendance_table = attendance_table;
        attendance_table.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{{}},
                new String[]{
                        "#", "Name", "Status"
                }
        ));
    }

    private static Object[][] get_all_students(String section){
        students = Model.get_section_students(section);
        Object[][] att_model = new Object[students.size()][3];
        for(int i = 0; i < students.size(); ++i) {
            Object[] st = students.get(i);
            att_model[i] = new Object[]{(int) st[0], (String) st[1], ""};
        }
        return att_model;
    }

    public static void setSectionbox(JComboBox<String> sectionbox) {
        Controller.sectionbox = sectionbox;
    }

    public static void set_sections(int teacher_id){
        ArrayList<String> sections_taught = Model.get_sections(teacher_id);
        DefaultComboBoxModel<String> section_model = new DefaultComboBoxModel<>(sections_taught.toArray(new String[0]));
        sectionbox.setModel(section_model);
        sectionbox.setSelectedIndex(0);
        String section = sectionbox.getItemAt(0);
        Object[][] att_model = Controller.get_all_students(section);
        attendance_table.setModel(new javax.swing.table.DefaultTableModel(
                att_model,
                new String[]{
                        "#", "Name", "Status"
                }
        ));
        result_section_box.setModel(section_model);
    }

    public static void setCoursebox(JComboBox<String> coursebox) {
        Controller.coursebox = coursebox;
    }

    public static JComboBox<String> getCoursebox() {
        return coursebox;
    }

    public static void set_courses(int teacher_id, String section){
        if(section != null){
            ArrayList<String> courses_taught = Model.get_courses(teacher_id, section);
            DefaultComboBoxModel<String> courses_model = new DefaultComboBoxModel<>(courses_taught.toArray(new String[0]));
            coursebox.setModel(courses_model);
            result_course_menu.setModel(courses_model);
        }
    }

    public static void setResult_section_box(JComboBox<String> result_section_box) {
        Controller.result_section_box = result_section_box;
    }

    public static void setStudent_dropdown(JComboBox<String> student_dropdown) {
        Controller.student_dropdown = student_dropdown;
    }

    public static void setResult_course_menu(JComboBox<String> result_course_menu) {
        Controller.result_course_menu = result_course_menu;
    }

    public static void setStudent_marks(JTextField student_marks) {
        Controller.student_marks = student_marks;
    }

    public static void set_students(){
        String[] all_students = new String[students.size()];
        int i = 0;
        for (Object[] student : students) {
            all_students[i++] = (String) student[1];
        }
        DefaultComboBoxModel<String> students_model = new DefaultComboBoxModel<>(all_students);
        student_dropdown.setModel(students_model);
    }

    public static void fetch_student_result(String student, String course, String section){
        if(student != null && course != null && section != null){
            int marks = Model.get_student_result(student, course, section);
            Controller.student_marks.setText(""+marks);
        }
    }

    public static void update_student_result(String student, String course, String section, String marks){
        if(student != null && course != null && section != null && marks != null){
            int obtained_marks = Integer.parseInt(marks);
            Model.update_student_result(student, course, section, obtained_marks);
        }
    }

    public static void display_attendance(String section, String course, int date, String month, int year){
        if(section != null && course != null){
            if(students == null){
                get_all_students(section);
            }
            String today = "" + date + "-" + month + "-" + year;
            ArrayList<Object[]> attendance_data = Model.get_date_attendance(section, course, today);
            attendance = new HashMap<Integer, Object[]>();
            for(int i = 0; i < attendance_data.size(); ++i){
                attendance.put((int) attendance_data.get(i)[0], attendance_data.get(i));
            }

            Object[][] att_model = new Object[students.size()][3];
            Controller.get_all_students(section);
            for(int i = 0; i < students.size(); ++i) {
                if (attendance.containsKey((int) students.get(i)[0])) {
                    att_model[i] = new Object[]{(int) students.get(i)[0], (String) students.get(i)[1], (String) attendance.get(students.get(i)[0])[2]};
                } else {
                    att_model[i] = new Object[]{(int) students.get(i)[0], (String) students.get(i)[1], ""};
                }
            }
            attendance_table.setModel(new javax.swing.table.DefaultTableModel(
                    att_model,
                    new String[]{
                            "#", "Name", "Status"
                    }
            ));
            attendance_table.getModel().addTableModelListener(new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent e) {

                    if(att_updates == null)
                        att_updates = new HashMap<Integer, String>();

                    int row = e.getFirstRow();
                    int col = e.getColumn();
                    String val = (String) attendance_table.getValueAt(row, col);
                    int roll_no = (int) attendance_table.getValueAt(row, 0);
                    att_updates.put(roll_no, val);
                }
            });
        }
    }

    public static void update_attendance(String course, int date, int month, int year){
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, date);
        java.sql.Date today = new java.sql.Date(cal.getTimeInMillis());

        Model.update_attendance(Controller.user.user_id, course, today, att_updates);

    }
}
