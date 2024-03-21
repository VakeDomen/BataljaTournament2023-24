import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

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

	public static String[] blueFleets;
	public static String[] cyanFleets;
	public static String[] greenFleets;
	public static String[] yellowFleets;

	public static String[] myPlanets =  new String[0];
	public static String[] friendlyPlanets = new String[0];
	public static String[] opp1Planets = new String[0];
	public static String[] opp2Planets = new String[0];


	public static String[] myFleets = new String[0];
	public static String[] friendlyFleets = new String[0];
	public static String[] opp1Fleets = new String[0];
	public static String[] opp2Fleets = new String[0];

	public static String friendlyColor;


	public static void main(String[] args) throws Exception {

		try {
			//Random rand = new Random(); // source of random for random moves
			while (true) {
				getGameState();

				
				//logToFile("TEST1: "+Arrays.toString(myPlanets)+"");
				if(myColor.equals("green")) {
					myPlanets = greenPlanets;
					myFleets = greenFleets;
					friendlyFleets = yellowFleets;
					opp1Fleets = blueFleets;
					opp2Fleets = cyanFleets;
					friendlyColor="yellow";
					opp1Planets = bluePlanets;
					opp2Planets = cyanPlanets;
					friendlyPlanets = yellowPlanets;
				}else if(myColor.equals("yellow")) {
					myPlanets = yellowPlanets;
					myFleets = yellowFleets;
					friendlyFleets = yellowFleets;
					opp1Fleets = blueFleets;
					opp2Fleets = cyanFleets;
					friendlyColor="green";
					opp1Planets = bluePlanets;
					opp2Planets = cyanPlanets;
					friendlyPlanets = greenPlanets;
				}else if(myColor.equals("blue")) {
					myPlanets = bluePlanets;
					myFleets = blueFleets;
					friendlyFleets = cyanFleets;
					opp1Fleets = greenFleets;
					opp2Fleets = yellowFleets;
					friendlyColor="cyan";
					opp1Planets = greenPlanets;
					opp2Planets = yellowPlanets;
					friendlyPlanets = cyanPlanets;
				}else {
					myPlanets = cyanPlanets;
					myFleets = cyanFleets;
					friendlyFleets = blueFleets;
					opp1Fleets = greenFleets;
					opp2Fleets = yellowFleets;
					friendlyColor="blue";
					opp1Planets = greenPlanets;
					opp2Planets = yellowPlanets;
					friendlyPlanets = bluePlanets;
				}
				//logToFile("TEST2: "+Arrays.toString(myPlanets)+"");
					
					//if(myPlanets.length > 0) {
						for(int i = 0; i < myPlanets.length; i++) {
							String[] myPlanet = myPlanets[i].split(" ");
								//int fleets = getTotalFleets(getClosestPlanet(myPlanet, neutralPlanets, true));
								//if(fleets < 0) {
									String[] closestPlanet = getClosestPlanet(myPlanet, false, true);
									int thisPlanetFleets = getTotalFleets(myPlanet);
									if((closestPlanet.length != 0) && thisPlanetFleets > 0) {
										int fleetsToSend = (int)((getTotalFleets(closestPlanet)*(-1))*1.5);
										System.out.println("A "+myPlanet[1]+" "+closestPlanet[1] + " "+fleetsToSend);
										logToFile("A "+myPlanet[1]+" "+closestPlanet[1] + " "+fleetsToSend);
									}else {
										logToFile("There exists no such planet.");
									}
								//}
							//logToFile("A "+myPlanet[1]+" "+getClosestPlanet(myPlanet, neutralPlanets)[1]);
						}
						 
					//}
				
				System.out.println("E");
			}
		} catch (Exception e) {
			logToFile("ERROR: "+e.getMessage()+"END");
			//logToFile(e.printStackTrace());
			logToFile(e.getMessage());
			e.printStackTrace();
		}
		fileOut.close();
		
	}

	public static int getTotalFleets(String[] planet) {
		int sum = 0;
		if(planet[6].equals(myColor) || planet[6].equals(friendlyColor)) {
			sum+=Integer.parseInt(planet[5]);
		}else {
			sum-=Integer.parseInt(planet[5]);
		}
		
		for(int i = 0; i < myFleets.length; i++) {
			if(planet[1].equals(myFleets[i].split(" ")[4])) {
				sum+=Integer.parseInt(myFleets[i].split(" ")[2]);
			}
		}
		for(int i = 0; i < friendlyFleets.length; i++) {
			if(planet[1].equals(friendlyFleets[i].split(" ")[4])) {
				sum+=Integer.parseInt(friendlyFleets[i].split(" ")[2]);
			}
		}
		for(int i = 0; i < opp1Fleets.length; i++) {
			if(planet[1].equals(opp1Fleets[i].split(" ")[4])) {
				sum-=Integer.parseInt(opp1Fleets[i].split(" ")[2]);
			}
		}
		for(int i = 0; i < opp2Fleets.length; i++) {
			if(planet[1].equals(opp2Fleets[i].split(" ")[4])) {
				sum-=Integer.parseInt(opp2Fleets[i].split(" ")[2]);
			}
		}

		return sum;
	}


	public static String[] getClosestPlanet(String[] origin, boolean isFriendly, boolean checkFleetCount) {
		String[] closestPlanet = new String[0];
		double distance = 999;
		double x1 = Double.parseDouble(origin[2]);
		double y1 = Double.parseDouble(origin[3]);
		//if(!isFriendly) {
			//Checks for closest opponent 1 planet
		for(int i = 0; i < opp1Planets.length; i++) {
			double x2 = Double.parseDouble(opp1Planets[i].split(" ")[2]);
			double y2 = Double.parseDouble(opp1Planets[i].split(" ")[3]);

			double localDistance = Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));

			//Dobi vse skupaj fleets, tako da dobi total fleets od planeta in temu deducira koliko fleetov produca na rundo * rund ki bo rablo da pride do planeta
			int fleets = getTotalFleets(opp1Planets[i].split(" "))-(int)((Double.parseDouble(opp1Planets[i].split(" ")[4]) * 10) * (((int)localDistance)/2));

			if((localDistance < distance) && ((fleets <= 0) || !checkFleetCount)) {
				distance = localDistance;
				closestPlanet = opp1Planets[i].split(" ");
			}

		}
		//Checks for closest opponent 2 planet
		for(int i = 0; i < opp2Planets.length; i++) {
			double x2 = Double.parseDouble(opp2Planets[i].split(" ")[2]);
			double y2 = Double.parseDouble(opp2Planets[i].split(" ")[3]);

			double localDistance = Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));

			int fleets = getTotalFleets(opp2Planets[i].split(" "))-(int)((Double.parseDouble(opp2Planets[i].split(" ")[4]) * 10) * (((int)localDistance)/2));

			if((localDistance < distance) && ((fleets <= 0) || !checkFleetCount)) {
				distance = localDistance;
				closestPlanet = opp2Planets[i].split(" ");
			}

		}
		//Checks for closest neutral planet
		for(int i = 0; i < neutralPlanets.length; i++) {
			double x2 = Double.parseDouble(neutralPlanets[i].split(" ")[2]);
			double y2 = Double.parseDouble(neutralPlanets[i].split(" ")[3]);

			double localDistance = Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));

			// && ((getTotalFleets(neutralPlanets[i].split(" ")) <= 0) || !checkFleetCount)
			if((localDistance < distance)) {
				distance = localDistance;
				closestPlanet = neutralPlanets[i].split(" ");
			}

		}

		/*
		//Work in progress....
		int eff = 999;
		if(neutralPlanets.length != 0 && Double.parseDouble(origin[4]) <= 0.5) {
			for(int i = 0; i < neutralPlanets.length; i++) {
				double x2 = Double.parseDouble(neutralPlanets[i].split(" ")[2]);
				double y2 = Double.parseDouble(neutralPlanets[i].split(" ")[3]);

				double localDistance = Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
				int turnsToGetThere = (int)localDistance/2;
				int localEff = (Integer.parseInt(neutralPlanets[i].split(" ")[5])/(int)((Double.parseDouble(origin[4]) * 10)/2)) *turnsToGetThere;
				if(localEff < eff) {
					eff = localEff;
					closestPlanet = neutralPlanets[i].split(" ");
				}

			}
		}
	 */

		//Checks for closest my planet
		for(int i = 0; i < myPlanets.length; i++) {
			double x2 = Double.parseDouble(myPlanets[i].split(" ")[2]);
			double y2 = Double.parseDouble(myPlanets[i].split(" ")[3]);

			double localDistance = Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));

			int fleets = getTotalFleets(myPlanets[i].split(" "))+(int)((Double.parseDouble(myPlanets[i].split(" ")[4]) * 10) * (((int)localDistance)/2));

			if((localDistance < distance) && ((fleets <= 0) || !checkFleetCount)) {
				distance = localDistance;
				closestPlanet = myPlanets[i].split(" ");
			}

		}

		//Checks for closest friendly planet
		for(int i = 0; i < friendlyPlanets.length; i++) {
			double x2 = Double.parseDouble(friendlyPlanets[i].split(" ")[2]);
			double y2 = Double.parseDouble(friendlyPlanets[i].split(" ")[3]);

			double localDistance = Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));

			int fleets = getTotalFleets(friendlyPlanets[i].split(" "))+(int)((Double.parseDouble(friendlyPlanets[i].split(" ")[4]) * 10) * (((int)localDistance)/2));

			if((localDistance < distance) && ((fleets <= 0) || !checkFleetCount)) {
				distance = localDistance;
				closestPlanet = friendlyPlanets[i].split(" ");
			}

		}
		//}
		return  closestPlanet;
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
			//logToFile(line);
			
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
				//logToFile(Arrays.toString(tokens));
				String planetName = Arrays.toString(tokens).replaceAll(",", "");
				planetName = planetName.substring(1, planetName.length()-1);
				if (tokens[6].equals("blue")) {
					bluePlanetsList.add(planetName);
				} 
				if (tokens[6].equals("cyan")) {
					cyanPlanetsList.add(planetName);
				} 
				if (tokens[6].equals("green")) {
					greenPlanetsList.add(planetName);
				} 
				if (tokens[6].equals("yellow")) {
					yellowPlanetsList.add(planetName);
				} 
				if (tokens[6].equals("null")) {
					neutralPlanetsList.add(planetName);
				} 
			} 
			if (firstLetter == 'F') {
				//logToFile(Arrays.toString(tokens));
				String fleet = Arrays.toString(tokens).replaceAll(",", "");
				fleet = fleet.substring(1, fleet.length()-1);
				if (tokens[7].equals("blue")) {
					blueFleetsList.add(fleet);
				} 
				if (tokens[7].equals("cyan")) {
					cyanFleetsList.add(fleet);
				} 
				if (tokens[7].equals("green")) {
					greenFleetsList.add(fleet);
				} 
				if (tokens[7].equals("yellow")) {
					yellowFleetsList.add(fleet);
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
	}
}
