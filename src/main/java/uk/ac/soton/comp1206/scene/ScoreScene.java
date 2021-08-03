package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.ScoreList;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.ScoreFiles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The ScoreScene scene. Holds the UI for scores, shows two custom ScoreList components,
 * for local and online scores.
 * Automatically changes to menu scene after a certain time after scores have been shown.
 */
public class ScoreScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(ScoreScene.class);

  private final Game game;

  protected int lowestScore;
  protected int remoteScore;
  protected int lowestRemoteScore;
  boolean newLocalScore = false;
  boolean newOnlineScore = false;

  private final Communicator communicator;

  private final StringProperty playerName = new SimpleStringProperty("");
  private final BooleanProperty scoresDisplayed = new SimpleBooleanProperty(false);

  private Pair<String, Integer> highestScoreWithName;
  private Timer timer;

  private VBox vBoxScore;
  private Text highScoreTitle;

  private ObservableList<Pair<String, Integer>> localScoresObservable;
  private ObservableList<Pair<String, Integer>> remoteScores;
  private final ArrayList<Pair<String, Integer>> onlineScores = new ArrayList<>();

  private boolean alreadyWrittenName = false;

  /**
   * Create a new Scores scene
   *
   * @param game
   * @param gameWindow the game window
   */
  public ScoreScene(Game game, GameWindow gameWindow) {
    super(gameWindow);
    this.game = game;

    this.communicator = gameWindow.getCommunicator();
    logger.info("Starting Score scene");
  }

  /**
   * Creating a time period that the scores scene will be visible for.
   *
   * @param delay time period.
   */
  public void setTimer(long delay) {
    if (this.timer != null) {
      this.timer.cancel();

    }
    TimerTask task = new TimerTask() {
      public void run() {
        Platform.runLater(ScoreScene.this::openMenu);
      }
    };
    this.timer = new Timer();
    this.timer.schedule(task, delay);

  }

  /**
   * Whenever the timer finishes the menu is opened
   */
  public void openMenu() {
    if (!this.newLocalScore) {
      this.timer.cancel();

      this.gameWindow.startMenu();
    }
  }

  /**
   * Revealing the scores.
   * Setting the timer for 15 seconds.
   */
  public void reveal() {
    this.setTimer(15000);
    //this.scene.setOnKeyPressed((e) -> this.openMenu());
    this.scoresDisplayed.set(true);
    //this.local.reveal();
    //this.remote.reveal();
  }

  @Override
  public void initialise() {
    this.communicator.addListener((score) -> Platform.runLater(() -> this.receiveOnlineScores(score.trim())));

    this.communicator.send("HISCORES");
  }


  @Override
/**
 * Building the score scene window
 */
  public void build() {
    logger.info("Building " + this.getClass().getName());

    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    StackPane scoreScenePane = new StackPane();
    scoreScenePane.setMaxWidth(gameWindow.getWidth());
    scoreScenePane.setMaxHeight(gameWindow.getHeight());
    scoreScenePane.getStyleClass().add("menu-background");

    root.getChildren().add(scoreScenePane);

    //adding borderPane to the scorePane
    var mainPane = new BorderPane();
    scoreScenePane.getChildren().add(mainPane);

    //adding the vBox to hold the scores
    vBoxScore = new VBox();
    vBoxScore.setAlignment(Pos.TOP_CENTER);
    vBoxScore.setPadding(new Insets(10, 10, 10, 10));
    vBoxScore.setSpacing(15);

    //adding the vBox to the mainPane
    mainPane.setCenter(this.vBoxScore);

    //adding "Game Over" text
    var gameOver = new Text("Game Over");
    gameOver.setTextAlignment(TextAlignment.CENTER);
    VBox.setVgrow(gameOver, Priority.ALWAYS);
    gameOver.getStyleClass().add("title");

    //adding "High Scores" title
    highScoreTitle = new Text("High Scores");
    highScoreTitle.setTextAlignment(TextAlignment.CENTER);
    VBox.setVgrow(this.highScoreTitle, Priority.ALWAYS);
    highScoreTitle.getStyleClass().add("title");
    highScoreTitle.setFill(Color.YELLOW);

    //adding the titles to the vBox
    vBoxScore.getChildren().add(gameOver);
    vBoxScore.getChildren().add(highScoreTitle);

    //create a score list inside a gridPane
    var gridPane = new GridPane();
    gridPane.visibleProperty().bind(this.scoresDisplayed);
    gridPane.setAlignment(Pos.CENTER);
    gridPane.setHgap(100);

    //adding local scores
    Text textLocalScores = new Text("Local Scores");
    textLocalScores.setTextAlignment(TextAlignment.CENTER);
    textLocalScores.getStyleClass().add("heading");
    GridPane.setHalignment(textLocalScores, HPos.CENTER);

    //adding online scores
    Text textOnlineScores = new Text("Online Scores");
    textOnlineScores.setTextAlignment(TextAlignment.CENTER);
    textOnlineScores.getStyleClass().add("heading");
    GridPane.setHalignment(textOnlineScores, HPos.CENTER);

    //adding the text to the gridPane
    gridPane.add(textLocalScores, 0, 0);
    gridPane.add(textOnlineScores, 1, 0);

    //creating the score list using the custom component
    ScoreList localScoreList = new ScoreList();
    GridPane.setHalignment(localScoreList, HPos.CENTER);

    //adding online score list
    ScoreList onlineScoreList = new ScoreList();
    GridPane.setHalignment(onlineScoreList, HPos.CENTER);

    //adding lists to gridPane
    gridPane.add(localScoreList, 0, 1);
    gridPane.add(onlineScoreList, 1, 1);

    //adding gridPane to the vBox
    this.vBoxScore.getChildren().add(gridPane);

    //if the scores are empty, load the scores (default)
    if (this.game.getTheScores().isEmpty()) {
      localScoresObservable = FXCollections.observableArrayList(ScoreFiles.loadScores());
    } else {
      //if not empty then get the scores
      localScoresObservable = FXCollections.observableArrayList(this.game.getTheScores());
      textLocalScores.setText("local");
    }

    //putting the local scores in order, from highest to lowest value
    localScoresObservable.sort((number1, number2) -> (number2.getValue()).compareTo(number1.getValue()));

    this.remoteScores = FXCollections.observableArrayList(this.onlineScores);
    SimpleListProperty wrapper = new SimpleListProperty(this.localScoresObservable);
    localScoreList.getScoreProperty().bind(wrapper);
    localScoreList.getUserName().bind(playerName);
    SimpleListProperty wrapper2 = new SimpleListProperty(this.remoteScores);
    onlineScoreList.getScoreProperty().bind(wrapper2);
    onlineScoreList.getUserName().bind(playerName);
  }

  /**
   * Receive incoming online scores, and fill the online ScoreList with the scores.
   *
   * @param score incoming score values.
   */
  private void receiveOnlineScores(String score) {
    String[] parts = score.split(" ", 2);
    String outPut = parts[0];
    if (outPut.equals("HISCORES")) {
      if (parts.length > 1) {
        String data = parts[1];
        this.loadOnlineScores(data);
      } else {
        this.loadOnlineScores("");
      }
    }
  }

  /**
   * Adding high scores to the online scoreList.
   *
   * @param words
   */
  public void loadOnlineScores(String words) {
    //clearing all previous scores
    this.onlineScores.clear();
    String[] strings = words.split("\n");
    //int scoreLines = strings.length;

    //looping through the scores
    for (String scoreLine : strings) {

      //splitting each line into two parts
      //the name and score will be split by a colon
      String[] parts = scoreLine.split(":", 2);
      String user = parts[0];
      int userScore = Integer.parseInt(parts[1]);
      //add the pair to our arrayList
      onlineScores.add(new Pair(user, userScore));
    }

    //ordering the results from highest to lowest
    this.onlineScores.sort((number1, number2) -> (number2.getValue()).compareTo(number1.getValue()));
    remoteScores.clear();

    //adding the results
    remoteScores.addAll(this.onlineScores);

    this.writeScores();
  }

  /**
   * Check if there is a new score, and prompt the user to type in their
   * user name if they achieved a high score.
   */
  public void writeScores() {
    //is there is no new scores, reveal the previous scores
    if (game.getTheScores().isEmpty()) {
      logger.info("There was no new score achieved");
      reveal();
    }
    int userScore = game.getScore();

    //if the local scores are under 10 then we have a new score
    //and if the userScore beats the last score
    //if these conditions are true, set newLocalScore to true
    if (localScoresObservable.size() < 10 || localScoresObservable.get(9).getValue() < game.getScore()) {
      this.newLocalScore = true;
    }
    //if the online scores are under 10 then we have a new score
    if (this.onlineScores.size() < 10) {
      this.newOnlineScore = true;
    }

    int nextLocalScore = 0;
    int nextOnlineScore = 0;
    Pair scoreAndName;
    if (userScore > lowestScore) {
      //looping through local scores with iterator
      Iterator listIterator = localScoresObservable.listIterator();
      listIterator.hasNext();
      ++nextLocalScore;
      scoreAndName = (Pair) listIterator.next();

      //checking if there is a new high score
      if ((Integer) scoreAndName.getValue() < userScore) {
        //new high score
        newLocalScore = true;
        logger.info("You have achieved a new local score!");
      }
    }
    if (remoteScore > lowestRemoteScore) {
      //looping through online scores with iterator
      for (Iterator iterator = this.onlineScores.iterator();
           iterator.hasNext();
           ++nextOnlineScore) {
        scoreAndName = (Pair) iterator.next();

        //checking if there is a new high score
        if ((Integer) scoreAndName.getValue() < remoteScore) {
          //new high score
          logger.info("You have achieved a new online score!");
          this.newOnlineScore = true;
          break;
        }
      }
    }
    logger.info("New score: {}", newLocalScore);
    if (!newLocalScore) {
      logger.info("No new score was achieved");
      reveal();
    } else {
      if (!alreadyWrittenName) {
        alreadyWrittenName = true;

        //making a textField that will be used to enter a name when a high score is achieved
        highScoreTitle.setText("You have achieved a New High Score!");

        //creating textField
        TextField textField = new TextField();
        textField.setPrefWidth((this.gameWindow.getWidth() / 2));
        textField.requestFocus();
        textField.setPromptText("Please enter your name");
        vBoxScore.getChildren().add(2, textField);

        //creating a button for entering name
        var enter = new Button("Submit name");
        enter.setDefaultButton(true);

        //add the button the vBox
        vBoxScore.getChildren().add(3, enter);

        //getting the last scores
        int lastLocalScore = nextLocalScore;
        int lastOnlineScore = nextOnlineScore;

        //on action event for the button
        enter.setOnAction((e) -> {

          //adding text options
          var exit = menuTextButton("Exit To Menu");
          vBoxScore.getChildren().add(5, exit);
          //adding events when each text option is clicked
          exit.setOnMouseClicked((event) -> {
            gameWindow.startMenu();
          });

          //getting the user input from the textField and setting it to the userName
          String userInputName = textField.getText().replace(":", "");
          playerName.set(userInputName);

          //delete the textField after pressing the enter button
          vBoxScore.getChildren().remove(textField);
          vBoxScore.getChildren().remove(enter);

          //getting the pair of name and score of the new high scorer
          highestScoreWithName = new Pair(userInputName, userScore);

          //if it is a local score, add it to the local list
          if (newLocalScore) {
            localScoresObservable.add(lastLocalScore, highestScoreWithName);
          }
          //if it is a online score, add it to the online list
          if (newOnlineScore) {
            remoteScores.add(lastOnlineScore, highestScoreWithName);
          }
          //using the communicator, send the new high score
          this.communicator.send("HISCORE " + userInputName + ":" + userScore);
          this.communicator.send("HISCORES");

          //remove new score status to prevent multiple additions
          newLocalScore = false;
          newOnlineScore = false;

          //write the new scores list
          ScoreFiles.writeScores(localScoresObservable);
        });
      }
    }
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

