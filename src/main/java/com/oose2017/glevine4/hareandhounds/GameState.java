/**
 * Gregory Levine <glevine4@jhu.edu>
 * 601.421 Object Oriented Software Engineering
 * Homework 1
 * September 18th, 2017
 */

package com.oose2017.glevine4.hareandhound;

import java.util.List;
import java.util.ArrayList;

/**
 * Used to represent a single game instance.
 */
public class GameState {
    /** Used to create a unique game id. */
    private static int gameIdCounter = 1;
    /** The unique game id. */
    private String id;
    /** The id of the hound player. */
    private String houndPlayerId = "";
    /** The id of the hare player. */
    private String harePlayerId = "";
    /** The current state of the game. */
    private String state = "WAITING_FOR_SECOND_PLAYER";
    /** Hold the locations of all the pieces. */
    private int[][] board = {{0, 1, 0}, {1, 0, 1}, {0, 0, 0}, {0, 0, 0},
        {0, 2, 0}, };
    /** An array that will indicate which nodes in the board are connected. */
    private boolean[][][][] paths = new boolean[5][3][5][3];
    /** Stores a history of all prior states of the game to check for repeats.
     *  The game states are represented as 9 digit integers. */
    private List<Integer> boardHistory = new ArrayList<Integer>();

    /**
     * Create a new gamestate object to represent a new game.
     * @param pieceType the type of pieces for the creating player.
     */
    public GameState(String pieceType) {
        this.id = "Game" + gameIdCounter++;
        if ("HARE".equals(pieceType)) {
            this.harePlayerId = "Player1";
        } else if ("HOUND".equals(pieceType)) {
            this.houndPlayerId = "Player1";
        }
        this.boardHistory.add(new Integer(11012414));
        this.initPaths();
    }

    /**
     * Get the unique identifier for this game.
     * @return the id.
     */
    public String getGameId() {
        return this.id;
    }

    /**
     * Return the player id of the player with the given pieces.
     * @param pieceType the type of pieces
     * @return the player with the given pieces.
     */
    public String getPlayerId(String pieceType) {
        if ("HARE".equals(pieceType)) {
            return this.harePlayerId;
        } else if ("HOUND".equals(pieceType)) {
            return this.houndPlayerId;
        } else {
            return "";
        }
    }

    /**
     * Add a second player to the game.
     * @return A JSON containing the piece set for the player.
     */
    public String joinGame() {
        if ("".equals(this.houndPlayerId)) {
            this.houndPlayerId = "Player2";
            this.state = "TURN_HOUND";
            return "HOUND";
        } else if ("".equals(this.harePlayerId)) {
            this.harePlayerId = "Player2";
            this.state = "TURN_HOUND";
            return "HARE";
        } else {
            return null;
        }
    }

    /**
     * Moves the given piece, if the move is allowed.
     * @param fromX the x coordinate of the piece to move.
     * @param fromY the y coordinate of the piece to move.
     * @param toX the x coordinate to move to.
     * @param toY the y coordinate to move to.
     * @return true if the piece was moved, false otherwise.
     */
    public boolean movePiece(int fromX, int fromY, int toX, int toY) {
        // Check that both positions are on board.
        if (!this.isOnBoard(fromX, fromY) || !this.isOnBoard(toX, toY)) {
            return false;
        }

        // Check that the two positions are connected.
        if (!this.paths[fromX][fromY][toX][toY]) {
            return false;
        }

        // Check there is a piece there to move.
        if (this.board[fromX][fromY] == 0) {
            return false;
        }

        // Check that the hounds move is valid. Function added to reduce
        // cyclomatic complexity.
        if (!this.checkHoundMove(fromX, fromY, toX)) {
            return false;
        }

        // If the piece is a hare, check that it is the hares turn.
        if (this.board[fromX][fromY] == 2 && !"TURN_HARE".equals(this.state)) {
            return false;
        }

        // Check that the spot it moves is empty.
        if (this.board[toX][toY] != 0) {
            return false;
        }

        // Move the piece.
        this.board[toX][toY] = this.board[fromX][fromY];
        this.board[fromX][fromY] = 0;

        // Check if any win conditions are now satisfied.
        this.updateState();
        return true;
    }

    /**
     * Returns true if checks passed, false if not.
     */
    private boolean checkHoundMove(int fromX, int fromY, int toX) {
        // If the piece is a hound, check that it is not moving backward
        // and that it is the hound's turn
        if (this.board[fromX][fromY] == 1) {
            if (fromX > toX) {
                return false;
            }
            if (!"TURN_HOUND".equals(this.state)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a JSON object with the coordinates of all the pieces.
     * @return the JSON object.
     */
    public String describe() {
        // Response holds the json output as it is constructed.
        String response = "[";
        // Used so that commas are placed at appropriate places.
        boolean leadingComma = false;
        // Loop through all positions checking for pieces.
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 3; y++) {
                // Add hound piece to response.
                if (this.board[x][y] == 1) {
                    if (leadingComma) {
                        response += ", ";
                    }
                    leadingComma = true;
                    response += "{ \"pieceType\": \"HOUND\", \"x\": \"" + x
                        + "\", \"y\": \"" + y + "\" }";
                    // Add hare piece to response.
                } else if (this.board[x][y] == 2) {
                    if (leadingComma) {
                        response += ", ";
                    }
                    leadingComma = true;
                    response += "{ \"pieceType\": \"HARE\", \"x\":\"" + x
                        + "\", \"y\": \"" + y + "\" }";
                }
            }
        }
        return response + "]";
    }

    /**
     * Returns the state of the game as a JSON object.
     * @return the state.
     */
    public String getState() {
        return "{ \"state\": \"" + this.state + "\" }";
    }

    /**
     * Returns the id of the player whose turn it is.
     * @return the playerId.
     */
    public String nextPlayer() {
        if ("TURN_HOUND".equals(this.state)) {
            return this.houndPlayerId;
        } else if ("TURN_HARE".equals(this.state)) {
            return this.harePlayerId;
        } else {
            return "";
        }
    }

    /**
     * Sets up the paths array, which describes which nodes of the board are
     * connected.
     */
    public void initPaths() {
        this.paths[0][1][1][0] = true;
        this.paths[0][1][1][1] = true;
        this.paths[0][1][1][2] = true;
        this.paths[1][0][0][1] = true;
        this.paths[1][0][1][1] = true;
        this.paths[1][0][2][0] = true;
        this.paths[1][0][2][1] = true;
        this.paths[1][0][2][0] = true;
        this.paths[1][1][0][1] = true;
        this.paths[1][1][1][0] = true;
        this.paths[1][1][2][1] = true;
        this.paths[1][1][1][2] = true;
        this.paths[1][2][0][1] = true;
        this.paths[1][2][1][1] = true;
        this.paths[1][2][2][1] = true;
        this.paths[1][2][2][2] = true;
        this.paths[2][0][1][0] = true;
        this.paths[2][0][2][1] = true;
        this.paths[2][0][3][0] = true;
        this.paths[2][1][1][0] = true;
        this.paths[2][1][1][1] = true;
        this.paths[2][1][1][2] = true;
        this.paths[2][1][2][0] = true;
        this.paths[2][1][2][2] = true;
        this.paths[2][1][3][0] = true;
        this.paths[2][1][3][1] = true;
        this.paths[2][1][3][2] = true;
        this.paths[2][2][1][2] = true;
        this.paths[2][2][2][1] = true;
        this.paths[2][2][3][2] = true;
        this.paths[3][0][2][0] = true;
        this.paths[3][0][2][1] = true;
        this.paths[3][0][3][1] = true;
        this.paths[3][0][4][1] = true;
        this.paths[3][1][2][1] = true;
        this.paths[3][1][3][0] = true;
        this.paths[3][1][3][2] = true;
        this.paths[3][1][4][1] = true;
        this.paths[3][2][2][1] = true;
        this.paths[3][2][2][2] = true;
        this.paths[3][2][3][1] = true;
        this.paths[3][2][4][1] = true;
        this.paths[4][1][3][0] = true;
        this.paths[4][1][3][1] = true;
        this.paths[4][1][3][2] = true;
    }

    /**
     * Checks that a given coordinate position is on the board.
     * @param x the x coord.
     * @param y the y coord.
     * @return true if position on the board, false if not.
     */
    private boolean isOnBoard(int x, int y) {
        if (x < 0 || x > 4 || y < 0 || y > 2) {
            return false;
        }
        if (((x == 0) || (x == 4)) && ((y == 0) || (y == 2))) {
            return false;
        }
        return true;
    }

    /**
     * Checks win conditions, if no win then switch the turns.
     */
    private void updateState() {
        this.checkWinHareByEscape();
        this.checkWinHound();
        this.checkWinHareByStalling();
        if ("TURN_HARE".equals(this.state)) {
            this.state = "TURN_HOUND";
        } else if ("TURN_HOUND".equals(this.state)) {
            this.state = "TURN_HARE";
        }
    }

    /**
     * Checks if there are any hounds to the left of the hare.
     */
    private void checkWinHareByEscape() {
        int houndX = -1;
        int hareX = -1;
        // Find furthest left x position for hare and hound.
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 3; y++) {
                if (this.board[x][y] == 1) {
                    houndX = x;
                } else if (this.board[x][y] == 2) {
                    hareX = x;
                }
            }
            // If hound is left of hare, no win
            if (houndX != -1 && hareX == -1) {
                return;
                //If hare is right of or at the same x as furthest left hound,
                //hare wins.
            } else if (hareX != -1) {
                this.state = "WIN_HARE_BY_ESCAPE";
            }
        }
    }

    /**
     * Checks to see if the hare has nowhere to move.
     */
    private void checkWinHound() {
        int x = 0;
        int y = 0;
        //Find Hare.
        for (x = 0; x < 5; x++) {
            for (y = 0; y < 3; y++) {
                if (this.board[x][y] == 2) {
                    break;
                }
            }
            if (y != 3 && this.board[x][y] == 2) {
                break;
            }
        }
        //Check all adjacent places to see if move is possible.
        for (int x2 = 0; x2 < 5; x2++) {
            for (int y2 = 0; y2 < 3; y2++) {
                if (this.paths[x][y][x2][y2] && this.board[x2][y2] == 0) {
                    return;
                }
            }
        }
        this.state = "WIN_HOUND";
    }

    /**
     * Checks if the current board position is the third repeat of this
     * position. It does this by converting the position of the pieces into a
     * 9 digit integer that uniquely describes the position. One digit for the
     * x coord and y coord of each piece, and another to indicate which is the
     * hare.
     */
    private void checkWinHareByStalling() {
        int game = 0;
        int count = 1;
        int hareCount = 1;
        // Generates the unique id of this board position.
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 3; y++) {
                if (this.board[x][y] == 2) {
                    hareCount = count;
                }
                if (this.board[x][y] == 1 || this.board[x][y] == 2) {
                    game = game * 10 + x;
                    game = game * 10 + y;
                    count++;
                }
            }
        }
        game = game * 10 + hareCount;
        int repeats = 1;
        // Checks to see if this is the third time this position occurs.
        for (Integer i : this.boardHistory) {
            if (i.equals(new Integer(game))) {
                repeats++;
            }
        }
        this.boardHistory.add(game);
        //If it is, Hare wins by stalling.
        if (repeats > 2) {
            this.state = "WIN_HARE_BY_STALLING";
        }
    }
}
