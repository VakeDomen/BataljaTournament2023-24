import java.util.*;

public class Planet {
    // Attributes of a planet
    public int name; // Unique identifier of the planet
    public int x; // X-coordinate in the game space
    public int y; // Y-coordinate in the game space
    public float size; // Size of the planet, affects production
    public int fleet; // Number of ships on the planet
    public String color; // Owner of the planet, null if neutral

    public Planet(int name, int x, int y, float size, int fleet, String color) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.size = size;
        this.fleet = fleet;
        this.color = color;
    }

    public Planet(String[] line) {
        this.name = Integer.parseInt(line[1]);
        this.x = Integer.parseInt(line[2]);
        this.y = Integer.parseInt(line[3]);
        this.size = Float.parseFloat(line[4]);
        this.fleet = Integer.parseInt(line[5]);
        this.color = line[6];
    }

    public Planet(Planet old) {
        this.name = old.name;
        this.x = old.x;
        this.y = old.y;
        this.size = old.size;
        this.fleet = old.fleet;
        this.color = old.color;
    }

    public int distance(Planet p) {
        return (int) Math.ceil(Math.sqrt(Math.pow(this.x - p.x, 2) + Math.pow(this.y - p.y, 2)));
    }

    public float scalePlanetSize() {
        float maxSize = (float) Math.sqrt(Player.universeHeight + Player.universeWidth);
        float planetSize = this.size;
        if (planetSize >= maxSize) {
            planetSize = maxSize;
        }
        return planetSize; // Scale size to be in the range of 0 to maxDistance
    }
    public int getPlanetFleet() {
        return this.fleet;
    }

    // Method to find the maximum size factor across all planets
    public double findMaxSizeFactorAcrossPlanets(List<Planet> allPlanets) {
        return allPlanets.stream()
                .mapToDouble(Planet::scalePlanetSize) // Convert each planet to its size factor
                .max() // Find the maximum value
                .orElse(0); // Return 0 if there are no planets
    }
/*
    public int findMaxIncomingFleets(List<Planet> allPlanets) {
        return allPlanets.stream()
                .mapToInt(this::calculateIncomingFleets)
                .max()
                .orElse(0); // Default to 0 if no fleets are incoming
    }


    public int calculateIncomingFleets(Planet origin ,List<Fleet> allFleets) {
        return allFleets.stream()
                .filter(fleet -> origin.name == fleet.destinationPlanet) // Only consider fleets whose destination is this planet
                .mapToInt(fleet -> fleet.fleetSize) // Map to the fleet size
                .sum();
    }*/



    // Method to find maximum values for neighbor size sum, fleet sum, and neighbor count
    public double[] findMaxNeighborhoodStats(Map<Integer, Set<Planet>> neighborsMap, List<Planet> allPlanets, double w6) {
        double maxNeighborSizeSum = 0;
        int maxFleetSum = 0;
        int maxNeighborCount = 0;


        for (Planet planet : allPlanets) {
            double[] stats = planet.calculateNeighborSizeSum(neighborsMap, w6);
            if (stats[0] > maxNeighborSizeSum) {
                maxNeighborSizeSum = stats[0];
            }
            if (stats[1] > maxFleetSum) {
                maxFleetSum = (int) stats[1];
            }
            if (stats[2] > maxNeighborCount) {
                maxNeighborCount = (int) stats[2];
            }
        }

        return new double[]{maxNeighborSizeSum, maxFleetSum, maxNeighborCount};
    }

    public double findMaxDistanceBetweenPlanets(List<Planet> allPlanets) {
        return allPlanets.stream()
                .flatMap(planet1 -> allPlanets.stream()
                        .map(planet2 -> new Planet[]{planet1, planet2}))
                // Filter out pairs of the same planet
                .filter(pair -> !pair[0].equals(pair[1]))
                // Calculate distance for each pair and find the maximum
                .mapToDouble(pair -> pair[0].distance(pair[1]))
                .max()
                .orElse(0); // Return 0 if no planets or only one planet is present
    }



    // Method to calculate the total size of this planet's first-degree and second-degree neighbors within a radius of 5
    public double[] calculateNeighborSizeSum(Map<Integer, Set<Planet>> neighborsMap, double w6) {
        Set<Planet> firstDegreeNeighbors = neighborsMap.getOrDefault(this.name, Collections.emptySet());
        double secondDegreeNeighborSizeSum = 0;
        int secondDegreeNeighborsFleetSum = 0;
        int countNeighbours = 0;

        for (Planet neighbor : firstDegreeNeighbors) { //for each first degree neighbor add sizes of its second degree neighbors
            secondDegreeNeighborSizeSum += (double) neighbor.size; // Add the first degree neighbor size
            Set<Planet> secondDegreeNeighbors = neighborsMap.getOrDefault(neighbor.name, Collections.emptySet());
            for (Planet secondNeighbor : secondDegreeNeighbors) {
                if ((double)this.distance(secondNeighbor) <= w6) { // Assuming the threshold radius is 5
                    secondDegreeNeighborSizeSum += secondNeighbor.size; // Add the size if within radius of 5
                    secondDegreeNeighborsFleetSum += secondNeighbor.fleet;
                    countNeighbours++;
                }
            }
        }
        return new double[]{secondDegreeNeighborSizeSum, secondDegreeNeighborsFleetSum, countNeighbours};
    }

    public double calculateAppeal(Planet origin, Map<Integer, Set<Planet>> neighborsMap, State state, double w1, double w2, double w3, double w4, double w5, double w6) {
        double distanceFactor = ((double) this.distance(origin) / findMaxDistanceBetweenPlanets(state.planets));

        double sizeFactor = this.scalePlanetSize() / findMaxSizeFactorAcrossPlanets(state.planets); //divide with the max distance between 2 planets
        double maxNeighboursSizeSum = findMaxNeighborhoodStats(neighborsMap, state.planets, w6)[0];
        double maxNeighboursFleetSum = findMaxNeighborhoodStats(neighborsMap, state.planets, w6)[1];
        double maxNeighboursCount = findMaxNeighborhoodStats(neighborsMap, state.planets, w6)[2];

        double[] neighborStats = this.calculateNeighborSizeSum(neighborsMap, w6);
        double neighborSizeFactor = neighborStats[0] / maxNeighboursSizeSum;
        double neighbourFleetSum = (int) neighborStats[1] / maxNeighboursFleetSum;
        double neighbourCount = (int) neighborStats[2] / maxNeighboursCount;


        // Calculate incoming fleets for this planet
       // double incomingFleetsFactor = calculateIncomingFleets( this, state.fleets)/findMaxIncomingFleets(state.planets);

        double appeal = (distanceFactor * w1 + sizeFactor * w2 + neighborSizeFactor * w3 + neighbourFleetSum * w4 + neighbourCount * w5 /*+ incomingFleetsFactor *w7*/);
        //Player.logToFile("Appeal calculated from planet "+origin.name+" for planet " + this.name + ": " + appeal);

        return appeal;
    }

    public int getName() {
        return name;
    }

}