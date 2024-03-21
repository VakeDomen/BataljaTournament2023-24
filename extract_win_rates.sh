#!/bin/bash

dir="quallifiers_games"

# Process each log file to determine the winner
process_log_file() {
    local file="$1"
    # Read the last lines to find survivors and extract their team names
    while IFS= read -r line; do
        if [[ "$line" == *"survive: true"* ]]; then
            # Extract team name from the path
            team_name=$(echo "$line" | grep -oP 'STAT: .*/\K[^/]*(?=/)')
            sanitized_name=$(echo "$team_name" | sed 's/[^a-zA-Z0-9]/_/g')
            # Increment win count
            ((team_wins["$sanitized_name"]++))
        fi
    done < <(tail -n 100 "$file")
}

# Iterate over each group directory
for group in "$dir"/G*; do
    echo "Processing Group: $(basename "$group")"
    declare -A team_wins=()

    # Find all log files in the group and process them
    while IFS= read -r file; do
        process_log_file "$file"
    done < <(find "$group" -type f -name '*.log')

    # Display win rates for the group
    echo "Team Win Rates for Group $(basename "$group"):"
    total_games=$(find "$group" -type f -name '*.log' | wc -l)
    for team in "${!team_wins[@]}"; do
        win_count=${team_wins[$team]}
        echo "$team: $win_count wins"
    done
    echo "-----------------------------------"
done
