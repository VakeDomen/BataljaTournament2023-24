/**
 * The {@code GameState} class is responsible for holding all the data of the Game that may be needed by the player. It holds data about
 * the players color, the color of their teammate, what turn it currently is, and any messages sent by the Players ally to them in the
 * previous. The {@code GameState} also contains an instance of the {@code Universe} object which holds all the data of all entities
 * currently active. For more info see {@link Universe}
 * @see Universe
 */
public class GameState {
	
	/**
	 * The color of the players {@code Planet}s and {@code Fleet}s
	 */
	public static String myColor;
	
	/**
	 * The color of the {@code Planet}s and {@code Fleet}s of the player which is this players teammate. This should be obtained from a
	 * message at the start of a game.
	 */
	public static String allyColor;
	
	/**
	 * Instance of the {@code Universe} object holding all the entity data of the game. For more info check {@link Universe}
	 * @see Universe
	 */
	public static Universe universe = new Universe();
	
	/**
	 * The turn counter of the game
	 */
	public static int turn = 0;
	
	/**
	 * Holds the message that may have been sent by the players teammate last turn. If no message has been sent last turn this value
	 * should be empty.
	 */
	public static String message;
}