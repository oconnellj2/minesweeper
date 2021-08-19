package testing;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.junit.Test;

import controller.MinesweeperController;
import exceptions.HitMineException;
import exceptions.IllegalMoveException;
import highscore.HighScoreBoard;
import highscore.PlayerProfile;
import model.MinesweeperBoard;
import model.MinesweeperModel;
import orderedpair.OrderedPair;

/** 
 * File: MinesweeperTest.java
 * Assignment: Minesweeper
 * Course: CSC 335; Spring 2021
 * This is a tester class, which acheives near 100% statement and branch coverage of
 * the MinesweeperModel and MinesweeperController classes, excepting some exceptions. 
 * 
 * @author Caroline Hyland
 * @author James O'Connell
 * @author Nick Leluan
 * @author Christian Trejo
 */
public class MinesweeperTest {

	/**
     * This method is meant to test that the HashMap adjacency list in the 
     * model is created successfully and returns the proper ArrayList of 
     * OrderedPairs for each position.
     */
	@Test
	void testAdjacencyList() {
		MinesweeperModel model = new MinesweeperModel(4, 4, 2, new HighScoreBoard(), "Custom", "square");
		HashMap<OrderedPair, ArrayList<OrderedPair>> expectedAdjacencies = get4x4Adjacencies();
		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < 4; col++) {
				assertEquals(expectedAdjacencies.get(new OrderedPair(row,col)), model.getAdjacencies(row, col));
			}
		}
	}

    /**
     * Helper method to generate expected Adacencies on row 0, 1, 2, and 3.
     */
	private HashMap<OrderedPair, ArrayList<OrderedPair>> get4x4Adjacencies() {
		HashMap<OrderedPair, ArrayList<OrderedPair>> adjacencies = new HashMap<OrderedPair, ArrayList<OrderedPair>>();
		addRow0Adjacencies(adjacencies);
		addRow1Adjacencies(adjacencies);
		addRow2Adjacencies(adjacencies);
		addRow3Adjacencies(adjacencies);
		return adjacencies;
	}

    /**
     * Helper method to generate expected Adacencies on row 0.
     */
	private void addRow0Adjacencies(HashMap<OrderedPair, ArrayList<OrderedPair>> adjacencies) {
		//add adjacencies for (0,0)
		ArrayList<OrderedPair> row0col0 = new ArrayList<OrderedPair>();
		row0col0.add(new OrderedPair(0,1));
		row0col0.add(new OrderedPair(1,0));
		row0col0.add(new OrderedPair(1,1));
		adjacencies.put(new OrderedPair(0,0), row0col0);
		
		//add adjacencies for (0,1)
		ArrayList<OrderedPair> row0col1 = new ArrayList<OrderedPair>();
		row0col1.add(new OrderedPair(0,0));
		row0col1.add(new OrderedPair(0,2));
		row0col1.add(new OrderedPair(1,0));
		row0col1.add(new OrderedPair(1,1));
		row0col1.add(new OrderedPair(1,2));
		adjacencies.put(new OrderedPair(0,1), row0col1);
		
		//add adjacencies for (0,2)
		ArrayList<OrderedPair> row0col2 = new ArrayList<OrderedPair>();
		row0col2.add(new OrderedPair(0,1));
		row0col2.add(new OrderedPair(0,3));
		row0col2.add(new OrderedPair(1,1));
		row0col2.add(new OrderedPair(1,2));
		row0col2.add(new OrderedPair(1,3));
		adjacencies.put(new OrderedPair(0,2), row0col2);
		
		//add adjacencies for (0,3)
		ArrayList<OrderedPair> row0col3 = new ArrayList<OrderedPair>();
		row0col3.add(new OrderedPair(0,2));
		row0col3.add(new OrderedPair(1,2));
		row0col3.add(new OrderedPair(1,3));
		adjacencies.put(new OrderedPair(0,3), row0col3);
	}

    /**
     * Helper method to generate expected Adacencies on row 1.
     */
	private void addRow1Adjacencies(HashMap<OrderedPair, ArrayList<OrderedPair>> adjacencies) {
		//add adjacencies for (1,0)
		ArrayList<OrderedPair> row1col0 = new ArrayList<OrderedPair>();
		row1col0.add(new OrderedPair(0,0));
		row1col0.add(new OrderedPair(0,1));
		row1col0.add(new OrderedPair(1,1));
		row1col0.add(new OrderedPair(2,0));
		row1col0.add(new OrderedPair(2,1));
		adjacencies.put(new OrderedPair(1,0), row1col0);
		
		//add adjacencies for (1,1)
		ArrayList<OrderedPair> row1col1 = new ArrayList<OrderedPair>();
		row1col1.add(new OrderedPair(0,0));
		row1col1.add(new OrderedPair(0,1));
		row1col1.add(new OrderedPair(0,2));
		row1col1.add(new OrderedPair(1,0));
		row1col1.add(new OrderedPair(1,2));
		row1col1.add(new OrderedPair(2,0));
		row1col1.add(new OrderedPair(2,1));
		row1col1.add(new OrderedPair(2,2));
		adjacencies.put(new OrderedPair(1,1), row1col1);
		
		//add adjacencies for (1,2)
		ArrayList<OrderedPair> row1col2 = new ArrayList<OrderedPair>();
		row1col2.add(new OrderedPair(0,1));
		row1col2.add(new OrderedPair(0,2));
		row1col2.add(new OrderedPair(0,3));
		row1col2.add(new OrderedPair(1,1));
		row1col2.add(new OrderedPair(1,3));
		row1col2.add(new OrderedPair(2,1));
		row1col2.add(new OrderedPair(2,2));
		row1col2.add(new OrderedPair(2,3));
		adjacencies.put(new OrderedPair(1,2), row1col2);
		
		//add adjacencies for (1,3)
		ArrayList<OrderedPair> row1col3 = new ArrayList<OrderedPair>();
		row1col3.add(new OrderedPair(0,2));
		row1col3.add(new OrderedPair(0,3));
		row1col3.add(new OrderedPair(1,2));
		row1col3.add(new OrderedPair(2,2));
		row1col3.add(new OrderedPair(2,3));
		adjacencies.put(new OrderedPair(1,3), row1col3);
	}

    /**
     * Helper method to generate expected Adacencies on row 2.
     */
	private void addRow2Adjacencies(HashMap<OrderedPair, ArrayList<OrderedPair>> adjacencies) {
		//add adjacencies for (2,0)
		ArrayList<OrderedPair> row2col0 = new ArrayList<OrderedPair>();
		row2col0.add(new OrderedPair(1,0));
		row2col0.add(new OrderedPair(1,1));
		row2col0.add(new OrderedPair(2,1));
		row2col0.add(new OrderedPair(3,0));
		row2col0.add(new OrderedPair(3,1));
		adjacencies.put(new OrderedPair(2,0), row2col0);
		
		//add adjacencies for (2,1)
		ArrayList<OrderedPair> row2col1 = new ArrayList<OrderedPair>();
		row2col1.add(new OrderedPair(1,0));
		row2col1.add(new OrderedPair(1,1));
		row2col1.add(new OrderedPair(1,2));
		row2col1.add(new OrderedPair(2,0));
		row2col1.add(new OrderedPair(2,2));
		row2col1.add(new OrderedPair(3,0));
		row2col1.add(new OrderedPair(3,1));
		row2col1.add(new OrderedPair(3,2));
		adjacencies.put(new OrderedPair(2,1), row2col1);
		
		//add adjacencies for (2,2)
		ArrayList<OrderedPair> row2col2 = new ArrayList<OrderedPair>();
		row2col2.add(new OrderedPair(1,1));
		row2col2.add(new OrderedPair(1,2));
		row2col2.add(new OrderedPair(1,3));
		row2col2.add(new OrderedPair(2,1));
		row2col2.add(new OrderedPair(2,3));
		row2col2.add(new OrderedPair(3,1));
		row2col2.add(new OrderedPair(3,2));
		row2col2.add(new OrderedPair(3,3));
		adjacencies.put(new OrderedPair(2,2), row2col2);
		
		//add adjacencies for (2,3)
		ArrayList<OrderedPair> row2col3 = new ArrayList<OrderedPair>();
		row2col3.add(new OrderedPair(1,2));
		row2col3.add(new OrderedPair(1,3));
		row2col3.add(new OrderedPair(2,2));
		row2col3.add(new OrderedPair(3,2));
		row2col3.add(new OrderedPair(3,3));
		adjacencies.put(new OrderedPair(2,3), row2col3);
	}

    /**
     * Helper method to generate expected Adacencies on row 3.
     */
	private void addRow3Adjacencies(HashMap<OrderedPair, ArrayList<OrderedPair>> adjacencies) {
		//add adjacencies for (3,0)
		ArrayList<OrderedPair> row3col0 = new ArrayList<OrderedPair>();
		row3col0.add(new OrderedPair(2,0));
		row3col0.add(new OrderedPair(2,1));
		row3col0.add(new OrderedPair(3,1));
		adjacencies.put(new OrderedPair(3,0), row3col0);
		
		//add adjacencies for (3,1)
		ArrayList<OrderedPair> row3col1 = new ArrayList<OrderedPair>();
		row3col1.add(new OrderedPair(2,0));
		row3col1.add(new OrderedPair(2,1));
		row3col1.add(new OrderedPair(2,2));
		row3col1.add(new OrderedPair(3,0));
		row3col1.add(new OrderedPair(3,2));
		adjacencies.put(new OrderedPair(3,1), row3col1);
		
		//add adjacencies for (3,2)
		ArrayList<OrderedPair> row3col2 = new ArrayList<OrderedPair>();
		row3col2.add(new OrderedPair(2,1));
		row3col2.add(new OrderedPair(2,2));
		row3col2.add(new OrderedPair(2,3));
		row3col2.add(new OrderedPair(3,1));
		row3col2.add(new OrderedPair(3,3));
		adjacencies.put(new OrderedPair(3,2), row3col2);
		
		//add adjacencies for (3,3)
		ArrayList<OrderedPair> row3col3 = new ArrayList<OrderedPair>();
		row3col3.add(new OrderedPair(2,2));
		row3col3.add(new OrderedPair(2,3));
		row3col3.add(new OrderedPair(3,2));
		adjacencies.put(new OrderedPair(3,3), row3col3);
	}

    /**
     * Junit test indented to test automatic reveal once the player executes a
     * first click on the board.
     */
	@Test
	void testAutomaticReveal() {
		MinesweeperModel model = new MinesweeperModel(10, 10, 10, new HighScoreBoard(), "Custom", "square");
		MinesweeperController controller = new MinesweeperController(model.getBoard());
		OrderedPair[] mines = {new OrderedPair(2,2), new OrderedPair(1,8), new OrderedPair(4,1),
				new OrderedPair(5,6), new OrderedPair(5,9), new OrderedPair(6,6), new OrderedPair(6,7),
				new OrderedPair(7,1), new OrderedPair(8,3), new OrderedPair(9,9)};
		model.buildTestBoard(mines);
		//we are planning to click (4,4) in the grid
		HashSet<OrderedPair> visibles = getAutomaticRevealVisibles();
		try {
			controller.putTurn(4, 4); //executes click on (4,4)
			//checks that every square was revealed or not successfully
			for (int row = 0; row < 10; row++) {
				for (int col = 0; col < 10; col++) {
					if (visibles.contains(new OrderedPair(row,col))) {
						assertEquals(MinesweeperBoard.VISIBLE, model.getVisibility(row, col));
					} else {
						assertEquals(MinesweeperBoard.INVISIBLE, model.getVisibility(row, col));
					}
				}
			}
		} catch (HitMineException | IllegalMoveException e) {
			assert(false); //this should be a valid click, so this should not happen
		} 
	}

    /**
     * Helper method intended to get a set of visibles used for testAutomaticReveal.
     * 
     * @return HashSet of all visibles on the board.
     */
	private HashSet<OrderedPair> getAutomaticRevealVisibles() {
		HashSet<OrderedPair> visibles = new HashSet<OrderedPair>();
		visibles.add(new OrderedPair(4,4)); //adds clicked position
		
		visibles.add(new OrderedPair(0,0));
		visibles.add(new OrderedPair(0,1));
		visibles.add(new OrderedPair(0,2));
		visibles.add(new OrderedPair(0,3));
		visibles.add(new OrderedPair(0,4));
		visibles.add(new OrderedPair(0,5));
		visibles.add(new OrderedPair(0,6));
		visibles.add(new OrderedPair(0,7));
		
		visibles.add(new OrderedPair(1,0));
		visibles.add(new OrderedPair(1,1));
		visibles.add(new OrderedPair(1,2));
		visibles.add(new OrderedPair(1,3));
		visibles.add(new OrderedPair(1,4));
		visibles.add(new OrderedPair(1,5));
		visibles.add(new OrderedPair(1,6));
		visibles.add(new OrderedPair(1,7));
		
		visibles.add(new OrderedPair(2,0));
		visibles.add(new OrderedPair(2,1));
		visibles.add(new OrderedPair(2,3));
		visibles.add(new OrderedPair(2,4));
		visibles.add(new OrderedPair(2,5));
		visibles.add(new OrderedPair(2,6));
		visibles.add(new OrderedPair(2,7));
		visibles.add(new OrderedPair(2,8));
		visibles.add(new OrderedPair(2,9));
		
		visibles.add(new OrderedPair(3,0));
		visibles.add(new OrderedPair(3,1));
		visibles.add(new OrderedPair(3,2));
		visibles.add(new OrderedPair(3,3));
		visibles.add(new OrderedPair(3,4));
		visibles.add(new OrderedPair(3,5));
		visibles.add(new OrderedPair(3,6));
		visibles.add(new OrderedPair(3,7));
		visibles.add(new OrderedPair(3,8));
		visibles.add(new OrderedPair(3,9));
		
		visibles.add(new OrderedPair(4,2));
		visibles.add(new OrderedPair(4,3));
		visibles.add(new OrderedPair(4,5));
		visibles.add(new OrderedPair(4,6));
		visibles.add(new OrderedPair(4,7));
		visibles.add(new OrderedPair(4,8));
		visibles.add(new OrderedPair(4,9));
		
		visibles.add(new OrderedPair(5,2));
		visibles.add(new OrderedPair(5,3));
		visibles.add(new OrderedPair(5,4));
		visibles.add(new OrderedPair(5,5));
		
		visibles.add(new OrderedPair(6,2));
		visibles.add(new OrderedPair(6,3));
		visibles.add(new OrderedPair(6,4));
		visibles.add(new OrderedPair(6,5));
		
		visibles.add(new OrderedPair(7,2));
		visibles.add(new OrderedPair(7,3));
		visibles.add(new OrderedPair(7,4));
		visibles.add(new OrderedPair(7,5));
		
		return visibles;
	}

    /**
     * JUnit test intended to handle is a user wins a game of minesweeper.
     */
	@Test
	void testIsGameWon() {
		MinesweeperModel model = new MinesweeperModel(10, 10, 10, new HighScoreBoard(), "Custom", "square");
		MinesweeperController controller = new MinesweeperController(model.getBoard());
		OrderedPair[] mines = {new OrderedPair(0,0), new OrderedPair(2,2), new OrderedPair(2,5),
				new OrderedPair(2,8), new OrderedPair(5,2), new OrderedPair(5,5), new OrderedPair(5,8),
				new OrderedPair(8,2), new OrderedPair(8,5), new OrderedPair(8,8)};
		model.buildTestBoard(mines);
		
		//ensure mines were placed on the board sucessfully
		for(int idx = 0; idx < mines.length; idx++) {
			assertEquals(-1, controller.getVal(mines[idx].getRow(), mines[idx].getCol()));
		}
			
		//test game is won check
		for (int row = 0; row < 10; row++) {
			for (int col = 0; col < 10; col++) {
				if (model.getMineLocs().contains(new OrderedPair(row, col))) {
					continue; //skip over mines
				} else if (row == 9 && col == 9) { //if it is the last (game winning) square
					model.putVisibility(row, col, MinesweeperBoard.VISIBLE);
					assertTrue(controller.isGameWon());
				} else {
					model.putVisibility(row, col, MinesweeperBoard.VISIBLE);
					assertFalse(controller.isGameWon());
				}
			}
		}
	}

    /**
     * Tests various difficulties to be displayed properly on the high score board.
     */
	@Test
	void testHighScoreBoard() {
		HighScoreBoard board = new HighScoreBoard();
		Random rand = new Random();
		String beginnerR = "Beginnerrectangle";
		String intermediateR = "Intermediaterectangle";
		String expertR = "Expertrectangle";
		for (int x = 0; x < 100; x++) {
			int num = rand.nextInt(1000);
			board.addNewScore(Integer.toString(x), num, beginnerR);
			board.addNewScore(Integer.toString(x+10), num, intermediateR);
			board.addNewScore(Integer.toString(x+20), num, expertR);
		}
		ArrayList<PlayerProfile> b = board.getTopTen(beginnerR);
		for (PlayerProfile p : b) {
			assertFalse(p.getName().equals(null));
			assertEquals(p.getMode().equals(beginnerR.toLowerCase()),true);
			assertTrue(p.getScore() >= 0);
		}
		assertEquals(board.getTopTen("ShouldBeNull"),null);
		for (PlayerProfile p : b) {
			//System.out.println(String.format("%s.............%s",p.getName(),p.getScore()));
			assertEquals(p.getMode().equals(beginnerR.toLowerCase()),true);
		}
		HighScoreBoard smallBoard = new HighScoreBoard();
		for(int y = 0; y < 5; y++) {
			int num = rand.nextInt(1000);
			smallBoard.addNewScore(Integer.toString(y), num, expertR);
		}
		for (PlayerProfile p : smallBoard.getTopTen(expertR)) {
			assertEquals(p.getMode().equals(expertR.toLowerCase()),true);
			
		}
		
	}

    /**
     * Tests that the shapes "triangle", "diamond", and "cross" function
     * properly and are able to handle turns and retain their proper metadata.
     */
    @Test
    void testShapes() {
        // Handle if a save file exists.
        File file = new File("save_game.dat");
        if (file.exists()) {
            // Remove save.
            try {
                file.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
		MinesweeperController controller = new MinesweeperController(10, 10, 10, new HighScoreBoard(), "Expert", "triangle");
        assertEquals("Expert", controller.getDifficulty());
        assertEquals("triangle", controller.getShape());
        assertEquals(10, controller.numCols());
        assertEquals(10, controller.numRows());
        assertEquals(10, controller.getNumFlags());
        controller.setScore(10);
        assertEquals(10, controller.getScore());
        //reveal a square
		try {
			controller.putTurn(9, 9);
		} catch (HitMineException | IllegalMoveException e) {
			//this should not happen
			assert(false);
		}
        assertEquals(10, controller.getNumMines());
        assertEquals(1, controller.getVisible(9, 9));

        HighScoreBoard hsb = new HighScoreBoard();
		controller = new MinesweeperController(14, 12, 12, hsb, "Intermediate", "diamond");
        assertEquals(hsb, controller.getHighScoreBoard());
        assertEquals("Intermediate", controller.getDifficulty());
        assertEquals("diamond", controller.getShape());
        //reveal a square
		try {
			controller.putTurn(7, 6);
		} catch (HitMineException | IllegalMoveException e) {
			//this should not happen
			assert(false);
		}
        assertEquals(12, controller.getNumMines());

        hsb = new HighScoreBoard();
        MinesweeperBoard testBoard = new MinesweeperBoard(9, 17, "Beginner", "cross");
		controller = new MinesweeperController(testBoard, hsb);
        assertEquals("Beginner", controller.getDifficulty());
        assertEquals("cross", controller.getShape());
        assertEquals(false, controller.getInit());
        //reveal a square
		try {
			controller.putTurn(7, 6);
		} catch (HitMineException | IllegalMoveException e) {
			//this should not happen
			assert(false);
		}
        assertEquals(true, controller.getInit());
    }

    /**
     * Verifies that the controller is able to update the model.
     */
    @Test
    void testUpdateModel() {
    	//set up controller initially
    	MinesweeperController controller = new MinesweeperController(8, 8, 10, new HighScoreBoard(), "Beginner", "square");
    	
    	//update controller
    	HighScoreBoard newHighScoreBoard = new HighScoreBoard();
    	newHighScoreBoard.addNewScore("James", 10, "Intermediatesquare");
    	controller.updateModel(16, 16, 40, newHighScoreBoard, "Intermediate", "square");
    	controller.putNewScore("Christian", 15, "Expertsquare");
    	
    	//check update was successful
    	assertEquals(16, controller.numRows());
    	assertEquals(16, controller.numCols());
    	assertEquals(newHighScoreBoard, controller.getHighScoreBoard());
    	assertEquals("Intermediate", controller.getDifficulty());
    	assertEquals("square", controller.getShape());
    }

    /**
     * Tests that an exception is thrown when the user clicks on a DNE and
     * tests that an exception is thrown when a mine is clicked on.
     */
    @Test
    void testPutTurn() {
        MinesweeperBoard board= new MinesweeperBoard(8, 8, "Beginner", "triangle");
        MinesweeperController controller = new MinesweeperController(board);
        // Test DNE.
		try {
			controller.putTurn(0, 0);
		} catch (HitMineException | IllegalMoveException e) {
			//This should be a DNE!
			assert(true);
		}

        MinesweeperModel model = new MinesweeperModel(10, 10, 10, new HighScoreBoard(), "Custom", "square");
		controller = new MinesweeperController(model.getBoard());
		OrderedPair[] mines = {new OrderedPair(2,2)};
		model.buildTestBoard(mines);
        try {
			controller.putTurn(2, 2);
		} catch (HitMineException | IllegalMoveException e) {
			//This should be a mine!
			assert(true);
		}
    }

}
