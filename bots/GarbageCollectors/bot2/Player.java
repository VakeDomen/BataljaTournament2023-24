import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Player {
	static BufferedWriter fileOut = null;
	
	/*
		GAME DATA
	*/
	static int maxDistance = 0;
	static boolean enemyMode = false;
	static int freeTurns = 0;
	static int hoardingPower = 0;
	static boolean endgame=false;
	

	public static void main(String[] args) throws Exception {
		
			Random rand = new Random(); // source of random for random moves
			//Utils.Logging.createLog("idk");
			//Utils.Logging.createLog("raw");
			/*
				**************
				Main game loop
				**************
			  	- each iteration of the loop is one turn.
			  	- this will loop until we stop playing the game
			  	- we will be stopped if we die/win or if we crash
			*/
			while (true) {
				try {
				/*
					- at the start of turn we first recieve data
					about the universe from the game.
					- data will be loaded into the static variables of
					this class
				*/
				
				getGameState();
				
				if(GameState.universe.getNeutralPlanets().length == 0){
					endgame = true;
					enemyMode = true;
				}
				
				if(maxDistance == 0){
					maxDistance = (int)Math.sqrt(2*Math.pow(GameState.universe.width,2));
					maxDistance = (int)(maxDistance*0.4);
				}
				/*
				 	*********************************
					LOGIC: figure out what to do with
					your turn
					*********************************
					- current plan: attack randomly
				*/
				
				
				
				
				//LOGIC START
				double averagePower = GameState.universe.planets.stream().mapToInt(Planet::getPower).average().orElseThrow();
				averagePower*=0.2;
				//decrease this to make the bot more aggressive
				if(enemyMode){
					averagePower+=hoardingPower;
				}else{
					averagePower*=0.2;
				}
				defendingLogic(averagePower);
				if(enemyMode){
					hoardingPower+=2;
				}
				attackingLogic(averagePower);
				
				//String message = Utils.Communication.serialize(new Utils.Communication.Order(0));
				//Utils.Commands.message(message);
				Utils.Commands.endTurn();
				
				/*
				  	- E will end my turn. 
				  	- you should end each turn (if you don't the game will think you timed-out)
				  	- after E you should send no more commands to the game
				 */
				} catch (Exception e) {
					Utils.Logging.logError(e);
					Utils.Commands.endTurn();
				}
			}
		
	}
	
	private static void attackingLogic(double averagePower) throws IOException {
		
		Planet[] ownedPlanets = GameState.universe.getOwnedPlanetsStream().filter(x -> x.power>averagePower).toArray(Planet[]::new);
		
		//Utils.Logging.customLog("idk", "Number of targets " + targets.length);
		
		for(Planet ownedMainPlanet:ownedPlanets){
			if(ownedMainPlanet.hasDeployed) continue;
			Planet[] targets = GameState.universe.getEnemyPlanetsStream().sorted(Comparator.comparingDouble(ownedMainPlanet::getDistanceFrom)).toArray(Planet[]::new);
			
			for (Planet posTarget:targets){
				if(ownedMainPlanet.getDistanceFrom(posTarget) > maxDistance && enemyMode) break;
				int maxEta = GameState.universe.getFleetsDestinedForStream(posTarget).mapToInt(Fleet::getEta).max().orElse(0);
				double targetpower = posTarget.power;
				targetpower -= GameState.universe.getFleetsAttackingStream(posTarget).mapToInt(Fleet::getPower).sum();
				targetpower += GameState.universe.getFleetsReinforcingStream(posTarget).mapToInt(Fleet::getPower).sum();
				targetpower++;
				if(targetpower+Utils.Simulation.simulateGrowth(maxEta, posTarget) < 0) continue;
				final Planet target = posTarget;
				Planet[] attackingPlanets = GameState.universe.getOwnedPlanetsStream().filter(x -> !x.hasDeployed).sorted(Comparator.comparingDouble(ownedMainPlanet::getDistanceFrom)).toArray(Planet[]::new);
				int[] combinedPower = Arrays.stream(attackingPlanets).mapToInt(Planet::getPower).toArray();
				maxEta = Math.max(maxEta, Arrays.stream(attackingPlanets).mapToInt(x -> Utils.Simulation.simulateFleet(x, target)).max().orElse(0));
				targetpower+=Utils.Simulation.simulateGrowth(maxEta,target);
				for(int i=0; i < attackingPlanets.length && targetpower>=0; i++){
					if(attackingPlanets[i].power <= 0) continue;
					targetpower-=(int)attackingPlanets[i].power;
					Utils.Commands.deployFleet(attackingPlanets[i], target, 1f);
				}
			}
		}
	}
	
	private static void defendingLogic(double averagePower){
		boolean enemyAttacked = false;
		Planet[] myPlanets = GameState.universe.getOwnedPlanets();
		for(Planet myPlanet:myPlanets){
			int power = myPlanet.power;
			int maxEta = GameState.universe.getFleetsDestinedForStream(myPlanet).mapToInt(Fleet::getEta).max().orElse(0);
			power-=GameState.universe.getFleetsAttackingStream(myPlanet).mapToInt(Fleet::getPower).sum();
			power+=GameState.universe.getFleetsReinforcingStream(myPlanet).mapToInt(Fleet::getPower).sum();
			if(power > 0) continue;
			if(!enemyAttacked) enemyAttacked = true;
			Planet[] freePlanets = GameState.universe.getOwnedPlanetsStream().filter(x -> GameState.universe.getFleetsAttacking(x).length == 0).filter(x -> !x.hasDeployed).sorted(Comparator.comparingDouble(myPlanet::getDistanceFrom)).toArray(Planet[]::new);
			for(Planet reinforcement:freePlanets){
				maxEta = (int)(Arrays.stream(freePlanets).mapToDouble(myPlanet::getDistanceFrom).max().orElse(0)/2.0) - maxEta;
				if(maxEta<0) maxEta=0;
				int freeTroops = reinforcement.power - (int)(averagePower*0.5);
				if(freeTroops< 1) continue;
				power+=freeTroops;
				Utils.Commands.deployFleet(reinforcement, myPlanet, freeTroops);
				if(power-Utils.Simulation.simulateGrowth(maxEta, myPlanet)>0) break;
			}
		}
		if(enemyAttacked){
			enemyMode = true;
			freeTurns = 0;
		}else{
			freeTurns++;
		}
		if(freeTurns >= 50 && !endgame){
			enemyMode = false;
		}
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
	 * The function will store the whole state of the game inside an object called gameState which can be found at
	 * the top of the Player class
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
		//resetting the message in case there was one stored in previous turn
		GameState.message = "";
		GameState.turn++;
		ArrayList<Fleet> universeFleets= new ArrayList<>();
		ArrayList<Planet> universePlanets= new ArrayList<>();
		//Utils.Logging.logWorld(String.format("<------------------ ROUND %d ------------------>", GameState.turn));
		while (!(line = stdin.readLine()).equals("S")) {
			/* 
				- save the data we recieve to the log file, so you can see what 
				data is recieved form the game (for debugging)
			*/
			
			//Utils.Logging.customLog("raw", "SIN: "+line);
			
			String[] tokens = line.split(" ");
			char firstLetter = line.charAt(0);
			/*
			 	U <int> <int> <string> 						
				- Universe: Size (x, y) of playing field, and your color
			*/
			if (firstLetter == 'U') {
				GameState.universe.width = Integer.parseInt(tokens[1]);
				GameState.universe.height = Integer.parseInt(tokens[2]);
				GameState.myColor = tokens[3];
				if(GameState.myColor.equals("green")){
					GameState.allyColor = "yellow";
				}
				if(GameState.myColor.equals("yellow")){
					GameState.allyColor = "green";
				}
				if(GameState.myColor.equals("blue")){
					GameState.allyColor = "cyan";
				}
				if(GameState.myColor.equals("cyan")){
					GameState.allyColor = "blue";
				}
			} 
			/*
				P <int> <int> <int> <float> <int> <string> 	
				- Planet: Name (number), position x, position y, 
				planet size, army size, planet color (blue, cyan, green, yellow or null for neutral)
			*/
			if (firstLetter == 'P') {
				boolean isAllied = tokens[6].equals(GameState.myColor) || tokens[6].equals(GameState.allyColor);
				boolean isOwned = tokens[6].equals(GameState.myColor);
				boolean isNeutral = tokens[6].equals("null");
				int name = Integer.parseInt(tokens[1]);
				Position position = new Position(Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]));
				float size = Float.parseFloat(tokens[4]);
				int power = Integer.parseInt(tokens[5]);
				Planet planet = new Planet(
						name,
						position,
						size,
						power,
						isAllied,
						isOwned,
						isNeutral,
						tokens[6]);
				universePlanets.add(planet);
				//Utils.Logging.logWorld(planet);
			}
			/*
				F	<int> <int> <int> <int> <int> <int> <string>
				Fleet: name, size, origin, destination, current turn, turns needed, color
			 */
			if (firstLetter == 'F'){
				boolean isAllied = tokens[7].equals(GameState.myColor) || tokens[7].equals(GameState.allyColor);
				boolean isOwned = tokens[7].equals(GameState.myColor);
				int name = Integer.parseInt(tokens[1]);
				int size = Integer.parseInt(tokens[2]);
				Planet origin = GameState.universe.getPlanet(Integer.parseInt(tokens[3]));
				Planet destination = GameState.universe.getPlanet(Integer.parseInt(tokens[4]));
				int currentTurn = Integer.parseInt(tokens[5]);
				int turnsNeeded = Integer.parseInt(tokens[6]);
				int eta = turnsNeeded-currentTurn;
				String color = tokens[7];
				Fleet fleet = new Fleet(
						name,
						size,
						origin,
						destination,
						eta,
						isAllied,
						isOwned,
						color);
				universeFleets.add(fleet);
				//Utils.Logging.logWorld(fleet);
			}
			if(firstLetter == 'M'){
				GameState.message = tokens[1];
				//Utils.Logging.logWorld(String.format("Message: %s", GameState.message));
			}
		}
		GameState.universe.planets = universePlanets;
		GameState.universe.fleets = universeFleets;
	}
}
