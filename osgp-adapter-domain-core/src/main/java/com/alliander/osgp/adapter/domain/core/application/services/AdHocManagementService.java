package com.alliander.osgp.adapter.domain.core.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.infra.jms.RequestMessage;

@Service(value = "domainCoreAdHocManagementService")
@Transactional(value = "transactionManager")
public class AdHocManagementService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdHocManagementService.class);

    @Autowired
    private DeviceRepository deviceRepository;

    /**
     * Constructor
     */
    public AdHocManagementService() {
        // Parameterless constructor required for transactions...
    }

    // === SET REBOOT ===

    public void setReboot(@Identification final String organisationIdentification, @Identification final String deviceIdentification,
            final String correlationUid, final String messageType) throws FunctionalException {

        LOGGER.debug("set reboot called for device {} with organisation {}", deviceIdentification, organisationIdentification);

        this.findOrganisation(organisationIdentification);

        final Device device = this.findActiveDevice(deviceIdentification);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification, deviceIdentification, null), messageType, device
                .getNetworkAddress().toString());
    }
}
