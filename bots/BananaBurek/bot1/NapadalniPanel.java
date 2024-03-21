import java.util.ArrayList;

public class NapadalniPanel {

    public int x; //pozicija
    public int y;
    public String color; //barva mojega igralca
    ArrayList<Planet> mojiPlaneti;
    ArrayList<Planet> nevtralni;
    ArrayList<Planet> nasprotnik;
    ArrayList<Planet> myTeam;


    ArrayList<Ladice> mojeLadice;
    ArrayList<Ladice> nasprotneLdaice;
    public NapadalniPanel(int x, int y, String color) {
        this.x = x;
        this.y = y;
        this.color = color;

        mojiPlaneti = new ArrayList<>();
        nevtralni = new ArrayList<>();
        nasprotnik = new ArrayList<>();
        mojeLadice = new ArrayList<>();
        nasprotneLdaice = new ArrayList<>();
        myTeam = new ArrayList<>();
    }

    //glavna funkcija ki izvede napad
    public String napad(){
        setUpGameState();
        String attack = "";
        if(!nasprotnik.isEmpty()){
            if(mojiPlaneti.size() > 3){
                //defence();
            }
            if(nevtralni.size() > 2){
                attack += getAttack(attack, nevtralni);
            }else {
                attack += getAttack(attack, nasprotnik);
            }
        }
    return attack;
    }


    public String defence(){
        StringBuilder def = new StringBuilder();
        for (int i = 0; i < mojiPlaneti.size(); i++) {
            if (mojiPlaneti.get(i).isDeffender()) {
                def.append(getDefenders(mojiPlaneti.get(i)));
            }
        }


        return def.toString();
    }

    public String getDefenders(Planet p){
        StringBuilder def = new StringBuilder();
        ArrayList<Planet> temp = mojiPlaneti;
        int deffender = 0;
        if(mojiPlaneti.size() >= 5 && mojiPlaneti.size() < 7){
            deffender = 2;
        } else if (mojiPlaneti.size() > 7) {
            deffender = 3;
        }else {
            deffender = 1;
        }
        int y = 0;
        while(y < deffender || deffender >= temp.size()){
            Planet x = closest(temp,p);
            temp.remove(x);
            if(x.isAttacker() && !x.isAttacking()){
                def.append(x.napadiPlanet(p,0));
                y++;
                for (int i = 0; i < mojiPlaneti.size(); i++) {
                    if(mojiPlaneti.get(i) == x){
                        mojiPlaneti.get(i).setAttacking(true);
                    }
                }
            }
        }
        return def.toString();
    }
    private String getAttack(String attack, ArrayList<Planet> p) {
        StringBuilder attackBuilder = new StringBuilder(attack);
        for (Planet planet : mojiPlaneti) {
            if(planet.isAttacker() && !planet.isAttacking()){
                Planet x = closest(p, planet);
                attackBuilder.append(planet.napadiPlanet(x,0));
                planet.setAttacking(true);
            }
        }
        attack = attackBuilder.toString();
        return attack;
    }

    public Planet closest(ArrayList<Planet> planets, Planet p){
        Planet newp = planets.get(0);
        double min = Double.MAX_VALUE;
        double dis = 0;
        for (int i = 0; i < planets.size(); i++) {
                dis = distance(planets.get(i),p);
                if(dis < min ){
                    min = dis;
                    newp = planets.get(i);
            }
        }
        return newp;
    }


    public double distance(Planet p1, Planet p2) {
        double x1 = p1.getX();
        double y1 = p1.getY();
        double x2 = p2.getX();
        double y2 = p2.getY();

        return Math.hypot(x2 - x1, y2 - y1);
    }



    public void setUpGameState(){
        for (Planet p : mojiPlaneti) {
            int att = numOfAttackingFleets(p);
            int def = p.getStladij() + numOfDefendingFleets(p);
            if (def <= att + 10) {
                p.setDeffender(true);
            } else {
                p.setAttacker(true);
            }
        }
    }

    public int numOfAttackingFleets(Planet p){
        int sum = 0;
        for (int i = 0; i < nasprotneLdaice.size(); i++) {
            if(p.getIme() == nasprotneLdaice.get(i).getNapadanplanet()){
                sum += nasprotneLdaice.get(i).getVelikost();
            }
        }
        return sum;
    }
    public int numOfDefendingFleets(Planet p){
        int sum = 0;
        for (int i = 0; i < mojeLadice.size(); i++) {
            if(p.getIme() == mojeLadice.get(i).getNapadanplanet()){
                sum += mojeLadice.get(i).getVelikost();
            }
        }
        return sum;
    }

    public void dodajPlanet(String[] t) {
        Planet p = new Planet(t); //ustvarimo planet
        if (p.getColor().equals(this.color)) {
            //moji planeti
            mojiPlaneti.add(p);
        }else if (p.getColor().equals("null")) {
            //nevtralni planeti
            nevtralni.add(p);
        }else if ((this.color.equals("green") && p.getColor().equals("yellow")) ||
                (this.color.equals("yellow") && p.getColor().equals("green"))) {
            myTeam.add(p);
        } else if ((this.color.equals("cyan") && p.getColor().equals("blue")) ||
                this.color.equals("blue") && p.getColor().equals("cyan")) {
            myTeam.add(p);
        } else {
            //nasprotnikovi planeti
            nasprotnik.add(p);
        }
    }

    public void dodajFleat(String[] t) {
        Ladice f = new Ladice(t);
        if (f.getColor().equals(this.color)) {
            mojeLadice.add(f);
        }else if ((this.color.equals("green") && f.getColor().equals("yellow")) ||
                (this.color.equals("yellow") && f.getColor().equals("green"))) {
            mojeLadice.add(f);
        } else if ((this.color.equals("cyan") && f.getColor().equals("blue")) ||
                this.color.equals("blue") && f.getColor().equals("cyan")) {
            mojeLadice.add(f);
        } else {
            //nasprotnikovi planeti
            nasprotneLdaice.add(f);
        }
    }
}