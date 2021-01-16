package game;

import engine.Alliance;
import engine.Board;
import engine.Board.BoardBuilder;
import engine.player.Player;
import gui.MainFrame;

import javax.swing.*;

import static tests.engine.BoardTest.builder;

/**
 * This is the execution path of the entire project and can be called
 * as the .exe file if it were a program.
 * Main class that serves as the controller that initializes Board, BoardBuilder
 * and Players, and runs the program.
 *
 * Author: Brethren Ace de la Gente
 * Date: 2021-01-14
 */
public class Main {

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      try {
        Board board = new Board(builder.createDemoBoardBuild());
        BoardBuilder builder = new BoardBuilder();

        Player player1 = new Player(board, Alliance.ALLY);
        Player player2 = new Player(board, Alliance.AXIS);
        board.setPlayerAlly(player1);
        board.setPlayerAxis(player2);

        builder = builder.createRandomBuild();
        board.setBoardBuilder(builder);

        board.setDebugMode(true);
        new MainFrame(board);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

}
