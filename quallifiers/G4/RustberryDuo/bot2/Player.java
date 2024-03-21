import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player {
	static BufferedWriter fileOut = null;

	/* ========== * GLOBAL VARIABLES* ========== */
	// Game data:
	public static int universeWidth;
	public static int universeHeight;
	public static String myColor;

	// Eyes hurt, but it works xD - Hash map of all planets mapped by color:
	public static Map<String, List<Map<String, Object>>> planets = new HashMap<>();

	// List of our own planets: 
	public static List<Map<String, Object>> myPlanetsList = new ArrayList<>();

	// List of all enemy planets including the neutral planets:
	public static List<Map<String, Object>> enemyPlanetsList = new ArrayList<>();

	// Global counters to count how many turns have passed:
	// From start:
	public static int currentTurnNumber = 0;
	// From the moment all neutral planets are gone:
	public static int currentTurnNumberAfterNeutral = 0;

	// List of all the neutral planets our bot has attacked:
	public static List<Map<String, Object>> attackedNeutralPlanets = new ArrayList<>();
	// ============================================================================

	public static void main(String[] args) throws Exception {
		try {
			while (true) {
				planets.put("blue", new ArrayList<>());
				planets.put("cyan", new ArrayList<>());
				planets.put("green", new ArrayList<>());
				planets.put("yellow", new ArrayList<>());
				planets.put("neutral", new ArrayList<>());

				getGameState();
				setPlanetLists();

				/* ========== * LOGIC, STRATEGY & ATTACK * ========== */

				if (myPlanetsList.size() > 0 && enemyPlanetsList.size() > 0) {
					if (planets.get("neutral").size() <= 0 && currentTurnNumberAfterNeutral % 20 == 0) {
						attactWithFurthestPlanet(myPlanetsList, enemyPlanetsList);
					} else {
						for (Map<String, Object> myPlanet : myPlanetsList) {

							if (!checkTurn(myPlanet)) {
								continue;
							}
							Map<String, Object> target = findClosestPlanet(myPlanet, enemyPlanetsList);
							attack(myPlanet, target);
							
						}
					}
				}
				currentTurnNumber++;

				clearLists();
				System.out.println("E");
			}
		} catch (Exception e) {
			logToFile("ERROR: ");
			logToFile(e.getMessage());
			e.printStackTrace();
		}
		fileOut.close();
	}

	// TODO: tweak this whole function
	// ======================= ATTACK WITH FURTHEST PLANET ========================
	public static void attactWithFurthestPlanet(List<Map<String, Object>> myPlanetsF,
			List<Map<String, Object>> enemyPlanetsF) {
		int[] distances = new int[myPlanetsF.size()];
		int index = 0;
		for (Map<String, Object> myplanet : myPlanetsF) {
			int distance = Integer.MIN_VALUE;
			for (Map<String, Object> enemyplanet : enemyPlanetsF) {
				int tempDistance = calculateDistance(myplanet, enemyplanet);
				if (tempDistance < distance) {
					distance = tempDistance;
				}
			}
			distances[index] = distance;
			index++;
		}
		int maxMinDistance = 0;
		for (int i = 1; i < distances.length; i++) {
			if (distances[i] > maxMinDistance)
				maxMinDistance = i;
		}
		Map<String, Object> targetFurthestPlanet = myPlanetsF.get(0);
		int furthestDistance = Integer.MIN_VALUE;
		for (Map<String, Object> enemyPlanet : enemyPlanetsF) {
			int tempdistance = calculateDistance(myPlanetsF.get(maxMinDistance), enemyPlanet);
			if (tempdistance > furthestDistance) {
				furthestDistance = tempdistance;
				targetFurthestPlanet = enemyPlanet;
			}
		}
		System.out.println("A " + myPlanetsF.get(maxMinDistance).get("name") + " " + targetFurthestPlanet.get("name")
				+ " " + (Integer.parseInt(targetFurthestPlanet.get("armySize").toString()) + 1));
	}
	// ============================================================================

	// TODO: tweak attack function, add another parameter for army size
	// ================================== ATTACK ==================================
	public static void attack(Map<String, Object> myPlanet, Map<String, Object> target) throws IOException {
		try {
			if (checkIfNeutral(target)) {
				logToFile("my neutral planet is " + target);
				//attackedNeutralPlanets.add(target);
				System.out.println("M " + target.get("name").toString() + " " + target.get("x").toString() + " "
						+ target.get("y").toString() + " " + target.get("size").toString() + " "
						+ target.get("armySize").toString() + " " + target.get("color").toString());
				logToFile("my planet is " + myPlanet);
			}
		} catch (Exception e) {
			logToFile("crash" + e.getMessage());
		}
		int myArmy = Integer.parseInt(myPlanet.get("armySize").toString());
		int targetArmy = Integer.parseInt(target.get("armySize").toString());
		if (myArmy > targetArmy) // {
			System.out.println("A " + myPlanet.get("name") + " " + target.get("name") + " " + (targetArmy + 1));
		
	}
	// ============================================================================

	// ============================== CHECK IF NEUTRAL ============================
	public static boolean checkIfNeutral(Map<String, Object> planet) {
		if (planet.get("color").equals("null")) {
			return true;
		}
		return false;
	}
	// ============================================================================

	// TODO: tweak check turn function
	// =============================== CHECK TURN =================================
	public static boolean checkTurn(Map<String, Object> myPlanet) {
		if (myPlanet.get("size").equals("0.1")) {
			if (currentTurnNumber % 10 == 0)
				return true;
		}
		if (myPlanet.get("size").equals("0.2")) {
			if (currentTurnNumber % 9 == 0)
				return true;
		}
		if (myPlanet.get("size").equals("0.3")) {
			if (currentTurnNumber % 8 == 0)
				return true;
		}
		if (myPlanet.get("size").equals("0.4")) {
			if (currentTurnNumber % 7 == 0)
				return true;
		}
		if (myPlanet.get("size").equals("0.5")) {
			if (currentTurnNumber % 6 == 0)
				return true;
		}
		if (myPlanet.get("size").equals("0.6")) {
			if (currentTurnNumber % 5 == 0)
				return true;
		}
		if (myPlanet.get("size").equals("0.7")) {
			if (currentTurnNumber % 4 == 0)
				return true;
		}
		if (myPlanet.get("size").equals("0.8")) {
			if (currentTurnNumber % 3 == 0)
				return true;
		}
		if (myPlanet.get("size").equals("0.9")) {
			if (currentTurnNumber % 2 == 0)
				return true;
		}
		if (myPlanet.get("size").equals("1.0")) {
			if (currentTurnNumber % 4 == 0)
				return true;
		}
		return false;
	}
	// ============================================================================

	// =========================== FIND CLOSEST PLANET ============================
	public static Map<String, Object> findClosestPlanet(Map<String, Object> myPlanet,
			List<Map<String, Object>> enemyPlanets) {
		int distance = Integer.MAX_VALUE;
		Map<String, Object> target = myPlanet;

		for (Map<String, Object> enemyPlanet : enemyPlanetsList) {
			int tempdistance = calculateDistance(myPlanet, enemyPlanet);
			if (tempdistance < distance) {
				distance = tempdistance;
				target = enemyPlanet;
			}
		}
		try {
			if (target.get("color").equals("null")) {
				if (!attackedNeutralPlanets.contains(target)) {
					return target;
				} else {
					enemyPlanets.remove(target);
					return findClosestPlanet(myPlanet, enemyPlanets);
				}
			} else {
				return target;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return target;

	}
	// ============================================================================

	// ============================ CALCULATE DISTANCE ============================
	public static int calculateDistance(Map<String, Object> myPlanet, Map<String, Object> enemyPlanet) {
		int distance = 0;
		// calculate distance between two planets
		// distance = sqrt((x2 - x1)^2 + (y2 - y1)^2)
		int myPlanetX = Integer.parseInt(myPlanet.get("x").toString());
		int myPlanetY = Integer.parseInt(myPlanet.get("y").toString());
		int enemyPlanetX = Integer.parseInt(enemyPlanet.get("x").toString());
		int enemyPlanetY = Integer.parseInt(enemyPlanet.get("y").toString());
		distance = (int) Math.sqrt(Math.pow(myPlanetX - enemyPlanetX, 2)
				+ Math.pow(myPlanetY - enemyPlanetY, 2));
		return distance;
	}
	// ============================================================================

	// =============================== LOG TO FILE ================================
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
	// ============================================================================

	// =============================== CLEAR LISTS ================================
	public static void clearLists() {
		planets.get("blue").clear();
		planets.get("cyan").clear();
		planets.get("green").clear();
		planets.get("yellow").clear();
		planets.get("neutral").clear();
		myPlanetsList.clear();
		enemyPlanetsList.clear();
	}
	// ============================================================================

	// ============================= SET PLANET LISTS =============================
	public static void setPlanetLists() {
		if (myColor.equals("blue")) {
			myPlanetsList = planets.get("blue");
			for (Map.Entry<String, List<Map<String, Object>>> entry : planets.entrySet()) {
				if (!entry.getKey().equals("blue") && !entry.getKey().equals("cyan")) {
					enemyPlanetsList.addAll(entry.getValue());
				}
			}
		} else if (myColor.equals("cyan")) {
			myPlanetsList = planets.get("cyan");
			for (Map.Entry<String, List<Map<String, Object>>> entry : planets.entrySet()) {
				if (!entry.getKey().equals("blue") && !entry.getKey().equals("cyan")) {
					enemyPlanetsList.addAll(entry.getValue());
				}
			}
		} else if (myColor.equals("green")) {
			myPlanetsList = planets.get("green");
			for (Map.Entry<String, List<Map<String, Object>>> entry : planets.entrySet()) {
				if (!entry.getKey().equals("green") && !entry.getKey().equals("yellow")) {
					enemyPlanetsList.addAll(entry.getValue());
				}
			}
		} else if (myColor.equals("yellow")) {
			myPlanetsList = planets.get("yellow");
			for (Map.Entry<String, List<Map<String, Object>>> entry : planets.entrySet()) {
				if (!entry.getKey().equals("green") && !entry.getKey().equals("yellow")) {
					enemyPlanetsList.addAll(entry.getValue());
				}
			}
		}
		if (planets.get("neutral").size() <= 0) {
			currentTurnNumberAfterNeutral++;
		}
	}
	// ============================================================================

	// ============================== GET GAME STATE ==============================
	public static void getGameState() throws NumberFormatException, IOException {
		BufferedReader stdin = new BufferedReader(
				new java.io.InputStreamReader(System.in));

		String line = "";

		while (!(line = stdin.readLine()).equals("S")) {
			logToFile(line);
			// Map<String, Object> planet = new HashMap<>();

			String[] tokens = line.split(" ");
			char firstLetter = line.charAt(0);

			if (firstLetter == 'U') {
				universeWidth = Integer.parseInt(tokens[1]);
				universeHeight = Integer.parseInt(tokens[2]);
				myColor = tokens[3];
			}

			/*
			 * P <int> <int> <int> <float> <int> <string>
			 * - Planet: Name, position x, position y, planet size, army size, color
			 */

			if (firstLetter == 'P') {
				Map<String, Object> planet = new HashMap<>();
				planet.put("name", tokens[1]);
				planet.put("x", tokens[2]);
				planet.put("y", tokens[3]);
				planet.put("size", tokens[4]);
				planet.put("armySize", tokens[5]);
				planet.put("color", tokens[6]);

				if (tokens[6].equals("blue")) {
					planets.get("blue").add(planet);
				}
				if (tokens[6].equals("cyan")) {
					planets.get("cyan").add(planet);
				}
				if (tokens[6].equals("green")) {
					planets.get("green").add(planet);
				}
				if (tokens[6].equals("yellow")) {
					planets.get("yellow").add(planet);
				}
				if (tokens[6].equals("null")) {
					planets.get("neutral").add(planet);
				}
			}

			if (firstLetter == 'M') {
				Map<String, Object> planet = new HashMap<>();
				planet.put("name", tokens[1]);
				planet.put("x", tokens[2]);
				planet.put("y", tokens[3]);
				planet.put("size", tokens[4]);
				planet.put("armySize", tokens[5]);
				planet.put("color", tokens[6]);
				attackedNeutralPlanets.add(planet);
			}

			// logToFile("this is end");
			// logToFile("" + planets);
			// planet.clear();
		}
		/*
		 * - override data from previous turn
		 * - convert the lists into fixed size arrays
		 */

	}
	// ============================================================================
}
