/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.support;

/**
 * helper interface that 'notifies' the cucumber test that a response message
 * has been delivered.
 *
 */
@Deprecated
public interface ResponseNotifier {

    /**
     * Use this method to poll if an response with the given correlId is
     * available within a given time-lap otherwise false is returned. The total
     * time-lap = laptime * lapcount
     *
     * @param correlid
     *            , String
     * @param laptime
     *            , int milisec to wait before next poll request to the database
     * @param maxlaps
     *            , int max nr of times to poll.
     * @return boolean true if response is available, false if no response is
     *         available within a timelap.
     */
    boolean waitForResponse(final String correlid, final int timeout, final int maxtime);
}
