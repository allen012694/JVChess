package FullGame;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class Pawn {
	
	//coloring the cell that available for moving a Pawn
	public static void hint(JButton[][] field, ChessMap map, int row, int col) {
		//use for white player
		if (map.getMap()[row][col]=='p') {
			
			if (map.getMap()[row-1][col]=='\0' && row-1>=0 && row-1<8) {
				if (map.foresight(row, col, row-1, col) == true) field[row-1][col].setBackground(Color.green);
				if (row==6 && map.getMap()[row-2][col]=='\0' && map.foresight(row, col, row-2, col) == true) field[row-2][col].setBackground(Color.green);
			}
			if (col+1<8 && (map.getMap()[row-1][col+1]=='P' || map.getMap()[row-1][col+1]=='Q' || map.getMap()[row-1][col+1]=='N' || map.getMap()[row-1][col+1]=='R' || map.getMap()[row-1][col+1]=='B' || map.getMap()[row-1][col+1]=='K') && map.foresight(row, col, row-1, col+1) == true) field[row-1][col+1].setBackground(Color.green);
			if (col-1>=0 && (map.getMap()[row-1][col-1]=='P' || map.getMap()[row-1][col-1]=='Q' || map.getMap()[row-1][col-1]=='N' || map.getMap()[row-1][col-1]=='R' || map.getMap()[row-1][col-1]=='B' || map.getMap()[row-1][col-1]=='K') && map.foresight(row, col, row-1, col-1) == true) field[row-1][col-1].setBackground(Color.green);
			//Passing hint when the pawn of current turn stand on the same row as previous turn and the column off is 1 cell
			if (row==map.passingEnAt[0] && Math.abs(col-map.passingEnAt[1])==1 && map.getMap()[row][map.passingEnAt[1]]=='P' && map.foresight(row, col, row-1, map.passingEnAt[1])) field[row-1][map.passingEnAt[1]].setBackground(Color.green);
		}
		
		//use for black player
		else if (map.getMap()[row][col]=='P') {
			
			if (map.getMap()[row+1][col]=='\0' && row+1>=0 && row+1<8) {
				if (map.foresight(row, col, row+1, col) == true) field[row+1][col].setBackground(Color.green);
				if (row==1 && map.getMap()[row+2][col]=='\0' && map.foresight(row, col, row+2, col) == true) field[row+2][col].setBackground(Color.green);
			}
			if (col+1<8 && (map.getMap()[row+1][col+1]=='p' || map.getMap()[row+1][col+1]=='q' || map.getMap()[row+1][col+1]=='n' || map.getMap()[row+1][col+1]=='r' || map.getMap()[row+1][col+1]=='b' || map.getMap()[row+1][col+1]=='k') && map.foresight(row, col, row+1, col+1) == true) field[row+1][col+1].setBackground(Color.green);
			if (col-1>=0 && (map.getMap()[row+1][col-1]=='p' || map.getMap()[row+1][col-1]=='q' || map.getMap()[row+1][col-1]=='n' || map.getMap()[row+1][col-1]=='r' || map.getMap()[row+1][col-1]=='b' || map.getMap()[row+1][col-1]=='k') && map.foresight(row, col, row+1, col-1) == true) field[row+1][col-1].setBackground(Color.green);
			if (row==map.passingEnAt[0] && Math.abs(col-map.passingEnAt[1])==1 && map.getMap()[row][map.passingEnAt[1]]=='p' && map.foresight(row, col, row+1, map.passingEnAt[1])) field[row+1][map.passingEnAt[1]].setBackground(Color.green);
		}
	}
	
	//set domination area for both side
	public static void setDom(ChessMap map,int row, int col) {
		//use for white player
		if (map.getMap()[row][col]=='p') {
			if (row-1>=0 && row-1<8 && col+1>=0 && col+1<8) map.getDom()[0][row-1][col+1]=true;
			if (row-1>=0 && row-1<8 && col-1>=0 && col-1<8) map.getDom()[0][row-1][col-1]=true;
			if (row==map.passingEnAt[0] && Math.abs(col-map.passingEnAt[1])==1 && map.getMap()[row][map.passingEnAt[1]]=='P') map.getDom()[0][row][map.passingEnAt[1]]=true;
		}
		
		//use for black player
		else if (map.getMap()[row][col]=='P') {
			if (row+1>=0 && row+1<8 && col+1>=0 && col+1<8) map.getDom()[1][row+1][col+1]=true;
			if (row+1>=0 && row+1<8 && col-1>=0 && col-1<8) map.getDom()[1][row+1][col-1]=true;
			if (row==map.passingEnAt[0] && Math.abs(col-map.passingEnAt[1])==1 && map.getMap()[row][map.passingEnAt[1]]=='p') map.getDom()[1][row][map.passingEnAt[1]]=true;
		}
	}
	
	//moving a pawn (include managing promoting)
	public static void moving(ChessMap map, int fromR, int fromC, int toR, int toC, boolean inTemp) {
		map.getMap()[toR][toC]=map.getMap()[fromR][fromC];
		map.getMap()[fromR][fromC]='\0';
		map.prevMove[0]=fromR; map.prevMove[1]=fromC; map.prevMove[2]=toR; map.prevMove[3]=toC;
		
		//in case there is a pawn advanced 2 cells forward, save the location for enable passing Pawn 
		if (fromC==toC && Math.abs(fromR-toR)==2) {
			map.passingEnAt[0]=toR;
			map.passingEnAt[1]=toC;
			return; //then break the function
		}
		
		if (fromR==map.passingEnAt[0] && Math.abs(fromC-map.passingEnAt[1])==1) {
			map.getMap()[map.passingEnAt[0]][map.passingEnAt[1]]='\0';
		}
		map.passingEnAt[0]=map.passingEnAt[1]=-1;
		
		if (inTemp==false) {
			//construct a Frame for choosing
			class Choice extends JPanel {
	
				private static final long serialVersionUID = 1L;
				private ButtonGroup gChoice;
				private JRadioButton[] rbChoices;
				private JLabel[] icons;
				public int selection;
				
				public Choice(boolean isWhite) {
					this.init(isWhite); //definite player
				}
				
				private void init(boolean isWhite) {
					this.setLayout(new GridLayout(2,2));
					icons=new JLabel[4];
					icons[0]=new JLabel();
					icons[0].setIcon(new ImageIcon((isWhite)?"Chessman\\WQueen.gif":"Chessman\\BQueen.gif"));
					icons[1]=new JLabel();
					icons[1].setIcon(new ImageIcon((isWhite)?"Chessman\\WRook.gif":"Chessman\\BRook.gif"));
					icons[2]=new JLabel();
					icons[2].setIcon(new ImageIcon((isWhite)?"Chessman\\WBishop.gif":"Chessman\\BBishop.gif"));
					icons[3]=new JLabel();
					icons[3].setIcon(new ImageIcon((isWhite)?"Chessman\\WKnight.gif":"Chessman\\BKnight.gif"));
					
//					this.add(icons[0]); this.add(icons[1]); this.add(icons[2]); this.add(icons[3]);
					
					rbChoices = new JRadioButton[4];
					rbChoices[0] = new JRadioButton("");
					rbChoices[1] = new JRadioButton("");
					rbChoices[2] = new JRadioButton("");
					rbChoices[3] = new JRadioButton("");
					
					gChoice = new ButtonGroup();
					for (int i=0;i<4;i++) {
						gChoice.add(rbChoices[i]);
						this.add(icons[i]);
						this.add(rbChoices[i]);
						rbChoices[i].addMouseListener(new MouseAdapter() {
							@Override
							public void mouseReleased(MouseEvent e) {
								confirm(e);
							}
						});
					}
					
					rbChoices[0].setSelected(true);
					selection=0;
				}

				public void confirm(MouseEvent e) {
					for (int i=0;i<4;i++) if (rbChoices[i].isSelected()) {
						selection=i;
						return;
					}
				}
			}
			
			//code for promoting
			if (map.getMap()[toR][toC]=='p' && toR==0) {
				
				Choice choice = new Choice(true);
				
				choice.setVisible(true);
			
				JOptionPane.showMessageDialog(null, choice, "Choice", JOptionPane.PLAIN_MESSAGE);
				if (choice.selection==0) map.getMap()[toR][toC]='q';
				else if (choice.selection==1) map.getMap()[toR][toC]='r';
				else if (choice.selection==2) map.getMap()[toR][toC]='b';
				else if (choice.selection==3) map.getMap()[toR][toC]='n';
			}
			
			else if (map.getMap()[toR][toC]=='P' && toR==7) {
				Choice choice = new Choice(false);
				
				choice.setVisible(true);
			
				JOptionPane.showMessageDialog(null, choice, "Choice",JOptionPane.QUESTION_MESSAGE);
				if (choice.selection==0) map.getMap()[toR][toC]='Q';
				else if (choice.selection==1) map.getMap()[toR][toC]='R';
				else if (choice.selection==2) map.getMap()[toR][toC]='B';
				else if (choice.selection==3) map.getMap()[toR][toC]='N';
			}
		}
	}
}
