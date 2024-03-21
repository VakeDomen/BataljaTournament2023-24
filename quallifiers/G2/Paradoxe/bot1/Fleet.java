public class Fleet {
    int fleetName; // Assuming fleet name is an integer ID
    int fleetSize;
    int originPlanet;
    int destinationPlanet;
    int currentTurn;
    int turnsNeeded;

    String ownerColor; // May be null for neutral

    public Fleet(int fleetName, int fleetSize, int originPlanet, int destinationPlanet, int currentTurn, int turnsNeeded, String ownerColor) {
        this.fleetName = fleetName;
        this.fleetSize = fleetSize;
        this.originPlanet = originPlanet;
        this.destinationPlanet = destinationPlanet;
        this.currentTurn = currentTurn;
        this.turnsNeeded = turnsNeeded;
        this.ownerColor = ownerColor;
    }

    public Fleet(String[] line) {
        this.fleetName = Integer.parseInt(line[1]);
        this.fleetSize = Integer.parseInt(line[2]);
        this.originPlanet = Integer.parseInt(line[3]);
        this.destinationPlanet = Integer.parseInt(line[4]);
        this.currentTurn = Integer.parseInt(line[5]);
        this.turnsNeeded = Integer.parseInt(line[6]);
        this.ownerColor = line[7];
    }

    public Fleet(Fleet fleet) {
        this.fleetName = fleet.fleetName;
        this.fleetSize = fleet.fleetSize;
        this.originPlanet = fleet.originPlanet;
        this.destinationPlanet = fleet.destinationPlanet;
        this.currentTurn = fleet.currentTurn;
        this.turnsNeeded = fleet.turnsNeeded;
        this.ownerColor = fleet.ownerColor;

    }

    public int getTurns() {
        return this.turnsNeeded - this.currentTurn;
    }
    public int getFleetSize() {
        return this.fleetSize;
    }
}