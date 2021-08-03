package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.utility.Multimedia;

import java.util.*;

/**
 * The Grid is a model which holds the state of a game board. It is made up of a set of Integer values arranged in a 2D
 * arrow, with rows and columns.
 * <p>
 * Each value inside the Grid is an IntegerProperty can be bound to enable modification and display of the contents of
 * the grid.
 * <p>
 * The Grid contains functions related to modifying the model, for example, placing a piece inside the grid.
 * <p>
 * The Grid should be linked to a GameBoard for it's display.
 */
public class Grid {

  private static final Logger logger = LogManager.getLogger(Grid.class);

  /**
   * The number of columns in this grid
   */
  private final int cols;

  /**
   * The number of rows in this grid
   */
  private final int rows;

  /**
   * The grid is a 2D arrow with rows and columns of SimpleIntegerProperties.
   */
  private final SimpleIntegerProperty[][] grid;

  /**
   * Create a new Grid with the specified number of columns and rows and initialise them
   *
   * @param cols number of columns
   * @param rows number of rows
   */
  public Grid(int cols, int rows) {
    this.cols = cols;
    this.rows = rows;

    //Create the grid itself
    grid = new SimpleIntegerProperty[cols][rows];

    //Add a SimpleIntegerProperty to every block in the grid
    for (var y = 0; y < rows; y++) {
      for (var x = 0; x < cols; x++) {
        grid[x][y] = new SimpleIntegerProperty(0);
      }
    }
  }

  /**
   * Get the Integer property contained inside the grid at a given row and column index. Can be used for binding.
   *
   * @param x column
   * @param y row
   * @return the IntegerProperty at the given x and y in this grid
   */
  public IntegerProperty getGridProperty(int x, int y) {
    return grid[x][y];
  }

  /**
   * Update the value at the given x and y index within the grid
   *
   * @param x     column
   * @param y     row
   * @param value the new value
   */
  public void set(int x, int y, int value) {
    grid[x][y].set(value);
  }

  /**
   * Get the value represented at the given x and y index within the grid
   *
   * @param x column
   * @param y row
   * @return the value
   */
  public int get(int x, int y) {
    try {
      //Get the value held in the property at the x and y index provided
      return grid[x][y].get();
    } catch (ArrayIndexOutOfBoundsException e) {
      //No such index
      return -1;
    }
  }

  /**
   * Get the number of columns in this game
   *
   * @return number of columns
   */
  public int getCols() {
    return cols;
  }

  /**
   * Get the number of rows in this game
   *
   * @return number of rows
   */
  public int getRows() {
    return rows;
  }

  /**
   * Getting the coordinates on the gameBaord of all the blocks of a given piece.
   *
   * @param gamePiece
   * @param x         coordinate
   * @param y         coordinate
   * @return HashSet of integers containing the coordinates of the piece.
   */
  private Set<int[]> getCoordinatesOnGrid(GamePiece gamePiece, int x, int y) {

    Set<int[]> pieceCoordinates = new HashSet<>();

    //Arrays of the x and y coordinates of the grid (5x5)
    int[] xArray = {-1, -1, -1, 0, 0, 0, 1, 1, 1};
    int[] yArray = {-1, 0, 1, -1, 0, 1, -1, 0, 1};

    for (int i = 0; i < xArray.length; i++) {
      int newY = xArray[i] + x;
      int newX = yArray[i] + y;

      //checking if the block given by the x and y arrays are full or not
      //by using the getBlocks method on the piece passed to this method
      if (gamePiece.getBlocks()[1 + xArray[i]][1 + yArray[i]] != 0) {
        //if the block is not empty, then add the coordinates to the HashSet
        int[] pair = new int[2];
        pair[0] = newY;
        pair[1] = newX;
        pieceCoordinates.add(pair);
        logger.info("X:{} Y:{}", pair[0], pair[1]);
      }
    }
    logger.info("Returning set of piece coordinates");
    return pieceCoordinates;
  }

  /**
   * Checks if a given set of coordinates are playable, by seeing if all the blocks are inside the grid,
   * and if they and being placed on another block.
   *
   * @param pieceCoordinatesOnGrid the piece coordinates on grid
   * @return the boolean
   */
  public boolean canPlayPiece(Set<int[]> pieceCoordinatesOnGrid) {

    //getting all the pairs of coordinates in the given set

    for (int[] pair : pieceCoordinatesOnGrid) {
      int gridX = pair[0];
      int gridY = pair[1];

      //checking if the block is inside the grid
      if (gridX < 0 || gridX > getCols() || gridY < 0 || gridY > getRows()) {
        logger.error("The chosen position is outside the grid!");
        return false;
      }
      //checking if the block is on top of another block
      if (get(gridX, gridY) != 0) {
        logger.error("Piece conflicts with another!");
        return false;
      }
    }
    //if both conditions are not true, the piece is in a valid position
    logger.info("The piece can be played!");
    return true;
  }

  /**
   * Attempting to play a given piece in a given x and y location.
   *
   * @param gamePiece the game piece which is trying to be played
   * @param x         coordinate
   * @param y         coordinate
   * @return the boolean (can the piece be played?)
   */
  public boolean playPiece(GamePiece gamePiece, int x, int y) {

    Set<int[]> pieceCoordinatesOnGrid = getCoordinatesOnGrid(gamePiece, x, y);

    //if the piece can be played
    if (canPlayPiece(pieceCoordinatesOnGrid)) {

      Multimedia.playAudio("sounds/place.wav");

      //set the colour of the blocks on the grid to the correct colour
      //using the getValue method
      for (int[] pair : pieceCoordinatesOnGrid) {
        logger.info("setting colour");
        set(pair[0], pair[1], gamePiece.getValue());
      }
      logger.info("{} piece was successfully added in x: {}, y: {}", gamePiece.toString(), x, y);
      return true;
    }
    logger.error("{} piece can not be added in x: {}, y: {}", gamePiece.toString(), x, y);
    Multimedia.playAudio("sounds/fail.wav");
    return false;
  }
}
