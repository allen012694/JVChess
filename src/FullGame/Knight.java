package FullGame;

import java.awt.Color;

import javax.swing.JButton;

public class Knight {
	
	//coloring the cell that available for moving a Pawn
	public static void hint(JButton[][] field, ChessMap map, int row, int col) {
		int[] order = {-1,-2,-1,2,1,-2,1,2}; //check condition following the changes of row, col index by this order

		//use for white player
		if (map.getMap()[row][col]=='n') {
			for (int i=0;i<8;i++) {
				if ((row+order[i]>=0 && row+order[i]<8 && col+order[(i+1)%8]>=0 && col+order[(i+1)%8]<8) //the destination must be inside board
						&& (map.getMap()[row+order[i]][col+order[(i+1)%8]]=='P' || map.getMap()[row+order[i]][col+order[(i+1)%8]]=='R'
							|| map.getMap()[row+order[i]][col+order[(i+1)%8]]=='B' || map.getMap()[row+order[i]][col+order[(i+1)%8]]=='Q'
							|| map.getMap()[row+order[i]][col+order[(i+1)%8]]=='N' || map.getMap()[row+order[i]][col+order[(i+1)%8]]=='K'
							|| map.getMap()[row+order[i]][col+order[(i+1)%8]]=='\0')
						&& map.foresight(row, col, row+order[i], col+order[(i+1)%8]) == true)
					field[row+order[i]][col+order[(i+1)%8]].setBackground(Color.green);
			}
		}
		
		//use for black player
		else if (map.getMap()[row][col]=='N') {
			for (int i=0;i<8;i++) {
				if ((row+order[i]>=0 && row+order[i]<8 && col+order[(i+1)%8]>=0 && col+order[(i+1)%8]<8)
						&& (map.getMap()[row+order[i]][col+order[(i+1)%8]]=='p' || map.getMap()[row+order[i]][col+order[(i+1)%8]]=='r'
							|| map.getMap()[row+order[i]][col+order[(i+1)%8]]=='b' || map.getMap()[row+order[i]][col+order[(i+1)%8]]=='q'
							|| map.getMap()[row+order[i]][col+order[(i+1)%8]]=='n' || map.getMap()[row+order[i]][col+order[(i+1)%8]]=='k'
							|| map.getMap()[row+order[i]][col+order[(i+1)%8]]=='\0')
						&& map.foresight(row, col, row+order[i], col+order[(i+1)%8]) == true)
					field[row+order[i]][col+order[(i+1)%8]].setBackground(Color.green);
			}
		}
	}
	
	//set Dominant area for horseman
	public static void setDom(ChessMap map, int row, int col) {
		int[] order = {-1,-2,-1,2,1,-2,1,2};
		
		for (int i=0;i<8;i++) {
			if (row+order[i]>=0 && row+order[i]<8 && col+order[(i+1)%8]>=0 && col+order[(i+1)%8]<8) //the destination must be inside board
				map.getDom()[(map.getMap()[row][col]=='n')? 0:1][row+order[i]][col+order[(i+1)%8]]=true;
		}		
	}
	
	//moving a Horseman
	public static void moving(ChessMap map, int fromR, int fromC, int toR, int toC) {
		map.getMap()[toR][toC]=map.getMap()[fromR][fromC];
		map.getMap()[fromR][fromC]='\0';
		map.prevMove[0]=fromR; map.prevMove[1]=fromC; map.prevMove[2]=toR; map.prevMove[3]=toC;
	}
}
