import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
//import java.util.Random;

public class Player {
	static BufferedWriter fileOut = null;
	
	/*
		GAME DATA
	*/
	public static int universeWidth;
	public static int universeHeight;
	public static String myColor;
	
	public static String[] bluePlanets;
	public static String[] cyanPlanets;
	public static String[] greenPlanets;
	public static String[] yellowPlanets;
	public static String[] neutralPlanets;

	public static String[] bluePlanets2;
	public static String[] cyanPlanets2;
	public static String[] greenPlanets2;
	public static String[] yellowPlanets2;
	public static String[] neutralPlanets2;

	public static String[] blueFleets;
	public static String[] cyanFleets;
	public static String[] greenFleets;
	public static String[] yellowFleets;


	public static void main(String[] args) throws Exception {

		try {
			//Random rand = new Random(); // source of random for random moves

			/*
				**************
				Main game loop
				**************
			  	- each iteration of the loop is one turn.
			  	- this will loop until we stop playing the game
			  	- we will be stopped if we die/win or if we crash
			*/
			while (true) {
				/*
					- at the start of turn we first recieve data
					about the universe from the game.
					- data will be loaded into the static variables of
					this class
				*/
				getGameState();

				/*
				 	*********************************
					LOGIC: figure out what to do with
					your turn
					*********************************
					- current plan: attack randomly
				*/
				int targetArrLength = neutralPlanets.length+4;
				String[] myPlanets = new String[0];
				String targetPlayer = "";
				String[] myWholePlanets = new String[0];

				/*
					- get my planets based on my color
					- select a random other color as the target player 
				*/
				if (myColor.equals("blue")) {
					myPlanets = bluePlanets;
					myWholePlanets= bluePlanets2;
					targetPlayer = "green yellow neutral";
					targetArrLength = greenPlanets.length + yellowPlanets.length + neutralPlanets.length;		
				} 

				if (myColor.equals("cyan")) {
					myPlanets = cyanPlanets;
					myWholePlanets= cyanPlanets2;
					targetPlayer = "green yellow neutral";
					targetArrLength = greenPlanets.length + yellowPlanets.length + neutralPlanets.length;
				} 

				if (myColor.equals("green")) {
					myPlanets = greenPlanets;
					myWholePlanets= greenPlanets2;
					targetPlayer = "cyan blue neutral";
					targetArrLength = cyanPlanets.length + bluePlanets.length + neutralPlanets.length;
				} 
				
				if (myColor.equals("yellow")) {
					myPlanets = yellowPlanets;
					myWholePlanets= yellowPlanets2;
					targetPlayer = "cyan blue neutral";
					targetArrLength = cyanPlanets.length + bluePlanets.length + neutralPlanets.length;
				}

				/*
					- based on the color selected as the target,
					find the planets of the targeted player
				*/
				//String[] targetPlayerPlanets = new String[0];
				String[] targetWholePlanets = new String[targetArrLength];
				String[] targetPlayerPlanets = new String[targetArrLength];
				logToFile("bluePlanets.length:"+bluePlanets.length);
				logToFile("cyanPlanets.length:"+cyanPlanets.length);
				logToFile("greenPlanets.length:"+greenPlanets.length);
				logToFile("yellowPlanets.length:"+yellowPlanets.length);
				
				if (targetPlayer.equals("cyan blue neutral")) {
					arrayCopy(bluePlanets, 0, targetPlayerPlanets, 0, bluePlanets.length);
					arrayCopy(cyanPlanets, 0, targetPlayerPlanets, bluePlanets.length, cyanPlanets.length);
					arrayCopy(neutralPlanets, 0, targetPlayerPlanets, bluePlanets.length+cyanPlanets.length, neutralPlanets.length);

					arrayCopy(bluePlanets2, 0, targetWholePlanets, 0, bluePlanets.length);
					arrayCopy(cyanPlanets2, 0, targetWholePlanets, bluePlanets.length, cyanPlanets.length);
					arrayCopy(neutralPlanets2, 0, targetWholePlanets, bluePlanets.length+cyanPlanets.length, neutralPlanets.length);
				}

				if (targetPlayer.equals("green yellow neutral")) {
					arrayCopy(greenPlanets, 0, targetPlayerPlanets, 0, greenPlanets.length);
					arrayCopy(yellowPlanets, 0, targetPlayerPlanets, greenPlanets.length, yellowPlanets.length);
					arrayCopy(neutralPlanets, 0, targetPlayerPlanets, greenPlanets.length+yellowPlanets.length, neutralPlanets.length);

					arrayCopy(greenPlanets2, 0, targetWholePlanets, 0, greenPlanets.length);
					arrayCopy(yellowPlanets2, 0, targetWholePlanets, greenPlanets.length, yellowPlanets.length);
					arrayCopy(neutralPlanets2, 0, targetWholePlanets, greenPlanets.length+yellowPlanets.length, neutralPlanets.length);
				}

				// if (targetPlayer.equals("green")) {
				// 	targetPlayerPlanets = greenPlanets;
				// 	targetWholePlanets= greenPlanets2;
				// }

				// if (targetPlayer.equals("yellow")) {
				// 	targetPlayerPlanets = yellowPlanets;
				// 	targetWholePlanets= yellowPlanets2;
				// }

				// if (targetPlayer.equals("neutral")) {
				// 	targetPlayerPlanets = neutralPlanets;
				// 	targetWholePlanets= neutralPlanets2;
				// }
				/*
					- if the target player has any planets
					and if i have any planets (we could only have 
					fleets) attack a random planet of the target 
					from each of my planets
				*/
				if (targetPlayerPlanets.length > 0 && myPlanets.length > 0) {
					logToFile("if");
					for (int i = 0 ; i < myPlanets.length ; i++) {
						logToFile("outer for");
						logToFile("myPlanets[i]: " + myWholePlanets[i]);
						 String myPlanet = myPlanets[i];
						 String myPlanetParsing= myWholePlanets[i];
						 String target="";
						 double minDistance = 1000000000;
						 double myX=Double.parseDouble(myPlanetParsing.split(" ")[2]);
						double myY=Double.parseDouble(myPlanetParsing.split(" ")[3]);
						for (int j = 0 ; j < targetPlayerPlanets.length ; j++) {
							logToFile("inner for");
							logToFile("target planets[j]"+targetWholePlanets[j]);
							 String targetParsing= targetWholePlanets[j];
							 double targetX=Double.parseDouble(targetParsing.split(" ")[2]);
							 double targetY=Double.parseDouble(targetParsing.split(" ")[3]);
							 if(calculateDistance(myX, myY, targetX, targetY) < minDistance){
							 	minDistance = calculateDistance(myX, myY, targetX, targetY);
							 	target = targetPlayerPlanets[j];
							}
						
						}
						
						System.out.println("A " + myPlanet + " " + target);
					}
				}
				
				/*
					- send a hello message to your teammate bot :)
					- it will recieve it form the game next turn (if the bot parses it)
				 */
				System.out.println("M Hello");

				/*
				  	- E will end my turn. 
				  	- you should end each turn (if you don't the game will think you timed-out)
				  	- after E you should send no more commands to the game
				 */
				System.out.println("E");
			}
		} catch (Exception e) {
			logToFile("ERROR: ");
			logToFile(e.getMessage());
			e.printStackTrace();
		}
		fileOut.close();
		
	}


	/**
	 * This function should be used instead of System.out.print for 
	 * debugging, since the System.out.println is used to send 
	 * commands to the game
	 * @param line String you want to log into the log file.
	 * @throws IOException
	 */
	public static void logToFile(String line) throws IOException {
		if (fileOut == null) {
			FileWriter fstream = new FileWriter("Igralec.log");
			fileOut = new BufferedWriter(fstream);
		}
		if (line.charAt(line.length() - 1) != '\n') {
			line += "\n";
		}
		fileOut.write(line);
		fileOut.flush();
	}


	/**
	 * This function should be called at the start of each turn to obtain information about the current state of the game.
	 * The data received includes details about planets and fleets, categorized by color and type.
	 *
	 * This version of the function uses dynamic lists to store data about planets and fleets for each color,
	 * accommodating for an unknown quantity of items. At the end of data collection, these lists are converted into fixed-size
	 * arrays for consistent integration with other parts of the program.
	 *
	 * Feel free to modify and extend this function to enhance the parsing of game data to your needs.
	 *
	 * @throws NumberFormatException if parsing numeric values from the input fails.
	 * @throws IOException if an I/O error occurs while reading input.
	 */
	public static void getGameState() throws NumberFormatException, IOException {
		BufferedReader stdin = new BufferedReader(
			new java.io.InputStreamReader(System.in)
		); 
		/*
			- this is where we will store the data recieved from the game,
			- Since we don't know how many planets/fleets each player will 
			have, we are using lists.
		*/ 
		LinkedList<String> bluePlanetsList = new LinkedList<>();
		LinkedList<String> cyanPlanetsList = new LinkedList<>();
		LinkedList<String> greenPlanetsList = new LinkedList<>();
		LinkedList<String> yellowPlanetsList = new LinkedList<>();
		LinkedList<String> neutralPlanetsList = new LinkedList<>();

		LinkedList<String> blueFleetsList = new LinkedList<>();
		LinkedList<String> cyanFleetsList = new LinkedList<>();
		LinkedList<String> greenFleetsList = new LinkedList<>();
		LinkedList<String> yellowFleetsList = new LinkedList<>();
		LinkedList<String> bluePlanetsList2 = new LinkedList<>();

		LinkedList<String> cyanPlanetsList2 = new LinkedList<>();
		LinkedList<String> greenPlanetsList2 = new LinkedList<>();
		LinkedList<String> yellowPlanetsList2 = new LinkedList<>();
		LinkedList<String> neutralPlanetsList2 = new LinkedList<>();

		
		/*
			********************************
			read the input from the game and
			parse it (get data from the game)
			********************************
			- game is telling us about the state of the game (who ows planets
			and what fleets/attacks are on their way). 
			- The game will give us data line by line. 
			- When the game only gives us "S", this is a sign
			that it is our turn and we can start calculating out turn.
			- NOTE: some things like parsing of fleets(attacks) is not implemented 
			and you should do it yourself
		*/
		String line = "";
		/*
			Loop until the game signals to start playing the turn with "S"
		*/ 
		while (!(line = stdin.readLine()).equals("S")) {
			/* 
				- save the data we recieve to the log file, so you can see what 
				data is recieved form the game (for debugging)
			*/ 
			logToFile(line); 

			String wholePlanet=line;
			String[] tokens = line.split(" ");
			char firstLetter = line.charAt(0);
			/*
			 	U <int> <int> <string> 						
				- Universe: Size (x, y) of playing field, and your color
			*/
			if (firstLetter == 'U') {
				universeWidth = Integer.parseInt(tokens[1]);
				universeHeight = Integer.parseInt(tokens[2]);
				myColor = tokens[3];
			} 
			/*
				P <int> <int> <int> <float> <int> <string> 	
				- Planet: Name (number), position x, position y, 
				planet size, army size, planet color (blue, cyan, green, yellow or null for neutral)
			*/

			

			if (firstLetter == 'P') {
				String plantetName = tokens[1];
				if (tokens[6].equals("blue")) {
					bluePlanetsList.add(plantetName);
					bluePlanetsList2.add(wholePlanet);
				} 
				if (tokens[6].equals("cyan")) {
					cyanPlanetsList.add(plantetName);
					cyanPlanetsList2.add(wholePlanet);
				} 
				if (tokens[6].equals("green")) {
					greenPlanetsList.add(plantetName);
					greenPlanetsList2.add(wholePlanet);
				} 
				if (tokens[6].equals("yellow")) {
					yellowPlanetsList.add(plantetName);
					yellowPlanetsList2.add(wholePlanet);
				} 
				if (tokens[6].equals("null")) {
					neutralPlanetsList.add(plantetName);
					neutralPlanetsList2.add(wholePlanet);
				} 
			} 
		}
		/*
			- override data from previous turn
			- convert the lists into fixed size arrays
		*/ 
		bluePlanets = bluePlanetsList.toArray(new String[0]);
		cyanPlanets = cyanPlanetsList.toArray(new String[0]);
		greenPlanets = greenPlanetsList.toArray(new String[0]);
		yellowPlanets = yellowPlanetsList.toArray(new String[0]);
		neutralPlanets = neutralPlanetsList.toArray(new String[0]);
		blueFleets = blueFleetsList.toArray(new String[0]);
		cyanFleets = cyanFleetsList.toArray(new String[0]);
		greenFleets = greenFleetsList.toArray(new String[0]);
		yellowFleets = yellowFleetsList.toArray(new String[0]);
		bluePlanets2 = bluePlanetsList2.toArray(new String[0]);
		cyanPlanets2= cyanPlanetsList2.toArray(new String[0]);
		greenPlanets2 = greenPlanetsList2.toArray(new String[0]);
		yellowPlanets2 = yellowPlanetsList2.toArray(new String[0]);
		neutralPlanets2 = neutralPlanetsList2.toArray(new String[0]);
	}
	public static double calculateDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
	public static void arrayCopy(String[] source, int sourceStart, String[] destination, int destStart, int length) {
        for (int i = 0; i < length; i++) {
            destination[destStart + i] = source[sourceStart + i];
        }
    }

}