/**
 * Gregory Levine <glevine4@jhu.edu>
 * 601.421 Object Oriented Software Engineering
 * Homework 1
 * September 18th, 2017
 */

package com.oose2017.glevine4.hareandhound;

/**
 * A class to read json into with gson.
 */
public class Move {
    /** The id of the player */
    private String playerId;
    /** The x coord of piece to move. */
    private String fromX;
    /** The Y coord of piece to move. */
    private String fromY;
    /** The X coord to move to. */
    private String toX;
    /** The Y coord to move to. */
    private String toY;

    /**
     * Constructor for the class.
     * @param playerId the player id for piece to move.
     * @param fromX the x coord to move from.
     * @param fromY the y coord to move from.
     * @param toX the x coord to move to.
     * @param toY the y coord to move to.
     */
    public Move(String playerId, String fromX, String fromY, String toX,
        String toY) {
        this.playerId = playerId;
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
    }

    /**
     * Returns the player id.
     * @return the player id.
     */
    public String getPlayerId() {
        return this.playerId;
    }

    /**
     * Get the x coord of piece.
     * @return the coordinate.
     */
    public String getFromX() {
        return this.fromX;
    }

    /**
     * Get the y coord of piece.
     * @return the y coordinate.
     */
    public String getFromY() {
        return this.fromY;
    }

    /**
     * Get the x coord to move to.
     * @return the x coordinate.
     */
    public String getToX() {
        return this.toX;
    }

    /**
     * Get the y coord to move to.
     * @return the y coordinate.
     */
    public String getToY() {
        return this.toY;
    }
}
