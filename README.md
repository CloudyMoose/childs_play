Child's Play
===========

Something something RTS children imagination, something capture points, something.


Architecture (tmp)
------------------

```
ChildsPlayGame - Global game singleton class.
	\_  Game Screen - The game loop is there. Other screens can be the main menu for example.
		|_ World - Representation of what's going on in the game.
		|	|_ Players
		|	\_ Map
		\_ WorldRenderer - Renders the World (duh).

```