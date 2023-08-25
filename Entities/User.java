package Final_DBMS_Project.src.main.java.DB_project.Entities;

public abstract class User {
    public int user_id;
    public String phone;
    public int salary;

    User(int user_id, String phone, int salary){
        this.user_id = user_id;
        this.phone = phone;
        this.salary = salary;
    }
}
