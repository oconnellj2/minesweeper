package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import orderedpair.OrderedPair;

/**
 * File: MinesweeperBoard.java 
 * Assignment: Minesweeper
 * Course: CSC 335; Spring 2021
 * 
 * This class holds all the logic about the current state of a playable Minesweeper game board. 
 * Two key features of this class are the two int[][] 2-Dimensional arrays that store the current 
 * state and values that make up a game board. The visibility board represents what is currently 
 * visible to the user in the view, the value board stores what is found at the given location which 
 * is either a mine, mine proximity indicator or blank space. 
 * 
 * This class is serializble so it can be saved in the event that the user of the game does not
 * complete a game that has been started. Because this class is one of only 2 classes that are 
 * serializable, a few details about a current game are saved here from the view in ordered to be 
 * saved and reinitialized in the event a user wants to continue a game.
 * 
 * @author Caroline Hyland
 * @author James O'Connell
 * @author Nicholas Leluan
 * @author Christian Trejo
 */
public class MinesweeperBoard implements Serializable {

	/**
	 * Default serials version ID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Visible locations will have value = 1 in visibleBoard.
	 */
	public static final int VISIBLE = 1;
	
	/**
	 * Invisible locations will have value = 0 in visibleBoard.
	 */
	public static final int INVISIBLE = 0;
	
	/**
	 * Locations with mines will have a value = -1 in valBoard.
	 */
	public static final int MINE = -1;
	
	/**
	 * Locations with flags will have a value = -2 in visibleBoard.
	 */
	public static final int FLAG = -2;
	
	/**
	 * Locations that do not exist (DNE) will have a value = -3 in visibleBoard.
	 */
	public static final int DNE = -3;
	
	/**
	 * 2D array representing the current status of the board populated by 
	 * values in range [-1,8] inclusive representing how many mines a location 
	 * is touching or -1 if the location is a mine. 
	 */
	protected int[][] valBoard;
	
	/**
	 * 2D array representing the current status of the board populated by 
	 * 0 for invisible, 1 for visible, and -2 for flag.
	 */
	private int[][] visibleBoard;
	
	/**
	 * HashSet containing OrderedPair objects for all coordinates on the grid
	 * visible to the player.
	 */
	protected HashSet<OrderedPair> visibles;
	
	/**
	 * A set of all the "Does Not Exist" Ordered Pair locations on the board.
	 */
	private HashSet<OrderedPair> dnes;
	
	/**
	 * The number of rows in the current board. 
	 */
	private int rows;
	
	/**
	 * The number of columns in the current board. 
	 */
	private int cols;
	
	/**
	 * HashSet containing OrderedPair objects for all coordinates on the grid
	 * containing mines.
	 */
	private HashSet<OrderedPair> mineLocs;
	
	/**
	 * HashMap that contains an adjacency list for each coordinate
	 * in the grid. Each key has a list of all neighboring positions on the grid.
	 */
	private HashMap<OrderedPair, ArrayList<OrderedPair>> adjacencies;

    /**
	 * Flag to determine if mines and proximity locations have been placed on the valBoard.
	 */
	protected boolean initialized;

    /**
     * The current runtime of the current game. 
     * 
     * This is stored here to be serialized. 
     */
	private int score;
    
    /**
     * Difficulty mode the current board.
     * 
     * This is stored here to be serialized.
     */
	private String difficulty;

    /**
     * Current shape of the board. It will either be rectangle, diamond, hexagon, 
     * triangle or custom.
     */
	private String shape;
	
	/**
     * Tracks the number of flags available for use.
     */
	private int flags;
    
	/**
	 * Constructs a new instance of MinsweeperBoard.
	 * 
	 * @param N The number of rows in the board. 
	 * @param M The number of columns in the board.
	 * @param level The difficulty of the board. 
	 * @param shape The shape of the board to build. 
	 */
	public MinesweeperBoard(int N, int M, String level, String shape) {
		valBoard = new int[N][M];
		visibleBoard = new int[N][M];
		visibles = new HashSet<OrderedPair>();
		dnes = new HashSet<OrderedPair>();
		mineLocs = new HashSet<OrderedPair>();
		rows = N;
		cols = M;
        initialized = false;
        score = 0;
        difficulty = level;
        

        this.shape = shape.toLowerCase();
        if((this.shape).equals("triangle")){
        	setUpTriangle();
        } else if ((this.shape).equals("cross")){
        	setUpCross();
        } else if (shape.equals("diamond")) {
        	setUpDiamond();
        }
        
        adjacencies = makeAdjacencyList();
        flags = mineLocs.size();
	} 

	/**
	 * Sets location [row][col] in the visibleBoard to DNE (does not exist).
	 * 
	 * This method is used to create custom shapes with boundaries.
	 * 
	 * @param row The row coordinate of location to set to DNE
	 * @param col The column coordinate of location to set to DNE.
	 */
	private void makeDNE(int row, int col) {
		dnes.add(new OrderedPair(row, col));
		visibleBoard[row][col] = MinesweeperBoard.DNE;
	}
	
	
	/**
	 * Creates a HashMap where each location is mapped to a list of its 
	 * adjacent locations.  
	 * 
	 * @return HashMap of adjacency lists. 
	 */ 
	private HashMap<OrderedPair, ArrayList<OrderedPair>> makeAdjacencyList() {
		HashMap<OrderedPair, ArrayList<OrderedPair>> adjacencyList = new HashMap<OrderedPair, ArrayList<OrderedPair>>();
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				//skip dne squares
				if (dnes.contains(new OrderedPair(row,col))) {
					continue;
				}
				 
				//process adjacencies
				ArrayList<OrderedPair> currAdjacencies = new ArrayList<OrderedPair>();
				//add adjacencies in row above current row
				if (row-1 >= 0) {
					if (col-1 >= 0) {
						processSquare(currAdjacencies, new OrderedPair(row-1, col-1));
					}
					processSquare(currAdjacencies, new OrderedPair(row-1, col));
					if (col+1 < cols) {
						processSquare(currAdjacencies, new OrderedPair(row-1, col+1));
					}
				}
				//add adjacencies in current row
				if (col-1 >= 0) {
					processSquare(currAdjacencies, new OrderedPair(row, col-1));
				}
				if (col+1 < cols) {
					processSquare(currAdjacencies, new OrderedPair(row, col+1));
				}
				//add adjacencies in row below current row
				if (row+1 < rows) {
					if (col-1 >= 0) {
						processSquare(currAdjacencies, new OrderedPair(row+1, col-1));
					}
					processSquare(currAdjacencies, new OrderedPair(row+1, col));
					if (col+1 < cols) {
						processSquare(currAdjacencies, new OrderedPair(row+1, col+1));
					}
				}
				adjacencyList.put(new OrderedPair(row, col), currAdjacencies);
			}
		}
		return adjacencyList;
	}

	/**
	 * Private helper method which adds the given position to the adjacency
	 * list provided if the square exists.
	 * 
	 * @param currAdjacencies The adjacency list to add the position if conditions are met. 
	 * @param position The position to add to the adjacency list. 
	 */
    private void processSquare(ArrayList<OrderedPair> currAdjacencies, OrderedPair position) {
		if (!dnes.contains(position)) {
			currAdjacencies.add(position);
		}
	}

	/**
     * Sets the player's current game runtime as the score.
     * 
     * @param currScore Player's current game runtime to save.
     */
    public void setScore(int currScore) {
        this.score = currScore;
    }

    /**
     * Returns the current score associated with the board. 
     * 
     * This method will likely be used when there is a previous game loaded and the
     * serialized value of score needs to be used in the GUI setup. 
     * 
     * @return The current score associated with the board. 
     */
    public int getScore() {
        return this.score;
    }
    
    /**
     * Decreases the value representing the number of flags placed. 
     */
    public void subFlag() {
        this.flags--;
    }

    /**
     * Increases the value representing the number of flags placed.
     */
    public void addFlag() {
        this.flags++;
    }
    
    /**
     * Return the number of flags count.
     * 
     * @return Number of flags count.
     */
    public int getNumFlags() {
    	return this.flags;
    }
    
    /**
     * Set the number of flags to the specified number.
     * 
     * @param num The number of flags value to save.
     */
    public void setNumFlags(int num) {
    	this.flags = num;
    }
    
    /**
     * Returns the difficulty associated with the current board.
     * 
     * Method is likely used after the user opts to continue a previously saved game and the 
     * Model, Controller and View need to know what difficulty was serialized 
     * 
     * @return String value representing the difficulty associated with the current board. 
     */
    public String getDifficulty() {
    	return difficulty;
    }
    
    /**
     * Returns the current shape of the board.
     * 
     * This value will either be rectangle, triangle, hexagon, triangle or custom.
     * 
     * @return String value representing the shape of the board.
     */
    public String getShape() {
    	return shape;
    }

    /**
     * Returns true if the board has been initialized, false otherwise. 
     * 
     * A board becomes initialized after a player makes their first move and the 
     * board is populated with mines and proximity locations around the first move 
     * 
     * @return true if the game has been initialized, false otherwise.
     */
    public boolean getInit() {
        return this.initialized;
    }

    /**
     * Sets the initialized field to the given boolean value. 
     * 
     * @param init boolean value to set initialized to.
     */
    public void setInit(boolean init) {
        this.initialized = init;
    }

	/**
	 * Method that returns the value stored at the specified location in the 
	 * MinesweeperBoard
	 * 
	 * @param row int row of position being considered
	 * @param col int column of position being considered
	 * @return int value current found at the (row, col) position of the valBoard
	 */
	public int getVal(int row, int col) {
		return valBoard[row][col];
	}
	
	/**
	 * Method that puts the given value into the instance of the MinesweeperBoard
	 * 
	 * @param row int row of position being considered
	 * @param col int column of position being considered
	 * @param val int value to be placed in instance of Board
	 */
	public void putVal(int row, int col, int val) {
		valBoard[row][col] = val;
	}
	
	/**
	 * Method that gets the current visibility status at the passed in row and col 
	 * location 
	 * 
	 * @param row int row of position being considered
	 * @param col int column of position being considered
	 * @return int visibility status at the (row, col) position
	 */
	public int getVisible(int row, int col) {
		return visibleBoard[row][col];
	}
	
	/**
	 * Method that puts or updates the visibility status at the (row, col) position of the 
	 * visible board
	 * 
	 * Method also creates and adds a new OrderedPair object into the visibles ArrayList
	 * which stores all the locations on the board that has been revealed to the player
	 * 
	 * @param row int row of position being considered
	 * @param col int column of position being considered
	 * @param vis int new visibility status for the (row, col) position on the board
	 */
	public void putVisible(int row, int col, int vis) {
		if (vis == VISIBLE) {
			visibles.add(new OrderedPair(row, col));
		}
		visibleBoard[row][col] = vis;
	}
	
	/**
	 * Method that returns the number of squares that are visible to the user 
	 * 
	 * @return int number of visible squares on the game board 
	 */
	public int numVisible() {
		return visibles.size();
	}
	
	/**
	 * Method that returns the number of rows that this instance of MinesweeperBoard has
	 * 
	 * This number will correspond with the passed in N parameter when creating a new instance
	 * of the MinesweeperBoard class using the two argument constructor 
	 * 
	 * @return int value number of rows this instance Board contains 
	 */
	public int numRows() {
		return this.rows;
	}
	
	/**
	 * Method that returns the number of columns in the rows that this instance of MinesweeperBoard has
	 * 
	 * This number will correspond with the passed in M parameter when creating a new instance
	 * of the MinesweeperBoard class using the two argument constructor 
	 * 
	 * @return int value number of columns in the rows of this board 
	 */
	public int numCols() {
		return this.cols;
	}
	
	/**
	 * Method that adds a new mine to the board.
	 * 
	 * @param pair OrderedPair position of the new mine to be added
	 */
	public void addMine(OrderedPair pair) {
		mineLocs.add(pair);
	}
	
	/**
	 * Method that return a HashSet containing OrderedPair locations for all the mines
	 * in this instance of MinesweeperBoard 
	 * 
	 * @return HashSet of OrderedPair objects for all mine locations
	 */
	public HashSet<OrderedPair> getMineLocs(){
		return mineLocs;
	}
	
	/**
	 * Method that return a HashSet containing OrderedPair locations for all the DNE
	 * locations in this instance of MinesweeperBoard 
	 * 
	 * @return HashSet of OrderedPair objects for all DNE locations
	 */
	public HashSet<OrderedPair> getDNEs(){
		return dnes;
	}

	/**
	 * Method that return an ArrayList containing OrderedPair locations for all the valid
	 * grid spots bordering the given OrderedPair in this instance of MinesweeperBoard 
	 * 
	 * @param op OrderedPair representing the position whose adjacency list is being returned
	 * @return ArrayList of OrderedPairs for valid grid positions bordering the given position
	 * (adjacency list for the given position)
	 */
	public ArrayList<OrderedPair> getAdjacencies(OrderedPair op) {
		return adjacencies.get(op);
	}
	
	/**
	 * Sets DNEs in locations to create an equilateral triangle shape of available spaces.  
	 */
	private void setUpTriangle() {
		
		valBoard = new int[rows][cols];
		visibleBoard = new int[rows][cols];

		int[][] temp = new int[rows][cols];
		int middle = cols/2;		//middle column
		int width = 0;
		
		//Go through every row
		for(int i = 0; i < rows; i++) {
			temp[i][middle] = 4;
			width++;
			//Add values to each side of the middle column
			for(int j = 0; j < width; j++) {
				if(middle + j < cols) {
					temp[i][middle+j] = 4;
				}
				if(middle - j >= 0) {
					temp[i][middle-j] = 4;
			
				}
			}
		}

		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				if(temp[i][j]==0) {
					makeDNE(i,j);
				}
			}
		}
	}
	
	/**
	 * Sets DNEs in locations to create a diamond shape of available spaces.  
	 */
	private void setUpDiamond() {
		
		valBoard = new int[rows][cols];
		visibleBoard = new int[rows][cols];

		int[][] temp = new int[rows][cols];
		int middle = cols/2;		//middle column
		int width = 0;
		
		//Create top half of the diamond
		for(int i = 0; i < rows/2; i++) {
			temp[i][middle] = 4;
			if(i != 0) {
				width++;
			}
			//Add values to both sides of the middle column
			for(int j = 1; j <= width; j++) {
				if(middle + j < cols) {
					temp[i][middle+j] = 4;
				}
				if(middle - j >= 0) {
					temp[i][middle-j] = 4;
				}
			}
		}
		
		//Middle row of the diamond
		for(int i = 0; i < cols; i++) {
			temp[rows/2][i] = 4;
		}
		
		//Create bottom half of the diamond
		width = 0;
		for(int i = rows-1; i > rows/2; i--) {
			temp[i][middle] = 4;
			if(i != rows-1) {
				width++;
			}
			//Add values to each side of the middle column
			for(int j = 1; j <= width; j++) {
				if(middle + j < cols) {
					temp[i][middle+j] = 4;
				}
				if(middle - j >= 0) {
					temp[i][middle-j] = 4;
				}
			}
		}
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				if(temp[i][j]==0) {
					makeDNE(i,j);
				}
			}
		}
	}
	
	/**
	 * Sets DNEs in locations to create a cross shape of available spaces.  
	 */
	private void setUpCross() {
		
		valBoard = new int[rows][cols];
		visibleBoard = new int[rows][cols];

		int[][] temp = new int[rows][cols];
		
		//Vertical bar 
		for(int i = 0; i < rows; i++) {
			for(int j = cols/4; j < 3*cols/4; j++) {
				temp[i][j] = 4;
			}
		}
		
		//Horizontal bar
		for(int i = rows/4; i < 3*rows/4; i++) {
			for(int j = 0; j < cols; j++) {
				temp[i][j] = 4;
			}
		}
		
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				if(temp[i][j]==0) {
					makeDNE(i,j);
				}
			}
		}	
	}
}
