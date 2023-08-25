package Final_DBMS_Project.src.main.java.DB_project;

public class pract {
    public static void main(String args[]) {
        System.out.println(validInteger("123"));
    }
    static boolean validInteger (String text){
        if (!text.matches("[0-9]+")){
            return false;
        }
        return true;
    }
}
