package uk.ac.soton.comp1206.event;

/**
 * The Game Lives listener is used to handle the event when a life is lost.
 */
public interface GameLivesListener {

  /**
   * Handle a lives lost event.
   * @param lives current number of lives.
   */
  public void onLivesLost(int lives);
}
