/**
 * Gregory Levine <glevine4@jhu.edu>
 * 601.421 Object Oriented Software Engineering
 * Homework 1
 * September 18th, 2017
 */

package com.oose2017.glevine4.hareandhound;

import static spark.Spark.*;

/**
 * The class containing main, sets things up.
 */
public final class Bootstrap {

    /**
     * The ip address to use.
     */
    public static final String IP_ADDRESS = "localhost";

    /**
     * The port to use.
     */
    public static final int PORT = 8080;

    /**
     * Hide the constructor.
     */
    private Bootstrap() {
    }

    /**
     * The main function. Creates the model instance and starts the web service.
     * @param args the command line args. Not used.
     * @throws Exception can throw exceptions. Shouldnt happen.
     */
    public static void main(String[] args) throws Exception {
        //Specify the IP address and Port at which the server should be run
        ipAddress(IP_ADDRESS);
        port(PORT);

        //Specify the sub-directory from which to serve static resources
        // (like html and css)
        staticFileLocation("/public");

        //Create the model instance and then configure and start the web service

        HareAndHoundService model = new HareAndHoundService();
        new HareAndHoundController(model);
    }
}
