package uk.ac.soton.comp1206.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Instances of GamePiece Represents the model of a specific Game Piece with it's block makeup.
 * <p>
 * The GamePiece class also contains a factory for producing a GamePiece of a particular shape, as specified by it's
 * number.
 */
public class GamePiece {

  private static final Logger logger = LogManager.getLogger(GamePiece.class);

  /**
   * The 2D grid representation of the shape of this piece
   */
  private int[][] blocks;

  /**
   * The value of this piece
   */
  private final int value;

  private int rotations;

  /**
   * The name of this piece
   */
  private final String name;

  /**
   * Create a new GamePiece of the specified piece number
   *
   * @param piece piece number
   * @return the created GamePiece
   */
  public static GamePiece createPiece(int piece) {
    switch (piece) {
      //Line
      case 0 -> {
        int[][] blocks = {{0, 0, 0}, {1, 1, 1}, {0, 0, 0}};
        logger.info(" LINE piece created");
        return new GamePiece("Line", blocks, 1);
      }

      //C
      case 1 -> {
        int[][] blocks = {{0, 0, 0}, {1, 1, 1}, {1, 0, 1}};
        logger.info(" C piece created");
        return new GamePiece("C", blocks, 2);
      }

      //Plus
      case 2 -> {
        int[][] blocks = {{0, 1, 0}, {1, 1, 1}, {0, 1, 0}};
        logger.info(" PLUS piece created");
        return new GamePiece("Plus", blocks, 3);
      }

      //Dot
      case 3 -> {
        int[][] blocks = {{0, 0, 0}, {0, 1, 0}, {0, 0, 0}};
        logger.info(" DOT piece created");
        return new GamePiece("Dot", blocks, 4);
      }

      //Square
      case 4 -> {
        int[][] blocks = {{1, 1, 0}, {1, 1, 0}, {0, 0, 0}};
        logger.info(" SQUARE piece created");
        return new GamePiece("Square", blocks, 5);
      }

      //L
      case 5 -> {
        int[][] blocks = {{0, 0, 0}, {1, 1, 1}, {0, 0, 1}};
        logger.info(" L piece created");
        return new GamePiece("L", blocks, 6);
      }

      //J
      case 6 -> {
        int[][] blocks = {{0, 0, 1}, {1, 1, 1}, {0, 0, 0}};
        logger.info(" J piece created");
        return new GamePiece("J", blocks, 7);
      }

      //S
      case 7 -> {
        int[][] blocks = {{0, 0, 0}, {0, 1, 1}, {1, 1, 0}};
        logger.info(" S piece created");
        return new GamePiece("S", blocks, 8);
      }

      //Z
      case 8 -> {
        int[][] blocks = {{1, 1, 0}, {0, 1, 1}, {0, 0, 0}};
        logger.info(" Z piece created");
        return new GamePiece("Z", blocks, 9);
      }

      //T
      case 9 -> {
        int[][] blocks = {{1, 0, 0}, {1, 1, 0}, {1, 0, 0}};
        logger.info(" T piece created");
        return new GamePiece("T", blocks, 10);
      }

      //X
      case 10 -> {
        int[][] blocks = {{1, 0, 1}, {0, 1, 0}, {1, 0, 1}};
        logger.info(" X piece created");
        return new GamePiece("X", blocks, 11);
      }

      //Corner
      case 11 -> {
        int[][] blocks = {{0, 0, 0}, {1, 1, 0}, {1, 0, 0}};
        logger.info(" CORNER piece created");
        return new GamePiece("Corner", blocks, 12);
      }

      //Inverse Corner
      case 12 -> {
        int[][] blocks = {{1, 0, 0}, {1, 1, 0}, {0, 0, 0}};
        logger.info(" INVERSE CORNER created");
        return new GamePiece("Inverse Corner", blocks, 13);
      }

      //Diagonal
      case 13 -> {
        int[][] blocks = {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}};
        logger.info(" DIAGONAL piece created");
        return new GamePiece("Diagonal", blocks, 14);
      }

      //Double
      case 14 -> {
        int[][] blocks = {{0, 1, 0}, {0, 1, 0}, {0, 0, 0}};
        logger.info(" DOUBLE piece created");
        return new GamePiece("Double", blocks, 15);
      }
    }

    //Not a valid piece number
    throw new IndexOutOfBoundsException("No such piece: " + piece);
  }

  /**
   * Create a new GamePiece of the specified piece number and rotation
   *
   * @param piece    piece number
   * @param rotation number of times to rotate
   * @return the created GamePiece
   */
  public static GamePiece createPiece(int piece, int rotation) {
    var newPiece = createPiece(piece);

    newPiece.rotate(rotation);
    return newPiece;
  }

  /**
   * Create a new GamePiece with the given name, block makeup and value. Should not be called directly, only via the
   * factory.
   *
   * @param name   name of the piece
   * @param blocks block makeup of the piece
   * @param value  the value of this piece
   */
  GamePiece(String name, int[][] blocks, int value) {
    this.name = name;
    this.blocks = blocks;
    this.value = value;

    //Use the shape of the block to create a grid with either 0 (empty) or the value of this shape for each block.
    for (int x = 0; x < blocks.length; x++) {
      for (int y = 0; y < blocks[x].length; y++) {
        if (blocks[x][y] == 0) continue;
        blocks[x][y] = value;
      }
    }
  }

  /**
   * Get the value of this piece
   *
   * @return piece value
   */
  public int getValue() {
    return value;
  }

  /**
   * Get the block makeup of this piece
   *
   * @return 2D grid of the blocks representing the piece shape
   */
  public int[][] getBlocks() {
    return blocks;
  }

  /**
   * Rotate this piece the given number of rotations
   *
   * @param rotations number of rotations
   */
  public void rotate(int rotations) {
    for (int rotated = 0; rotated < rotations; rotated++) {
      rotate();
    }
  }

  /**
   * Gets rotations.
   *
   * @return the rotations
   */
  public int getRotations() {
    return rotations;
  }

  /**
   * Rotate this piece exactly once by rotating it's 3x3 grid
   */
  public void rotate() {
    int[][] rotated = new int[blocks.length][blocks[0].length];
    rotated[2][0] = blocks[0][0];
    rotated[1][0] = blocks[0][1];
    rotated[0][0] = blocks[0][2];

    rotated[2][1] = blocks[1][0];
    rotated[1][1] = blocks[1][1];
    rotated[0][1] = blocks[1][2];

    rotated[2][2] = blocks[2][0];
    rotated[1][2] = blocks[2][1];
    rotated[0][2] = blocks[2][2];

    blocks = rotated;
    rotations = (rotations + 1) % 4;

  }

  /**
   * Return the string representation of this piece
   *
   * @return the name of this piece
   */
  public String toString() {
    return this.name;
  }
}
