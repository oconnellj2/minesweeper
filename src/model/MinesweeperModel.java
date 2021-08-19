package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Observable;
import java.util.Random;

import highscore.HighScoreBoard;
import orderedpair.OrderedPair;

/**
 * File: MinesweeperModel.java 
 * Assignment: Minesweeper
 * Course: CSC 335; Spring 2021
 * 
 * This class represents the model in the MVC Design pattern followed in the design of this iteration 
 * of Minesweeper. 
 * 
 * This class stores all relevant data for the game, including the MinesweeperBoard, the HighScoreBoard,
 * and the number of mines on the board.  There are constructors to build new Minesweeper models depending
 * on whether or not a MinesweeperBoard and/or HighScoreBoard are already in exitence.  Then, there are 
 * public methods to start a new game, return the adjacent squares to a given position, enter a new high 
 * score, get/set the value/visibility of a specific position, get the number of visible squares, the 
 * positions of the mines and DNEs, the difficulty, the shape, the MinesweeperBoard, the HighScoreBoard, 
 * the number of rows/cols on the board, and whether or not the board has been populated with mines.
 * There are also a variety of private helper methods to assist with this and another public method used
 * only for testing.
 * 
 * @author Caroline Hyland
 * @author James O'Connell
 * @author Nicholas Leluan
 * @author Christian Trejo
 */
@SuppressWarnings("deprecation")
public class MinesweeperModel extends Observable {
	
	/**
	 * Board containing int values that correspond with mine placement, proximity locations 
	 * and blank spaces of game board and ints indicating whether a position's contents is
	 * visible to the user, flaged, or non-existant in the given board confirguration
	 */
	private MinesweeperBoard board;
	
	/**
	 * Stored, saved and serialized instance of HighScoreBoard
	 */
	private HighScoreBoard highScoreBoard;

	/**
	 * Total number of mines found on present board or added to a board that has not
	 * been initialized yet
	 */
	private int mineCount;
	
	/** 
	 * Constructor for when there is a High Score saved file, but not Save Game file
	 * in the present working directory
	 * 
	 * For a NEW board and EXISTING HighScoreBoard
	 * 
	 * @param row int value for the number of rows to create on board
	 * @param col int value for the number of columns to put inside each row on the board
	 * @param count int value for the number of mines to place into the board
	 * @param highScoreBoard HighScoreBoard instance of a previously serialized and saved 
	 * HighScoreBoard object
	 * @param difficulty String value for the difficulty of the board
	 * @param shape String value for the shape of the board
	 */
	public MinesweeperModel(int row, int col, int count, HighScoreBoard highScoreBoard, String difficulty, String shape) {
		board = new MinesweeperBoard(row, col, difficulty, shape);
		mineCount = count;
      	this.highScoreBoard = highScoreBoard;
        board.setNumFlags(mineCount);
    }
	
	/**
	 * Constructor for when there is a Saved Game file present but NOT a High Score
	 * saved serialized file in the directory.
	 * 
	 * For an EXISTING board and NEW HighScoreBoard 
	 * 
	 * @param board MinesweeperBoard saved serialized version of a MinesweeperBoard 
	 */
	public MinesweeperModel(MinesweeperBoard board) {
		this.board = board;
		mineCount = board.getMineLocs().size();
        highScoreBoard = new HighScoreBoard();
    }
	
	
	/**
	 * Constructor for when there is a Saved Game file and a High Score saved
	 * file actively in the directory of this program. No need to create a new
	 * instance of these classes.
	 * 
	 * For an EXISTING instance of BOTH MinesweeperBoard and HighScoreBoard
	 *  
	 * @param board MinesweeperBoard saved serialized version of a MinesweeperBoard 
	 * @param highScoreBoard HighScoreBoard instance of a previously serialized and saved 
	 * HighScoreBoard object
	 */
	public MinesweeperModel(MinesweeperBoard board, HighScoreBoard highScoreBoard) {
		this.board = board;
		mineCount = board.getMineLocs().size();
      	this.highScoreBoard = highScoreBoard;
    }

	/**
	 * Purpose: It sets up the mines and visibility array within the board when first 
	 * starting the game.  It relies on the idea that the user's first click cannot be 
	 * touching a mine location.
	 * 
	 * @param userRow int row coordinate of the user's click.
	 * @param userCol int column coordinate of the user's click.
	 */
	public void newGame(int userRow, int userCol) {
		
		this.board.setInit(true);
		
		//Initialize revealBoard to all false
		for(int row = 0; row < board.numRows(); row++) {
			for(int col = 0; col < board.numCols(); col++) {
				if (!board.getDNEs().contains(new OrderedPair(row,col))) {
					board.putVisible(row, col, MinesweeperBoard.INVISIBLE);
				}
			}
		}
		
		setMines(userRow, userCol); 		//Use field value for number of mines
		setMineCounts();
		
	}
	
	
	/**
	 * Purpose: This sets the necessary number of mines in random places on 
	 * the board, ensuring none of them are on a square touching the user's first 
	 * click or on a DNE square.
	 * 
	 * @param userRow int row coordinate of the user's click.
	 * @param userCol int column coordinate of the user's click.	 
	 */
	private void setMines(int userRow, int userCol) {
		Random rand = new Random();
		int randomRow;
		int randomCol;
		int currNumMines = 0;
		
		OrderedPair userPair = new OrderedPair(userRow, userCol);
		
		while (currNumMines < mineCount) {
			randomRow = rand.nextInt(board.numRows());	
			randomCol = rand.nextInt(board.numCols());
			
			OrderedPair op = new OrderedPair(randomRow, randomCol);
			
			if(!op.equals(userPair) && !board.getDNEs().contains(op)) {		//Check if mine (row,col) != user's clicked (row,col) or invalid
				if(!board.getAdjacencies(userPair).contains(op)) {	//Check if mine's coordinates dont touch user's coordinates
					if(board.getVal(randomRow, randomCol) == 0) {		//Check if no mines already there
						board.putVal(randomRow, randomCol, MinesweeperBoard.MINE);
						currNumMines++;
						board.addMine(op);
					}
				}
			}
		}
	}
	
	
	/**
	 * Purpose: This iterates through the value array in the MinesweeperBoard, and, 
	 * for every square that is not a mine or a DNE, it calculates how many mines the
	 * square is touching and saves that as its value.
	 */
	private void setMineCounts() {
		
		for (int row = 0; row < board.numRows(); row++) {
			for (int col = 0; col < board.numCols(); col++) {
				if (board.getVal(row, col) == MinesweeperBoard.MINE) {
					continue; //pass over mines
				}
				if (board.getVisible(row, col) == MinesweeperBoard.DNE) {
					continue; //pass over squares not in use
				}
				int adjMines = 0;
				for (OrderedPair neighbor: board.getAdjacencies(new OrderedPair(row,col))) {
					if (board.getVal(neighbor.getRow(), neighbor.getCol()) == MinesweeperBoard.MINE) {
						adjMines++;
					}
				}
				board.putVal(row, col, adjMines);
			}
		}
		
	}
	
	/**
	 * Purpose: This method is used only for testing purposes. It takes in an array of 
	 * OrderedPair locations expressing where the mines should be placed on the board.  Then,
	 * it places mines in those locations and changes the status of the board to reflect that
	 * mines have been placed.  This allows for testing of various operations on a predetermined
	 * and predicatable board.
	 * 
	 * @param mines OrderedPair[] containing all the intended mine locations for the board
	 */
	public void buildTestBoard(OrderedPair[] mines) {
		this.board.setInit(true);
		//Initialize revealBoard to all false
		for(int row = 0; row < board.numRows(); row++) {
			for(int col = 0; col < board.numCols(); col++) {
				board.putVisible(row, col, MinesweeperBoard.INVISIBLE);
			}
		}
		
		//adds predetermined mines
		for (int idx = 0; idx < mines.length; idx++) {
			board.putVal(mines[idx].getRow(), mines[idx].getCol(), MinesweeperBoard.MINE);
			board.addMine(mines[idx]);

		}
		
		setMineCounts();
	}
	
	/**
	 * Purpose: This is a getter which returns an ArrayList of OrderedPairs containing all
	 * active board squares around the specific location.
	 * 
	 * @param row int value of which row to get inside board
	 * @param col int value of which column to get inside passed in row value
	 * @return ArrayList of OrderedPair objects that represent the adjacency list for
	 * the passed in row/col coordinate
	 */
	public ArrayList<OrderedPair> getAdjacencies(int row, int col) {
		return board.getAdjacencies(new OrderedPair(row, col));
	}

	/**
	 * Purpose: This is a getter that returns the value at a specific position in the board.
	 * This value is the number of mines that border the specific square.
	 * 
	 * @param row int value to index into the rows of Board
	 * @param col int value to index into the columns of the row of the Board 
	 * @return int value that is found at the board[row][col] location
	 */
	public int getVal(int row, int col) {
		return board.getVal(row, col);
		
	}
	
	/**
	 * Purpose: This is a setter for the value of a specific position in the board.
	 * The int value passed in will be in the range [-1,8] inclusive. If the passed in 
	 * value is -1; this indicates a mine on the board. If the passed in value is a 0, 
	 * this indicates a space on the board. If the passed in value is between 1 and 8, 
	 * this represents proximity values that correspond to the number of mines that can
	 * be found boarding this (row, col) location
	 * 
	 * @param row int value to index into the rows of MinesweeperBoard
	 * @param col int value to index into the columns of the row of the MinesweeperBoard 
	 * @param val int value to be placed on the board at this location 
	 */
	public void putVal(int row, int col, int val) {
		board.putVal(row, col, val);
	}

	
	/**
	 * Purpose: This is a getter for the visibility of a specific position on the board.
	 * An int of 0 indicates invisible, 1 is visible, -2 is flag, and -3 is DNE.
	 * 
	 * @param row int value of which row to get inside the present board
	 * @param col int value of which column to get inside the passed in row 
	 * @return int visibility for the specified position
	 */
	public int getVisibility(int row, int col) {
		return board.getVisible(row, col);
	}
	
	/**
	 * Purpose: This is a getter fro the number of visible squares on the board. 
	 * 
	 * @return int value for the total number of squares that have been made visible to 
	 * the user 
	 */
	public int numVisible() {
		return board.numVisible();
	}
	
	/**
	 * Purpose: This is a setter that will change the visibility of a specific position on 
	 * the board. Invisible is indicated with 0, visible with 1, a flag with -2, and DNE with
	 * -3.
	 * 
	 * @param row int value for the row to get inside the visibilty board
	 * @param col int value for the column to change inside the given row 
	 * @param value int representing the new visibility status of the specified location
	 */
	public void putVisibility(int row, int col, int value) {
		board.putVisible(row, col, value);
        // Mark Observable and notify Observer.
		setChanged();
        notifyObservers(board);
	}
	
	/**
	 * Purpose: This is a getter for a HashSet containing all of the mine locations on the board.
	 * 
	 * @return HashSet of OrderdPair objects that represent all the mine locations in the 
	 * present game board 
	 */
	public HashSet<OrderedPair> getMineLocs() {
		return board.getMineLocs();
	}
	
	/**
	 * Purpose: This is a getter for a HashSet containing all of the DNE locations on the board.
	 * 
	 * @return HashSet of OrderdPair objects that represent all the DNE locations in the 
	 * present game board 
	 */
	public HashSet<OrderedPair> getDNEs() {
		return board.getDNEs();
	}
	
	/**
	 * Purpose: This is a getter for String representing the difficulty of the current board.
	 * 
	 * @return String expressing the board difficulty level
	 */
	public String getDifficulty() {
		return board.getDifficulty();
	}
	
	/**
	 * Purpose: This is a getter for String representing the shape of the current board.
	 * 
	 * @return String expressing the board shape
	 */
	public String getShape() {
		return board.getShape();
	}
	
	/**
	 * Purpose: This is a getter for the MinesweeperBoard contained within this model.
	 * 
	 * @return MinesweeperBoard representing the current game board
	 */
    public MinesweeperBoard getBoard() {
        return board;
    }
    
    /**
     * Purpose: This method enters in a new score into the HighScoreBoard class.  It is 
     * called when the user has successfully completed a game and has been prompted to 
     * enter their name. It saves their score and adds it to the existing HighScoreBoard object.
     * 
     * @param name String value of the user's inputed name from text prompt in View
     * @param score int value of the score achieved for this user's attempt 
     * @param mode String value representing the difficulty and shape the user completed
     */
    public void enterNewScore(String name, int score, String mode) {
    	highScoreBoard.addNewScore(name, score, mode);
    }
    
    /**
	 * Purpose: This is a getter for the HighScoreBoard contained within this model.
	 * 
	 * @return HighScoreBoard representing the current high score board
	 */
    public HighScoreBoard getHighScoreBoard() {
    	return highScoreBoard;
    }
    
    /**
	 * Purpose: This is a getter for the number of rows in the MinesweeperBoard.
	 * 
	 * @return int number of rows in the MinesweeperBoard
	 */
    public int numRows() {
    	return board.numRows();
    }
    
    /**
	 * Purpose: This is a getter for the number of columns in the MinesweeperBoard.
	 * 
	 * @return int number of columns in the MinesweeperBoard
	 */
    public int numCols() {
    	return board.numCols();
    }
    
    /**
     * Purpose: This is a getter for the state of the board, which returns a boolean expressing 
     * if the instance variable board has been initialized or not.  An initialized board is a 
     * board in which the mines and proximity values have already been set (meaning the first 
     * turn has been played)
     * 
     * @return boolean true if the board has been initialized and false if not
     */
    public boolean getInit() {
    	return board.getInit();
    }
    
}
