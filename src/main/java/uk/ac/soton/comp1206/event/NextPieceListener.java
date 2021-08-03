package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * The Next Piece listener is used to handle the event when a new piece is spawned.
 */
public interface NextPieceListener {

  /**
   * Handle the next piece event
   * @param piece the next piece that needs to be displayed
   */
  public void onNextPiece(GamePiece piece, GamePiece followingPiece);
}


