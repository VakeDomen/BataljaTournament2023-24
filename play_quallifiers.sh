#!/bin/bash


game=0

for group in G1 G2 G3 G4
do 
    echo "------------ $group -------------"
    rm -r quallifiers_games/$group
    mkdir quallifiers_games/$group


    for team1 in $(ls quallifiers/$group)
    do
        
        
        for team2 in $(ls quallifiers/$group)
        do
            if [ $team1 = $team2 ]; then
                continue
            fi        

            

            while [ 1 ]
            do
                java -jar Game.jar quallifiers/$group/$team1/bot1 quallifiers/$group/$team1/bot2 quallifiers/$group/$team2/bot1 quallifiers/$group/$team2/bot2 > game.log
                # java -jar Game.jar quallifiers/$group/$team1/bot1 quallifiers/$group/$team1/bot2 quallifiers/$group/$team2/bot1 quallifiers/$group/$team2/bot2 > game.log
                
                read -p "write something to accept the game" key
                if [ "$key" = "1" ]; then
                    mv game.log quallifiers_games/$group/$team1-$team2.log
                    break
                fi
                

            done
        done
    done
done
