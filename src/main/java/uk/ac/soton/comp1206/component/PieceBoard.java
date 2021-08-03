package uk.ac.soton.comp1206.component;

import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;

/**
 * A PieceBoard is a visual component that extends GameBoard.
 * Used to display current and upcoming pieces in the challengeScene.
 */
public class PieceBoard extends GameBoard {

  private GamePiece piece;

  /**
   * Boolean variable to be able to control which PieceBoard shows the while placement circle.
   */
  private final Boolean circleEnabled;

  /**
   * Instantiates a new Piece board.
   *
   * @param gamePiece
   * @param width
   * @param height
   * @param circleEnabled
   */
  public PieceBoard(GamePiece gamePiece, double width, double height, boolean circleEnabled) {
    super(new Grid(3, 3), width, height);
    this.circleEnabled = circleEnabled;
    setPieceToDisplay(gamePiece);
  }

  /**
   * Sets a piece to display in the pieceBoard, and checks for the circleEnabled boolean.
   *
   * @param gamePiece the game piece
   * @return the piece to display
   */
  public PieceBoard setPieceToDisplay(GamePiece gamePiece) {
    piece = gamePiece;
    pieceGrid();

    //if circleEnabled is true, the placement circle will be shown.
    if (circleEnabled) {
      getBlock(1, 1).circle();
    }

    return this;
  }

  /**
   * Gets blocks of the member variable piece and only paints the blocks in the pieceBoard is the their value is
   * not 0.
   */
  private void pieceGrid() {
    int[][] blocks = piece.getBlocks();
    for (int column = 0; column < blocks.length; column += 1) {
      for (int row = 0; row < blocks[column].length; row += 1) {
        grid.set(column, row, 0);
        if (blocks[column][row] != 0) {
          grid.set(column, row, piece.getValue());
        }
      }
    }
  }
}