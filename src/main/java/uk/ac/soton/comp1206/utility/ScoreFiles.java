package uk.ac.soton.comp1206.utility;

import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * The ScoreFiles class is responsible for writing and reading scores.
 * It also displays a default score list when there is no local scores.
 */
public class ScoreFiles {

  private static final Logger logger = LogManager.getLogger(ScoreFiles.class);


  /**
   * Creating a default score list, which will be displayed when there are local scores.
   */
  public static void defaultList() {
    ArrayList<Pair<String, Integer>> defaultResults = new ArrayList<>();
    defaultResults.add(new Pair<>("Aymen1", 50000));
    defaultResults.add(new Pair<>("Aymen2", 40000));
    defaultResults.add(new Pair<>("Aymen3", 30000));
    defaultResults.add(new Pair<>("Aymen4", 20000));
    defaultResults.add(new Pair<>("Aymen5", 10000));
    defaultResults.add(new Pair<>("Aymen6", 5000));
    defaultResults.add(new Pair<>("Aymen7", 2000));
    defaultResults.add(new Pair<>("Aymen8", 1000));
    defaultResults.add(new Pair<>("Aymen9", 500));
    defaultResults.add(new Pair<>("Aymen10", 100));
    writeScores(defaultResults);
  }

  /**
   * Loads a set of high scores from a file.
   *
   * @return the default file or newly updated
   */
  public static ArrayList<Pair<String, Integer>> loadScores() {
    logger.info("Loading scores");
    ArrayList<Pair<String, Integer>> pairArrayList = new ArrayList<>();
    //try-catch in case there is a problem with reading the file
    try {
      Path p = Paths.get("scores.txt");
      //checking if scores.txt exists, if it does not
      //then create a default list
      if (Files.notExists(p)) {
        defaultList();
      }
      //reading how many lines there are (scores) and saving them to a variable
      List<String> listOfScoresFromFile = Files.readAllLines(p);

      //separating the names and the scores with a ":"
      for (String pair : listOfScoresFromFile) {
        String[] parts = pair.split(":");
        pairArrayList.add(new Pair<>(parts[0], Integer.parseInt(parts[1])));
      }
    } catch (Exception e) {
      logger.error("Unable to read file: " + e.getMessage());
      e.printStackTrace();
    }
    return pairArrayList;
  }

  /**
   * Sorts the scores from highest to lowest, and then separates the name
   * and score with a colon.
   *
   * @param scoreAndName
   */
  public static void writeScores(List<Pair<String, Integer>> scoreAndName) {
    //sorts the scores in order
    scoreAndName.sort((result1, result2) -> (result2.getValue()).compareTo(result1.getValue()));

    //try-catch in case there is a problem with reading the file
    try {
      Path p = Paths.get("scores.txt");
      StringBuilder file = new StringBuilder();
      int score = 0;

      for (Pair<String, Integer> scorePair : scoreAndName) {
        ++score;
        String name = scorePair.getKey();
        //adding the name and score to a line and then adding a newline after
        file.append(name).append(":").append(scorePair.getValue()).append("\n");
        //only adding 11 lines
        if (score >= 10) {
          break;
        }
      }
      Files.writeString(p, file.toString());
    } catch (Exception e) {
      logger.error("Unable to write to file: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
