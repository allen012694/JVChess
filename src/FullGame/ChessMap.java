package FullGame;

import java.io.Serializable;

public class ChessMap implements Serializable{
	
	private static final long serialVersionUID = -6742176661610760603L;
	//private ArrayList<String> moveSet; //keep track of moves 
	private char[][] map = new char[8][8];
	private boolean[][][] dominant; //2 domination map for black (index 1) and white (index 0) 
	public int[][] KLoc; //saving location of 2 Kings, [0][0]=WKR, [0][1]=WKC, [1][0]=BKR, [1][1]=BKC
	public boolean[][] isCastled; //is castling yet [0][0]: queen side of white, [0][1]: king side of white, [1][0]: queen side of black , [1][1]: king side of black (right)
	public int[] passingEnAt; //saving temporarily location of pawn that could be passed, init at (-1,-1)
	public int[] prevMove; //[0]:fromR, [1]:fromC, [2]:toR, [3]:toC
	
	//r=rook, n=knight, b=bishop, q=queen, k=king, p=pawn
	private String back = "rnbqkbnr";
	//private String front = "pppppppp"; 	
	
	public ChessMap() {
		prevMove=new int[4];
		for (int i=0;i<4;i++) prevMove[i]=-1;
		passingEnAt= new int[2];
		passingEnAt[0]=passingEnAt[1]=-1;
		
		isCastled=new boolean[2][2];
		KLoc = new int[2][2];
		for (int i=0;i<8;i++) {
			//lower case for White, upper case for Black
			map[7][i]=back.toCharArray()[i];
			map[6][i]='p';
			map[1][i]='P';
			map[0][i]=back.toUpperCase().toCharArray()[i];
			
			KLoc[1][0]=0; KLoc[1][1]=4; KLoc[0][0]=7; KLoc[0][1]=4; 
			//test hint on map
			//<give the code here>
		}
	}

	//flag cells that is in attack range of rival
	public void dominate() {
		dominant = new boolean[2][8][8];
		for (int i=0;i<8;i++) {
			for (int j=0;j<8;j++) {
				if (map[i][j]=='p' || map[i][j]=='P') Pawn.setDom(this, i, j);
				else if (map[i][j]=='r' || map[i][j]=='R') Rook.setDom(this, i, j);
				else if (map[i][j]=='n' || map[i][j]=='N') Knight.setDom(this, i, j);
				else if (map[i][j]=='b' || map[i][j]=='B') Bishop.setDom(this, i, j);
				else if (map[i][j]=='q' || map[i][j]=='Q') Queen.setDom(this, i, j);
				else if (map[i][j]=='k' || map[i][j]=='K') King.setDom(this, i, j);
			}
		}
	}
	
	public char[][] getMap() {
		return this.map;
	}
	
	public boolean[][][] getDom() {
		return this.dominant;
	}
	
	//expecting a move whether it's safe
	public boolean foresight(int fromR, int fromC, int toR, int toC) {
		ChessMap temp = new ChessMap();
		
		//copy info to a temporary Map
		for (int i=0;i<8;i++)
			for (int j=0;j<8;j++) temp.getMap()[i][j] = this.map[i][j];
		for (int i=0;i<2;i++) {
			temp.KLoc[i][0]=this.KLoc[i][0];
			temp.KLoc[i][1]=this.KLoc[i][1];
			temp.isCastled[i][0]=this.isCastled[i][0];
			temp.isCastled[i][1]=this.isCastled[i][1];
			temp.passingEnAt[i]=this.passingEnAt[i];
		}
		
		if (temp.getMap()[fromR][fromC]=='p' || temp.getMap()[fromR][fromC]== 'P') Pawn.moving(temp, fromR, fromC, toR, toC, true);
		else if (temp.getMap()[fromR][fromC]=='n' || temp.getMap()[fromR][fromC]== 'N') Knight.moving(temp, fromR, fromC, toR, toC);
		else if (temp.getMap()[fromR][fromC]=='r' || temp.getMap()[fromR][fromC]== 'R') Rook.moving(temp, fromR, fromC, toR, toC);
		else if (temp.getMap()[fromR][fromC]=='b' || temp.getMap()[fromR][fromC]== 'B') Bishop.moving(temp, fromR, fromC, toR, toC);
		else if (temp.getMap()[fromR][fromC]=='q' || temp.getMap()[fromR][fromC]== 'Q') Queen.moving(temp, fromR, fromC, toR, toC);
		else if (temp.getMap()[fromR][fromC]=='k' || temp.getMap()[fromR][fromC]== 'K') King.moving(temp, fromR, fromC, toR, toC);
		temp.dominate();
		
		//depend on which side to decide which case is dangerous
		if ((temp.getMap()[toR][toC] == 'p' || temp.getMap()[toR][toC] == 'n' || temp.getMap()[toR][toC] == 'r' ||
				temp.getMap()[toR][toC] == 'b' || temp.getMap()[toR][toC] == 'q' || temp.getMap()[toR][toC] == 'k') && temp.getDom()[1][temp.KLoc[0][0]][temp.KLoc[0][1]] == true)
			return false;
		if ((temp.getMap()[toR][toC] == 'P' || temp.getMap()[toR][toC] == 'N' || temp.getMap()[toR][toC] == 'R' ||
				temp.getMap()[toR][toC] == 'B' || temp.getMap()[toR][toC] == 'Q' || temp.getMap()[toR][toC] == 'K') && temp.getDom()[0][temp.KLoc[1][0]][temp.KLoc[1][1]] == true)
			return false;
		
		return true;
	}
	
	@Override
	public String toString() {
		String display_map ="";
		for (int i=0;i<8;i++) {
			for (int j=0;j<8;j++) {
				if (map[i][j]=='\0') display_map+="0";
				else display_map+=map[i][j];
			}
			display_map+="\n";
		}
		display_map+="-----------------------------\n";
		for (int i=0;i<8;i++) {
			for (int j=0;j<8;j++) display_map+= (dominant[0][i][j])? "x":"0";
			display_map+="\n";
		}
		display_map+="-----------------------------\n";
		for (int i=0;i<8;i++) {
			for (int j=0;j<8;j++) display_map+= (dominant[1][i][j])? "x":"0";
			display_map+="\n";
		}
		return display_map;
	}
}
