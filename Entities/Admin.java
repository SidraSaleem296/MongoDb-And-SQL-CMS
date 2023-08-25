package Final_DBMS_Project.src.main.java.DB_project.Entities;

public class Admin extends User{
    public char shift;

    public Admin(int user_id, String phone, int salary, char shift){
        super(user_id, phone, salary);
        this.shift = shift;
    }
}
