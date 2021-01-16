package gui;

import engine.Alliance;
import engine.Board;
import engine.Board.Tile;
import engine.Move;
import engine.pieces.Piece;
import engine.player.Player;
import utils.BoardUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Board JPanel that displays the board game and all its components.
 *
 * Author: Brethren de la Gente
 * Date: 2021-01-15
 *
 */
public class BoardPanel extends JPanel {

  /** Reference to the Board engine */
  private static Board gameStateBoard;

  /** Menu bar and other buttons */
  private static JButton restartBtn,quitBtn, undoBtn,
                         redoBtn,rulesBtn,
                         doneArrangingBtn, startGameBtn;

  /** Player names assigned once game initialized. */
  private static JLabel playerAxisNameLbl, playerAllyNameLbl;

  /** Reference to the menu bar JPanel inner class */
  private static MenuBarPanel menuBarPanel;

  /** Reference to the move history JPanel inner class */
  private static MoveHistoryPanel moveHistoryPanel;

  /** Reference to the inner board JPanel inner class */
  private static InnerBoardPanel boardPanel;

  /** Default TilePanel colors  */
  private final static Color DARK_TILE_COLOR = new Color(69, 68, 68, 255);
  private final static Color LIGHT_TILE_COLOR = new Color(226, 219, 219);

  /** TilePanel highlight colors */
  private final static Color ACTIVE_TILE_COLOR = new Color(245, 153, 51);
  private final static Color ENEMY_TILE_COLOR = new Color(179, 11, 33);
  private final static Color VALID_TILE_COLOR = new Color(50, 168, 29);
  private final static Color INVALID_TILE_COLOR = new Color(115, 9, 234);

  /** Board panel component dimensions */
  private final static Dimension FRAME_DIMENSION = new Dimension(1200, 735);
  private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(900, 700);
  private final static Dimension TILE_PANEL_DIMENSION = new Dimension(100, 100);
  private final static Dimension MENU_BAR_PANEL_DIMENSION =
    new Dimension(
      (int) FRAME_DIMENSION.getWidth(),
      (int) (FRAME_DIMENSION.getHeight() - BOARD_PANEL_DIMENSION.getHeight()));
  private final static Dimension MOVE_HISTORY_PANEL_DIMENSION =
    new Dimension(
      (int) (FRAME_DIMENSION.getWidth() - BOARD_PANEL_DIMENSION.getWidth()),
      (int) (FRAME_DIMENSION.getHeight() - MENU_BAR_PANEL_DIMENSION.getHeight()));

  /**
   * Constructor method that takes in Board engine as parameter.
   */
  public BoardPanel(final Board board) {
    gameStateBoard = board;
    this.setLayout(new BorderLayout());
    this.setVisible(false);

    // Initialize inner classes.
    boardPanel = new InnerBoardPanel();
    menuBarPanel = new MenuBarPanel();
    moveHistoryPanel = new MoveHistoryPanel();

    this.add(boardPanel, BorderLayout.CENTER);
    this.add(menuBarPanel, BorderLayout.NORTH);
    this.add(moveHistoryPanel, BorderLayout.WEST);
  }

  /**
   * Initialize BoardPanel by initializing InnerBoardPanel class.
   */
  public void initBoardPanel() {
    System.out.println("BoardPanel gameStateBoard:\n" + gameStateBoard);
    boardPanel.initInnerBoardPanel();
  }

  /**
   * Hides in-game BoardPanel buttons and show arrange mode buttons.
   */
  public void arrangeMode() {
    clearBoardPanel();
    printOpeningMessage();

    // Set up arrange mode button arrangement
    undoBtn.setVisible(false);
    redoBtn.setVisible(false);
    rulesBtn.setVisible(false);

    doneArrangingBtn.setVisible(true);
    startGameBtn.setVisible(true);
  }

  /**
   * Hides arrange mode board panel buttons and show in-game buttons.
   */
  public void startMode() {
    // Show in-game menu buttons.
    undoBtn.setVisible(true);
    redoBtn.setVisible(true);
    rulesBtn.setVisible(true);
    setPlayerNamesVisibility(true);

    doneArrangingBtn.setVisible(false);
    startGameBtn.setVisible(false);
  }

  /**
   * Gets axis player name JLabel
   * @return JLabel playerAxisNameLbl field.
   */
  public final JLabel getPlayerAxisNameLbl() {
    return playerAxisNameLbl;
  }

  /**
   * Gets ally player name JLabel
   * @return JLabel playerAllyNameLbl field.
   */
  public final JLabel getPlayerAllyNameLbl() {
    return playerAllyNameLbl;
  }

  /**
   * Set the visibility of the Player names in the menu bar panel.
   */
  public void setPlayerNamesVisibility(final boolean visibility) {
    playerAxisNameLbl.setVisible(visibility);
    playerAllyNameLbl.setVisible(visibility);
  }

  /**
   * Gets the restart button of the menu bar panel.
   * @return JButton restartBtn MenuBarPanel field.
   */
  public final JButton getRestartBtn() {
    return restartBtn;
  }


  /**
   * Gets the quit button of the menu bar panel.
   * @return JButton quitBtn MenuBarPanel field.
   */
  public final JButton getQuitBtn() {
    return quitBtn;
  }

  /**
   * Gets the quit button of the menu bar panel.
   * @return JButton quitBtn MenuBarPanel field.
   */
  public final JButton getUndoBtn() {
    return undoBtn;
  }

  /**
   * Gets the redo button of the menu bar panel.
   * @return JButton redoBtn MenuBarPanel field.
   */
  public final JButton getRedoBtn() {
    return redoBtn;
  }


  /**
   * Gets the game rules button of the menu bar panel.
   * @return JButton gameRulesBtn MenuBarPanel field.
   */
  public final JButton getGameRulesBtn() {
    return rulesBtn;
  }

  /**
   * Gets the done arranging button of the move history panel.
   * @return JButton doneArrangingBtn MoveHistoryPanel field.
   */
  public final JButton getDoneArrangingBtn() {
    return doneArrangingBtn;
  }

  /**
   * Gets the start game button of the move history panel.
   * @return JButton startGameBtn MoveHistoryPanel field.
   */
  public final JButton getStartGameBtn() {
    return startGameBtn;
  }

  /**
   * Repaints BoardPanel colors and sets active tile to default null -1.
   */
  public final void repaintBoardPanel() {
    boardPanel.setActiveTile(-1);
    boardPanel.refreshInnerBoardPanelIcons();
    boardPanel.refreshTilesBackgroundColor();
  }

  /**
   * Repaints board panel and clear move history panel text area move entries.
   */
  public final void clearBoardPanel() {
    repaintBoardPanel();
    moveHistoryPanel.clearMoveHistory();
  }


  /**
   * Removes last executed move entry from move history text area.
   */
  public final void undoMoveHistoryUpdate() {
    moveHistoryPanel.removeMoveFromHistory(gameStateBoard.getLastMove());
  }

  /**
   * Reprint move entry executed by redo from move history text area.
   */
  public final void redoMoveHistoryUpdate() {
    moveHistoryPanel.appendToMoveHistory(gameStateBoard.getLastMove());
  }

  /**
   * Print opening message in move history text area.
   */
  public final void printOpeningMessage() {
    if (!gameStateBoard.isGameStarted())
      moveHistoryPanel.printOpeningMessage();
  }

  /**
   * BoardPanel Inner JPanel for menu bar.
   */
  public static class MenuBarPanel extends JPanel {

    /** Menu bar buttons */
    private final JButton restart, quit, undo, redo,rules;

    /** Player names label */
    private final JLabel playerAxisName, playerAllyName;

    /**
     * Constructor method that initializes this MenuBarPanel and all its components
     */
    public MenuBarPanel() {
      this.setLayout(new FlowLayout());
      this.setPreferredSize(MENU_BAR_PANEL_DIMENSION);

      playerAxisName = new JLabel("AXIS PLAYER:");
      playerAxisName.setFont(new Font("SansSerif", Font.BOLD, 16));
      playerAxisName.setVisible(false);
      this.add(playerAxisName);

      this.add(new JSeparator(SwingConstants.HORIZONTAL));
      this.add(new JSeparator(SwingConstants.HORIZONTAL));

      restart = new JButton("Restart");
      this.add(restart);
      quit = new JButton("Quit");
      this.add(quit);

      this.add(new JSeparator(SwingConstants.HORIZONTAL));
      this.add(new JSeparator(SwingConstants.HORIZONTAL));
      this.add(new JSeparator(SwingConstants.HORIZONTAL));

      undo = new JButton("Undo");
      this.add(undo);
      undo.setVisible(false);
      redo = new JButton("Redo");
      this.add(redo);
      redo.setVisible(false);

      this.add(new JSeparator(SwingConstants.HORIZONTAL));
      this.add(new JSeparator(SwingConstants.HORIZONTAL));
      this.add(new JSeparator(SwingConstants.HORIZONTAL));

      rules = new JButton("Game Rules");
      this.add(rules);
      rules.setVisible(false);

      this.add(new JSeparator(SwingConstants.HORIZONTAL));
      this.add(new JSeparator(SwingConstants.HORIZONTAL));

      playerAllyName = new JLabel("ALLY PLAYER:");
      playerAllyName.setFont(new Font("SansSerif", Font.BOLD, 16));
      playerAllyName.setVisible(false);
      this.add(playerAllyName);

      setAllButtons();
    }

    /**
     * Puts all the buttons together in the BoardPanel gui
     */
    public void setAllButtons() {
      playerAxisNameLbl = this.playerAxisName;
      playerAllyNameLbl = this.playerAllyName;
      restartBtn = this.restart;
      quitBtn = this.quit;
      undoBtn = this.undo;
      redoBtn = this.redo;
      rulesBtn = this.rules;
    }

  } // MenuBarPanel

  /**
   * BoardPanel inner JPanel class for move history. Prints out opening message
   * and all executed moves.
   */
  public static class MoveHistoryPanel extends JPanel {

    /** Move history text area */
    private final JTextArea moveHistoryTextArea = new JTextArea();

    /** Move history opening message string */
    private String openingMessage;

    /**
     * Constructor method that initializes this MoveHistoryPanel.
     */
    public MoveHistoryPanel() {
      this.setLayout(new BorderLayout());
      this.setBorder(new EmptyBorder(0, 0, 0, 5));
      this.setPreferredSize(MOVE_HISTORY_PANEL_DIMENSION);
      moveHistoryTextArea.setEditable(false);

      final JLabel label = new JLabel("MOVE HISTORY");
      label.setHorizontalAlignment(JLabel.CENTER);
      label.setVerticalAlignment(JLabel.CENTER);
      label.setFont(new Font("SansSerif", Font.BOLD, 18));

      final JButton doneArrangingBtn = new JButton("Done Arranging");
      final JButton startGameBtn = new JButton("Start Game");
      setDoneArrangingBtn(doneArrangingBtn);
      setStartGameBtn(startGameBtn);

      final JPanel initGameButtonsPanel = new JPanel();
      initGameButtonsPanel.add(doneArrangingBtn);
      initGameButtonsPanel.add(startGameBtn);

      moveHistoryTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      moveHistoryTextArea.setFont(new Font("SansSerif", Font.PLAIN, 14));

      // set scrollable vertically as needed
      final JScrollPane moveHistoryVScrollable = new JScrollPane(moveHistoryTextArea);
      moveHistoryVScrollable.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

      this.add(label, BorderLayout.NORTH);
      this.add(moveHistoryVScrollable, BorderLayout.CENTER);
      this.add(initGameButtonsPanel, BorderLayout.SOUTH);

      printOpeningMessage();
    }

    /**
     * Appends String into move history text area.
     * @param text texts to append into text area.
     */
    public void appendTextToMoveHistory(final String text) {
      moveHistoryTextArea.append(text);
    }

    /**
     * Converts Move into string and appends to move history text area
     * @param move Move to append into text area.
     */
    public void appendToMoveHistory(final Move move) {
      moveHistoryTextArea.append(convertMoveToString(move));
    }

    /**
     * Converts Move to String object to append into move history text area.
     * @param move Move to be converted into String object.
     * @return String version of the move instance.
     */
    public String convertMoveToString(final Move move) {
      String moveString = "";

      if (move.getMoveType() == "attacking") {
        final Alliance superiorPieceAlliance =
          move.getEliminatedPiece().getPieceAlliance() ==
          Alliance.AXIS ? Alliance.ALLY : Alliance.AXIS;

        moveString = "\nTurn " + move.getTurnId() + ": " + move.getOriginCoords() +
                   " to " + move.getDestinationCoords() + " " + superiorPieceAlliance;
      } else if (move.getMoveType() == "draw") {
        moveString = "\nTurn " + move.getTurnId() + ": " + move.getOriginCoords() +
                   " to " + move.getDestinationCoords() + " DRAW";
      } else if (move.getMoveType() == "normal") {
        moveString = "\nTurn " + move.getTurnId() + ": " + move.getOriginCoords() +
                   " to " + move.getDestinationCoords();
      } else {
        // DONE: Do not print INVALID moves in move history text area.
        // Instead show popup or flash board color.
        JOptionPane.showMessageDialog(null, "INVALID MOVE! PLEASE TRY AGAIN");
      }

      return moveString;
    }

    /**
     * Removes specific Move entry from move history text area.
     * @param move move entry to be removed from move history text area.
     */
    // TODO: Fix only removing invalid moves
    public void removeMoveFromHistory(final Move move) {
      if (move != null) {
        removeRecentInvalidMove();

        final String fullMoveHistory = moveHistoryTextArea.getText();
        final String moveString = convertMoveToString(move);

        clearMoveHistory();
        appendTextToMoveHistory(fullMoveHistory.replace(moveString, ""));
      }
    }

    /**
     * Removes invalid Move entry/entries from move history text area.
     */
    public void removeRecentInvalidMove() {
      if (gameStateBoard.getLastInvalidMove() != null) {
        final String fullMoveHistory = moveHistoryTextArea.getText();
        final String invalidMoveString = convertMoveToString(gameStateBoard.getLastInvalidMove());

        clearMoveHistory();
        appendTextToMoveHistory(fullMoveHistory.replace(invalidMoveString, ""));
      }
    }

    /**
     * Clears all text from move history text area.
     */
    public void clearMoveHistory() {
      moveHistoryTextArea.selectAll();
      moveHistoryTextArea.replaceSelection("");
    }

    /**
     * Sets or hoist done arranging button up to parent BoardPanel class for
     * easy access.
     */
    public void setDoneArrangingBtn(final JButton doneArranging) {
      doneArrangingBtn = doneArranging;
    }

    /**
     * Sets or hoist start game button up to parent BoardPanel class for
     * easy access.
     */
    public void setStartGameBtn(final JButton startGame) {
      startGameBtn = startGame;
    }

    /**
     * Clears move history text area and print opening message.
     */
    public void printOpeningMessage() {
      clearMoveHistory();
      String name = "Player";

      // Get current move maker Player name
      final Alliance moveMaker = gameStateBoard.getMoveMaker();

      if (gameStateBoard.getAxisPlayerName() != null && moveMaker == Alliance.AXIS)
        name = gameStateBoard.getAxisPlayerName();
      else if (gameStateBoard.getAllyPlayerName() != null && moveMaker == Alliance.ALLY)
        name = gameStateBoard.getAllyPlayerName();

      openingMessage = "Welcome to World at War(Blitz),\n" + name + "!\n\n" +
        "Please arrange your pieces however\n" +
        "you like within the first three rows \n " + "of your territory (" +
        gameStateBoard.getMoveMaker() + ").\n\n" +
        "Once you are ready, please click the\n" +
        "'Start Game' button below.\n\n" +
        "The one who starts will be the first \n" +
        "to move!";

      moveHistoryTextArea.append(openingMessage);
    }

  } // MoveHistoryPanel

  /**
   * BoardPanel inner JPanel class that displays the inner board panel.
   */
  @SuppressWarnings("unused")
  private static class InnerBoardPanel extends JPanel {

    /** List of all TilePanel inner class instance. */
    private final List<TilePanel> boardTiles;

    /** List of all candidate move tiles of active piece tile. */
    private final List<Integer> candidateMoveTiles;

    /** HashMap of all current active piece Moves. */
    private Map<String, Move> currentPieceMoves;

    /** HasMap of all pre-loaded axis pieces icons. */
    private Map<String, Image> axisPieceIcons;
    private Map<String, Image> allyPieceIcons;

    /** Hover highlight switch for highlighting candidate piece moves. */
    private boolean enableHoverHighlight = true;

    /** Hovered tile ID. */
    private int hoveredTileId;

    /** Current active tile ID. -1 if no active */
    private int activeTileId = -1;

    /**
     * Constructor method of InnerBoardPanel.
     */
    public InnerBoardPanel() {
      this.setLayout(new GridLayout(BoardUtils.TILE_ROW_COUNT, BoardUtils.TILE_COLUMN_COUNT));
      this.boardTiles = new ArrayList<>();
      this.candidateMoveTiles = new ArrayList<>();

      preLoadImages();
      createTilePanels();

      setPreferredSize(BOARD_PANEL_DIMENSION);
      validate();
    }

    /**
     * Creates all TilePanel that will contain piece icons.
     */
    public void createTilePanels() {
      for (int i = 0; i < BoardUtils.ALL_TILES_COUNT; i++) {
        final TilePanel tilePanel = new TilePanel(i);
        this.boardTiles.add(tilePanel);
        add(tilePanel);
      }
    }

    /**
     * Method that initializes InnerBoardPanel by initializing all TilePanels.
     */
    public void initInnerBoardPanel() {
      for (int i = 0; i < boardTiles.size(); i++) {
        this.boardTiles.get(i).initTilePanel();
      }
    }

    /**
     * Highlights Tiles based on current mouse hovered piece candidate Moves
     * and move types. Also sets all highlighted tiles as candidate move tiles.
     */
    // TODO: add tileId on top left of tile panel
    private void highlightPieceMoves(final int tileId) {
      final Tile sourceTile = gameStateBoard.getTile(tileId);

      if (sourceTile.isTileOccupied()) {
        currentPieceMoves = sourceTile.getPiece().evaluateMoves(gameStateBoard);

        for (final Map.Entry<String, Move> entry : currentPieceMoves.entrySet()) {
          final int destinationCoords = entry.getValue().getDestinationCoords();

          if (entry.getValue().getMoveType() == "attacking" ||
              entry.getValue().getMoveType() == "draw") {
            boardTiles.get(destinationCoords).setBackground(ENEMY_TILE_COLOR);

          } else if (entry.getValue().getMoveType() == "normal") {
            boardTiles.get(destinationCoords).setBackground(VALID_TILE_COLOR);

          } else if (entry.getValue().getMoveType() == "invalid") {
            boardTiles.get(destinationCoords).setBackground(INVALID_TILE_COLOR);
          }
          // Sets highlighted tile as candidate move tile.
          candidateMoveTiles.add(destinationCoords);
          boardTiles.get(destinationCoords).setIsCandidateMoveTile(true);
        }
      }
    }

    /**
     * Gets active tile ID.
     * @return int activeTileId field.
     */
    private int getActiveTileId() {
      return this.activeTileId;
    }

    /**
     * Gets the active tile TilePanel instance.
     * @return TilePanel of the active tile.
     */
    private TilePanel getActiveTilePanel() {
      return boardTiles.get(this.activeTileId);
    }

    /**
     * Sets the active tile. Also toggles on and off corresponding TilePanel as
     * active or not active.
     * @param newActiveTile new active tile id
     */
    private void setActiveTile(final int newActiveTile) {
      if (newActiveTile == -1) {
        if (this.activeTileId != -1) {
          boardTiles.get(this.activeTileId).setIsTileActive(false);
          this.activeTileId = -1;
        }
      } else {
        if (this.activeTileId != -1) {
          boardTiles.get(this.activeTileId).setIsTileActive(false);
        }
        this.activeTileId = newActiveTile;
        boardTiles.get(newActiveTile).setIsTileActive(true);
      }
    }

    /**
     * Deactivates currently active TilePanel and sets activeTileId to -1.
     */
    private void deactivateActiveTile() {
      if (this.activeTileId != -1) {
        clearHighlights();
        boardTiles.get(activeTileId).setIsTileActive(false);
        this.activeTileId = -1;
      }
    }

    /**
     * Deactivate specific tile.
     * @param tileId tile id of the tile to be deactivated.
     */
    private void deactivateTile(final int tileId) {
      if (boardTiles.get(tileId).isTileActive()) {
        boardTiles.get(tileId).deactivateTile();
      }
    }

    /**
     * Enables or disables TilePanel hover highlight.
     */
    private void setHoverHighlight(final boolean enabled) {
      this.enableHoverHighlight = enabled;
    }

    /**
     * Clears all highlights of candidate move tiles and active tile.
     */
    private void clearHighlights() {
      // remove candidate move tile highlights
      for (int i = 0; i < candidateMoveTiles.size(); i++) {
        boardTiles.get(candidateMoveTiles.get(i)).assignTileColor();
        boardTiles.get(candidateMoveTiles.get(i)).setIsCandidateMoveTile(false);
      }
      // remove active tile highlight
      if (this.activeTileId != -1)
        boardTiles.get(activeTileId).assignTileColor();
    }

    /**
     * Refresh tile pieces icons.
     */
    private void refreshInnerBoardPanelIcons() {
      for (int i = 0; i < boardTiles.size(); i++) {
        boardTiles.get(i).loadPieceIcons();
        boardTiles.get(i).assignTilePieceIcon();
        boardTiles.get(i).validate();
      }
    }

    /**
     * Refresh all instance of TilePanel background color.
     */
    private void refreshTilesBackgroundColor() {
      for (int i = 0; i < boardTiles.size(); i++) {
        boardTiles.get(i).assignTileColor();
        boardTiles.get(i).validate();
      }
    }

    /**
     * Pre-loads piece image icons and store into HashMaps separately by piece
     * Alliance. Also hard-coded to scale to fit into TilePanel dimensions.
     */
    private void preLoadImages() {
      // Pre-load piece image
      final Map<String, Image> axisPieceIcons = new HashMap<>();
      final Map<String, Image> allyPieceIcons = new HashMap<>();

      final String axisPiecesPath = "src/images/pieces/original/axis/";
      final String allyPiecesPath = "src/images/pieces/original/ally/";
      final String[] axisPathNames;
      final String[] allyPathNames;
      BufferedImage bufferedImage;
      Image image;

      final File axisImageFiles = new File(axisPiecesPath);
      final File allyImageFiles = new File(allyPiecesPath);
      axisPathNames = axisImageFiles.list();
      allyPathNames = allyImageFiles.list();

      // load axis images
      assert axisPathNames != null;
      for (final String pathFile : axisPathNames) {
        try {
          final String strippedAlliancePathName = pathFile.replaceAll("^BLACK_", "");
          final String pathNameRank = strippedAlliancePathName.replaceAll(".png$", "");

          bufferedImage = ImageIO.read(BoardPanel.class.getResourceAsStream( "/images/pieces/original/axis/" + pathFile));
          image = bufferedImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);

          axisPieceIcons.put(pathNameRank, image);
        } catch (final IOException e) {
          e.printStackTrace();
        }
      }

      // load ally images
      assert allyPathNames != null;
      for (final String pathFile : allyPathNames) {
        try {
          final String strippedAlliancePathName = pathFile.replaceAll("^WHITE_", "");
          final String pathNameRank = strippedAlliancePathName.replaceAll(".png$", "");

          bufferedImage = ImageIO.read(BoardPanel.class.getResourceAsStream( "/images/pieces/original/ally/" + pathFile));
          image = bufferedImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);

          allyPieceIcons.put(pathNameRank, image);
        } catch (final IOException e) {
          e.printStackTrace();
        }
      }

      this.axisPieceIcons = axisPieceIcons;
      this.allyPieceIcons = allyPieceIcons;
    }

    /**
     * Gets all pre-loaded axis piece icons.
     * @return Map<String, Image> axisPieceIcons field.
     */
    private Map<String, Image> getAxisPieceIcons() {
      return axisPieceIcons;
    }

    /**
     * Gets all pre-loaded ally piece icons.
     * @return Map<String, Image> allyPieceIcons field.
     */
    private Map<String, Image> getAllyPieceIcons() {
      return allyPieceIcons;
    }

    /**
     * Gets currently hovered tile ID.
     * @return int hoveredTileId field.
     */
    private int getHoveredTileId() {
      return this.hoveredTileId;
    }

    /**
     * Sets hovered tile ID.
     * @param tileId tile Id being hovered.
     */
    private void setHoveredTileId(final int tileId) {
      this.hoveredTileId = tileId;
    }
  } // InnerBoardPanel

  /**
   * BoardPanel inner JPanel class that displays tiles and piece icons
   */
  private static class TilePanel extends JPanel {

    /** Tile panel ID */
    private final int tileId;

    /** Checks if this TilePanel is active or has been clicked */
    private boolean isTileActive = false;

    /** Checks if this TilePanel is a candidate move tile */
    private boolean isCandidateMoveTile = false;

    /** Current contained piece hidden icon */
    private Image iconHidden;

    /** Current contained piece normal icon */
    private Image iconNormal;

    /**
     * Constructor method that takes in tile ID.
     */
    public TilePanel(final int tileId) {
      this.setLayout(new GridBagLayout());
      this.tileId = tileId;
      setPreferredSize(TILE_PANEL_DIMENSION);
      setBorder(BorderFactory.createLineBorder(Color.GRAY, 3));
    }

    /**
     * Load and assign all piece icons, tile colors and event listeners
     */
    public void initTilePanel() {
      loadPieceIcons();
      assignTilePieceIcon();
      assignTileColor();
      addTilePanelListener();
      validate();
    }

    /**
     * Adds this TilePanel mouse listeners for board interaction.
     */
    // TODO: improve READABILITY
    private void addTilePanelListener() {
      this.addMouseListener(new MouseListener() {

        @Override
        public void mouseClicked(final MouseEvent e) {
        }

        /**
         * Highlights hovered TilePanel and all candidate move tiles if
         * TilePanel contains piece. Disables highlight when game concludes or
         * has a winner.
         */
        @Override
        public void mouseEntered(final MouseEvent e) {
          if (gameStateBoard.getEndGameWinner() == null &&
              gameStateBoard.isGameStarted()) {
            if (isOccupyingPieceOwnedByMoveMaker() || isCandidateMoveTile) {
              if (boardPanel.enableHoverHighlight)
                boardPanel.highlightPieceMoves(tileId);

              boardPanel.setHoveredTileId(tileId);
            }
          } else if (gameStateBoard.isGameInitialized() &&
              !gameStateBoard.isGameStarted()) {
            if ((gameStateBoard.getMoveMaker() == Alliance.AXIS &&
                 tileId < BoardUtils.ALL_TILES_COUNT / 2) ||
                (gameStateBoard.getMoveMaker() == Alliance.ALLY &&
                 tileId >= BoardUtils.ALL_TILES_COUNT / 2 )) {

              boardPanel.setHoveredTileId(tileId);
            }
          }
        }

        /**
         * Removes all highlights as mouse leaves TilePanel. Disables highlight
         * when game concludes or has a winner.
         */
        @Override
        public void mouseExited(final MouseEvent e) {
          if (gameStateBoard.getEndGameWinner() == null &&
              gameStateBoard.isGameStarted()) {
            if (isOccupyingPieceOwnedByMoveMaker()) {
              if (boardPanel.enableHoverHighlight)
                boardPanel.clearHighlights();
            }
          }
        }

        /**
         * Highlights pressed TilePanel and set as active. Active tile that have
         * been toggled on will remain to be highlighted as well as the
         * corresponding candidate move tiles until the active TilePanel have
         * pressed one more time or pressed another TilePanel to toggle off.
         *
         * Also, when TilePanel is active, hover highlight is disabled and the
         * highlighted candidate move tiles will listens for a mouse press to
         * send signal to board engine to move the piece contained in the active
         * TilePanel to the direction of the clicked candidate move tile.
         *
         * Furthermore, if game arrange mode will only activate tile and will
         * not highlight piece candidate move tiles.
         *
         * Listener disables once the game concludes or has game winner.
         */
        @Override
        public void mousePressed(final MouseEvent e) {
          // Ensures TilePanel can only be pressed if game is ongoing
          if (gameStateBoard.getEndGameWinner() == null &&
              gameStateBoard.isGameStarted()) {
            // Ensures TilePanel can only be clicked by respective player.
            if (isOccupyingPieceOwnedByMoveMaker()) {
              // Ensures TilePanel will only activate if theres an occupying piece
              if (gameStateBoard.getTile(tileId).isTileOccupied() && !isCandidateMoveTile) {
                // Ensures TilePanel can only be clicked by the move maker
                if (gameStateBoard.getTile(tileId).getPiece().getPieceAlliance() ==
                    gameStateBoard.getMoveMaker()) {

                  boardPanel.clearHighlights();

                  // Toggle off TilePanel if active, else activate and lock the highlights
                  if (isTileActive) {
                    boardPanel.deactivateActiveTile();
                    boardPanel.setHoverHighlight(true);
                  } else {
                    boardPanel.setActiveTile(tileId);
                    boardPanel.setHoverHighlight(false);
                    boardPanel.highlightPieceMoves(tileId);
                    setBackground(ACTIVE_TILE_COLOR);
                  }
                }
              }
            }
            // If game is in arrange mode or not yet started.
          } else if (gameStateBoard.isGameInitialized() &&
                    !gameStateBoard.isGameStarted()) {
              if ((gameStateBoard.getMoveMaker() == Alliance.AXIS &&
                   tileId < BoardUtils.ALL_TILES_COUNT / 2) ||
                  (gameStateBoard.getMoveMaker() == Alliance.ALLY &&
                   tileId >= BoardUtils.ALL_TILES_COUNT / 2 )) {

              // Arrange mode active tile highlights.
              if (isTileActive) {
                boardPanel.deactivateActiveTile();
              } else if (boardPanel.getActiveTileId() != -1 &&
                        gameStateBoard.getTile(boardPanel.getActiveTileId()).isTileOccupied()) {
                boardPanel.deactivateTile(tileId);
              } else if (gameStateBoard.getTile(tileId).isTileOccupied()) {
                boardPanel.setActiveTile(tileId);
                setBackground(ACTIVE_TILE_COLOR);
              }
            }
          }
        }

        /**
         * Listener that gives signal to game engine to execute if theres an
         * active tile and the highlighted candidate piece move tiles has been
         * released from being clicked.
         */
        @Override
        public void mouseReleased(final MouseEvent e) {
          // Ensures TilePanel can only be pressed if game is ongoing.
          if (gameStateBoard.getEndGameWinner() == null &&
              gameStateBoard.isGameStarted()) {
            // Ensures TilePanel can only be clicked by respective player or is
            // a candidate piece move tile.
            if (isOccupyingPieceOwnedByMoveMaker() || isCandidateMoveTile) {
              // Ensures when mouse is released the hovered TilePanel is still
              // within the TilePanel that has been pressed, and is a candidate
              // move tile.
              if (isCandidateMoveTile && boardPanel.getHoveredTileId() == tileId) {
                // Get active Tile piece
                final Piece activePiece = gameStateBoard.getTile(boardPanel.getActiveTileId()).getPiece();

                // Get player and execute move.
                final Player player = gameStateBoard.getPlayer(activePiece.getPieceAlliance());
                player.makeMove(activePiece.getPieceCoords(), tileId);

                // The executed move will now be the last move after being
                // executed. Append move to mov history panel.
                if (gameStateBoard.getLastMove() != null) {
                  final Move lastMove = gameStateBoard.getLastMove();
                  moveHistoryPanel.appendToMoveHistory(lastMove);
                }

                // If game has concluded or has a winner, announce to move
                // history text area.
                if (gameStateBoard.isEndGame()) {
                  final String endGameMessage = "GAME OVER, " +
                    gameStateBoard.getEndGameWinner() + " PLAYER WON!";
                  final String separator = "\n**********************************\n";
                  moveHistoryPanel.appendTextToMoveHistory("\n" + separator +
                                                  endGameMessage + separator);
                  boardPanel.refreshTilesBackgroundColor();
                }

                // Refresh BoardPanel
                boardPanel.refreshInnerBoardPanelIcons();
                boardPanel.deactivateActiveTile();
                boardPanel.setHoverHighlight(true);
              }
            }
            // If game is in arrange mode, move piece freely without restriction.
          } else if (gameStateBoard.isGameInitialized() &&
                      !gameStateBoard.isGameStarted()) {
            // Ensures TilePanel can only be activated by the move maker and
            // within Players respective territory.
            if ((gameStateBoard.getMoveMaker() == Alliance.AXIS &&
                 tileId < BoardUtils.ALL_TILES_COUNT / 2) ||
                (gameStateBoard.getMoveMaker() == Alliance.ALLY &&
                 tileId >= BoardUtils.ALL_TILES_COUNT / 2 )) {
              final int hoveredTileId = boardPanel.getHoveredTileId();
              final int activeTileId = boardPanel.getActiveTileId();

              if (Board.isDebugMode()) {
                System.out.println("hoveredTileId=" + hoveredTileId +
                                   ";activeTileId=" + activeTileId +
                                   ";currenttileId=" + tileId);
              }

              // If theres an active tile and another TilePanel have been clicked
              if (hoveredTileId == tileId && hoveredTileId != activeTileId &&
                  activeTileId != -1) {
                // If TilePanel is empty moved the active tile piece over it
                if (gameStateBoard.getTile(tileId).isTileEmpty()) {
                  gameStateBoard.movePiece(activeTileId, tileId);

                  if (Board.isDebugMode())
                    System.out.println(gameStateBoard.getTile(tileId).getPiece().getRank() +
                        " at " + activeTileId + " moved to " + tileId + "\n");
                  // Else, TilePanel is occupied and swapped with active tile piece.
                } else {
                  gameStateBoard.swapPiece(activeTileId, tileId);

                  if (Board.isDebugMode())
                    System.out.println(gameStateBoard.getTile(tileId).getPiece().getRank() +
                        " at " + activeTileId + " swapped with " +
                        gameStateBoard.getTile(activeTileId).getPiece().getRank() +
                        " at " + tileId + "\n");
                }

                // Refresh BoardPanel and disables active if piece have been moved.
                boardPanel.refreshInnerBoardPanelIcons();
                boardPanel.deactivateActiveTile();
              }
            }
          }
        }
      });
    }

    /**
     * Loads the occupying piece icons into TilePanel.
     */
    private void loadPieceIcons() {
      // Pre-load piece image
      if (gameStateBoard.getTile(tileId).isTileOccupied()) {
        final Tile currTile = gameStateBoard.getTile(tileId);
        final Alliance pieceAlliance = currTile.getPiece().getPieceAlliance();
        final String pieceRank = currTile.getPiece().getRank();

        if (pieceAlliance == Alliance.AXIS) {
          this.iconNormal = boardPanel.getAxisPieceIcons().get(pieceRank);
          this.iconHidden = boardPanel.getAxisPieceIcons().get("Hidden");
        } else {
          this.iconNormal = boardPanel.getAllyPieceIcons().get(pieceRank);
          this.iconHidden = boardPanel.getAllyPieceIcons().get("Hidden");
        }

        if (Board.isDebugMode() && gameStateBoard.isGameInitialized())
          System.out.println("Tile " + tileId + " icons loaded");
      } else {
        if (Board.isDebugMode() && gameStateBoard.isGameInitialized())
          System.out.println("Tile " + tileId + " icons empty");
      }
    }

    /**
     * Checks if occupying piece is owned by the current move maker.
     */
    private boolean isOccupyingPieceOwnedByMoveMaker() {
      if (gameStateBoard.getTile(tileId).isTileOccupied()) {
        return gameStateBoard.getTile(tileId).getPiece().getPieceAlliance() ==
                gameStateBoard.getMoveMaker();
      }
      return false;
    }

    /**
     * Assign tile piece icon to display in this TilePanel if occupied.
     */
    private void assignTilePieceIcon() {
      this.removeAll();

      if (gameStateBoard.getTile(tileId).isTileOccupied()) {
        final Tile currTile = gameStateBoard.getTile(tileId);

        if (gameStateBoard.getEndGameWinner() == null) {
          // Load normal icon if isMoveMaker, else hidden icon
          if (currTile.getPiece().getPieceAlliance() == gameStateBoard.getMoveMaker())
            add(new JLabel(new ImageIcon(iconNormal)));
          else
            add(new JLabel(new ImageIcon(iconHidden)));

        } else {
          add(new JLabel(new ImageIcon(iconNormal)));
        }
        if (Board.isDebugMode() && gameStateBoard.isGameInitialized())
          System.out.println("Tile " + tileId + " piece icon assigned");
      } else {
        if (Board.isDebugMode() && gameStateBoard.isGameInitialized())
          System.out.println("Tile " + tileId + " piece icon NOT assigned");
      }
    }

    /**
     * Sets the state of this TilePanel.
     * @param isTileActive state of tile.
     */
    private void setIsTileActive(final boolean isTileActive) {
      this.isTileActive = isTileActive;
    }

    /**
     * Sets the if this TilePanel is a candidate move tile.
     * @param isCandidateMoveTile is tile is a candidate piece move tile.
     */
    private void setIsCandidateMoveTile(final boolean isCandidateMoveTile) {
      this.isCandidateMoveTile = isCandidateMoveTile;
    }

    /**
     * Checks if this TilePanel is active.
     * @return boolean isTileActive field.
     */
    private boolean isTileActive() {
      return this.isTileActive;
    }

    /**
     * Deactivate this TilePanel.
     */
    private void deactivateTile() {
      this.isTileActive = false;
    }

    /**
     * Assign TilePanel color.
     */
    private void assignTileColor() {
      // If game is ongoing, displays separate colors for each territory, else
      // the game has concluded and paints all TilePanel the color of the winner
      // player.
      if (gameStateBoard.getEndGameWinner() == null) {
        // Axis territory color
        if (gameStateBoard.getTile(tileId).getTerritory() == Alliance.AXIS)
          setBackground(DARK_TILE_COLOR);
        // Ally territory color
        else
          setBackground(LIGHT_TILE_COLOR);

        if (Board.isDebugMode() && gameStateBoard.isGameInitialized())
          System.out.println("Tile " + tileId + " color assigned");

      } else {
        setBackground(gameStateBoard.getEndGameWinner() == Alliance.AXIS ?
            DARK_TILE_COLOR : LIGHT_TILE_COLOR);

        if (Board.isDebugMode())
          System.out.println("Tile " + tileId + " color assigned");
      }
    }

  } // TilePanel

} // BoardPanel
