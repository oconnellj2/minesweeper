package view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

import controller.MinesweeperController;
import exceptions.HitMineException;
import exceptions.IllegalMoveException;
import highscore.HighScoreBoard;
import highscore.PlayerProfile;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import model.MinesweeperBoard;

/** 
 * File: MinesweeperView.java
 * Assignment: Final Project
 * Course: CSC 335; Spring 2021
 * This class is the view for the Minesweeper game. 
 * 
 * Within this class all the methods to handle User input and displaying the current state of the board visually
 * to the user's screen is handled here. 
 * Class has 4 major display windows it will show:
 * Greeting Page - window that displayed New Game and Load Game (if file exists)
 * Options Page - window that displays all the possible game options the user can select for a new game
 * Game Board View - the current state of the board where the user actually plays; squares are clickable, has a dynamic timing
 * feature and has capabilities of placing down flags for suspecting mine locations 
 * High Score Displays - Displays the top 10 high scores for the given difficulty that the user has successfully completed
 *
 * @author Caroline Hyland
 * @author James O'Connell
 * @author Nicholas Leluan
 * @author Christian Trejo
 */
@SuppressWarnings("deprecation")
public class MinesweeperView extends Application implements Observer {
	// Main game fields.
	/**
	 * Stage object that can be manipulated and shown to the user 
	 */
    private Stage mainStage;
    
    /**
     * Main pane structure of the window 
     */
    private GridPane board;
    
    /**
     * Display pane used in the loadGame() display feature 
     */
    private BorderPane window;
    
    /**
     * instance of the controller class that helps comunicate with the other classes
     */
	private MinesweeperController controller;
	
    // Data fields.
	/**
	 * 2D representation of the GUI board of squares
	 */
    private Rectangle[][] squares;
    
    /**
     * Proximity mine locations 
     */
    private Text[][] nums;
    
    /**
     * values to place for the mine proximity values
     */
    private static final String[] NUM_STRINGS = {"", "1", "2", "3", "4", "5", "6", "7", "8"};
    
    /**
     * Color pallette for the game board proximity values 
     */
    private static final Color[] NUM_COLORS = {Color.TRANSPARENT, Color.BLUE, Color.GREEN, Color.RED, 
    		Color.NAVY, Color.MAROON, Color.TEAL, Color.BLACK, Color.GREY};
    
    /**
     * Image to use on the game board of the mines 
     */
    private Image mineImage = new Image("file:assets/Mine.png"); //mine image for the GUI board 
    
    /**
     * Image to use when a user places a flag 
     */
    private Image flagImage = new Image("file:assets/Flag.png"); // flag image for the GUI board
    
    /**
     * Mine image used in the High Score Board GUI
     */
    private Image altMineImage = new Image("file:assets/altMineImage.png");
    
    /** 
     * total falgs found on the currently played board 
     */
    private int numFlags;
    
    /**
     * GUI display for the flags on the board 
     */
    private Label flagLabel = new Label(Integer.toString(numFlags));
    
    // Timer fields.
    /**
     * Instance of the AnimationTimer
     */
    private AnimationTimer timer;
    
    /**
     * value to save and pass of the currents state of the time field
     */
    private int seconds;
    
    /**
     * boolean flag if the timer is still running
     */
    private boolean timerRunning = false;
    
    /**
     * label to be used in the displaying timer GUI on the board 
     */
    private Label timerLabel = new Label(Integer.toString(seconds));
    
    /**
     * boolean flag to determine if the game is over (either won or loss)
     */
    boolean gameOver;
    
    
    
    
    /**
     * Method that handles the initial logic to determine if certain saved game files are present
     * 
     * If there is not a saved game file or a saved serialzied High Score board class file, this method
     * will load a new controller which will create new instances of all the classes incorporated with
     * creating a new game in the MVC structure of the game. 
     * 
     * If the saved game file and/or the high score board files are not present; new instances of controller
     * or new instances of highscoreboard are created and a game is initialized.
     */
    public void mainMenu() {

        //If user exits GUI early or after game is over.
        mainStage.setOnCloseRequest((event) -> {
        	if (controller == null) {
        		// nothing should be done since no game has begun
        	} else if (!controller.isGameWon() && !gameOver && controller.getInit()) { 
        		saveBoard();
        		saveHighScoreBoard();
        	} else {
        		saveHighScoreBoard();
        	}
        });
        
        
        MinesweeperBoard loadedBoard = null;
        HighScoreBoard loadedHighScoreBoard = null;
        
        //Try to load MinesweeperBoard Instance
        try {	
        	File boardFile = new File("save_game.dat");
        	ObjectInputStream ois = new ObjectInputStream(new FileInputStream(boardFile));
			loadedBoard = (MinesweeperBoard) ois.readObject();
			ois.close();
			boardFile.delete();
		} catch (IOException | ClassNotFoundException e){
            // If no board, catch exception. Just use initial board.
		}
        
        // Try to load HighScoreBoard.
        try {
			File highFile = new File("highscores.dat");
        	ObjectInputStream ois2 = new ObjectInputStream(new FileInputStream(highFile));
			loadedHighScoreBoard = (HighScoreBoard) ois2.readObject();
			ois2.close();
			highFile.delete();
			
		} catch (IOException | ClassNotFoundException e){
            //If no board, catch exception. Just use initial board.
		}
       
        gameOver = false;
        
        //Call correct controller constructor based on existence of both boards
        if(!(loadedBoard == null) && !(loadedHighScoreBoard == null)) {
        	controller = new MinesweeperController(loadedBoard, loadedHighScoreBoard);
        	loadBoardData();
    		playGame();
        } else if (!(loadedBoard == null) && (loadedHighScoreBoard == null)) {
        	controller = new MinesweeperController(loadedBoard);
        	loadBoardData();
    		playGame();
        } else if (loadedBoard == null && !(loadedHighScoreBoard == null)) {
        	controller = null;
        	chooseGameGraphic(loadedHighScoreBoard);
        } else {
        	controller = null;
        	loadedHighScoreBoard = new HighScoreBoard();
        	chooseGameGraphic(loadedHighScoreBoard);
        }
    }
    /**
     * Method creates the 'Greeting Page' of the Minesweeper game.
     * 
     * Method draws to the window a welcome page and presents the user with 2 options depending on which
     * .dat files are present in the current directory.
     * If a saved game file is present in the directory, the player will have the option to either start a
     * new game or continue the saved game. If the the user picks a new game with a save game file in the 
     * directory, the saved game file is deleted and user is shown the Game menu options page.
     * If there is not a saved game file, then the user is only presented with one option "NEW GAME"
     * 
     * @param stage - Stage which functions as the window for the GUI.
     */
	@Override
	public void start(Stage stage) throws Exception {
		// Set up main window.
        mainStage = stage;
        mainStage.setTitle("Minesweeper");
        mainStage.setResizable(true);
        
        board = new GridPane();
        board.setPadding(new Insets(5,0,8,0));
 
        board.setPrefWidth(500);
        board.setPrefHeight(500);
        board.setAlignment(Pos.CENTER);
    	BackgroundImage bgImage = new BackgroundImage(new Image("file:assets/greetingPageBG.png"),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,
    			BackgroundPosition.CENTER,BackgroundSize.DEFAULT);
    	board.setBackground(new Background(bgImage));
    	VBox menu = new VBox();
    	menu.setPadding(new Insets(25,0,8,0));
    	menu.setAlignment(Pos.CENTER);
    	File dummy = new File("save_game.dat");
    	GridPane temp = new GridPane();
    	temp.setPadding(new Insets(50,0,8,0));
    	Button newGame = new Button("New Game");
    	newGame.setPrefSize(125,50);
    	// NEW GAME LOGIC
    	newGame.setOnMouseClicked(e -> {
	    	if (dummy.exists()) {
	    		dummy.delete();
	    	}
	    	mainMenu();
    	});
    	temp.add(newGame, 0, 0);
    	GridPane.setHalignment(newGame, HPos.CENTER);
    	menu.getChildren().add(temp);
    	// LOAD GAME LOGIC    	
    	if (dummy.exists()) {
    		Button loadGame = new Button("Load Game");
    		loadGame.setPrefSize(125, 50);
    		GridPane g = new GridPane();
    		g.setPadding(new Insets(8,0,8,0));
    		g.add(loadGame, 0, 0);
        	GridPane.setHalignment(loadGame, HPos.CENTER);
    		menu.getChildren().add(g);
    		loadGame.setOnMouseClicked(e -> {
    			mainMenu();
    		});
    	}
    	// creation of names 
    	VBox x = new VBox();
    	x.setAlignment(Pos.BOTTOM_CENTER);
    	Text credits = new Text();
    	credits.setTextAlignment(TextAlignment.CENTER);
    	credits.setText("\n\n\nCaroline Hyland\nJames O'Connell\nChristian Trejo\nNicholas Leluan");
    	x.getChildren().add(credits);
    	menu.getChildren().add(x);
    	board.addRow(0, menu);
    	board.addRow(1, x);
    	mainStage.setScene(new Scene(board));
    	
    	mainStage.show();
     
	}

    /**
     * Method that loads in a previous saved game and sets all of if its values
     * into the associated fields
     * 
     */
	private void loadBoardData() {
    	seconds = controller.getScore();
        numFlags = controller.getNumFlags();
        flagLabel.setText("Flags:" + Integer.toString(numFlags));
        timerLabel.setText("Time:" + Integer.toString(seconds));
    	squares = new Rectangle[controller.numRows()][controller.numCols()];
		nums = new Text[controller.numRows()][controller.numCols()];
	}

    /**
     * Method to be used when the player wants to play a New Game
     * 
     * @param rows int value for the number of rows to put into the board
     * @param col int value for the number of columns to put into each row of the board
     * @param highScoreBoard HighScoreBoard object of a serialized instance of this class
     * @param difficulty String representation of the difficulty mode the user chose to play
     * @param shape String representing the shape of the board the user chose to play with 
     */
	private void buildNewGame(int rows, int cols, int mines, HighScoreBoard highScoreBoard, String difficulty, String shape) {
		if(controller == null) {
			controller = new MinesweeperController(rows, cols, mines, highScoreBoard, difficulty, shape);
		} else {
			controller.updateModel(rows, cols, mines, highScoreBoard, difficulty, shape);
		}
        numFlags = controller.getNumFlags();
		flagLabel.setText("Flags:" + Integer.toString(numFlags));
		// Handles resetting the timer.
        timerLabel.setText("Time:" + Integer.toString(seconds));
        timerRunning = false;
        seconds = 0;
        gameOver = false;
		squares = new Rectangle[controller.numRows()][controller.numCols()];
		nums = new Text[controller.numRows()][controller.numCols()];
		playGame();
	}

    /**
     * Method that draws the the GUI for the "New Game Selection" window which displays to the 
     * user all the different game options including a custom board option. 
     * This method uses other private helper methods to draw the GUI options for the player to choose from
     * Once the player has made a selction, a new game is automatically launched 
     * 
     * @param highScoreBoard HighScoreBoard instance of the present high scores on the local machine 
     */
	private void chooseGameGraphic(HighScoreBoard highScoreBoard) {			
		// Build display
		board = new GridPane();
		int boardRow = 0;
        board.setBackground(new Background(new BackgroundFill(Color.GREY, CornerRadii.EMPTY, Insets.EMPTY)));
        board.setPadding(new Insets(8));
        
        //Put Welcome message
        Text welcomeMessage = new Text();
        welcomeMessage.setText("Welcome to Minesweeper!");
        welcomeMessage.setFont(Font.font("Arial",20));
        board.add(welcomeMessage, 0, boardRow++);
        GridPane.setHalignment(welcomeMessage, HPos.CENTER);
        
        // Put "Choose Difficulty for Rectangle Board:" message
        Text rectangleDifficultyText = new Text();
        rectangleDifficultyText.setText("\nRectangular Board:");
        rectangleDifficultyText.setFont(Font.font("Arial",20));
        board.add(rectangleDifficultyText, 0, boardRow++);
        
        // Put rectangle difficulties into graphic
        makeRectangleDifficulties(highScoreBoard, boardRow);
        boardRow++;
        
        // Put "Choose Difficulty for Triangle Board:" message
        Text triangleDifficultyText = new Text();
        triangleDifficultyText.setText("\nTriangular Board:");
        triangleDifficultyText.setFont(Font.font("Arial",20));
        board.add(triangleDifficultyText, 0, boardRow++);
        
        // Put rectangle difficulties into graphic
        makeTriangleDifficulties(highScoreBoard, boardRow);
        boardRow++;
        
        // Put "Choose Difficulty for Diamond-Shaped Board:" message
        Text diamondDifficultyText = new Text();
        diamondDifficultyText.setText("\nDiamond-Shaped Board:");
        diamondDifficultyText.setFont(Font.font("Arial",20));
        board.add(diamondDifficultyText, 0, boardRow++);
        
        // Put rectangle difficulties into graphic
        makeDiamondDifficulties(highScoreBoard, boardRow);
        boardRow++;
        
        // Put "Choose Difficulty for Cross-Shaped Board:" message
        Text crossDifficultyText = new Text();
        crossDifficultyText.setText("\nCross-Shaped Board:");
        crossDifficultyText.setFont(Font.font("Arial",20));
        board.add(crossDifficultyText, 0, boardRow++);
        
        // Put rectangle difficulties into graphic
        makeCrossDifficulties(highScoreBoard, boardRow);
        boardRow++;
        
        // Put "Customized Rectangular Board:" message
        Text customDifficultyText = new Text();
        customDifficultyText.setText("\nCustomized Rectangular Board:");
        customDifficultyText.setFont(Font.font("Arial",20));
        board.add(customDifficultyText, 0, boardRow++);
        
        // handle custom board
        GridPane rowsGridPane = new GridPane();
        rowsGridPane.setPadding(new Insets(8));
        Text rowsText = new Text();
        rowsText.setText("How many rows?  ");
        rowsText.setFont(Font.font("Arial",15));
        rowsGridPane.add(rowsText, 0, 0);
        TextField customRows = new TextField();
        rowsGridPane.add(customRows, 1, 0);
        board.add(rowsGridPane, 0, boardRow++);
        
        GridPane colsGridPane = new GridPane();
        colsGridPane.setPadding(new Insets(8));
        Text colsText = new Text();
        colsText.setText("How many columns?  ");
        colsText.setFont(Font.font("Arial",15));
        colsGridPane.add(colsText, 0, 0);
        TextField customCols = new TextField();
        colsGridPane.add(customCols, 1, 0);
        board.add(colsGridPane, 0, boardRow++);
        
        GridPane minesGridPane = new GridPane();
        minesGridPane.setPadding(new Insets(8));
        Text minesText = new Text();
        minesText.setText("How many mines?  ");
        minesText.setFont(Font.font("Arial",15));
        minesGridPane.add(minesText, 0, 0);
        TextField customMines = new TextField();
        minesGridPane.add(customMines, 1, 0);
        board.add(minesGridPane, 0, boardRow++);

        Button customBoardDone = new Button("Make Custom Rectangular Board");
        customBoardDone.setTextFill(Color.BLACK);
        customBoardDone.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	buildCustomBoard(customRows.getText(), customCols.getText(), customMines.getText(), highScoreBoard);
            }
        });
        board.add(customBoardDone, 0, boardRow++);
        
        //show graphics
        mainStage.setScene(new Scene(board));
        mainStage.show();
	}

	/**
	 * GUI for the Rectangle difficulties and options
	 * Upon selection, the game board is initialized with Rectangular dimensions and is 
	 * launched automatically given the specifications are chosen by the user
	 * 
	 * @param highScoreBoard HighScoreBoard instance of the present high scores on the local machine 
	 * @param boardRow int value in which to add to the GridPane board
	 */
	private void makeRectangleDifficulties(HighScoreBoard highScoreBoard, int boardRow) {
		GridPane rectangleDifficulties = new GridPane();
        rectangleDifficulties.setPadding(new Insets(8));
        rectangleDifficulties.setHgap(10);
        Button beginner = new Button("Beginner");
        beginner.setTextFill(Color.GREEN);
        beginner.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
				buildNewGame(8, 8, 10, highScoreBoard, "Beginner", "square");
            }
        });
        rectangleDifficulties.add(beginner, 0, 0);
        Button intermediate = new Button("Intermediate");
        intermediate.setTextFill(Color.GOLDENROD);
        intermediate.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
				buildNewGame(16, 16, 40, highScoreBoard, "Intermediate", "square");
            }
        });
        rectangleDifficulties.add(intermediate, 1, 0);
        Button expert = new Button("Expert");
        expert.setTextFill(Color.RED);
        expert.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
				buildNewGame(16, 30, 99, highScoreBoard, "Expert", "square");
            }
        });
        rectangleDifficulties.add(expert, 2, 0);
        board.add(rectangleDifficulties, 0, boardRow);
	}
	
	/**
	 * GUI for the Triangle difficulties and options
	 * Upon selection, the game board is initialized with triangular dimensions and is 
	 * launched automatically given the specifications are chosen by the user
	 * 
	 * @param highScoreBoard HighScoreBoard instance of the present high scores on the local machine 
	 * @param boardRow int value in which to add to the GridPane board
	 */
	private void makeTriangleDifficulties(HighScoreBoard highScoreBoard, int boardRow) {
		GridPane triangleDifficulties = new GridPane();
        triangleDifficulties.setPadding(new Insets(8));
        triangleDifficulties.setHgap(10);
        Button beginner = new Button("Beginner");
        beginner.setTextFill(Color.GREEN);
        beginner.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
				buildNewGame(8, 15, 10, highScoreBoard, "Beginner", "triangle");
            }
        });
        triangleDifficulties.add(beginner, 0, 0);
        Button intermediate = new Button("Intermediate");
        intermediate.setTextFill(Color.GOLDENROD);
        intermediate.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
				buildNewGame(12, 23, 23, highScoreBoard, "Intermediate", "triangle");
            }
        });
        triangleDifficulties.add(intermediate, 1, 0);
        Button expert = new Button("Expert");
        expert.setTextFill(Color.RED);
        expert.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
				buildNewGame(16, 31, 51, highScoreBoard, "Expert", "triangle");
            }
        });
        triangleDifficulties.add(expert, 2, 0);
        board.add(triangleDifficulties, 0, boardRow);
	}
	
	/**
	 * GUI for the diamond difficulties and options
	 * Upon selection, the game board is initialized with diamond dimensions and is 
	 * launched automatically given the specifications are chosen by the user
	 * 
	 * @param highScoreBoard HighScoreBoard instance of the present high scores on the local machine 
	 * @param boardRow int value in which to add to the GridPane board
	 */
	private void makeDiamondDifficulties(HighScoreBoard highScoreBoard, int boardRow) {
		GridPane diamondDifficulties = new GridPane();
        diamondDifficulties.setPadding(new Insets(8));
        diamondDifficulties.setHgap(10);
        Button beginner = new Button("Beginner");
        beginner.setTextFill(Color.GREEN);
        beginner.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
				buildNewGame(11, 11, 10, highScoreBoard, "Beginner", "diamond");
            }
        });
        diamondDifficulties.add(beginner, 0, 0);
        Button intermediate = new Button("Intermediate");
        intermediate.setTextFill(Color.GOLDENROD);
        intermediate.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
				buildNewGame(15, 15, 18, highScoreBoard, "Intermediate", "diamond");
            }
        });
        diamondDifficulties.add(intermediate, 1, 0);
        Button expert = new Button("Expert");
        expert.setTextFill(Color.RED);
        expert.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
				buildNewGame(19, 19, 37, highScoreBoard, "Expert", "diamond");
            }
        });
        diamondDifficulties.add(expert, 2, 0);
        board.add(diamondDifficulties, 0, boardRow);
	}
	

	/**
	 * GUI for the cross difficulties and options
	 * Upon selection, the game board is initialized with cross dimensions and is 
	 * launched automatically given the specifications are chosen by the user
	 * 
	 * @param highScoreBoard HighScoreBoard instance of the present high scores on the local machine 
	 * @param boardRow int value in which to add to the GridPane board
	 */
	private void makeCrossDifficulties(HighScoreBoard highScoreBoard, int boardRow) {
		GridPane crossDifficulties = new GridPane();
        crossDifficulties.setPadding(new Insets(8));
        crossDifficulties.setHgap(10);
        Button beginner = new Button("Beginner");
        beginner.setTextFill(Color.GREEN);
        beginner.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
				buildNewGame(8, 8, 8, highScoreBoard, "Beginner", "cross");
            }
        });
        crossDifficulties.add(beginner, 0, 0);
        Button intermediate = new Button("Intermediate");
        intermediate.setTextFill(Color.GOLDENROD);
        intermediate.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
				buildNewGame(12, 12, 17, highScoreBoard, "Intermediate", "cross");
            }
        });
        crossDifficulties.add(intermediate, 1, 0);
        Button expert = new Button("Expert");
        expert.setTextFill(Color.RED);
        expert.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
				buildNewGame(16, 16, 40, highScoreBoard, "Expert", "cross");
            }
        });
        crossDifficulties.add(expert, 2, 0);
        board.add(crossDifficulties, 0, boardRow);
	}

	/**
	 * GUI method that creates and displays the high scores of the given difficulty and given shape
	 * 
	 * This GUI will display the top 10 scoring players and their associated scores in list format.
	 * A high score is determined by the lowest time. 
	 * When displayed, the user's submitted high score is highlighted in gold if the user made it on 
	 * the board 
	 * @param leaders ArrayList of PlayerProfile objects representing the top 10 scores and associated players
	 * for the given difficulty the user has completed
	 * @param player String value of the passed in name inputed by the user when game completed
	 * @param playerScore int value of the score player achieved when completing the game
	 */
	private void displayHighScores(ArrayList<PlayerProfile> leaders,String player, int playerScore) {
    	board = new GridPane();
    	board.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, CornerRadii.EMPTY, Insets.EMPTY)));
        board.setPadding(new Insets(8,8,15,8));
        board.setMaxWidth(100);
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER);
        Text header = new Text();
        header.setText(String.format("%s %s High Scores!", controller.getDifficulty(),controller.getShape().substring(0,1).toUpperCase() + controller.getShape().substring(1)));
        header.setFont(Font.font("Arial",20));
        headerBox.getChildren().add(header);
        board.add(headerBox, 0, 0);
        VBox stack = new VBox();
        stack.setAlignment(Pos.CENTER);
        int count = 1;
        boolean flag = true;
        // Go through all the players
        for (PlayerProfile p : leaders) {
        	Image mine;
        	if (count == 1) {
        		mine = new Image("file:assets/goldMine.png"); // first place
        	}else if(count == 2) {
        		mine = new Image("file:assets/silverMine.png"); // second place
        	}else if(count == 3) {
        		mine = new Image("file:assets/bronzeMine.png"); // third place
        	}else {
        		mine = altMineImage; // all others 
        	}
        	GridPane slot = new GridPane();
        	slot.setAlignment(Pos.CENTER);
        	slot.setPadding(new Insets(8,8,8,8));
        	ColumnConstraints column = new ColumnConstraints();
        	column.setPercentWidth(15);
        	column.setHalignment(HPos.CENTER);
        	slot.getColumnConstraints().add(column);
        	Label place = new Label();
        	Rectangle placeRec = new Rectangle(25, 25, Color.TRANSPARENT);
        	GridPane displayPlace = new GridPane();
        	Text name = new Text();
        	Text score = new Text();
        	displayPlace.setAlignment(Pos.CENTER_LEFT);
        	place.setText(Integer.toString(count));
        	place.setFont(Font.font("Arial",FontWeight.BOLD,15));
        	place.setTextFill(Color.BLACK);
        	placeRec.setFill(new ImagePattern(mine));
        	displayPlace.getChildren().addAll(placeRec,place);
        	GridPane.setHalignment(place, HPos.CENTER);
        	name.setText(p.getName());
        	if (p.getName().equals(player) && p.getScore() == playerScore && flag) {
        		name.setFill(Color.BLACK);
        		score.setFill(Color.BLACK);
        		name.setFont(Font.font("Arial",FontWeight.BOLD,17));
        		score.setFont(Font.font("Arial",FontWeight.BOLD,17));
        		flag = false;
        	}
        	StackPane nameBox = new StackPane(name);
        	nameBox.setAlignment(Pos.CENTER_LEFT);
        	nameBox.setMinWidth(150);
        	score.setText(Integer.toString(p.getScore()));
        	score.prefWidth(3);
        	slot.addColumn(0, displayPlace);
        	slot.addColumn(1, nameBox);
        	slot.addColumn(2, score);
        	stack.getChildren().add(slot);
        	count++;
        }
        board.addRow(1, stack);
        Image mainMenu = new Image("file:assets/mainMenuHSB.png");
        Image mainMenuHover = new Image("file:assets/mainMenuHSBHover.png");
        ImageView custom = new ImageView();
        custom.setImage(mainMenu);
        custom.setOnMouseEntered(e -> custom.setImage(mainMenuHover));
        custom.setOnMouseExited(e -> custom.setImage(mainMenu));
        custom.setOnMouseClicked(e -> chooseGameGraphic(controller.getHighScoreBoard()));
        board.add(custom, 0, 6);
        mainStage.setScene(new Scene(board));
        mainStage.show();
    	
    	
    }

    /**
     * Method that validates a users request to create a custom shaped board and creates a board/game
     * given the criteria is valid.
     * 
     * @param givenRows String value for the number of rows user has requested
     * @param givenCols String value for the number of columns the user has requested
     * @param givenMines String value for the number of mines the user has reqyested
     * @param highScoreBoard HighScoreBoard instance to be passed along to create a new game in Controller
     * and Model 
     */
	private void buildCustomBoard(String givenRows, String givenCols, String givenMines, HighScoreBoard highScoreBoard) {
		// checks for row/col/mines not filled out
		if (givenRows.equals("") || givenCols.equals("") || givenMines.equals("")) {
			new Alert(Alert.AlertType.INFORMATION, "Please ensure the number of rows, columns, "
					+ "and mines for your custom board are provided.").showAndWait();	
			return;
		}
		
		// checks that the row/col/mines are filled out with ints
		int numRows;
		int numCols;
		int numMines;
			
		try {
			numRows = Integer.parseInt(givenRows);
			numCols = Integer.parseInt(givenCols);
			numMines = Integer.parseInt(givenMines);
		} catch (NumberFormatException e) {
			new Alert(Alert.AlertType.INFORMATION, "Please ensure the number of rows, columns and "
					+ "mines is written as a number (in digits).").showAndWait();	
			return;
		}
		
		// checks that nums for row/col/mine are valid
		if (numRows < 1 || numRows > 35 || numCols < 1 || numCols > 35) {
			new Alert(Alert.AlertType.INFORMATION, "The number of rows and columns for the board must be "
					+ "between 1 and 35 (inclusive).").showAndWait();	
			return;
		} 
		if (numRows < 4 && numCols < 4) {
			new Alert(Alert.AlertType.INFORMATION, "You must have at least either 4 rows or 4 "
					+ "columns.").showAndWait();	
			return;
		} 
		if (numMines < 1) {
			new Alert(Alert.AlertType.INFORMATION, "There must be at least one mine.").showAndWait();	
			return;
		} else if (numMines > (.25)*(numRows*numCols)) {
			int maxMines = (int)((.25)*(numRows*numCols));
			new Alert(Alert.AlertType.INFORMATION, "The maximum number of mines for your "
					+ "chosen board size is " + maxMines + ".").showAndWait();	
			return;
		}
		
		// accepts custom settings and builds new board
		buildNewGame(numRows, numCols, numMines, highScoreBoard, "Custom", "square");
	}

    /**
     * GUI Method that draws the static board of an active game.
     * Elements of this GUI include:
     * Flag Counter, timer, menu drop down for new game 
     * 
     */
	private void playGame() {
		// Set up game view.
        window = new BorderPane();
        window.setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        // Set up menu bar.
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        Label newGameLabel = new Label("New Game");
        MenuItem newGame = new CustomMenuItem(newGameLabel);
        menuFile.getItems().add(newGame);
        menuBar.getMenus().add(menuFile);

        // Set up flag counter.
        flagCounter();

        // Setup timer.
        timer();

        // Set main game board.
        setBoard();
        controller.update(this);
        controller.addObserver(this);

        // If the user wants to start a new game.
        newGame(newGameLabel);

        // Set window.
        GridPane game = new GridPane();
        GridPane.setHalignment(timerLabel, HPos.RIGHT);
        GridPane.setHalignment(flagLabel, HPos.LEFT);
        game.add(flagLabel, 0, 0);
        game.add(timerLabel, 0, 0);
        game.add(board, 0, 1);
        window.setTop(menuBar);
        window.setCenter(game);

        // Show stage.
        Group group = new Group();
        group.getChildren().add(window);
        Scene scene = new Scene(group);
        mainStage.setScene(scene);
        mainStage.show();
	}


    /**
     * GUI Method to draw and keep track of the number of flags that remain for the user to use.
     * This will be displayed in the top left corner below the file drop down menu
     */
    private void flagCounter() {
        numFlags = controller.getNumFlags();
        flagLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 20));
        flagLabel.setText("Flags:" + Integer.toString(numFlags));
    }
    /**
     * Init method to create a new timer at the start of the game 
     * 
     * The timer will be displayed n the top right corner of the game board window
     * and will run indefinitely until the user either wins or loses a game
     * This value is also associated with the int score that the user can achieve
     * when playing the game and having it submitted to the HighScoreBoard class 
     * 
     */
    private void timer() {
        timerLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 20));
        timerLabel.setTextFill(Color.RED);
        seconds = controller.getScore();
        timerLabel.setText("Time:" + Integer.toString(seconds));
        timer = new AnimationTimer() {
            private long lastSec = 0;
            @Override
            public void handle(long now) {
                if (lastSec != 0) {
                    if (now > lastSec + 1_000_000_000) {
                        seconds++;
                        timerLabel.setText("Time:" + Integer.toString(seconds));
                        lastSec = now;
                    }
                } else {
                    lastSec = now;
                }
            }

        };
    }

    /**
     * Method to be used when the user has closed out of the program without finishing the game.
     * 
     * Because the MinesweeperBoard of this program is serializible, this method will serialize
     * the current state of the board and save it in a file called "save_game.dat".
     * 
     */
    private void saveBoard() {
    	try {
            // Save score and reset score.
            controller.setScore(seconds);
            seconds = 0;
    		File boardFile = new File("save_game.dat");
    		FileOutputStream fos = new FileOutputStream(boardFile);
    		ObjectOutputStream oos = new ObjectOutputStream(fos);
    		oos.writeObject(controller.getBoard());
    		fos.close();
    		oos.close();
    		
    	} catch (IOException e) {
    	}
    }

    /**
     * Method to be used when the user has closed out of the program without finishing the game.
     * 
     * Because the HighScoreBoard of this program is serializible, this method will serialize
     * the current state of the board and save it in a file called "highscores.dat".
     * 
     */
    private void saveHighScoreBoard() {
    	try {
    		File highFile = new File("highscores.dat");
    		FileOutputStream fos = new FileOutputStream(highFile);
    		ObjectOutputStream oos = new ObjectOutputStream(fos);
    		oos.writeObject(controller.getHighScoreBoard());
    		
    		fos.close();
    		oos.close();
    	} catch (IOException e) {
    	}
    }

    /**
     * Handles if the user clicks the label to start a new game.
     * 
     * Method is used in the creation of the game board in the playGame() method of
     * this class 
     * 
     * @param newGameLabel - New game label.
     */
    private void newGame(Label newGameLabel) {
        // Clicks new game.
        newGameLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    File file = new File("save_game.dat");
                    file.delete();
                } catch (Exception e) {
                    // Could not delete file.
                }
                timer.stop();
                timerRunning = false;
                
                // Start new game.
                chooseGameGraphic(controller.getHighScoreBoard());
            }
        });
    }

	/**
	 * Method that is called to populate the entire game board with mies and mine proximity locations
	 * after a user has attempted their first move.
	 * 
	 * Because the game must guarantee that the first position the player clicks on cannot be a mine, the board
	 * needs to be built around the position of the players first click. With magic in the controller and model
	 * of this MVC pattern program, this method is used to display the now initialized game board with mine
	 * and mine proximity value locations 
	 */
	private void setBoard() {
        // Main game board.
        board = new GridPane();
        board.setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        board.setPadding(new Insets(8));

        // Fill GridPane.
        for (int row=0; row<controller.numRows(); row++) {
            for (int col=0; col<controller.numCols(); col++) {
                Rectangle tempRec = new Rectangle(30, 30, Color.TRANSPARENT);
                Text tempNum = new Text();
                tempNum.setFont(Font.font("Arial",20));
                tempNum.setFill(Color.BLACK);
                // Check for load values at their indices.
                if (controller.getVisible(row, col) == MinesweeperBoard.VISIBLE) {
                    tempRec.setFill(Color.TAN);
                    if (controller.getVal(row, col) == MinesweeperBoard.MINE) {
                        tempRec.setFill(new ImagePattern(mineImage));
                    } else {
                        tempNum.setText(NUM_STRINGS[controller.getVal(row, col)]);
                        tempNum.setFill(NUM_COLORS[controller.getVal(row, col)]);
                    }
                } else if (controller.getVisible(row, col) == MinesweeperBoard.FLAG) {
                    tempRec.setFill(new ImagePattern(flagImage));
                } else if (controller.getVisible(row, col) == MinesweeperBoard.DNE) {
                	tempRec.setStroke(Color.TRANSPARENT);
                }
                // Add event to square.
                tempRec.setOnMouseClicked(new executeTurn(row, col));
                squares[row][col] = tempRec;
                nums[row][col] = tempNum;
                StackPane newStackPane = new StackPane(tempRec, tempNum);
                newStackPane.setPadding(new Insets(2));
                BorderStroke[] borderStrokes = new BorderStroke[4];
                // Add lines
                for (int i=0; i<borderStrokes.length; i++) {
                	if (controller.getVisible(row, col) == MinesweeperBoard.DNE) {
                		borderStrokes[i] = new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT);
                    } else {
                        borderStrokes[i] = new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT);
                    }
                }
                newStackPane.setBorder(new Border(borderStrokes));
                board.add(newStackPane, col, row);
            }
        }
    }

    /**
     * Method that is called at the end of a game.
     * 
     * An end game can either happen when the user has clicked on a mine locations or if the player has
     * successfully completed the game by clicking on every position on the board that is NOT a mine.
     * If the user loses the game by clicking a mine, the user is greeted with a text alert of their failure
     * and immediately sent back to the new game option pane to start a new game.
     * If the user wins the game, they are prompted to enter in a name to the pop up window. This String values
     * is then passed along with the users current score value and stored inside the HighScoreBoard class. After 
     * these events, the user is then displayed the top 10 high scores of the difficulty they attempted.
     * 
     * Called by the event handlers by the squares. Passed boolean true if won.
     * 
     * @param message String that represents if the player has won or lost.
     */
    private void endGame(boolean won) {
    	timer.stop();
    	timerRunning = false;
    	int score = seconds;
        seconds = 0;
    	if (won && !controller.getDifficulty().equals("Custom")) {
    		TextInputDialog dialog = new TextInputDialog("Enter a name");
    		dialog.setTitle("Congratualtions! You won!");
    		dialog.setHeaderText("You found all the mines!");
    		Optional<String> name = dialog.showAndWait();
    		try {
    			controller.putNewScore(name.get(), score, controller.getDifficulty()+controller.getShape());
    		}catch (NoSuchElementException e) {
    			
    		}
    		HighScoreBoard hsBoard = controller.getHighScoreBoard();
    		ArrayList<PlayerProfile> leaders = hsBoard.getTopTen(controller.getDifficulty()+controller.getShape());

    		displayHighScores(leaders,name.get(),score);
    	// edge case; not showing high score board for Custom games
    	// just let player know they won and direct them back to the chooseGameGraphic()
    	} else if (controller.getDifficulty().equals("Custom") && won) {
    		gameOver = true;
    		new Alert(Alert.AlertType.INFORMATION,"Congratualtions, you won!").showAndWait();
    		chooseGameGraphic(controller.getHighScoreBoard());
    	}
    	else {
    		gameOver = true;
    		new Alert(Alert.AlertType.INFORMATION, "You hit a mine! You Lose!").showAndWait();	
    		chooseGameGraphic(controller.getHighScoreBoard());
    	} 
    }
    


    /**
     * This method updates the view when changes have been made to the model.
     * 
     * @param o Observable which in this case is the model that was updated.
     * @param arg Object which in this case is the board held within the model.
     */
	@Override
	public void update(Observable o, Object arg) {
        for (int row=0; row<controller.numRows(); row++) {
            for (int col=0; col<controller.numCols(); col++) {
            	if (controller.getVisible(row, col) == MinesweeperBoard.VISIBLE) {
                    squares[row][col].setFill(Color.TAN);
                    if (controller.getVal(row, col) == MinesweeperBoard.MINE) {
                    	squares[row][col].setFill(new ImagePattern(mineImage));
                    } else {
                    	nums[row][col].setText(NUM_STRINGS[controller.getVal(row, col)]);
                    	nums[row][col].setFill(NUM_COLORS[controller.getVal(row, col)]);
                    }
                } else if (controller.getVisible(row, col) == MinesweeperBoard.FLAG) {
                	squares[row][col].setFill(new ImagePattern(flagImage));
                } else if (controller.getVisible(row, col) == MinesweeperBoard.DNE) {
                	continue; //do not modify non-existing game squares
            	} else {
                    squares[row][col].setFill(Color.TRANSPARENT);
                    nums[row][col].setFill(Color.BLACK);
                    nums[row][col].setText("");
                }
            }
        }

    }
	
	/**
     * This class represents a player's Mouse Click on a square and triggers 
     * an event.
     */
    private class executeTurn implements EventHandler<MouseEvent> {
        private int row;
        private int col;

        /**
         * Represents an instance of the of a players move with the specified row and column being clicked.
         * 
         * @param row - int row index on the board.
         * @param col - int column index on the board.
         */
        public executeTurn(int row, int col) {
            this.row = row;
            this.col = col;
        }

        /**
         * Executes a players move on the board.
         * @param event MouseEvent indicating the circle was clicked.
         */
        @Override 
        public void handle(MouseEvent event) {
        	if (event.getButton() == MouseButton.PRIMARY) {
        		// Execute turn on a left click
                try {
                    controller.putTurn(row, col);
                    if (!timerRunning) {
                        // Setup timer.
                        timer();
                        timer.start();
                        timerRunning = true;
                    }
                    // Check if the player has won the game.
                    if (controller.isGameWon()) {
                        endGame(true);
                        
                    }
                } catch (HitMineException e) {
                    // End game.
                    endGame(false);
                } catch (IllegalMoveException e) {
                    // Invalid move so do nothing.
                }
        	} else if (event.getButton() == MouseButton.SECONDARY) {
        		// Place or remove a flag on a right click
        		try {
        			controller.flag(row, col);
                    numFlags = controller.getNumFlags();
                    flagLabel.setText("Flags:" + Integer.toString(numFlags));
        		} catch (IllegalMoveException e) {
        			// Invalid move so do nothing.
        		}
        	}
            
        }
    }

}