#!/bin/bash
rm -r quallifiers

teams=$(ls bots)
for team in $teams 
do
    javac ./bots/$team/bot1/*.java
    javac ./bots/$team/bot2/*.java
done

mkdir quallifiers quallifiers/G1 quallifiers/G2 quallifiers/G3 quallifiers/G4

cp -r bots/Bot quallifiers/G1
cp -r bots/SpaceY quallifiers/G1
cp -r bots/Ctrl+Alt+Defeat quallifiers/G1
cp -r bots/Rakija quallifiers/G2
cp -r bots/YuGo quallifiers/G2
cp -r bots/Paradoxe quallifiers/G2
cp -r bots/BananaBurek quallifiers/G3
cp -r bots/SofElen quallifiers/G3
cp -r bots/dexter quallifiers/G3
cp -r bots/GarbageCollectors quallifiers/G4
cp -r bots/RustberryDuo quallifiers/G4
cp -r bots/ArtisticMonkeys quallifiers/G4
cp -r bots/BogosortBatalja quallifiers/G4