import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * The {@code Universe} class is responsible for holding entity data. This class is used in {@code GameState} to hold all entity data
 * for the game. The {@code Universe} holds the data about all the {@code Planet}s found in the game and any {@code Fleet}s traveling
 * through the {@code Universe}. The class also provides methods to access the entities inside the {@code Universe} either individually
 * or grouped together as part of a collective set with something in common (e.g. all Planets owned by the Player).
 * @see GameState
 */
public class Universe {
	/**
	 * Width of the {@code Universe}
	 */
	public int width;
	
	/**
	 * Height of the {@code Universe}
	 */
	public int height;
	
	/**
	 * {@code ArrayList} holding references to entities  of type {@code Planet} that can be found inside the {@code Universe}
	 * @see Planet
	 */
	public ArrayList<Planet> planets;
	
	/**
	 * {@code ArrayList} holding references to entities of type {@code Fleet} that can be found inside {@code Universe}
	 * @see Fleet
	 */
	public ArrayList<Fleet> fleets;
	
	/**
	 * Returns the {@code Planet} from the {@code Universe} with the provided {@code name}. If the planet is not found it simply
	 * returns {@code null}
	 * @param name name of the {@code Planet} you want to get from the game field
	 * @return {@code Planet} with the given name or {@code null} if not found
	 */
	public Planet getPlanet(int name){
		for(Planet planet: planets){
			if (planet.name == name) return planet;
		}
		return null;
	}
	
	/**
	 * Returns all entities of type {@code Planet} from the {@code Universe}
	 * which match a given condition in the form of a {@code Predicate}.
	 * @param predicate The condition under which a {@code Planet} should be included in the return
	 * @return {@code Planet[]} containing all {@code Planet}s matching the given {@code predicate}
	 * @see Predicate
	 */
	public Planet[] getPlanets(Predicate<Planet> predicate){
		return getPlanetsStream(predicate).toArray(Planet[]::new);
	}
	
	/**
	 * Returns a {@code Stream} all entities of type {@code Planet} from the {@code Universe}
	 * which match a given condition in the form of a {@code Predicate}.
	 * @param predicate The condition under which a {@code Planet} should be included in the return
	 * @return {@code Stream<Planet>} formed by filtering according to the predicate
	 * @see Predicate
	 */
	
	public Stream<Planet> getPlanetsStream(Predicate<Planet> predicate){
		return planets.stream().filter(predicate);
	}
	
	/**
	 * Returns all entities of type {@code Planet} from the {@code Universe} which the Player is the owner off
	 * @return {@code Planet[]} containing all {@code Planet}s owned by the player
	 */
	public Planet[] getOwnedPlanets(){
		return getPlanets(Planet::isOwned);
	}
	
	/**
	 * Returns a {@code Stream} of all entities of type {@code Planet} from the {@code Universe} which the Player is the owner off
	 * @return {@code Stream<Planet>} containing all {@code Planet}s owned by the player
	 */
	public Stream<Planet> getOwnedPlanetsStream(){
		return getPlanetsStream(Planet::isOwned);
	}
	
	/**
	 * Returns all entities of type {@code Planet} from the {@code Universe} which are owned by the Players teammate
	 * @return {@code Planet[]} containing all {@code Planet}s owned by the players Teammate
	 */
	public Planet[] getTeammatePlanets(){
		return getPlanets(planet -> planet.isAllied && !planet.isOwned);
	}
	
	/**
	 * Returns a {@code Stream} of all entities of type {@code Planet} from the {@code Universe} which are owned by the Players teammate
	 * @return {@code Stream<Planet>} containing all {@code Planet}s owned by the players Teammate
	 */
	public Stream<Planet> getTeammatePlanetsStream(){
		return getPlanetsStream(planet -> planet.isAllied && !planet.isOwned);
	}
	
	/**
	 * Returns all entities of type {@code Planet} from the {@code Universe} which are not owned by the Player or
	 * their Teammate. These planets either belong to one of the enemy players or are neutral. They can be simply referred
	 * to as enemy planets. To obtain only planets belonging to enemy players see {@link #getEnemyPlayerPlanets()} or
	 * {@link #getNeutralPlanets()} if you only want neutral {@code Planet}s
	 * @return {@code Planet[]} containing all enemy {@code Planet}s
	 */
	public Planet[] getEnemyPlanets(){
		return  getPlanets(planet -> !planet.isAllied);
	}
	
	/**
	 * Returns a {@code Stream} of all entities of type {@code Planet} from the {@code Universe} which are not owned by the Player or
	 * their Teammate. These planets either belong to one of the enemy players or are neutral. They can be simply referred
	 * to as enemy planets. To obtain only planets belonging to enemy players see {@link #getEnemyPlayerPlanetsStream()} or
	 * {@link #getNeutralPlanetsStream()} if you only want neutral {@code Planet}s
	 * @return {@code Stream<Planet>} containing all enemy {@code Planet}s
	 */
	public Stream<Planet> getEnemyPlanetsStream(){
		return  getPlanetsStream(planet -> !planet.isAllied);
	}
	
	/**
	 * Returns all entities of type {@code Planet} owned by enemy players.
	 * These planets are also included among enemy {@code Planet}s obtained from {@link #getEnemyPlanets()}
	 * @return {@code Planet[]} containing all enemy {@code Planet}s owned by Players
	 */
	public Planet[] getEnemyPlayerPlanets(){
		return  getPlanets(planet -> !planet.isAllied && !planet.isNeutral);
	}
	
	/**
	 * Returns a {@code Stream} pf all entities of type {@code Planet} owned by enemy players.
	 * These planets are also included among enemy {@code Planet}s obtained from {@link #getEnemyPlanetsStream()}
	 * @return {@code Stream<Planet>} containing all enemy {@code Planet}s owned by Players
	 */
	public Stream<Planet> getEnemyPlayerPlanetsStream(){
		return  getPlanetsStream(planet -> !planet.isAllied && !planet.isNeutral);
	}
	
	/**
	 * Returns all entities of type {@code Planet} owned by no players be it ally or enemy. These {@code Planet}s do not grow or attack.
	 * These planets are also included among enemy {@code Planet}s obtained from {@link #getEnemyPlanets()}
	 * @return {@code Planet[]} containing all neutral {@code Planet}s
	 */
	public Planet[] getNeutralPlanets(){
		return  getPlanets(Planet::isNeutral);
	}
	
	/**
	 * Returns a {@code Stream} of all entities of type {@code Planet} owned by no players be it ally or enemy. These {@code Planet}s do not grow or attack.
	 * These planets are also included among enemy {@code Planet}s obtained from {@link #getEnemyPlanetsStream()}
	 * @return {@code Stream<Planet>} containing all neutral {@code Planet}s
	 */
	public Stream<Planet> getNeutralPlanetsStream(){
		return  getPlanetsStream(Planet::isNeutral);
	}
	
	/**
	 * Returns all entities of type {@code Planet} that are of a given color. Although this function can return if provided the color
	 * "null" it is recommended to use {@link #getNeutralPlanets()} instead. If the {@code color} given is not a valid color the
	 * array returned will be empty.
	 * @param color what color of {@code Planet}s are we searching for
	 * @return {@code Planet[]} containing all {@code Planet}s of a given {@code color}
	 */
	public Planet[] getPlanets(String color){
		return  getPlanets(planet -> planet.color.equals(color));
	}
	
	/**
	 * Returns a {@code Stream} of all entities of type {@code Planet} that are of a given color. Although this function can return if provided the color
	 * "null" it is recommended to use {@link #getNeutralPlanetsStream()} instead. If the {@code color} given is not a valid color the
	 * {@code Stream} returned will be empty.
	 * @param color what color of {@code Planet}s are we searching for
	 * @return {@code Stream<Planet>} containing all {@code Planet}s of a given {@code color}
	 */
	public Stream<Planet> getPlanetsStream(String color){
		return  getPlanetsStream(planet -> planet.color.equals(color));
	}
	
	/**
	 * Returns all entities of type {@code Fleet} from the {@code Universe}
	 * which match a given condition in the form of a {@code Predicate}.
	 * @param predicate The condition under which a {@code Fleet} should be included in the return
	 * @return {@code Fleet[]} containing all {@code Fleet}s matching the given {@code predicate}
	 * @see Predicate
	 */
	public Fleet[] getFleets(Predicate<Fleet> predicate){
		return getFleetsStream(predicate).toArray(Fleet[]::new);
	}
	
	/**
	 * Returns a {@code Stream} of all entities of type {@code Fleet} from the {@code Universe}
	 * which match a given condition in the form of a {@code Predicate}.
	 * @param predicate The condition under which a {@code Fleet} should be included in the return
	 * @return {@code Stream<Fleet>} containing all {@code Fleet}s matching the given {@code predicate}
	 * @see Predicate
	 */
	public Stream<Fleet> getFleetsStream(Predicate<Fleet> predicate){
		return fleets.stream().filter(predicate);
	}
	
	/**
	 * Returns all entities of type {@code Fleet} which have been sent from one of the owned {@code Planets} currently found in the
	 * {@code Universe}. If no {@code Fleet}s have been sent yet or none are currently flying through the {@code Universe} the
	 * array returned will be empty
	 * @return {@code Fleet[]} containing all active fleets sent by the Player
	 */
	public Fleet[] getOwnedFleets(){
		return getFleets(Fleet::isOwned);
	}
	
	/**
	 * Returns a {@code Stream} of all entities of type {@code Fleet} which have been sent from one of the owned {@code Planets} currently found in the
	 * {@code Universe}. If no {@code Fleet}s have been sent yet or none are currently flying through the {@code Universe} the
	 * {@code Stream} returned will be empty
	 * @return {@code Stream<Fleet>} containing all active fleets sent by the Player
	 */
	public Stream<Fleet> getOwnedFleetsStream(){
		return getFleetsStream(Fleet::isOwned);
	}
	
	/**
	 * Returns all entities of type {@code Fleet} which have been sent from a {@code Planet} owned by the Player's Teammate currently
	 * found in the {@code Universe}. In no {@code Fleet}s have been sent yet or none are currently flying through the {@code Universe}
	 * the array returned will be empty
	 * @return {@code Fleet[]} containing all active fleets sent by the Player's teammate
	 */
	public Fleet[] getTeammateFleets(){
		return  getTeammateFleetsStream().toArray(Fleet[]::new);
	}
	
	/**
	 * Returns a {@code Stream} of all entities of type {@code Fleet} which have been sent from a {@code Planet} owned by the Player's Teammate currently
	 * found in the {@code Universe}. In no {@code Fleet}s have been sent yet or none are currently flying through the {@code Universe}
	 * the {@code Stream} returned will be empty
	 * @return {@code Stream<Fleet>} containing all active fleets sent by the Player's teammate
	 */
	public Stream<Fleet> getTeammateFleetsStream(){
		return  getFleetsStream(fleet -> fleet.isAllied && !fleet.isOwned);
	}
	
	/**
	 * Returns all entities of type {@code Fleet} which have been sent from a {@code Planet} owned by one of the enemy Players that
	 * can be currently found in the {@code Universe}. If no fleets are currently flying through the {@code Universe} the array returned
	 * will be empty (Which is great). The array will never contain a fleet sent from a neutral {@code Planet} as those {@code Planet}s
	 * are unable to send {@code Fleet}s
	 * @return {@code Fleet[]} containing all active fleets sent by an enemy Player.
	 */
	public Fleet[] getEnemyFleets(){
		return getFleets(fleet -> !fleet.isAllied);
	}
	
	/**
	 * Returns a {@code Stream} of all entities of type {@code Fleet} which have been sent from a {@code Planet} owned by one of the enemy Players that
	 * can be currently found in the {@code Universe}. If no fleets are currently flying through the {@code Universe} the {@code Stream} returned
	 * will be empty (Which is great). The {@code Stream} will never contain a fleet sent from a neutral {@code Planet} as those {@code Planet}s
	 * are unable to send {@code Fleet}s
	 * @return {@code Stream<Fleet>} containing all active fleets sent by an enemy Player.
	 */
	public Stream<Fleet> getEnemyFleetsStream(){
		return getFleetsStream(fleet -> !fleet.isAllied);
	}
	
	/**
	 * Returns all entities of type {@code Fleet} which have their destination set to a {@code Planet} with the name {@code planet}.
	 * The fleets may be travelling to the {@code Planet} as either attackers or reinforcement. For an array of attacking fleets check
	 * {@link #getFleetsAttacking(int)} or {@link #getFleetsReinforcing(int)} for those reinforcing the target. The array will be empty
	 * if no active fleets have the {@code Planet} set at their destination.
	 * @param planet name of the destination {@code Planet} the {@code Fleet}s returned are destined for
	 * @return {@code Fleet[]} of {@code Fleet}s whose destination is {@code Planet} named {@code planet}
	 * @see #getFleetsAttacking(int)
	 * @see #getFleetsReinforcing(int)
	 */
	public Fleet[] getFleetsDestinedFor(int planet){
		Planet destination = getPlanet(planet);
		return  getFleetsDestinedFor(destination);
	}
	
	/**
	 * Returns a {@code Stream} of all entities of type {@code Fleet} which have their destination set to a {@code Planet} with the name {@code planet}.
	 * The fleets may be travelling to the {@code Planet} as either attackers or reinforcement. For an {@code Stream} of attacking fleets check
	 * {@link #getFleetsAttackingStream(int)} or {@link #getFleetsReinforcingStream(int)} for those reinforcing the target. The {@code Stream} will be empty
	 * if no active fleets have the {@code Planet} set at their destination.
	 * @param planet name of the destination {@code Planet} the {@code Fleet}s returned are destined for
	 * @return {@code Stream<Fleet>} of {@code Fleet}s whose destination is {@code Planet} named {@code planet}
	 * @see #getFleetsAttackingStream(int)
	 * @see #getFleetsReinforcingStream(int)
	 */
	public Stream<Fleet> getFleetsDestinedForStream(int planet){
		Planet destination = getPlanet(planet);
		return  getFleetsDestinedForStream(destination);
	}
	
	/**
	 * Returns all entities of type {@code Fleet} which have their destination set to the {@code Planet} named {@code planet}
	 * The function only returns {@code Fleet}s which are attackers from the point of view of the {@code Planet}. For an array of all
	 * {@code Fleet}s that are destined for the {@code Planet} see {@link #getFleetsDestinedFor(int)} and for an array of {@code Fleet}s
	 * reinforcing the {@code Planet} check {@link #getFleetsReinforcing(int)} instead. The returned array will be empty if no active {@code Fleet}s
	 * are attacking the {@code Planet}.
	 * @param planet name of the destination {@code Planet} for which we are finding attacking {@code Fleet}s
	 * @return {@code Fleet[]} containing all {@code Fleet}s destined for {@code Planet} named {@code planet} with the purpose of attacking
	 * @see #getFleetsDestinedFor(int)
	 * @see #getFleetsReinforcing(int)
	 */
	public Fleet[] getFleetsAttacking(int planet){
		Planet destination = getPlanet(planet);
		return  getFleetsAttacking(destination);
	}
	
	/**
	 * Returns a {@code Stream} of all entities of type {@code Fleet} which have their destination set to the {@code Planet} named {@code planet}
	 * The function only returns {@code Fleet}s which are attackers from the point of view of the {@code Planet}. For an {@code Stream} of all
	 * {@code Fleet}s that are destined for the {@code Planet} see {@link #getFleetsDestinedForStream(int)} and for an array of {@code Fleet}s
	 * reinforcing the {@code Planet} check {@link #getFleetsReinforcingStream(int)} instead. The returned {@code Stream} will be empty if no active {@code Fleet}s
	 * are attacking the {@code Planet}.
	 * @param planet name of the destination {@code Planet} for which we are finding attacking {@code Fleet}s
	 * @return {@code Stream<Fleet>} containing all {@code Fleet}s destined for {@code Planet} named {@code planet} with the purpose of attacking
	 * @see #getFleetsDestinedForStream(int)
	 * @see #getFleetsReinforcingStream(int)
	 */
	public Stream<Fleet> getFleetsAttackingStream(int planet){
		Planet destination = getPlanet(planet);
		return  getFleetsAttackingStream(destination);
	}
	
	/**
	 * Returns all entities of type {@code Fleet} which have their destination to the {@code Planet} named {@code planet}
	 * The function only returns {@code Fleet}s which are allied to the destination {@code Planet} and their purpose is reinforcement.
	 * For {@code Fleets} destined for {@code Planet} with the purpose of attacking see {@link #getFleetsAttacking(int)} or
	 * {@link #getFleetsDestinedFor(int)} for all {@code Fleet}s destined for {@code Planet}. The returned array will be empty if
	 * there are no active {@code Fleet}s active destined for {@code Planet} with their purpose of reinforcing the {@code Planet}
	 * @param planet name of the destination {@code Planet} for which we are finding reinforcing {@code Fleet}s
	 * @return {@code Fleet[]} containing all {@code Fleet}s destined for {@code Planet} with their purpose being reinforcement
	 * @see #getFleetsDestinedFor(int) 
	 * @see #getFleetsAttacking(int) 
	 */
	public Fleet[] getFleetsReinforcing(int planet){
		Planet destination = getPlanet(planet);
		return  getFleetsReinforcing(destination);
	}
	
	/**
	 * Returns {@code Stream} all entities of type {@code Fleet} which have their destination to the {@code Planet} named {@code planet}
	 * The function only returns {@code Fleet}s which are allied to the destination {@code Planet} and their purpose is reinforcement.
	 * For {@code Fleets} destined for {@code Planet} with the purpose of attacking see {@link #getFleetsAttackingStream(int)} or
	 * {@link #getFleetsDestinedForStream(int)} for all {@code Fleet}s destined for {@code Planet}. The returned a{@code Stream} will be empty if
	 * there are no active {@code Fleet}s active destined for {@code Planet} with their purpose of reinforcing the {@code Planet}
	 * @param planet name of the destination {@code Planet} for which we are finding reinforcing {@code Fleet}s
	 * @return {@code Stream<Fleet>} containing all {@code Fleet}s destined for {@code Planet} with their purpose being reinforcement
	 * @see #getFleetsDestinedForStream(int)
	 * @see #getFleetsAttackingStream(int)
	 */
	public Stream<Fleet> getFleetsReinforcingStream(int planet){
		Planet destination = getPlanet(planet);
		return  getFleetsReinforcingStream(destination);
	}
	
	/**
	 * Returns all entities of type {@code Fleet} which have their destination set to {@code planet}.
	 * The fleets may be travelling to the {@code Planet} as either attackers or reinforcement. For an array of attacking fleets check
	 * {@link #getFleetsAttacking(Planet)} or {@link #getFleetsReinforcing(Planet)} for those reinforcing the target. The array will be empty
	 * if no active fleets have the {@code Planet} set at their destination.
	 * @param planet {@code Planet} the {@code Fleet}s returned are destined for
	 * @return {@code Fleet[]} of {@code Fleet}s whose destination is {@code planet}
	 * @see #getFleetsAttacking(Planet)
	 * @see #getFleetsReinforcing(Planet)
	 */
	public Fleet[] getFleetsDestinedFor(Planet planet){
		return getFleets(fleet -> fleet.destination.equals(planet));
	}
	
	/**
	 * Returns a {@code Stream} of all entities of type {@code Fleet} which have their destination set to {@code planet}.
	 * The fleets may be travelling to the {@code Planet} as either attackers or reinforcement. For an {@code Stream} of attacking fleets check
	 * {@link #getFleetsAttackingStream(Planet)} or {@link #getFleetsReinforcingStream(Planet)} for those reinforcing the target. The {@code Stream} will be empty
	 * if no active fleets have the {@code Planet} set at their destination.
	 * @param planet {@code Planet} the {@code Fleet}s returned are destined for
	 * @return {@code Stream<Fleet>} of {@code Fleet}s whose destination is {@code planet}
	 * @see #getFleetsAttackingStream(Planet)
	 * @see #getFleetsReinforcingStream(Planet)
	 */
	public Stream<Fleet> getFleetsDestinedForStream(Planet planet){
		return getFleetsStream(fleet -> fleet.destination.equals(planet));
	}
	
	/**
	 * Returns all entities of type {@code Fleet} which have their destination set to the {@code planet}
	 * The function only returns {@code Fleet}s which are attackers from the point of view of the {@code Planet}. For an array of all
	 * {@code Fleet}s that are destined for the {@code Planet} see {@link #getFleetsDestinedFor(Planet)} and for an array of {@code Fleet}s
	 * reinforcing the {@code Planet} check {@link #getFleetsReinforcing(Planet)} instead. The returned array will be empty if no active {@code Fleet}s
	 * are attacking the {@code Planet}.
	 * @param planet {@code Planet} for which we are finding attacking {@code Fleet}s
	 * @return {@code Fleet[]} containing all {@code Fleet}s destined for {@code planet} with the purpose of attacking
	 * @see #getFleetsDestinedFor(Planet)
	 * @see #getFleetsReinforcing(Planet)
	 */
	public Fleet[] getFleetsAttacking(Planet planet){
		return getFleetsAttackingStream(planet).toArray(Fleet[]::new);
	}
	/**
	 * Returns a {@code Stream} of all entities of type {@code Fleet} which have their destination set to the {@code planet}
	 * The function only returns {@code Fleet}s which are attackers from the point of view of the {@code Planet}. For an {@code Stream} of all
	 * {@code Fleet}s that are destined for the {@code Planet} see {@link #getFleetsDestinedForStream(Planet)} and for an {@code Stream} of {@code Fleet}s
	 * reinforcing the {@code Planet} check {@link #getFleetsReinforcingStream(Planet)} instead. The returned {@code Stream} will be empty if no active {@code Fleet}s
	 * are attacking the {@code Planet}.
	 * @param planet {@code Planet} for which we are finding attacking {@code Fleet}s
	 * @return {@code Stream<Fleet>} containing all {@code Fleet}s destined for {@code planet} with the purpose of attacking
	 * @see #getFleetsDestinedForStream(Planet)
	 * @see #getFleetsReinforcingStream(Planet)
	 */
	public Stream<Fleet> getFleetsAttackingStream(Planet planet){
		return getFleetsDestinedForStream(planet).filter( fleet -> fleet.isAllied != planet.isAllied);
	}
	
	/**
	 * Returns all entities of type {@code Fleet} which have their destination to the {@code planet}
	 * The function only returns {@code Fleet}s which are allied to the destination {@code Planet} and their purpose is reinforcement.
	 * For {@code Fleets} destined for {@code Planet} with the purpose of attacking see {@link #getFleetsAttacking(Planet)} or
	 * {@link #getFleetsDestinedFor(Planet)} for all {@code Fleet}s destined for {@code Planet}. The returned array will be empty if
	 * there are no active {@code Fleet}s active destined for {@code Planet} with their purpose of reinforcing the {@code Planet}
	 * @param planet {@code Planet} for which we are finding reinforcing {@code Fleet}s
	 * @return {@code Fleet[]} containing all {@code Fleet}s destined for {@code Planet} with their purpose being reinforcement
	 * @see #getFleetsDestinedFor(Planet)
	 * @see #getFleetsAttacking(Planet)
	 */
	public Fleet[] getFleetsReinforcing(Planet planet){
		return getFleetsReinforcingStream(planet).toArray(Fleet[]::new);
	}
	
	/**
	 * Returns a {@code Stream} of all entities of type {@code Fleet} which have their destination to the {@code planet}
	 * The function only returns {@code Fleet}s which are allied to the destination {@code Planet} and their purpose is reinforcement.
	 * For {@code Fleets} destined for {@code Planet} with the purpose of attacking see {@link #getFleetsAttackingStream(Planet)} or
	 * {@link #getFleetsDestinedForStream(Planet)} for all {@code Fleet}s destined for {@code Planet}. The returned {@code Stream} will be empty if
	 * there are no active {@code Fleet}s active destined for {@code Planet} with their purpose of reinforcing the {@code Planet}
	 * @param planet {@code Planet} for which we are finding reinforcing {@code Fleet}s
	 * @return {@code Stream<Fleet>} containing all {@code Fleet}s destined for {@code Planet} with their purpose being reinforcement
	 * @see #getFleetsDestinedForStream(Planet)
	 * @see #getFleetsAttackingStream(Planet)
	 */
	public Stream<Fleet> getFleetsReinforcingStream(Planet planet){
		return getFleetsDestinedForStream(planet).filter( fleet -> fleet.isAllied == planet.isAllied);
	}
}