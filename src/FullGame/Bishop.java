
package FullGame;

import java.awt.Color;

import javax.swing.JButton;

public class Bishop {
	//coloring the cell that available for moving a Bishop
	public static void hint(JButton[][] field, ChessMap map, int row, int col) {
		int fromR=row, fromC=col;
		int[] offs = {-1,-1,1,1};
		for (int i=0;i<4;i++) span(field, map, row+offs[i], col+offs[(i+1)%4], map.getMap()[row][col],offs[i],offs[(i+1)%4],fromR,fromC); 
	}
	
	//spanning recursively
	private static void span(JButton[][] field, ChessMap map, int row, int col, char root, int offr, int offc, int fromR, int fromC) {
		if (row>=0 && row<8 && col>=0 && col<8) {
				if (map.getMap()[row][col]=='\0') {
				if (map.foresight(fromR, fromC, row, col) == true) field[row][col].setBackground(Color.green);
				span(field, map, row+offr, col+offc, root, offr, offc, fromR, fromC);
				return;
			}
			
			else if (root == 'b' && (map.getMap()[row][col]=='R' || map.getMap()[row][col]=='B' || map.getMap()[row][col]=='N'
									|| map.getMap()[row][col]=='P' || map.getMap()[row][col]=='K' || map.getMap()[row][col]=='Q')) {
				if (map.foresight(fromR, fromC, row, col) == true) field[row][col].setBackground(Color.green);
				return;
			}
			else if (root == 'B' && (map.getMap()[row][col]=='r' || map.getMap()[row][col]=='b' || map.getMap()[row][col]=='n'
									|| map.getMap()[row][col]=='p' || map.getMap()[row][col]=='k' || map.getMap()[row][col]=='q')) {
				if (map.foresight(fromR, fromC, row, col) == true) field[row][col].setBackground(Color.green);
				return;
			}
		}
	}
	
	public static void setDom(ChessMap map,int row, int col) {
		spanDom(map,row-1,col-1,map.getMap()[row][col],-1,-1);
		spanDom(map,row-1,col+1,map.getMap()[row][col],-1,1);
		spanDom(map,row+1,col-1,map.getMap()[row][col],1,-1);
		spanDom(map,row+1,col+1,map.getMap()[row][col],1,1);
	}
	
	private static void spanDom(ChessMap map, int row, int col, char root, int offr, int offc) {
		if (row>=0 && row<8 && col>=0 && col<8) {
			if (root=='b' && map.getMap()[row][col]=='\0') {
				map.getDom()[0][row][col]=true;
				spanDom(map, row+offr, col+offc, root, offr, offc);
				return;
			}
			if (root=='B' && map.getMap()[row][col]=='\0') {
				map.getDom()[1][row][col]=true;
				spanDom(map, row+offr, col+offc, root, offr, offc);
				return;
			}
			if (root == 'b' && map.getMap()[row][col]!='\0') {
				map.getDom()[0][row][col]=true;
				return;
			}
			if (root == 'B' && map.getMap()[row][col]!='\0') {
				map.getDom()[1][row][col]=true;
				return;
			}
		}
	}
	
	//moving a Bishop
	public static void moving(ChessMap map, int fromR, int fromC, int toR, int toC) {
		map.getMap()[toR][toC]=map.getMap()[fromR][fromC];
		map.getMap()[fromR][fromC]='\0';
		map.prevMove[0]=fromR; map.prevMove[1]=fromC; map.prevMove[2]=toR; map.prevMove[3]=toC;
	}
}
