package textchess;

import java.util.*;

class Board {

    private static int totalKnightsOnBoard = 0;

    private final int width;
    private final int height;
    int whiteCaptured = 0;
    int blackCaptured = 0;
    private Random rand;

    // the currentPlace is used to describe the knight location on
    // one dimension array which is transferred from the 2-dim board
    private char[][] board;
    private HashMap<Integer, KnightCoordinate> playerKnightsCoordinates;
    private HashMap<Integer, KnightCoordinate> aiKnightsCoordinates;
    private int currentPlaceRow;
    private int currentPlaceColumn;

    private HashMap<Integer, Knight> allKnightsOnBoard;


    // This part is for AI
//	private HashMap<Integer, List<KnightCoordinate>> aiCurrentPositionAllPossibleDestination;
//	List<HashMap<Integer, List<KnightCoordinate>>> aiAllPossible

    Board(int width, int height, Random random) {
        this.width = width;
        this.height = height;
        this.rand = random;

        this.board = new char[this.height][this.width];
        this.playerKnightsCoordinates = new HashMap<>();
        this.aiKnightsCoordinates = new HashMap<>();

        allKnightsOnBoard = new HashMap<>();
    }

    private void updateKilledNumber(char player) {
        // if player is user, which hold 'N', then the captured knights are 'n', update this.blackCaptured
        // and vice versa.
        if (player == 'n') {
            this.whiteCaptured = totalKnightsOnBoard / 2 - this.playerKnightsCoordinates.keySet().size();
        } else {
            this.blackCaptured = totalKnightsOnBoard / 2 - this.aiKnightsCoordinates.keySet().size();
        }
    }

    private void drawGrid() {
//	private void drawGrid(int i, int j) {
        // combine the drawGrid with drawKnight
        // delete the following ' ' after the '|'
        // j represents row, i represents column
//		System.out.print("| ");
        System.out.print("|");
    }

//	private void drawGrid(int i, int j, boolean terminate) {
//		this.drawGrid(i, j);
//		if (terminate) {
//			System.out.print("|");
//		}
//	}

    private void drawHorizontalLine() {
        for (int i = 0; i < this.width; i++) {
            System.out.print("--");
        }
        System.out.println("-");
    }

    private void drawHorizontalAxis() {
        char a = 'A';
        for (int i = 0; i < this.width; i++) {
            char label = (char) (a + i);
            System.out.print(" " + label);
        }
        System.out.println();
    }

    private void drawVerticalTick(int i) {
        System.out.print(" ");
        System.out.print(i + 1);
    }

    private void drawKnight(int knightRow, int knightCol) {
        System.out.print(this.board[knightRow][knightCol]);
    }

    void draw() {
        this.drawHorizontalAxis();
        boolean terminate;
        for (int j = 0; j < this.height; j++) {
            this.drawHorizontalLine();
            for (int i = 0; i < this.width; i++) {
                terminate = i == this.width - 1;
//				this.drawGrid(i, j, i == this.width - 1);
                this.drawGrid();
                this.drawKnight(j, i);
                if (terminate) {
                    this.drawGrid();
                }
//				System.out.print(this.board[j][i]);
                // draw the piece here after the "|"
                // for the last knight, the piece should be printed first,
                // then print the last "|"
            }
            this.drawVerticalTick(j);
            System.out.println();
        }
        this.drawHorizontalLine();
    }

    //======================================================================
    // 1) question
    private void updateKnightPosition(Knight knight, int oneDimensionKnightPosition) {
        knight.setRow(oneDimensionKnightPosition / this.width);
        knight.setColumn(oneDimensionKnightPosition % this.width);
        knight.setCurrentCoordinate();
    }

    private void updateKnightPosition(Knight knight, int newRow, int newColumn) {
        knight.setRow(newRow);
        knight.setColumn(newColumn);
        knight.setCurrentCoordinate();
    }

    void randomPlace(Knight knight) {
        // generate the random place where is valid
        int initialPositionInOneDimensionFormat;
        int currentPlacingKnightRow;
        int currentPlacingKnightColumn;
        do {
            initialPositionInOneDimensionFormat = Math.abs(this.rand.nextInt() % (this.width * this.height));
            currentPlacingKnightRow = initialPositionInOneDimensionFormat / this.width;
            currentPlacingKnightColumn = initialPositionInOneDimensionFormat % this.width;
        } while (placeIsNotValid(currentPlacingKnightRow, currentPlacingKnightColumn));

        this.updateKnightPosition(knight, initialPositionInOneDimensionFormat);
        placeKnightHere(knight);
        totalKnightsOnBoard++;

        this.allKnightsOnBoard.put(knight.getSequenceNumber(), knight);
        updateKnightCoordinateMap(knight);
    }

    private void updateKnightCoordinateMap(Knight knight) {
        if (knight.getColor() == 1) {
            // color==1 means 'N' or player
            this.playerKnightsCoordinates.put(knight.getSequenceNumber(), new KnightCoordinate(this.currentPlaceRow, this.currentPlaceColumn));
        } else {
            // color==0 means 'n' or AI
            this.aiKnightsCoordinates.put(knight.getSequenceNumber(), new KnightCoordinate(this.currentPlaceRow, this.currentPlaceColumn));
        }
    }

    private void placeKnightHere(Knight knight) {
        this.board[knight.getRow()][knight.getColumn()] = knight.getPiece();
    }


    private boolean placeIsNotValid(int row, int column) {
        // if the place is not valid, return true, else return false
        return this.board[row][column] != '\0';
    }

    private static int convertStringColumnToNumberColumnIndex(String column) {
        return column.charAt(0) - 'A';
    }

    // 3) question
    public boolean manualMove(String startLetter, int startRow, String endLetter, int endRow) {

        //The startLetter is string, need to translate it to char
        int startColumn = convertStringColumnToNumberColumnIndex(startLetter);
        int endColumn = convertStringColumnToNumberColumnIndex(endLetter);
        // startRow or endRow starting from 1, but board index starting from 1.
        startRow -= 1;
        endRow -= 1;

        char currentPlayer = 'N';

        int startSeqNo = this.getKnightSequenceNumByPosition(startRow, startColumn);
        int endSeqNo = this.getKnightSequenceNumByPosition(endRow, endColumn);

        if (this.validMove(startRow, startColumn, endRow, endColumn, currentPlayer)) {
            if (endSeqNo != -1 && this.endPositionsIsEnemy(endRow, endColumn, currentPlayer)) {
                this.allKnightsOnBoard.remove(endSeqNo);
                this.blackCaptured++;
            }
            this.board[startRow][startColumn] = '\0';
            this.board[endRow][endColumn] = 'N';
            this.updateKnightPosition(this.allKnightsOnBoard.get(startSeqNo), endRow, endColumn);
            this.checkWinning();
            return true;
        }
        return false;
    }

    private boolean endPositionsIsEnemy(int endRow, int endColumn, char currentPlayerPiece) {
        return currentPlayerPiece == 'N' ? this.board[endRow][endColumn] == 'n' : this.board[endRow][endColumn] == 'N';
    }

    private int getKnightSequenceNumByPosition(int row, int column) {
        for (Knight knight : this.allKnightsOnBoard.values()) {
            if (knight.getRow() == row && knight.getColumn() == column) {
                return knight.getSequenceNumber();
            }
        }
        return -1;
    }

    private boolean validMove(int startRow, int startColumn, int endRow, int endColumn, char currentPlayerPiece) {

        if (!this.isValidPosition(startRow, startColumn)) {
            return false;
        }

        if (!this.isValidPosition(endRow, endColumn)) {
            System.out.println("End position is not inside the board.");
            return false;
        }

        if (this.knightAtThisPosition(startRow, startColumn) != currentPlayerPiece) {
            if (currentPlayerPiece == 'N') {
                System.out.println("The player does not have a piece at " + (char) (startRow + 'A') + (startColumn + 1));
                return false;
            } else if (currentPlayerPiece == 'n') {
                System.out.println("The AI does not have a piece at " + (char) (startRow + 'A') + (startColumn + 1));
                return false;
            }
        }

        if (!this.isValidLShapeMove(startRow, startColumn, endRow, endColumn)) {
            System.out.println("Invalid move.");
            return false;
        }

        if (knightAtThisPosition(endRow, endColumn) == currentPlayerPiece) {
            System.out.println("Invalid move.");
            return false;
        }
        return true;
    }

    private boolean isValidPosition(int row, int column) {
        // column and row are starting from 0 to width-1 and height-1
//		return column < this.width && column >= 0 && row < this.height && row >= 0;
        return row < this.height && row >= 0 && column < this.width && column >= 0;
    }

    private boolean isValidLShapeMove(int startColumn, int startRow, int endColumn, int endRow) {
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

    private char knightAtThisPosition(int row, int column) {
        return this.board[row][column];
    }

    // -------------------------------------------------------------------------------
    // 4) question
    public void aiMove() {

        int currentAiPositionHasMaxEnemyNumber = 0;

        HashMap<Integer, Knight> allAiKnightsMap = this.getAllAiKnights();

        Knight selectedAiKnight = null;
        List<Knight> alternativeSelectedAiKnightsNotInBeneficialArea = new LinkedList<>();
        List<Knight> alternativeSelectedAiKnightsInBeneficialArea = new LinkedList<>();

        KnightCoordinate selectedDestination;

        for (Knight knight : allAiKnightsMap.values()) {

            knight.setAllValidDestinations(getAllValidDestinationsOfCurrentPosition(knight));
            knight.setAllValidDestinationsOccupiedByEnemy(getCurrentPositionAllDestinationsOccupiedByEnemy(knight));

            if (knight.getAllValidDestinationsOccupiedByEnemy().size() == 0) {
                if (this.isNotInBeneficialAttackingArea(knight)) {
                    alternativeSelectedAiKnightsNotInBeneficialArea.add(knight);
                } else {
                    alternativeSelectedAiKnightsInBeneficialArea.add(knight);
                }
            } else {
                if (currentAiPositionHasMaxEnemyNumber < knight.getAllValidDestinationsOccupiedByEnemy().size()) {
                    currentAiPositionHasMaxEnemyNumber = knight.getAllValidDestinationsOccupiedByEnemy().size();
                    selectedAiKnight = knight;
                }
            }
        }

        if (selectedAiKnight == null) {
            if (alternativeSelectedAiKnightsNotInBeneficialArea.size() != 0) {
                selectedAiKnight = this.randomSelectAiKnightFromAlternativeList(alternativeSelectedAiKnightsNotInBeneficialArea);
            } else {
                selectedAiKnight = this.randomSelectAiKnightFromAlternativeList(alternativeSelectedAiKnightsInBeneficialArea);
            }
        }
        selectedDestination = this.getBestDestination(selectedAiKnight);

        this.moveKnight(selectedAiKnight, selectedDestination);
        this.checkWinning();
    }

    private Knight randomSelectAiKnightFromAlternativeList(List<Knight> alternativeKnights) {
        return alternativeKnights.get(new Random().nextInt(alternativeKnights.size()));
    }

    private HashMap<Integer, Knight> getAllAiKnights() {
        HashMap<Integer, Knight> allAiKnight = new HashMap<>();
        for (Knight knight : this.allKnightsOnBoard.values()) {
            if (knight.getPiece() == 'n') {
                allAiKnight.put(knight.getSequenceNumber(), knight);
            }
        }
        return allAiKnight;
    }

    private KnightCoordinate getBestDestination(Knight knight) {

        int destinationSurroundingEnemiesMaxNumber = 0;
        int destinationSurroundingEnemiesNumber;
        KnightCoordinate bestDestination = null;
        List<KnightCoordinate> allGoodOptionsDestinations;

        if (knight.getAllValidDestinationsOccupiedByEnemy().size() > 0) {
            allGoodOptionsDestinations = knight.getAllValidDestinationsOccupiedByEnemy();
        } else {
            allGoodOptionsDestinations = knight.getAllValidDestinations();
        }

        List<KnightCoordinate> allDestinationsWithoutNextStepEnemy = new LinkedList<>();

        for (KnightCoordinate oneGoodDestination : allGoodOptionsDestinations) {
            destinationSurroundingEnemiesNumber = this.getCurrentPositionAllDestinationsOccupiedByEnemy(oneGoodDestination, knight.getEnemy()).size();
            if (destinationSurroundingEnemiesNumber == 0) {
                allDestinationsWithoutNextStepEnemy.add(oneGoodDestination);
            } else if (destinationSurroundingEnemiesNumber > destinationSurroundingEnemiesMaxNumber) {
                destinationSurroundingEnemiesMaxNumber = destinationSurroundingEnemiesNumber;
                bestDestination = oneGoodDestination;
            }
        }

        if (bestDestination == null) {
            for (KnightCoordinate destinationWithoutNextStepEnemy : allDestinationsWithoutNextStepEnemy) {
                if (!this.isNotInBeneficialAttackingArea(destinationWithoutNextStepEnemy)) {
                    bestDestination = destinationWithoutNextStepEnemy;
                }
            }
            if (bestDestination == null) {
                bestDestination = this.randomSelectDestination(allDestinationsWithoutNextStepEnemy);
            }
        }
        return bestDestination;
    }

    private KnightCoordinate randomSelectDestination(List<KnightCoordinate> allValidDestinations) {
        return allValidDestinations.get(new Random().nextInt(allValidDestinations.size()));
    }

    private boolean isNotInBeneficialAttackingArea(Knight knight) {
        return this.isNotInBeneficialAttackingArea(knight.getCurrentCoordinate());
    }

    private boolean isNotInBeneficialAttackingArea(KnightCoordinate kC) {
        //row and col starting from 0 to height and width
        return kC.getKnightRow() - 2 < 0 || kC.getKnightRow() + 2 >= this.height
                || kC.getKnightColumn() - 2 < 0 || kC.getKnightColumn() + 2 >= this.width;
    }

    private void moveKnight(Knight selectedKnight, KnightCoordinate destination) {
        int destinationRow = destination.getKnightRow();
        int destinationColumn = destination.getKnightColumn();

        System.out.print("AI moves from " + (char) (selectedKnight.getColumn() + 'A') + " " + (selectedKnight.getRow() + 1) + " to "
                + (char) (destination.getKnightColumn() + 'A') + " " + (destination.getKnightRow() + 1));

        if (this.board[destinationRow][destinationColumn] == selectedKnight.getEnemy()) {
            if (selectedKnight.getEnemy() == 'N') {
                int killedKnightSequenceNumber = this.getKnightSequenceNumByPosition(destinationRow, destinationColumn);
                System.out.print(" and captured your piece.");
                this.whiteCaptured++;
                this.allKnightsOnBoard.remove(killedKnightSequenceNumber);
            } else {
                this.blackCaptured++;
            }
        }
        System.out.println();

        this.board[selectedKnight.getRow()][selectedKnight.getColumn()] = '\0';
        this.board[destinationRow][destinationColumn] = selectedKnight.getPiece();
        this.updateKnightPosition(selectedKnight, destinationRow, destinationColumn);
    }

    private List<KnightCoordinate> getCurrentPositionAllDestinationsOccupiedByEnemy(Knight knight) {
        return this.getCurrentPositionAllDestinationsOccupiedByEnemy(knight.getCurrentCoordinate(), knight.getEnemy());
    }

    private List<KnightCoordinate> getCurrentPositionAllDestinationsOccupiedByEnemy(KnightCoordinate currentPosition, char enemy) {

        char currentPlayer = enemy == 'N' ? 'n' : 'N';
        List<KnightCoordinate> allValidDestinationsOfCurrentPosition = this.getAllValidDestinationsOfCurrentPosition(currentPosition, currentPlayer);
        int validDestinationRow;
        int validDestinationColumn;
        List<KnightCoordinate> allEnemyPositions = new ArrayList<>();

        for (int i = 0; i < allValidDestinationsOfCurrentPosition.size(); i++) {
            validDestinationRow = allValidDestinationsOfCurrentPosition.get(i).getKnightRow();
            validDestinationColumn = allValidDestinationsOfCurrentPosition.get(i).getKnightColumn();
            if (this.board[validDestinationRow][validDestinationColumn] == enemy) {
                allEnemyPositions.add(new KnightCoordinate(validDestinationRow, validDestinationColumn));
            }
        }
        return allEnemyPositions;
    }

    private List<KnightCoordinate> getAllDestinationsOfCurrentPosition(KnightCoordinate knightCoordinate){
        int currentRow = knightCoordinate.getKnightRow();
        int currentColumn = knightCoordinate.getKnightColumn();
        List<KnightCoordinate> allPossibleDestinations = new ArrayList<>();

        allPossibleDestinations.add(new KnightCoordinate(currentRow - 1, currentColumn - 2));
        allPossibleDestinations.add(new KnightCoordinate(currentRow - 1, currentColumn + 2));
        allPossibleDestinations.add(new KnightCoordinate(currentRow + 1, currentColumn - 2));
        allPossibleDestinations.add(new KnightCoordinate(currentRow + 1, currentColumn + 2));
        allPossibleDestinations.add(new KnightCoordinate(currentRow - 2, currentColumn - 1));
        allPossibleDestinations.add(new KnightCoordinate(currentRow - 2, currentColumn + 1));
        allPossibleDestinations.add(new KnightCoordinate(currentRow + 2, currentColumn - 1));
        allPossibleDestinations.add(new KnightCoordinate(currentRow + 2, currentColumn + 1));

        return allPossibleDestinations;
    }

    private List<KnightCoordinate> getAllDestinationsOfCurrentPosition(Knight knight) {
        return this.getAllDestinationsOfCurrentPosition(knight.getCurrentCoordinate());
    }

    private List<KnightCoordinate> getAllValidDestinationsOfCurrentPosition(Knight knight) {
        return this.getAllValidDestinationsOfCurrentPosition(knight.getCurrentCoordinate(), knight.getPiece());
    }

    private List<KnightCoordinate> getAllValidDestinationsOfCurrentPosition(KnightCoordinate kC, char currentPlayer) {

        List<KnightCoordinate> allPossibleDestinations = this.getAllDestinationsOfCurrentPosition(kC);
        int onePossibleDestinationRow;
        int onePossibleDestinationColumn;

        Iterator<KnightCoordinate> iteratorOfAllValidDestinationsOfCurrentPosition = allPossibleDestinations.iterator();
        while (iteratorOfAllValidDestinationsOfCurrentPosition.hasNext()) {
            KnightCoordinate onePossibleDestination = iteratorOfAllValidDestinationsOfCurrentPosition.next();
            onePossibleDestinationRow = onePossibleDestination.getKnightRow();
            onePossibleDestinationColumn = onePossibleDestination.getKnightColumn();
            if (!this.isValidPosition(onePossibleDestinationRow, onePossibleDestinationColumn)
                    || this.knightAtThisPosition(onePossibleDestinationRow, onePossibleDestinationColumn) == currentPlayer) {
                iteratorOfAllValidDestinationsOfCurrentPosition.remove();
            }
        }
        return allPossibleDestinations;
    }

    // ------------------------------------------------------
    // The following is to create a class of AI Knights' coordinates
    static class KnightCoordinate {
        int row;
        int col;

        KnightCoordinate(int row, int col) {
            this.row = row;
            this.col = col;
        }

        KnightCoordinate() {
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
            System.out.print("" + (char) (this.col + 'A') + (this.row + 1));
        }
    }
    // -------------------------------------------------------------------------------
    // 7) question
    private void checkWinning() {
        if (this.whiteCaptured == totalKnightsOnBoard / 2) {
            System.out.println("Player lost " + this.whiteCaptured
                    + " piece(s). AI lost " + this.blackCaptured + " piece(s).");
            System.out.println("AI wins! ");
            this.draw();
            System.exit(0);
        } else if (this.blackCaptured == totalKnightsOnBoard / 2) {
            System.out.println("Player lost " + this.whiteCaptured
                    + " piece(s). AI lost " + this.blackCaptured + " piece(s).");
            System.out.println("Player wins!");
            this.draw();
            System.exit(0);
        }
    }

}
