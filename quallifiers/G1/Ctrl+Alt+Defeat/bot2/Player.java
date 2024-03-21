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
	private static String[] vsiPlaneti;
	public static int minIndex = 0;
	public static long seed = System.currentTimeMillis(); // current system time kot seed
	Random rand = new Random(seed);


	public static void main(String[] args) throws Exception {

		try {

			while (true) {

				getGameState();
				String[] myPlanets = new String[0];
				//String targetPlayer = "";
				napadiNajblizjega(myPlanets);

				/*

					- so I'm thinking: we create a fnc for the evklidska razdalja (done) med planeti in
					  zavzamemo planet closest to us (done)
					- ko ni vecc neutral planetov...napademo najblizjega nasprotnika (done)
					- focus on attacking one planet with multiple bots and not "usak sam zase" (opuščena stratecija-ehh)
					- boti si morjo zapomnit raz situacije: kategorizacija določenih stanj igre: (done)
					  1. zmagujemo - kako naprej
					  2. zgublamo - kako naprej

				*/

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

	//uporabi fnc oceni situacijo in fnc napadi najblizjega
	public static String[] napad(String [] myPlanets){
		int situacija = oceniSituacijo(myPlanets);
		String mojPlanet = "";

		for (int i = 0; i < myPlanets.length; i++) {
			mojPlanet = myPlanets[i];
		}

		//situacije:
		if (situacija == 1){ //1. my yellow planets are winning and the cyan ones are loosing
			//aka napadi tudi soigralca, da mu daš mal moči
			vsiPlaneti = mergeArrays(bluePlanets, blueFleets, greenPlanets, greenFleets,cyanPlanets, cyanFleets, neutralPlanets);

		} else if(situacija == 2 && getColor(mojPlanet) == "cyan"){ //2. my cyan planets are winning and the yellow planets are loosing
			//aka napadi tudi soigralca, da mu daš mal moči
			vsiPlaneti = mergeArrays(bluePlanets, blueFleets, greenPlanets, greenFleets, yellowPlanets, yellowFleets,neutralPlanets);

		} else if (situacija == 3 && (getColor(mojPlanet) == "yellow" || getColor(mojPlanet) == "cyan")){ //3. both my yellow and cyan planets are winning
			vsiPlaneti = mergeArrays(bluePlanets, blueFleets, greenPlanets, greenFleets,neutralPlanets);

		} else if(situacija == 4 && getColor(mojPlanet) == "blue"){ //4.  my blue planets are winning and the green ones are loosing
			//aka napadi tudi soigralca, da mu daš mal moči
			vsiPlaneti = mergeArrays(cyanPlanets, cyanFleets, yellowPlanets, yellowFleets,greenPlanets, greenFleets, neutralPlanets);
		} else if (situacija == 5 && getColor(mojPlanet) == "green"){ //5. my green planets are winning and the blue ones are loosing
			//aka napadi tudi soigralca, da mu daš mal moči
			vsiPlaneti = mergeArrays(cyanPlanets, cyanFleets, yellowPlanets, yellowFleets, bluePlanets, blueFleets,neutralPlanets);
		} else if (situacija == 6 && (getColor(mojPlanet) == "blue" || getColor(mojPlanet) == "green")){ //6 both my green and blue planets are winning
			vsiPlaneti = mergeArrays(cyanPlanets, cyanFleets, yellowPlanets, yellowFleets, neutralPlanets);
		} else { //both of my planets are winning

			if (getColor(mojPlanet) == "green") {
				vsiPlaneti = mergeArrays(cyanPlanets, cyanFleets, yellowPlanets, yellowFleets, neutralPlanets);
			} else if (getColor(mojPlanet) == "blue") {
				vsiPlaneti = mergeArrays(cyanPlanets, cyanFleets, yellowPlanets, yellowFleets, neutralPlanets);
			} else if (getColor(mojPlanet) == "cyan") {
				vsiPlaneti = mergeArrays(bluePlanets, blueFleets, greenPlanets, greenFleets, neutralPlanets);
			} else if (getColor(mojPlanet) == "yellow") {
				vsiPlaneti = mergeArrays(bluePlanets, blueFleets, greenPlanets, greenFleets, neutralPlanets);
			}

		}
		return vsiPlaneti;
	}

	//izracunaj razdaljo med dvema planetoma
	public static double evklidskaRazdalja (int x1, int y1, int x2, int y2){
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}

	//napadi najblizji planet
	public static void napadiNajblizjega(String [] myPlanets) {
		double[] najDistance = new double[myPlanets.length]; //za vsak planet bomo shranl distanco
		String[] najPlanet = new String[myPlanets.length]; //najblizji planet za vsak nas planet

		for (int i = 0; i < myPlanets.length; i++) {
			String mojPlanet = myPlanets[i];
			int mojPlanetX = getX(mojPlanet);
			int mojPlanetY = getY(mojPlanet);


			double najDistanca = Double.MAX_VALUE; //najprej nej bo najbl oddaljena distanca najblizja
			String najblizjiPlanet = null; //najprej nej bo najblizji noben

			vsiPlaneti = new String[1];

			//kdo je moj target?
			vsiPlaneti = napad(myPlanets);

			for (int j = 0; j < vsiPlaneti.length; j++) {
				String planet = vsiPlaneti[j]; //extractamo po en planet od useh
				if (!planet.equals(mojPlanet)) {
					int planetX = getX(planet); //dobi x koordinato
					int planetY = getY(planet); //dobi y koordinato


					//kaksna je distanca zdej med mojim planetom in targetom?
					double distanca = evklidskaRazdalja(mojPlanetX, mojPlanetY, planetX, planetY);

					//zdej posodobimo naso naj distanco
					if (distanca < najDistanca) {
						najDistanca = distanca; //najblizja distanca postance distanca najblizjiga planeta
						najblizjiPlanet = planet; //popravimo tud string
					}
				}
			}

			//shranimo distance
			najDistance[i] = najDistanca;
			najPlanet[i] = najblizjiPlanet;
		}

		//zdej pa zavzamemo najblizji neutraln planet
		minIndex = 0;
		for (int i = 0; i < najPlanet.length; i++) {
			if (najDistance[i] < najDistance[minIndex]) {
				minIndex = i;
			}
		}

		//napadi planet z min indexom..
		String targetPlanet = najPlanet[minIndex];
		//za attack komando
		System.out.println("A " + myPlanets[minIndex] + " ");
	}


	//zdruzi arraye planetov
	public static String[] mergeArrays(String[]... arrays){
		int dolzina = 0;

		for (int i = 0; i < arrays.length; i++) {
			dolzina += arrays[i].length;
		}

		String [] rez = new String[dolzina];
		int index = 0;
		for (int i = 0; i < arrays.length; i++) {
			String [] polje = arrays[i];
			System.arraycopy(polje, 0, rez, index, polje.length);
			index += polje.length;
		}

		return rez;
	}

	//dobi X kooridnato
	public static int getX(String planet){
		//planet sestavljen iz P 0 51 80 1.0 100 green (x je drugi)
		String [] format = planet.split(" ");
		return Integer.parseInt(format[2]);
	}
	//dobi Y koordinato
	public static int getY(String planet){
		//planet sestavljen iz P 0 51 80 1.0 100 green (y je tretji)
		String [] format = planet.split(" ");
		return Integer.parseInt(format[3]);
	}

	//dobi barvo planeta
	public static String getColor(String planet){
		//planet sestavljen iz P 0 51 80 1.0 100 green (color je sedmi)
		String [] format = planet.split(" ");
		return format[7];
	}
	//dobi planet size
	public static float getPlanetSize(String planet){
		//planet sestavljen iz P 0 51 80 1.0 100 green (planet size je peti)
		String [] format = planet.split(" ");
		return Float.parseFloat(format[5]);
	}
	//dobi planet fleets
	public static int getPlanetFleets(String planet){
		//planet sestavljen iz P 0 51 80 1.0 100 green (fleet size je sesti)
		String [] format = planet.split(" ");
		return Integer.parseInt(format[6]);
	}

	//kategorizacija situacije
	public static int oceniSituacijo(String [] myPlanets){
		int situacija = 0; //imeli bomo 6 situacij based on the planet size and fleet size

		//kdo sem jaz?
		String mojPlanet = "";
		for (int i = 0; i < myPlanets.length; i++) {
			mojPlanet = myPlanets[i];
		}

		//situacije:
		//1. my yellow planets are winning and the cyan ones are loosing
		if (getColor(mojPlanet) == "yellow"
				&& Arrays.stream(yellowPlanets).anyMatch(Arrays.asList(myPlanets)::contains)
				&& Arrays.stream(cyanPlanets).noneMatch(Arrays.asList(myPlanets)::contains)) {
			situacija = 1;
			//2. my cyan planets are winning and the yellow planets are loosing
		} else if (getColor(mojPlanet) == "cyan"
				&& Arrays.stream(cyanPlanets).anyMatch(Arrays.asList(myPlanets)::contains)
				&& Arrays.stream(yellowPlanets).noneMatch(Arrays.asList(myPlanets)::contains)) {
			situacija = 2;
			//3. both my yellow and cyan planets are winning
		} else if ((getColor(mojPlanet) == "yellow" || getColor(mojPlanet) == "cyan")
				&& Arrays.stream(yellowPlanets).anyMatch(Arrays.asList(myPlanets)::contains)
				&& Arrays.stream(cyanPlanets).anyMatch(Arrays.asList(myPlanets)::contains)) {
			situacija = 3;
			//4.  my blue planets are winning and the green ones are loosing
		}else if (getColor(mojPlanet) == "blue"
				&& Arrays.stream(bluePlanets).anyMatch(Arrays.asList(myPlanets)::contains)
				&& Arrays.stream(greenPlanets).noneMatch(Arrays.asList(myPlanets)::contains)) {
			situacija = 4;
			//5. my green planets are winning and the blue ones are loosing
		}else if (getColor(mojPlanet) == "green"
				&& Arrays.stream(greenPlanets).anyMatch(Arrays.asList(myPlanets)::contains)
				&& Arrays.stream(bluePlanets).noneMatch(Arrays.asList(myPlanets)::contains)) {
			situacija = 5;
			//6 both my green and blue planets are winning
		}else if ((getColor(mojPlanet) == "green" || getColor(mojPlanet) == "blue")
				&& Arrays.stream(greenPlanets).anyMatch(Arrays.asList(myPlanets)::contains)
				&& Arrays.stream(bluePlanets).anyMatch(Arrays.asList(myPlanets)::contains)) {
			situacija = 6;
		} else {
			situacija = 0; //both are winning
		}

		return situacija;
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
			logToFile(line);

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