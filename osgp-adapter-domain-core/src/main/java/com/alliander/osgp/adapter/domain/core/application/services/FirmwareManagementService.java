package com.alliander.osgp.adapter.domain.core.application.services;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.exceptions.PlatformException;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.infra.jms.RequestMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "domainCoreFirmwareManagementService")
@Transactional(value = "transactionManager")
public class FirmwareManagementService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceManagementService.class);

    /**
     * Constructor
     */
    public FirmwareManagementService() {
        // Parameterless constructor required for transactions...
    }

    // === UPDATE FIRMWARE ===

    public void updateFirmware(final String organisationIdentification, final String deviceIdentification, final String correlationUid,
            @NotBlank final String firmwareIdentification, final Long scheduleTime, final String messageType) throws FunctionalException {

        LOGGER.debug("Update firmware called with organisation [{}], device [{}], firmwareIdentification [{}].", organisationIdentification,
                deviceIdentification, firmwareIdentification);

        this.findOrganisation(organisationIdentification);
        final Device device = this.findActiveDevice(deviceIdentification);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification, deviceIdentification, firmwareIdentification),
                messageType, device.getNetworkAddress().toString(), scheduleTime);
    }

    // === GET FIRMWARE VERSION ===

    public void getFirmwareVersion(@Identification final String organisationIdentification, @Identification final String deviceIdentification,
            final String correlationUid, final String messageType) throws FunctionalException {

        LOGGER.debug("Get firmware version called with organisation [{}], device [{}].", organisationIdentification, deviceIdentification);

        this.findOrganisation(organisationIdentification);
        final Device device = this.findActiveDevice(deviceIdentification);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification, deviceIdentification, null), messageType, device
                .getNetworkAddress().toString());
    }

    public void handleGetFirmwareVersionResponse(final String firmwareVersion, final String deviceIdentification, final String organisationIdentification,
            final String correlationUid, final String messageType, final ResponseMessageResultType deviceResult, final String errorDescription) {

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        String description = "";

        try {
            if (deviceResult == ResponseMessageResultType.NOT_OK || StringUtils.isNotEmpty(errorDescription)) {
                throw new PlatformException("Device Response not ok.");
            }
        } catch (final Exception e) {
            LOGGER.error("Unexpected Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            description = e.getMessage();
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(correlationUid, organisationIdentification, deviceIdentification, result, description,
                firmwareVersion));
    }
}
