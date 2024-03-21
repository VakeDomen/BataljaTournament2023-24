import java.util.*;

public class FleetManager {

    private final Universe universe;
    private final PlanetManager planetManager;


    public FleetManager(Universe universe, PlanetManager planetManager) {
        this.universe = universe;
        this.planetManager = planetManager;
    }



    public void parse(String lineFromGame) {

        String[] tokens = lineFromGame.split(" ");
        int fleetName = Integer.parseInt(tokens[1]);
        int fleetSize = Integer.parseInt(tokens[2]);
        int origin = Integer.parseInt(tokens[3]);
        int destination = Integer.parseInt(tokens[4]);
        int currentTurn = Integer.parseInt(tokens[5]);
        int neededTurns = Integer.parseInt(tokens[6]);

        Planet planet = planetManager.getPlanet(destination);

        if (planet.isTrackingFleet(currentTurn, neededTurns, fleetName)) {
            planet.updateFleet(currentTurn, neededTurns, fleetName);
            return;
        }
        planet.trackFleet(
                new Fleet(fleetName, fleetSize, origin, destination, currentTurn, neededTurns, tokens[7]),
                isAttacker(tokens[7])
        );
    }


    public void clearInactiveFleets() {
        planetManager.clearInactiveFleets();
    }


    private boolean isAttacker(String fleetColor) {
        return !(fleetColor.equals(universe.getMyColor()) || fleetColor.equals(universe.getTeammateColor()));
    }

    // F 422 1 24 18 8 9 cyan --> this is the last report about fleet
}
