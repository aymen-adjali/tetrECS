package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBoard;

/**
 * The Right Clicked listener is used to handle the event when a board is right clicked.
 */
public interface RightClicked {

  /**
   * Handle a right clicked event.
   * @param gameBoard the board which was right clicked.
   */
  public void onRightClicked(GameBoard gameBoard);
}
