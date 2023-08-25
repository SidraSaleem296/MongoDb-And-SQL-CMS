package Final_DBMS_Project.src.main.java.DB_project.Entities;

import java.util.Date;

public class Librarian extends User{
    public int working_hours;
    public int admin_id;
    public int library_code;

    public Librarian(int user_id, int admin_id, String phone, int salary, int working_hours, int library_code){
        super(user_id, phone, salary);
        this.admin_id = admin_id;
        this.working_hours = working_hours;
        this.library_code = library_code;
    }
}
