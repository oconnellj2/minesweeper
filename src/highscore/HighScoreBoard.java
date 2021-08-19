package highscore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * File: HighScoreBoard.java
 * 
 * This class encompasses and stores all the logic behind the high score board to be used to record and
 * display top scoring attempts of user's of Minesweeper. This class implements the Serializable interface 
 * which is used to save the current state of this board to be used on subsequent game plays of Minesweeper. 
 * This class is saved as "highscores.dat" in other classes and will be found on a user's local directory
 * when the first successful run of the game is complete. This file can be deleted by going to the file directory
 * this program resides and moving the file to trash. 
 * 
 * @author Caroline Hyland
 * @author James O'Connell
 * @author Nicholas Leluan
 * @author Christian Trejo
 */
public class HighScoreBoard implements Serializable {
	
	/**
	 * Serializable ID for HighScoreBoard 
	 */
	private static final long serialVersionUID = 1L;	
	
	/**
	 * allBoards: holds all the boards that can be saved for this game. This has a max size 
	 * of 12. One board for each difficulty of each shape. Custom is saved but not used 
	 */
	private HashMap<String, TreeMap<Integer,ArrayList<PlayerProfile>>> allBoards;

	/**
	 * Zero argument constructor
	 * 
	 * Creates an instance of the HighScoreBoard class that will hold all modes as String keys
	 * and the values associated to the leader board of that particular game difficulty 
	 */
	public HighScoreBoard() {
		allBoards = new HashMap<String, TreeMap<Integer,ArrayList<PlayerProfile>>>();
	}
	
	/**
	 * Adds a new record into the corresponding High Score Board TreeMap
	 * 
	 * Method first checks to see if the passed in mode is currently in the allBoard HashMap or not.
	 * If the game mode/difficulty is not in the allBoard HashMap, it is added.
	 * A new PlayerProfile class is created which contains methods within it that give information about
	 * the particular player who completed the game.
	 * 
	 * @param name String name of the player to be associated with
	 * @param score int value of the score the player achieved
	 * @param mode String value of the difficulty mode that the player completed
	 */
	public void addNewScore(String name, int score, String mode) {
		mode = mode.toLowerCase();
		
		if (!allBoards.containsKey(mode)) {
			allBoards.put(mode, new TreeMap<Integer, ArrayList<PlayerProfile>>());
		}
		PlayerProfile newPlayer = new PlayerProfile(name, score, mode);
		TreeMap<Integer, ArrayList<PlayerProfile>> addTo = allBoards.get(mode);
		// score has not been achieved
		if (!addTo.containsKey(score)){
			addTo.put(score, new ArrayList<PlayerProfile>());
		}
		
		addTo.get(score).add(newPlayer);
	}
	
	/**
	 * Method that gets the Top Ten highest scored scores for a particular mode
	 * 
	 * For the purposes of this game, the top 10 will consist of the lowest scores achieved 
	 * as the nature of the game is to complete the puzzle in as little time as possible
	 * 
	 * @param mode String representing the difficulty/mode that we want to return the top 10 for
	 * @return ArrayList of PlayerProfile objects that will consists of the top 10 scores for the 
	 * given difficulty mode 
	 */
	public ArrayList<PlayerProfile> getTopTen(String mode){
		mode = mode.toLowerCase();
		
		if (!allBoards.containsKey(mode)) {
			return null;
		}
		return getTopN(allBoards.get(mode),10);
	}
	
	/**
	 * Method that get the top 10 scores of a given passed in board that represents all the scores 
	 * of a given board that corresponds to the passed in mode from the getTopTen() public method
	 * 
	 * In detail, this method will only return the top 10 of a given board sorted from highest score 
	 * to lowest score. If there are multiple players with the same score, the most recently submission
	 * of said score is added first.
	 * If there are less than 10 recorded scores in the passed in board; this function will return all the
	 * scores sorted as detailed above.
	 * 
	 * @param board TreeMap mapping Integers to an ArrayList of PlayerProfile objects which will either 
	 * represent the easyLeaders, mediumLeaders or hardLeaders data structure as passed in from getTopTen() 
	 * method 
	 * @return an ArrayList of PlayerProfile objects organized by highest score; and by most recency if there
	 * are ties in scores. 
	 */
	private ArrayList<PlayerProfile> getTopN(TreeMap<Integer, ArrayList<PlayerProfile>> board, int N){
		ArrayList<PlayerProfile> retList = new ArrayList<PlayerProfile>();
		// items will be removed from board. This is a temp structure to hold removed items
		// that will be added later 
		TreeMap<Integer, ArrayList<PlayerProfile>> addBack = new TreeMap<Integer, ArrayList<PlayerProfile>>();
		int count = 0;
		while (count < N && !board.isEmpty()){
			int max = board.firstKey();
			ArrayList<PlayerProfile> lst = board.get(max);
			addBack.put(max, lst);
			// getting the last element of the lists; the last elements will be the most 
			// recently added scores if ties are found
			for (int add = lst.size()-1; add >= 0; add--) {
				retList.add(lst.get(add));
				count++;
				// reached 10 before looking at all the players with this score
				if (count == 10) {
					board.putAll(addBack);
					return retList;
				}
			}
			board.remove(max);
		}
		board.putAll(addBack);
		return retList;
	}
	
	
	
	

}
