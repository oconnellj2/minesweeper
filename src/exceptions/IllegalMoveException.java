package exceptions;

/** 
 * File: IllegalMoveException.java
 * Assignment: Minesweeper
 * Course: CSC 335; Spring 2021
 * This class is meant to model an exception indicating that the position in 
 * the grid has been clicked on after it has already been revealed.  
 * 
 * An instance of the exception can be created without the specific coordinates
 * of the position in which case a general message is stored or with the coordinates 
 * of the position, in which case a more specific message is stored.  This class 
 * also overrides the Exception class's getLocalizedMessage() method to give the 
 * most accurate error message possible.
 * 
 * @author Caroline Hyland
 * @author James O'Connell
 * @author Nick Leluan
 * @author Christian Trejo
 */
public class IllegalMoveException extends Exception {
	public String message;
	
	/**
	 * This is a zero argument constructor that creates a new IllegalMoveException
	 * with a generic message.
     */
	public IllegalMoveException() {
		message = "The selected position is not valid for this move.";
	}
	
	/**
	 * This is a single argument constructor that creates a new IllegalMoveException
	 * with a specific message indicating the coordinates of the invalid 
	 * move.
	 * 
	 * @param row int representing the row of the position of the previously 
	 * revealed square that has been selected
	 * @param col int representing the column of the position of the previously
	 * revealed square that has been selected
     */
	public IllegalMoveException(int row, int col) {
		message = "The position (" + row + ", " + col + ") is not a valid position "
				+ "for this type of move.";
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
