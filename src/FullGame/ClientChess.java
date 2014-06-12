package FullGame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

public class ClientChess extends Thread{
	public Socket comSock=null;
	public ObjectInputStream instream = null; //for read input stream
	public ObjectOutputStream outstream = null; //for push stream as output stream
	private int portt; private String IP;
	private ChessBoard board=null;
	int count=0;
	
	public ClientChess(ChessBoard board) {
		this.portt=board.port;
		this.board=board;
	}
	
	public void sendAct(String nextAct) {
		try {
			outstream.writeObject(nextAct);
			outstream.flush();
		} catch (IOException e) {
		}
	}
	
	public void closeConnect() throws IOException {
		outstream.close();
		instream.close();
		comSock.close();
	}

	@Override
	public void run() {
		IP=JOptionPane.showInputDialog("Enter IP Address", null);
		try {
			comSock = new Socket(IP,portt);
			outstream=new ObjectOutputStream(comSock.getOutputStream());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "There is no available host at port: "+portt);
			return;
		}
		
		JOptionPane.showMessageDialog(null, "Connected");
		
		board.isOnline=true;
		
		try {
			instream=new ObjectInputStream(comSock.getInputStream());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e);
		}
		while (true) {
			JOptionPane.showMessageDialog(null, "chay");
			try {
				if (comSock!=null) {
					String temp=null;
					if ((temp=(String)instream.readObject())!=null && temp.length()>0) {
						//receive Draw request
						if (temp.equalsIgnoreCase("rd")) {
							int choice=JOptionPane.showConfirmDialog(null, "Your rival want this match should be DRAW!\nAccept???","Message",JOptionPane.YES_NO_OPTION);
							if (choice==JOptionPane.NO_OPTION) this.sendAct("dd");
							else {
								this.sendAct("ad");
								board.DRAW=board.END=true;
								board.decryptMap();
							}
						}
						//accept draw
						else if (temp.equalsIgnoreCase("ad")) {
							board.wait.setVisible(false);
							board.wait=null;
							board.DRAW=board.END=true;
							board.decryptMap();
						}
						//decline draw
						else if (temp.equalsIgnoreCase("dd")) {
							board.wait.message.setText("Request was rejected");
							board.wait.setDefaultCloseOperation(2);
						}
						//reset game request
						else if (temp.equalsIgnoreCase("rr")) {
							int choice = JOptionPane.showConfirmDialog(null, "Your rival want to reset the game?\nAccept???","Message",JOptionPane.YES_NO_OPTION);
							if (choice==JOptionPane.NO_OPTION) {
								this.sendAct("dr");
							}
							else {
								this.sendAct("ar");
								board.turn=0;
								board.END=board.DRAW=false;
								board.selectedCol=board.selectedRow=-1;
								board.mapBoard=new ChessMap();
								board.decryptMap();
								board.coloringCell();
								board.state.setVisible(false);
							}
						}
						//accept reset
						else if (temp.equalsIgnoreCase("ar")) {
							board.wait.setVisible(false);
							board.wait=null;
							
							board.turn=0;
							board.END=board.DRAW=false;
							board.selectedCol=board.selectedRow=-1;
							board.mapBoard=new ChessMap();
							board.decryptMap();
							board.coloringCell();
							board.state.setVisible(false);
						}
						//decline reset
						else if (temp.equalsIgnoreCase("dr")) {
							board.wait.setDefaultCloseOperation(2); //2 is dispose on close
							board.wait.message.setText("Request was rejected");
						}
						else {
							char[] move = temp.toCharArray();
							int [] tmove = {Integer.parseInt(""+move[0]),Integer.parseInt(""+move[1]),Integer.parseInt(""+move[2]),Integer.parseInt(""+move[3])};
							
							if (board.mapBoard.getMap()[tmove[0]][tmove[1]]!=move[4]) {
								board.mapBoard.getMap()[tmove[2]][tmove[3]]=move[4];
								board.mapBoard.getMap()[tmove[0]][tmove[1]]='\0';
								board.turn++;
								for (int i=0;i<4;i++) board.mapBoard.prevMove[i]=tmove[i];
							}
							else{
								board.Move(tmove[0], tmove[1], tmove[2], tmove[3]);
							}
							board.coloringCell();
							board.selectedRow=board.selectedCol=-1;
							board.decryptMap();
							if (board.END==true) board.isOnline=false;
						}
					}
				}
//				sleep(1000);
			} catch (Exception e) {
				int choice = JOptionPane.showConfirmDialog(null, e+"\nDo you want to retry","Error",JOptionPane.YES_NO_OPTION);
				if (choice == JOptionPane.NO_OPTION) {
					return;
				}
			}
		}
	}
}
