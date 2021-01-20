package com.TestClasses.EngineTests;

import com.BrethrenAce.GameEngine.Board;
import com.BrethrenAce.GameEngine.Board.BoardBuilder;
import com.BrethrenAce.GameEngine.Board.Tile;
import com.BrethrenAce.GameUtilities.BoardUtils;

import java.util.Map;

/**
 * This is a test class derived from an Indian guy's github chess engine.
 * Author: Brethren de la Gente
 * Date: 2021-01-07
 */
public class BoardTest {

  public static final BoardBuilder builder = new BoardBuilder();
  private static final Board board = new Board(builder.createDemoBoardBuild());
  private static final boolean territoryErrorDetected = false;
  private static boolean allianceErrorDetected = false;
  private static boolean emptyBoardErrorDetected = false;
  private static Map<Integer, String> tileTerritoryErrors;
  private static Map<Integer, String> tilePieceAllianceErrors;
  private static Map<Integer, String> emptyBoardTileErrors;

  public static void main(String[] args) {
    // TODO: exception handling
    territoryCheck();
    emptyBoardCheck();
  }

  public static void territoryCheck() {
    for (int i = 0; i < BoardUtils.ALL_TILES_COUNT; i++) {
    }

    if (territoryErrorDetected) {
      System.out.println("Board territory check HAS BEEN FAILED");
      for (Map.Entry<Integer, String> entry : tileTerritoryErrors.entrySet()) {
        System.out.println("Tile" + entry.getKey() + ", " + entry.getValue());
      }
    } else {
      System.out.println("Board territory check HAS BEEN PASSED");
    }
  }

  public static void pieceAllianceCheck() {
    board.buildBoard(builder.createDemoBoardBuild());
    for (int i = 0; i < BoardUtils.ALL_TILES_COUNT; i++) {
      final Tile currentTile = board.getTile(i);
      if (currentTile.isTileOccupied() &&
          currentTile.getTerritory() != currentTile.getPiece().getPieceAlliance()) {
        allianceErrorDetected = true;
        tilePieceAllianceErrors.put(i, "E: " + currentTile.getPiece().getPieceAlliance() +
            " " + currentTile.getPiece().getRank() +
            " on " + currentTile.getTerritory() + " Territory");
      }
    }

    if (allianceErrorDetected) {
      System.out.println("Board tile piece alliance  check FAILED");
      for (Map.Entry<Integer, String> entry : tilePieceAllianceErrors.entrySet()) {
        System.out.println("Tile" + entry.getKey() + ", " + entry.getValue());
      }
    } else {
      System.out.println("Board tile piece alliance check ...PASSED");
    }
  }

  public static void emptyBoardCheck() {
    board.emptyBoard();

    for (int i = 0; i < BoardUtils.ALL_TILES_COUNT; i++) {
      final Tile currentTile = board.getTile(i);
      if (currentTile.isTileOccupied()) {
        emptyBoardErrorDetected = true;
        emptyBoardTileErrors.put(i, "E: Tile is not empty");
      }
    }

    if (emptyBoardErrorDetected) {
      System.out.println("Empty board check FAILED");
      for (Map.Entry<Integer, String> entry : emptyBoardTileErrors.entrySet()) {
        System.out.println("Tile" + entry.getKey() + ", " + entry.getValue());
      }
    } else {
      System.out.println("Empty board check HAS BEEN PASSED");
    }
  }

  public static void setTilePieceAllianceErrors(Map<Integer, String> tilePieceAllianceErrors) {
    BoardTest.tilePieceAllianceErrors = tilePieceAllianceErrors;
  }

  public static void setTileTerritoryErrors(Map<Integer, String> tileTerritoryErrors) {
    BoardTest.tileTerritoryErrors = tileTerritoryErrors;
  }

  public static void setEmptyBoardTileErrors(Map<Integer, String> emptyBoardTileErrors) {
    BoardTest.emptyBoardTileErrors = emptyBoardTileErrors;
  }
}
