Child's Play
===========

Something something -RTS- Turn Based,  children's imagination, something capture points, something.

Gameplay
--------

### Actions

Possible options:

* Regular turn based: Every units can move once and have an action (wait/attack/other) each turn
* DnD: Every unit has 2 actions (move/wait/attack/other) each turn. Doing the same action twice has disminishing return
* Ticket: The player has a fixed number of ticket each turn. He can use them to do an action (move/wait/attack/other) on any unit. Disminishing return by action type

### Game state synchronization

At the beginning of your turn, you download the list of actions done by the other player and replay them.

### Terrain

The map is made of areas (to be conquered) that are made of hexagonal tiles.


Architecture (tmp)
------------------

```
ChildsPlayGame - Global game singleton class.
	|_  Game Screen - The game loop is there. Other screens can be the main menu for example.
	|	|_ World - Representation of what's going on in the game. Also available as singleton
	|	|	|_ Players
	|	|	\_ Map
	|	\_ WorldRenderer - Renders the World (duh).
	\_ NetworkPeer - takes care of sending and recieving messages

```

Network Protocol
----------------
[![wsd](http://www.websequencediagrams.com/cgi-bin/cdraw?lz=cGFydGljaXBhbnQgUGxheWVyCgAHDENsaWVudAAGDVNlcnZlcgoKaWYgdGhlcmUgaXMgbm8gcwASBSBydW5uaW5nCgkARwYtPisAKgY6IGNyZWF0ZQAhBwplbmQKABoJAGAGABoJYwBvBgB2Bi0-ADgJb25uZWN0L2luaXQgc2VxdWVuY2UKCmFsdAB2BzogbmJDAB8GaW9ucyA9PSBuYk1heACBUgZzCgCBNAYARwpzdGFydCBnYW1lCgoKbG9vcCB0dXJucwoJAAcFAGgHaW9ucwoJCQA0CACBIwgANgZ0dXJuIHJlcXVlc3QgWwCCOgZBAHgGXQoJCQCBOggAglMGOiB1cGRhdGUAbQUgc3RhdGUsAD0LAIIGCQAmCHBsYXlcbgAWBQkAgkYJAIIjCGVuZACBKQUsIHNlbmQgQ29tbWFuZFsAagwAgnEIAIEKDQoJZW5kAIJ5BWVuZA&s=default)](http://www.websequencediagrams.com/?lz=cGFydGljaXBhbnQgUGxheWVyCgAHDENsaWVudAAGDVNlcnZlcgoKaWYgdGhlcmUgaXMgbm8gcwASBSBydW5uaW5nCgkARwYtPisAKgY6IGNyZWF0ZQAhBwplbmQKABoJAGAGABoJYwBvBgB2Bi0-ADgJb25uZWN0L2luaXQgc2VxdWVuY2UKCmFsdAB2BzogbmJDAB8GaW9ucyA9PSBuYk1heACBUgZzCgCBNAYARwpzdGFydCBnYW1lCgoKbG9vcCB0dXJucwoJAAcFAGgHaW9ucwoJCQA0CACBIwgANgZ0dXJuIHJlcXVlc3QgWwCCOgZBAHgGXQoJCQCBOggAglMGOiB1cGRhdGUAbQUgc3RhdGUsAD0LAIIGCQAmCHBsYXlcbgAWBQkAgkYJAIIjCGVuZACBKQUsIHNlbmQgQ29tbWFuZFsAagwAgnEIAIEKDQoJZW5kAIJ5BWVuZA&s=default)
<!--
participant Player
participant Client
participant Server

if there is no server running
	Player->+Server: create server
end
Player->+Client: create client
Client->Server: connect/init sequence

alt server: nbConnections == nbMaxPlayers
	Server->Server: start game


	loop turns
		loop connections
			Server->Client: start turn request [PlayerActions]
			Client->Player: update game state, start turn
	Player->Player: play\nturn
			Player->Client: end turn, send Command[]
			Client->Server: PlayerActions
		end
	end
end
 -->
