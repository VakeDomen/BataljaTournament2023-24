import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Player {
    public static int universeWidth;
    public static int universeHeight;
    static BufferedWriter fileOut = null;
    public static String myColor;
    public static String teammateColor; //how to get the colour of our opponent ?????

    public static void main(String[] args) {
        GeneticPlayer geneticPlayer = new GeneticPlayer();
        try {
            while (true) {
                State currentState = new State();
                if (!currentState.grayPlanets.isEmpty()) {
                    geneticPlayer.earlyGame(currentState); // Make early game decisions
                } else {
                    geneticPlayer.endGame(currentState); // Handle end game scenario
                }
                //geneticPlayer.calculateFutureState(currentState);
                //currentState.categorizePlanets();

                System.out.println("M " + myColor); // Make sure currentState.myColor is the correct field
                System.out.println("E");
            }
        } catch (Exception e) {
            logToFile(e.getLocalizedMessage());
            logToFile(e.getMessage());
            for (int i = 0; i < e.getStackTrace().length; i++) {
                logToFile(String.valueOf(e.getStackTrace()[i]));
            }
        }
    }



    public static void logToFile(String line) {
        try {
            if (fileOut == null) {
                FileWriter fstream = new FileWriter("Player.log");
                fileOut = new BufferedWriter(fstream);
            }
            if (line.charAt(line.length() - 1) != '\n') {
                line += "\n";
            }
            fileOut.write(line);
            fileOut.flush();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
