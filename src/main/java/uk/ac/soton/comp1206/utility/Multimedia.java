package uk.ac.soton.comp1206.utility;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Multimedia class handles all the logic to play audio in the game.
 * Separate methods are used to play music and sounds.
 */
public class Multimedia {

  private static final Logger logger = LogManager.getLogger(Multimedia.class);

  private static final BooleanProperty audioEnabled = new SimpleBooleanProperty(false);
  private static final BooleanProperty musicEnabled = new SimpleBooleanProperty(true);

  private static MediaPlayer audioPlayer;
  public static MediaPlayer musicPlayer;



  /**
   * Play an audio file
   *
   * @param file filename to play from resources
   */
  public static void playAudio(String file) {

    //if music is enabled
    if (!getAudioEnabled()) return;

    String toPlay = Multimedia.class.getResource("/" + file).toExternalForm();
    logger.info("Playing audio: " + toPlay);

    //try-catch in case there is an error when trying to play the audio
    try {
      Media play = new Media(toPlay);
      //JavaFX MediaPlayer declared outside the function
      //or it gets garbage collected and stops playing sounds
      audioPlayer = new MediaPlayer(play);
      audioPlayer.play();
    } catch (Exception e) {
      setAudioEnabled(false);
      e.printStackTrace();
      logger.error("Unable to play audio file, disabling audio");
    }
  }

  /**
   * Plays a music file on loop.
   *
   * @param file the file that will try and be played.
   */
  public static void playMusic(String file) {

    //if music is enabled
    if (!getMusicEnabled()) return;

    String toPlay = Multimedia.class.getResource("/" + file).toExternalForm();
    logger.info("Playing music: " + toPlay);

    //try-catch in case there is an error when trying to play the audio
    try {
      Media play = new Media(toPlay);
      //JavaFX MediaPlayer declared outside the function
      //or it gets garbage collected and stops playing sounds!
      musicPlayer = new MediaPlayer(play);

      //Set the media player to automatically loop
      musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
      musicPlayer.play();

    } catch (Exception e) {
      setAudioEnabled(false);
      e.printStackTrace();
      logger.error("Unable to play audio file, disabling audio");
    }
  }

  /**
   * accessor methods for audioEnabled.
   */
  public static BooleanProperty audioEnabledProperty() {
    return audioEnabled;
  }

  public static void setAudioEnabled(boolean enabled) {
    logger.info("Audio enabled set to: " + enabled);
    audioEnabledProperty().set(enabled);
  }

  public static boolean getAudioEnabled() {
    return audioEnabledProperty().get();
  }

  /**
   * accessor methods for musicEnabled.
   */
  public static BooleanProperty musicEnabledProperty() {
    return musicEnabled;
  }

  public static void setMusicEnabled(boolean enabled) {
    logger.info("Music enabled set to: " + enabled);
    musicEnabledProperty().set(enabled);
  }

  public static boolean getMusicEnabled() {
    return musicEnabledProperty().get();
  }

  public static void stopMusic() {
    musicPlayer.stop();
  }
}
