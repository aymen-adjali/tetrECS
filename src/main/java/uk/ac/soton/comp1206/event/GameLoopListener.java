package uk.ac.soton.comp1206.event;

/**
 * The Game Loop listener is used to handle the event when a game loop is complete.
 */
public interface GameLoopListener {

  /**
   * Handle a game loop finishing event.
   * @param timer length of timer.
   */
  public void onGameLoop(int timer);
}
