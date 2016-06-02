package com.alliander.osgp.platform.cucumber.hooks;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class SimulatePushedAlarmsHooks {

    public static void simulateAlarm(final String deviceId, final byte[] alarmsToPush) throws UnknownHostException,
    IOException {
        final Socket socket = new Socket("osgp-tst.cloudapp.net", 9598);
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