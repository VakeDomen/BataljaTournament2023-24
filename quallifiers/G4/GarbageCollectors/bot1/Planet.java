/**
 * The {@code Planet} class holds data  about an individual planet. It holds the {@code Planet}'s name, its position in the
 * {@code Universe}, the size of the {@code Planet}, the amount of military power on the {@code Planet}, if the {@code Planet} is an allied,
 * if it's owned by the player, if its neutral, and its color.
 */
public class Planet {
	
	/**
	 * name of the planet presented as a signed 32-bit integer
	 */
	public int name;
	
	/**
	 * Position of the planet in the {@code Universe} of type {@code Position}
	 * @see Position
	 */
	public Position position;
	
	/**
	 * The planets size. the size is in the range (0, 1] with 1 representing the biggest planet possible. The size influences how quickly
	 * the military power of the planet grows.
	 */
	public float size;
	
	/**
	 * The military power of the planet. This value will increase linearly in accordance to the size of the planet. It can also be
	 * increased by a reinforcement from an allied planet. The military power is spent when attacking or when defending from an attack
	 * If the military power goes below 0 the planet is conquered by the attacker
	 */
	public int power;
	
	/**
	 * Flag which tells if the planet is currently friendly to the player or not
	 */
	public boolean isAllied;
	
	/**
	 * Flag which tells if the planet is currently owned by the player or not
	 */
	public boolean isOwned;
	
	/**
	 * Flag which tells if the planet is currently owned by no player
	 */
	public boolean isNeutral;
	
	/**
	 * Color of the player that currently owns the planet. "null" if neutral
	 */
	public String color;
	
	/**
	 * Flag which tells if the planet has already send a {@code Fleet} this round. Planets can sent a maximum of 1 fleet
	 * per round
	 */
	public boolean hasDeployed = false;
	
	public Planet(int name, Position position, float size, int power, boolean isAllied,
	              boolean isOwned, boolean isNeutral, String color) {
		this.name = name;
		this.position = position;
		this.size = size;
		this.power = power;
		this.isAllied = isAllied;
		this.isOwned = isOwned;
		this.isNeutral = isNeutral;
		this.color = color;
	}
	
	public Planet(){
		name = 0;
		position = new Position(0,0);
		size = 1f;
		power = 0;
		isOwned = false;
		isAllied = false;
		isNeutral = false;
		color = "unknown";
	}
	
	public Planet(Planet planet){
		this.name = planet.name;;
		this.position = new Position(planet.position.x, planet.position.y);
		this.size = planet.size;
		this.power = planet.power;
		this.isOwned = planet.isOwned;
		this.isAllied = planet.isAllied;
		this.isNeutral = planet.isNeutral;
		this.color = planet.color;
		this.hasDeployed = planet.hasDeployed;
	}
	
	/**
	 * Gets the name of the planet
	 * @return {@code int} representing the name of the planet
	 */
	public int getName() {
		return name;
	}
	
	/**
	 * Gets the planet'ss position in the universe
	 * @return {@code Position} of the planet
	 */
	public Position getPosition() {
		return position;
	}
	
	/**
	 * Gets the planet's site
	 * @return {@code float} representation of the planet's size
	 */
	public float getSize() {
		return size;
	}
	
	/**
	 * Gets the military power of the planet
	 * @return {@code int} representing the military power of the planet
	 */
	public int getPower() {
		return power;
	}
	
	/**
	 * Gets the planet's alliance status
	 * @return {@code true} if the planet is owned by the player of their teammate
	 */
	public boolean isAllied() {
		return isAllied;
	}
	
	/**
	 * Gets the planet's ownership status
	 * @return {@code true} if the planet is owned by the player
	 */
	public boolean isOwned() {
		return isOwned;
	}
	
	/**
	 * Gets the planet's neutrality status
	 * @return {@code true} if the planet is owned by no player
	 */
	public boolean isNeutral() {
		return isNeutral;
	}
	
	/**
	 * Gets the planet's color
	 * @return {@code String} representing the color of the planet
	 */
	public String getColor() {
		return color;
	}
	
	/**
	 * Gets the planets deployment status
	 * @return {@code true} if the planet has already deployed a fleet in this turn
	 */
	public boolean isHasDeployed() {
		return hasDeployed;
	}
	
	/**
	 * Checks if this {@code Planet} is the same as {@code planet} by checking their names
	 * @param planet {@code Planet} for which we are checking if it's the same planet as this one
	 * @return {@code true} if the names match {@code false} otherwise
	 */
	public boolean equals(Planet planet) {
		return name == planet.name;
	}
	
	/**
	 * Returns the distance of this planet from {@code planet}
	 * @param planet {@code Planet} for which we are checking distance
	 * @return {@code double} representing the distance between the 2 planets
	 */
	public double getDistanceFrom(Planet planet){
		return Math.sqrt(Math.pow(this.position.x - planet.position.x, 2) + Math.pow(this.position.y - planet.position.y, 2));
	}
	
	@Override
	public String toString() {
		return "{" +
				       "\"type\": \"Planet\"," +
				       String.format("\"name\": %d,", name) +
				       String.format("\"position\": %s,", position) +
				       String.format("\"size\": %s,", size) +
				       String.format("\"power\": %d,", power) +
				       String.format("\"isAllied\": %s,", isAllied) +
				       String.format("\"isOwned\": %s,", isOwned) +
				       String.format("\"isNeutral\": %s,", isNeutral) +
				       String.format("\"color\": \"%s\"", color) +
				       "}";
	}
}
