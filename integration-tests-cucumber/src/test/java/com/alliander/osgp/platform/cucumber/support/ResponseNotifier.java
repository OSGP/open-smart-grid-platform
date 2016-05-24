package com.alliander.osgp.platform.cucumber.support;

/**
 * helper interface that 'notifies' the cucumber test that a response message has been delivered.
 *
 */
public interface ResponseNotifier {

    /**
     * Use this method to poll if an response with the given correlId is available within a given time-lap otherwise false is returned.
     * The total time-lap = laptime * lapcount
     * 
     * @param correlid, String
     * @param laptime, int milisec to wait before next poll request to the database
     * @param maxlaps, int max nr of times to poll. 
     * @return boolean true if response is available, false if no response is available within a timelap.
     */
    boolean waitForResponse(final String correlid, final int laptime, final int maxlaps);
}
