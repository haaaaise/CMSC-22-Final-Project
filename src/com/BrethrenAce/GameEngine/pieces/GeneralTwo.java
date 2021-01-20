package com.BrethrenAce.GameEngine.pieces;

import com.BrethrenAce.GameEngine.Alliance;
import com.BrethrenAce.GameEngine.player.Player;
import com.BrethrenAce.GameUtilities.BoardUtils;

/**
 * Two star general piece class that inherits from abstract Piece class.
 *
 * Author: Brethren de la Gente
 * Date: 2021-01-07
 */
public class GeneralTwo extends Piece {

  /** Rank of the piece */
  private final String rank = BoardUtils.GENERAL_TWO_RANK;

  /** Power level of the piece to compare ranks */
  private final int piecePower = 11;

  /** Allowed amount of piece instance owned by a Player in a single game */
  private final int legalPieceInstanceCount = 1;

  /**
   * Constructor that takes in the owner Player, and Alliance of this piece.
   * Sets pieceCoords to -1 temporarily.
   */
  public GeneralTwo(final Player owner, final Alliance alliance) {
    super(owner, alliance);
  }

  /**
   * Constructor that takes in the owner Player, Alliance and coordinates of
   * this Piece.
   */
  public GeneralTwo(final Player owner, final Alliance alliance,
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
    final GeneralTwo copy = new GeneralTwo(
        this.pieceOwner, this.pieceAlliance, this.pieceCoords);
    return copy;
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

