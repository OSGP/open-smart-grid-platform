package org.osgp.adapter.protocol.dlms.domain.factories;

import java.io.IOException;

import javax.naming.OperationNotSupportedException;

import org.openmuc.jdlms.LnClientConnection;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;

import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

public class DlmsConnectionFactory {

    private final int clientAccessPoint;
    private final int logicalDeviceAddress;
    private final int responseTimeout;

    private final DlmsDeviceRepository dlmsDeviceRepository;

    public DlmsConnectionFactory(final DlmsDeviceRepository dlmsDeviceRepository, final int clientAccessPoint,
            final int logicalDeviceAddress, final int responseTimeout) {
        this.dlmsDeviceRepository = dlmsDeviceRepository;
        this.clientAccessPoint = clientAccessPoint;
        this.logicalDeviceAddress = logicalDeviceAddress;
        this.responseTimeout = responseTimeout;
    }

    /**
     * Returns an open connection using the appropriate security settings for
     * the device.
     *
     * @param device
     *            The device to connect to. This reference can be updated when
     *            the invalid but correctable connection credentials are
     *            detected.
     * @return an open connection
     * @throws IOException
     * @throws OperationNotSupportedException
     */
    public LnClientConnection getConnection(DlmsDevice device) throws TechnicalException {
        if (device.isHls5Active()) {
            final Hls5Connector connector = new Hls5Connector(device, this.responseTimeout, this.logicalDeviceAddress,
                    this.clientAccessPoint);
            final LnClientConnection connection = connector.connect();
            device = this.dlmsDeviceRepository.save(connector.getUpdatedDevice());
            return connection;
        } else {
            // TODO ADD IMPLEMENTATIONS FOR OTHER SECURITY MODES
            throw new UnsupportedOperationException("Only HLS 5 connections are currently supported");
        }
    }
}
