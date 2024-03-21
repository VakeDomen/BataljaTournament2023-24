import java.io.*;
import java.util.LinkedList;

public class Player {
	static BufferedWriter fileOut = null;

	public static int universeWidth;
	public static int universeHeight;
	public static String player;
	public static String teammate;
	public static String[] enemies = new String[2];

	public static Planet[] bluePlanets;
	public static Planet[] cyanPlanets;
	public static Planet[] greenPlanets;
	public static Planet[] yellowPlanets;
	public static Planet[] neutralPlanets;
	public static Planet[] playerPlanets;
	public static Planet[] universe;

	public static Fleet[] blueFleets;
	public static Fleet[] cyanFleets;
	public static Fleet[] greenFleets;
	public static Fleet[] yellowFleets;
	public static Fleet[] playerFleets;
	public static Fleet[] enemyFleets;

	public static void main(String[] args) throws Exception {
		try {
			// The main loop of the game, it will automatically be terminated if player dies or the game stops.
			while(true) {
				// Getting the current game data and writing in inside this class variables.
				getGameState();

				// BOT LOGIC:
				Planet[] chosenPlanets = getChosenPlanets();

				for(int i = 0; i < chosenPlanets.length; i++) {
					Planet playerPlanet = playerPlanets[i];
					Planet chosenPlanet = chosenPlanets[i];

					int fleetSize = chosenPlanet.estimateFleetSize(playerPlanet);
					if(fleetSize > 0) System.out.println("A " + playerPlanet.name + " " + chosenPlanets[i].name + " " + fleetSize);
				}

				// End the turn.
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
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		// Storing number of planets and lists for all types inside linked lists.

		LinkedList<Planet> bluePlanetsList = new LinkedList<>();
		LinkedList<Planet> cyanPlanetsList = new LinkedList<>();
		LinkedList<Planet> greenPlanetsList = new LinkedList<>();
		LinkedList<Planet> yellowPlanetsList = new LinkedList<>();
		LinkedList<Planet> neutralPlanetsList = new LinkedList<>();

		LinkedList<Fleet> blueFleetsList = new LinkedList<>();
		LinkedList<Fleet> cyanFleetsList = new LinkedList<>();
		LinkedList<Fleet> greenFleetsList = new LinkedList<>();
		LinkedList<Fleet> yellowFleetsList = new LinkedList<>();

		// Getting data from the game, by reading its inputs.
		// Command "S" is used as a sign that it is player's turn.

		// Reading the first game input.
		String line = reader.readLine();
		// Checking if this implies player's turn.
		boolean isPlayersTurn = line.equals("S");

		// Updating the game data, until player's turn is reached.
		while(!isPlayersTurn) {
			// Saving the game data inside the file.
			logToFile(line);

			// Dividing data from the line into "tokens".
			String[] tokens = line.split(" ");

			char command = line.charAt(0);
			String data;

			switch(command) {
				case 'U':
					// U <int> <int> <string>
					/*
						Universe:
						Size of the playing field,
						Player's color
					 */

					universeWidth = Integer.parseInt(tokens[1]);
					universeHeight = Integer.parseInt(tokens[2]);
					player = tokens[3];

					teammate = getTeammate(player);
					enemies = getEnemy(player);

					break;
				case 'P':
					// P <int> <int> <int> <float> <int> <string>
					/*
						Planet:
						Name (number),
						Position x,
						Position y,
						Planet size,
						Army size,
						Planet color (blue, cyan, green, yellow or null for neutral)
					*/

					data = getData("planet", tokens);
					Planet planet = new Planet(data);

					if(planet.color.equals("blue")) bluePlanetsList.add(planet);
					if(planet.color.equals("cyan")) cyanPlanetsList.add(planet);
					if(planet.color.equals("green")) greenPlanetsList.add(planet);
					if(planet.color.equals("yellow")) yellowPlanetsList.add(planet);
					if(planet.color.equals("null")) neutralPlanetsList.add(planet);

					break;
				case 'F':
					// F <int> <int> <int> <int> <int> <int> <string>
					/*
						Fleet:
						Name (number),
						Fleet size
						Origin planet
						Destination planet
						Current turn
						Number of needed turns
						Planet color (owner - may be null for neutral)
					 */

					data = getData("fleet", tokens);
					Fleet fleet = new Fleet(data);

					if(fleet.color.equals("blue")) blueFleetsList.add(fleet);
					if(fleet.color.equals("cyan")) cyanFleetsList.add(fleet);
					if(fleet.color.equals("green")) greenFleetsList.add(fleet);
					if(fleet.color.equals("yellow")) yellowFleetsList.add(fleet);
				default:
			}

			// Updating "line" and "isPlayersTurn" variables.
			line = reader.readLine();
			isPlayersTurn = line.equals("S");
		}

		// Updating the data from the previous turn.
		// Converting linked lists into arrays with fixed size.
		bluePlanets = bluePlanetsList.toArray(new Planet[0]);
		cyanPlanets = cyanPlanetsList.toArray(new Planet[0]);
		greenPlanets = greenPlanetsList.toArray(new Planet[0]);
		yellowPlanets = yellowPlanetsList.toArray(new Planet[0]);
		neutralPlanets = neutralPlanetsList.toArray(new Planet[0]);

		playerPlanets = getPlanets(player);

		blueFleets = blueFleetsList.toArray(new Fleet[0]);
		cyanFleets = cyanFleetsList.toArray(new Fleet[0]);
		greenFleets = greenFleetsList.toArray(new Fleet[0]);
		yellowFleets = yellowFleetsList.toArray(new Fleet[0]);

		playerFleets = getGroupFleets(player);
		enemyFleets = getGroupFleets(enemies[0]);
	}

	public static String getTeammate(String type) {
		String teammate = "";

		if(type.equals("green")) teammate = "yellow";
		if(type.equals("yellow")) teammate = "green";
		if(type.equals("blue")) teammate = "cyan";
		if(type.equals("cyan")) teammate = "blue";

		return teammate;
	}

	public static String[] getEnemy(String type) {
		String[] enemies = new String[2];

		if(type.equals("green") || type.equals("yellow")) enemies = new String[]{"blue", "cyan"};
		if(type.equals("blue") || type.equals("cyan")) enemies = new String[]{"green", "yellow"};

		return enemies;
	}

	public static Planet[] getPlanets(String type) {
		Planet[] playerPlanets = new Planet[0];

		if(type.equals("blue")) playerPlanets = bluePlanets;
		if(type.equals("cyan")) playerPlanets = cyanPlanets;
		if(type.equals("green")) playerPlanets = greenPlanets;
		if(type.equals("yellow")) playerPlanets = yellowPlanets;

		return playerPlanets;
	}

	public static boolean getPlanetsStatus(String type) {
		boolean status = true;

		if((type.equals("green") || type.equals("yellow")) && (greenPlanets.length == 0 && yellowPlanets.length == 0)) status = false;
		if((type.equals("blue") || type.equals("cyan")) && (bluePlanets.length == 0 && cyanPlanets.length == 0)) status = false;

		if(neutralPlanets.length > 0) status = true;

		return status;
	}

	public static Planet[] getUniverse() {
		Planet[][] universeMatrix = new Planet[][]{bluePlanets, cyanPlanets, greenPlanets, yellowPlanets, neutralPlanets};

		LinkedList<Planet> universeList = new LinkedList<>();

		for(int i = 0; i < universeMatrix.length; i++) {
			Planet[] planets = universeMatrix[i];
			for(int j = 0; j < planets.length; j++) universeList.add(planets[j]);
		}

		Planet[] universe = universeList.toArray(new Planet[0]);
		return universe;
	}

	public static Fleet[] getFleets(String type) {
		Fleet[] fleets = new Fleet[0];

		if(type.equals("blue")) fleets = blueFleets;
		if(type.equals("cyan")) fleets = cyanFleets;
		if(type.equals("green")) fleets = greenFleets;
		if(type.equals("yellow")) fleets = yellowFleets;

		return fleets;
	}

	public static Fleet[] getGroupFleets(String type) {
		LinkedList<Fleet> fleetsList = new LinkedList<>();

		Fleet[] fleets = getFleets(type);
		for(int i = 0; i < fleets.length; i++) fleetsList.add(fleets[i]);

		Fleet[] teammateFleets = getFleets(getTeammate(type));
		for(int i = 0; i < teammateFleets.length; i++) fleetsList.add(teammateFleets[i]);

		Fleet[] groupFleets = fleetsList.toArray(new Fleet[0]);
		return groupFleets;
	}

	public static Planet[] getChosenPlanets() {
		universe = getUniverse();

		LinkedList<Planet> chosenPlanetsList = new LinkedList<>();

		if(playerPlanets.length > 0 && getPlanetsStatus(enemies[0])) {
			for(int i = 0; i < playerPlanets.length; i++) {
				Planet planet = playerPlanets[i];
				Planet[] sortedUniverse = planet.sortUniverse(universe);

				double[] sortedDistances = planet.sortDistances(universe);

				Planet chosenTargetPlanet = null;
				boolean isChosen = false;

				for(int j = 0; j < sortedUniverse.length; j++) if(!isChosen) {
					Planet targetPlanet = sortedUniverse[j];
					double targetDistance = sortedDistances[j];

					if(targetPlanet.isPlayer()) {
						boolean isLost = targetPlanet.isLost();

						if(isLost && targetDistance <= 60) {
							chosenTargetPlanet = targetPlanet;
							isChosen = true;
						}
					}

					else {
						boolean isTaken = targetPlanet.isTaken(planet);
						boolean isUnbalancedSize = targetPlanet.isUnbalancedSize(planet);

						if(!isTaken && !isUnbalancedSize) {
							chosenTargetPlanet = targetPlanet;
							isChosen = true;
						}
					}
				}

				if(chosenTargetPlanet == null) {
					for(int j = 0; j < sortedUniverse.length; j++) if(!isChosen) {
						Planet targetPlanet = sortedUniverse[j];

						if(!targetPlanet.isPlayer()) {
							chosenTargetPlanet = targetPlanet;
							isChosen = true;
						}
					}
				}

				if(chosenTargetPlanet == null) chosenTargetPlanet = sortedUniverse[0];

				chosenPlanetsList.add(chosenTargetPlanet);
			}
		}

		Planet[] chosenPlanets = chosenPlanetsList.toArray(new Planet[0]);
		return chosenPlanets;
	}

	public static String getData(String type, String[] tokens) {
		String data = "";
		String[] properties = new String[0];

		switch(type) {
			case "planet":
				properties = new String[]{"name", "x", "y", "size", "army", "color"};
				break;
			case "fleet":
				properties = new String[]{"name", "size", "origin", "destination", "currentTurns", "requiredTurns", "color"};
				break;
			default:
		}

		for(int i = 0; i < properties.length; i++) data += properties[i] + "=" + tokens[i + 1] + "\n";
		return data;
	}
}

class Planet extends Player {
	public int name, x, y, army;
	public float size;
	public String color;
	private final int takenValue;

	Planet(String data) {
		initialize(data);
		takenValue = (int) (army + size * 50);
	}

	private void initialize(String data) {
		String[] properties = data.split("\n");

		for(int i = 0; i < properties.length; i++) {
			String[] property = properties[i].split("=");

			String key = property[0];
			String value = property[1];

			int integerValue;
			float floatValue;

			switch(key) {
				case "name":
					integerValue = Integer.parseInt(value);
					name = integerValue;

					break;
				case "x":
					integerValue = Integer.parseInt(value);
					x = integerValue;

					break;
				case "y":
					integerValue = Integer.parseInt(value);
					y = integerValue;

					break;
				case "size":
					floatValue = Float.parseFloat(value);
					size = floatValue;

					break;
				case "army":
					integerValue = Integer.parseInt(value);
					army = integerValue;

					break;
				case "color":
					color = value;
					break;
				default:
			}
		}
	}

	public boolean isPlayer() {
		boolean status = false;
		if(color.equals(player) || color.equals(teammate)) status = true;

		return status;
	}

	public double calculateDistance(Planet targetPlanet) {
		int differenceX = (int) Math.pow((targetPlanet.x - x), 2);
		int differenceY = (int) Math.pow((targetPlanet.y - y), 2);

		double distance = Math.sqrt(differenceX + differenceY);
		return distance;
	}

	public double[] sortDistances(Planet[] universe) {
		double[] distances = new double[universe.length];

		for(int i = 0; i < universe.length; i++) distances[i] = calculateDistance(universe[i]);

		double temp;

		for(int i = 0; i < distances.length; i++) {
			for(int j = i + 1; j < distances.length; j++) {
				if(distances[i] > distances[j]) {
					temp = distances[i];
					distances[i] = distances[j];
					distances[j] = temp;
				}
			}
		}

		return distances;
	}

	public Planet[] sortUniverse(Planet[] universe) {
		double[] distances = sortDistances(universe);

		Planet[] sortedUniverse = new Planet[distances.length - 1];
		int index = 0;

		for(int i = 1; i < distances.length; i++) {
			boolean targetPlanetAdded = false;

			for(int j = 0; j < universe.length; j++) {
				double currentDistance = calculateDistance(universe[j]);

				if(!targetPlanetAdded && distances[i] == currentDistance) {
					targetPlanetAdded = true;

					sortedUniverse[index] = universe[j];
					index++;
				}
			}
		}

		return sortedUniverse;
	}

	public boolean isTaken(Planet planet) {
		boolean status = false;

		int playerFleetsSum = getCloseFleetsSum(planet, playerFleets);
		int enemyFleetsSum = getCloseFleetsSum(planet, enemyFleets);

		playerFleetsSum -= enemyFleetsSum;

		if(takenValue <= playerFleetsSum) status = true;

		return status;
	}

	public boolean isLost() {
		boolean status = false;

		int playerFleetsSum = getDestinationFleetsSum(playerFleets);
		int enemyFleetsSum = getDestinationFleetsSum(enemyFleets);

		enemyFleetsSum -= playerFleetsSum;
		if(takenValue <= enemyFleetsSum) status = true;

		return status;
	}

	public boolean isUnbalancedSize(Planet planet) {
		boolean isUnbalanced = false;

		int planetArmy = planet.army;
		int turns = 0;

		while(planetArmy <= takenValue) {
			planetArmy += (int) (planet.size * 10);
			turns++;
		}

		if(turns > 25) isUnbalanced = true;

		return isUnbalanced;
	}

	public int estimateFleetSize(Planet planet) {
		int fleetSize = 0;
		int calculatedFleetSize = calculateFleetSize(planet);

		if(takenValue <= planet.army) fleetSize = calculatedFleetSize;

		else {
			double distance = calculateDistance(planet);
			Fleet[] enemyDestinationFleets = getDestinationFleets(enemyFleets);

			if(enemyDestinationFleets.length > 0) {
				int sum = 0, index = 0;
				int turns = 0;

				while(sum < army && index < enemyDestinationFleets.length) {
					sum += enemyDestinationFleets[index].size;

					int arrival = enemyDestinationFleets[index].requiredTurns - enemyDestinationFleets[index].currentTurn;
					turns += arrival;

					index++;
				}

				if(distance >= turns) fleetSize = planet.army;
			}
		}

		return fleetSize;
	}

	private int calculateFleetSize(Planet planet) {
		int fleetSize = takenValue;

		int playerFleetsSum = getDestinationFleetsSum(playerFleets);
		int enemyFleetsSum = getDestinationFleetsSum(enemyFleets);

		fleetSize -= playerFleetsSum;
		fleetSize += enemyFleetsSum;

		if(fleetSize < planet.army / 2) fleetSize = planet.army / 2;

		return fleetSize;
	}

	private Fleet[] getDestinationFleets(Fleet[] fleets) {
		LinkedList<Fleet> destinationFleetsList = new LinkedList<>();
		for(int i = 0; i < fleets.length; i++) if(fleets[i].destination == name) destinationFleetsList.add(fleets[i]);

		Fleet[] destinationFleets = destinationFleetsList.toArray(new Fleet[0]);
		return destinationFleets;
	}

	private int getDestinationFleetsSum(Fleet[] fleets) {
		int fleetsSum = 0;
		for(int i = 0; i < fleets.length; i++) if(fleets[i].destination == name) fleetsSum += fleets[i].size;

		return fleetsSum;
	}

	private int getCloseFleetsSum(Planet planet, Fleet[] fleets) {
		int fleetsSum = 0;

		double distance = calculateDistance(planet);
		Fleet[] destinationFleets = getDestinationFleets(fleets);

		if(destinationFleets.length > 0) {
			int sum = 0, index = 0;
			int turns = 0;

			while(sum < army && index < destinationFleets.length) {
				sum += destinationFleets[index].size;

				int arrival = destinationFleets[index].requiredTurns - destinationFleets[index].currentTurn;
				turns += arrival;

				index++;
			}

			if(distance >= turns) fleetsSum = planet.army;
		}

		return fleetsSum;
	}
}

class Fleet {
	int name, size, origin, destination, currentTurn, requiredTurns;
	String color;

	Fleet(String data) {
		initialize(data);
	}

	private void initialize(String data) {
		String[] properties = data.split("\n");

		for(int i = 0; i < properties.length; i++) {
			String[] property = properties[i].split("=");

			String key = property[0];
			String value = property[1];

			int integerValue;

			switch(key) {
				case "name":
					integerValue = Integer.parseInt(value);
					name = integerValue;

					break;
				case "size":
					integerValue = Integer.parseInt(value);
					size = integerValue;

					break;
				case "origin":
					integerValue = Integer.parseInt(value);
					origin = integerValue;

					break;
				case "destination":
					integerValue = Integer.parseInt(value);
					destination = integerValue;

					break;
				case "currentTurn":
					integerValue = Integer.parseInt(value);
					currentTurn = integerValue;

					break;
				case "requiredTurns":
					integerValue = Integer.parseInt(value);
					requiredTurns = integerValue;

					break;
				case "color":
					color = value;
					break;
				default:
			}
		}
	}
}