import java.util.Arrays;

public class WordGenerator {
    static int mistakes = 0 ;

    static String getWord() {
        //TODO search file get random word
        return "sandwich";
    }


    static String maskWord(String word) {
        char[] letters = word.toCharArray();
        StringBuilder wordToReturn = new StringBuilder();
        for (int i = 0; i < letters.length; i++) {
            if (i != 0 && i != letters.length - 1) {
                letters[i] = '*';
            }
            wordToReturn.append(letters[i]);
        }
        return wordToReturn.toString();
    }

    public static String guess(String word, String guess, String maskedWord) {
        boolean changedValue = false;
        boolean winner = true;
        char[] letters = word.toCharArray();
        char[] guessLetter = guess.toCharArray();
        char[] arrayToReturn = maskedWord.toCharArray();
        StringBuilder result = new StringBuilder();
        if (guess.length() < 1) {
            return "Invalid guess";
        } else {
            for (int i = 0; i <letters.length ; i++) {
                if (letters[i] == guessLetter[0]){
                    arrayToReturn[i] = guessLetter[0];
                    changedValue = true;
                }
            }
            if (!changedValue){
                mistakes++;
            }
        }
        for (int i = 0; i <arrayToReturn.length ; i++) {
            result.append(arrayToReturn[i]);
            if (arrayToReturn[i] == '*'){
                winner = false;
            }
        }
        if (mistakes == 6 ){
            Login.players();
            mistakes = 0;
            return "You Lose";
        }
        if (winner){
            return "You win";
        }
        System.out.println("mistakes"  + mistakes);
        return result.toString();
    }
}
