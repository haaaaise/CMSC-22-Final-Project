package engine;

import engine.pieces.*;
import engine.player.Player;
import gui.BoardPanel;
import utils.BoardUtils;
import utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The backbone of the entire project, which can be considered a God class since it is not polished yet.
 * Main class that orchestrates all the classes of the engine package and gui.
 * This class serves as the driver of the program that builds the board game
 * from scratch using internal and external classes.
 * Uses 1D array for as coordinate system. 9x8 board with 0 to 71 tile indices.
 * Contains BoardBuilder and Tile inner classes.
 * The code is mainly resourced and modified from: https://github.com/amir650/AxisWidow-Chess

 * Author: Brethren de la Gente
 * Date: 2021-01-07
 */
@SuppressWarnings("unused")
public class Board {

  /** List of all Tiles that contains data of each piece */
  private static List<Tile> gameBoard;

  /** Player instance that all contains all infos on axis pieces */
  private static Player playerAxis;

  /** Player instance that all contains all infos on ally pieces */
  private static Player playerAlly;

  /** Axis player's name assigned when game initialized */
  private static String playerAxisName;

  /** Ally player's name assigned when game initialized */
  private static String playerAllyName;

  /** Axis pieces counter */
  private static int axisPiecesLeft = 0;

  /** Ally pieces counter */
  private static int allyPiecesLeft = 0;

  /** Board builder instance */
  private BoardBuilder customBuilder;

  /** BoardPanel gui instance */
  private BoardPanel boardPanel;

  /** Board initial configurations for saving game state */
  private List<Tile> initBoardConfig;

  /** Game initialization checker */
  private boolean gameInitialized = false;

  /** Game started checker */
  private boolean gameStarted = false;

  /** Debug mode toggle for debugging purposes */
  private static boolean debugMode;

  /** Current turn counter */
  private int currentTurn;

  /** Holds value of last executed turn. Only changes when making a move */
  private int lastExecutedTurn;

  /** Reference to most recent move */
  private Move lastMove;

  /** Reference to most recent invalid move */
  private Move lastInvalidMove;

  /** First move maker */
  private Alliance firstMoveMaker;

  /** Current move maker */
  private Alliance moveMaker;

  /** End game winner */
  private Alliance endGameWinner;

  public Board(BoardBuilder demoBoardBuild) {}

  /**
   * Constructor that takes in two Player instance.
   * @param playerAxis is a player
   * @param playerAlly is a player
   */
  public Board(final Player playerAxis, final Player playerAlly) {
    playerAxis.setBoard(this);
    playerAlly.setBoard(this);
    Board.playerAxis = playerAxis;
    Board.playerAlly = playerAlly;
  }

  /**
   * Method that empties board Tiles pieces.
   */
  public void emptyBoard() {
    gameBoard = new ArrayList<Tile>();
    // Add new empty Tiles in board
    for (int i = 0; i < BoardUtils.ALL_TILES_COUNT; i++) {
      // Set Tile territory
      if (i < BoardUtils.ALL_TILES_COUNT / 2)
        this.addTile(i, Alliance.AXIS);
      else
        this.addTile(i, Alliance.ALLY);
    }
  }

  /**
   * Method that builds board pieces initial arrangement.
   * Depends on BoardBuilder inner class.
   */
  public void buildBoard() {
    this.emptyBoard();

    // Use custom build if exists, else randomly placed pieces build.
    final BoardBuilder builder = this.customBuilder == null ?
      new BoardBuilder().createRandomBuild() : this.customBuilder;

    // Insert pieces to Board Tiles based on build config.
    for (final Map.Entry<Integer, Piece> entry : builder.boardConfig.entrySet()) {
      // insert piece to Tile if empty
      if (gameBoard.get(entry.getKey()).isTileEmpty()) {
        gameBoard.get(entry.getKey()).insertPiece(entry.getValue());
      }
    }
      axisPiecesLeft = builder.getAxisPiecesCount();
    allyPiecesLeft = builder.getAllyPiecesCount();
  }

  /**
   * Method that sets the builder for this board.
   * @param builder BoardBuilder instance.
   */
  public void setBoardBuilder(final BoardBuilder builder) {
    this.customBuilder = builder;
  }

  /**
   * Method that initializes game. Enters initialize mode where players may
   * arrange their respective board pieces.
   */
  public void initGame() {
    buildBoard();

    // Initialize players
    try {
      playerAxis.initPlayer();
    } catch(final NullPointerException e) {
      System.out.println("E: AXIS player has not been assigned");
    }
    try {
      playerAlly.initPlayer();
    } catch(final NullPointerException e) {
      System.out.println("E: ALLY player has not been assigned");
    }

    this.gameInitialized = true;
    setMoveMaker(playerAlly); // TODO: Option to pick first move

    // Displays Board GUI
    displayBoard();
    this.boardPanel.arrangeMode();

    if (isDebugMode())
      System.out.println("Board:\n" + this);
  }

  /**
   * Method that switches to opposing player a change to arrange pieces.
   */
  public void playerDoneArranging() {
    if (this.getAxisPlayer().isMoveMaker()) {
      setMoveMaker(playerAlly);
    } else {
      setMoveMaker(playerAxis);
    }
  }

  /**
   * Method that starts game. Disables initialize mode and enters actual game.
   * Pieces no longer allowed to be arranged in this state.
   */
  public void startGame() {
    this.gameStarted = true;
    this.gameInitialized = false;
    this.currentTurn = 1;
    this.lastExecutedTurn = 0;
    this.boardPanel.startMode();

    if (getMoveMaker() == Alliance.AXIS)
      this.firstMoveMaker = Alliance.AXIS;
    else
      this.firstMoveMaker = Alliance.ALLY;

    // Save initial board arrangement for saving and loading game state.
    this.initBoardConfig = new ArrayList<>();
    for (int i = 0; i < gameBoard.size(); i++) {
      // Make copies of all Tiles in gameBoard into initBoardConfig.
      initBoardConfig.add(gameBoard.get(i).clone());
    }

    if (isDebugMode()) {
      System.out.println(this);
      System.out.println("CurrentTurn: " + currentTurn + "\n" +
                         "TotalPieces: " + (axisPiecesLeft + allyPiecesLeft) + "\n");
    }
  }

  /**
   * Method that Resumes ongoing game state.
   */
  public void resumeGame() {
    this.gameStarted = true;
    this.gameInitialized = false;

    if (isDebugMode()) {
      System.out.println("Game resumed");
    }
  }

  /**
   * Restarts and rebuild game with custom or randon build.
   */
  public void restartGame() {
    buildBoard();
    this.gameStarted = false;
    this.gameInitialized = true;
    this.endGameWinner = null;
    setMoveMaker(playerAlly);

    // Go into arrange mode
    this.boardPanel.arrangeMode();
  }

  /**
   * Game initialization checker.
   * @return boolean gameInitialized field.
   */
  public boolean isGameInitialized() {
    return gameInitialized;
  }

  /**
   * Game started checker.
   * @return boolean isGameStarted field.
   */
  public boolean isGameStarted() {
    return gameStarted;
  }

  /**
   * Gets List of Tile that contains initial board
   * pieces arrangement.
   * @return List<Tile> initBoardConfig field.
   */
  public List<Tile> getInitBoardConfig() {
    return this.initBoardConfig;
  }

  /**
   * Method that displays Board via GUI BoardPanel instance.
   */
  public void displayBoard() {
    this.boardPanel = new BoardPanel(this);
  }

  /**
   * Gets boardPanel field instance.
   * @return BoardPanel boardPanel field.
   */
  public BoardPanel getBoardPanel() {
    return this.boardPanel;
  }

  /**
   * Method that sets debug mode state.
   */
  public void setDebugMode(final boolean debug) {
    debugMode = debug;
  }

  /**
   * Debug mode checker method. Used in static way by other classes.
   */
  public static boolean isDebugMode() {
    return debugMode;
  }

  /**
   * Gets specific tile from gameBoard field.
   * @param tileId tile number.
   * @return Tile from gameBoard field List.
   */
  public Tile getTile(final int tileId) {
    return gameBoard.get(tileId);
  }

  /**
   * Gets current board state.
   * @return List<Tile> gameBoard field.
   */
  public List<Tile> getBoard() {
    return gameBoard;
  }

  /**
   * Swaps two pieces and update piece coordinates.
   * @param sourcePieceCoords source piece coordinates.
   * @param targetPieceCoords target piece coordinates.
   * @return boolean true if successful, else false.
   */
  public boolean swapPiece(final int sourcePieceCoords, final int targetPieceCoords) {
    if (this.getTile(sourcePieceCoords).isTileOccupied() &&
        this.getTile(targetPieceCoords).isTileOccupied()) {
      final Piece sourcePiece = this.getTile(sourcePieceCoords).getPiece().clone();
      final Piece targetPiece = this.getTile(targetPieceCoords).getPiece().clone();
      sourcePiece.setPieceCoords(targetPieceCoords);
      targetPiece.setPieceCoords(sourcePieceCoords);
      this.getBoard().get(sourcePieceCoords).replacePiece(targetPiece);
      this.getBoard().get(targetPieceCoords).replacePiece(sourcePiece);

      return true;
    }
    return false;
  }

  /**
   * Replaces Tile piece.
   * @param targetCoords target occupied tile to replace.
   * @param sourcePiece new Piece instance to replace with.
   * @return boolean true if successful, else false.
   */
  public boolean replacePiece(final int targetCoords, final Piece sourcePiece) {
    if (this.getTile(targetCoords).isTileOccupied()) {
      // TODO: improve piece manipulation efficiency
      sourcePiece.setPieceCoords(targetCoords);
      this.getBoard().get(targetCoords).replacePiece(sourcePiece);
      this.getTile(targetCoords).replacePiece(sourcePiece);

      return true;
    }
    return false;
  }

  /**
   * Moves piece from one Tile to another.
   * @param sourcePieceCoords source piece coordinates.
   * @param targetPieceCoords targetPiece coordinates.
   * @return boolean true if successful, else false.
   */
  public boolean movePiece(final int sourcePieceCoords, final int targetPieceCoords) {
    // insert copy of source piece into target tile
    if (this.getTile(targetPieceCoords).isTileEmpty()) {
      final Piece sourcePieceCopy = this.getTile(sourcePieceCoords).getPiece().clone();
      sourcePieceCopy.setPieceCoords(targetPieceCoords);
      this.getTile(targetPieceCoords).insertPiece(sourcePieceCopy);
      // delete source piece
      this.getTile(sourcePieceCoords).removePiece();

      return true;
    }
    return false;
  }

  /**
   * Inserts piece into an empty tile.
   * @param sourcePieceCoords source piece coordinates.
   * @param piece Piece instance to insert.
   * @return boolean true if successful, else false.
   */
  public boolean insertPiece(final int sourcePieceCoords, final Piece piece) {
    if (this.getTile(sourcePieceCoords).isTileEmpty()) {
      piece.setPieceCoords(sourcePieceCoords);
      this.getBoard().get(sourcePieceCoords).insertPiece(piece);
      this.getTile(sourcePieceCoords).insertPiece(piece);
      return true;
    }
    return false;
  }

  /**
   * Deletes occupied tile.
   * @param pieceCoords piece coordinates.
   * @return boolean true if successful, else false.
   */
  public boolean deletePiece(final int pieceCoords) {
    if (this.getTile(pieceCoords).isTileOccupied()) {
      this.getTile(pieceCoords).removePiece();

      if (isDebugMode())
        System.out.println(this);

      return true;
    }
    return false;
  }

  /**
   * Method that adds Tile into gameBoard field.
   * @param tileId tile id.
   * @param territory tile territory Alliance.
   * @param occupied is tile occupied by a piece.
   */
  private void addTile(final int tileId, final Alliance territory) {
    gameBoard.add(new Tile(tileId, territory));
  }

  /**
   * Switches move maker to opposing player.
   */
  public void switchMoveMakerPlayer() {
    if (this.getAxisPlayer().isMoveMaker()) {
      setMoveMaker(playerAlly);
    } else {
      setMoveMaker(playerAxis);
    }

    if (isDebugMode())
      System.out.println(this.getMoveMaker());
  }

  /**
   * Setter method that sets last executed turn.
   * @param turn turn to replace the last executed.
   */
  public void updateLastExecutedTurn(final int turn) {
    this.lastExecutedTurn = turn;
  }

  /**
   * Increments game turn.
   */
  public void incrementTurn() {
    this.currentTurn++;

    if (isDebugMode()) {
      System.out.println(this);
      System.out.println("Current Turn: " + currentTurn + "\n");
    }
  }

  /**
   * Decrements game turn.
   */
  public void decrementTurn() {
    this.currentTurn--;

    if (isDebugMode()) {
      System.out.println(this);
      System.out.println("Current Turn: " + currentTurn + "\n");
    }
  }

  /**
   * Gets first move maker.
   * @return Alliance firstMoveMaker field.
   */
  public Alliance getFirstMoveMaker() {
    return this.firstMoveMaker;
  }

  /**
   * Gets last executed turn.
   * @return int lastExecutedTurn field.
   */
  public int getLastExecutedTurn() {
    return this.lastExecutedTurn;
  }

  /**
   * Gets game current turn.
   * @return int currentTurn field.
   */
  public int getCurrentTurn() {
    return this.currentTurn;
  }

  /**
   * Sets first move maker.
   * @param firstMoveMaker first move maker Alliance.
   */
  public void setFirstMoveMaker(final Alliance firstMoveMaker) {
    this.firstMoveMaker = firstMoveMaker;
  }

  /**
   * Sets last executed turn.
   * @param lastExecutedTurn last move execution turn.
   */
  public void setLastExecutedTurn(final int lastExecutedTurn) {
    this.lastExecutedTurn = lastExecutedTurn;
  }

  /**
   * Sets current turn.
   * @param turn turn to replace the game current turn.
   */
  public void setCurrentTurn(final int turn) {
    this.currentTurn = turn;
  }

  /**
   * Gets the most recent Move.
   * @return Move lastMove field, else null.
   */
  public Move getLastMove() {
    if (this.lastMove != null)
      return this.lastMove;

    return null;
  }

  /**
   * Gets the most recent invalid move.
   * @return Move lastInvalidMove field, else null.
   */
  public Move getLastInvalidMove() {
    if (this.lastInvalidMove != null)
      return this.lastInvalidMove;

    return null;
  }

  /**
   * Sets last Move field.
   * @param move last executed Move.
   */
  public void setLastMove(final Move move) {
    this.lastMove = move;
  }

  /**
   * Sets last invalid Move field.
   * @param move last invalid Move.
   */
  public void setLastInvalidMove(final Move move) {
    this.lastInvalidMove = move;
  }

  /**
   * Sets the required axis Player instance.
   * @param player axis Player instance.
   */
  public void setPlayerAxis(final Player player) {
    player.setBoard(this);
    playerAxis = player;
  }

  /**
   * Sets the required ally Player instance.
   * @param player ally Player instance.
   */
  public void setPlayerAlly(final Player player) {
    player.setBoard(this);
    playerAlly = player;
  }

  /**
   * Gets specific Player currently registered in this Board based on the
   * alliance.
   * @param alliance Alliance of the Player.
   * @return Player based on the alliance param.
   */
  public Player getPlayer(final Alliance alliance) {
    if (alliance == Alliance.AXIS)
      return playerAxis;
    else
      return playerAlly;
  }

  /**
   * Gets the axis Player.
   * @return Player playerAxis field. Null if uninitialized.
   */
  public Player getAxisPlayer() {
    if (playerAxis != null)
      return playerAxis;

    return null;
  }

  /**
   * Gets the ally Player.
   * @return Player playerAlly field. Null if uninitialized.
   */
  public Player getAllyPlayer() {
    if (playerAlly != null)
      return playerAlly;

    return null;
  }

  /**
   * Checks if a Player of the same Alliance exists in this Board instance.
   * @return true if a player already exists, else false.
   */
  public boolean isPlayerExisting(final Player player) {
      return (player.getAlliance() == Alliance.AXIS && getAxisPlayer() != null) ||
              (player.getAlliance() == Alliance.ALLY && getAllyPlayer() != null);
  }

  /**
   * Gets the axis player designated name.
   * @return String playerAxisName field.
   */
  public String getAxisPlayerName() {
    return playerAxisName;
  }

  /**
   * Gets the ally player designated name.
   * @return String playerAllyName.
   */
  public String getAllyPlayerName() {
    return playerAllyName;
  }

  /**
   * Sets the axis player name.
   */
  public void setAxisPlayerName(final String playerName) {
    playerAxisName = playerName;
  }

  /**
   * Sets the ally player name.
   */
  public void setAllyPlayerName(final String playerName) {
    playerAllyName = playerName;
  }

  /**
   * Gets the current move maker
   * @return Alliance moveMaker field.
   */
  public Alliance getMoveMaker() {
    return this.moveMaker;
  }

  /**
   * Set the current move maker player.
   * @param player Player to replace current move maker.
   * @return boolean true if successful, else false.
   */
  public boolean setMoveMaker(final Player player) {
    if (!player.isMoveMaker()) {
      if (player.getAlliance() == Alliance.AXIS) {
        playerAxis.setMoveMaker(true);
        playerAlly.setMoveMaker(false);
        this.moveMaker = playerAxis.getAlliance();
      } else {
        playerAxis.setMoveMaker(false);
        playerAlly.setMoveMaker(true);
        this.moveMaker = playerAlly.getAlliance();
      }
      return true;
    }
    System.out.println("E: " + player.getAlliance() +
                       " player is already the move maker");
    return false;
  }

  /**
   * Method to check if a player has won.
   * @return boolean true if endGameWinner has been initialized, else false.
   */
  public boolean isEndGame() {
      return this.endGameWinner != null;
  }

  /**
   * Gets the end game winner.
   * @return Alliance endGameWinner field if not null, else null.
   */
  public Alliance getEndGameWinner() {
    if (this.endGameWinner != null) {
      return this.endGameWinner;
    }
    return null;
  }

  /**
   * Sets the end game winner.
   * @param endGameWinner Alliance of the game winner.
   * @return boolean true if successful, else false.
   */
  public boolean setEndGameWinner(final Alliance endGameWinner) {
    if (endGameWinner != null) {
      this.endGameWinner = endGameWinner;
      return true;
    }
    return false;
  }

  @Override
  public String toString() {
    String debugBoard = "\n    0 1 2 3 4 5 6 7 8\n";
    debugBoard += "    _________________\n";
    for (int i = 0; i < BoardUtils.ALL_TILES_COUNT / 2; i += 9) {
      if (i < 10)
        debugBoard += " " + i + " |";
      else
        debugBoard += i + " |";
      for (int j = i; j < i + 9; j++) {
        if (this.getTile(j).isTileEmpty()) {
          debugBoard += "-";
        } else {
          final String rank = this.getTile(j).getPiece().getRank();
          debugBoard += rank.substring(0, 1);
        }
        debugBoard += " ";
      }
      debugBoard += "\n";
    }

    debugBoard += "   |-----------------\n";

    for (int i = BoardUtils.ALL_TILES_COUNT / 2; i < BoardUtils.ALL_TILES_COUNT; i += 9) {
      if (i < 10)
        debugBoard += " " + i + " |";
      else
        debugBoard += i + " |";
      for (int j = i; j < i + 9; j++) {
        if (this.getTile(j).isTileEmpty()) {
          debugBoard += "-";
        } else {
          final String rank = this.getTile(j).getPiece().getRank();
          debugBoard += rank.substring(0, 1);
        }
        debugBoard += " ";
      }
      debugBoard += "\n";
    }

    return debugBoard;
  }

  public void buildBoard(BoardBuilder demoBoardBuild) {
  }

  /**
   * Class for building board configurations. This class sets and arranges the
   * piece of each Tile of the Board. Required for Board initialization.
   */
  @SuppressWarnings("unused")
  public static class BoardBuilder {

    /** HashMap of board configuration that contains all designated pieces */
    private final Map<Integer, Piece> boardConfig;

    /** Axis pieces counter */
    private int axisPiecesCount;

    /** Ally pieces counter */
    private int allyPiecesCount;

    /** No argument constructor that initializes all class fields. */
    public BoardBuilder() {
      this.boardConfig = new HashMap<>();
      this.axisPiecesCount = 0;
      this.allyPiecesCount = 0;
    }

    /**
     * Gets axis pieces count added to boardConfig field.
     * @return int axisPiecesCount field.
     */
    public int getAxisPiecesCount() {
      return axisPiecesCount;
    }

    /**
     * Gets ally pieces count added to boardConfig field.
     * @return int allyPiecesCount field.
     */
    public int getAllyPiecesCount() {
      return axisPiecesCount;
    }

    /**
     * Method that creates a sample demo board configuration.
     * @return this with pre-made board configuration.
     */
    public BoardBuilder createDemoBoardBuild() {
      // Start Tile row index.
      final int[] row = {0, 8, 17, 26};

      // Axis territory
      int boardOffset = 0;

      // row 0
      setPiece(new GeneralTwo(playerAxis, Alliance.AXIS, boardOffset + row[0] + 9));
      setPiece(new Major(playerAxis, Alliance.AXIS, boardOffset + row[0] + 8));
      setPiece(new Private(playerAxis, Alliance.AXIS, boardOffset + row[0] + 7));
      setPiece(new Sergeant(playerAxis, Alliance.AXIS, boardOffset + row[0] + 6));
      setPiece(new LtOne(playerAxis, Alliance.AXIS, boardOffset + row[0] + 5));
      setPiece(new Private(playerAxis, Alliance.AXIS, boardOffset + row[0] + 4));
      setPiece(new Flag(playerAxis, Alliance.AXIS, boardOffset + row[0] + 3));
      setPiece(new LtTwo(playerAxis, Alliance.AXIS, boardOffset + row[0] + 2));
      setPiece(new Private(playerAxis, Alliance.AXIS, boardOffset + row[0] + 1));
      // row 1
      setPiece(new Agent(playerAxis, Alliance.AXIS, boardOffset + row[1] + 8));
      setPiece(new Private(playerAxis, Alliance.AXIS, boardOffset + row[1] + 7));
      setPiece(new Captain(playerAxis, Alliance.AXIS, boardOffset + row[1] + 5));
      setPiece(new Agent(playerAxis, Alliance.AXIS, boardOffset + row[1] + 4));
      setPiece(new Colonel(playerAxis, Alliance.AXIS, boardOffset + row[1] + 3));
      setPiece(new Private(playerAxis, Alliance.AXIS, boardOffset + row[1] + 2));
      setPiece(new LtCol(playerAxis, Alliance.AXIS, boardOffset + row[1] + 1));
      // row 2
      setPiece(new GeneralThree(playerAxis, Alliance.AXIS, boardOffset + row[2] + 9));
      setPiece(new Private(playerAxis, Alliance.AXIS, boardOffset + row[2] + 6));
      setPiece(new GeneralFour(playerAxis, Alliance.AXIS, boardOffset + row[2] + 5));
      // row 3
      setPiece(new GeneralOne(playerAxis, Alliance.AXIS, boardOffset + row[3] + 3));
      setPiece(new GeneralFive(playerAxis, Alliance.AXIS, boardOffset + row[3] + 2));
      setPiece(new GeneralFive(playerAlly, Alliance.ALLY, boardOffset + row[3] + 1));

      // Ally territory
      boardOffset = BoardUtils.ALL_TILES_COUNT / 2;

      // row 0
      setPiece(new GeneralFive(playerAlly, Alliance.AXIS, boardOffset + row[0] + 1));
      setPiece(new GeneralFive(playerAlly, Alliance.ALLY, boardOffset + row[0] + 2));
      setPiece(new GeneralOne(playerAlly, Alliance.ALLY, boardOffset + row[0] + 3));
      // row 1
      setPiece(new GeneralFour(playerAlly, Alliance.ALLY, boardOffset + row[1] + 5));
      setPiece(new Private(playerAlly, Alliance.ALLY, boardOffset + row[1] + 6));
      setPiece(new GeneralThree(playerAlly, Alliance.ALLY, boardOffset + row[1] + 9));
      // row 2
      setPiece(new LtCol(playerAlly, Alliance.ALLY, boardOffset + row[2] + 1));
      setPiece(new Private(playerAlly, Alliance.ALLY, boardOffset + row[2] + 2));
      setPiece(new Colonel(playerAlly, Alliance.ALLY, boardOffset + row[2] + 3));
      setPiece(new Agent(playerAlly, Alliance.ALLY, boardOffset + row[2] + 4));
      setPiece(new Captain(playerAlly, Alliance.ALLY, boardOffset + row[2] + 5));
      setPiece(new Private(playerAlly, Alliance.ALLY, boardOffset + row[2] + 7));
      setPiece(new Agent(playerAlly, Alliance.ALLY, boardOffset + row[2] + 8));
      // row 3
      setPiece(new Private(playerAlly, Alliance.ALLY, boardOffset + row[3] + 1));
      setPiece(new LtTwo(playerAlly, Alliance.ALLY, boardOffset + row[3] + 2));
      setPiece(new Flag(playerAlly, Alliance.ALLY, boardOffset + row[3] + 3));
      setPiece(new Private(playerAlly, Alliance.ALLY, boardOffset + row[3] + 4));
      setPiece(new LtOne(playerAlly, Alliance.ALLY, boardOffset + row[3] + 5));
      setPiece(new Sergeant(playerAlly, Alliance.ALLY, boardOffset + row[3] + 6));
      setPiece(new Private(playerAlly, Alliance.ALLY, boardOffset + row[3] + 7));
      setPiece(new Major(playerAlly, Alliance.ALLY, boardOffset + row[3] + 8));
      setPiece(new GeneralTwo(playerAlly, Alliance.ALLY, boardOffset + row[3] + 9));

      return this;
    }

    /**
     * Method that creates random board configuration.
     * @return this with random board configuration.
     */
    public BoardBuilder createRandomBuild() {
      final int[] occupiedTiles = {};

      if (isDebugMode())
        System.out.println("Inserting random pieces...");

      // Axis pieces
      final int[] axisTerritoryBounds = {0, (BoardUtils.ALL_TILES_COUNT / 2) - 1};
      final List<Piece> unsetAxisPieces = new ArrayList<>();

      unsetAxisPieces.add(new GeneralFive(playerAxis, Alliance.AXIS));
      unsetAxisPieces.add(new GeneralFour(playerAxis, Alliance.AXIS));
      unsetAxisPieces.add(new GeneralThree(playerAxis, Alliance.AXIS));
      unsetAxisPieces.add(new GeneralTwo(playerAxis, Alliance.AXIS));
      unsetAxisPieces.add(new GeneralOne(playerAxis, Alliance.AXIS));
      unsetAxisPieces.add(new Colonel(playerAxis, Alliance.AXIS));
      unsetAxisPieces.add(new LtCol(playerAxis, Alliance.AXIS));
      unsetAxisPieces.add(new Major(playerAxis, Alliance.AXIS));
      unsetAxisPieces.add(new Captain(playerAxis, Alliance.AXIS));
      unsetAxisPieces.add(new LtOne(playerAxis, Alliance.AXIS));
      unsetAxisPieces.add(new LtTwo(playerAxis, Alliance.AXIS));
      unsetAxisPieces.add(new Sergeant(playerAxis, Alliance.AXIS));
      unsetAxisPieces.add(new Private(playerAxis, Alliance.AXIS));
      unsetAxisPieces.add(new Flag(playerAxis, Alliance.AXIS));
      unsetAxisPieces.add(new Agent(playerAxis, Alliance.AXIS));

      // Sets axis pieces randomly excluding already occupied tiles.
      for (final Piece unsetPiece : unsetAxisPieces) {
        setAllPieceInstanceRandomly(
            this, unsetPiece, axisTerritoryBounds[0],
            axisTerritoryBounds[1], occupiedTiles);
      }

      // Ally pieces
      final int[] allyTerritoryBounds = {BoardUtils.ALL_TILES_COUNT / 2,
        (BoardUtils.ALL_TILES_COUNT) - 1};
      final List<Piece> unsetAllyPieces = new ArrayList<>();

      unsetAllyPieces.add(new GeneralFive(playerAlly, Alliance.ALLY));
      unsetAllyPieces.add(new GeneralFour(playerAlly, Alliance.ALLY));
      unsetAllyPieces.add(new GeneralThree(playerAlly, Alliance.ALLY));
      unsetAllyPieces.add(new GeneralTwo(playerAlly, Alliance.ALLY));
      unsetAllyPieces.add(new GeneralOne(playerAlly, Alliance.ALLY));
      unsetAllyPieces.add(new Colonel(playerAlly, Alliance.ALLY));
      unsetAllyPieces.add(new LtCol(playerAlly, Alliance.ALLY));
      unsetAllyPieces.add(new Major(playerAlly, Alliance.ALLY));
      unsetAllyPieces.add(new Captain(playerAlly, Alliance.ALLY));
      unsetAllyPieces.add(new LtOne(playerAlly, Alliance.ALLY));
      unsetAllyPieces.add(new LtTwo(playerAlly, Alliance.ALLY));
      unsetAllyPieces.add(new Sergeant(playerAlly, Alliance.ALLY));
      unsetAllyPieces.add(new Private(playerAlly, Alliance.ALLY));
      unsetAllyPieces.add(new Flag(playerAlly, Alliance.ALLY));
      unsetAllyPieces.add(new Agent(playerAlly, Alliance.ALLY));

      // Sets ally pieces randomly excluding already occupied tiles.
      for (final Piece unsetPiece : unsetAllyPieces) {
        setAllPieceInstanceRandomly(
            this, unsetPiece, allyTerritoryBounds[0],
            allyTerritoryBounds[(1)], occupiedTiles);
      }

      return this;
    }

    /**
     * Sets piece in designated Tile location.
     * @param piece Piece instance to insert into specific Tile.
     * @return boolean true if successful, else false.
     */
    public boolean setPiece(final Piece piece) {
      // checks if within bounds, correct territory, and piece legal count
      if (isPieceWithinBounds(piece) &&
          isPieceInCorrectTerritory(piece) &&
          isLegalPieceInstanceChecker(piece) &&
          isTileEmpty(piece.getPieceCoords())) {
        boardConfig.put(piece.getPieceCoords(), piece);

        if (piece.getPieceAlliance() == Alliance.AXIS)
          this.axisPiecesCount++;
        else
          this.allyPiecesCount++;

        if (isDebugMode())
          System.out.println(
              piece.getPieceAlliance() + " " +
              piece.getRank() + " piece inserted at " +
              piece.getPieceCoords());

        return true;
      }
      return false;
    }

    /**
     * Method thats sets all available amount of a single pieces in random
     * locations within its respective Alliance territory.
     * @param builder BoardBuilder to set the Piece into
     * @param piece Piece to set all legal amount of instance randomly.
     * @param from start index bounds to set the piece/pieces within.
     * @param to end index bounds to set the piece/pieces within.
     * @param occupiedTiles int array that contains all Tile exclusions to stop
     * inserting piece in.
     */
    public void setAllPieceInstanceRandomly(final BoardBuilder builder, final Piece piece,
                                            final int from, final int to,
                                            final int[] occupiedTiles) {
      Piece pieceCopy = piece.clone();
      int pieceInstanceCounter = countPieceInstances(piece.getRank(),
                                                     piece.getPieceAlliance());
      int randomEmptyTile;

      while (pieceInstanceCounter < piece.getLegalPieceInstanceCount()) {
        randomEmptyTile = Utils.getRandomWithExclusion(from, to, occupiedTiles);
        pieceCopy.setPieceCoords(randomEmptyTile);
        // TODO: Fix to check if randomEmptyTile is empty
        if (builder.setPiece(pieceCopy)) {
          pieceCopy = piece.clone();
          Utils.appendToIntArray(occupiedTiles, randomEmptyTile);
          pieceInstanceCounter++;

          if (debugMode)
            System.out.println(piece.getPieceAlliance() + " " +
                piece.getRank() + " random placement successful");
        }
      }
    }

    /**
     * Method that counts all piece instances that has been set into boardConfig
     * field.
     * @param rank Piece rank of the piece to be counted.
     * @param alliance Alliance of the piece to be counted.
     * @return int the count of the specified piece.
     */
    public int countPieceInstances(final String rank, final Alliance alliance) {
      int pieceInstanceCounter = 0;

      // Count all pieces from boardConfig HashMap field.
      for (final Map.Entry<Integer, Piece> entry : boardConfig.entrySet()) {
        if (entry.getValue().getRank() == rank &&
            entry.getValue().getPieceAlliance() == alliance)
          pieceInstanceCounter++;
      }

      return pieceInstanceCounter;
    }

    /**
     * Checks if a Piece to be inserted is within bounds of the Board.
     * @param piece the Piece instance to be checked.
     * @return boolean true if piece is within bounds, else false.
     */
    public boolean isPieceWithinBounds(final Piece piece) {
      if (piece.getPieceCoords() < BoardUtils.ALL_TILES_COUNT &&
          piece.getPieceCoords() >= 0) {
        return true;
      }

      if (isDebugMode())
        System.out.println(piece.getPieceAlliance() + " " +
                           piece.getRank() + " at Tile " +
                           piece.getPieceCoords() + " is out of bounds." +
                           " Piece not inserted.");
      return false;
    }

    /**
     * Checks if a Piece to be inserted is within its respective territory
     * Alliance.
     * @param piece the Piece instance to be checked.
     * @return boolean true if piece is within its respective territory, else
     * false.
     */
    public boolean isPieceInCorrectTerritory(final Piece piece) {
      if ((piece.getPieceAlliance() == Alliance.AXIS &&
            piece.getPieceCoords() < BoardUtils.ALL_TILES_COUNT / 2) ||
          (piece.getPieceAlliance() == Alliance.ALLY &&
            piece.getPieceCoords() > BoardUtils.ALL_TILES_COUNT / 2)) {
        return true;
      }

      if (isDebugMode())
        System.out.println("E: " + piece.getPieceAlliance() + " " +
                           piece.getRank() + " at Tile " +
                           piece.getPieceCoords() + " is in illegal territory." +
                           " Piece not inserted.");
      return false;
    }

    /**
     * Checks if a Piece to be inserted exceeds the amount of allowed instance
     * in a single game.
     * @param piece the Piece instance to be checked.
     * @return boolean true if the piece is still less than or equal the amount
     * of allowed instance of the specific piece.
     */
    public boolean isLegalPieceInstanceChecker(final Piece piece) {
      int pieceInstanceCounter = 0;
      for (final Map.Entry<Integer, Piece> entry : this.boardConfig.entrySet()) {
        if (piece.getRank() == entry.getValue().getRank() &&
            piece.getPieceAlliance() == entry.getValue().getPieceAlliance())
          pieceInstanceCounter++;
      }

      if (pieceInstanceCounter <= piece.getLegalPieceInstanceCount())
        return true;

      if (isDebugMode())
        System.out.println("E: " + piece.getRank() + " exceeded maxismum instance." +
                           " Piece not inserted.");
      return false;
    }

    /**
     * Checks if Tile is empty or does not contain a Piece instance.
     * @return boolean true if Tile is empty, else false.
     */
    public boolean isTileEmpty(final int coords) {
      if (!boardConfig.containsKey(coords))
        return true;

      if (isDebugMode())
        System.out.println("E: Tile " + coords + " is occupied");
      return false;
    }

    /**
     * Gets the boardConfig field.
     * @return Map<Integer, Piece> boardConfig field if not null, else null.
     */
    public Map<Integer, Piece> getBoardConfig() {
      try {
        return this.boardConfig;
      } catch(final NullPointerException e) {
        System.out.println("BuilderBoard Error: Board config does not exist");
        return null;
      }

    }

    @Override
    public String toString() {
      String builder = "BoardBuilder boardConfig=" + boardConfig.size() + "\n";

      for (final Map.Entry<Integer, Piece> entry : boardConfig.entrySet()) {
        builder += "tileId=" + entry.getKey() +
                   ";piece=" + entry.getValue().getRank() +
                   ";pieceAlliance=" + entry.getValue().getPieceAlliance() +
                   "\n";
      }

      return builder;
    }

  } // BoardBuilder

  /**
   * Tile class that will contain a single piece instance. All together,
   * represents the whole board arrangement.
   */
  @SuppressWarnings("unused")
  public static class Tile {

    /** Tile unique index or ID number. From 0 to 71 */
    private final int tileId;

    /** Tile territorial Alliance. 0 - 35 axis and 36 - 71 ally territory. */
    private final Alliance territory;

    /** Is tile occupied by piece */
    private boolean occupied;

    /** Containing piece. Null if empty or remains uninitialized. */
    private Piece piece;

    /**
     * Constructor that takes in the tileId and territorial Alliance, and sets
     * the tile as empty.
     */
    public Tile(final int tileId, final Alliance territory) {
      this.tileId = tileId;
      this.territory = territory;
      this.occupied = false;
    }

    /**
     * Checks if this Tile is empty of Piece instance.
     * @return boolean true if this Tile is empty, else false.
     */
    public boolean isTileEmpty() {
        return !this.occupied;
    }

    /**
     * Checks if this Tile is occupied by Piece instance.
     * @return boolean true if this Tile is occupied, else false.
     */
    public boolean isTileOccupied() {
        return this.occupied;
    }

    /**
     * Returns the Tile index or ID.
     * @return int tileId field.
     */
    public int getTileId() {
      return this.tileId;
    }

    /**
     * Gets the Tile territorial Alliance.
     * @return Alliance territory field.
     */
    public Alliance getTerritory() {
      return this.territory;
    }

    /**
     * Gets the occupying Piece of the Tile.
     * @return the Piece occupying the Tile, else null.
     */
    public Piece getPiece() {
      if (this.occupied)
        return this.piece;
      else
        if (isDebugMode())
          System.out.println("Board.Tile.getPiece() E: Tile Piece does not exist.");

      return null;
    }

    /**
     * Inserts the Piece into this Tile.
     * @param piece the Piece instance to insert.
     * @return boolean true if successful, else false if already occupied.
     */
    public boolean insertPiece(final Piece piece) {
      if (isTileEmpty()) {
        this.piece = piece;
        this.occupied = true;
        return true;
      }

      if (isDebugMode())
        System.out.println("Board.Tile.insertPiece() E: Tile is occupied.");

      return false;
    }

    /**
     * Replaces the occupying Piece with another Piece instance.
     * @param piece the Piece to replace the existing with.
     * @return boolean true if successful, else false if is Tile is empty.
     */
    public boolean replacePiece(final Piece piece) {
      if (isTileOccupied()) {
        this.piece = piece;
        return true;
      }

      if (isDebugMode())
        System.out.println("Board.Tile.replacePiece() E: Tile is empty.");

      return false;
    }

    /**
     * Empties the occupying piece of this Tile.
     * @return boolean true if successful, else false if tile is empty.
     */
    public boolean removePiece() {
      if (isTileOccupied()) {
        this.piece = null;
        this.occupied = false;
        return true;
      }

      if (isDebugMode())
        System.out.println("Board.Tile.removePiece() E: Tile is already empty.");

      return false;
    }

    /**
     * Creates deep copy of this Tile instance.
     * @return Tile deep copy of this Tile instance.
     */
    @Override
    public Tile clone() {
      final Tile tileCopy = new Tile(this.tileId, this.territory);

      if (isTileOccupied())
        tileCopy.insertPiece(getPiece().clone());

      return tileCopy;
    }

    @Override
    public String toString() {
      if (this.occupied)
        return "Tile " + this.tileId + " contains " +
          this.piece.getPieceAlliance() + " " + this.piece.getRank();
      else
        return "Tile " + this.tileId + " is empty";
    }

  } // Tile

} // Board
