/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.hooks;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import com.alliander.osgp.platform.dlms.cucumber.ApplicationConfiguration;

/**
 * DLMS specific. 
 */
public class SimulatePushedAlarmsHooks {
	
    /**
     * 
     * @param deviceId
     * @param alarmsToPush
     * @throws UnknownHostException
     * @throws IOException
     */
    public static void simulateAlarm(final String deviceId, final byte[] alarmsToPush) throws UnknownHostException,
    IOException {
    	ApplicationConfiguration applicationConfig = new ApplicationConfiguration();
    	
        // TODO Make this configurable.
        final Socket socket = new Socket(applicationConfig.alarmNotificationsHost, Integer.parseInt(applicationConfig.alarmNotificationsPort));
        try {
            final OutputStream outputStream = socket.getOutputStream();
            outputStream.write(deviceId.getBytes(StandardCharsets.US_ASCII));
            outputStream.write(alarmsToPush);
            socket.shutdownOutput();
            socket.shutdownInput();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

}