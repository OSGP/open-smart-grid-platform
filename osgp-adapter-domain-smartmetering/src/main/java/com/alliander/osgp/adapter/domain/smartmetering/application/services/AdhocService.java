package com.alliander.osgp.adapter.domain.smartmetering.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.AdhocMapper;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.core.OsgpCoreRequestMessageSender;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.dto.valueobjects.smartmetering.SynchronizeTimeReads;
import com.alliander.osgp.dto.valueobjects.smartmetering.SynchronizeTimeReadsRequest;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.RequestMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;


/**
 * @author OSGP
 *
 */
@Service(value = "domainSmartMeteringAdhocService")
@Transactional(value = "transactionManager")
public class AdhocService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdhocService.class);

    @Autowired
    @Qualifier(value = "domainSmartMeteringOutgoingOsgpCoreRequestMessageSender")
    private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

    @Autowired
    private AdhocMapper adhocMapper;

    @Autowired
    private WebServiceResponseMessageSender webServiceResponseMessageSender;

    public AdhocService() {
        // Parameterless constructor required for transactions...
    }

    
    public void requestSynchronizeTimeData(
            @Identification final String organisationIdentification,
            @Identification final String deviceIdentification,
            final String correlationUid,
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.SynchronizeTimeReadsRequest synchronizeTimeReadsRequestValueObject,
            final String messageType) throws FunctionalException {

        LOGGER.info("requestSynchronizeTimeReads for organisationIdentification: {} for deviceIdentification: {}",
                organisationIdentification, deviceIdentification);

        // TODO: bypassing authorization, this should be fixed.
        // Organisation organisation =
        // this.findOrganisation(organisationIdentification);
        // final Device device = this.findActiveDevice(deviceIdentification);

        // TODO deviceAuthorization
        // final DeviceAuthorization deviceAuthorization = new
        // DeviceAuthorization(duMy, organisation,
        // com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup.OWNER);
        // this.deviceAuthorizationRepository.save(deviceAuthorization);

        final SynchronizeTimeReadsRequest synchronizeTimeReadsRequestDto = this.adhocMapper.map(
        		synchronizeTimeReadsRequestValueObject, SynchronizeTimeReadsRequest.class);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification,
                deviceIdentification, synchronizeTimeReadsRequestDto), messageType);
    }

    public void handleSynchronizeTimeReadsresponse(final String deviceIdentification,
            final String organisationIdentification, final String correlationUid, final String messageType,
            final ResponseMessageResultType deviceResult, final OsgpException exception,
            final SynchronizeTimeReads synchronizeTimeReadsValueDTO) {

        LOGGER.info("handleSynchronizeReadsresponse for MessageType: {}", messageType);

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        OsgpException osgpException = exception;

        try {
            if (deviceResult == ResponseMessageResultType.NOT_OK || osgpException != null) {
                LOGGER.error("Device Response not ok.", osgpException);
                throw osgpException;
            }
        } catch (final Exception e) {
            LOGGER.error("Unexpected Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            osgpException = new TechnicalException(ComponentType.UNKNOWN,
                    "Unexpected exception while retrieving response message", e);

        }

        final com.alliander.osgp.domain.core.valueobjects.smartmetering.SynchronizeTimeReads SynchronizeTimeReadsValueDomain = this.adhocMapper
                .map(synchronizeTimeReadsValueDTO,
                        com.alliander.osgp.domain.core.valueobjects.smartmetering.SynchronizeTimeReads.class);

        this.webServiceResponseMessageSender.send(new ResponseMessage(correlationUid, organisationIdentification,
                deviceIdentification, result, osgpException, SynchronizeTimeReadsValueDomain), messageType);

    }
}
