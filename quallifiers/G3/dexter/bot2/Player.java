import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Player {

	static BufferedWriter fileOut = null;

	public static Universe universe = new Universe();
	public static PlanetManager planetManager = new PlanetManager(universe);
	public static FleetManager fleetManager = new FleetManager(universe, planetManager);


	public static void main(String[] args) throws Exception {

		try {

			while (true) {

				getGameState();
				// At first iteration of the game calculate distance tables
				if (!planetManager.isDistanceTablesInitialized()) {
					planetManager.initDistanceTables();
				}
				// Clean fleet manager, for not containing dead values
				fleetManager.clearInactiveFleets();


				ArrayList<Planet> myPlanets = planetManager.getMyPlanets();
				ArrayList<Planet> candidates = planetManager.getCandidatePlanets();

				// alpha = 1 - not bad
				// alpha = 0.5 - seems to perform better
				double alpha = 0.7;
				if (!myPlanets.isEmpty()) {
					for (Planet p : myPlanets) {

						for (Planet c : candidates) {
							c.calculateUtility(p, alpha);
						}
						candidates.sort(Comparator.comparingDouble(Planet::getUtility).reversed());

						Planet first = candidates.get(0);
						int ffs = first.getFleetSize();
						int dit = first.getDistanceInTurns(p);
						int ddd = first.defendersInNTurns(dit);
						int aaa = p.attackersInNTurns(dit);
						int nft = first.getNumberOfFleetsOverNTurns(dit);
						if (first.isNeutral()) {
							if (ffs + nft < p.getFleetSize()) {
								System.out.println("A " + p.getName() + " " + first.getName() + " " + p.getFleetSize());
							}
						} else {
							if ((ffs + nft + ddd < p.getFleetSize() - aaa)) {
								System.out.println("A " + p.getName() + " " + first.getName() + " " + p.getFleetSize());
							}
						}


					}
				}

				/*


				int[] counter = new int[planetManager.getNumberOfPlanets()];

				if (!myPlanets.isEmpty()) {
					for (Planet p : myPlanets) {

						int closest = p.getClosestEnemy();
						if (closest != -1) {
							counter[closest]++;
						}
					}

					int target = -1;
					int count = 0;
					for (int i = 0; i < counter.length; i++) {
						if (count < counter[i]) {
							target = i;
							count = counter[i];
						}
					}

					if (target != -1) {
						for(Planet p : myPlanets) {
							// index of attacker is number of turns needed for number of attackers to arrive
							int[] attackers = p.attackersInTurns();
							int dispatch = 0;
                            for (int i = 1; i < attackers.length; i++) {
								int fInNTurns = p.getFleetSize() + p.getNumberOfFleetsOverNTurns(i);
								int diff = fInNTurns - attackers[i] - 5;
								if (diff > dispatch) {
									dispatch = diff;
								} else {
									break;
								}
							}

							System.out.println("A " + p.getName() + " " + target + " " + dispatch);
						}
					}

				}

				 */
				
				/*
					- send a hello message to your teammate bot :)
					- it will receive it form the game next turn (if the bot parses it)
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



	public static void getGameState() throws NumberFormatException, IOException {

		BufferedReader stdin = new BufferedReader(new java.io.InputStreamReader(System.in));

		String line = "";
		while (!(line = stdin.readLine()).equals("S")) {

			//logToFile(line);

			switch (line.charAt(0)) {
				case 'U': universe.initialize(line); break;
				case 'P': planetManager.parse(line); break;
				case 'F': fleetManager.parse(line); break;
			}
		}
	}
}
