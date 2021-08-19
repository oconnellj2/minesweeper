package orderedpair;

import java.io.Serializable;

/** 
 * File: OrderedPair.java
 * Assignment: Minesweeper
 * Course: CSC 335; Spring 2021
 * This class is meant to model an OrderedPair object that stores coordinates
 * of a location in the game board as row and column values.  
 * 
 * It has a two argument constructor taking in the row and column of the 
 * OrderedPair to store in the instance fields.  It has getters for each field,
 * and it overrides the Object class' equals method and hashCode method.
 * 
 * @author Caroline Hyland
 * @author James O'Connell
 * @author Nick Leluan
 * @author Christian Trejo
 */
public class OrderedPair implements Serializable {

	/**
	 * This contains the row coordinate for this position in the grid.
	 */
    private int row;
    /**
     * This contains the column coordinate for this position in the grid.
     */
    private int col;
	
    /**
     * This is a two argument constructor which makes a new OrderedPair with the 
     * specified row and column.
     * 
     * @param row int value for row instance field
     * @param col int value for col instance field
     */
    public OrderedPair(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * This is a getter method for the row instance field.
     * 
     * @return int value of row instance field
     */
    public int getRow() {
        return this.row;
    }

    /**
     * This is a getter method for the col instance field.
     * 
     * @return int value of col instance field
     */
    public int getCol() {
        return this.col;
    }

    /**
     * This overrides the equals method of the Object class, saying that this object
     * is equal to any other object that is also an OrderedPair with the same row
     * and col values.
     * 
     * @param o Object to which the current object is being compared
     * @return boolean expressing whether or not the current object is equal to o
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof OrderedPair) {
            OrderedPair other = (OrderedPair) o;
            if (this.row == other.row && this.col == other.col) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * This overrides the hashCode method of the Object class, ensuring that equivalent
     * OrderedPair hash to the same values.
     * 
     * @return int hash code for the OrderedPair
     */
    @Override
    public int hashCode() {
        return (11*row) + (19*col);
    }

}
