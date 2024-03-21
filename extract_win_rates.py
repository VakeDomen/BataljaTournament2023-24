import os
from collections import defaultdict
from rich.console import Console
from rich.table import Table

def process_log_file(filepath):
    """Process a log file to find the winning team."""
    lines = None
    with open(filepath, 'r') as file:
        lines = file.readlines()[-50:]  # Assuming the last 50 lines contain the relevant stats

    p1 = filepath.split(".")[0].split("/")[2].split("-")[0]
    p2 = filepath.split(".")[0].split("/")[2].split("-")[1]

    wins = {}
    wins[p1] = 0
    wins[p2] = 0

    # Filter lines that indicate a surviving bot
    # survivors = [line for line in lines if 'survive: true' in line]
    # winners = []
    watchingPlayer = None
    for line in lines:
        if p1 in line:
            watchingPlayer = p1

        if p2 in line:
            watchingPlayer = p2

        if 'survive: true' in line:
            wins[watchingPlayer] = 1

    # for line in survivors:
    #     # Assuming team name is properly included before 'survive: true'
    #     # and following a specific pattern in the stat line
    #     parts = line.split('/')
    #     if len(parts) > 2:
    #         team_name = parts[-4]  # Adjust based on the actual structure of your lines
    #         winners.append(team_name)

    return wins

def calculate_win_rates(directory):
    """Calculate and print win rates for each group in the directory."""
    groups = [d for d in os.listdir(directory) if os.path.isdir(os.path.join(directory, d))]
    for group in sorted(groups):
        
        table = Table(title="Group: " + group)

        win_counts = {}

        for root, dirs, files in os.walk(os.path.join(directory, group)):
            for file in files:
                if file.endswith('.log'):
                    winners = process_log_file(os.path.join(root, file))
                    for winner in winners:
                        if win_counts.get(winner) is None:
                            win_counts[winner] = [0,0]
                        win_counts[winner][0] += winners[winner]
                        win_counts[winner][1] += 1
        
        # Display win rates
        
                
        win_rates = [(team, (wins[0] / wins[1]) * 100) for team, wins in win_counts.items()]
        # Sort by win rate in descending order
        sorted_win_rates = sorted(win_rates, key=lambda x: x[1], reverse=True)

        table = Table()
        table.add_column(group + " Team")
        table.add_column("Win Rate")

        row_count = 0
        for team, win_rate in sorted_win_rates:
            style='bright_green'
            if row_count >= 2:
                style='red'
            row_count += 1
            table.add_row(team, f"{win_rate:.2f}%", style=style)

        console = Console()
        console.print(table)

# Change this to the path of your 'quallifiers_games' directory
directory = "quallifiers_games"
calculate_win_rates(directory)
