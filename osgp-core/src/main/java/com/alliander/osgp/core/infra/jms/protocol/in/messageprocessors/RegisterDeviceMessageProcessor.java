package com.alliander.osgp.core.infra.jms.protocol.in.messageprocessors;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.core.infra.jms.protocol.in.ProtocolRequestMessageProcessor;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.dto.valueobjects.DeviceRegistrationData;
import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.RequestMessage;

@Component("oslpRegisterDeviceMessageProcessor")
public class RegisterDeviceMessageProcessor extends ProtocolRequestMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterDeviceMessageProcessor.class);

    @Autowired
    private DeviceRepository deviceRepository;

    private static final String LOCAL_HOST = "127.0.0.1";

    protected RegisterDeviceMessageProcessor() {
        super(DeviceFunction.REGISTER_DEVICE);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        final String messageType = message.getJMSType();
        final String organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
        final String deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);

        LOGGER.info("Received message of messageType: {} organisationIdentification: {} deviceIdentification: {}",
                messageType, organisationIdentification, deviceIdentification);

        final RequestMessage requestMessage = (RequestMessage) message.getObject();
        final Object dataObject = requestMessage.getRequest();

        try {
            final DeviceRegistrationData deviceRegistrationData = (DeviceRegistrationData) dataObject;

            this.updateRegistrationData(deviceIdentification, deviceRegistrationData.getIpAddress(),
                    deviceRegistrationData.getDeviceType(), deviceRegistrationData.isHasSchedule());
        } catch (final UnknownHostException e) {
            throw new JMSException(e.getMessage());
        }
    }

    // === REGISTER DEVICE ===

    /**
     * Update device registration data (ipaddress, etc). Device is added
     * (without an owner) when not exist yet.
     * 
     * @param deviceIdentification
     *            The device identification.
     * @param ipAddress
     *            The IP address of the device.
     * @param deviceType
     *            The type of the device, SSLD or PSLD.
     * @param hasSchedule
     *            In case the device has a schedule, this will be true.
     * 
     * @return Device with updated data
     * 
     * @throws UnknownHostException
     */
    @Transactional(value = "transactionManager")
    private Device updateRegistrationData(final String deviceIdentification, final String ipAddress,
            final String deviceType, final boolean hasSchedule) throws UnknownHostException {

        LOGGER.info("updateRegistrationData called for device: {} ipAddress: {}, deviceType: {}.",
                deviceIdentification, ipAddress, deviceType);

        // Convert the IP address from String to InetAddress.
        final InetAddress address = LOCAL_HOST.equals(ipAddress) ? InetAddress.getLoopbackAddress() : InetAddress
                .getByName(ipAddress);

        // Lookup device
        Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);

        // Check for existing IP addresses
        this.clearDuplicateAddresses(deviceIdentification, address);

        if (device == null) {
            // Device does not exist yet, create without an owner.
            device = new Device(deviceIdentification);
        }

        // Device already exists, update registration data
        device.updateRegistrationData(address, deviceType);
        return this.deviceRepository.save(device);
    }

    private void clearDuplicateAddresses(final String deviceIdentification, final InetAddress address) {
        final List<Device> devices = this.deviceRepository.findByNetworkAddress(address);

        for (final Device device : devices) {
            if (!LOCAL_HOST.equals(device.getNetworkAddress().getHostAddress())) {
                if (!device.getDeviceIdentification().equals(deviceIdentification)) {
                    device.clearNetworkAddress();
                    this.deviceRepository.save(device);
                }
            }
        }
    }
}
