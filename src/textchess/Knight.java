package textchess;

import java.util.Scanner;

public class Knight {
	private int color;
	private char piece;
	private static int sequenceNumber=0;

	public Knight(int color) {
		this.color = color;
		this.piece = getPiece();
		sequenceNumber++;
	}

	public int getSequenceNumber(){
	    // use this sequence number to identify each Knight and mapping to its coordinate
		return sequenceNumber;
	}

	public char getPiece(){
		// Make sure there are only WHITE(1) and BLACK(0)
//	    while(this.color!=0 && this.color!=1){
//			Scanner newColor = new Scanner(System.in);
//			System.out.println("Invalid coloar, WHITE or BLACK: ");
//			this.setColor(newColor.next());
//		}
	    if(this.color == 1){
	    	return 'N';
		} else {
	    	return 'n';
		}
	}

	private void setColor(String color){
		if(color.equals("WHITE")){
			this.color = 1;
		} else if (color.equals("BLACK")){
			this.color= 0;
		}
	}

	public int getColor(){
		return this.color;
	}
}
