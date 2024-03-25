#!/bin/bash

# testing flags
qualifiers=0
turny=1

if [ $qualifiers -eq 1 ]; then
    clear
    python3 display_banner.py "Qualifier groups:"
    python3 display_groups.py
    read -p "press [enter] to contune to qualifiers"

    clear
    ./quallifiers.sh
fi

if [ $turny -eq 1 ]; then
    clear
    python3 extract_win_rates.py
    read -p "press [enter] to contune to eliminations"
    rm brackets.txt
    cp brackets_base.txt brackets.txt
    clear
    echo
    ./eliminations.sh
    python3 display_brackets.py
fi