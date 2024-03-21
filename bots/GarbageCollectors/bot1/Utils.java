import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;

public class Utils {
	
	/**
	 * The {@code Commands} class provides wrapper functions for sending actions to the Game. The functions will make sure that
	 * everything runs smoothly and no actions are done which would cause issues with parsing or get the player kicked out for rule
	 * violations or disallowed actions.
	 */
	public static class Commands{
		
		/**
		 * Deploys a {@code Fleet} from {@code origin Planet} to {@code destination Planet} with {@code power} amount of troops. The return of
		 * this function may not accurately represent if the fleet was deployed or not as the communication to the game is one
		 * directional. The deployment may still fail from an unknown cause even though the function returned {@code true}. The amount
		 * of troops sent is an absolute value, to send an amount of troops relative to the military power of the {@code Planet} see
		 * {@link #deployFleet(Planet, Planet, float)} instead
		 * @param origin {@code Planet} from which the {@code Fleet} will be sent
		 * @param destination Destination {@code Planet} for the given {@code Fleet}
		 * @param power The amount of troops that will be taken from {@code Planet} and put on the {@code Fleet}
		 * @see #deployFleet(Planet, Planet, float)
		 * @throws IllegalArgumentException {@code origin} has already deployed this turn. The {@code power} is less than 1 or exceeds
		 * military power found on {@code origin}
		 */
		public static void deployFleet(Planet origin, Planet destination, int power) throws IllegalArgumentException{
			if(power < 1) throw new IllegalArgumentException("Cannot send less than 1 troop");
			if (origin.power < power) throw  new IllegalArgumentException(String.format("Planet %d has only %d military power. Attempted to deploy %d", origin.name, origin.power, power));
			if (origin.hasDeployed) throw new IllegalArgumentException(String.format("Planet %d has already attacked this turn", origin.name));
			if(!GameState.universe.planets.contains(destination)) throw new IllegalArgumentException(String.format(" Destination planet %d does not exist", destination.name));
			if(!origin.isOwned) throw  new IllegalArgumentException(String.format("Origin planet %d is not owned by the player", origin.name));
//			try{
//				Utils.Logging.logWorld(String.format("A %d %d %d\n", origin.name, destination.name, power));
//				Utils.Logging.customLog("raw", String.format("SOUT: A %d %d %d\n",origin.name, destination.name, power));
//			}catch (IOException e){
//				Utils.Logging.logError(e);
//			}
			System.out.printf("A %d %d %d\n", origin.name, destination.name, power);
			origin.hasDeployed = true;
		}
		
		/**
		 * Deploys a {@code Fleet} from a {@code Planet} named {@code origin} to {@code Planet} named {@code destination}
		 * with {@code power} amount of troops. The return of
		 * this function may not accurately represent if the fleet was deployed or not as the communication to the game is one
		 * directional. The deployment may still fail from an unknown cause even though the function returned {@code true}. The amount
		 * of troops sent is an absolute value, to send an amount of troops relative to the military power of the {@code Planet} see
		 * {@link #deployFleet(int, int, float)} instead
		 * @param origin Name of the {@code Planet} from which the {@code Fleet} will be sent
		 * @param destination Name of the {@code Planet} from which the {@code Fleet} will be sent
		 * @param power The amount of troops taken from {@code Planet} and put on the {@code Fleet}
		 * @see #deployFleet(int, int, float)
		 * @throws IllegalArgumentException {@code Planet origin} has already deployed this turn, {@code power} is less than 1 or exceeds military power
		 * found on {@code Planet origin}
		 */
		public static void deployFleet(int origin, int destination, int power) throws IllegalArgumentException{
			Planet originPlanet  = GameState.universe.getPlanet(origin);
			Planet destinationPlanet = GameState.universe.getPlanet(destination);
			deployFleet(originPlanet, destinationPlanet, power);
		}
		
		/**
		 * Deploys a {@code Fleet} from {@code origin} to {@code destination} with the amount of troops proportional to
		 * {@code (power*100)%} of the military power of {@code origin}. The return of
		 * this function may not accurately represent if the fleet was deployed or not as the communication to the game is one
		 * directional. The deployment may still fail from an unknown cause even though the function returned {@code true}.
		 * The amount of troops sent is a relative value, to send a specific amount of troops see {@link #deployFleet(Planet, Planet, int)}
		 * instead. The deployment will fail if {@code power} is greater than {@code 1}
		 * @param origin {@code Planet} from which the {@code Fleet} will be sent
		 * @param destination Destination {@code Planet} for the {@code Fleet}
		 * @param power The relative amount of troops that will be taken from {@code origin} and put on the {@code Fleet}
		 * @see #deployFleet(Planet, Planet, int)
		 * @throws IllegalArgumentException {@code origin} has already deployed this turn, {@code power} represents more than 100% troops or
		 * 0% or less military power found on {@code origin}
		 */
		public static void deployFleet(Planet origin, Planet destination, float power){
			if(power>1)
				throw new IllegalArgumentException("Attempted to deploy more than 100% of the troops found on Planet " + origin.name);
			if(power<=0) throw new IllegalArgumentException("Attempted to deploy 0% or fewer troops");
			int finalPower = (int)Math.ceil(origin.power*power);
			deployFleet(origin, destination, finalPower);
		}
		
		/**
		 * Deploys a {@code Fleet} from a {@code Planet} named {@code origin} to a {@code Planet} named {@code destination} with the
		 * amount of troops proportional to {@code (power*100)%} of the military power found on the {@code origin Planet}.The return of
		 * this function may not accurately represent if the fleet was deployed or not as the communication to the game is one
		 * directional. The deployment may still fail from an unknown cause even though the function returned {@code true}. This
		 * function sends a relative amount of troops to send an exact amount of troops check {@link #deployFleet(int, int, int)} instead
		 * If {@code power} is greater than {@code 1} the function will return {@code false}
		 * @param origin name of the {@code Planet} that the {@code Fleet} will be sent from
		 * @param destination name of the {@code Planet} that the {@code Fleet} will be sent to
		 * @param power The relative amount of troops that will be taken from the {@code origin Planet} and put onto the {@code Fleet}
		 * @see #deployFleet(int, int, int)
		 * @throws IllegalArgumentException {@code Planet origin} has already deployed this turn, {@code power} represents more than 100%
		 * or 0% or less of military power found on {@code Planet origin}
		 */
		public static void deployFleet(int origin, int destination, float power) throws IllegalArgumentException{
			Planet originPlanet = GameState.universe.getPlanet(origin);
			Planet destinationPlanet = GameState.universe.getPlanet(destination);
			deployFleet(originPlanet, destinationPlanet, power);
		}
		
		/**
		 * Deploys a {@code Fleet} from {@code origin} to {code destination} with the amount of troops representing {@code 50%} of
		 * military power of {@code origin}. The return of
		 * this function may not accurately represent if the fleet was deployed or not as the communication to the game is one
		 * directional. The deployment may still fail from an unknown cause even though the function returned {@code true}.
		 * @param origin {@code Planet} from which the {@code Fleet} will be sent
		 * @param destination {@code Planet} to which the {@code Fleet} will be sent
		 * @throws IllegalArgumentException {@code origin} has already deployed on this turn
		 */
		public static void deployFleet(Planet origin, Planet destination) throws IllegalArgumentException{
			deployFleet(origin, destination, 0.5f);
		}
		
		/**
		 * Deploys a {@code Fleet} from a {@code Planet} named {@code origin} to a {@code Planet} named {@code destination} with the
		 * amount of troops representing 50% of the military power found on {@code origin Planet}. The return of
		 * this function may not accurately represent if the fleet was deployed or not as the communication to the game is one
		 * directional. The deployment may still fail from an unknown cause even though the function returned {@code true}.
		 * @param origin name of the {@code Planet} from which the {@code Fleet} will be sent from.
		 * @param destination name of the {@code Planet} the {@code Fleet} will be sent to.
		 * @throws IllegalArgumentException {@code Planet origin} has already deployed this turn
		 */
		public static void deployFleet(int origin, int destination) throws IllegalArgumentException{
			deployFleet(origin, destination, 0.5f);
		}
		
		/**
		 * Appends the {@code message} to the {@code GameState.message} object which will be sent at the end of the turn to the teammate
		 * of the player. This function fails if {@code message} includes a newline character as those cause problems with parsing.
		 * Use this function instead of manually sending the message action to the game
		 * as {@link #endTurn()} will automatically send a message at the end of the turn which can cause problems.
		 * @param message Text to be appended to {@code GameState.message} object.
		 * @throws IllegalArgumentException If {@code message} contains a new line character
		 */
		public static void message(String message) throws IllegalArgumentException{
			if(message.contains("\n")) throw new IllegalArgumentException("Message cannot contain a new line character");
			GameState.message += String.format(" %s", message);
			GameState.message = GameState.message.trim();
		}
		
		/**
		 * Sends the message contained in {@code GameState.message} object to the teammate of the player and ends the turn.
		 * Do not use this function if you have sent a message action to the game manually as this function automatically sends
		 * another one which may cause problems. It is recommended to use this function to end the turn as it will handle the message
		 * sending for you.
		 */
		public static void endTurn(){
			if(!GameState.message.isBlank()){
				System.out.printf("M %s\n", GameState.message);
//				try{
//				Utils.Logging.customLog("raw", String.format("SOUT: M %s", GameState.message));
//				}catch (IOException e){
//					Utils.Logging.logError(e);
//				}
			}
			GameState.message="";
//			try{
//				Utils.Logging.customLog("raw", "SOUT: E");
//			}catch (IOException e){
//				Utils.Logging.logError(e);
//			}
			System.out.println("E");
		}
	}
	
	/**
	 * The {@code Logging} class provides functionality for continuous advanced logging with the capability of custom defined logs
	 * All logs can be found in {@code log/<logName>/<logName><index>.log}. The class also handles managing logs so a new log is generated
	 * if one already exists from a previous run instead of overriding it.
	 */
	public static class Logging{
		private static final HashMap<String, BufferedWriter> loggingMap = new HashMap<>();
		
		/**
		 * Creates a log with the given {@code name}. {@code name} is the name that will be used for subfolder,
		 * filenames, and as the name of the log to be used with {@link #customLog(String name, Object obj)}
		 * @param name name for the log you are creating
		 * @throws IOException - If either the folder structure or file for the log failed to be created
		 * @throws IllegalArgumentException - If a log named {@code name} already exists
		 */
		public static void createLog(String name) throws IOException, IllegalArgumentException {
			if(loggingMap.containsKey(name)) throw new IllegalArgumentException(String.format("A log named \"%s\" already exists", name));
			File folder = new File(String.format("./log/%s/", name));
			int index = 0;
			if (!folder.exists()){
				boolean success = folder.mkdirs();
				if(!success) {
					throw new IOException(String.format("Failed to create %s", folder.getPath()));
				}
			}
			File[] files = folder.listFiles();
			if(Objects.nonNull(files)){
				index = files.length+1;
			}
			File file = new File(folder, String.format("%s%d.log", name, index));
			BufferedWriter writer = new BufferedWriter( new FileWriter(file, true));
			loggingMap.put(name, writer);
		}
		
		/**
		 * Logs the {@code obj} to a log named {@code name}. This function will call {@code toString()} on the Object as such it is
		 * recommended to override the method for the object before it is logged otherwise the log will be essentially useless. The
		 * function will fail to log if the given {@code name} does not exist in the logging map
		 * @param name name of the log to which to log as defined in {@link #createLog(String)}
		 * @param obj {@code Object} to be logged
		 * @param <T> Object
		 * @throws IOException if {@code obj} failed to be written
		 * @throws IllegalArgumentException if {@code name} is not a valid log
		 */
		public static <T> void customLog(String name,T obj) throws IOException, IllegalArgumentException{
			if(!loggingMap.containsKey(name)) throw new IllegalArgumentException(String.format("\"%s\" is not a valid log", name));
			BufferedWriter writer = loggingMap.get(name);
			writer.write(GameState.turn+": "+obj.toString());
			writer.newLine();
			writer.flush();
		}
		
		/**
		 * Logs the {@code worldObj} to log named "world" found at {@code log/world/world<index>.log}. The function will call the objects
		 * {@code toString()} method. As such it is
		 * recommended to override said method for any {@code Object} that is put into this log as the log will otherwise be essentially
		 * useless
		 * @param worldObj {@code Object} to be logged to the world log
		 * @param <T> Object
		 * @throws IOException If {@code worldObj} failed to be written to log
		 * @throws IllegalArgumentException If logging has not yet been initialized
		 */
		public static <T> void logWorld(T worldObj) throws IOException, IllegalArgumentException{
			if(!loggingMap.containsKey("world")) createLog("world");
			customLog("world", worldObj);
		}
		
		/**
		 * Logs the given {@code Exception} to the log named "error" found at {@code log/error/error<index>.log}. The function will print
		 * the {@code Exception} together with its stack trace for easier debugging. The function will only log the error, but won't stop
		 * execution of the application
		 * @param error {@code Exception} to be printed to the log
		 * @param <T> {@code Object} extending {@link Exception}
		 * @see Exception
		 */
		public static <T extends Exception> void logError(T error){
			try{
			if(!loggingMap.containsKey("error")) createLog("error");
			customLog("error", error);
			for(StackTraceElement trace : error.getStackTrace()){
				customLog("error", trace);
			}
			}catch (IOException e){
				System.out.println(e);
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * The {@code Communication} class provides functionality for advanced communication between players in the form of {@code Order}s.
	 * The {@code Communication} class relies on one player being the master and the other player/s following orders from the master player
	 * @see Order
	 */
	public static class Communication{
		
		/**
		 * Serializes {@code Order}s into a string that can be sent to the other player
		 * @param orders an indeterminate amount of orders
		 * @return {@code String} representing all of the orders which can then be messaged to the player
		 */
		public static String serialize(Order... orders){
			return Arrays.stream(orders).map(Order::toString).collect(Collectors.joining(";"));
		}
		
		/**
		 * Deserializes the given message into an array of {@code Order}s. This method will fail if the message is not formatted correctly
		 * or if there is something wrong with the message.
		 * @param commands {@code String} message to pa deserialized into a set of {@code Command}s
		 * @return {@code Order[]} obtained by parsing the message string
		 */
		public static Order[] deserialize(String commands){
			String[] orderStrings = commands.split(";");
			Order[] orders = new Order[orderStrings.length];
			for(int i = 0; i<orderStrings.length; i++){
				String[] orderParts = orderStrings[i].split(":");
				int opCode = Integer.parseInt(orderParts[0]);
				Planet[] args = new Planet[0];
				if(!orderParts[1].isBlank())
				 args  = Arrays.stream(orderParts[1].split("-")).map(Integer::parseInt).map(GameState.universe::getPlanet).toArray(Planet[]::new);
				orders[i] = new Order(opCode, args);
			}
			return orders;
		}
		
		/**
		 * The {@code Command} class wraps the arguments and the type of command into an object. Used in conjunction with {@code Communication} class
		 * <br><br>
		 * Currently, known opCodes:
		 * <ul>
		 * <li>0 - Everything is ok (should be sent every turn so that the other player knows the master player is still active)</li>
		 * <li>1 - Attack {@code args} (the args is a list of target planets that should be attacked)</li>
		 * </ul>
		 * @see Communication
		 */
		public static class Order{
			/**
			 * Tells what action is required from the receiving player. List of opCodes can be found in the class documentation found at
			 * {@link Communication}
			 */
			int opCode;
			/**
			 * The arguments provided for the said action
			 */
			Planet[] args;
			
			/**
			 * Default constructor for creating orders
			 * @param opCode Action that should be taken by the receiving player
			 * @param args an indeterminate amount of arguments of type {@code Planet}
			 */
			public Order(int opCode, Planet... args){
				this.args = args;
				this.opCode = opCode;
			}
			
			@Override
			public String toString() {
				String str = String.format("%d:", opCode);
				str += Arrays.stream(args).map(arg -> arg.name).map(String::valueOf).collect(Collectors.joining("-"));
				return str;
			}
		}
	}
	
	/**
	 * The {@code Simulation} class provides functions that calculate growth of a {@code Planet}/s through time and functions that can
	 * calculate how long a {@code Fleet}/s would take to reach a {@code Planet} from a specified {@code Planet}
	 */
	public static class Simulation{
		
		/**
		 * Simulates how {@code planet} will grow int {@code turns} amount of turns.
		 * @param turns timespan to calculate growth for
		 * @param planet {@code Planet} for which to calculate growth
		 * @return simulated {@code Planet} as it would appear in game after {@code turns} amount of turns
		 */
		public static double simulateGrowth(int turns, Planet planet){
			if(planet.isNeutral) return planet.power;
			double power = planet.power;
			power += planet.size*10*turns;
			return power;
		}
		
		/**
		 * Simulates how {@code Planet planet} would grow in {@code turns} turns.
		 * @param turns timespan for which to calculate growth
		 * @param planet name of {@code Planet} for which to simulate growth
		 * @return simulated {@code Planet} as it would appear in game after {@code turns} turns
		 */
		public static double simulateGrowth(int turns, int planet){
			Planet planetObj = GameState.universe.getPlanet(planet);
			return simulateGrowth(turns, planetObj);
		}
		
		/**
		 * Simulates a {@code Fleet} and calculates how long said fleet would need to reach {@code destination} from {@code origin}
		 * @param origin origin planet from which the fleet will start
		 * @param destination destination planet to which the fleet will travel
		 * @return {@code int} representing the length of travel in game turns
		 */
		public static int simulateFleet(Planet origin, Planet destination){
			return (int)origin.getDistanceFrom(destination)/2;
		}
		
		/**
		 * Simulates a {@code Fleet} and calculates how long said fleet would need to reach {@code Plaent destination} from {@code Planet origin}
		 * @param origin name of origin planet from which the fleet will start
		 * @param destination name of destination planet to which the fleet will travel
		 * @return {@code int} representing the length of travel in game turns
		 */
		public static int simulateFleet(int origin, int destination){
			Planet originObj = GameState.universe.getPlanet(origin);
			Planet destinationObj = GameState.universe.getPlanet(destination);
			return simulateFleet(originObj,destinationObj);
		}
		
		
	}
}
