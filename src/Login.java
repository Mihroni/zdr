import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Login {
    private static Scanner x = new Scanner(System.in);

    public static boolean verifyLogin(String username, String password, String filepath) {
        boolean found = false;
        String tempUsername;
        String tempPassword;
        try {
            x = new Scanner(new File(filepath));
            x.useDelimiter("[,\n]");
            while (x.hasNext() && !found) {
                tempUsername = x.next();
                tempPassword = x.next();

                if (tempUsername.trim().equals(username.trim()) && tempPassword.trim().equals(password.trim())) {
                    found = true;
                }
            }
            x.close();

        } catch (Exception e) {
            System.out.println(e);
        }

        return found;
    }
    public static String players() {
        try {
            return Files.readString(Path.of("order.txt"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String words() {
        try {
            return Files.readString(Path.of("word.txt"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String winner() {
        try {
            return Files.readString(Path.of("winner.txt"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static boolean isFileEmpty() {
        File file = new File("word.txt");
        return (file.length() == 0);
    }

    public static boolean verifyUsername(String filepath, String username) throws IOException {
        boolean found = false;
        String tempUsername;

        try {
            x = new Scanner(new File(filepath));
            x.useDelimiter("[,\n]");
            while (x.hasNext() && !found) {
                tempUsername = x.next();


                if (tempUsername.trim().equals(username.trim())) {
                    found = true;
                }
            }
            x.close();

        } catch (Exception e) {
            System.out.println(e);
        }

        return found;
    }

}
