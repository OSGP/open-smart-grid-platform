package org.osgp.adapter.protocol.dlms.domain.factories;

import java.io.IOException;

import org.openmuc.jdlms.DlmsConnection;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;

import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

public class DeviceConnector implements AutoCloseable {
    private final Hls5Connector connector;
    private final DlmsDevice device;
    private DlmsConnection dlmsConnection;

    public DeviceConnector(final Hls5Connector connector, final DlmsDevice device) {
        this.connector = connector;
        this.device = device;
    }

    public DlmsConnection connection() {
        if (this.dlmsConnection == null) {
            throw new IllegalStateException("");
        }
        return this.dlmsConnection;
    }

    public void disconnect() throws IOException {
        if (this.dlmsConnection != null) {
            this.dlmsConnection.disconnect();
            this.dlmsConnection = null;
        }
    }

    public boolean isConnected() {
        return this.dlmsConnection != null;
    }

    public void connect() throws TechnicalException {
        if (this.dlmsConnection != null) {
            throw new IllegalStateException("");
        }

        connector.setDevice(device);
        this.dlmsConnection = connector.connect();
    }

    @Override
    public void close() throws Exception {
        this.disconnect();
    }
}
