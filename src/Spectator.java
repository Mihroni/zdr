public class Spectator {
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
}
