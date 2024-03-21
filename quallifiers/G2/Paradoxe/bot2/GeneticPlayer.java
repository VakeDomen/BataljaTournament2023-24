
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class GeneticPlayer implements Strategy {
    // Weights to adjust the importance of each factor in the appeal calculation
    double w1 = 2; // Weight for distance factor
    double w2 = 0; // Weight for size factor
    double w3 = 0; // Weight for second-degree neighbor size factor
    double w4 = 0; // How many fleets planet has
    double w5 = 0; // How many neighbours planet has
    double w6 = 20; // Threshold radius for second-degree neighbors
    //double w7 = 0; // How many incoming fleets planet has
    double maxDistance = Math.sqrt(Math.pow(Player.universeWidth,2) + Math.pow(Player.universeHeight,2)); // Maximum distance in the universe
    double clusteringThreshold = maxDistance * 0.3; //clustering
    double denseThreshold = maxDistance * 0.25; //dense graph
    int maxTurnsAhead;  // How many turns ahead to simulate

    @Override
    public void earlyGame(State state) {
        //adjustStrategyBasedOnMap(state);
        // Deep copy the current state to simulate future state
        State futureState = new State(deepCopyPlanets(state.planets),deepCopyFleets(state.fleets));
        futureState = peekIntoFuture(futureState);
        attack(state, futureState, futureState.grayPlanets);

    }
    @Override
    public void endGame(State state) {
        //adjustStrategyBasedOnMap(state);
        // Deep copy the current state to simulate future state
        State futureState = new State(deepCopyPlanets(state.planets),deepCopyFleets(state.fleets));
        futureState = peekIntoFuture(futureState);
        attack(state, futureState, futureState.enemyPlanets);

    }
    public void attack(State state, State futureState, LinkedList<Planet> targets) {
        state.myPlanets.stream().filter(planet -> planet.fleet > 5).forEach(origin -> {
            // Find the most appealing planet
            Planet mostAppealing = targets.stream()
                    .min(Comparator.comparingDouble(target ->
                            target.calculateAppeal(origin, state.neighborsMap, state, w1, w2, w3, w4, w5, w6)))
                    .orElse(null);

            //at current or in we need to go to the future and see if from that future???
            /// from CURRENT =>GO FUTURUE BEFORE THE EVENT (FLEET ARRIVAL) HAPPENS
            int enemyArrivalFleet = state.fleets.stream()
                    .filter(fleet -> fleet.destinationPlanet == origin.name) // Filter fleets targeting the origin planet
                    .min(Comparator.comparingInt(Fleet::getTurns)) // Find the fleet that will arrive the earliest
                    .map(Fleet::getTurns) // Extract the number of turns until arrival for that fleet
                    .orElse(0); // Default to 0 if no such fleet exists



            if (mostAppealing != null && !mostAppealing.color.equals(Player.myColor) &&
                    !mostAppealing.color.equals(Player.teammateColor)) {
                // Collect all planets within the radius, including your own and your teammate's
                List<Planet> closePlanets = state.planets.stream()
                        .filter(planet -> !planet.equals(origin)) // Exclude the origin planet itself
                        .filter(planet -> origin.distance(planet) / 2 <= origin.distance(mostAppealing) / 2 && origin.distance(planet)/2 < enemyArrivalFleet)
                        .collect(Collectors.toList());

                    int distance = origin.distance(mostAppealing) / 2;
                    Planet predictedTarget = peekIntoFutureWithTurns(state, mostAppealing, distance);
                    Planet predictedOrigin = peekIntoFutureWithTurns(state, origin, distance + 2);




                    // Directly attack the most appealing planet if it's close enough and the conditions are met
                    if (predictedOrigin.color.equals(Player.myColor) &&
                            !predictedTarget.color.equals(Player.myColor) &&
                            !predictedTarget.color.equals(Player.teammateColor)) {

                        if (predictedOrigin.fleet >= predictedTarget.fleet ) {
                            System.out.println("A " + origin.name + " " + mostAppealing.name + " " + predictedTarget.fleet);
                            origin.fleet -= predictedTarget.fleet;
                            targets.remove(mostAppealing);
                        } else {
                            // Sum up fleets from close planets, including your own and your teammate's
                            int totalFleetSurplus = closePlanets.stream()
                                    .map(planet -> peekIntoFutureWithTurns(state, planet, origin.distance(planet) / 2))
                                    .filter(futurePlanet -> futurePlanet.color.equals(Player.myColor) ||
                                            futurePlanet.color.equals(Player.teammateColor))
                                    .mapToInt(futurePlanet -> futurePlanet.fleet)
                                    .sum();

                            if (totalFleetSurplus >= predictedTarget.fleet) {
                                System.out.println("A " + origin.name + " " + mostAppealing.name + " " + predictedTarget.fleet);
                                origin.fleet -= predictedTarget.fleet;
                                targets.remove(mostAppealing);
                            }
                        }
                    }

                if (!predictedOrigin.color.equals(Player.myColor) && !predictedOrigin.color.equals(Player.teammateColor)) {
                    // Transfer the fleets to the closest friendly planet
                    List<Planet> closeMyPlanets = state.myPlanets.stream()
                            .filter(planet -> !planet.equals(origin)) // Exclude the origin planet itself
                            .filter(planet -> origin.distance(planet) / 2 <= origin.distance(mostAppealing) / 2 && origin.distance(planet)/2 < enemyArrivalFleet)
                            .collect(Collectors.toList());

                    // Calculate the future state for each close friendly planet
                    List<Planet> futureCloseMyPlanets = closeMyPlanets.stream()
                            .map(planet -> peekIntoFutureWithTurns(state, planet, origin.distance(planet) / 2))
                            .collect(Collectors.toList());

                    // Find the closest planet in the future state
                    Planet closestPlanet = futureCloseMyPlanets.stream()
                            .filter(planet -> planet.color.equals(Player.myColor))
                            .min(Comparator.comparing(planet -> origin.distance(planet)))
                            .orElse(null);

                    if (closestPlanet != null) {
                        System.out.println("A " + origin.name + " " + closestPlanet.name + " " + origin.fleet);
                        origin.fleet = 0; // Assuming you transfer all fleets
                    }
                }

            }
        });
    }



    public void calculateFutureState(State state) {
        for (Planet planet : state.planets) {
            List<Fleet> fleetsToPlanet = state.fleets.stream().filter(fleet -> fleet.destinationPlanet == planet.name)
                    .collect(Collectors.toList());
            for (Fleet fleet : fleetsToPlanet) {
                if ("null".equals(planet.color) || !planet.color.equals(fleet.ownerColor) || !planet.color.equals(Player.teammateColor)) {
                    planet.fleet -= fleet.fleetSize;
                } else {
                    planet.fleet += fleet.fleetSize;
                }
                if (planet.fleet < 0) {
                    planet.color = fleet.ownerColor;
                    planet.fleet = Math.abs(planet.fleet);
                }
            }
        }
        state.categorizePlanets();
    }

    /**encapsulate the decision for the shape of the map*/
    public void adjustStrategyBasedOnMap(State state) {
        double averageDistance = calculateAverageDistance(state.planets);
        double stdDeviation = calculateStandardDeviation(state.planets);
        // Adjust strategy based on the density of the graph
        if (averageDistance < clusteringThreshold || stdDeviation <= denseThreshold) {
            // The graph is more clustered: shortest-path and neighbors are most important
            w1 = 1; // Weight for distance
            w2 = 0; // Deprioritize size factor
            w3 = 1; // Weight for second-degree neighbor size factor
            w4 = 1;
            w5 = 1;
        } else {
            // The graph is less clustered: prioritize size factor
            w1 = 0; // Deprioritize distance
            w2 = 1; // Weight for size
            w3 = 0; // Deprioritize second-degree neighbors
            w4 = 1;
            w5 = 1;
        }
    }

    /**
     * smaller average distance might = denser or more clustered graph
     * larger average distance could suggest a sparser graph
     ****/
    public double calculateAverageDistance(List<Planet> planets) {
        double totalDistance = IntStream.range(0, planets.size()).parallel().mapToDouble(i -> {
            Planet planet1 = planets.get(i);
            return IntStream.range(0, planets.size())
                    .filter(j -> i != j) // Ensure we don't compare the planet with itself
                    .mapToDouble(j -> {
                        Planet planet2 = planets.get(j);
                        return planet1.distance(planet2); // Use the provided distance method
                    }).sum(); // Sum distances from planet1 to all other planets
        }).sum(); // Sum all distances calculated by the stream

        int totalComparisons = planets.size() * (planets.size() - 1); // Total number of unique comparisons
        return totalComparisons > 0 ? totalDistance / totalComparisons : 0; // Calculate average if there are comparisons
    }

    /**
     * High Standard Deviation indicates that some planets are much further apart than others,
     * suggesting clusters of planets with sparse areas in between.
     */
    public double calculateStandardDeviation(List<Planet> planets) {
        if (planets.size() < 2) {
            // Standard deviation is not defined for fewer than 2 points
            return 0;
        }
        int count = planets.size() * (planets.size() - 1) / 2;//unique pairs of planets

        //Calculate the mean distance
        double totalDistance = IntStream.range(0, planets.size()).parallel().mapToDouble(i ->
                IntStream.range(i + 1, planets.size())
                        .mapToDouble(j -> planets.get(i).distance(planets.get(j)))
                        .sum()).sum();

        double meanDistance = totalDistance / count;

        //Calculate the sum of squared differences from the mean
        double sumOfSquaredDifferences = IntStream.range(0, planets.size()).parallel().mapToDouble(i ->
                IntStream.range(i + 1, planets.size())
                        .mapToDouble(j -> {
                            double distance = planets.get(i).distance(planets.get(j));
                            return Math.pow(distance - meanDistance, 2);
                        }).sum()).sum();
        //Divide by the number of distances and take the square root
        return Math.sqrt(sumOfSquaredDifferences / count);
    }

    public State peekIntoFuture(State state) {
        List<Fleet> incomingFleet = state.fleets.stream().sorted(Comparator.comparingInt(Fleet::getTurns)).collect(Collectors.toList());
        int turn =0;
        for(Fleet fleet: incomingFleet){
            while(turn< fleet.getTurns()){
                for (Planet planet : state.planets) {
                    if (!"null".equals(planet.color)) {
                        planet.fleet += planet.size * 10; // Adjust fleet generation rate as needed
                    }
                }
                turn++;
            }
            Planet target = state.planets.stream().filter(planet -> planet.name == fleet.destinationPlanet).collect(Collectors.toList()).get(0);
            if(areTogether(fleet,target)){
                target.fleet+= fleet.fleetSize;
            }else{
                target.fleet-= fleet.fleetSize;
                if(target.fleet<0){
                    target.color = fleet.ownerColor;
                    target.fleet *=-1;
                }
            }
            turn++;
        }
        return state;
    }

    public boolean areTogether(Fleet fleet,  Planet planet){
        if(fleet.ownerColor.equals("blue") || fleet.ownerColor.equals("cyan")){
            return planet.color.equals("blue") || planet.color.equals("cyan");
        }
        if(fleet.ownerColor.equals("green") || fleet.ownerColor.equals("yellow")){
            return planet.color.equals("green") || planet.color.equals("yellow");
        }
        return planet.color.equals("null");
    }

    public Planet peekIntoFutureWithTurns(State state, Planet keyPlanet , int turns) {
        List<Fleet> incomingFleet = state.fleets
                .stream()
                .filter(fleet -> fleet.destinationPlanet == keyPlanet.name)
                .sorted(Comparator.comparingInt(Fleet::getTurns))
                .collect(Collectors.toList());

        int turn = 0;
        for (Fleet f : incomingFleet) {
            if (turn == turns) {
                break;
            }/* here I sort them by: fleet.turnsNeeded-fleet.currentTurn*/
            if (turn != f.getTurns()) {
                //go for each planet   NO EVENT HAPPENED
                for (Planet planet1 : state.planets) {
                    if (!"null".equals(planet1.color)) {
                        planet1.fleet += planet1.size * 10; // Adjust fleet generation rate as needed
                    }
                }
            } else{

                if(areTogether(f,keyPlanet)){
                    keyPlanet.fleet += f.fleetSize;
                }else{
                    keyPlanet.fleet -= f.fleetSize;
                }
                if (keyPlanet.fleet < 0) {
                    keyPlanet.color = f.ownerColor;
                    keyPlanet.fleet = Math.abs(keyPlanet.fleet);
                }
                /*
                if (keyPlanet.color.equals("null") || !keyPlanet.color.equals(f.ownerColor) || !keyPlanet.color.equals(Player.teammateColor)) {
                    // Update the planet's color and fleet size if conquered
                    keyPlanet.fleet -= f.fleetSize;
                } else { // Friendly planet
                    keyPlanet.fleet += f.fleetSize;
                }
                if (keyPlanet.fleet < 0) {
                    keyPlanet.color = f.ownerColor;
                    keyPlanet.fleet = Math.abs(keyPlanet.fleet);
                }*/
            }
            turn++;
        }
        //if negative means the planet cannot defend itself
        return keyPlanet;
    }

    public Map<Planet, List<Fleet>> predictEnemyMovements(List<Fleet> fleets, List<Planet> planets) {
        Map<Planet, List<Fleet>> enemyMovements = new HashMap<>();
        // Filter out fleets that do not come from our planet or our partner's planet
        List<Fleet> filteredFleets = fleets.stream()
                .filter(fleet -> !fleet.ownerColor.equals(Player.myColor) && !fleet.ownerColor.equals(Player.teammateColor))
                .collect(Collectors.toList());

        // Now process only filtered fleets
        for (Fleet fleet : filteredFleets) {
            Planet destination = planets.stream()
                    .filter(planet -> planet.name == fleet.destinationPlanet).findFirst().orElse(null);
            if (destination != null) {
                enemyMovements.computeIfAbsent(destination, k -> new ArrayList<>()).add(fleet);
            }
        }
        return enemyMovements;
    }


    public LinkedList<Planet> deepCopyPlanets (LinkedList<Planet> planets){
        LinkedList<Planet> clonedPlanets = new LinkedList<Planet>();
        planets.forEach(planet -> {
            clonedPlanets.add(new Planet(planet));
        });
        return clonedPlanets;
    }

    public LinkedList<Fleet> deepCopyFleets (LinkedList<Fleet> fleets){
        LinkedList<Fleet> clonedFleets = new LinkedList<Fleet>();
        fleets.forEach(f -> {
            clonedFleets.add(new Fleet(f));
        });
        return clonedFleets;
    }
}