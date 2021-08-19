package exceptions;

/** 
 * File: HitMineException.java
 * Assignment: Minesweeper
 * Course: CSC 335; Spring 2021
 * This class is meant to model an exception indicating that the position in 
 * the grid contains a mine.  
 * 
 * An instance of the exception can be created without the specific coordinates
 * of the mine in which case a general message is stored or with the coordinates 
 * of the mine, in which case a more specific message is stored.  This class 
 * also overrides the Exception class's getLocalizedMessage() method to give the 
 * most accurate error message possible.
 * 
 * @author Caroline Hyland
 * @author James O'Connell
 * @author Nick Leluan
 * @author Christian Trejo
 */
public class HitMineException extends Exception {
	public String message;
	
	/**
	 * This is a zero argument constructor that creates a new HitMineException
	 * with a generic message.
     */
	public HitMineException() {
		message = "The user hit a mine.";
	}
	
	/**
	 * This is a single argument constructor that creates a new HitMineException
	 * with a specific message indicating the coordinates of the invalid 
	 * move.
	 * 
	 * @param row int representing the row of the position of the mine
	 * @param col int representing the column of the position of the mine
     */
	public HitMineException(int row, int col) {
		message = "There is a mine at the position (" + row + ", " + col + ").";
	}
	
	/**
	 * This method returns a message indicating specifics about the exception 
	 * that occurred.
	 * 
	 * @return String representing the exception
     */
	@Override
	public String getLocalizedMessage() {
		return message;
	}
}
