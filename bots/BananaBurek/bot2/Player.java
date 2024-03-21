import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

public class Player {
    public static void main(String[] args) throws Exception {
        FileWriter fstream = new FileWriter("Igralec.log"); // uporabimo za debuganje
        BufferedWriter out = new BufferedWriter(fstream);
        try {
			/*
				**************
				Initialization
				**************
			*/

            Scanner bralec = new Scanner(System.in);
            String line = bralec.nextLine();// line of input from the game
            //U	<int>,<int>,<string>						Universe - size (x in y) playing field (allways 100,100), and your color
            String[] tokens = line.split(" ");
            int x = Integer.parseInt(tokens[1]);
            int y = Integer.parseInt(tokens[2]);
            String color = tokens[3];
            //pisemo na log
            out.write(line + "\n");
            out.flush();//vedno flushat buffer!!

			/*
				**************
				Main game loop
				**************
			*/
            while (true) {
				/*
					******************************
					clear state from previous turn
					******************************
				*/

                //ustvarimo novi objekt
                //oziroma brisi starega in naredi novi objekt
                NapadalniPanel np = new NapadalniPanel(x,y,color);

				/*
					********************************
					read the input from the game and
					parse it (state of the game)
					********************************
				*/


					/*

						P	<int>,<int>,<int>,<float>,<int>,<string>	Planet:	- name (number), - position x, - position y, - planet size, - fleet size, - planet color (red, blue or null)
						S	-											Start - when a player receives this command from the game he starts responding
					*/
                line = bralec.nextLine();
                while( ! line.equals("S") ){   //ponavljaj branje dokler ne preberes "S"
                    //beremo input dokler ne dobimo S
                    //out.write(line + "\n");
                    out.flush();
                    //prsajmo vrstico
                    tokens = line.split(" ");

                    if(tokens[0].equals("P")){
                        //dobili smo planet in si ga zapomnimo
                        np.dodajPlanet(tokens);
                    }



                    if(tokens[0].equals("F")){
                        np.dodajFleat(tokens);
                    }
                    line = bralec.nextLine(); //preberemo naslednjo vrstico
                }


				/*
					*********************************
					LOGIC: figure out what to do with
					your turn
					*********************************
				*/

                String attack = np.napad();  //izvedi napad
                out.write(attack + "\n");
                out.flush();//vedno flushat buffer!!
				/*
					*********************************
					Print the attack command and thus
					end the turn
					*********************************
				*/
                attack += "E"; // end of turn command
                System.out.println(attack); // send commands to the game
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.write("ERROR: ");
            out.write(e.getMessage());
            out.flush();
        }
        out.close();
    }
}
