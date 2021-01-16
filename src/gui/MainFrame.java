package gui;

import engine.Alliance;
import engine.Board;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * The main class that serves as the JFrame that puts the BoardPanel and
 * MainMenuPanel together using JLayeredPane.
 *
 * Author: Brethren Ace de la Gente
 * Date: 2021-01-04
 */
@SuppressWarnings("unused")
public class MainFrame {

  /** Main JFrame dimension */
  private final static Dimension FRAME_DIMENSION = new Dimension(1200, 735);

  /** Reference to Board engine */
  private final Board gameStateBoard;

  /** Reference to BoardPanel */
  private BoardPanel boardPanel;

  // Fetch main menu panel and all its components into this frame then add
// listeners.
  /** Reference to MainMenuPanel */
  private final MainMenuPanel mainMenuPanel = new MainMenuPanel();

  /** Reference to main the JFrame JLayeredPanel */
  private final JLayeredPane layeredPane;

  /** JPanel that serves as the main JFrame content pane */
  private final JPanel contentPane;

  /** The main JFrame */
  private final JFrame frame;

  /** MainMenuPanel buttons */
  private JButton doneArrangingBtn, startGameBtn;
  private JButton mainMenuStartBtn, mainMenuHowToPlayBtn, mainMenuQuitBtn;

  /** BoardPanel menu bar buttons and labels */
  private JButton menuBarRestartBtn;
  private JButton menuBarQuitBtn;
  private JButton menuBarUndoBtn;
  private JButton menuBarRedoBtn;
  private JButton menuBarGameRulesBtn;
  private JLabel menuBarPlayerAxisLbl, menuBarPlayerAllyLbl;

  /** Stores Player names */
  private String playerAxisName, playerAllyName;

  /** JDialog as alternative popup menus */
  private JDialog playerAssignDialog;
  private JDialog mainMenuLoadDialog;

  /** Pop menu prompt for quit buttons */
  private JPopupMenu mainMenuQuitPopup, menuBarQuitPopup;

  /**
   * MainFrame constructor that takes in engine Board class.
   * @param board Reference to the Board instance.
   */
  public MainFrame(Board board) {
    this.gameStateBoard = board;
    frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(1, 1, 1, 1));

    layeredPane = new JLayeredPane();
    layeredPane.setPreferredSize(FRAME_DIMENSION);

    fetchMainMenuButtons(mainMenuPanel);
    addMainMenuButtonsListeners();
    createMainMenuQuitPopupMenu();
    // Add main menu at the top of the JLayeredPane
    layeredPane.add(mainMenuPanel, Integer.valueOf(2));
    mainMenuPanel.setBounds(0, 0, FRAME_DIMENSION.width, FRAME_DIMENSION.height);

    // Add everything into the frame.
    contentPane.add(layeredPane);
    frame.add(contentPane);
    frame.pack();
    frame.setVisible(true);
  }

  /**
   * Adds each main menu buttons a listeners.
   */
  private void addMainMenuButtonsListeners() {

    // Main menu start button action listener to hide main menu and load board panel.
    mainMenuStartBtn.addActionListener(e -> {
      // Initialize game and load BoardPanel.
      gameStateBoard.initGame();
      boardPanel = gameStateBoard.getBoardPanel();
      boardPanel.initBoardPanel();

      // Fetch and add listeners to all BoardPanel buttons.
      fetchMenuBarComponents(boardPanel);
      fetchDoneArrangingBtn(boardPanel);
      fetchStartGameBtn(boardPanel);

      addMenuBarButtonsListeners();
      addDoneArrangingButtonListener();
      addStartGameButtonListener();

      createPlayerNameAssignDialog();
      createMenuBarQuitPopupMenu();

      // Add BoardPanel into JLayeredPane below the MainMenuPanel.
      layeredPane.add(boardPanel, Integer.valueOf(1));
      boardPanel.setBounds(0, 0, FRAME_DIMENSION.width, FRAME_DIMENSION.height);

      // Hide MainMenuPanel and set BoardPanel visible.
      mainMenuPanel.setVisible(false);
      boardPanel.setVisible(true);
      playerAssignDialog.setVisible(true);
    });

    // Main menu quit button action listener to show quit dialog.
    mainMenuQuitBtn.addActionListener(e -> {
      mainMenuQuitPopup.setVisible(true);
      int quitPopupWidth = mainMenuQuitPopup.getWidth() / 2;
      int quitPopupHeight = mainMenuQuitPopup.getHeight() / 2;
      mainMenuQuitPopup.show(frame, (FRAME_DIMENSION.width / 2) - quitPopupWidth,
                             (FRAME_DIMENSION.height / 2) - quitPopupHeight);
    });

    // Displays game instruction in a new JFrame on click.
    mainMenuHowToPlayBtn.addActionListener(e -> displayGameInstructions());
  }


  /**
   * Displays the game instruction in a new JFrame.
   */
  private void displayGameInstructions() {
    try {
      BufferedImage image = ImageIO.read(MainFrame.class.getResource("/images/gog_instructions_12002x.jpg"));
      ImageIcon icon = new ImageIcon(image);

      JFrame frame = new JFrame();
      frame.setLayout(new FlowLayout());
      frame.setSize(1200, 1100);

      JLabel iconContainerLbl = new JLabel();
      iconContainerLbl.setIcon(icon);

      frame.add(iconContainerLbl);
      frame.setVisible(true);
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Adds all BoardPanel menu bar buttons event listeners.
   */
  private void addMenuBarButtonsListeners() {

    // Restart button action listener.
    menuBarRestartBtn.addActionListener(arg0 -> {
      // Restart board engine and clear board panel.
      gameStateBoard.restartGame();
      frame.repaint();
    });

    // Menu bar quit button action listener.
    menuBarQuitBtn.addActionListener(e -> {
      menuBarQuitPopup.setVisible(true);
      int quitPopupWidth = menuBarQuitPopup.getWidth() / 2;
      int quitPopupHeight = menuBarQuitPopup.getHeight() / 2;
      menuBarQuitPopup.show(frame, (FRAME_DIMENSION.width / 2) - quitPopupWidth,
          (FRAME_DIMENSION.height / 2) - quitPopupHeight);
    });

    // Undo button action listener that undo most recent move execution.
    menuBarUndoBtn.addActionListener(e -> {
      if (gameStateBoard.getAxisPlayer().getMoveFromHistory(gameStateBoard.getCurrentTurn() - 1) != null) {
        boardPanel.undoMoveHistoryUpdate();
        if (gameStateBoard.getMoveMaker() == Alliance.AXIS)
          gameStateBoard.getAxisPlayer().undoLastMove();
        else
          gameStateBoard.getAllyPlayer().undoLastMove();

        boardPanel.repaintBoardPanel();
        frame.repaint();
      }
    });

    // Redo button action listener that redo move execution
    menuBarRedoBtn.addActionListener(e -> {
      if (gameStateBoard.getAxisPlayer().getMoveFromHistory(gameStateBoard.getCurrentTurn()) != null) {
        if (gameStateBoard.getMoveMaker() == Alliance.AXIS)
          gameStateBoard.getAxisPlayer().redoLastMove();
        else
          gameStateBoard.getAllyPlayer().redoLastMove();

        boardPanel.redoMoveHistoryUpdate();
        boardPanel.repaintBoardPanel();
        frame.repaint();
      }
    });

    menuBarGameRulesBtn.addActionListener(e -> displayGameInstructions());
  }

  /**
   * Adds BoardPanel done arranging button an action listener. This switches the
   * current move maker to the opposing Player. Will remain in arrange mode
   * until the game starts..
   */
  private void addDoneArrangingButtonListener() {
    doneArrangingBtn.addActionListener(e -> {
      gameStateBoard.playerDoneArranging();
      boardPanel.clearBoardPanel();
      boardPanel.printOpeningMessage();
    });
  }

  /**
   * Adds BoardPanel start game button an cation listener. When triggered, game
   * would no longer be in arrange mode and starts making turns.
   */
  private void addStartGameButtonListener() {
    startGameBtn.addActionListener(e -> {
      // Start game
      gameStateBoard.startGame();
      boardPanel.startMode();

      boardPanel.clearBoardPanel();
      frame.repaint();
    });
  }

  /**
   * Fetch all MainMenuPanel buttons.
   * @param mainMenuPanel the MainMenuPanel instance.
   */
  private void fetchMainMenuButtons(MainMenuPanel mainMenuPanel) {
    mainMenuStartBtn = mainMenuPanel.getStartBtn();
    mainMenuHowToPlayBtn = mainMenuPanel.getHowToPlayBtn();
    mainMenuQuitBtn = mainMenuPanel.getQuitBtn();
  }

  /**
   * Fetch all BoardPanel inner MenuBarPanel buttons.
   * @param boardPanel the BoardPanel instance.
   */
  private void fetchMenuBarComponents(BoardPanel boardPanel) {
    menuBarPlayerAxisLbl = boardPanel.getPlayerAxisNameLbl();
    menuBarPlayerAllyLbl = boardPanel.getPlayerAllyNameLbl();
    menuBarRestartBtn = boardPanel.getRestartBtn();
    menuBarQuitBtn = boardPanel.getQuitBtn();
    menuBarUndoBtn = boardPanel.getUndoBtn();
    menuBarRedoBtn = boardPanel.getRedoBtn();
    menuBarGameRulesBtn = boardPanel.getGameRulesBtn();
  }

  /**
   * Fetch BoardPanel inner MoveHistoryPanel done arranging button.
   * @param boardPanel the BoardPanel instance.
   */
  private void fetchDoneArrangingBtn(BoardPanel boardPanel) {
    this.doneArrangingBtn = boardPanel.getDoneArrangingBtn();
  }

  /**
   * Fetch BoardPanel inner MoveHistoryPanel start game button.
   * @param boardPanel the BoardPanel instance.
   */
  private void fetchStartGameBtn(@NotNull BoardPanel boardPanel) {
    this.startGameBtn = boardPanel.getStartGameBtn();
  }

  /**
   * Creates MainMenuPanel quit button popup menu for confirmation.
   */
  private void createMainMenuQuitPopupMenu() {
    mainMenuQuitPopup = new JPopupMenu();
    mainMenuQuitPopup.setLayout(new BorderLayout());
    mainMenuQuitPopup.setBorder(new EmptyBorder(10, 10, 10, 10));

    JLabel quitMessageLbl = new JLabel("Are you sure you want to quit?");
    quitMessageLbl.setFont(new Font("TimesRoman", Font.PLAIN, 10));
    JPanel quitPopupOptionsPanel = new JPanel();
    JButton quitConfirmBtn = new JButton("Yes");
    JButton quitAbortBtn = new JButton("No");

    quitPopupOptionsPanel.add(quitConfirmBtn);
    quitPopupOptionsPanel.add(quitAbortBtn);

    mainMenuQuitPopup.add(quitMessageLbl, BorderLayout.NORTH);
    mainMenuQuitPopup.add(quitPopupOptionsPanel, BorderLayout.CENTER);

    // Adds confirm button action listener to exit program.
    quitConfirmBtn.addActionListener(e -> System.exit(0));

    // Adds confirm button to hide popup menu.
    quitAbortBtn.addActionListener(e -> mainMenuQuitPopup.setVisible(false));
  }

  /**
   * Creates player name assignment dialog that takes user input for
   * MainMenuPanel start game button.
   */
  public void createPlayerNameAssignDialog() {
    JLabel playerAssignLbl = new JLabel("Please assign player names");
    playerAssignLbl.setFont(new Font("TimesRoman", Font.PLAIN, 20));

    JPanel playerAxisPanel = new JPanel();
    JPanel playerAllyPanel = new JPanel();
    menuBarPlayerAxisLbl = new JLabel("Axis Player");
    menuBarPlayerAllyLbl = new JLabel("Ally Player");

    JTextField playerAxisTextField = new JTextField(8);
    playerAxisPanel.add(menuBarPlayerAxisLbl);
    playerAxisPanel.add(playerAxisTextField);

    JTextField playerAllyTextField = new JTextField(8);
    playerAllyPanel.add(menuBarPlayerAllyLbl);
    playerAllyPanel.add(playerAllyTextField);

    JPanel playerAssignActionsPanel = new JPanel();
    JButton playerAssignConfirmBtn = new JButton("Confirm");
    JButton playerAssignAbortBtn = new JButton("Abort");
    playerAssignActionsPanel.add(playerAssignConfirmBtn);
    playerAssignActionsPanel.add(playerAssignAbortBtn);

    // options pane
    // Ref: https://stackoverflow.com/a/40200144/11850077
    Object[] options = new Object[] {};
    JOptionPane playerAssignOptionsPane = new JOptionPane(playerAssignLbl,
                                      JOptionPane.PLAIN_MESSAGE,
                                      JOptionPane.DEFAULT_OPTION,
                                      null, options, null);

    playerAssignOptionsPane.add(playerAxisPanel);
    playerAssignOptionsPane.add(playerAllyPanel);
    playerAssignOptionsPane.add(playerAssignActionsPanel);

    playerAssignDialog = new JDialog();
    playerAssignDialog.getContentPane().add(playerAssignOptionsPane);
    playerAssignDialog.pack();
    playerAssignDialog.setVisible(false);

    // Adds confirm button action listener to pass along the input players name
    // to the board engine and board panel to update the board.
    playerAssignConfirmBtn.addActionListener(e -> {
      playerAxisName = playerAxisTextField.getText();
      playerAllyName = playerAllyTextField.getText();

      if (Board.isDebugMode()) {
        System.out.println("Axis player: " + playerAxisName + " registered");
        System.out.println("Ally player: " + playerAllyName + " registered");
      }

      // Sets player names in both Board and BoardPanel.
      gameStateBoard.setAxisPlayerName(playerAxisName);
      gameStateBoard.setAllyPlayerName(playerAllyName);
      boardPanel.getPlayerAxisNameLbl().setText(
          "AXIS PLAYER: " + playerAxisName);
      boardPanel.getPlayerAllyNameLbl().setText(
          "ALLY PLAYER: " + playerAllyName);

      // Hide player assign dialog and update opening message to greet the
      // move maker player with the input name.
      playerAssignDialog.setVisible(false);
      boardPanel.clearBoardPanel();
      boardPanel.printOpeningMessage();
    });

    // Adds abort button action listener to exit back to main menu.
    playerAssignAbortBtn.addActionListener(e -> {
      boardPanel.setVisible(false);
      mainMenuPanel.setVisible(true);
      playerAssignDialog.setVisible(false);
    });
  }

  /**
   * Creates BoardPanel quit button pop up menu confirmation.
   */
  private void createMenuBarQuitPopupMenu() {
    menuBarQuitPopup = new JPopupMenu();
    menuBarQuitPopup.setLayout(new BorderLayout());
    menuBarQuitPopup.setBorder(new EmptyBorder(10, 10, 10, 10));

    JLabel quitMessageLbl = new JLabel("Back to main menu?");
    quitMessageLbl.setFont(new Font("TimesRoman", Font.PLAIN, 15));
    JPanel menuBarQuitPopupOptionsPanel = new JPanel();
    JButton quitBackToMain = new JButton("Main Menu");
    JButton quitConfirmBtn = new JButton("Surrender");

    menuBarQuitPopupOptionsPanel.add(quitBackToMain);
    menuBarQuitPopupOptionsPanel.add(quitConfirmBtn);

    menuBarQuitPopup.add(quitMessageLbl, BorderLayout.NORTH);
    menuBarQuitPopup.add(menuBarQuitPopupOptionsPanel, BorderLayout.CENTER);

    // Adds confirm button action listener to exit the program.
    quitConfirmBtn.addActionListener(e -> System.exit(0));

    // Adds back to main action listener to exit back to main menu.
    quitBackToMain.addActionListener(e -> {
      menuBarQuitPopup.setVisible(false);
      boardPanel.setVisible(false);
      mainMenuPanel.setVisible(true);
    });
  }
}
