package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

/**
 * DlmsConnectionMessageProcessor implementation, used to test the abstract
 * DlmsConnectionMessageProcessor base class.
 */
class DlmsConnectionMessageProcessorImpl extends DlmsConnectionMessageProcessor {
    private final DlmsMessageListener messageListener;

    public DlmsConnectionMessageProcessorImpl(final DlmsConnectionHelper connectionHelper,
            final DlmsMessageListener messageListener, final DlmsDeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
        this.connectionHelper = connectionHelper;
        this.messageListener = messageListener;
    }

    @Override
    public DlmsConnectionManager createConnectionForDevice(final DlmsDevice device,
            final MessageMetadata messageMetadata) throws OsgpException {
        return super.createConnectionForDevice(device, messageMetadata);
    }

    @Override
    protected DlmsMessageListener createMessageListenerForDeviceConnection(final DlmsDevice device,
            final MessageMetadata messageMetadata) {
        return this.messageListener;
    }
}
