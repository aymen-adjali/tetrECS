package uk.ac.soton.comp1206.scene;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;

/**
 * The Intro is to play a "loading" screen, which plays an animation and then takes the user to the menu.
 */
public class IntroScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(IntroScene.class);

  private ImageView escIntro;
  private FadeTransition fadeTransition;

  /**
   * Create a new Intro scene
   *
   * @param gameWindow the Game Window
   */
  public IntroScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Intro Scene");
  }

  /**
   * Build the Challenge window
   */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());

    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    var introPane = new StackPane();
    introPane.setMaxWidth(gameWindow.getWidth());
    introPane.setMaxHeight(gameWindow.getHeight());
    introPane.getStyleClass().add("intro");
    root.getChildren().add(introPane);

    //Add the ECS Logo
    escIntro = new ImageView(new Image(this.getClass().getResource("/images/ECSGames.png").toExternalForm()));
    escIntro.setPreserveRatio(true);
    escIntro.setFitWidth(450);
    introPane.getChildren().add(escIntro);

    //make the logo fade in
    animateLogo();
  }

  /**
   * Animate the ecs logo to fade in for 6 seconds.
   */
  public void animateLogo() {
    fadeTransition = new FadeTransition(Duration.millis(6000), escIntro);
    fadeTransition.setFromValue(0.0);
    fadeTransition.setToValue(1.0);
    fadeTransition.play();
  }

  /**
   * Initialise the scene and start the game
   */
  @Override
  public void initialise() {

    logger.info("Initialising Intro");
    Multimedia.playMusic("sounds/intro.mp3");

    //move to menu after 6 seconds when animation is finished
    fadeTransition.setOnFinished(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        gameWindow.startMenu();
      }
    });
  }
}

