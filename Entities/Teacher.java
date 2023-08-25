package Final_DBMS_Project.src.main.java.DB_project.Entities;

import java.util.Date;

public class Teacher extends User {
    public int admin_id;
    public String qualification;
    public Date joining_date;

    public Teacher(int user_id, int admin_id, String phone, int salary, String qualification, Date joining_date){
        super(user_id, phone, salary);
        this.admin_id = admin_id;
        this.qualification = qualification;
        this.joining_date = joining_date;
    }
}
