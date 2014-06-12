package FullGame;

import javax.swing.JOptionPane;

//The game was originally made by Nguyen Tuong Lan
public class Launcher {
	static public void main (String[] args) {
		JOptionPane.showMessageDialog(null, "This program was made by\nNguyen Tuong Lan!\n");
		new ChessBoard();
	}
}
