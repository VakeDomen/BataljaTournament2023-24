public class Universe {

    private int width, height;
    private String myColor, tmColor, e1Color, e2Color;
    private int maxPlanetDistanceInTurns;

    public void initialize(String line) {

        String[] tokens = line.split(" ");

        this.width = Integer.parseInt(tokens[1]);
        this.height = Integer.parseInt(tokens[2]);

        this.maxPlanetDistanceInTurns = (int) (Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)) / 2);

        String color = tokens[3];
        switch (color) {
            case "green": { myColor = color; tmColor = "yellow"; e1Color = "blue"; e2Color = "cyan"; } break;
            case "blue": { myColor = color; tmColor = "cyan"; e1Color = "green"; e2Color = "yellow"; } break;
            case "yellow": { myColor = color; tmColor = "green"; e1Color = "blue"; e2Color = "cyan"; } break;
            case "cyan": { myColor = color; tmColor = "blue"; e1Color = "green"; e2Color = "yellow"; } break;
        }
    }

    public int getMaxPlanetDistanceInTurns() {
        return maxPlanetDistanceInTurns;
    }

    public String getMyColor() {
        return myColor;
    }

    public String getTeammateColor() {
        return tmColor;
    }

    public String getEnemy1Color() {
        return e1Color;
    }

    public String getEnemy2Color() {
        return e2Color;
    }

}
