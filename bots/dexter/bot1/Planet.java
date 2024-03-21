import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Planet {

    private final Universe universe;
    private final PlanetManager planetManager;

    private final int name; // name of first planet in game is 0
    private final int positionX;
    private final int positionY;
    private final float planetSize;
    private int fleetSize;
    private String planetColor;

    // Contains distance from this planet, to every other planet
    private double utility;
    private int avgDistanceToOtherPlanetsInTurns;
    private int[] distanceTable;
    // indices are basically turns needed for fleets to arrive
    // keys in hashmap are IDs of individual fleets
    private final HashMap<Integer, Fleet>[] defenders;
    private final HashMap<Integer, Fleet>[] attackers;


    public Planet(int name, int x, int y, float pSize, int fSize, String color, PlanetManager planetManager) {
        this.planetManager = planetManager;
        this.universe = planetManager.getUniverse();
        this.name = name;
        this.positionX = x;
        this.positionY = y;
        this.planetSize = pSize;
        this.fleetSize = fSize;
        this.planetColor = color;
        this.defenders = new HashMap[universe.getMaxPlanetDistanceInTurns()];
        for (int i = 0; i < universe.getMaxPlanetDistanceInTurns(); i++) {
            defenders[i] = new HashMap<>();
        }
        this.attackers = new HashMap[universe.getMaxPlanetDistanceInTurns()];
        for (int i = 0; i < universe.getMaxPlanetDistanceInTurns(); i++) {
            attackers[i] = new HashMap<>();
        }
    }


    public int[] attackersInTurns() {
        int[] a = new int[attackers.length];
        for (int i = 0; i < attackers.length; i++) {
            HashMap<Integer, Fleet> attackerMap = attackers[i];
            for (Fleet fleet : attackerMap.values()) {
                a[i] += fleet.getFleetSize();
            }
        }
        return a;
    }

    public int attackersInNTurns(int nTurns) {
        int a = 0;
        HashMap<Integer, Fleet> attackerMap = attackers[nTurns];
        for (Fleet fleet : attackerMap.values()) {
            a += fleet.getFleetSize();
        }
        return a;
    }

    public int defendersInNTurns(int nTurns) {
        int d = 0;
        HashMap<Integer, Fleet> attackerMap = defenders[nTurns];
        for (Fleet fleet : attackerMap.values()) {
            d += fleet.getFleetSize();
        }
        return d;
    }


    public int getNumberOfFleetsOverNTurns(int nTurns) {
        // number of reinforcements + generated fleets
        // for now just for simplicity sake only taking into a count generated
        return (int) (this.planetSize * 10 * nTurns);
    }


    public void calculateUtility(Planet sourcePlanet, double alpha) {
        this.utility = (planetSize * 10) / Math.pow(this.getDistanceInTurns(sourcePlanet),alpha);
    }


    // TODO: Make sure real time needed turns for index are calculated correctly
    public boolean isTrackingFleet(int currentTurn, int neededTurns, int fleetName) {
        return  attackers[neededTurns - currentTurn + 1].containsKey(fleetName) ||
                defenders[neededTurns - currentTurn + 1].containsKey(fleetName);
    }

    public void updateFleet(int currentTurn, int neededTurns, int fleetName) {
        Fleet fleet;
        fleet = attackers[neededTurns - currentTurn + 1].get(fleetName);
        attackers[neededTurns - currentTurn + 1].remove(fleetName);
        if (fleet == null) {
            fleet = defenders[neededTurns - currentTurn + 1].get(fleetName);
            defenders[neededTurns - currentTurn + 1].remove(fleetName);
        }
        fleet.setCurrentTurn(currentTurn);
        if (isAttacker(fleet.getFleetColor())) {
            attackers[neededTurns - currentTurn].put(fleetName, fleet);
        } else {
            defenders[neededTurns - currentTurn].put(fleetName, fleet);
        }
    }

    public void trackFleet(Fleet fleet, boolean attacker) {
        if (attacker) {
            attackers[fleet.getNeededTurns() - fleet.getCurrentTurn()].put(fleet.getFleetName(), fleet);
        } else {
            defenders[fleet.getNeededTurns() - fleet.getCurrentTurn()].put(fleet.getFleetName(), fleet);
        }
    }

    private boolean isAttacker(String fleetColor) {
        return !(fleetColor.equals(universe.getMyColor()) || fleetColor.equals(universe.getTeammateColor()));
    }


    public boolean isOwnershipChanged(String newColor) {
        return !(Objects.equals(planetColor, newColor));
    }

    public int getDistanceInTurns(Planet other) {
        double euclidean = Math.sqrt(Math.pow((this.positionX - other.positionX), 2) + Math.pow((this.positionY - other.positionY), 2));
        return (int) euclidean / 2;
    }




    public void clearInactiveFleets() {
        attackers[0] = new HashMap<>();
        attackers[1] = new HashMap<>();
        defenders[0] = new HashMap<>();
        defenders[1] = new HashMap<>();
    }

    /**
     *  If there is no enemy or neutral planet in universe, the function returns -1;
     */
    public int getClosestEnemy() {
        int name = -1;
        int distance = Integer.MAX_VALUE;
        for (int i = 0; i < distanceTable.length; i++) {
            if (distance > distanceTable[i] && planetManager.getPlanet(i).isEnemy()) {
                distance = distanceTable[i];
                name = i;
            }
        }
        return name;
    }

    public boolean isEnemy() {
        return  !this.planetColor.equals(universe.getMyColor()) && !this.planetColor.equals(universe.getTeammateColor());
    }

    public int getName() {
        return name;
    }

    public boolean isNeutral() {
        return this.planetColor.equals("null");
    }
    
    public double getUtility() {
        return utility;
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public int getFleetSize() {
        return fleetSize;
    }

    public String getPlanetColor() {
        return planetColor;
    }

    public void setFleetSize(int fleetSize) {
        this.fleetSize = fleetSize;
    }

    public void setPlanetColor(String planetColor) {
        this.planetColor = planetColor;
    }

    public void setAvgDistanceToOtherPlanetsInTurns(int avgDistanceToOtherPlanetsInTurns) {
        this.avgDistanceToOtherPlanetsInTurns = avgDistanceToOtherPlanetsInTurns;
    }

    public int getAvgDistanceToOtherPlanetsInTurns() {
        return avgDistanceToOtherPlanetsInTurns;
    }

    public void setDistanceTable(int[] distanceTable) {
        this.distanceTable = distanceTable;
    }
}
