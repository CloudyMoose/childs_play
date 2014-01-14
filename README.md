Child's Play
===========

Child’s Play is a turn based strategy game for mobile devices in which you control a group of children as they play in their neighborhood. The children are pretending to be medieval knights, thieves and wizards and compete to win a pretend war against children of other factions

The game was developped for the Computer Game Design class at KTH



You can download the game [here](https://github.com/CloudyMoose/childs_play/releases)



In this demo, you will play on a simple map made of 5 areas that you will have to take control of to get an edge over the other player. Each player will start with a castle and a few units. To be able to recruit more of them, you will have to take control of areas containing Apple Trees, which generate resources every turn. To win, you have to reduce your opponent’s health point to 0 by attacking his/her castle with your units or controlling areas containing Catapults, which will damage his castle every turn. Area capture is done by staying on the area’s control tiles, which are indicated by a flag of the color of the player controlling that area (or just a pole when the area is neutral). At the beginning of each turn, every unit standing on a control tile will raise your flag higher for that area. When your flag reaches the top, you will be able to get benefits from that area starting from the next turn.

There are 3 possible actions: attack and move with a child, and recruit at your castle. The combat is really simple now: the first one to attack wins. You have to manage carefully your actions to get the advantage. Each of these actions use a ticket, and you have 5 of them at the beginning of each turn. The same unit can do many actions in the same turns, as long as you have tickets for them. However, this comes with a penalty. For example, in the beginning, a child can move by up to 4 tiles, but if you try to move him again, he will only be able to move by 3 tiles. You are also able to attack only in your first 2 actions with a single child.

Two modes are playable for now:

* single player to try the game commands and mechanism. There is no Computer Player in the game yet. From the title menu, click **Start Single Player Server** and then click **Play**.
* multiplayer against another human opponent: You can play on the same computer by running the JAR twice, or with any two devices (android phones, computers) by being on the same network. One player has to click **Start Multiplayer Server**, which will start listening on the network. When clicking **Play**, the application will automatically find the server on the network. The first player to join will be Blue. Both players have to click **Play**, even the one who started the server.


## Pictures:

### Title Screen
![Title Screen](https://raw2.github.com/CloudyMoose/childs_play/master/raw-assets/preview/childs_play_title.jpg)

### Game Screen
![Game Screen](https://raw2.github.com/CloudyMoose/childs_play/master/raw-assets/preview/childsplay_game.jpg)
![Game Screen Explained](https://raw2.github.com/CloudyMoose/childs_play/master/raw-assets/preview/childsplay_tuto.jpg)
