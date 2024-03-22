#!/bin/bash


game=0

for group in G1 G2 G3 G4
do 
    echo "------------ $group -------------"
    

    for game in $(ls quallifiers_games/$group)
    do
        
        team1="${game%-*}"
        team2="${game#*-}"
        team2="${team2%.log}"

        clear
        python3 display_banner.py "Group:   $group"
        python3 display_banner.py " "
        python3 display_banner.py "$team1"
        python3 display_banner.py "VS"
        python3 display_banner.py "$team2"

        java -jar Game.jar -fullScreen=true -replay=./quallifiers_games/$group/$game
        
        read -p "write something to accept the game" key

        sleep 4

    done
done
