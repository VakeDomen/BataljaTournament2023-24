import sys

winner = sys.argv[1]

pairs = {
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

wins_needed = {
    8: 2,
    9: 2,
    10: 2,
    11: 2,
    12: 2,
    14: 2,
    16: 3,
    17: 4,
    18: -1
}


participants = []
wins = []
with open("brackets.txt", 'r') as file:
    for parti in [line.strip() for line in file if line.strip()]:
        participants.append(parti.split(" ")[0])
        wins.append(int(parti.split(" ")[1]))

num_of_pl = len(participants)
pair = pairs[num_of_pl]


reversed_participants = participants.copy()
reversed_participants.reverse()

if len(pair) != 0 and pair[0] != -1:  
    wins[pair[pair.index(len(participants) - 1 - reversed_participants.index(winner))]] += 1


if wins[pair[0]] >= wins_needed[num_of_pl]:
    participants.append(participants[pair[0]])
    wins.append(0)
    if num_of_pl == 12 or num_of_pl == 14:
        participants.append(participants[pair[1]])
        wins.append(0)
    

if wins[pair[1]] >= wins_needed[num_of_pl]:
    participants.append(participants[pair[1]])
    wins.append(0)
    if num_of_pl == 12 or num_of_pl == 14:
        participants.append(participants[pair[0]])
        wins.append(0)
    


# Write the updated data back to the file
with open("brackets.txt", 'w') as file:
    for name, win in zip(participants, wins):
        file.write(f"{name} {win}\n")

