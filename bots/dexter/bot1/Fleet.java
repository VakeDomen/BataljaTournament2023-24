public class Fleet {

    private boolean referenced;

    private final int fleetName;
    private final int fleetSize;
    private final int originPlanet;
    private final int destinationPlanet;
    private int currentTurn;
    private final int neededTurns;
    private final String fleetColor;

    public Fleet(int fleetName, int fleetSize, int origin, int destination, int turn, int neededTurns, String color) {
        this.fleetName = fleetName;
        this.fleetSize = fleetSize;
        this.originPlanet = origin;
        this.destinationPlanet = destination;
        this.currentTurn = turn;
        this.neededTurns = neededTurns;
        this.fleetColor = color;
    }

    public void setCurrentTurn(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    public boolean isReferenced() {
        return referenced;
    }

    public int getFleetName() {
        return fleetName;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public int getNeededTurns() {
        return neededTurns;
    }

    public int getFleetSize() {
        return fleetSize;
    }

    public String getFleetColor() {
        return fleetColor;
    }

    public void setReferenced(boolean referenced) {
        this.referenced = referenced;
    }
}
