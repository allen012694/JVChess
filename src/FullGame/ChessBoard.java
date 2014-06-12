package FullGame;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

//Chess board 
public class ChessBoard extends JFrame{
	private static final long serialVersionUID = 2108631743607484560L;
	public boolean END=false, DRAW=false; //check state wheter the game is ending or not
	public boolean isServer=false; //check whether this is a server or client
	public boolean isOnline=false; //check whether this is a offline or online
	private boolean PlayOff=false; //this must be enable before playing game Offline
	public ClientChess client=null; //Thread for request and respond as client
	public ServerChess server=null; //Thread for request and respond as server (also listen for request to connect)
	public ChessMap mapBoard;  //a map of board for managing state of chess round
	public JButton[][] boardCells;  //a GUI ChessBoard allowing player to interact
	private static final int cellSize = 85;
	private JPanel pBoard, pMenu; //panel containning chessboard and chessmen, containing scoresheet and options
	public int turn=0; //even for white, odd for black
	public int selectedRow=-1, selectedCol=-1;
	public JTextArea moveNote; private JScrollPane noteScroll;
	private JButton bCreateSV, bConnectSV, bPlayOff, bReset, bDraw, bDisconnect; //functional buttons
	public int port=0;
	public JLabel player,state; //display which color you are, state of the match
	public String movement; //move info
	public Waiting wait; //a waiting for acception message
	//an inner class for a waiting message (without involve to the thread of the main game)
	public class Waiting extends JFrame {
		private static final long serialVersionUID = -6652542923007114148L;
		public JLabel message = new JLabel("Waiting for accept");
		public Waiting() {
			this.setLayout(new FlowLayout());
			this.setAlwaysOnTop(true);
			this.setSize(200,100);
			this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			this.add(message);
		}
	}
	
	public ChessBoard() {
		bCreateSV=new JButton("Create Game");
		bConnectSV=new JButton("Join Game");
		bDisconnect=new JButton("Disconnect");
		bReset=new JButton("Reset");
		bDraw = new JButton("Draw Request");
		bPlayOff=new JButton("Play Offline");
		boardCells = new JButton[8][8];
		mapBoard=new ChessMap();
		moveNote= new JTextArea();
		noteScroll=new JScrollPane();
		state = new JLabel();
		
		this.init();
		this.decryptMap();
	}
	
	//init display for chessgame
	public void init() {
		//Initialize JFrame of game
		this.setTitle("Chess Work");
		this.setSize((cellSize*8+500), (cellSize*8+38)); //size of the frame
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);  //set default Close method
		this.setLayout(null); //use no layout (manipulation component manually)
		
		//set up pBoard for containing chess board
		pBoard = new JPanel();
		pBoard.setLayout(null);
		pBoard.setBounds(0, 0, cellSize*8, cellSize*8);
		pBoard.setVisible(true);
		this.add(pBoard);
		
		//configure each JButtons (represent for a cell) 
		for (int i=0;i<8;i++) {
			for (int j=0;j<8;j++) {
				boardCells[i][j]=new JButton("");
				boardCells[i][j].setBounds(j*cellSize, i*cellSize, cellSize, cellSize);
				pBoard.add(boardCells[i][j]);
				
				//generate action listener for event mouse click
				boardCells[i][j].addMouseListener(new MouseAdapter() {
					@Override
					public void  mousePressed(MouseEvent e) {
						chessMousePressed(e);
					}
				});
			}
			
		}
		
		//pMenu panel for contain MoveNote and functional buttons
		pMenu=new JPanel();
		pMenu.setLayout(null);
		pMenu.setBounds(pBoard.getBounds().width+40, 0, 400, cellSize*8+38);
		pMenu.setBackground(Color.gray);
		pMenu.setVisible(true);
		this.add(pMenu);
		
		//setup MoveNote
		noteScroll.setViewportView(moveNote);
		noteScroll.setBounds(0,70, pMenu.getSize().width, 450);
		pMenu.add(noteScroll);
		moveNote.setEditable(false);
		moveNote.setFont(new Font("Arial", 1, 20)); //1st parameter is font name; 2nd parameter is style, 0: plain, 1: bold, 2:italic, 3:bold and italic; 3rd parameter is size
		moveNote.setText("");
		
		//set up functional bunttons
		bCreateSV.setBounds(0, noteScroll.getLocation().y+noteScroll.getSize().height+50, 130, 55);
		bDraw.setBounds(bCreateSV.getLocation().x+bCreateSV.getSize().width+5, bCreateSV.getLocation().y, 130, 55);
		bConnectSV.setBounds(bCreateSV.getSize().width*2+10, noteScroll.getLocation().y+noteScroll.getSize().height+50, 130, 55);
		bPlayOff.setBounds(bCreateSV.getLocation().x, bCreateSV.getLocation().y+bCreateSV.getSize().height,130, 55);
		bReset.setBounds(bConnectSV.getLocation().x,bConnectSV.getLocation().y+bConnectSV.getSize().height,130,55);
		bDisconnect.setBounds(bPlayOff.getLocation().x+bPlayOff.getSize().width+5,bPlayOff.getLocation().y,130,55);
		bDisconnect.setEnabled(false);
		pMenu.add(bCreateSV);
		pMenu.add(bDraw);
		pMenu.add(bConnectSV);
		pMenu.add(bPlayOff);
		pMenu.add(bDisconnect);
		pMenu.add(bReset);
		bCreateSV.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				bCreateSV_mouseReleased(arg0);
			}
		});
		bConnectSV.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				bConnectSV_mouseReleased(arg0);
			}
		});
		bPlayOff.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				bPlayOff_mouseReleased(arg0);
			}
		});
		bDisconnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				bDisconnect_mouseReleased(arg0);
			}
		});
		bDraw.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				bDraw_mouseReleased(arg0);
			}
		});
		bReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				bReset_mouseReleased(arg0);
			}
		});
		
		state.setBounds(30, noteScroll.getLocation().y+noteScroll.getSize().height, pMenu.getSize().width-50, 50);
		pMenu.add(state);
		state.setFont(new Font("Arial",1,22));
		state.setForeground(Color.yellow);
		state.setVisible(false);
		
		bDraw.setEnabled(false); //only in Online Mode can use this
		//finish preparing for play
		this.coloringCell();
		this.setVisible(true);
		
		//temporarily disable 2 buttons that yet to be implemented
//		bReset.setEnabled(false);
	}

	//request Draw (using only in Online mode)
	protected void bDraw_mouseReleased(ActionEvent arg0) {
		if (isServer) server.sendAct("rd");
		else client.sendAct("rd");
		
		//create waiting message
		wait=new Waiting();
		wait.setVisible(true);
	}

	//Reset game to initial state: if in Offline mode, enable selection to choose onlinemode (if want)... in Online mode, just reset the chessmap
	protected void bReset_mouseReleased(ActionEvent arg0) {
		if (isOnline) {
			if (isServer) server.sendAct("rr");
			else client.sendAct("rr");
			
			//create a waiting message
			wait=new Waiting();
			wait.setVisible(true);
			return;
		}
		
		mapBoard=new ChessMap();
		this.decryptMap();
		turn=0;
		PlayOff=false;
		bPlayOff.setEnabled(true);
		bConnectSV.setEnabled(true);
		bCreateSV.setEnabled(true);
		bDraw.setEnabled(false);
		END=DRAW=false;
		selectedRow=selectedCol=-1;
		state.setVisible(false);
		this.coloringCell();
	}

	//disconnect the game if in online mode
	@SuppressWarnings("deprecation")
	protected void bDisconnect_mouseReleased(ActionEvent arg0) {
//		if (isOnline) {
			try {
				if (server!=null) {
					server.stop();
					server.closeConnect();
				}
				else if (client!=null) {
					client.stop();
					client.closeConnect();
				}
				isOnline=false;
				isServer=false;
				bDisconnect.setEnabled(false);
				bCreateSV.setEnabled(true);
				bConnectSV.setEnabled(true);
				bPlayOff.setEnabled(true);
				player.setVisible(false);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Can't Close due to some unexpected errors");
			}
//		}
	}

	//For those want to play in a single computer
	protected void bPlayOff_mouseReleased(ActionEvent arg0) {
		bCreateSV.setEnabled(false);
		bConnectSV.setEnabled(false);
		bPlayOff.setEnabled(false);
		PlayOff=true;
	}

	//Play as client
	protected void bConnectSV_mouseReleased(ActionEvent arg0) {
		String tempPort = JOptionPane.showInputDialog(null, "Enter port");
		try {
			port = Integer.parseInt(tempPort);
		} catch (NumberFormatException e1) {
			return;
		}
		bCreateSV.setEnabled(false);
		bConnectSV.setEnabled(false);
		bPlayOff.setEnabled(false);
		client=new ClientChess(this);
		client.start();
		player=new JLabel("You're Black");
		player.setFont(new Font("Arial",0,50));
		player.setForeground(Color.black);
		pMenu.add(player);
		player.setBounds(0, noteScroll.getLocation().y-60,pMenu.getSize().width,50);
		bDisconnect.setEnabled(true);
		bDraw.setEnabled(true);
	}

	//Create a game (host a game) then wait for connection
	protected void bCreateSV_mouseReleased(ActionEvent arg0) {
		String tempPort = JOptionPane.showInputDialog(null, "Enter port");
		try {
			port = Integer.parseInt(tempPort);
		} catch (NumberFormatException e1) {
			return;
		}
		bCreateSV.setEnabled(false);
		bConnectSV.setEnabled(false);
		bPlayOff.setEnabled(false);
		server=new ServerChess(this);
		server.start();
		player=new JLabel("You're White");
		player.setFont(new Font("Arial",0,50));
		player.setForeground(Color.white);
		pMenu.add(player);
		player.setBounds(0, noteScroll.getLocation().y-60,pMenu.getSize().width,50);
		bDisconnect.setEnabled(true);
		bDraw.setEnabled(true);
	}

	//coloring the chess board to its original state
	public void coloringCell() {
		for (int i=0;i<8;i++) {
			for (int j=0;j<8;j++) {
				boardCells[i][j].setBackground(((i+j)%2==0)? Color.decode("#F3F781"):Color.decode("#DF7401")); //Color.decode() display the color from Color HexCode
			}
		}
	}
	
	public JButton[][] getBoardCell() {
		return boardCells;
	}
	
	//event handler for Mouse Pressed
	private void chessMousePressed(MouseEvent e) {
		if (END==true) return;
		//prevent Server and Client go wrong
		if (isOnline==true) {
			if (turn%2==0 && isServer==false) return;
			if (turn%2==1 && isServer==true) return;
		}
		else if (PlayOff==false) return;
		
		//get the location of the component that'd been invoke event listener (the location is relative to the parent container)
		//x axis run through column Num, y axis run through row Num
		int row=e.getComponent().getLocation().y/cellSize, col=e.getComponent().getLocation().x/cellSize; //get the ordering location of a clicking button
		
		if (boardCells[row][col].getBackground().equals(Color.green) && selectedRow>=0 && selectedCol>=0) {			
			this.Move(selectedRow, selectedCol, row, col);
			this.coloringCell();
			this.decryptMap();
			movement=""+selectedRow+selectedCol+row+col+mapBoard.getMap()[row][col];
			selectedRow=-1; selectedCol=-1;
			
			//when player move a piece succesful, send to the other
			if (isOnline==true) {
				try {
					if (isServer==true) server.sendAct(movement);
					else client.sendAct(movement);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1);
				}
//				if (END==true) {
//					PlayOff=false;
//					isOnline=false;
////					END=false;
//				}
			}
			return;
		}
		
		this.coloringCell();
		if (mapBoard.getMap()[row][col]!='\0') boardCells[row][col].setBackground(Color.lightGray);  //hightlight 
		
		if (mapBoard.getMap()[row][col]==((turn%2==0)? 'p':'P')) {Pawn.hint(boardCells, mapBoard, row, col); selectedRow=row; selectedCol=col; }
		else if (mapBoard.getMap()[row][col]==((turn%2==0)? 'n':'N')) {Knight.hint(boardCells, mapBoard, row, col); selectedRow=row; selectedCol=col; }
		else if (mapBoard.getMap()[row][col]==((turn%2==0)? 'r':'R')) {Rook.hint(boardCells, mapBoard, row, col); selectedRow=row; selectedCol=col; }
		else if (mapBoard.getMap()[row][col]==((turn%2==0)? 'b':'B')) {Bishop.hint(boardCells, mapBoard, row, col); selectedRow=row; selectedCol=col; }
		else if (mapBoard.getMap()[row][col]==((turn%2==0)? 'q':'Q')) {Queen.hint(boardCells, mapBoard, row, col); selectedRow=row; selectedCol=col; }
		else if (mapBoard.getMap()[row][col]==((turn%2==0)? 'k':'K')) {King.hint(boardCells, mapBoard, row, col); selectedRow=row; selectedCol=col; }
		
	}
	
	//load the state of chess round from a map made by char array
	public void decryptMap() {
		for (int i=0;i<8;i++) {
			for (int j=0;j<8;j++) {
				switch(mapBoard.getMap()[i][j]) {
					case 'r':	boardCells[i][j].setIcon(new ImageIcon("Chessman\\WRook.gif")); boardCells[i][j].setEnabled(true); break;
					case 'n':	boardCells[i][j].setIcon(new ImageIcon("Chessman\\WKnight.gif")); boardCells[i][j].setEnabled(true); break;
					case 'b':	boardCells[i][j].setIcon(new ImageIcon("Chessman\\WBishop.gif")); boardCells[i][j].setEnabled(true); break;
					case 'q':	boardCells[i][j].setIcon(new ImageIcon("Chessman\\WQueen.gif")); boardCells[i][j].setEnabled(true); break;
					case 'k':	boardCells[i][j].setIcon(new ImageIcon("Chessman\\WKing.gif")); boardCells[i][j].setEnabled(true); break;
					case 'p':	boardCells[i][j].setIcon(new ImageIcon("Chessman\\WPawn.gif")); boardCells[i][j].setEnabled(true); break;
					case 'R':	boardCells[i][j].setIcon(new ImageIcon("Chessman\\BRook.gif")); boardCells[i][j].setEnabled(true); break;
					case 'N':	boardCells[i][j].setIcon(new ImageIcon("Chessman\\BKnight.gif")); boardCells[i][j].setEnabled(true); break;
					case 'B':	boardCells[i][j].setIcon(new ImageIcon("Chessman\\BBishop.gif")); boardCells[i][j].setEnabled(true); break;
					case 'Q':	boardCells[i][j].setIcon(new ImageIcon("Chessman\\BQueen.gif")); boardCells[i][j].setEnabled(true); break;
					case 'K':	boardCells[i][j].setIcon(new ImageIcon("Chessman\\BKing.gif")); boardCells[i][j].setEnabled(true); break;
					case 'P':	boardCells[i][j].setIcon(new ImageIcon("Chessman\\BPawn.gif")); boardCells[i][j].setEnabled(true); break;
					default:	boardCells[i][j].setIcon(null); boardCells[i][j].setEnabled(false);
				}
			}
		}
		mapBoard.dominate(); //after each succesful move, the map will be re-decrypt and recalculate domination area
		
		if (turn==0) return; //avoid out of bound when check round state 
		
		//Notify next player to know if he is in Check, CheckMate or Draw
		this.checkState();

		//coloring previous move
		if (mapBoard.prevMove[0]>=0 && mapBoard.prevMove[1]>=0 && mapBoard.prevMove[2]>=0 && mapBoard.prevMove[3]>=0) {
			boardCells[mapBoard.prevMove[0]][mapBoard.prevMove[1]].setBackground(Color.pink);
			boardCells[mapBoard.prevMove[2]][mapBoard.prevMove[3]].setBackground(Color.pink);
		}
	}
	
	//examining the round state of CHECK
	public boolean isCheck() {
		if (mapBoard.getDom()[(turn-1)%2][mapBoard.KLoc[turn%2][0]][mapBoard.KLoc[turn%2][1]]==true) return true;
		return false;
	}
	
	//examining the round state of CHECKMATE
	public boolean isCheckMate() {
		if (isCheck()) {
			int count=0;
			for (int i=0;i<8;i++) {
				for (int j=0;j<8;j++) {
					if (mapBoard.getMap()[i][j]==((turn%2)==0? 'p':'P')) Pawn.hint(boardCells, mapBoard, i, j);
					if (mapBoard.getMap()[i][j]==((turn%2)==0? 'q':'Q')) Queen.hint(boardCells, mapBoard, i, j);
					if (mapBoard.getMap()[i][j]==((turn%2)==0? 'r':'R')) Rook.hint(boardCells, mapBoard, i, j);
					if (mapBoard.getMap()[i][j]==((turn%2)==0? 'n':'N')) Knight.hint(boardCells, mapBoard, i, j);
					if (mapBoard.getMap()[i][j]==((turn%2)==0? 'b':'B')) Bishop.hint(boardCells, mapBoard, i, j);
					if (mapBoard.getMap()[i][j]==((turn%2)==0? 'k':'K')) King.hint(boardCells, mapBoard, i, j);
				}
			}
			for (int i=0;i<8;i++) for (int j=0;j<8;j++) count+=(boardCells[i][j].getBackground().equals(Color.green))? 1:0;
			this.coloringCell();
			if (count==0) {
				END=true;
			}
			return (count==0); //checkmate only if CHECK and (NO AVAILABLE MOVE or KING CAN'T MOVE) 
		}		
		return false;
	}
	
	//examining the round state of DRAW
	public boolean isDraw() {
		//Draw: request from rival
		if (DRAW==true) {
			END=true;
			return true;
		}
		
		//Draw: no available move
		int count=0; int countChess=0;
		for (int i=0;i<8;i++) {
			for (int j=0;j<8;j++) {
				countChess+=(mapBoard.getMap()[i][j]!='\0')? 1:0;
				if (mapBoard.getMap()[i][j]==((turn%2)==0? 'p':'P')) Pawn.hint(boardCells, mapBoard, i, j);
				if (mapBoard.getMap()[i][j]==((turn%2)==0? 'q':'Q')) Queen.hint(boardCells, mapBoard, i, j);
				if (mapBoard.getMap()[i][j]==((turn%2)==0? 'r':'R')) Rook.hint(boardCells, mapBoard, i, j);
				if (mapBoard.getMap()[i][j]==((turn%2)==0? 'n':'N')) Knight.hint(boardCells, mapBoard, i, j);
				if (mapBoard.getMap()[i][j]==((turn%2)==0? 'b':'B')) Bishop.hint(boardCells, mapBoard, i, j);
				if (mapBoard.getMap()[i][j]==((turn%2)==0? 'k':'K')) King.hint(boardCells, mapBoard, i, j);
			}
		}
		for (int i=0;i<8;i++) for (int j=0;j<8;j++) count+=(boardCells[i][j].getBackground().equals(Color.green))? 1:0;
		this.coloringCell();
		if (count==0) {
			END=true;
			return true;
		}
		
		//Draw: only 2 kings left
		if (countChess==2) {
			END=true;
			return true;
		}
		
		//Draw: the same move set 3 times
		
		//Draw: 50 moves but no piece is removed
		return false;
	}
	
	//check state of round
	public void checkState() {
		//CheckMate go first (but method to check state CHECK go first in method check CHECKMATE
		if (isCheckMate()) {
//			JOptionPane.showMessageDialog(null, "CheckMate!\n"+((((turn-1)%2)==0)?"White":"Black")+" Player Win!"); 
			state.setText("CHECKMATE! "+((((turn-1)%2)==0)?"White":"Black")+" Player Win!");
			state.setVisible(true);
			return;
		}
		
		if (isCheck()) {
//			JOptionPane.showMessageDialog(null, "Check\n");
			state.setText("CHECK");
			state.setVisible(true);
			return;
		}
		
		if (isDraw()) {
//			JOptionPane.showMessageDialog(null, "Draw");
			state.setText("DRAW");
			state.setVisible(true);
			return;
		}
		state.setVisible(false);
	}
	
	//move a piece from ?? to ??
	public void Move(int fromR, int fromC, int toR, int toC) {
		if (mapBoard.getMap()[fromR][fromC]==((turn%2==0)? 'p':'P')) Pawn.moving(mapBoard, fromR, fromC, toR, toC,false);
		else if (mapBoard.getMap()[fromR][fromC]==((turn%2==0)? 'n':'N')) Knight.moving(mapBoard, fromR, fromC, toR, toC);
		else if (mapBoard.getMap()[fromR][fromC]==((turn%2==0)? 'r':'R')) Rook.moving(mapBoard, fromR, fromC, toR, toC);
		else if (mapBoard.getMap()[fromR][fromC]==((turn%2==0)? 'b':'B')) Bishop.moving(mapBoard, fromR, fromC, toR, toC);
		else if (mapBoard.getMap()[fromR][fromC]==((turn%2==0)? 'q':'Q')) Queen.moving(mapBoard, fromR, fromC, toR, toC);
		else if (mapBoard.getMap()[fromR][fromC]==((turn%2==0)? 'k':'K')) King.moving(mapBoard, fromR, fromC, toR, toC);
		
		turn++; //switch player
	}

}
 