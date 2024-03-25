import sys




lines = None
filepath=sys.argv[1]

with open(filepath, 'r') as file:
    lines = file.readlines()[-80:]  # Assuming the last 50 lines contain the relevant stats


watchingPlayer = None
for line in lines:
    if "STAT" in line:
        watchingPlayer = line.split(" ")[1].split("/")[-2]

    if 'survive: true' in line:
        print(watchingPlayer)
        break