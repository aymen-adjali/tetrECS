package uk.ac.soton.comp1206.event;

/**
 * The Line Cleared listener is used to handle the event when a line is completed and needs to be cleared.
 */
import java.util.Set;

public interface LineClearedListener {

  /**
   * Handle a line cleared event.
   * @param gameBlockCoordinates the game block coordinates
   */
  public void onLineListener(Set<int[]> gameBlockCoordinates);
}
