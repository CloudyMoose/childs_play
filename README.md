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

Game Loop (tmp)
---------------

The multiplayer aspect is handled in a peer to peer style. The player's actions are transformed into UpdateRequests which are put into a queue. The content of that queue is regularly sent to other players. Upon reception, the players use these UpdateRequests to replay the other player's actions and update their game world.

```
foreach turn:
	send update requests to other players

	receive update requests and apply them to the world

	loop fixedUpdate: actual time flow in the world
		it should be executed a fixed number of times per second (UPS = updates per second)

	render world's state to screen
end

```

### Remarks

* The state is not synchonized atm, Since the local player's action are done the moment they are issued. They should only be applied on the following turn, with the other players' actions.
* The current game loop does not work since actions are done over a certain number of turns which may not be the same for all clients. (Note: if the UPS is low enough, it shouldn't be a problem) Possible solutions:
	- Send the number of fixedUpdates in an updateRequest
	- Synchronize the clients' update frequency in the beginning
	- other?
