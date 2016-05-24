package com.alliander.osgp.platform.cucumber.support;

/**
 * helper interface that 'notifies' the cucumber test that a response message has been delivered.
 *
 */
public interface ResponseNotifier {

    /**
     * Use this method to poll if an response with the given correlId is available. If so than true will be returned and the value will be in the first element of the input array
     * otherwise false is returned, and the input is left untouched.
     * @param correlid, String
     * @param response, an array with at least one element. If the response is available, the first (0) element will be filled with the result.
     * @return boolean
     */
    boolean isResponseAvailable(final String correlid);
}
