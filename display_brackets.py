from colorama import Fore, Style

def generate_tournament_bracket(filename):
    with open(filename, 'r') as file:
        participants = [line.strip() for line in file if line.strip()]
        pa = []
        wins = []
        for parti in participants:
            pa.append(parti.split(" ")[0])
            wins.append(parti.split(" ")[1])
        participants = pa

    num_of_pl = len(participants)

    # Determine the padding length based on the longest participant name
    max_length = max(len(participant) for participant in participants)
    max_length = max(max_length, 10)  # Ensure minimum placeholder length

    participants = [participant.ljust(max_length, ' ') for participant in participants]

    # Prepare placeholders for missing participants up to 8
    while len(participants) < 18:
        participants.append('?' * max_length)
        wins.append("?")

    playing = {
        8: [0, 1],
        9: [2, 3],
        10: [4, 5],
        11: [6, 7],
        12: [8, 9],
        14: [10, 11],
        16: [13, 15],
        17: [12, 14],
        18: [17]
    }
    first_colored=False
    for i, participant in enumerate(participants):
        color = Fore.RESET
        style = Style.DIM
        if i in playing[num_of_pl]:
            if not first_colored:
                color = Fore.YELLOW
                first_colored=True
            else:
                color = Fore.CYAN
            style = Style.BRIGHT

        participants[i] = color + style + participant + Style.RESET_ALL
        wins[i] = color + style + wins[i] + Style.RESET_ALL


    
    # Generate bracket
    print(f"1. {participants[0]} [ {wins[0]} ]--------+")
    print(f"                              Bo3 |--- {participants[8]} [ {wins[8]} ]--------+")
    print(f"2. {participants[1]} [ {wins[1]} ]--------+                                   |")
    print(f"                                                                  Bo3 |--- {participants[12]} [ {wins[12]} ]--------+")
    print(f"3. {participants[2]} [ {wins[2]} ]--------+                                   |                                   |")
    print(f"                              Bo3 |--- {participants[9]} [ {wins[9]} ]--------+                                   |")
    print(f"4. {participants[3]} [ {wins[3]} ]--------+                                                                       |")
    print(f"                                                                                                      Bo7 |--- {participants[17]}")
    print(f"5. {participants[4]} [ {wins[4]} ]--------+                                                                       |")
    print(f"                              Bo3 |--- {participants[10]} [ {wins[10]} ]--------+                                   |")
    print(f"6. {participants[5]} [ {wins[5]} ]--------+                                   |                                   |")
    print(f"                                                                  Bo3 |--- {participants[14]} [ {wins[14]} ]--------+")
    print(f"7. {participants[6]} [ {wins[6]} ]--------+                                   |")
    print(f"                              Bo3 |--- {participants[11]} [ {wins[11]} ]--------+")
    print(f"8. {participants[7]} [ {wins[7]} ]--------+")
    print()
    print(f"                                                                           {participants[13]} [ {wins[13]} ]--------+")
    print(f"                                                                                                      Bo5 |--- {participants[16]}")
    print(f"                                                                           {participants[15]} [ {wins[15]} ]--------+")


# Replace 'path_to_your_file' with the actual path to your 'brackets.txt' file

# Example usage
generate_tournament_bracket("brackets.txt")
