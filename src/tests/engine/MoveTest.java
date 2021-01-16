package tests.engine;

import engine.Alliance;
import engine.Board;
import engine.Board.BoardBuilder;
import engine.player.Player;

import javax.swing.*;

/**
 * Obsolete piece of coding test outsourced from chess engine Youtube tutorials.
 * It's main purpose is to evaluate if Move method is working or not.
 * Author: Brethren de la Gente
 * Date: 2021-01-21
 */
public class MoveTest {
    private static final BoardBuilder builder = new BoardBuilder();
    private static final Board board = new Board(builder.createDemoBoardBuild());
    private static final Player playerAxis = new Player(board, Alliance.AXIS);
    private static final Player playerAlly = new Player(board, Alliance.ALLY);

    public static void main(String[] args) {
        board.displayBoard();
        SwingUtilities.invokeLater(MoveTest::attackingMoveTest);

    }

    public static void attackingMoveTest() {
        System.out.println(board);

        board.setPlayerAlly(playerAlly);
        board.setPlayerAxis(playerAxis);

        board.initGame();

        playerAlly.makeMove(54, 45);
        playerAxis.makeMove(9, 18);
        playerAlly.makeMove(45, 36);
        playerAxis.makeMove(18, 27);
        playerAlly.makeMove(36, 27);

        System.out.println(board);

        System.out.println(playerAlly);
        System.out.println(playerAxis);
    }
}
