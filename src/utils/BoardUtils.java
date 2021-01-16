package utils;

import engine.Alliance;
import engine.pieces.*;
import engine.player.Player;

/**
 * Various quality of life utilities for coding, mainly string assignments to bind numbers
 * so that we don't have to manually put numbers every time and risk build failure when
 * updating them.
 *
 * Resourced from Black Widow Chess engine
 * Author: Brethren de la Gente
 * Date: 2021-01-07
 */
@SuppressWarnings("unused")
public class BoardUtils {

  /** Board tile display size */
  public static final int TILE_SIZE = 80;

  /** Number of columns in the game board */
  public static final int TILE_COLUMN_COUNT = 9;

  /** Number of row in the game board */
  public static final int TILE_ROW_COUNT = 8;

  /** Count of all tiles in the game board */
  public static final int ALL_TILES_COUNT = TILE_COLUMN_COUNT * TILE_ROW_COUNT;

  /** Board display width */
  public static final int BOARD_WIDTH = TILE_SIZE * TILE_COLUMN_COUNT;

  /** Board display height */
  public static final int BOARD_HEIGHT = TILE_SIZE * TILE_ROW_COUNT;

  /** Pieces ranks based on their class name */
  public static final String GENERAL_FIVE_RANK = "GeneralFive";
  public static final String GENERAL_FOUR_RANK = "GeneralFour";
  public static final String GENERAL_THREE_RANK = "GeneralThree";
  public static final String GENERAL_TWO_RANK = "GeneralTwo";
  public static final String GENERAL_ONE_RANK = "GeneralOne";
  public static final String COLONEL_RANK = "Colonel";
  public static final String LT_COLONEL_RANK = "LtCol";
  public static final String MAJOR_RANK = "Major";
  public static final String CAPTAIN_RANK = "Captain";
  public static final String LT_ONE_RANK = "LtOne";
  public static final String LT_TWO_RANK = "LtTwo";
  public static final String SERGEANT_RANK = "Sergeant";
  public static final String PRIVATE_RANK = "Private";
  public static final String FLAG_RANK = "Flag";
  public static final String AGENT_RANK = "Agent";

  /** First tile index of the first board row */
  public static final int FIRST_ROW_INIT = 0;

  /** First tile index of the second board row */
  public static final int SECOND_ROW_INIT = FIRST_ROW_INIT + TILE_COLUMN_COUNT;

  /** First tile index of the last board row */
  public static final int LAST_ROW_INIT = (TILE_ROW_COUNT - 1) * TILE_COLUMN_COUNT;

  /** First tile index of the second to las board row */
  public static final int SECOND_TO_LAST_ROW_INIT = LAST_ROW_INIT - TILE_COLUMN_COUNT;

  /**
   * Creates Piece instance of the passed in piece rank and alliance.
   * @param pieceRankName name or rank of the piece to be created.
   * @param owner Player reference who will own the piece.
   * @param alliance Alliance of the piece.
   * @return the Piece created. Null unsuccessful.
   */
  public static Piece pieceInstanceCreator(String pieceRankName, Player owner,
                                           Alliance alliance) {
    Piece piece = null;

    if (pieceRankName.contains(GENERAL_FIVE_RANK))
      piece = new GeneralFive(owner, alliance);
    else if (pieceRankName.contains(GENERAL_FOUR_RANK))
      piece = new GeneralFour(owner, alliance);
    else if (pieceRankName.contains(GENERAL_THREE_RANK))
      piece = new GeneralThree(owner, alliance);
    else if (pieceRankName.contains(GENERAL_TWO_RANK))
      piece = new GeneralTwo(owner, alliance);
    else if (pieceRankName.contains(GENERAL_ONE_RANK))
      piece = new GeneralOne(owner, alliance);
    else if (pieceRankName.contains(COLONEL_RANK))
      piece = new Colonel(owner, alliance);
    else if (pieceRankName.contains(LT_COLONEL_RANK))
      piece = new LtCol(owner, alliance);
    else if (pieceRankName.contains(MAJOR_RANK))
      piece = new Major(owner, alliance);
    else if (pieceRankName.contains(CAPTAIN_RANK))
      piece = new Captain(owner, alliance);
    else if (pieceRankName.contains(LT_ONE_RANK))
      piece = new LtOne(owner, alliance);
    else if (pieceRankName.contains(LT_TWO_RANK))
      piece = new LtTwo(owner, alliance);
    else if (pieceRankName.contains(SERGEANT_RANK))
      piece = new Sergeant(owner, alliance);
    else if (pieceRankName.contains(PRIVATE_RANK))
      piece = new Private(owner, alliance);
    else if (pieceRankName.contains(FLAG_RANK))
      piece = new Flag(owner, alliance);
    else if (pieceRankName.contains(AGENT_RANK))
      piece = new Agent(owner, alliance);

    return piece;
  }

  /**
   * Constructor method that ensures this BoardUtils class cannot be instantiated.
   */
  private BoardUtils() {
    throw new RuntimeException("You cannot instantiate BoardUtils class");
  }
}
