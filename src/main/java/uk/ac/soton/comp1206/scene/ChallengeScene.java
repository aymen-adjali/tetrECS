package uk.ac.soton.comp1206.scene;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HorizontalDirection;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.*;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;
import uk.ac.soton.comp1206.utility.ScoreFiles;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(ChallengeScene.class);

  protected Game game;

  private GameBoard mainGameBoard;

  //keyboard object to control all the user inputs
  private Keyboard keyboard;

  //small gameBoards to hold the current and upcoming pieces
  private PieceBoard currentPieceBoard;
  private PieceBoard upcomingPieceBoard;

  private CheckBox mute;

  private Slider volumeSlider;

  private final IntegerProperty highScore = new SimpleIntegerProperty();

  /**
   * Create a new Single Player challenge scene
   *
   * @param gameWindow the Game Window
   */
  public ChallengeScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Challenge Scene");
  }

  /**
   * Build the Challenge window
   */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());

    setupGame();

    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    StackPane challengePane = new StackPane();
    challengePane.setMaxWidth(gameWindow.getWidth());
    challengePane.setMaxHeight(gameWindow.getHeight());
    challengePane.getStyleClass().add("menu-background");
    root.getChildren().add(challengePane);

    //adding borderPanes to place corresponding areas in the mainPain to allow for better positioning
    var mainPane = new BorderPane();
    var leftPane = new BorderPane();
    var topPane = new BorderPane();
    var rightPane = new BorderPane();
    var bottomPane = new BorderPane();
    var centrePane = new BorderPane();

    //placing the panes in the mainPain
    mainPane.setLeft(leftPane);
    mainPane.setTop(topPane);
    mainPane.setRight(rightPane);
    mainPane.setBottom(bottomPane);
    mainPane.setCenter(centrePane);

    //creating vBox to hold the highscore, level and pieceBoards
    var rightVbox = new VBox();
    rightPane.setCenter(rightVbox);
    rightVbox.setAlignment(Pos.CENTER);
    rightVbox.setPadding(new Insets(0, 30, 0, 0));

    //creating vBox to hold the settings
    var leftVbox = new VBox();
    leftPane.setCenter(leftVbox);
    leftVbox.setAlignment(Pos.CENTER);
    leftVbox.setPadding(new Insets(0, 30, 0, 0));

    challengePane.getChildren().add(mainPane);

    //Add a title
    var challengeModeTitle = new Text("Challenge Mode");
    challengeModeTitle.getStyleClass().add("title");
    topPane.setCenter(challengeModeTitle);

    //Add a VBox to fill with score and level
    var vBoxScores_Slider_Check = new VBox();
    var vBoxLives = new VBox();

    var scoreLabel = new Text();
    var scoreTitle = new Text("Score");

    var levelLabel = new Text();
    var levelTitle = new Text("Level");

    var livesLabel = new Text();
    var livesTitle = new Text("Lives");

    var highScoreLabel = new Text("0");
    var highScoreTitle = new Text("High Score");

    //adding style and binding the score label to the correct game property
    scoreLabel.getStyleClass().add("score");
    scoreTitle.getStyleClass().add("heading");
    scoreLabel.textProperty().bind(game.scoreProperty().asString());
    vBoxScores_Slider_Check.getChildren().add(scoreTitle);
    vBoxScores_Slider_Check.getChildren().add(scoreLabel);

    //adding style and binding the lives label to the correct game property
    livesLabel.getStyleClass().add("lives");
    livesTitle.getStyleClass().add("heading");
    livesLabel.textProperty().bind(game.livesProperty().asString());
    vBoxLives.getChildren().add(livesTitle);
    vBoxLives.getChildren().add(livesLabel);

    //adding style and binding the level label to the correct game property
    levelLabel.getStyleClass().add("level");
    levelTitle.getStyleClass().add("heading");
    levelLabel.textProperty().bind(game.levelProperty().asString());

    //adding style and binding the highscore label to the correct game property
    highScoreLabel.getStyleClass().add("level");
    highScoreTitle.getStyleClass().add("heading");
    highScoreLabel.textProperty().bind(this.highScore.asString());

    //add scoreboards to the challengePane
    topPane.setLeft(vBoxScores_Slider_Check);
    vBoxScores_Slider_Check.setAlignment(Pos.TOP_CENTER);
    vBoxScores_Slider_Check.setPadding(new Insets(10, 10, 10, 10));

    topPane.setRight(vBoxLives);
    vBoxLives.setAlignment(Pos.TOP_CENTER);
    vBoxLives.setPadding(new Insets(10, 10, 10, 10));

    //add highscore to vbox
    rightVbox.getChildren().add(highScoreTitle);
    rightVbox.getChildren().add(highScoreLabel);

    //Add level to vbox
    rightVbox.getChildren().add(levelTitle);
    rightVbox.getChildren().add(levelLabel);

    //add incoming text
    var upcomingPiece = new Text("Incoming");
    upcomingPiece.getStyleClass().add("heading");
    rightVbox.getChildren().add(upcomingPiece);

    BorderPane paneInsideRightVBox1 = new BorderPane();
    rightVbox.getChildren().add(paneInsideRightVBox1);

    BorderPane paneInsideRightVBox2 = new BorderPane();
    rightVbox.getChildren().add(paneInsideRightVBox2);

    //add timer
    var vBoxInsideBottomPane = new VBox();
    var timerBar = new Rectangle(0, gameWindow.getWidth() + 2, gameWindow.getWidth() / 1.2,
        gameWindow.getHeight() / 28);
    //Border
    timerBar.setFill(Color.GREEN);

    //add VBox for the mute check box and the toggle audio text
    var toggleAudioAndCheckBox = new VBox();

    //Add a mute checkbox
    mute = new CheckBox();
    mute.selectedProperty().bindBidirectional(Multimedia.audioEnabledProperty());

    var toggleAudio = new Text("Toggle Audio");
    toggleAudio.setTextAlignment(TextAlignment.CENTER);
    toggleAudio.getStyleClass().add("smallHeading");

    toggleAudioAndCheckBox.getChildren().add(toggleAudio);
    toggleAudioAndCheckBox.getChildren().add(mute);

    toggleAudioAndCheckBox.setAlignment(Pos.TOP_CENTER);
    toggleAudioAndCheckBox.setPadding(new Insets(0, 0, 0,40));
    //add the nodes to the vBox
    leftVbox.getChildren().add(toggleAudioAndCheckBox);

    //add VBox for the mute check box and the toggle audio text
    var musicVolumeAndSlider = new VBox();

    //add a slider for the volume
    volumeSlider = new Slider();
    volumeSlider.setOrientation(Orientation.HORIZONTAL);
    volumeSlider.setMaxWidth(gameWindow.getWidth() / 2);

    var musicVolume = new Text("Music Volume");
    musicVolume.setTextAlignment(TextAlignment.CENTER);
    musicVolume.getStyleClass().add("smallHeading");

    musicVolumeAndSlider.getChildren().add(musicVolume);
    musicVolumeAndSlider.getChildren().add(volumeSlider);

    musicVolumeAndSlider.setPadding(new Insets(0, 0, 0,40));
    musicVolumeAndSlider.setAlignment(Pos.TOP_CENTER);
    //add the nodes to the vBox
    leftVbox.getChildren().add(musicVolumeAndSlider);

    //initialising the current piece board with the current piece being displayed
    currentPieceBoard = new PieceBoard(game.getCurrentPiece(), gameWindow.getWidth() / 6, gameWindow.getWidth() / 6, true);
    paneInsideRightVBox1.setCenter(currentPieceBoard);
    currentPieceBoard.setPadding(new Insets(10, 0, 0, 0));

    //initialising the upcoming piece board with the following piece being displayed
    upcomingPieceBoard = new PieceBoard(game.getFollowingPiece(), gameWindow.getWidth() / 8, gameWindow.getWidth() / 8, false);
    paneInsideRightVBox2.setCenter(upcomingPieceBoard);
    upcomingPieceBoard.setPadding(new Insets(10, 0, 0, 0));

    //add the timer to the bottom pane
    vBoxInsideBottomPane.getChildren().add(timerBar);
    vBoxInsideBottomPane.setAlignment(Pos.BOTTOM_LEFT);
    vBoxInsideBottomPane.setPadding(new Insets(0, 0, 3, 0));
    bottomPane.setCenter(vBoxInsideBottomPane);

    //add game board
    mainGameBoard = new GameBoard(game.getGrid(), gameWindow.getWidth() / 2, gameWindow.getWidth() / 2);
    centrePane.setCenter(mainGameBoard);

    //initialising the keyboard with the gameBoard as a parameter
    keyboard = new Keyboard(mainGameBoard);

    //Handle block on gameBoard grid being clicked
    mainGameBoard.setOnBlockClick(this::blockClicked);

    //calling the correct methods with the new pieces
    game.setNextPieceListener((gamePiece, followingPiece) -> {
      receiveGamePiece(gamePiece);
      receiveNextGamePiece(followingPiece);
    });

    //calling the correct method to with the blocks that need to be cleared
    game.setLineClearedListener(this::receiveCoordinatesToClear);

    //calling the correct method to create a new timer with the correct time
    game.setGameLoopListener((timer) -> {
      Platform.runLater(() ->
          timer(timerBar, game.getTimerDelay()));
    });

    //calling the correct method with the number of lives
    game.setGameLivesListener(this::receiveNumberOfLives);

    //calling the rotate method when the currentPieceBoard is left clicked
    currentPieceBoard.setOnBlockClick((e) -> {
      game.rotateCurrentPiece();
    });

    //calling the swap method when the upcomingPieceBoard is left clicked
    upcomingPieceBoard.setOnBlockClick((e) -> {
      game.swapCurrentPiece();
    });

    //calling the rotate method when the mainGameBoard is right clicked
    mainGameBoard.setOnRightClick((e) -> {
      game.rotateCurrentPiece();
    });
  }

  /**
   * Handle when a block is clicked
   *
   * @param gameBlock the Game Block that was clocked
   */
  private void blockClicked(GameBlock gameBlock) {
    game.blockClicked(gameBlock);
  }

  /**
   * Setup the game object and model
   */
  public void setupGame() {
    logger.info("Starting a new challenge");
    //Start new game
    game = new Game(5, 5);
  }

  /**
   * Initialise the scene and start the game
   */
  @Override
  public void initialise() {
    logger.info("Initialising Challenge");
    game.start();

    //set up keyboard with correct parameters
    keyboard.move(scene, game, gameWindow);

    //add listener for the score value, getting the last value in the local scores.txt file
    //making it the highest score
    game.scoreProperty().addListener(this::setScore);
    ArrayList<Pair<String, Integer>> scores = ScoreFiles.loadScores();
    highScore.set((Integer) ((Pair) scores.get(0)).getValue());

    volumeSlider.setValue(Multimedia.musicPlayer.getVolume() * 100);
    volumeSlider.valueProperty().addListener(new InvalidationListener() {
      @Override
      public void invalidated(Observable observable) {
        Multimedia.musicPlayer.setVolume(volumeSlider.getValue() / 100);
      }
    });
  }

  /**
   * Receive a gamePiece and display it on the current board.
   */
  public void receiveGamePiece(GamePiece piece) {
    logger.info("Receiving the next piece {}", piece.toString());
    currentPieceBoard.setPieceToDisplay(piece);
  }

  /**
   * Receive a gamePiece next and display it on the upcoming board.
   */
  public void receiveNextGamePiece(GamePiece piece) {
    logger.info("Receiving the Following piece {}", piece.toString());
    upcomingPieceBoard.setPieceToDisplay(piece);
  }

  /**
   * Receive coordinates that need to be cleared fade them out.
   */
  public void receiveCoordinatesToClear(Set<int[]> setOfCoordinates) {
    logger.info("Receiving the set of coordinates to clear");
    mainGameBoard.fadeOut(setOfCoordinates);
  }

  /**
   * Receive the number of lives, if it is zero then end the game.
   */
  public void receiveNumberOfLives(int lives) {
    if (lives == -1) {
      startScore();
    }
  }

  /**
   * Animates a timer with colours from green to red according to the time value passed to it.
   * The closer the timer is too finishing the the more "urgent" the colour will become
   *
   * @param timer       a rectangle which will be animated
   * @param initialTime the initial time for the timer
   */
  public void timer(Rectangle timer, int initialTime) {

    double calender = Calendar.getInstance().getTimeInMillis();
    double ratio = 1;

    //setting the timer width
    timer.setWidth(ratio * gameWindow.getWidth());

    //AnimationTimer class
    AnimationTimer animationTimer = new AnimationTimer() {
      double timeLeft = initialTime;
      int red;
      int green;
      int blue;

      @Override
      public void handle(long l) {
        //if the time has not run out yet
        if (timeLeft > 0) {
          Calendar currentCalender = Calendar.getInstance();
          timeLeft = initialTime - (currentCalender.getTimeInMillis() - calender);
          double ratio = timeLeft / initialTime;
          timer.setWidth(ratio * gameWindow.getWidth());

          //change the value of the RGB values linearly according to the ratio value
          if (ratio > 0.9) {
            green = 128;
          } else if (ratio <= 0.9 && ratio > 0.5) {
            green = (int) Math.floor(-317.5 * ratio + 413.75);
            red = (int) Math.floor(-637.5 * ratio + 573.75);
          } else if (ratio > 0.1 && ratio < 0.5) {
            green = (int) Math.floor(637.5 * ratio - 63.75);
          } else if (ratio < 0.1) {
            green = 0;
          }
          //set the colour of the timer with the updates RGB values
          timer.setFill(Color.rgb(red, green, 0));
        } else {
          stop();
        }
      }
    };
    animationTimer.start();
  }

  private void startScore() {
    Platform.runLater(() -> {
      gameWindow.startScores(game);
      game.gameTimer.cancel();
    });
  }

  /**
   * Sets score.
   *
   * @param observableValue the observable value
   * @param old             the old
   * @param newNum          the new num
   */
  protected void setScore(ObservableValue<? extends Number> observableValue, Number old, Number newNum) {
    logger.info("Score is now {}", newNum);
    if (newNum.intValue() > this.highScore.get()) {
      this.highScore.set(newNum.intValue());
    }
  }
}
