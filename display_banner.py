import sys
from art import *
from colorama import Fore, Style
from colorama import init as colorama_init

colorama_init()

color = Fore.RESET

if len( sys.argv ) == 3:
    if sys.argv[2] == "cyan":
        color = Fore.CYAN + Style.BRIGHT

    if sys.argv[2] == "green":
        color = Fore.YELLOW + Style.BRIGHT


print(color)

tprint(sys.argv[1], font="small")
