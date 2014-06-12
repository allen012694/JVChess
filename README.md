JVChess
=======
This is a small chess game written by Java on Eclipse.

The game support solo play (but not supporting AI) and versus play (on the same computer or through the network).

With the feature versus through the network, 2 players must decided 1 of them to be server and create new server 
with an available port, the other side will join the game with the destination IP address and the port that matches 
with the server.

The game is not complete yet, there are still some rules in Official Chess Game that have yet been implemented.
For example: 3 repeated sets of move make the game draw, only 2 kings left make the game draw, 1 king vs 1 king + 1 bishop make the game draw...

The code structure is far to be good and need more improvement by combining with design patterns.
The game should include a movement log, AI in solo mode.
