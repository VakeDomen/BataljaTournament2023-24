/**
 * The {@code Fleet} class holds data about an individual {@code Fleet} sent by a {@code Planet}. It holds data like the name and
 * military power of the {@code Fleet}, the {@code Fleet}s origin {@code Planet} and its destination {@code Planet}. As well as the
 * estimated amount of turns the {@code Fleet} will take to reach its destination, if the {@code Fleet} is allied to the player, if it's
 * owned by the player, and if the {@code Fleet} is neutral. It also holds the color the origin planet was when the {@code Fleet} was
 * sent
 */
public class Fleet {
	/**
	 * The name of the fleet represented as a 32-bit integer
	 */
	public int name;
	/**
	 * The military power contained on the {@code Fleet}
	 */
	public int power;
	/**
	 * The {@code Planet} which sent the {@code Fleet}. This entry holds up-to-date information of the {@code Planet} which sent
	 * the {@code Fleet} and not the state of the {@code Planet} when the {@code Fleet} was sent, as such the data of the
	 * origin {@code Planet} may not be reliable as the owner of the {@code Planet} may have changed sine the {@code Fleet} was sent off
	 */
	public Planet origin;
	/**
	 * The up-to-date information of the {@code Planet} the {@code Fleet} is destined for. Same as with {@code origin} the information
	 * may be unreliable as the owner of the {@code Planet} may have changed while the {@code Fleet} has been travelling
	 */
	public Planet destination;
	/**
	 * The estimated amount of turns the {@code Fleet} will still take until it reaches the destination
	 */
	public int eta;
	/**
	 * Flag which tells if the {@code Fleet} was sent from an allied {@code Planet} at the time of departure
	 */
	public boolean isAllied;
	/**
	 * Flag which tells if the {@code Fleet} was sent from a {@code Planet} which was owned by the player at the time of departure
	 */
	public boolean isOwned;
	/**
	 * Color the {@code Planet} that sent the {@code Fleet} was at the time of the {@code Fleet}'s departure
	 */
	public String color;
	
	public Fleet(int name, int power, Planet origin, Planet destination, int eta, boolean isAllied, boolean isOwned,String color) {
		this.name = name;
		this.power = power;
		this.origin = origin;
		this.destination = destination;
		this.eta = eta;
		this.isAllied = isAllied;
		this.isOwned = isOwned;
		this.color = color;
	}
	
	public Fleet(){
		name = 0;
		power = 0;
		origin = null;
		destination = null;
		eta = 0;
		isAllied = false;
		isOwned = false;
		color = "unknown";
	}
	/**
	 * Checks if the name of this {@code Fleet} matches the name of {@code fleet}
	 * @param fleet {@code Fleet} of which the name we are comparing to
	 * @return {@code true} if the name matches {@code false} otherwise
	 */
	public boolean equals(Fleet fleet) {
		return this.name == fleet.name;
	}
	
	/**
	 * Gets Fleets name
	 * @return {@code int} name of fleet
	 */
	public int getName() {
		return name;
	}
	
	/**
	 * Gets Fleets military power
	 * @return {@code int} military power of the fleet
	 */
	public int getPower() {
		return power;
	}
	
	/**
	 * Gets Fleets origin Planet
	 * @return {@code Planet} from which the fleet originates
	 */
	public Planet getOrigin() {
		return origin;
	}
	
	/**
	 * Gets Fleets desination Planet
	 * @return {@code Planet} to which the fleet is travelling
	 */
	public Planet getDestination() {
		return destination;
	}
	
	/**
	 * Gets the Fleets estimated time of arrive in game turns
	 * @return {@code int} amount of game turns the fleet will take to arrive at destination
	 */
	public int getEta() {
		return eta;
	}
	
	/**
	 * Gets the fleets allies status
	 * @return {@code true} if the fleet is player's or their teammate's
	 */
	public boolean isAllied() {
		return isAllied;
	}
	
	/**
	 * Gets the fleets status of ownership
	 * @return {@code true} if the fleet is owned by the player
	 */
	public boolean isOwned() {
		return isOwned;
	}
	
	/**
	 * Gets the fleet color
	 * @return {@code String} representing the color of the origin planet at the time of departure
	 */
	public String getColor() {
		return color;
	}
	
	@Override
	public String toString() {
		return "{" +
				       "\"type\": \"Fleet\"," +
				       String.format("\"name\": %d,", name) +
				       String.format("\"power\": %d,", power) +
				       String.format("\"origin\": %s,", origin) +
				       String.format("\"destination\": %s,", destination) +
				       String.format("\"eta\": %d,", eta) +
				       String.format("\"isAllied\": %s,", isAllied) +
				       String.format("\"isOwned\": %s,", isOwned) +
				       String.format("\"color\": \"%s\"", color) +
				       "}";
	}
}
