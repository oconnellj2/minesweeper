package controller;

import java.util.ArrayList;
import java.util.HashSet;

import exceptions.HitMineException;
import exceptions.IllegalMoveException;
import highscore.HighScoreBoard;
import model.MinesweeperBoard;
import model.MinesweeperModel;
import orderedpair.OrderedPair;
import view.MinesweeperView;

/**
 * File: MinsweeperController.java
 * Assignment: Minesweeper
 * Course: CSC 335; Spring 2021
 * 
 * This class encompasses the Controller in the MVC design pattern followed in the creation of Minesweeper.
 * 
 * This class acts like a liaison between MinsweeperView and other classes of this game and provides the majority of the 
 * game logic to signal to the View the current state and status of the active game and to process changes. A key feature 
 * of this class is communicating with MinesweeperModel which hold data including the MinesweeperBoard.  It holds a 
 * reference to the model in use, along with having a variety of constructors for different circumstances and a public 
 * method to update the model.  There are public methods to handle changes regarding the observable-observer relationship 
 * between the view and the model, and a getter for the MinesweeperBoard, the difficulty of the board, the shape fo the board, 
 * the status of the board (initialized or not), the number of mines on the board, the number of flags on the board, the
 * score associated with the board, the shape of the board, the value at a specifici position on the board, the visibility 
 * at a specific location on the board, the number of rows on the board, and the number of columns on the board.  There is a 
 * setter for the score, a method to add a new score, and then the rest of the game logic.  These methods include methods to 
 * add and remove flags, check if the game is won, and handle a user turn (where they click on a square).
 * 
 * @author Caroline Hyland
 * @author James O'Connell
 * @author Nicholas Leluan
 * @author Christian Trejo
 */
@SuppressWarnings("deprecation")
public class MinesweeperController {

	private MinesweeperModel model;

	/**
	 * Constructor used when a saved serialized HighScoreBoard is present in directory
	 * but a saved serialized MinesweeperBoard is NOT present 
	 * 4 argument constructor
	 * 
	 * @param row int value for the number of rows in the MinesweeperBoard
	 * @param col int value for the number of cols in the MinesweeperBoard
	 * @param count int value for the number of mines to place in the MinesweeperBoard
	 * @param highScoreBoard HighScoreBoard object representing a previously saved HighScoreBoard
	 * @param difficulty String value representing the difficulty level the user is attempting
	 * @param shape String value representing the shape the user is attempting
	 */
	public MinesweeperController(int row, int col, int count, HighScoreBoard highScoreBoard, String difficulty, String shape) {
		model = new MinesweeperModel(row, col, count, highScoreBoard, difficulty, shape);

	}
	
	
	/**
	 * Constructor to be used when a saved and serialized MinesweeperBoard and HighScoreBoard
	 * can be found; this is used when a saved game is loaded up and continued.
	 * Two argument constructor 
	 * 
	 * @param board MinesweeperBoard object that represents the current state of a saved board
	 * @param highScoreBoard HighScoreBoard object that represents a saved high score board 
	 */
	public MinesweeperController(MinesweeperBoard board, HighScoreBoard highScoreBoard) {
		model = new MinesweeperModel(board, highScoreBoard);
	}
	
	
	/**
	 * Constructor used in the event that a saved and serialized MinesweeperBoard is present
	 * but a saved and serialized HighScoreBoard is NOT present.  It is likely to needed when 
	 * the user saves an incomplete game, but the user has yet to win any games and the user 
	 * wants to reload the previously incomplete game.
	 * One argument constructor
	 *  
	 * @param board MinesweeperBoard object representing the current state of a saved and serialized board
	 */
	public MinesweeperController(MinesweeperBoard board) {
		model = new MinesweeperModel(board);
	}
	
	/**
	 * Purpose: This method is used to update the model stored within the controller to be a 
	 * new MinesweeperModel object with the provided specifications.
	 * 
	 * @param row int number of rows for the MinesweeperBoard
	 * @param col int number of columns for the MinesweeperBoard
	 * @param count int number of mines for the MinesweeperBoard
	 * @param highBoard HighScoreBoard containing all information about saved scores and usernames
	 * @param difficulty String representing the difficulty of the MinesweeperBoard
	 * @param shape String representing the shape of the MinesweeperBoard
	 */
	public void updateModel(int row, int col, int count, HighScoreBoard highBoard, String difficulty, String shape) {
		model = new MinesweeperModel(row, col, count, highBoard, difficulty, shape);

	}

    /**
     * Purpose: This is a getter for the score (time) associated with the current MinesweeperBoard.
     * 
     * @return int representing the players current score (time)
     */
    public int getScore() {
        return this.model.getBoard().getScore();
    }

    /**
     * Purpose: This is a setter for the score (time) associated with the current MinesweeperBoard.
     * 
     * @param score int containing the new score (time) to be saved to the board
     */
    public void setScore(int score) {
        this.model.getBoard().setScore(score);
    }

    /**
     * Purpose: This is a getter for the number of flags available to go on the current 
     * MinesweeperBoard.
     * 
     * @return int representing the number of flags available to go on the current MinesweeperBoard
     */
    public int getNumFlags() {
        return this.getBoard().getNumFlags();
    }
    
    /**
     * Purpose: This is a getter for the visibility status of a specific square on the current 
     * MinesweeperBoard.
     * 
     * @param row int indicating the row of the square being checked
     * @param col int indicating the column of the square being checked
     * @return visiblity value associated with the position (row, col) in the MinesweeperBoard
     */
    public int getVisible(int row, int col) {
        return this.model.getBoard().getVisible(row, col);
    }
	
    /**
	 * Purpose: This method processes each player click. If the user tries to click on a square 
	 * where they cannot make a move, an IllegalMoveException is thrown.  If they click on a mine,
	 * a HitMineException is thrown.  Otherwise, the square on which they clicked is revealed, and, 
	 * if it is empty, then automatically all other neighboring squares are revealed (rippling out
	 * for neighboring empty squares).
	 * 
	 * @param row int value of the row to put move on
	 * @param col int value of the column to put move on
	 * @throws HitMineException indicates mine has been clicked on
	 * @throws IllegalMoveException indicates the move is not legal (the square is already revealed,
	 * the square is flaged, or the square does not exist *DNE* for the specified board shape)
	 */
	public void putTurn(int row, int col) throws HitMineException, IllegalMoveException {
		
		if(model.getVisibility(row, col) != MinesweeperBoard.INVISIBLE) {
			//exception is thrown if visibility is set to visible or flag or dne
			throw new IllegalMoveException(row, col);
		}
		
		if(!model.getInit()) {
			model.newGame(row, col);
		} 
		
		if(model.getVal(row, col) == MinesweeperBoard.MINE) {
			HashSet<OrderedPair> mineLocs = model.getMineLocs();
			for(OrderedPair op : mineLocs) {
				model.putVisibility(op.getRow(), op.getCol(), MinesweeperBoard.VISIBLE);
			}
			throw new HitMineException(row, col);
		}
		
		model.putVisibility(row, col, MinesweeperBoard.VISIBLE);
		HashSet<OrderedPair> visited = new HashSet<OrderedPair>();
		automaticReveal(row, col, visited);	
		
	}
	
	/**
	 * This is a private recursive helper method which handles the automatic
	 * reveal of all spaces touching with the initial space and all connected
	 * empty spaces.
	 * 
	 * @param row int containing the row coordinate of the initial space
	 * @param col int containing the column coordinate of the initial space
	 * @param visited HashSet of OrderedPairs containing all the orderedPairs that 
	 * have been previously examined to see if they need to be revealed
	 */
	private void automaticReveal(int row, int col, HashSet<OrderedPair> visited) {
		visited.add(new OrderedPair(row, col));
		if (model.getVal(row, col) != 0) {
			return; //automatic reveal only applied on empty squares
		}
		ArrayList<OrderedPair> adjacencies = model.getAdjacencies(row, col);
		for (OrderedPair neighbor: adjacencies) {
			if (model.getVisibility(neighbor.getRow(), neighbor.getCol()) == MinesweeperBoard.INVISIBLE) {
				model.putVisibility(neighbor.getRow(), neighbor.getCol(), MinesweeperBoard.VISIBLE);
				if (!visited.contains(new OrderedPair(neighbor.getRow(), neighbor.getCol()))) {
					automaticReveal(neighbor.getRow(), neighbor.getCol(), visited);
				}
			}
		}
	}
	
	/**
	 * Purpose: This method handles the placement and removal of flags on the board.  It 
	 * throws an IllegalMoveException if the square that is clicked is already visible to 
	 * the user or is DNE (does not exist for the specified shape).  If there is already a 
	 * flag in the specified position, then the flag is removed.  Otherwise, the flag is 
	 * placed.
	 * 
	 * @param row int value of the row in which to add the flag on board
	 * @param col int value of the column in the row in which to add the flag on board
	 * @throws IllegalMoveException indicates user is trying to place a flag on an invalid location:
	 * either an already revealed square or a square set to DNE for that particular shape
	 */
	public void flag(int row, int col) throws IllegalMoveException {
        if (model.getVisibility(row, col) == MinesweeperBoard.FLAG) {
			// if there is already a flag there, remove it.
			model.putVisibility(row, col, MinesweeperBoard.INVISIBLE);
            this.getBoard().addFlag();
		} else if (model.getVisibility(row, col) == MinesweeperBoard.VISIBLE || model.getVisibility(row, col) == MinesweeperBoard.DNE || (this.getNumFlags() <= 0)) {
			//exception is thrown if the square is already visible to the user
			throw new IllegalMoveException(row, col);
		} else {
			//if there is no flag there, place a flag there
			model.putVisibility(row, col, MinesweeperBoard.FLAG);
            this.getBoard().subFlag();
		}
	}
	
	/**
     * Purpose: This method is used to check whether the game has been won, returning true if 
     * it has and false if not.
     * 
     * @return boolean expressing whether or not the game has been won
     */
	public boolean isGameWon() {
		//nonmine squares are all squares that are not a mine and not nonexistent in shape
		int nonMineSquares = (model.numRows()*model.numCols()) - model.getMineLocs().size() - model.getDNEs().size();
		if (model.numVisible() == nonMineSquares) { //if all non-mines are visible
			return true;
		} else {
			return false;
		}
	}
	
	/**
     * Purpose: This is a getter for the value associated with a particular position in the 
     * current MinesweeperBoard.
     * 
     * @param row int indicating the row of the specified square
     * @param col int indicating the column of the specified square
     * @return int representing the value stored at the specified square in the board
     */
	public int getVal(int row, int col) {
		return model.getVal(row, col);
	}
	
	
	/**
     * Purpose: This method adds the given view as an observer of the model.
     * 
     * @param view MinesweeperView being added as the observer of the model
     */
	public void addObserver(MinesweeperView view) {
		model.addObserver(view);
	}
	
	/**
     * Purpose: This method deletes the given view as an observer of the model.
     * 
     * @param view MinesweeperView being deleted as the observer of the model
     */
	public void deleteObserver(MinesweeperView view) {
		model.deleteObserver(view);
	}

	/**
     * Purpose: This triggers the view to update based on the model.
     * 
     * @param view MinesweeperView being triggered to update
     */
    public void update(MinesweeperView view) {
        view.update(model, model.getBoard());
    }
    
    /**
     * Purpose: This method records a new score into the HighScoreBoard class that is
     * stored inside of the Model.  Any and all completed games are saved into the 
     * HighScoreBoard.
     * 
     * @param name String value for the user's name as inputed in a text prompt field
     * @param score int value of the user's score achieved completing the game
     * @param mode String value representing the difficulty mode user was playing on 
     */
    public void putNewScore(String name, int score, String mode) {
    	model.enterNewScore(name, score,mode);
    }

    /**
     * Purpose: This is a getter for the number of rows in the current MinesweeperBoard.
     * 
     * @return int representing the number of rows in the current MinesweeperBoard
     */
    public int numRows() {
    	return model.numRows();
    }
    
    /**
     * Purpose: This is a getter for the number of columns in the current MinesweeperBoard.
     * 
     * @return int representing the number of columns in the current MinesweeperBoard
     */
    public int numCols() {
    	return model.numCols();

    }
    
    /**
     * Purpose: This is a getter for the difficulty level of the current MinesweeperBoard.
     * 
     * @return String representing the difficulty level of the current MinesweeperBoard
     */
    public String getDifficulty() {
    	return model.getDifficulty();
    }
    
    /**
     * Purpose: This is a getter for the shape of the current MinesweeperBoard.
     * 
     * @return String representing the shape of the current MinesweeperBoard
     */
    public String getShape() {
    	return model.getShape();
    }
    

    /**
     * Purpose: This is a getter for the current HighScoreBoard.
     * 
     * @return HighScoreBoard containing all the currently saved high scores
     */
    public HighScoreBoard getHighScoreBoard() {
    	return model.getHighScoreBoard();
    }
    
    /**
     * Purpose: This is a getter for the current MinesweeperBoard.
     * 
     * @return MinesweeperBoard containing the current state of the board
     */
    public MinesweeperBoard getBoard() {
    	return model.getBoard();
    }
    
    /**
     * Purpose: This is a getter for the number of mines on the current MinesweeperBoard.
     * 
     * @return int representing the number of mines on the current MinesweeperBoard
     */
    public int getNumMines() {
    	return model.getMineLocs().size();
    }
    
    /**
     * Purpose: This is a getter for the initialization status of the current MinesweeperBoard.
     * The board is considered initialized once mines have been placed (meaning after the first 
     * turn is executed).
     * 
     * @return boolean expressing whether or not the board is initialized 
     */
    public boolean getInit() {
    	return model.getInit();
    }
    
    
}
