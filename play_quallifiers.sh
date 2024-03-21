#!/bin/bash

teams=$(ls bots)
for team in $teams 
do
    javac ./bots/$team/bot1/*.java
    javac ./bots/$team/bot2/*.java
done
