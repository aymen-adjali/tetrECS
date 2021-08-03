package uk.ac.soton.comp1206.scene;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;

import java.io.File;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 * Options for single Player game, instructions, *multiplayer* and exit are here.
 */
public class MultiplayerScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(MultiplayerScene.class);
  private ImageView gif;

  public MultiplayerScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Multiplayer Scene");
  }

  /**
   * Build the multiplayer layout
   */
  @Override
  public void build() {

    logger.info("Building " + this.getClass().getName());

    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    var menuPane = new StackPane();
    menuPane.setMaxWidth(gameWindow.getWidth());
    menuPane.setMaxHeight(gameWindow.getHeight());
    menuPane.getStyleClass().add("menu-background");
    root.getChildren().add(menuPane);

    var mainPane = new BorderPane();
    menuPane.getChildren().add(mainPane);

    var centerPane = new BorderPane();

    mainPane.setCenter(centerPane);

    //vBox for options
    var vbox = new VBox();
    mainPane.setBottom(vbox);
    vbox.setPadding(new Insets(10, 50, 50, 20));
    vbox.setSpacing(10);
    vbox.setAlignment(Pos.BOTTOM_CENTER);
    centerPane.setCenter(vbox);

    //title
    gif = new ImageView(new Image(this.getClass().getResource("/gifs/construction4.gif").toExternalForm()));
    gif.setPreserveRatio(true);
    gif.setFitWidth(550);
    vbox.getChildren().add(gif);

    //adding text options
    var exitOption = menuTextButton("Back To Menu");
    vbox.getChildren().add(exitOption);

    exitOption.setOnMouseClicked((e) -> {
      startMenuScene();
    });
  }

  /**
   * Initialise the menu
   */
  @Override
  public void initialise() {

    //listener to handle the ESC key press, exits the game
    scene.setOnKeyPressed((e) -> {
      if (e.getCode() != KeyCode.ESCAPE) return;
      gameWindow.startMenu();
    });
  }

  /**
   * Handle when the Back to Menu button is pressed
   */
  private void startMenuScene() {
    gameWindow.startMenu();
  }

  /**
   * Method to create menu buttons.
   */
  public Text menuTextButton(String name) {
    Text textButton = new Text(name);
    textButton.getStyleClass().add("menuItem");
    textButton.setTextAlignment(TextAlignment.CENTER);
    return textButton;
  }
}
