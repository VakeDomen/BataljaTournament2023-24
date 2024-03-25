#!/bin/bash

game=0

for group in G2 G3 G4; do
    echo "------------ $group -------------"
    rm -rf quallifiers_games/$group
    mkdir -p quallifiers_games/$group

    team_list=($(ls quallifiers/$group))  # Store team names in an array

    for (( i=0; i<${#team_list[@]}; i++ )); do
        team1=${team_list[$i]}
        
        for (( j=i+1; j<${#team_list[@]}; j++ )); do  # Start from the next team
            team2=${team_list[$j]}

            while true; do
                clear
                # python3 display_brackets.py
                python3 display_banner.py "Group: $group"
                python3 display_banner.py ""
                python3 display_banner.py "$team1"
                python3 display_banner.py "VS"
                python3 display_banner.py "$team2"

                java -jar Game.jar quallifiers/$group/$team1/bot1 quallifiers/$group/$team1/bot2 quallifiers/$group/$team2/bot1 quallifiers/$group/$team2/bot2 > game.log
                
                read -p "write something to accept the game (1 to accept): " key
                if [ "$key" = "1" ]; then
                    mv game.log quallifiers_games/$group/${team1}-${team2}.log
                    break
                fi
            done
        done
    done
done
