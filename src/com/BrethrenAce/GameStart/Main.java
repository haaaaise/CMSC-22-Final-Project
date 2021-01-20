package com.BrethrenAce.GameStart;

import com.BrethrenAce.GUI.MainFrame;
import com.BrethrenAce.GameEngine.Alliance;
import com.BrethrenAce.GameEngine.Board;
import com.BrethrenAce.GameEngine.Board.BoardBuilder;
import com.BrethrenAce.GameEngine.player.Player;

import javax.swing.*;

import static com.TestClasses.EngineTests.BoardTest.builder;

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
