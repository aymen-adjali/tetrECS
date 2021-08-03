package uk.ac.soton.comp1206.component;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.scene.MenuScene;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * KeyBoard class that controls all the user driven events.
 * Enables the user to play the game using only keyboard inputs.
 */
public class Keyboard {

  private static final Logger logger = LogManager.getLogger(Keyboard.class);

  /**
   * The x and y coordinates where the user is currently at.
   */
  private int x;
  private int y;

  private final GameBoard gameBoard;


  /**
   * Returns the x coordinate.
   */
  public int getX() {
    return x;
  }

  /**
   * Returns the y coordinate.
   */
  public int getY() {
    return y;
  }

  /**
   * Instantiates a new Keyboard, sets the initial coordinates to {0,0}.
   *
   * @param gameBoard the game board
   */
  public Keyboard(GameBoard gameBoard) {
    this.gameBoard = gameBoard;
    x = 0;
    y = 0;
  }


  /**
   * Moves the position "up" by decreasing y, if it is within the bounds.
   */
  public void movePositionUp() {
    if (y >= 1) {
      logger.info("Position before moveUp is {{},{}}", x, y);
      y -= 1;
      logger.info("Position after moveUp is {{},{}}", x, y);
    }
  }

  /**
   * Moves the position "down" by increasing y, if it is within the bounds.
   */
  public void movePositionDown() {
    if (y < (gameBoard.getGrid().getRows() - 1)) {
      logger.info("Position before moveDown is {{},{}}", x, y);
      y += 1;
      logger.info("Position after moveDown is {{},{}}", x, y);
    }
  }

  /**
   * Moves the position "left" by decreasing x, if it is within the bounds.
   */
  public void movePositionLeft() {
    if (x > 0) {
      logger.info("Position before moveLeft is {{},{}}", x, y);
      x -= 1;
      logger.info("Position after moveLeft is {{},{}}", x, y);
    }
  }

  /**
   * Moves the position "right" by increasing x, if it is within the bounds.
   */
  public void movePositionRight() {
    if (x < (gameBoard.getGrid().getCols() - 1)) {
      logger.info("Position before moveRight is {{},{}}", x, y);
      x += 1;
      logger.info("Position after moveRight is {{},{}}", x, y);
    }
  }

  /**
   * Move takes in the input of the user in a given scene and controls the hover when using the keyboard to navigate.
   *
   * @param scene
   * @param game
   * @param gameWindow
   */
  public void move(Scene scene, Game game, GameWindow gameWindow) {

    scene.setOnKeyPressed((e) -> {

      //remove hover on current block
      removeHover(getCurrentBlock());

      //ESCAPE to go back to menu
      if (e.getCode() == KeyCode.ESCAPE) {
        game.gameTimer.cancel();
        gameWindow.startMenu();
      }

      //UP or W to move position up
      else if (e.getCode() == KeyCode.W || e.getCode() == KeyCode.UP) {
        movePositionUp();
      }

      //DOWN or S to move position down
      else if (e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.S) {
        movePositionDown();
      }

      //LEFT or A to move position left
      else if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) {
        movePositionLeft();
      }

      //RIGHT or D to move position up
      else if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) {
        movePositionRight();
      }

      //ENTER or X to play current piece at current x,y
      else if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.X) {
        game.blockClicked((gameBoard.getBlock(x, y)));
      }

      //SPACE or R to swap current piece
      else if (e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.R) {
        game.swapCurrentPiece();
      }

      //Q or Z or [ to swap to rotate current piece right
      else if (e.getCode() == KeyCode.Q || e.getCode() == KeyCode.Z || e.getCode() == KeyCode.OPEN_BRACKET) {
        rotatePieceRight(game);
      }

      //E or C or ] to swap to rotate current piece left
      else if (e.getCode() == KeyCode.E || e.getCode() == KeyCode.C || e.getCode() == KeyCode.CLOSE_BRACKET) {
        rotatePieceLeft(game);
      }

      //key with no use
      else {
        logger.info("This key has no use!");
      }

      //add hover to current block
      hoverCurrentBlock(getCurrentBlock());
    });

    //remove the hover on the block which the mouse is hovered on to avoid conflict
    gameBoard.setOnMouseMoved((e) -> {
      gameBoard.getBlock(x, y).setHover(false);
      gameBoard.getBlock(x, y).paint();
    });
  }

  /**
   * Rotates the current piece 3 times (rotate right).
   *
   * @param game Game
   */
  private void rotatePieceRight(Game game) {
    game.rotateCurrentPiece();
    game.rotateCurrentPiece();
    game.rotateCurrentPiece();
  }

  /**
   * Rotates the current piece once.
   *
   * @param game Game
   */
  private void rotatePieceLeft(Game game) {
    game.rotateCurrentPiece();
  }

  /**
   * Sets hover on the current block to true and repaints it.
   *
   * @param currentBlock GameBlock
   */
  private void hoverCurrentBlock(GameBlock currentBlock) {
    currentBlock.setHover(true);
    currentBlock.paint();
    logger.info("Just added hover to the block: {{}{}}", getX(), getY());
  }

  /**
   * Sets hover on the current block to false and repaints it.
   *
   * @param currentBlock GameBlock
   */
  private void removeHover(GameBlock currentBlock) {
    currentBlock.setHover(false);
    currentBlock.paint();
    logger.info("Just removed hover to the block: {{}{}}", getX(), getY());
  }

  /**
   * Returns the current gameBlock
   *
   * @return GameBlock
   */
  private GameBlock getCurrentBlock() {
    GameBlock currentBlock = gameBoard.getBlock(getX(), getY());
    return currentBlock;
  }
}
