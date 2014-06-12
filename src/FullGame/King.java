package FullGame;

import java.awt.Color;

import javax.swing.JButton;

public class King {
	//coloring the cell that available for moving a Queen
	public static void hint(JButton[][] field, ChessMap map, int row, int col) {
		int[] ordr = {-1,-1,-1,0,0,1,1,1};
		int[] ordc = {-1,0,1,-1,1,-1,0,1};
		
		if (map.getMap()[row][col]=='k') {
			for (int i=0;i<8;i++) {
				if ((row+ordr[i]>=0 && row+ordr[i]<8 && col+ordc[i]>=0 && col+ordc[i]<8)  
					 && map.foresight(row, col, row+ordr[i], col+ordc[i])==true && ( map.getMap()[row+ordr[i]][col+ordc[i]]=='\0' 
					     || map.getMap()[row+ordr[i]][col+ordc[i]]=='P' || map.getMap()[row+ordr[i]][col+ordc[i]]=='R'
					     || map.getMap()[row+ordr[i]][col+ordc[i]]=='B' || map.getMap()[row+ordr[i]][col+ordc[i]]=='Q'
					     || map.getMap()[row+ordr[i]][col+ordc[i]]=='N' || map.getMap()[row+ordr[i]][col+ordc[i]]=='K'))
					field[row+ordr[i]][col+ordc[i]].setBackground(Color.green);
			}	
			if (map.getDom()[1][map.KLoc[0][0]][map.KLoc[0][1]]==false) { //king must not be in CHECK in order to castling
				if (map.isCastled[0][0]==false && map.getMap()[7][1]=='\0' && map.getMap()[7][2]=='\0' && map.getMap()[7][3]=='\0' && map.getDom()[1][7][2]==false && map.getDom()[1][7][3]==false && map.getMap()[7][0]=='r') field[7][2].setBackground(Color.green);
				if (map.isCastled[0][1]==false && map.getMap()[7][5]=='\0' && map.getMap()[7][6]=='\0' && map.getDom()[1][7][6]==false && map.getDom()[1][7][5]==false && map.getMap()[7][7]=='r') field[7][6].setBackground(Color.green);
			}
		}
		
		else if (map.getMap()[row][col]=='K') {
			for (int i=0;i<8;i++) {
				if ((row+ordr[i]>=0 && row+ordr[i]<8 && col+ordc[i]>=0 && col+ordc[i]<8)  
					 && map.foresight(row, col, row+ordr[i], col+ordc[i])==true && (map.getMap()[row+ordr[i]][col+ordc[i]]=='\0' 
					     || map.getMap()[row+ordr[i]][col+ordc[i]]=='p' || map.getMap()[row+ordr[i]][col+ordc[i]]=='r'
					     || map.getMap()[row+ordr[i]][col+ordc[i]]=='b' || map.getMap()[row+ordr[i]][col+ordc[i]]=='q'
					     || map.getMap()[row+ordr[i]][col+ordc[i]]=='n' || map.getMap()[row+ordr[i]][col+ordc[i]]=='k'))
					field[row+ordr[i]][col+ordc[i]].setBackground(Color.green);
			}
			
			if (map.getDom()[0][map.KLoc[1][0]][map.KLoc[1][1]]==false) {
				if (map.isCastled[1][1]==false && map.getMap()[0][1]=='\0' && map.getMap()[0][2]=='\0' && map.getMap()[0][3]=='\0' && map.getDom()[0][0][2]==false && map.getDom()[0][0][3]==false && map.getMap()[0][0]=='R') field[0][2].setBackground(Color.green);
				if (map.isCastled[1][0]==false && map.getMap()[0][5]=='\0' && map.getMap()[0][6]=='\0' && map.getDom()[0][0][6]==false && map.getDom()[0][0][5]==false && map.getMap()[0][7]=='R') field[0][6].setBackground(Color.green);
			}
		}
		
	}
	
	public static void setDom(ChessMap map, int row, int col) {
		int[] order = {-1,-1,0,-1,1,0,1,1};;
		
		if (map.getMap()[row][col]=='k') {
			for (int i=0;i<8;i++) {
				if (row+order[i]>=0 && row+order[i]<8 && col+order[(i+1)%8]>=0 && col+order[(i+1)%8]<8)  
					map.getDom()[0][row+order[i]][col+order[(i+1)%8]]=true;
			}
		}
		
		else if (map.getMap()[row][col]=='K') {
			for (int i=0;i<8;i++) {
				if (row+order[i]>=0 && row+order[i]<8 && col+order[(i+1)%8]>=0 && col+order[(i+1)%8]<8)  
					map.getDom()[1][row+order[i]][col+order[(i+1)%8]]=true;
			}
		}
	}
	
	//moving a king (include managing castling)
	public static void moving(ChessMap map, int fromR, int fromC, int toR, int toC) {
		map.getMap()[toR][toC]=map.getMap()[fromR][fromC];
		map.getMap()[fromR][fromC]='\0';
		if (map.getMap()[toR][toC]=='k') { map.KLoc[0][0]=toR; map.KLoc[0][1]=toC; }
		else if (map.getMap()[toR][toC]=='K') { map.KLoc[1][0]=toR; map.KLoc[1][1]=toC; }
		map.prevMove[0]=fromR; map.prevMove[1]=fromC; map.prevMove[2]=toR; map.prevMove[3]=toC; 
		
		//code for castling (castling happen on the same row and column off 2 cells)
		if (fromR==toR && fromC-toC==2 && map.isCastled[(map.getMap()[toR][toC]=='k')? 0:1][0]==false) {
			Rook.moving(map, fromR, 0, toR, toC+1);
			map.isCastled[(map.getMap()[toR][toC]=='k')? 0:1][0]=map.isCastled[(map.getMap()[toR][toC]=='k')? 0:1][1]=true;
			map.prevMove[0]=fromR; map.prevMove[1]=fromC; map.prevMove[2]=toR; map.prevMove[3]=toC; 
		}
		else if (fromR==toR && fromC-toC==-2 && map.isCastled[(map.getMap()[toR][toC]=='k')? 0:1][1]==false) {
			Rook.moving(map, fromR, 7, toR, toC-1);
			map.isCastled[(map.getMap()[toR][toC]=='k')? 0:1][0]=map.isCastled[(map.getMap()[toR][toC]=='K')? 0:1][1]=true;
			map.prevMove[0]=fromR; map.prevMove[1]=fromC; map.prevMove[2]=toR; map.prevMove[3]=toC; 
		}
	}
}
