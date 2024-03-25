#!/bin/bash


rm -r eliminations
mkdir eliminations

game=0

while [ 1 ]
do
    players=$(python3 get_next_elimination_pair.py)
    
    if [ "$players" = "END" ]; then
        break
    fi
    
    # Read the two lines of players into team1 and team2
    read -r team1 <<< "$players"
    read -r team2 <<< "$(tail -n 1 <<< "$players")"

    echo "Team 1: $team1"
    echo "Team 2: $team2"

    # Folder name based on team names
    folder_name="g-$team1-$team2"

    # Check if folder exists, if not, create it
    if [ ! -d "eliminations/$folder_name" ]; then
        mkdir -p "eliminations/$folder_name"
    fi

    while true; do
        clear
        # python3 display_brackets.py
        python3 display_banner.py "Group: $group"
        python3 display_banner.py ""
        python3 display_banner.py "$team1"
        python3 display_banner.py "VS"
        python3 display_banner.py "$team2"

        java -jar Game.jar bots/$team1/bot1 bots/$team1/bot2 bots/$team2/bot1 bots/$team2/bot2 > game.log

        read -p "write something to accept the game (1 to accept): " key
        if [ "$key" = "1" ]; then
            winner=$(python3 get_game_winner.py ./game.log)
            mv game.log eliminations/$folder_name/g-$game.log
            python3 update_brackets.py $team1 $team2 $winner
            ((game++))
            break
        fi
    done
    break
done