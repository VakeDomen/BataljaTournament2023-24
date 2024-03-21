public class Planet {

    //P	<int>,<int>,<int>,<float>,<int>,<string>	Planet:	- name (number), - position x, - position y, - planet size, - fleet size, - planet color (red, blue or null)

    //lastnosti
    public int ime;
    public int x;//pozicija
    public int y;
    public float velikost;
    public int stladij;
    public String color;
    public boolean attacker;
    public boolean deffender;
    public boolean attacking;

    //konstruktor
    public Planet(String[] t){
        this.ime = Integer.parseInt(t[1]);
        this.x = Integer.parseInt(t[2]);
        this.y = Integer.parseInt(t[3]);
        this.velikost = Float.parseFloat(t[4]);
        this.stladij = Integer.parseInt(t[5]);
        this.color = t[6];
        this.attacker = false;
        this.deffender = false;
        this.attacking = false;
    }

    public boolean isAttacker() {
        return attacker;
    }

    public boolean isAttacking() {
        return attacking;
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }

    public void setAttacker(boolean attacker) {
        this.attacker = attacker;
    }

    public boolean isDeffender() {
        return deffender;
    }

    public void setDeffender(boolean deffender) {
        this.deffender = deffender;
    }

    public String getColor() {
        return this.color;
    }

    public int getIme() {
        return this.ime;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public float getVelikost() {
        return velikost;
    }

    public int getStladij() {
        return stladij;
    }
    public String napadiPlanet(Planet b,int n) {
        String poteza = "A" + " ";
        poteza += this.ime + " "; //planet A ki napada
        poteza += b.getIme(); //napadan planet
        if(n != 0){
            poteza += n + " ";
        }
        poteza += "\n";
        return poteza;
    }

}
