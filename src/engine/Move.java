package engine;

import engine.Board.Tile;
import engine.pieces.Piece;
import engine.player.Player;
import utils.BoardUtils;

import javax.swing.*;
import java.util.Map;

/**
 * A class that decides the mobility of all pieces and has several functions, mainly to
 * check current game scenario(e.g. winning). All pieces movement are
 * the same, i.e. a step forwards, backwards and sidewards. Also, Move class
 * evaluates all the four possible moves and categorized them as one of the
 * following, "attacking", "draw", "normal", and "invalid." Finally, Move class
 * checks to see if the executing move will conclude the game and declase the
 * winner.
 *
 * Author: Brethren de la Gente
 * Date: 2021-01-07
 */
@SuppressWarnings("unused")
public class Move {

  /** Turn ID that serves as reference. */
  private int turnId;

  /** Reference to the Board to execute the move in */
  private final Board board;

  /** Reference to the Player that owns the Piece to be moved. */
  private final Player player;

  /** Location of the occupied Tile in which the piece to be moved. */
  private final int sourceTileCoords;

  /** Location of the Tile to where the source piece will potentially move into */
  private final int targetTileCoords;

  /** boolean that holds if the this Move instance has bee executed */
  private boolean isExecuted = false;

  /** Move type to determine the behavior of piece relocation */
  private String moveType;

  /** Deep copy of the source piece */
  private Piece sourcePieceCopy;

  /** Deep copy of the target piece if move type is attacking or draw */
  private Piece targetPieceCopy;

  /** Deep copy of the eliminated piece if move type is attacking */
  private Piece eliminatedPiece;

  /**
   * Constructor that takes in the player who will move the piece, board, source
   * tile coordinates and target tile coordinates.
   * @param player Player reference making the move.
   * @param board Board reference.
   * @param sourceTileCoords location of the Tile containing a piece to be moved.
   * @param targetTileCoords location of the destination of the piece to be moved.
   */
  public Move(final Player player,
              final Board board,
              final int sourceTileCoords,
              final int targetTileCoords) {
    this.player = player;
    this.board = board;
    this.sourceTileCoords = sourceTileCoords;
    this.targetTileCoords = targetTileCoords;
    this.turnId = -1;
  }

  /**
   * Evaluates the move for this Move instance based on the target tile location
   * and the source piece to be moved.
   * "attacking" = if target Tile contains opposing piece Alliance.
   * "normal"     = if target Tile is empty.
   * "invalid"    = if target Tile contains friendly piece Alliance.
   * "draw"       = if target Tile contains opposing piece Alliance and has the
   *                same rank, with the exception of Flag rank.
   */
  public void evaluateMove() {
    this.sourcePieceCopy = this.board.getTile(sourceTileCoords).getPiece().clone();
    if (this.board.getTile(targetTileCoords).isTileOccupied())
      this.targetPieceCopy = this.board.getTile(targetTileCoords).getPiece().clone();
    else
      this.targetPieceCopy = null;

    if (board.getTile(targetTileCoords).isTileOccupied())
      if (targetPieceCopy.getPieceAlliance() != sourcePieceCopy.getPieceAlliance())
        if (isSameRank() && isTargetPieceFlag())
          this.moveType = "attacking";
        else if (isSameRank())
          this.moveType = "draw";
        else
          this.moveType = "attacking";
      else
        this.moveType = "invalid";
    else
      this.moveType = "normal";
  }

  /**
   * Executes this Move instance and actuate the Move to reflect the changes in
   * the Board.
   * @return boolean true if successful, else false.
   */
  public boolean execute() {
    if (legalMoveCheck()) {
      this.turnId = board.getCurrentTurn();

      switch (this.moveType) {

        case "attacking":
          // Check if source or target piece is Flag rank, then conclude the game.
          if (isTargetPieceFlag()) {
            JOptionPane.showMessageDialog(null, "\n" + sourcePieceCopy.getPieceAlliance() +
                    " player WON!\n");
            board.setEndGameWinner(sourcePieceCopy.getPieceAlliance());
          } else if (isSourcePieceFlag() && !isTargetPieceFlag()){
            JOptionPane.showMessageDialog(null,"\n" + targetPieceCopy.getPieceAlliance() +
                               " player WON!\n");
            board.setEndGameWinner(targetPieceCopy.getPieceAlliance());
          }

          // Eliminate low ranking piece from the attacking engagement.
          if (isTargetPieceEliminated()) {
            board.replacePiece(targetTileCoords, sourcePieceCopy);
            board.getTile(sourceTileCoords).removePiece();
            eliminatedPiece = targetPieceCopy;
          } else {
            board.getTile(sourceTileCoords).removePiece();
            eliminatedPiece = sourcePieceCopy;
          }
          this.isExecuted = true;
          break;

        case "normal":
          // Check if Flag has been maneuvered into the opposite end row of the board.
          if (isFlagSucceeded()) {
            System.out.println("\n" + sourcePieceCopy.getPieceAlliance() +
                " player WON!\n");
            board.setEndGameWinner(sourcePieceCopy.getPieceAlliance());
          }

          // Move Tile normally
          board.movePiece(sourceTileCoords, targetTileCoords);
          this.isExecuted = true;
          break;

        case "draw":
          // Eliminates both pieces from the game.
          board.getTile(sourceTileCoords).removePiece();
          board.getTile(targetTileCoords).removePiece();
          break;

        case "invalid":
          // Do nothing and return false.
          System.out.println("E: Invalid move");
          System.out.println(this.toString());
          return false;
        default:
          return false;
      }

      // If successful, change execution status and return true.
      this.isExecuted = true;
      return true;
    }
    return false;
  }

  /**
   * Check if this Move instance is one of the legal moves of the current state
   * of the source piece. Depends on Piece evaluateMoves() method.
   * @return boolean true of this Move is a candidate move for the source piece.
   */
  private boolean legalMoveCheck() {
    // Fetch all possible moves if any.
    final Map<String, Move> possiblePieceMoves =
      this.board.getTile(sourceTileCoords).getPiece().evaluateMoves(board);

    // Check if one of possible piece moves
    for (final Map.Entry<String, Move> entry : possiblePieceMoves.entrySet()) {
      if (entry.getValue().getDestinationCoords() == targetTileCoords) {
        return true;
      }
    }

      // set move type to "invalid" if not in possible moves.
    moveType = "invalid";

    if (Board.isDebugMode()) {
      String targetPiece;
      if (targetPieceCopy == null)
        targetPiece = "";
      else
        targetPiece = targetPieceCopy.getRank();

      System.out.println(sourcePieceCopy.getPieceAlliance() + " " +
          sourcePieceCopy.getRank() + " " + sourceTileCoords + " to " +
          targetPiece + " " + targetTileCoords + " " +
          this.moveType + " ILLEGAL MOVE");
    }

    return false;
  }

  /**
   * Checks if source and target piece has the same rank.
   * @return boolean true if same rank, else false.
   */
  private boolean isSameRank() {
      return sourcePieceCopy.getRank() == targetPieceCopy.getRank();
  }

  /**
   * Checks if target piece has been eliminated by the source piece. Higher
   * ranking piece will eliminate lower ranking piece.
   * Agent piece will eliminated all pieces regardless of rank except the Private
   * piece.
   * If both pieces were are of Flag rank, the aggressor piece will win
   * the engagement.
   * @return boolean true if target piece is subordinate to the source piece,
   * else false.
   */
  private boolean isTargetPieceEliminated() {
    if (isSourcePieceFlag() && isTargetPieceFlag())
      return true;
    else if (sourcePieceCopy.getRank() == "Private" && targetPieceCopy.getRank() == "Agent")
      return true;
    else if (sourcePieceCopy.getRank() == "Agent" && targetPieceCopy.getRank() == "Private")
      return false;
    else return sourcePieceCopy.getPiecePower() > targetPieceCopy.getPiecePower();
  }

  /**
   * Checks if the target piece is ranked Flag.
   * @return boolean true if target piece is Flag, else false.
   */
  private boolean isTargetPieceFlag() {
      return targetPieceCopy.getRank() == "Flag";
  }

  /**
   * Checks if the source piece is ranked Flag.
   * @return boolean true if source piece is Flag, else false.
   */
  private boolean isSourcePieceFlag() {
      return sourcePieceCopy.getRank() == "Flag";
  }

  /**
   * Checks if the Flag piece has succeeded maneuvering into opposite end row
   * of the Board without being eliminated.
   * @return boolean true if Flag has succeeded, else false.
   */
  private boolean isFlagSucceeded() {
    if (sourcePieceCopy.getRank() == "Flag" &&
        board.getTile(targetTileCoords).isTileEmpty())
      // Check if Flag piece is in the respective opposite end row of the board.
        return (sourcePieceCopy.getPieceAlliance() == Alliance.AXIS &&
                targetTileCoords >= BoardUtils.LAST_ROW_INIT) ||
                (sourcePieceCopy.getPieceAlliance() == Alliance.ALLY &&
                        targetTileCoords < BoardUtils.SECOND_ROW_INIT);

    return false;
  }

  /**
   * Gets the player executing this Move.
   * @return Player player field.
   */
  public Player getPlayer() {
    return this.player;
  }

  /**
   * Gets the source Tile from Board.
   * @return Tile from the Board.
   */
  public Tile getSourceTile() {
    return board.getTile(sourceTileCoords);
  }

  /**
   * Gets the source piece destination or target Tile coordinates.
   * @return int target Tile index or ID.
   */
  public int getDestinationCoords() {
    return this.targetTileCoords;
  }

  /**
   * Gets the source piece or Tile coordinates.
   * @return int source piece or Tile index or ID.
   */
  public int getOriginCoords() {
    return this.sourceTileCoords;
  }

  /**
   * Gets the move type of this Move instance.
   * @return String moveType field. Null if uninitialized.
   */
  public String getMoveType() {
    if (this.moveType != null)
      return this.moveType;

    return null;
  }

  /**
   * Gets the turn ID of this Move instance.
   * @return int turnId field. -1 if not set.
   */
  public int getTurnId() {
    if (this.turnId != -1)
      return this.turnId;

    return -1;
  }

  /**
   * Gets the eliminated piece of the attacking execution.
   * @return Piece the eliminated piece after attacking engagement.
   */
  public Piece getEliminatedPiece() {
    if (eliminatedPiece != null)
      return this.eliminatedPiece;

    return null;
  }

  /**
   * Gets the source piece of this Move instance.
   * @return Piece sourcePieceCopy field. Null if uninitialized.
   */
  public Piece getSourcePiece() {
    if (this.sourcePieceCopy != null)
      return this.sourcePieceCopy;

    return null;
  }

  /**
   * Gets the target piece of this Move instance.
   * @return Piece targetPieceCopy field. Null if uninitialized.
   */
  public Piece getTargetPiece() {
    if (this.targetPieceCopy != null)
      return this.targetPieceCopy;

    return null;
  }

  /**
   * Undo or set Move execution to false.
   * @return boolean true if successful, else false if already false.
   */
  public boolean undoExecution() {
    if (!this.isExecuted)
      return false;

    this.isExecuted = false;
    return true;
  }

  /**
   * Redo or set Move execution to true
   * @return boolean true if successful, else false if already true.
   */
  public boolean redoExecution() {
    if (this.isExecuted)
      return false;

    this.isExecuted = true;
    return true;
  }

  /**
   * Checks if this Move instance has been executed already.
   * @return boolean true if is executed, else false.
   */
  public boolean isMoveExecuted() {
    return this.isExecuted;
  }

  /**
   * Sets the execution state to true or false.
   * @param isExecuted boolean
   */
  public void setExecutionState(final boolean isExecuted) {
    this.isExecuted = isExecuted;
  }

  /**
   * Sets the move type of this Move instance.
   * @param moveType String move type to set.
   */
  public void setMoveType(final String moveType) {
    this.moveType = moveType;
  }

  /**
   * Sets the turn ID of this move instance.
   * @param turnId int turn id
   */
  public void setTurnId(final int turnId) {
    this.turnId = turnId;
  }

  @Override
  public String toString() {
    final Alliance sourcePieceAlliance = sourcePieceCopy == null ? null : sourcePieceCopy.getPieceAlliance();
    final String sourcePiece = sourcePieceCopy == null ? "" : sourcePieceCopy.getRank();
    final String targetPiece = targetPieceCopy == null ? "" : targetPieceCopy.getRank();

    if (isExecuted) {
      String superiorPieceAlliance = "";
      if (this.moveType == "attacking") {
        superiorPieceAlliance = eliminatedPiece.getPieceAlliance() == Alliance.AXIS ?
          " " + Alliance.ALLY: " " + Alliance.AXIS;
      }
      return "Turn " + this.turnId + ": " +
        sourcePieceAlliance + " " +
        sourcePiece + " " +
        sourceTileCoords + " to " +
        targetPiece + targetTileCoords + " " +
        this.moveType + superiorPieceAlliance +
        " EXECUTED";
    } else {
      return "Turn " + this.turnId + ": " +
        sourcePieceAlliance + " " +
        sourcePiece + " " +
        sourceTileCoords + " to " +
        targetPiece + targetTileCoords + " ";
    }
  }
}
