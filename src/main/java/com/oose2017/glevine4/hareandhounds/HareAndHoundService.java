/**
 * Gregory Levine <glevine4@jhu.edu>
 * 601.421 Object Oriented Software Engineering
 * Homework 1
 * September 18th, 2017
 */

package com.oose2017.glevine4.hareandhound;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * A class which provides necessary functions to the controller.
 */
public class HareAndHoundService {

    /** An list to hold all the games that have been created. */
    private List<GameState> games = new ArrayList<GameState>();

    /**
     * Create a new game with a unique gameId.
     * @param body a JSON string containing the type of pieces for the creating
     *     player.
     * @return A JSON string containing gameId, playerId, and the type of
     *     pieces for the first player.
     * @throws HareAndHoundServiceException if the piece type is invalid
     */
    public String createGame(String body) throws HareAndHoundServiceException {
        String pieceType = this.getPieceType(body);
        if (!"HARE".equals(pieceType) && !"HOUND".equals(pieceType)) {
            throw new HareAndHoundServiceException("Invalid piece type", 400);
        }
        GameState newGame = new GameState(pieceType);
        this.games.add(newGame);

        return "{ \"gameId\": \"" + newGame.getGameId()
             + "\", \"playerId\": \"" + newGame.getPlayerId(pieceType)
             + "\", \"pieceType\": \"" + pieceType + "\" }";
    }

    /**
     * Add a second player to the game with gameId.
     * @param gameId the id of the game to join.
     * @return A JSON object with gameId, a playerId, and the type of pieces the
     *     new player will play.
     * @throws HareAndHoundServiceException if the game id is invalid, or the
     *     game can not be joined.
     */
    public String joinGame(String gameId) throws HareAndHoundServiceException {
        for (GameState game : this.games) {
            if (gameId.equals(game.getGameId())) {
                String pieceType = game.joinGame();
                if (pieceType == null) {
                    throw new HareAndHoundServiceException(
                        "Second Player is already joined", 410);
                }
                return "{ \"gameId\": \"" + game.getGameId()
                     + "\", \"playerId\": \"" + game.getPlayerId(pieceType)
                     + "\", \"pieceType\": \"" + pieceType + "\" }";
            }
        }
        throw new HareAndHoundServiceException("Invalid Game Id", 404);
    }

    /**
     * Moves a piece.
     * @param gameId the id of the game in which to move piece.
     * @param body JSON string containing instructions on which piece to move
     *     where.
     * @return a JSON object containing the id of player whose piece moved.
     * @throws HareAndHoundServiceException If given a bad request, invalid move
     *     invalid gameId
     */
    public String movePiece(String gameId, String body)
        throws HareAndHoundServiceException {
        //Parse JSON input in body.
        String[] tokens = body.split("\"");
        if (tokens.length != 25 || !tokens[0].equals("{")) {
            throw new HareAndHoundServiceException("Bad Request", 400);
        }
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 1; i < 25; i += 4) {
            map.put(tokens[i], tokens[i + 2]);
            if (!tokens[i + 1].equals(":") || !tokens[i + 3].equals(",")) {
                if (i != 21 || !tokens[i + 3].equals("}")) {
                    throw new HareAndHoundServiceException("Bad Request", 400);
                }
            }
        }
        //Find the game with gameId and call move to move the piece.
        for (GameState game : this.games) {
            if (gameId.equals(game.getGameId())) {
                return this.move(game, map.get("playerId"),
                    Integer.parseInt(map.get("fromX")),
                    Integer.parseInt(map.get("fromY")),
                    Integer.parseInt(map.get("toX")),
                    Integer.parseInt(map.get("toY")));
            }
        }
        throw new HareAndHoundServiceException(
            "{ \"reason\": \"INVALID_GAME_ID\" }", 404);
    }

    /**
     * Returns a JSON representation of the current positions of the pieces on
     * the board with the given id.
     * @param gameId the id of the gameId
     * @return the JSON representation.
     * @throws HareAndHoundServiceException if the gameId is invalid
     */
    public String describeBoard(String gameId)
        throws HareAndHoundServiceException {
        for (GameState game : this.games) {
            if (gameId.equals(game.getGameId())) {
                return game.describe();
            }
        }
        throw new HareAndHoundServiceException("Invalid Game Id", 404);
    }

    /**
     * Used to get the state of the game with the given id.
     * @param gameId the id of the game.
     * @return a string representing the state of the game.
     * @throws HareAndHoundServiceException if the gameId is invalid.
     */
    public String getState(String gameId) throws HareAndHoundServiceException {
        for (GameState game : this.games) {
            if (gameId.equals(game.getGameId())) {
                return game.getState();
            }
        }
        throw new HareAndHoundServiceException("Invalid Game Id", 404);
    }

    //-----------------------------------------------------------------------//
    // Helper Classes and Methods
    //-----------------------------------------------------------------------//

    /**
     * An exception to be used in this project. Just wraps Exception and adds
     * the ability to attach an error code.
     */
    public static final class HareAndHoundServiceException extends Exception {
        /** Holds an error code. */
        private final int code;

        /**
         * Constructs and instance of this exception.
         * @param message the message for the exception.
         * @param code the error code.
         */
        public HareAndHoundServiceException(String message, int code) {
            super(message);
            this.code = code;
        }

        /**
         * Return the error code.
         * @return the code.
         */
        public int getCode() {
            return this.code;
        }
    }

    /**
     * Moves the piece as specified
     * @param game the GameState object cooresponding to the game in which to
     *     move the piece.
     * @param playerId the id of the player whose piecce should be moved.
     * @param fromX the x coodinate of the piece to be moved.
     * @param fromY the y coordinate of the piece to be moved.
     * @param toX the x coordinate of the place to move piece.
     * @param toY the y coordinate of the place to move piece.
     * @return a string of JSON to respond with.
     */
    private String move(GameState game, String playerId, int fromX,
        int fromY, int toX, int toY) throws HareAndHoundServiceException {
        if (game.getPlayerId("HARE").equals(playerId)
            || game.getPlayerId("HOUND").equals(playerId)) {
            if (!game.nextPlayer().equals(playerId)) {
                throw new HareAndHoundServiceException(
                    "{ \"reason\": \"INCORRECT TURN\" }", 422);
            }
            if (!game.movePiece(fromX, fromY, toX, toY)) {
                throw new HareAndHoundServiceException(
                    "{ \"reason\": \"ILLEGAL_MOVE\" }", 422);
            }
            return "{ \"playerId\": \"" + playerId + "\"}";
        } else {
            throw new HareAndHoundServiceException(
                "{ \"reason\": \"INVALID_PLAYER_ID\" }", 404);
        }
    }

    /**
     * Returns the type of the piece given in the JSON string.
     * @param json the String
     * @return the piece type
     */
    private String getPieceType(String json) {
        String[] tokens = json.split("\"");
        if (tokens.length != 5 || !tokens[0].equals("{")
            || !tokens[1].equals("pieceType")) {
            return "";
        }
        if (!tokens[2].equals(":") || !tokens[4].equals("}")) {
            return "";
        }
        return tokens[3];
    }
}
