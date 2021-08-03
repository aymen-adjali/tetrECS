# TETRECS
This project is a fast paced block placement game (based on tetris). Compete to achieve new local and online high scores!
 
## Table of Contents
* [General Info](#general-information)
* [Technologies Used](#technologies-used)
* [Video](#video)
* [Usage](#usage)
* [Project Status](#project-status)
* [Room for Improvement](#room-for-improvement)
* [Acknowledgements](#acknowledgements)

## General Information
Skills developed as a result of this project:
- JavaFX
- Creating and using custom components
- Graphics and animation
- Listeners, properties and binding
- Communications
- Media
- Files

The battle is outputted into the command line.

There are many mechanics of the simulation, such as Hit Points, Knowledge Points, Experience Points, and characteristics like speed, attack and defence, which change 
when characters level up.

The "brains" of the simulation is the StudentTeam class, where the program decides which attack to use. All student have base attacks, 
and special abilities which can only be used if they have enough KP (knowledge points). Students have multiple special abilities so the class
also tries to make the most of each ability, using the ability that would be most effective in that situation. For example choosing to heal if 
students are low HP instead of attacking.

## Technologies Used
- Java
- Command line

## Video

![Simulation](./video/ECSBattleArena.gif)

## Usage
A Monsters.txt file (in the correct format) is needed in order to provide monsters for the students to fight. 
Each line in the file is a new "level" of monsters. Made up of minions and bosses.
The simulation is run with the command:

javac TowerOfMonsters.java

java TowerOfMonsters (file(change location if necessary) that you want to use to run the simulation with).
eg: java TowerOfMonsters ../Monsters.txt

### Example Monster.txt file
![Example of Monsters.txt file](./images/monsters.png)

## Project Status
Project is: _complete_

## Room for Improvement

Room for improvement:
- Add a GUI for the simulation.
- Add new student types with different abilities. 
- Adjust StudentTeam to be smarter/more optimise for calculating student attacks.

## Acknowledgements
- This project was inspired by [The Electronics and Computer Science department at The University Of Southampton](https://www.ecs.soton.ac.uk/).
