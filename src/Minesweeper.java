import view.MinesweeperView;
import javafx.application.Application;

/** 
 * File: Minesweeper.java
 * Assignment: Final Project
 * Course: CSC 335; Spring 2021
 * This class is the main driver for the implementation of the Minesweeper
 * game. This class wil launch the GUI version of the Minesweeper game 
 *
 * @author Caroline Hyland
 * @author James O'Connell
 * @author Nicholas Leluan
 * @author Christian Trejo
 */
public class Minesweeper {
    public static void main(String[] args) {
        Application.launch(MinesweeperView.class, args);
    }

}