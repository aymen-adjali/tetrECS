package uk.ac.soton.comp1206.component;

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;

/**
 * The ScoresList class which extends VBox holds and displays a list of names and associated scores.
 */
public class ScoreList extends VBox {

  private static final Logger logger = LogManager.getLogger(ScoreList.class);

  private final ArrayList<HBox> scoreList = new ArrayList<>();
  public final SimpleListProperty scores = new SimpleListProperty();
  private final StringProperty userName = new SimpleStringProperty();

  public ScoreList() {

    //update the score list when a name or score are changed
    scores.addListener((InvalidationListener) (c) -> update());
    this.userName.addListener((e) -> this.update());

    //adding scoreList style
    this.getStyleClass().add("scorelist");
    this.setAlignment(Pos.CENTER);
    this.setSpacing(2.0D);
  }

  /**
   *  Updating the scores when the list is updated,
   */
  public void update() {
    logger.info("Updating score list with {} scores", this.scores.size());

    //clearing all previous children and scores
    scoreList.clear();
    getChildren().clear();


    int counter = 0;

    //getting the top 10 scores only
    for (Object updatedValues : this.scores) {
      logger.info("I have looped");
      Pair score = (Pair) updatedValues;

      //making sure there is only 10
      counter += 1;
      if (counter > 10) {
        break;
      }

      //creating a hBox with the new list
      var scoreVbox = new HBox();
      scoreVbox.getStyleClass().add("scoreitem");
      scoreVbox.setAlignment(Pos.CENTER);
      scoreVbox.setSpacing(10);

      //using the variable x to create a rainbow colour effect using the .COLOURS method in GameBlock
      var stringColour = GameBlock.COLOURS[counter];
      //add the userName
      Text scorer = new Text(score.getKey() + ":");
      if ((score.getKey()).equals(this.userName.get())) {
        //if the score is the users then make it bold
        scorer.getStyleClass().add("scorer");
      }
      scorer.setTextAlignment(TextAlignment.CENTER);
      scorer.setFill(stringColour);

      //add the score
      var scores = new Text(score.getValue().toString());

      //make the score bold
      scores.getStyleClass().add("myscore");

      //add the text to the scoreVbox
      scoreVbox.getChildren().add(scorer);
      scoreVbox.getChildren().add(scores);

      //set alignment and the correct colour
      scores.setTextAlignment(TextAlignment.CENTER);
      scores.setFill(stringColour);
      HBox.setHgrow(scores, Priority.ALWAYS);
      HBox.setHgrow(scorer, Priority.ALWAYS);

      this.getChildren().add(scoreVbox);
      this.scoreList.add(scoreVbox);

      //points.setFill(Color.RED);
      //scorer.setFill(Color.WHITE);
    }
  }

  /**
   * Gets user name.
   *
   * @return the user name
   */
  public StringProperty getUserName() {
    return userName;
  }

  /**
   * Gets score property.
   *
   * @return the score property
   */
  public SimpleListProperty getScoreProperty() {
    return scores;
  }
}
