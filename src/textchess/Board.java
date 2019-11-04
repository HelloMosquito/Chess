package textchess;

import java.util.*;

public class Board {

	private static int totalKnightsOnBoard = 0;

	private final int width;
	private final int height;
	int whiteCaptured = 0;
	int blackCaptured = 0;
	Random rand;

	// the currentPlace is used to describe the knight location on
	// one dimension array which is transferred from the 2-dim board
	private int currentPlace;
	private char[][] board;
	private HashMap<Integer, KnightCoordinate> playerKnightsCoordinates;
	private HashMap<Integer, KnightCoordinate> aiKnightsCoordinates;
	private int row;
	private int col;

	// This part is for AI
	HashMap<Integer, List<KnightCoordinate>> aiCurrentPositionAllPossibleDestination;
//	List<HashMap<Integer, List<KnightCoordinate>>> aiAllPossible

	public Board(int width, int height, Random random) {
		this.width = width;
		this.height = height;
		this.rand = random;

		this.board = new char[this.height][this.width];
		this.playerKnightsCoordinates = new HashMap<>();
		this.aiKnightsCoordinates = new HashMap<>();

		this.aiCurrentPositionAllPossibleDestination = new HashMap<>();
	}

	private void updateKilledNumber(char player){
	    // if player is user, which hold 'N', then the captured knights are 'n', update this.blackCaptured
		// and vice versa.
		if(player=='n'){
			this.whiteCaptured = totalKnightsOnBoard/2 - this.playerKnightsCoordinates.keySet().size();
		} else {
			this.blackCaptured = totalKnightsOnBoard/2 - this.aiKnightsCoordinates.keySet().size();
		}
	}

	void drawGrid(int i, int j) {
		// combine the drawGrid with drawKnight
		// delete the following ' ' after the '|'
		// j represents row, i represents column

//		System.out.print("| ");
		System.out.print("|");
		System.out.print(this.board[j][i]);
	}

	void drawGrid(int i, int j, boolean terminate) {
		this.drawGrid(i, j);
		if (terminate) {
			System.out.print("|");
		}
	}

	void drawHorizontalLine() {
		for (int i = 0; i < this.width; i++) {
			System.out.print("--");
		}
		System.out.println("-");
	}

	void drawHorizontalAxis() {
		char a = 'A';
		for (int i = 0; i < this.width; i++) {
			char label = (char) (a + i);
			System.out.print(" " + label);
		}
		System.out.println("");
	}

	void drawVerticalTick(int i) {
		System.out.print(" ");
		System.out.print(i + 1);
	}

	public void draw() {
		this.drawHorizontalAxis();
		for (int j = 0; j < this.height; j++) {
			this.drawHorizontalLine();
			for (int i = 0; i < this.width; i++) {
				this.drawGrid(i, j, i == this.width - 1);
				// draw the piece here after the "|"
				// for the last knight, the piece should be printed first,
				// then print the last "|"
			}
			this.drawVerticalTick(j);
			System.out.println("");
		}
		this.drawHorizontalLine();
	}

	//======================================================================
	// 1) question
	private void getBoardIdx() {
		this.row = this.currentPlace / this.width;
		this.col = this.currentPlace % this.width;
	}

	public void randomPlace(Knight knight) {
		// generate the random place where is valid
		do {
			this.currentPlace = Math.abs(this.rand.nextInt() % (this.width * this.height));
		} while (placeIsNotValid());
		putKnight(knight);
		totalKnightsOnBoard++;

		updateKnightCoordinateMap(knight);
	}

//	// start *************************************************************
//	public void printKnightMap() {
//		System.out.println("--------> player");
//		for (Integer key : this.playerKnightsCoordinates.keySet()) {
//			System.out.print("key: " + key + " --> value: ");
//			this.playerKnightsCoordinates.get(key).printCoordinate();
//			System.out.println();
//		}
//
//		System.out.println("--------> AI");
//		for (Integer key : this.aiKnightsCoordinates.keySet()) {
//			System.out.print("key: " + key + "--> value: ");
//			this.aiKnightsCoordinates.get(key).printCoordinate();
//			System.out.println();
//		}
//	}
//	// end *************************************************************

	private void updateKnightCoordinateMap(Knight knight) {
		if (knight.getColor() == 1) {
			// color==1 means 'N' or player
			this.playerKnightsCoordinates.put(knight.getSequenceNumber(), new KnightCoordinate(this.row, this.col));
		} else {
			// color==0 means 'n' or AI
			this.aiKnightsCoordinates.put(knight.getSequenceNumber(), new KnightCoordinate(this.row, this.col));
		}
	}

	private void updateKnightCoordinateMap(Knight knight, KnightCoordinate knightCoordinate) {
		if (knight.getColor() == 1) {
			// color==1 means 'N' or player
			this.playerKnightsCoordinates.put(knight.getSequenceNumber(), knightCoordinate);
		} else {
			// color==0 means 'n' or AI
			this.aiKnightsCoordinates.put(knight.getSequenceNumber(), knightCoordinate);
		}
	}

	private void updateKnightCoordinateMap(int knightSequenceNo, KnightCoordinate knightCoordinate) {
		if (knightSequenceNo <= totalKnightsOnBoard / 2) {
			this.playerKnightsCoordinates.put(knightSequenceNo, knightCoordinate);
		} else {
			this.aiKnightsCoordinates.put(knightSequenceNo, knightCoordinate);
		}
	}

	private void deleteKnightCoordinateMap(int knightSequenceNo) {
		if (knightSequenceNo <= totalKnightsOnBoard / 2) {
			this.playerKnightsCoordinates.remove(knightSequenceNo);
		} else {
			this.aiKnightsCoordinates.remove(knightSequenceNo);
		}
	}

	private void deleteKnightCoordinateMap(int row, int col){
		int sequenceNum = this.getKnightSequenceNoByCoordinate(row, col);
		this. deleteKnightCoordinateMap(sequenceNum);
//		if(sequenceNum <= totalKnightsOnBoard / 2){
//			this.playerKnightsCoordinates.remove(sequenceNum);
//		} else {
//			this.aiKnightsCoordinates.remove(sequenceNum);
//		}
	}

	private void drawHorizontalKnights(int row, int col) {
		// draw the knights pieces, if no piece here, draw " "
		System.out.print(this.board[row][col]);
	}

	// start *************************************************
	private void flushBoard() {
		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				System.out.print(this.board[i][j] + " ");
			}
			System.out.println();
		}
	}
	// end *****************************************************

	private void putKnight(Knight knight) {
		this.board[this.row][this.col] = knight.getPiece();

	}


	private boolean placeIsNotValid() {
		// if the place is not valid, return true, else return false
		getBoardIdx();
		return this.board[this.row][this.col] != '\0';
	}

	private static int parsePlaceCoordinate(String column) {
		return column.charAt(0) - 'A';
	}

	// 3) question

	public boolean manualMove(String startLetter, int startNumber, String endLetter, int endNumber) {

//		// start ********************************
//		this.printKnightMap();
//		System.out.println("===============================");
//
//		// end ********************************

		//The startLetter is string, need to translate it to char
		int startLetterColumn = parsePlaceCoordinate(startLetter);
		int endLetterColumn = parsePlaceCoordinate(endLetter);
		// startNumber or endNumber starting from 1, but board index starting from 1.
		startNumber--;
		endNumber--;

		int startSeqNo = this.getKnightSequenceNoByCoordinate(startNumber, startLetterColumn);
		int endSeqNo = this.getKnightSequenceNoByCoordinate(endNumber, endLetterColumn);

		if (this.validMove(startLetterColumn, startNumber, endLetterColumn, endNumber)) {

			if (this.endPositionsIsEnemy(endNumber, endLetterColumn, 'N')) {
//				System.out.println("AI has: " + this.aiKnightsCoordinates.size() + " Knights");
				this.deleteKnightCoordinateMap(endSeqNo);
//				System.out.println("Ai key: " + endSeqNo);
				this.updateKilledNumber('N');
//				System.out.println("Currently, AI has: " + this.aiKnightsCoordinates.size() + " Knights");

//				this.deleteKnightCoordinateMap(endNumber, endLetterColumn);
			}
			this.board[startNumber][startLetterColumn] = '\0';
			this.board[endNumber][endLetterColumn] = 'N';
			this.updateKnightCoordinateMap(startSeqNo, new KnightCoordinate(endNumber, endLetterColumn));

			this.checkWinning();
			return true;
		}
		return false;
	}

	private boolean endPositionsIsEnemy(int endRow, int endColumn, char playerOrAi) {
		return playerOrAi == 'N' ? this.board[endRow][endColumn] == 'n' : this.board[endRow][endColumn] == 'N';
	}


	private int getKnightSequenceNoByCoordinate(int row, int col) {
		for (HashMap.Entry<Integer, KnightCoordinate> entry : this.playerKnightsCoordinates.entrySet()) {
			if (entry.getValue().getKnightRow() == row && entry.getValue().getKnightColumn() == col) {
				return entry.getKey();
			}
		}
		for (HashMap.Entry<Integer, KnightCoordinate> entry : this.aiKnightsCoordinates.entrySet()) {
			if (entry.getValue().getKnightRow() == row && entry.getValue().getKnightColumn() == col) {
				return entry.getKey();
			}
		}
		return -1;
	}

	private boolean validMove(int startLetter, int startNumber, int endLetter, int endNumber) {

		if (!this.validPlace(startLetter, startNumber)) {
			return false;
		}

		if (!this.validPlace(endLetter, endNumber)) {
			System.out.println("End position is not inside the board.");
			return false;
		}

		if (!(this.ownKnightAtThisPosition(startLetter, startNumber) == 'N')) {
			System.out.println("The player does not have a piece at " + (char) (startLetter + 'A') + (startNumber + 1));
			return false;
		}

		if (!this.validLShapeMove(startLetter, startNumber, endLetter, endNumber)) {
			System.out.println("Invalid move.");
			return false;
		}

		if (ownKnightAtThisPosition(endLetter, endNumber) == 'N') {
			System.out.println("Invalid move.");
			return false;
		}

		return true;
	}

	private boolean validPlace(int column, int row) {
		// column and row are starting from 0 to width-1 and height-1
		return column < this.width && column >= 0 && row < this.height && row >= 0;
	}

	private boolean validLShapeMove(int startColumn, int startRow, int endColumn, int endRow) {
    /*   horizontal L type like the following:
		 * * * * *       * * * * *     * * * * K       K * * * *      * * * * *
		 * * K * *   to  * * X * *  or * * X * *   or  * * X * *  or  * * X * *
		 * * * * *       * * * * K     * * * * *       * * * * *      K * * * *
        -------------------------------------------------------------------------------
		 vertical L type like the following:
		 * * * * *       * K * * *     * * * K *       * * * * *      * * * * *
		 * * * * *       * * * * *     * * * * *       * * * * *      * * * * *
		 * * K * *   to  * * X * *  or * * X * *   or  * * X * *  or  * * X * *
		 * * * * *       * * * * *     * * * * *       * * * * *      * * * * *
		 * * * * *       * * * * *     * * * * *       * K * * *      * * * K *
		-------------------------------------------------------------------------------

		 The reason why not check place validation first is:
		 the validLShapeMove() is executed after validPlace() in validMove()
		 the places have been checked, there is no need to check again.

		 The 1st item in return before || is horizontal L-type
		 The 2nd item after || is vertical L-type
	*/
		return (Math.abs(endColumn - startColumn) == 2 && Math.abs(endRow - startRow) == 1)
				|| (Math.abs(endColumn - startColumn) == 1 && Math.abs(endRow - startRow) == 2);
	}

	private char ownKnightAtThisPosition(int column, int row) {
		return this.board[row][column];
	}

	// -------------------------------------------------------------------------------
	// 4) question
	public void aiMove() {

		int currentPositionMaxEnemy = 0;

		int selectedKnightSequenceNum = this.aiKnightsCoordinates.keySet().iterator().next();
		List<KnightCoordinate> selectedKnightAllDestination = this.getAiCurrentPositionAllPossibleDestination(this.aiKnightsCoordinates.get(selectedKnightSequenceNum));
		KnightCoordinate destination = this.getBestDestination(selectedKnightSequenceNum, selectedKnightAllDestination);

//		int currentSequenceNo = totalKnightsOnBoard / 2;
//		KnightCoordinate currentDestinationPosition = this.aiKnightsCoordinates.get(currentSequenceNo);
//		int tempCurrentNumber;

		List<KnightCoordinate> tempAllDestination;
		List<KnightCoordinate> tempAllDestinationOccupiedByEnemy;


		for (HashMap.Entry<Integer, KnightCoordinate> entry: this.aiKnightsCoordinates.entrySet()) {

			tempAllDestination = getAiCurrentPositionAllPossibleDestination(entry.getValue());
//			System.out.println("-------------------------------");
//			this.printAllDestination(entry.getKey(), tempAllDestination);
//			System.out.println("-------------------------------");
//
			tempAllDestinationOccupiedByEnemy = getCurrentPositionAllEnemyDestination(entry.getKey(), tempAllDestination);
//			System.out.println("-------------------------------");
//			this.printAllDestination(entry.getKey(), tempAllDestinationOccupiedByEnemy);
//			System.out.println("-------------------------------");
//

			if (tempAllDestinationOccupiedByEnemy.size() == 0 && tempAllDestinationOccupiedByEnemy.size()>= currentPositionMaxEnemy) {
				if (this.isNotInBenificialAttackingArea(this.aiKnightsCoordinates.get(entry.getKey()))) {
					selectedKnightSequenceNum = entry.getKey();
					destination = this.getBestDestination(entry.getKey(), tempAllDestination);
				} else {

				}
			} else {
				if (tempAllDestinationOccupiedByEnemy.size() > currentPositionMaxEnemy) {
					currentPositionMaxEnemy = tempAllDestinationOccupiedByEnemy.size();
					selectedKnightSequenceNum = entry.getKey();
					destination = this.getBestDestination(entry.getKey(), tempAllDestinationOccupiedByEnemy);
				}
			}
		}
		this.moveKnight(selectedKnightSequenceNum, destination);
		this.updateKilledNumber('n');
		this.checkWinning();
	}

//	// start *************************************************************************************
//	private void printAllDestination(int seqNum, List<KnightCoordinate> kC){
//		System.out.println("All destination are shown following: ");
//		int i=1;
//		for(KnightCoordinate kc: kC){
//			System.out.print("Knight seq num: " + seqNum + " => ");
//			System.out.println("The " + (i++) + " item: " + (char)(kc.getKnightColumn()+'A') + (kc.getKnightRow()+1));
//		}
//	}
//	// end ***************************************************************************************

	private KnightCoordinate getBestDestination(int knightSequenceNum, List<KnightCoordinate> kC){
		char enemy = this.checkEnemy(knightSequenceNum);
		KnightCoordinate destination = kC.get(0);
		for(KnightCoordinate kc: kC){
			if(!this.isNotInBenificialAttackingArea(kc)){
				destination = kc;
			}

			if(this.board[kc.getKnightRow()][kc.getKnightColumn()] == enemy){
				destination = kc;
			}
		}
		return destination;
	}

//	private KnightCoordinate selectDestination(int knightSequenceNum, HashMap<Integer, List<KnightCoordinate>> allDestination){
//
//		int selectedKnightSequencenum = totalKnightsOnBoard/2;
//		int numOfEnemy = 0;
//		KnightCoordinate destination =
//
//		for(HashMap.Entry<Integer, List<KnightCoordinate>> entry: allDestination.entrySet()){
//			if(this.isInBenificialAttackingArea(this.aiKnightsCoordinates.get(entry.getKey()))){
//				selectedKnightSequencenum = entry.getKey();
//			}
//			if(entry.getValue().size() > numOfEnemy){
//				numOfEnemy = entry.getValue().size();
//				selectedKnightSequencenum = entry.getKey();
//			}
//		}
//
//		if(numOfEnemy == 0){
//
//		}
//
//
//	}

	private boolean isNotInBenificialAttackingArea(KnightCoordinate kC){
		if(kC.getKnightRow()-2>=0 && kC.getKnightRow()+2<this.height
				&& kC.getKnightColumn()-2>=0 && kC.getKnightColumn()+2<this.width){
		    //row and col starting from 0 to height and width
			return false;
		}
		return true;
	}

	private char checkEnemy(int knightSequenceNo){
		return knightSequenceNo <= totalKnightsOnBoard/2 ? 'n':'N';
	}

	private void moveKnight(int knightSequenceNumber, KnightCoordinate end){

		int r;
		int c;
		char enemy;
		char player;

		if(knightSequenceNumber <= totalKnightsOnBoard/2){
			r = this.playerKnightsCoordinates.get(knightSequenceNumber).getKnightRow();
			c = this.playerKnightsCoordinates.get(knightSequenceNumber).getKnightColumn();
			player = 'N';
			enemy = 'n';
		} else {
			r = this.aiKnightsCoordinates.get(knightSequenceNumber).getKnightRow();
			c = this.aiKnightsCoordinates.get(knightSequenceNumber).getKnightColumn();
			player = 'n';
			enemy = 'N';
		}

		System.out.print("AI moves from " + (char)(c+'A') + " " + (r+1) + " to "
				+ (char)(end.getKnightColumn()+'A') + " " + (end.getKnightRow()+1));

		if(this.board[end.getKnightRow()][end.getKnightColumn()] == enemy){
			System.out.println(" and captured your piece.");
			this.deleteKnightCoordinateMap(this.getKnightSequenceNoByCoordinate(end.getKnightRow(), end.getKnightColumn()));
		} else {
			System.out.println();
		}

		this.board[r][c] = '\0';
		this.board[end.getKnightRow()][end.getKnightColumn()] = player;

		this.updateKnightCoordinateMap(knightSequenceNumber, end);
	}

	private List<KnightCoordinate> getCurrentPositionAllEnemyDestination(int knightSequenceNum, KnightCoordinate kC){
		/*
			kC is the current position of knight which need to move.
			First, all possible destination need to be determined.
		 */
		List<KnightCoordinate> allPossibleDestination = getAiCurrentPositionAllPossibleDestination(kC);
		return this.getCurrentPositionAllEnemyDestination(knightSequenceNum, allPossibleDestination);
	}

	private  List<KnightCoordinate> getCurrentPositionAllEnemyDestination(int knightSequenceNum, List<KnightCoordinate> allPossibleDestination){
		/*
			In this method, all possible destination has been determined.
			So it's only necessary to check whether there is any enemy in all the possible destination.
		 */
		char enemy = this.checkEnemy(knightSequenceNum);
		int r;
		int c;

		List<KnightCoordinate> allEnemyPosition = new ArrayList<>();
		for(int i=0; i<allPossibleDestination.size(); i++){
			r = allPossibleDestination.get(i).getKnightRow();
			c = allPossibleDestination.get(i).getKnightColumn();
			if(this.board[r][c] == enemy){
				allEnemyPosition.add(new KnightCoordinate(r, c));
			}
		}
		return allEnemyPosition;
	}

	private List<KnightCoordinate> getAiCurrentPositionAllPossibleDestination(KnightCoordinate kC){
		int currentRow = kC.getKnightRow();
		int currentCol = kC.getKnightColumn();

		List<KnightCoordinate> allPossibleDestination = new ArrayList<>();

		allPossibleDestination.add(new KnightCoordinate(currentRow-1, currentCol-2));
		allPossibleDestination.add(new KnightCoordinate(currentRow-1, currentCol+2));
		allPossibleDestination.add(new KnightCoordinate(currentRow+1, currentCol-2));
		allPossibleDestination.add(new KnightCoordinate(currentRow+1, currentCol+2));
		allPossibleDestination.add(new KnightCoordinate(currentRow-2, currentCol-1));
		allPossibleDestination.add(new KnightCoordinate(currentRow-2, currentCol+1));
		allPossibleDestination.add(new KnightCoordinate(currentRow+2, currentCol-1));
		allPossibleDestination.add(new KnightCoordinate(currentRow+2, currentCol+1));

		int r=0;
		int c=0;

		Iterator<KnightCoordinate> iter = allPossibleDestination.iterator();

		while(iter.hasNext()){
			KnightCoordinate item = iter.next();
			r = item.getKnightRow();
			c = item.getKnightColumn();
			if (!this.validPlace(c, r) || this.ownKnightAtThisPosition(c, r)=='n') {
//			    allPossibleDestination.remove(item);
                iter.remove();
            }
		}

//		for(int i=0; i<allPossibleDestination.size(); i++){
//			r = allPossibleDestination.get(i).getKnightRow();
//			c = allPossibleDestination.get(i).getKnightColumn();
//			if (!this.validPlace(c, r) || this.ownKnightAtThisPosition(c, r)=='n') {
//				allPossibleDestination.remove(i);
//			}
//		}

		return allPossibleDestination;
	}

//	private int checkAllDestinationListEnemyTotalNumber(char currentPlayer, List<KnightCoordinate> currentPositionAllDestination){
//		int totalNumber = 0;
//		char enemy;
//
//		if(currentPlayer == 'N'){
//			enemy = 'n';
//		} else if(currentPlayer == 'n'){
//			enemy = 'N';
//		} else {
//			// if invalid input, re-input
//			return -1;
//		}
//
//		int r;
//		int c;
//
//		for(int i=0; i<currentPositionAllDestination.size(); i++){
//			r = currentPositionAllDestination.get(i).getKnightRow();
//			c = currentPositionAllDestination.get(i).getKnightColumn();
//			if(this.board[r][c] == enemy){
//				totalNumber++;
//			}
//		}
//		return totalNumber;
//	}




	// ------------------------------------------------------
	// The following is to create a class of AI Knights' coordinates
	private class KnightCoordinate {
		int row;
		int col;

		KnightCoordinate(int row, int col) {
			this.row = row;
			this.col = col;
		}

		KnightCoordinate(){
			this(0, 0);
		}

		public int[] getKnightCoordinate() {
			return new int[]{this.row, this.col};
		}

		public int getKnightRow() {
			return this.row;
		}

		public int getKnightColumn() {
			return this.col;
		}

		public void printCoordinate() {
			System.out.print("" + (char)(this.col+'A') + (this.row+1));
		}

	}


	// -------------------------------------------------------------------------------
	// 7) question

	private void checkWinning(){
		if(this.whiteCaptured == totalKnightsOnBoard / 2){
			System.out.println("Player lost " + this.whiteCaptured
					+ " piece(s). AI lost " + this.blackCaptured + " piece(s).");
			System.out.println("AI wins! ");
			this.draw();
			System.exit(0);
		} else if(this.blackCaptured == totalKnightsOnBoard / 2){
			System.out.println("Player lost " + this.whiteCaptured
					+ " piece(s). AI lost " + this.blackCaptured + " piece(s).");
			System.out.println("Player wins!");
			this.draw();
			System.exit(0);
		}
	}

}
