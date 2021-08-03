package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.LineClearedListener;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.event.GameLivesListener;
import uk.ac.soton.comp1206.utility.Multimedia;

import java.util.*;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

  private static final Logger logger = LogManager.getLogger(Game.class);

  /**
   * Simple Integer Properties so game variables can be bound to UI components.
   */
  private final IntegerProperty score = new SimpleIntegerProperty(0);
  private final IntegerProperty level = new SimpleIntegerProperty(0);
  private final IntegerProperty lives = new SimpleIntegerProperty(1);
  private final IntegerProperty multiplier = new SimpleIntegerProperty(1);

  private final ArrayList<Pair<String, Integer>> scoresList = new ArrayList<>();
  private int scoreToAdd;

  /**
   * Get scores array list.
   *
   * @return the array list
   */
  public ArrayList<Pair<String, Integer>> getTheScores() {
    return scoresList;
  }

  private GamePiece currentPiece;
  private GamePiece followingPiece;

  /**
   * Listener fields.
   */
  private NextPieceListener nextPieceListener;
  private LineClearedListener lineClearedListener;
  private GameLoopListener gameLoopListener;
  private GameLivesListener gameLivesListener;

  /**
   * Initial timer is 12000ms.
   */
  private int timerDelay = 12000;
  public Timer gameTimer;

  /**
   * Number of rows
   */
  protected final int rows;

  /**
   * Number of columns
   */
  protected final int cols;

  /**
   * The grid model linked to the game
   */
  protected final Grid grid;

  /**
   * Create a new game with the specified rows and columns. Creates a corresponding grid model.
   *
   * @param cols number of columns
   * @param rows number of rows
   */
  public Game(int cols, int rows) {
    this.cols = cols;
    this.rows = rows;

    //Create a new grid model to represent the game state
    this.grid = new Grid(cols, rows);

    followingPiece = spawnPiece();
    currentPiece = spawnPiece();
  }

  /**
   * Methods to initialise the listeners.
   */
  public void setNextPieceListener(NextPieceListener listener) {
    this.nextPieceListener = listener;
  }

  public void setLineClearedListener(LineClearedListener listener) {
    this.lineClearedListener = listener;
  }

  public void setGameLoopListener(GameLoopListener listener) {
    this.gameLoopListener = listener;
  }

  public void setGameLivesListener(GameLivesListener listener) {
    this.gameLivesListener = listener;
  }

  /**
   * Start the game
   */
  public void start() {
    logger.info("Starting game");
    initialiseGame();
  }

  /**
     * Create a timer which uses the timerDelay member variable as its parameter.
   */
  public void createTimer() {
    TimerTask task = new TimerTask() {
      public void run() {
        //calls gameLoop method each time
        gameLoop();
      }
    };
    gameTimer = new Timer("Timer");
    setTimerDelay(getLevel());
    gameTimer.schedule(task, timerDelay);
  }

  /**
   * Initialise a new game and set up anything that needs to be done at the start
   */
  public void initialiseGame() {
    logger.info("Initialising game");

    //creates a new timer for the start of the game
    createTimer();
  }

  /**
   * Handle what should happen when a particular block is clicked
   *
   * @param gameBlock the block that was clicked
   */
  public void blockClicked(GameBlock gameBlock) {
    //Get the position of this block
    int x = gameBlock.getX();
    int y = gameBlock.getY();

    //only call afterPiece() and nextPiece() method if the block was actually placed.
    if (grid.playPiece(currentPiece, x, y) == true) {
      afterPiece();
      nextPiece();
    }
  }

  /**
   * Get the grid model inside this game representing the game state of the board
   *
   * @return game grid model
   */
  public Grid getGrid() {
    return grid;
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
   *
   * @return the current piece
   */
  public GamePiece getCurrentPiece() {
    return currentPiece;
  }

  /**
   *
   * @return the following piece
   */
  public GamePiece getFollowingPiece() {
    return followingPiece;
  }

  /**
   * Spawns a random game piece with a random rotation.
   *
   * @return the game piece
   */
  public GamePiece spawnPiece() {
    logger.info("Spawning a random piece...");
    Random random = new Random();
    int randomNum = random.nextInt(15);
    int rotations = random.nextInt(4);
    return GamePiece.createPiece(randomNum, rotations);
  }

  /**
   * Called after a piece is played, to randomly spawn a new piece.
   */
  public void nextPiece() {
    GamePiece nextPiece = spawnPiece();
    currentPiece = followingPiece;
    followingPiece = nextPiece;

    //update listener with the new pieces
    nextPieceListener.onNextPiece(currentPiece, followingPiece);

    //creating a new timer when a piece is correctly played in time
    createTimer();
  }

  /**
   * After every piece is played, this method will check if there are any
   * new full columns or rows, and then clears them.
   * <p>
   * It calculates the number of lines and number of blocks which will be cleared, and increases
   * the score, level and multiplier accordingly.
   * <p>
   * Informs the lineClearedListener to invoke the clearLine animation.
   * <p>
   * Plays audio if at least one line was cleared.
   */
  public void afterPiece() {

    //using other methods to see if there are any full columns and rows
    List<Integer> column = findFullColumns();
    List<Integer> rows = findFullRows();

    //Hashset will be used to store the full block coordinates,
    //and will be used to call the fadeOut() method on them using the listener
    Set<int[]> coordinates = new HashSet<int[]>();

    //instantiating iterators which will be used to clear the full lines
    Iterator<Integer> columnIterator = column.iterator();
    Iterator<Integer> rowsIterator = rows.iterator();

    //calculating the number of lines and blocks which have been cleared
    int numberOfLines = column.size() + rows.size();
    int numberOfBlocks = (((findFullRows().size() * getCols()) + (findFullColumns().size() * getRows())) - (findFullColumns().size() * findFullRows().size()));

    //clear all full columns, and add the block coordinates to the coordinates set
    while (columnIterator.hasNext()) {
      Integer next = columnIterator.next();
      clearColumn(next);
      for (int i = 0; i < getRows(); i++) {
        int[] pair = new int[2];
        pair[0] = next;
        pair[1] = i;
        coordinates.add(pair);
      }
    }

    //clear all full rows, and add the block coordinates to the coordinates set
    while (rowsIterator.hasNext()) {
      Integer next = rowsIterator.next();
      clearRow(next);
      for (int i = 0; i < getCols(); i++) {
        int[] pair = new int[2];
        pair[0] = i;
        pair[1] = next;
        coordinates.add(pair);
      }
    }

    //inform the lineClearedListener that these blocks have been cleared
    lineClearedListener.onLineListener(coordinates);

    //add to score if any lines were cleared
    score(numberOfLines, numberOfBlocks);

    //check if level needs to be increased
    setLevel(Math.floorDiv(getScore(), 1000));

    //increase or reset multiplier and play sound effect according to if any lines were full
    if (numberOfLines == 0) {
      resetMultiplier();
    } else {
      Multimedia.playAudio("sounds/clear.wav");
      increaseMultiplier();
    }

    //cancel the timer after a piece has been played.
    gameTimer.cancel();
  }

  /**
   * Find all full columns and adds them to a list.
   *
   * @return the list
   */
  public ArrayList<Integer> findFullColumns() {

    ArrayList<Integer> fullColumns = new ArrayList<>();

    for (int i = 0; i < grid.getCols(); i++) {
      boolean emptyBlock = false;
      for (int x = 0; x < grid.getRows(); x++) {
        if (grid.get(i, x) == 0) {
          emptyBlock = true;
          logger.info("Column wasn't full!");
          break;
        }
      }
      if (emptyBlock == false) {
        logger.info("Column at index: {} was full!", i);
        fullColumns.add(i);
      }
    }
    return fullColumns;
  }

  /**
   * Find all full rows and adds them to a list.
   *
   * @return the list
   */

  public List<Integer> findFullRows() {

    ArrayList<Integer> fullRows = new ArrayList<>();

    for (int i = 0; i < grid.getRows(); i++) {
      boolean emptyBlock = false;
      for (int x = 0; x < grid.getCols(); x++) {
        if (grid.get(x, i) == 0) {
          emptyBlock = true;
          logger.info("Row wasn't full!");
          break;
        }
      }
      if (emptyBlock == false) {
        logger.info("Row at index: {} was full!", i);
        fullRows.add(i);
      }
    }
    return fullRows;
  }

  /**
   * Remove all blocks on the column specified by index.
   *
   * @param index the index of the line which will needs to be cleared
   */
  public void clearColumn(int index) {
    for (int i = 0; i < grid.getRows(); i++) {
      grid.set(index, i, 0);
    }
  }

  /**
   * Remove all blocks on the row specified by index.
   *
   * @param index the index of the line which will needs to be cleared
   */
  public void clearRow(int index) {
    for (int i = 0; i < grid.getCols(); i++) {
      grid.set(i, index, 0);
    }
  }

  /**
   * accessor methods for score.
   */
  public IntegerProperty scoreProperty() {
    return score;
  }

  public void setScore(Integer score) {
    logger.info("Score has been set to:{}", score);
    scoreProperty().set(score);
  }

  public Integer getScore() {
    return scoreProperty().get();
  }

  /**
   * accessor methods for level.
   */
  public IntegerProperty levelProperty() {
    return level;
  }

  public void setLevel(Integer level) {
    logger.info("Level has been set to:{}", level);
    levelProperty().set(level);
  }

  public Integer getLevel() {
    return levelProperty().get();
  }

  /**
   * accessor methods for lives.
   */
  public IntegerProperty livesProperty() {
    return lives;
  }

  public void setLives(Integer lives) {
    logger.info("Lives have been set to:{}", lives);
    livesProperty().set(lives);
  }

  public Integer getLives() {
    return livesProperty().get();
  }

  public void decreaseLives() {
    setLives(getLives() - 1);
  }

  /**
   * accessor methods for multiplier.
   */
  public IntegerProperty multiplierProperty() {
    return multiplier;
  }

  public void setMultiplier(Integer multiplier) {
    logger.info("Multiplier has been set to:{}", multiplier);
    multiplierProperty().set(multiplier);
  }

  public Integer getMultiplier() {
    return multiplierProperty().get();
  }

  /**
   * Adds to current score when lines are cleared.
   * Increase depends on number of lines cleared, number of blocks cleared
   * and multiplier.
   *
   * @param numberOfLines  the number of lines
   * @param numberOfBlocks the number of blocks
   */
  public void score(int numberOfLines, int numberOfBlocks) {
    //only add score if lines were cleared
    if (numberOfLines != 0) {
      scoreToAdd = (numberOfLines * numberOfBlocks * 10 * getMultiplier());
      setScore(getScore() + scoreToAdd);
    }
  }

  /**
   * increase the multiplier by one.
   */
  public void increaseMultiplier() {
    logger.info("Multiplier went from {} to...!", this::getMultiplier);
    setMultiplier(getMultiplier() + 1);
  }

  /**
   * reset the multiplier to 0.
   */
  public void resetMultiplier() {
    if (getMultiplier() == 1) {
      logger.info("Multiplier was already one... no need to decrease");
      logger.info("Multiplier = {}", this::getMultiplier);
    } else {
      logger.info("Multiplier decreased from {} to 1!", this::getMultiplier);
      setMultiplier(1);
    }
  }

  /**
   * Method to rotate current piece, informs the nextPieceListener and plays audio.
   */
  public void rotateCurrentPiece() {
    logger.info("{} piece has been rotated", currentPiece.toString());
    currentPiece.rotate();

    Multimedia.playAudio("sounds/rotate.wav");
    //update listener with new piece rotation
    nextPieceListener.onNextPiece(currentPiece, followingPiece);
  }

  /**
   * Swaps the current piece and following piece, by taking the type of pieces and their current rotation.
   * informs the nextPieceListener and plays audio.
   */
  public void swapCurrentPiece() {
    logger.info("Swapping {} with {}", currentPiece.toString(), followingPiece.toString());
    //making a temporary piece and giving it the same characteristics as currentPiece
    GamePiece temp = GamePiece.createPiece(currentPiece.getValue() - 1, currentPiece.getRotations());
    currentPiece = GamePiece.createPiece(followingPiece.getValue() - 1, currentPiece.getRotations());
    followingPiece = temp;

    Multimedia.playAudio("sounds/pling.wav");

    //update listener with the new swapped pieces
    nextPieceListener.onNextPiece(currentPiece, followingPiece);
  }

  /**
   * Accessor methods for timerDelay.
   */
  public int getTimerDelay() {
    return timerDelay;
  }

  /**
   * Sets the timerDelay to the correct value using (12000 - 500 * the current level).
   * If the timer is being reset and the level has not increased, the timerDelay will stay constant.
   */
  public void setTimerDelay(int level) {
    timerDelay = 12000 - (500 * level);
    logger.info("{} is now ", timerDelay);
    if (timerDelay < 2500) {
      timerDelay = 2500;
      logger.info("Delay was less then 2500");
    }

    logger.info("TimerDelay has been set to:{}", timerDelay);

    //update the listener with the new timerDelay value
    gameLoopListener.onGameLoop(timerDelay);
  }

  /**
   * When the timer runs out, the game moves on to the next piece and a life will be lost.
   */
  public void gameLoop() {
    Multimedia.playAudio("sounds/lifelose.wav");
    if (getLives() > -1) {
      //move on to nextPiece and lose a life
      nextPiece();
      decreaseLives();

      if (getLives() == -1) {
        //update listener if the lives have reached 0
        gameLivesListener.onLivesLost(getLives());
        //stop the timer
        gameTimer.cancel();
      }
      //reset multiplier to 0 as the user lost their streak
      resetMultiplier();
    }
  }
}
