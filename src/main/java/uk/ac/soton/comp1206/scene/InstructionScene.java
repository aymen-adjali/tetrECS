package uk.ac.soton.comp1206.scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;

/**
 * The Instruction scene.
 * Holds the controls for the user to be able to learn how to play, and a short
 * overview of the game.
 * Accessible from the menu scene.
 */
public class InstructionScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(InstructionScene.class);

  /**
   * Create a new Instruction scene
   *
   * @param gameWindow the Game Window
   */
  public InstructionScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Instruction Scene");
  }

  /**
   * Build the Instructions window
   */
  @Override
  public void build() {

    logger.info("Building " + this.getClass().getName());

    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    var instructionPane = new StackPane();
    instructionPane.setMaxWidth(gameWindow.getWidth());
    instructionPane.setMaxHeight(gameWindow.getHeight());
    instructionPane.getStyleClass().add("menu-background");
    root.getChildren().add(instructionPane);

    var mainPane = new BorderPane();
    instructionPane.getChildren().add(mainPane);

    //title
    var instructionsTitle = new Text("Instructions");
    instructionsTitle.getStyleClass().add("title");

    //Game pieces title
    var gamePieces = new Text("Game Pieces");
    gamePieces.getStyleClass().add("title");

    //text
    var text = new Text("TetrECS is a fast-paced gravity-free block placement game, where you must survive by clearing rows through careful placement of  \n" + "the upcoming blocks before the time runs out. Lose all 3 lives and you're destroyed");
    text.getStyleClass().add("instructions");
    text.setTextAlignment(TextAlignment.CENTER);

    //image
    var image = new ImageView(new Image(this.getClass().getResource("/images/Instructions.png").toExternalForm()));
    image.setPreserveRatio(true);
    image.setFitWidth(470);

    //vBox for buttons
    var vbox = new VBox();
    mainPane.setTop(vbox);

    //adding nodes to the vBox
    vbox.getChildren().add(instructionsTitle);
    vbox.getChildren().add(text);
    vbox.getChildren().add(image);
    vbox.getChildren().add(gamePieces);
    vbox.getChildren().add(get15pieces());

    vbox.setAlignment(Pos.CENTER);
  }

  /**
   * Returns a vBox with 3 columns
   */
  private VBox get15pieces() {
    VBox pieces_15 = new VBox();

    var row1 = makeLine(1);
    var row2 = makeLine(2);
    var row3 = makeLine(3);

    pieces_15.getChildren().addAll(row1, row2, row3);
    pieces_15.setAlignment(Pos.BOTTOM_CENTER);
    pieces_15.setSpacing(gameWindow.getWidth() / 100);

    return pieces_15;
  }

  /**
   * Returns a hBox filled with a row of 5 pieceBoards with different gamePieces displayed on them.
   */
  private HBox makeLine(int number) {
    HBox row = new HBox();
    //only 5 pieceBoards will be created
    for (int i = (5 * (number - 1)); i < (5 * number); i += 1) {
      //making sure a different piece is displayed
      GamePiece piece = GamePiece.createPiece(i);
      PieceBoard pieceBoard = new PieceBoard(piece, gameWindow.getHeight() / 10, gameWindow.getHeight() / 10, false);

      row.getChildren().add(pieceBoard);
      row.setAlignment(Pos.CENTER);
      row.setSpacing(gameWindow.getWidth() / 100);
    }
    return row;
  }

  /**
   * Initialise the scene and start the game
   */
  @Override
  public void initialise() {
    logger.info("Initialising Instructions");

    //listener to handle the ESC key press, returns user to the menu
    scene.setOnKeyPressed((e) -> {
      if (e.getCode() != KeyCode.ESCAPE) return;
      logger.info("Going back to Menu");
      gameWindow.startMenu();
    });
  }
}

