import java.io.BufferedReader;
import java.util.*;
import java.util.stream.Collectors;

public class State {
    LinkedList<Planet> planets;
    LinkedList<Fleet> fleets;
    LinkedList<Planet> enemyPlanets;
    LinkedList<Planet> grayPlanets;
    LinkedList<Planet> myPlanets;
    LinkedList<Planet> teammatePlanets;
    Map<Integer, Set<Planet>> neighborsMap;

    public State() {
        this.planets = new LinkedList<>();
        this.fleets = new LinkedList<>();
        this.enemyPlanets = new LinkedList<>();
        this.grayPlanets = new LinkedList<>();
        this.myPlanets = new LinkedList<>();
        this.teammatePlanets = new LinkedList<>();
        parse();
        neighborsMap = buildNeighborsMap();
    }

    public State(LinkedList<Planet> planets, LinkedList<Fleet> fleets) {
        this.planets = planets;
        this.fleets = fleets;
        categorizePlanets();
    }

    public void parse() {
        BufferedReader stdin = new BufferedReader(new java.io.InputStreamReader(System.in));
        String line = "";
        try {
            while (!(line = stdin.readLine()).equals("S")) {
                Player.logToFile(line);
                String[] tokens = line.split(" ");
                char firstLetter = line.charAt(0);
                if (firstLetter == 'U') {
                    Player.universeWidth = Integer.parseInt(tokens[1]);
                    Player.universeHeight = Integer.parseInt(tokens[2]);
                    Player.myColor = tokens[3]; //state shou
                }
                else if (firstLetter == 'P') {
                    //String planetName = tokens[1];
                    // Fill the linked list of Planets
                    planets.add(new Planet(tokens));
                }
                else if (firstLetter == 'F') {
                    // Fill the linked list of Fleets
                    fleets.add(new Fleet(tokens));
                }
                else if(firstLetter =='M') {
                    Player.teammateColor=line;
                }
            }
            Player.logToFile("S");
        } catch (Exception e) {
            Player.logToFile("Error ");
        }
        categorizePlanets();

    }
    public void categorizePlanets() {
        String myColor = Player.myColor; // Assume this is set somewhere after parsing the universe state.

        myPlanets = planets.stream().filter(planet -> planet.color.equals(Player.myColor)).collect(Collectors.toCollection(LinkedList::new));
        grayPlanets = planets.stream().filter(planet -> "null".equals(planet.color)).collect(Collectors.toCollection(LinkedList::new));
        teammatePlanets = planets.stream().filter(planet -> planet.color.equals(Player.teammateColor)).collect(Collectors.toCollection(LinkedList::new));

        enemyPlanets = planets.stream().
                filter(planet -> !planet.color.equals(Player.teammateColor)).
                filter(planet -> !planet.color.equals(Player.myColor)).
                filter(planet -> !"null".equals(planet.color)).
                collect(Collectors.toCollection(LinkedList::new));
    }

    public Map<Integer, Set<Planet>> buildNeighborsMap() {
        Map<Integer, Set<Planet>> neighborsMap = new HashMap<>();

        for (Planet planetA : planets) {
            for (Planet planetB : planets) {
                if (!planetA.equals(planetB) && planetA.distance(planetB) <= 30) { // Assuming the threshold radius is 10
                    neighborsMap.computeIfAbsent(planetA.name, k -> new HashSet<>()).add(planetB);
                }
            }
        }
        return neighborsMap;
    }

    private boolean isWithinRadius(Planet planet1, Planet planet2, int radius) {
        return Math.sqrt(Math.pow(planet1.x - planet2.x, 2) + Math.pow(planet1.y - planet2.y, 2)) <= radius;
    }

    public int findSizeOfLargestFleet(List<Fleet> fleets) {
        return fleets.stream()
                .mapToInt(Fleet::getFleetSize) // Convert each Fleet to int representing its size
                .max() // Find the maximum size
                .orElse(0); // Default to 0 if the list is empty
    }
}