import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class PlanetManager {

    private boolean distanceTablesInitialized = false;

    private final Universe universe;
    private final HashMap<Integer, Planet> planets;
    private final HashSet<Integer> myPlanets;
    private final HashSet<Integer> tmPlanets;
    private final HashSet<Integer> e1Planets;
    private final HashSet<Integer> e2Planets;
    private final HashSet<Integer> nPlanets;


    public PlanetManager(Universe universe) {
        this.universe = universe;
        this.planets = new HashMap<>();
        this.myPlanets = new HashSet<>();
        this.tmPlanets = new HashSet<>();
        this.e1Planets = new HashSet<>();
        this.e2Planets = new HashSet<>();
        this.nPlanets = new HashSet<>();
    }


    public void parse(String lineFromGame) {

        String[] tokens = lineFromGame.split(" ");
        int name = Integer.parseInt(tokens[1]);
        int x = Integer.parseInt(tokens[2]);
        int y = Integer.parseInt(tokens[3]);
        float pSize = Float.parseFloat(tokens[4]);
        int fSize = Integer.parseInt(tokens[5]);

        Planet p;
        if ((p = planets.get(name)) != null) {
            if (p.isOwnershipChanged(tokens[6])) {
                removePlanetName(name, p.getPlanetColor());
                addPlanetName(name, tokens[6]);
            }
            updatePlanet(name, fSize, tokens[6]);
            return;
        }

        p = new Planet(name, x, y, pSize, fSize, tokens[6], this);
        planets.put(name, p);
        addPlanetName(name, tokens[6]);
    }


    private void updatePlanet(int name, int fleetSize, String color) {
        Planet p = planets.get(name);
        p.setFleetSize(fleetSize);
        p.setPlanetColor(color);
    }


    private void removePlanetName(int name, String previousColor) {
        if (previousColor.equals(universe.getMyColor())) {
            myPlanets.remove(name);
        } else if (previousColor.equals(universe.getTeammateColor())) {
            tmPlanets.remove(name);
        } else if (previousColor.equals(universe.getEnemy1Color())) {
            e1Planets.remove(name);
        } else if (previousColor.equals(universe.getEnemy2Color())) {
            e2Planets.remove(name);
        } else {
            nPlanets.remove(name);
        }
    }

    private void addPlanetName(int name, String newColor) {
        if (newColor.equals(universe.getMyColor())) {
            myPlanets.add(name);
        } else if (newColor.equals(universe.getTeammateColor())) {
            tmPlanets.add(name);
        } else if (newColor.equals(universe.getEnemy1Color())) {
            e1Planets.add(name);
        } else {
            nPlanets.add(name);
        }
    }


    public void initDistanceTables() {
        // first planet in game has index 0
        for (int i = 0; i < planets.size(); i++) {

            Planet p = planets.get(i);
            int[] distanceTable = new int[planets.size()];
            int avgDstInTurns = 0;
            for (int j = 0; j < planets.size(); j++) {
                Planet o = planets.get(j);
                int dst = p.getDistanceInTurns(o);
                distanceTable[j] = dst;
                avgDstInTurns += dst;
            }
            p.setAvgDistanceToOtherPlanetsInTurns(avgDstInTurns / planets.size());
            p.setDistanceTable(distanceTable);
        }
        distanceTablesInitialized = true;
    }



    public ArrayList<Integer> getMyPlanetIds() {
        return new ArrayList<>(myPlanets);
    }

    public ArrayList<Planet> getMyPlanets() {
        ArrayList<Planet> pa = new ArrayList<>();
        for (int pid : myPlanets) {
            pa.add(planets.get(pid));
        }
        return pa;
    }

    public ArrayList<Planet> getCandidatePlanets() {
        ArrayList<Planet> pc = new ArrayList<>();
        for (int pid : nPlanets) pc.add(planets.get(pid));
        for (int pid : e1Planets) pc.add(planets.get(pid));
        for (int pid : e2Planets) pc.add(planets.get(pid));
        return pc;
    }



    public ArrayList<Integer> getAllPotentialTargets() {
        ArrayList<Integer> potentialTargets = new ArrayList<>();
        potentialTargets.addAll(e1Planets);
        potentialTargets.addAll(e2Planets);
        potentialTargets.addAll(nPlanets);
        return potentialTargets;
    }

    public Planet getPlanet(int name) {
        return planets.get(name);
    }


    public void clearInactiveFleets() {
        for (Planet planet : planets.values()) {
            planet.clearInactiveFleets();
        }
    }

    /**
     *  This function should not be called before all planets are initialized.
     *  Returns total number of planets.
     */
    public int getNumberOfPlanets() {
        return planets.size();
    }

    public Universe getUniverse() {
        return universe;
    }

    public boolean isDistanceTablesInitialized() {
        return distanceTablesInitialized;
    }
}
