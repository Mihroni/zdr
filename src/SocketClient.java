import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SocketClient extends JFrame implements ActionListener, Runnable {
    JTextArea textArea = new JTextArea();
    JScrollPane jp = new JScrollPane(textArea);
    JTextField input_Text = new JTextField();

    Socket socket;
    BufferedReader bufferedReader;
    PrintWriter printWriter;
    String name;
    String password;
    String firstPlayerName = ServerThread.firstPlayer;
    String secondPlayerName = ServerThread.secondPlayer;
    static String word = "";
    String maskedWord = "";
    boolean isFirstTurn = true;
    boolean isOver = false;
    Map<String, Integer> playerMap = new HashMap<>();

    public SocketClient() {
        super("Hangman");
        setFont(new Font("Arial Black", Font.PLAIN, 12));
        setForeground(new Color(173, 148, 157));
        setBackground(new Color(78, 167, 201));
        textArea.setToolTipText("Chat History");
        textArea.setForeground(new Color(255, 255, 255));
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));

        textArea.setBackground(new Color(131, 127, 127));

        getContentPane().add(jp, "Center");
        input_Text.setToolTipText("Enter your Message");
        input_Text.setForeground(new Color(0, 0, 0));
        input_Text.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        input_Text.setBackground(new Color(230, 230, 250));

        getContentPane().add(input_Text, "South");
        setSize(325, 411);
        setVisible(true);

        input_Text.requestFocus(); //Place cursor at run time, work after screen is shown

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        input_Text.addActionListener(this); //Event registration
    }

    public void SaveUser(String credentials, String filepath) {
        try {
            FileWriter writer = new FileWriter(filepath, true);
            writer.write(credentials);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void serverConnection() {
        try {
            String IP = "127.0.0.1";
            String filepath = "/Users/Dell/Desktop/login/names.txt";
            socket = new Socket(IP, 1234);
            String[] options = new String[]{"Log in", "Sign up"};
            int response = JOptionPane.showOptionDialog(null, "Hello, traveler", "Hangman",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, options, options[0]);

            if (response == 0) {
                Login(filepath, socket);
            } else {
                CreateUser(filepath, socket);

            }
        } catch (Exception e) {
            System.out.println(e + " Socket Connection error");
        }
    }

    public void Pw(Socket sk, String name) {
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(sk.getInputStream()));

            printWriter = new PrintWriter(sk.getOutputStream(), true);
            printWriter.println(name);
            new Thread(this).start();
        } catch (Exception e) {
            System.out.println(e + " Socket Connection error");
        }

    }

    public void CreateUser(String filepath, Socket sk) {
        name = JOptionPane.showInputDialog(this, "Create a new account", JOptionPane.INFORMATION_MESSAGE);
        password = JOptionPane.showInputDialog(this, "Please enter password", JOptionPane.INFORMATION_MESSAGE);
        boolean check;
        try {
            check = Login.verifyUsername(filepath, name);
            if (check) {
                JOptionPane.showMessageDialog(null, "Username already exists please enter e new one", "Error Window Title", JOptionPane.ERROR_MESSAGE);
                CreateUser(filepath, sk);
            } else {

                String credentials = "," + name + "," + password;
                SaveUser(credentials, filepath);
                Pw(sk, name);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    public void Login(String filepath, Socket sk) {
        try {
            name = JOptionPane.showInputDialog(this, "Please enter a nickname", JOptionPane.INFORMATION_MESSAGE);
            password = JOptionPane.showInputDialog(this, "Please enter password", JOptionPane.INFORMATION_MESSAGE);
            boolean verified = Login.verifyLogin(name, password, filepath);
            int i = 1;
            while (!verified) {
                if (!verified) {
                    JLabel messageLabel = new JLabel("<html><body><p style='width: 500px;'>" + "Invalid Password!" + "</p></body></html>");
                    Timer timer = new Timer(10000,
                            event -> SwingUtilities.getWindowAncestor(messageLabel).dispose());

                    i++;
                    timer.setRepeats(false);

                    timer.start();
                    JOptionPane.showMessageDialog(null, messageLabel, "Error Window Title", JOptionPane.ERROR_MESSAGE);

                    name = JOptionPane.showInputDialog(this, "Please enter a name", JOptionPane.INFORMATION_MESSAGE);

                    password = JOptionPane.showInputDialog(this, "Please enter password", JOptionPane.INFORMATION_MESSAGE);
                    verified = Login.verifyLogin(name, password, filepath);
                } else {
                    verified = false;
                }
            }
            Pw(sk, name);
        } catch (Exception e) {
            System.out.println(e + "--> Client run fail");
        }
    }


    public static void main(String[] args) {
        new SocketClient().serverConnection();
        long startTime = System.nanoTime();
        while (true) {
            if (WordGenerator.hasGuessed()                                                     ){
                System.out.println("reset");
                startTime = System.nanoTime();
            }
            if (System.nanoTime() - startTime > 3000000000L) {
                WordGenerator.mistakes++;
                startTime = System.nanoTime();
                System.out.println(WordGenerator.mistakes);
            }
        }
    }

    @Override
    public void run() {
        String data;

        try {
            while ((data = bufferedReader.readLine()) != null) {
                textArea.append(data + "\n");
                textArea.setCaretPosition(textArea.getText().length());
            }
        } catch (Exception e) {
            System.out.println(e + "--> Client run fail");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (checkForWinner(Login.winner()) != null && checkForWinner(Login.winner()).equals(firstPlayerName)){
            isOver = true;
            for (int i = 0; i <20 ; i++) {
                printWriter.println("the game is over! Winner is " + firstPlayerName);
            }
        }else if (checkForWinner(Login.winner()) != null && checkForWinner(Login.winner()).equals(secondPlayerName)){
            isOver = true;
            for (int i = 0; i <20 ; i++) {
                printWriter.println("the game is over! Winner is " + secondPlayerName);
            }
        }
        if (!isOver) {
            decidePlayer(Login.players(), Login.winner());
            if (Login.isFileEmpty()) {
                word = "";
                maskedWord = "";
            }
            String data = input_Text.getText();
            if (Login.isFileEmpty() && name.equals(firstPlayerName)) {
                word = data;
                maskedWord = WordGenerator.maskWord(word);
                try {
                    FileWriter writer = new FileWriter("word.txt", true);
                    writer.write(word);
                    writer.write("/");
                    writer.write(maskedWord);
                    writer.close();
                } catch (IOException f) {
                    System.out.println(f);
                }
                printWriter.println("the word you have to guss is " + maskedWord);
            } else if (name.equals(firstPlayerName)) {
                printWriter.println("this is a hint " + data);
            } else if (name.equals(secondPlayerName) && isFirstTurn) {
                getWord(Login.words());
                isFirstTurn = false;
            } else if (name.equals(secondPlayerName)) {
                printWriter.println(data);
                System.out.println("Word is " + word);
                System.out.println("Masked data is " + maskedWord);
                maskedWord = WordGenerator.guess(word, data, maskedWord);
                if (maskedWord.equals("You win")) {
                    deleteFileContent();
                    clearBoard();
                    resetBoard();
                    word = "";
                    isFirstTurn = true;
                    try {
                        FileWriter writer = new FileWriter("winner.txt", true);
                        writer.write(firstPlayerName);
                        writer.write("/");
                        writer.close();
                        isFirstTurn = true;
                    } catch (IOException f) {
                        System.out.println(f);
                    }
                } else if (maskedWord.equals("You Lose")) {
                    deleteFileContent();
                    clearBoard();
                    resetBoard();
                    word = "";
                    try {
                        FileWriter writer = new FileWriter("winner.txt", true);
                        writer.write(secondPlayerName);
                        writer.write("/");
                        writer.close();
                    } catch (IOException f) {
                        System.out.println(f);
                    }
                }
                printWriter.println(maskedWord);
            }
            input_Text.setText("");
        }
    }

    public void decidePlayer(String file, String winnerFile) {
        String[] players = file.split("/");
        String[] winners = winnerFile.split("/");
        if (winners.length% 2 == 1) {
            firstPlayerName = players[0];
            secondPlayerName = players[1];
        }else {
            firstPlayerName = players[1];
            secondPlayerName = players[0];
        }
        if (playerMap.isEmpty()) {
            playerMap.put(firstPlayerName, 0);
            playerMap.put(secondPlayerName, 0);
        }
        System.out.println(firstPlayerName);
        System.out.println(secondPlayerName);
    }

    public void getWord(String file) {
        String[] words = file.split("/");
        word = words[0];
        maskedWord = words[1];
    }

    public String checkForWinner(String winnerFile){
        String[] winners = winnerFile.split("/");
        int firstPlayerWins = 0;
        int secondPlayerWins = 0;
        if (winners.length>0) {
            for (int i = 0; i < winners.length; i++) {
                if (winners[i].equals(firstPlayerName)) {
                    firstPlayerWins++;
                } else if (winners[i].equals(secondPlayerName)) {
                    secondPlayerWins++;
                }
            }
        }
        if (firstPlayerWins == 2){
            return firstPlayerName;
        }else if (secondPlayerWins == 2){
            return secondPlayerName;
        }
        return null;
    }

    private void clearBoard() {
        for (int i = 0; i < 20; i++) {
            printWriter.println("");
        }
    }

    private void resetBoard() {
        String nameSwap = firstPlayerName;
        firstPlayerName = secondPlayerName;
        secondPlayerName = nameSwap;
        word = "";
    }

    public static void deleteFileContent() {
        Path path = Paths.get("word.txt");
        try {
            Files.write(path, "".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}