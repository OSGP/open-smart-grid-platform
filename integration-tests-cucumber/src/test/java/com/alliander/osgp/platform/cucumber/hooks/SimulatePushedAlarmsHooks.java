package com.alliander.osgp.platform.cucumber.hooks;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class SimulatePushedAlarmsHooks {

    public static void simulateAlarm(final String deviceId, final byte[] alarmsToPush, final String host,
            final int port) throws UnknownHostException, IOException {
        try (final Socket socket = new Socket(host, port);) {
            final OutputStream outputStream = socket.getOutputStream();
            outputStream.write(deviceId.getBytes(StandardCharsets.US_ASCII));
            outputStream.write(alarmsToPush);
            socket.shutdownOutput();
            socket.shutdownInput();
        }
    }

}