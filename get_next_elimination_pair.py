
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

    playing = {
        8: [0, 1],
        9: [2, 3],
        10: [4, 5],
        11: [6, 7],
        12: [8, 9],
        14: [10, 11],
        16: [13, 15],
        17: [12, 14],
        18: [-1]
    }
    for participant in playing[num_of_pl]:
        if participant == -1:
            print("END")
        else:
            print(participants[participant])


# Example usage
generate_tournament_bracket("brackets.txt")
