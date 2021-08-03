package uk.ac.soton.comp1206.scene;

import javafx.animation.*;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Slider;
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

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 * Options for single Player game, instructions, *multiplayer* and exit are here.
 */
public class MenuScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(MenuScene.class);
  private ImageView title;
  private Slider volumeSlider;

  /**
   * Create a new menu scene
   *
   * @param gameWindow the Game Window this will be displayed in
   */
  public MenuScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Menu Scene");
  }

  /**
   * Build the menu layout
   */
  @Override
  public void build() {

    Multimedia.playMusic("music/menu.mp3");

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

    //title
    title = new ImageView(new Image(this.getClass().getResource("/images/TetrECS.png").toExternalForm()));
    title.setPreserveRatio(true);
    title.setFitWidth(600);
    centerPane.setCenter(title);

    //vBox for options
    var vbox = new VBox();
    mainPane.setBottom(vbox);
    vbox.setPadding(new Insets(10, 50, 50, 20));
    vbox.setSpacing(10);
    vbox.setAlignment(Pos.BASELINE_CENTER);
    centerPane.setBottom(vbox);

    //adding text options
    var singlePlayerOption = menuTextButton("Single Player");
    vbox.getChildren().add(singlePlayerOption);

    var multiPlayerOption = menuTextButton("Multi Player");
    vbox.getChildren().add(multiPlayerOption);

    var howToPlayOption = menuTextButton("How to Play");
    vbox.getChildren().add(howToPlayOption);

    var exitOption = menuTextButton("Exit");
    vbox.getChildren().add(exitOption);

    //creating the timer and max width
    var sliderHbox = new HBox();
    volumeSlider = new Slider();
    volumeSlider.setMaxWidth(gameWindow.getWidth() / 3);

    Text volumeText = new Text("Music Volume");
    volumeText.setTextAlignment(TextAlignment.CENTER);
    volumeText.getStyleClass().add("smallHeading");

    sliderHbox.getChildren().add(volumeText);
    sliderHbox.getChildren().add(volumeSlider);

    sliderHbox.setAlignment(Pos.CENTER);
    sliderHbox.setPadding(new Insets(50, 0, 0, 0));
    vbox.getChildren().add(sliderHbox);

    //adding events when each text option is clicked
    singlePlayerOption.setOnMouseClicked((e) -> {
      startGame();
    });

    howToPlayOption.setOnMouseClicked((e) -> {
      startInstruction();
    });

    exitOption.setOnMouseClicked((e) -> {
      exitGame();
    });

    multiPlayerOption.setOnMouseClicked((e) -> {
      multiPlayer();
    });

  }

  /**
   * Initialise the menu
   */
  @Override
  public void initialise() {

    rotation();

    //listener to handle the ESC key press, exits the game
    scene.setOnKeyPressed((e) -> {
      if (e.getCode() != KeyCode.ESCAPE) return;
      gameWindow.shutdown();
    });

    volumeSlider.setValue(Multimedia.musicPlayer.getVolume() * 100);
    volumeSlider.valueProperty().addListener(new InvalidationListener() {
      @Override
      public void invalidated(Observable observable) {
        Multimedia.musicPlayer.setVolume(volumeSlider.getValue() / 100);
      }
    });
  }

  /**
   * Handle when the Single Player button is pressed
   */
  private void startGame() {
    Multimedia.stopMusic();
    Multimedia.playMusic("music/game.wav");
    gameWindow.startChallenge();
  }

  /**
   * Handle when the How to Play button is pressed
   */
  private void startInstruction() {
    gameWindow.startInstructions();
  }

  /**
   * Handle when the Exit button is pressed
   */
  private void exitGame() {
    gameWindow.shutdown();
  }

  /**
   * Handle when the Multiplayer button is pressed
   */
  private void multiPlayer() {
    gameWindow.startMultiPlayer();
  }


  /**
   * Animate TETRECS logo to rotate about its pivot indefinitely.
   */
  public void rotation() {
    RotateTransition rotateTransition = new RotateTransition(Duration.seconds(3), title);
    rotateTransition.setFromAngle(-7);
    rotateTransition.setToAngle(7);
    rotateTransition.setAutoReverse(true);
    rotateTransition.setCycleCount(Animation.INDEFINITE);

    rotateTransition.play();
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
