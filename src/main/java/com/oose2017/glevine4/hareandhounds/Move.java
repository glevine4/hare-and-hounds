package com.oose2017.glevine4.hareandhound;

public class Move {
    private String playerId;
    private String fromX;
    private String fromY;
    private String toX;
    private String toY;

    public Move(String playerId, String fromX, String fromY, String toX,
        String toY) {
        this.playerId = playerId;
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
    }

    public String getPlayerId() {
        return this.playerId;
    }

    public String getFromX() {
        return this.fromX;
    }

    public String getFromY() {
        return this.fromY;
    }

    public String getToX() {
        return this.toX;
    }

    public String getToY() {
        return this.toY;
    }
}
