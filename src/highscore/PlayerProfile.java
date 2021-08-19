package highscore;

import java.io.Serializable;

/**
 * File: PlayerProfile.java
 * 
 * Class that holds all pertinent information about a player that has completed a game of Minesweeper
 * 
 * This class is used closely with all things relating to the High Score Board. When entering a new record
 * int the High Score Board for this game, a new PlayerProfile is created by saving a user's name and the 
 * score they received. Score is an int value that represents the time it took to complete a puzzle.
 * 
 * @author Caroline Hyland
 * @author James O'Connell
 * @author Nicholas Leluan
 * @author Christian Trejo
 */
public class PlayerProfile implements Serializable {
	
	/**
	 * Serializble ID num 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * field that references the name of the PlayerProfle0
	 */
	private String name;
	
	/**
	 * int value ranging from 0-999
	 */
	private int score;
	
	/**
	 * difficulty mode the player played on 
	 */
	private String mode;
	
	/**
	 * Three argument constructor
	 * 
	 * A new instance of the PlayerProfile class is created using a String representing 
	 * the user's name, the int score value they achieived, and a String representation of the
	 * mode they were attempting 
	 * 
	 * @param name String representing the user's inputed name/username
	 * @param score int value of the time score achieved from completing a game 
	 * @param mode String value representing the difficulty mode user was on 
	 */
	public PlayerProfile(String name, int score, String mode) {
		this.name = name;
		this.score = score;
		this.mode = mode;
		
	}
	
	/**
	 * Getter method for this instance's mode difficulty 
	 * 
	 * @return String value of the mode field 
	 */
	public String getMode() {
		return this.mode;
	}
	
	/**
	 * Getter method for this isntance's score achieved when completing the game
	 * 
	 * @return int value of the score field 
	 */
	public int getScore() {
		return this.score;
	}
	
	/**
	 * Getter method for this instance's String value name stored as a field 
	 * 
	 * @return String value of name associated with this instance; not unique to others 
	 */
	public String getName() {
		return this.name;
	}

}
