package engine.pieces;

import engine.Alliance;
import engine.player.Player;
import utils.BoardUtils;

/**
 * Colonel piece class that inherits from abstract Piece class.
 *
 * Author: Brethren de la Gente
 * Date: 2021-01-07
 */
public class Colonel extends Piece {

  /** Rank of the piece */
  private final String rank = BoardUtils.COLONEL_RANK;

  /** Power level of the piece to compare ranks */
  private final int piecePower = 9;

  /** Allowed amount of piece instance owned by a Player in a single game */
  private final int legalPieceInstanceCount = 1;

  /**
   * Constructor that takes in the owner Player, and Alliance of this piece.
   * Sets pieceCoords to -1 temporarily.
   */
  public Colonel(final Player owner, final Alliance alliance) {
    super(owner, alliance);
  }

  /**
   * Constructor that takes in the owner Player, Alliance and coordinates of
   * this Piece.
   */
  public Colonel(final Player owner, final Alliance alliance,
                 final int coords) {
    super(owner, alliance, coords);
  }

  /**
   * Gets the current rank of this specific Piece instance.
   * @return String rank field.
   */
  @Override
  public final String getRank() {
    return this.rank;
  }

  /**
   * Gets the allowed legal instance per Player of this specific piece.
   * @return int legalPieceInstanceCount field.
   */
  @Override
  public final int getLegalPieceInstanceCount() {
    return this.legalPieceInstanceCount;
  }

  /**
   * Gets this Pieces instance power level.
   * @return int piecePower field.
   */
  @Override
  public final int getPiecePower() {
    return this.piecePower;
  }

  /**
   * Create deep copy of this specific Piece instance.
   * @return Piece deep copy if this Piece instance.
   */
  @Override
  public final Piece clone() {
    return new Colonel(
        this.pieceOwner, this.pieceAlliance, this.pieceCoords);
  }

  @Override
  public String toString() {
    return "piece=" + rank + ";piecePower=" + piecePower +
           ";pieceCoords=" + pieceCoords +
           ";legalPieceInstanceCount=" + legalPieceInstanceCount +
           ";pieceOwner=" + pieceOwner.getAlliance() +
           ";pieceAlliance=" + pieceAlliance;
  }
}
