public class Ladice {
    //F	<int>,<int>,<int>,<int>,<int>,<int>
    //- flee name (number),
    //- fleet size
    //- origin planet
    //- destination planet
    //- current turn
    //- number of needed turns

    public int ime;
    public int velikost;
    public int origin;
    public int napadanplanet;
    public int turn;
    public int stturnov;
    public String color;
    public Ladice(String[] t){

        this.ime = Integer.parseInt(t[1]);

        this.velikost = Integer.parseInt(t[2]);

        this.origin = Integer.parseInt(t[3]);

        this.napadanplanet = Integer.parseInt(t[4]);

        this.turn = Integer.parseInt(t[5]);

        this.stturnov = Integer.parseInt(t[6]);

        this.color = t[7];

    }

    public String getColor() {
        return color;
    }

    public int getIme() {
        return ime;
    }

    public int getVelikost() {
        return velikost;
    }

    public int getOrigin() {
        return origin;
    }

    public int getNapadanplanet() {
        return napadanplanet;
    }

    public int getStturnov() {
        return stturnov;
    }

    public int getTurn() {
        return turn;
    }
}
