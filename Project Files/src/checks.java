package Final_DBMS_Project.src.main.java.DB_project;

public class checks {
    checks(){

    }
    //returns true if string may contains number but no special character
   public static boolean validStringWithNumbers(String inputString){
        String specialCharactersString = "!@#$%&*()'+,-./:;<=>?[]^_`{|}";
        for (int i=0; i < inputString.length() ; i++) {
            char ch = inputString.charAt(i);
            if (specialCharactersString.contains(Character.toString(ch))) {
                //contains charaters
                return false;
            } else if (i == inputString.length() - 1)
                //does not contain special characters
                return true;
        }
        return true;
    }
    //returns true if string does not contains numbers and special characters
    public static boolean validStringWithoutNumbers(String str) {
        str = str.toLowerCase();
        char[] charArray = str.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char ch = charArray[i];
            if(ch == ' '){

            }
            else if (!(ch >= 'a' && ch <= 'z')) {
                return false;
            }
        }
        return true;
    }
    //returns true if a phone number is in valid format
    public static boolean validPhone(String number) {
        if(number.matches("^03[0-4][0-9]-[0-9]{7}$"))
            return true;
        else
            return false;
    }

    //returs true if email is in valid format
    public static boolean validEmail(String email)
    {
        email = email.trim();

        if(email == null || email.equals(""))
            return false;

        if(!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"))
            return false;

        return true;
    }

    //returns true if a string contains only integers
    public static boolean validInteger (String text){
        if (!text.matches("[0-9]+")){
            return false;
        }
        return true;
    }
}
