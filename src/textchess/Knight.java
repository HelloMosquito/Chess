package textchess;

import java.util.List;

public class Knight {
	private int color;
	private char piece;
	private char enemy;

	private static int totalKnightsNumber = 0;
	private int sequenceNumber;
	private int row;
	private int column;
	private List<Board.KnightCoordinate> allValidDestinations;
	private List<Board.KnightCoordinate> allValidDestinationsOccupiedByEnemy;
	private Board.KnightCoordinate currentCoordinate;


	public Knight(int color) {
		this.color = color;
        this.setPiece();
		this.setEnemy();
		this.sequenceNumber = totalKnightsNumber;
		totalKnightsNumber++;
	}

	public void setCurrentCoordinate(){
		this.currentCoordinate = new Board.KnightCoordinate(this.row, this.column);
	}

	public Board.KnightCoordinate getCurrentCoordinate(){
		return this.currentCoordinate;
	}

	public void setAllValidDestinations(List<Board.KnightCoordinate> allValidDestinations){
		this.allValidDestinations = allValidDestinations;
	}

	public List<Board.KnightCoordinate> getAllValidDestinations(){
		return this.allValidDestinations;
	}

	public List<Board.KnightCoordinate> getAllValidDestinationsOccupiedByEnemy() {
		return allValidDestinationsOccupiedByEnemy;
	}

	public void setAllValidDestinationsOccupiedByEnemy(List<Board.KnightCoordinate> allValidDestinationsOccupiedByEnemy) {
		this.allValidDestinationsOccupiedByEnemy = allValidDestinationsOccupiedByEnemy;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public int getSequenceNumber(){
	    // use this sequence number to identify each Knight and mapping to its coordinate
		return this.sequenceNumber;
	}

	public void setPiece(){
		// Make sure there are only WHITE(1) and BLACK(0)
//	    while(this.color!=0 && this.color!=1){
//			Scanner newColor = new Scanner(System.in);
//			System.out.println("Invalid coloar, WHITE or BLACK: ");
//			this.setColor(newColor.next());
//		}
	    if(this.color == 1){
	    	this.piece = 'N';
		} else {
	    	this.piece = 'n';
		}
	}

	public char getPiece(){
		return this.piece;
	}

	private void setEnemy(){
		this.enemy = this.getPiece()=='N'?'n':'N';
	}

	public char getEnemy(){
		return this.enemy;
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
