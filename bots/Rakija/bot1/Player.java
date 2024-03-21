import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Player {
	static BufferedWriter fileOut = null;

	/*
	 * GAME DATA
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

	public static HashMap<String, Integer> xCoordPlanetHash = new HashMap<>();
	public static HashMap<String, Integer> yCoordPlanetHash = new HashMap<>();
	public static HashMap<String, Float> sizePlanetHash = new HashMap<>();
	public static HashMap<String, Integer> sizeFleetPlanetHash = new HashMap<>();

	public static HashMap<String, String> fleetOriginHash = new HashMap<>();
	public static HashMap<String, String> fleetDestinationHash = new HashMap<>();
	public static HashMap<String, String> fleetOwnerHash = new HashMap<>();
	public static HashMap<String, Integer> fleetSizeHash = new HashMap<>();
	public static HashMap<String, Integer> fleetTurnsRemainingHash = new HashMap<>();

	public static void main(String[] args) throws Exception {

		try {
			/*
			 * Main game loop
			 */
			while (true) {
				// Receive game state
				getGameState();

				// Attack logic
				String[] myPlanets = getMyPlanets();
				String[] myTeamsPlanets = getMyTeamsPlanets();

				// Check if the player is the random bot
				boolean isRandomBot = false;
				if (isItTheRandomBot(myTeamsPlanets) && myTeamsPlanets.length > 2) {
					isRandomBot = true;
				}

				for (int i = 0; i < myPlanets.length; i++) {
					String myPlanet = myPlanets[i];

					String closestEnemyPlanet = null;
					String[] EnemyPlanetsSortedByDistance = findClosestEnemyPlanets(myPlanet);
					for (int j = 0; j < EnemyPlanetsSortedByDistance.length; j++) {
						if (!isGoingToBeConquered(EnemyPlanetsSortedByDistance[j], 10)) {
							closestEnemyPlanet = EnemyPlanetsSortedByDistance[j];
							break;
						}
					}
					if (isRandomBot) {
						if (closestEnemyPlanet != null) {
							System.out.println("A " + myPlanet + " " + closestEnemyPlanet);
							continue;
						}
					}
					if (sizeFleetPlanetHash.get(myPlanet) > 2000 && closestEnemyPlanet != null) {
						System.out.println("A " + myPlanet + " " + closestEnemyPlanet);
						continue;
					}
					if (!isMyPlanetBeingAttacked(myPlanet)) {
						if (isNeutralPlanet(closestEnemyPlanet) && closestEnemyPlanet != null) {
							String[] neutralPlanetsInRadius = findClosestNeutralPlanets(myPlanet, (int) calculateDistance(myPlanet, closestEnemyPlanet) + 20);
							neutralPlanetsInRadius = sortNeutralPlanetsByFleetSize(neutralPlanetsInRadius);
							// if (neutralPlanetsInRadius.length > 0 && sizeFleetPlanetHash.get(myPlanet) >
							// sizeFleetPlanetHash
							// .get(neutralPlanetsInRadius[0])) {
							if (neutralPlanetsInRadius.length > 0 && sizePlanetHash.get(myPlanet) < sizePlanetHash.get(closestEnemyPlanet)) {
								System.out.println("A " + myPlanet + " " + neutralPlanetsInRadius[0] + " "
										+ sizeFleetPlanetHash.get(neutralPlanetsInRadius[0]));
							} else if (closestEnemyPlanet != null) {
								System.out.println("A " + myPlanet + " " + closestEnemyPlanet);
							}
						} else if (closestEnemyPlanet != null) {
							String[] planetsInRadius = findMyClosestPlanets(myPlanet, 40);
							String reinforcementTarget = findClosestAttackedPlanet(planetsInRadius, myPlanet);
							if (reinforcementTarget != null && calculateDistance(myPlanet,
									closestEnemyPlanet) > calculateDistance(myPlanet, reinforcementTarget)) {
								System.out.println("A " + myPlanet + " " + reinforcementTarget + " "
										+ sizeFleetPlanetHash.get(myPlanet));
							} else if (closestEnemyPlanet != null) {
								System.out.println("A " + myPlanet + " " + closestEnemyPlanet);
							}
						}
					} else {
						// closestEnemyPlanet = findClosestEnemyPlanet(myPlanet);
						if (sizeFleetPlanetHash.get(myPlanet) > sizePlanetHash.get(myPlanet) * 500) {
							if (closestEnemyPlanet != null
									&& sizeFleetPlanetHash.get(myPlanet) > sizeFleetPlanetHash.get(closestEnemyPlanet)
											+ 10 + isGoingToBeConqueredForEnemies(closestEnemyPlanet)) {
								// Attack the closest enemy planet
								System.out.println("A " + myPlanet + " " + closestEnemyPlanet + " "
										+ sizeFleetPlanetHash.get(myPlanet));
							}
						}

					}
				}
				System.out.println("E");
			}
		} catch (Exception e) {
			logToFile("ERROR: " + e.getMessage());
			e.printStackTrace();
		}
		fileOut.close();
	}

	private static String[] sortNeutralPlanetsByFleetSize(String[] neutralPlanetsToBeSorted) {
		// Sort the neutral planets by fleet size using a custom comparator
		Arrays.sort(neutralPlanetsToBeSorted, new Comparator<String>() {
			@Override
			public int compare(String planet1, String planet2) {
				float size1 = sizePlanetHash.get(planet1);
				float size2 = sizePlanetHash.get(planet2);
				return Float.compare(size1, size2);
			}
		});

		// Return the sorted array of neutral planets
		return neutralPlanetsToBeSorted;
	}

	private static boolean isItTheRandomBot(String[] myPlanets) throws IOException {
		int counter = 0;
		for (int i = 0; i < myPlanets.length; i++) {
			if (isMyPlanetBeingAttacked(myPlanets[i])) {
				counter++;
			}
		}
		if (counter > (double) myPlanets.length / 100 * 80) {
			return true;
		}
		return false;
	}

	private static String findClosestAttackedPlanet(String[] myPlanets, String currentPlanet) throws IOException {
		String closestAttackedPlanet = null;
		double minDistance = Double.MAX_VALUE;
		String[] myTeamsPlanets = getMyTeamsPlanets();

		// Iterate through each of the player's planets
		for (String myTeamPlanet : myTeamsPlanets) {
			// Check if the planet is being attacked
			if (isMyPlanetBeingAttacked(myTeamPlanet)) {
				// Calculate distance to the attacked planet
				double distance = calculateDistance(currentPlanet, myTeamPlanet);
				// Update closest planet if closer than current minimum
				if (distance < minDistance) {
					minDistance = distance;
					closestAttackedPlanet = myTeamPlanet;
				}
			}
		}

		return closestAttackedPlanet;
	}

	// Method to get the player's team planets
	private static String[] getMyTeamsPlanets() {
		if (myColor.equals("blue") || myColor.equals("cyan")) {
			return concatArrays(bluePlanets, cyanPlanets);
		} else if (myColor.equals("green") || myColor.equals("yellow")) {
			return concatArrays(greenPlanets, yellowPlanets);
		} else {
			return new String[0]; // Default case for neutral player
		}
	}

	private static String[] findMyClosestPlanets(String myPlanet, int threshold) throws IOException {
		HashMap<String, Double> planetDistanceMap = new HashMap<>();
		String[] myPlanets = getMyPlanets();

		// Calculate distance for each enemy planet and store in the map
		for (String myplanet : myPlanets) {
			double distance = calculateDistance(myplanet, myPlanet);
			if (distance < threshold) {
				planetDistanceMap.put(myplanet, distance);
			}
		}

		// Sort the enemy planets based on distance
		String[] sortedMyPlanets = planetDistanceMap.entrySet().stream()
				.sorted(Map.Entry.comparingByValue())
				.map(Map.Entry::getKey)
				.toArray(String[]::new);

		return sortedMyPlanets;
	}

	// Method to find the closest enemy planet to a given planet
	private static String findClosestEnemyPlanet(String myPlanet) throws IOException {
		String closestEnemyPlanet = null;
		double minDistance = Double.MAX_VALUE;
		String[] enemyPlanets = getEnemyPlanets();

		for (int i = 0; i < enemyPlanets.length; i++) {
			double distance = calculateDistance(myPlanet, enemyPlanets[i]);
			if (distance < minDistance) {
				minDistance = distance;
				closestEnemyPlanet = enemyPlanets[i];
			}
		}

		return closestEnemyPlanet;
	}

	private static String[] findClosestEnemyPlanets(String myPlanet) throws IOException {
		HashMap<String, Double> planetDistanceMap = new HashMap<>();
		String[] enemyPlanets = getEnemyPlanets();

		// Calculate distance for each enemy planet and store in the map
		for (String enemyPlanet : enemyPlanets) {
			double distance = calculateDistance(myPlanet, enemyPlanet);
			planetDistanceMap.put(enemyPlanet, distance);
		}

		// Sort the enemy planets based on distance
		String[] sortedEnemyPlanets = planetDistanceMap.entrySet().stream()
				.sorted(Map.Entry.comparingByValue())
				.map(Map.Entry::getKey)
				.toArray(String[]::new);

		return sortedEnemyPlanets;
	}

	private static String[] findClosestNeutralPlanets(String myPlanet, int threshold) throws IOException {
		HashMap<String, Double> planetDistanceMap = new HashMap<>();
		String[] neutralPlanetsToBeSorted = neutralPlanets;

		// Calculate distance for each enemy planet and store in the map
		for (String enemyPlanet : neutralPlanetsToBeSorted) {
			double distance = calculateDistance(myPlanet, enemyPlanet);
			if (distance < threshold && !isGoingToBeConquered(enemyPlanet, 100)) {
				planetDistanceMap.put(enemyPlanet, distance);
			}
		}

		// Sort the enemy planets based on distance
		String[] sortedNeutralPlanets = planetDistanceMap.entrySet().stream()
				.sorted(Map.Entry.comparingByValue())
				.map(Map.Entry::getKey)
				.toArray(String[]::new);

		return sortedNeutralPlanets;
	}

	// Method to calculate the Euclidean distance between two planets
	private static double calculateDistance(String planet1, String planet2) throws IOException {
		// Extract coordinates of both planets
		int[] coordinates1 = extractCoordinates(planet1);
		int[] coordinates2 = extractCoordinates(planet2);

		// Calculate Euclidean distance
		return Math
				.sqrt(Math.pow(coordinates2[0] - coordinates1[0], 2) + Math.pow(coordinates2[1] - coordinates1[1], 2));
	}

	// Method to extract coordinates from planet string
	private static int[] extractCoordinates(String planet) throws IOException {
		// Extract x and y coordinates from HashMaps
		int x = xCoordPlanetHash.get(planet);
		int y = yCoordPlanetHash.get(planet);
		return new int[] { x, y };
	}

	// Method to get the player's planets
	private static String[] getMyPlanets() {
		if (myColor.equals("blue")) {
			return bluePlanets;
		} else if (myColor.equals("cyan")) {
			return cyanPlanets;
		} else if (myColor.equals("green")) {
			return greenPlanets;
		} else if (myColor.equals("yellow")) {
			return yellowPlanets;
		} else {
			return new String[0]; // Default case for neutral player
		}
	}

	// Method to get the enemy planets
	private static String[] getEnemyPlanets() {
		if (myColor.equals("blue")) {
			return concatArrays(greenPlanets, yellowPlanets, neutralPlanets);
		} else if (myColor.equals("cyan")) {
			return concatArrays(greenPlanets, yellowPlanets, neutralPlanets);
		} else if (myColor.equals("green")) {
			return concatArrays(bluePlanets, cyanPlanets, neutralPlanets);
		} else if (myColor.equals("yellow")) {
			return concatArrays(bluePlanets, cyanPlanets, neutralPlanets);
		} else {
			return concatArrays(bluePlanets, cyanPlanets, greenPlanets, yellowPlanets); // Default case for neutral
																						// player
		}
	}

	// Method to check if a planet is being attacked
	private static boolean isMyPlanetBeingAttacked(String myPlanet) {
		String[] enemyFleets = getEnemyFleets();
		for (int i = 0; i < enemyFleets.length; i++) {
			String enemyFleet = enemyFleets[i];
			if (fleetDestinationHash.get(enemyFleet).equals(myPlanet) && enemyFleets.length > 0) {
				return true;
			}
		}
		return false;
	}

	// Method to check if a planet is neutral
	private static boolean isNeutralPlanet(String planet) {
		return Arrays.asList(neutralPlanets).contains(planet);
	}

	// Method to check if a planet is going to be concquered based on the amount of
	// fleets going towards it
	private static boolean isGoingToBeConquered(String planet, int turnThreshold) {
		String[] myFleets = getMyFleets();
		String[] enemyFleets = getEnemyFleets();
		int totalFleetSize = 0;
		for (int i = 0; i < myFleets.length; i++) {
			String myFleet = myFleets[i];
			if (fleetDestinationHash.get(myFleet).equals(planet) && fleetTurnsRemainingHash.get(myFleet) <= turnThreshold) {
				totalFleetSize += fleetSizeHash.get(myFleet);
			}

		}
		for (int i = 0; i < enemyFleets.length; i++) {
			String enemyFleet = enemyFleets[i];
			if (fleetDestinationHash.get(enemyFleet).equals(planet)) {
				totalFleetSize -= fleetSizeHash.get(enemyFleet);
			}
		}
		return totalFleetSize > sizeFleetPlanetHash.get(planet);
	}

	private static int isGoingToBeConqueredForEnemies(String planet) {
		String[] myFleets = getMyFleets();
		String[] enemyFleets = getEnemyFleets();
		int totalFleetSize = 0;
		for (int i = 0; i < myFleets.length; i++) {
			String myFleet = myFleets[i];
			if (fleetDestinationHash.get(myFleet).equals(planet) && fleetTurnsRemainingHash.get(myFleet) <= 10) {
				totalFleetSize -= fleetSizeHash.get(myFleet);
			}

		}
		for (int i = 0; i < enemyFleets.length; i++) {
			String enemyFleet = enemyFleets[i];
			if (fleetDestinationHash.get(enemyFleet).equals(planet) && fleetTurnsRemainingHash.get(enemyFleet) <= 10) {
				totalFleetSize += fleetSizeHash.get(enemyFleet);
			}
		}
		return totalFleetSize;
	}

	// Method to get the enemy planets
	private static String[] getEnemyFleets() {
		if (myColor.equals("blue")) {
			return concatArrays(greenFleets, yellowFleets);
		} else if (myColor.equals("cyan")) {
			return concatArrays(greenFleets, yellowFleets);
		} else if (myColor.equals("green")) {
			return concatArrays(blueFleets, cyanFleets);
		} else if (myColor.equals("yellow")) {
			return concatArrays(blueFleets, cyanFleets);
		} else {
			return concatArrays(blueFleets, cyanFleets, greenFleets, yellowFleets); // Default case for neutral player
		}
	}

	// Method to get my teams fleets
	private static String[] getMyFleets() {
		if (myColor.equals("blue") || myColor.equals("cyan")) {
			return concatArrays(blueFleets, cyanFleets);
		} else if (myColor.equals("green") || myColor.equals("yellow")) {
			return concatArrays(greenFleets, yellowFleets);
		} else {
			return new String[0]; // Default case for neutral player
		}
	}

	// Method to concatenate arrays
	private static String[] concatArrays(String[]... arrays) {
		LinkedList<String> result = new LinkedList<>();
		for (String[] array : arrays) {
			for (String element : array) {
				result.add(element);
			}
		}
		return result.toArray(new String[0]);
	}

	/**
	 * This function should be used instead of System.out.print for
	 * debugging, since the System.out.println is used to send
	 * commands to the game
	 * 
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
	 * This function should be called at the start of each turn to obtain
	 * information about the current state of the game.
	 * The data received includes details about planets and fleets, categorized by
	 * color and type.
	 *
	 * This version of the function uses dynamic lists to store data about planets
	 * and fleets for each color,
	 * accommodating for an unknown quantity of items. At the end of data
	 * collection, these lists are converted into fixed-size
	 * arrays for consistent integration with other parts of the program.
	 *
	 * Feel free to modify and extend this function to enhance the parsing of game
	 * data to your needs.
	 *
	 * @throws NumberFormatException if parsing numeric values from the input fails.
	 * @throws IOException           if an I/O error occurs while reading input.
	 */
	public static void getGameState() throws NumberFormatException, IOException {
		BufferedReader stdin = new BufferedReader(
				new java.io.InputStreamReader(System.in));
		/*
		 * - this is where we will store the data recieved from the game,
		 * - Since we don't know how many planets/fleets each player will
		 * have, we are using lists.
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
		 * read the input from the game and
		 * parse it (get data from the game)
		 ********************************
		 * - game is telling us about the state of the game (who ows planets
		 * and what fleets/attacks are on their way).
		 * - The game will give us data line by line.
		 * - When the game only gives us "S", this is a sign
		 * that it is our turn and we can start calculating out turn.
		 * - NOTE: some things like parsing of fleets(attacks) is not implemented
		 * and you should do it yourself
		 */
		String line = "";
		/*
		 * Loop until the game signals to start playing the turn with "S"
		 */
		while (!(line = stdin.readLine()).equals("S")) {
			/*
			 * - save the data we recieve to the log file, so you can see what
			 * data is recieved form the game (for debugging)
			 */
			logToFile(line);

			String[] tokens = line.split(" ");
			char firstLetter = line.charAt(0);
			/*
			 * U <int> <int> <string>
			 * - Universe: Size (x, y) of playing field, and your color
			 */
			if (firstLetter == 'U') {
				universeWidth = Integer.parseInt(tokens[1]);
				universeHeight = Integer.parseInt(tokens[2]);
				myColor = tokens[3];
			}
			/*
			 * P <int> <int> <int> <float> <int> <string>
			 * - Planet: Name (number), position x, position y,
			 * planet size, army size, planet color (blue, cyan, green, yellow or null for
			 * neutral)
			 */
			if (firstLetter == 'P') {
				String plantetName = tokens[1];

				int x = Integer.parseInt(tokens[2]);
				int y = Integer.parseInt(tokens[3]);

				// Store x and y coordinates of the planet
				xCoordPlanetHash.put(plantetName, x);
				yCoordPlanetHash.put(plantetName, y);
				sizePlanetHash.put(plantetName, Float.parseFloat(tokens[4]));
				sizeFleetPlanetHash.put(plantetName, Integer.parseInt(tokens[5]));
				if (tokens[6].equals("blue")) {
					bluePlanetsList.add(plantetName);
				}
				if (tokens[6].equals("cyan")) {
					cyanPlanetsList.add(plantetName);
				}
				if (tokens[6].equals("green")) {
					greenPlanetsList.add(plantetName);
				}
				if (tokens[6].equals("yellow")) {
					yellowPlanetsList.add(plantetName);
				}
				if (tokens[6].equals("null")) {
					neutralPlanetsList.add(plantetName);
				}
			}

			if (firstLetter == 'F') {
				/*
				 * F <int> <int> <int> <int> <int> <int> <string>
				 * - Fleet: name, fleet size, origin planet, destination planet, current turn,
				 * number of turns needed, owner
				 */
				String fleetName = tokens[1];
				int fleetSize = Integer.parseInt(tokens[2]);
				String fleetSourcePlanet = tokens[3];
				String fleetDestinationPlanet = tokens[4];
				int turnsRemaining = Integer.parseInt(tokens[6]) - Integer.parseInt(tokens[5]);
				String fleetOwner = tokens[7];

				fleetOriginHash.put(fleetName, fleetSourcePlanet);
				fleetDestinationHash.put(fleetName, fleetDestinationPlanet);
				fleetOwnerHash.put(fleetName, fleetOwner);
				fleetSizeHash.put(fleetName, fleetSize);
				fleetTurnsRemainingHash.put(fleetName, turnsRemaining);

				if (fleetOwner.equals("blue")) {
					blueFleetsList.add(fleetName);
				}
				if (fleetOwner.equals("cyan")) {
					cyanFleetsList.add(fleetName);
				}
				if (fleetOwner.equals("green")) {
					greenFleetsList.add(fleetName);
				}
				if (fleetOwner.equals("yellow")) {
					yellowFleetsList.add(fleetName);
				}

			}

		}
		/*
		 * - override data from previous turn
		 * - convert the lists into fixed size arrays
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
