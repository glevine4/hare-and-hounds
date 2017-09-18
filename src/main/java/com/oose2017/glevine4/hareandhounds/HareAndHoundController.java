/**
 * Gregory Levine <glevine4@jhu.edu>
 * 601.421 Object Oriented Software Engineering
 * Homework 1
 * September 18th, 2017
 */

package com.oose2017.glevine4.hareandhound;

import java.util.Collections;

import static spark.Spark.*;

/**
 * The controller which takes requests from the frontend.
 */
public class HareAndHoundController {

    /** The service object for the controller. */
    private final HareAndHoundService hareAndHoundService;

    /**
     * Initializes the controller.
     * @param hareAndHoundService the service object for the controllers
     */
    public HareAndHoundController(HareAndHoundService hareAndHoundService) {
        this.hareAndHoundService = hareAndHoundService;
        this.setupEndpoints();
    }

    /**
     * Sets up the various endpoints specified by the api.
     */
    private void setupEndpoints() {
        /**
         * Endpoint for creating a new game.
         */
        post("/hareandhounds/api/games", (request, response) -> {
            try {
                response.status(201);
                return this.hareAndHoundService.createGame(request.body());
            } catch (HareAndHoundService.HareAndHoundServiceException ex) {
                response.status(ex.getCode());
                return Collections.EMPTY_MAP;
            }
        });

        /**
         * Endpoint for joining a created game.
         */
        put("/hareandhounds/api/games/:gameId", (request, response) -> {
            try {
                response.status(200);
                return this.hareAndHoundService.joinGame(
                    request.params(":gameId"));
            } catch (HareAndHoundService.HareAndHoundServiceException ex) {
                response.status(ex.getCode());
                return Collections.EMPTY_MAP;
            }
        });

        /**
         * Endpoint for moving a piece on the board.
         */
        post("/hareandhounds/api/games/:gameId/turns", (request, response)-> {
            try {
                response.status(200);
                return this.hareAndHoundService.movePiece(
                    request.params(":gameId"), request.body());
            } catch (HareAndHoundService.HareAndHoundServiceException ex) {
                response.status(ex.getCode());
                return ex.getMessage();
            }
        });

        /**
         * Endpoint for requesting a description of the current piece locations
         * on the board.
         */
        get("/hareandhounds/api/games/:gameId/board", (request, response) -> {
            try {
                response.status(200);
                return this.hareAndHoundService.describeBoard(
                    request.params(":gameId"));
            } catch (HareAndHoundService.HareAndHoundServiceException ex) {
                response.status(ex.getCode());
                return Collections.EMPTY_MAP;
            }
        });

        /**
         * Endpoing for getting the current state of the game.
         */
        get("/hareandhounds/api/games/:gameId/state", (request, response) -> {
            try {
                response.status(200);
                return this.hareAndHoundService.getState(
                    request.params(":gameId"));
            } catch (HareAndHoundService.HareAndHoundServiceException ex) {
                response.status(ex.getCode());
            }
            return Collections.EMPTY_MAP;
        });
    }
}
