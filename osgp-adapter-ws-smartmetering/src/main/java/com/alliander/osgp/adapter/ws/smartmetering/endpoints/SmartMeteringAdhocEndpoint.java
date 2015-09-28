package com.alliander.osgp.adapter.ws.smartmetering.endpoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.alliander.osgp.adapter.ws.endpointinterceptors.OrganisationIdentification;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.RetrieveSynchronizeTimeReadsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.RetrieveSynchronizeTimeReadsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeReadsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeReadsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.AsyncResponse;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.AdhocMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.services.AdhocService;
import com.alliander.osgp.adapter.ws.smartmetering.domain.entities.PeriodicMeterReads;
import com.alliander.osgp.adapter.ws.smartmetering.domain.entities.SynchronizeTimeReads;
import com.alliander.osgp.adapter.ws.smartmetering.domain.repositories.PeriodicMeterReadsRepository;
import com.alliander.osgp.adapter.ws.smartmetering.domain.repositories.SynchronizeTimeDataRepository;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;


//MethodConstraintViolationException is deprecated.
//Will by replaced by equivalent functionality defined
//by the Bean Validation 1.1 API as of Hibernate Validator 5.
@Endpoint
public class SmartMeteringAdhocEndpoint {

	private static final Logger LOGGER = LoggerFactory.getLogger(SmartMeteringAdhocEndpoint.class);
    private static final String SMARTMETER_ADHOC_NAMESPACE = "http://www.alliander.com/schemas/osgp/smartmetering/sm-adhoc/2014/10";

    @Autowired
    private AdhocService adhocService;

    @Autowired
    private AdhocMapper adhocMapper;
    
    @Autowired
    private SynchronizeTimeDataRepository synchronizeTimeDataRepository;

    public SmartMeteringAdhocEndpoint() {
    }
    
    @PayloadRoot(localPart = "SynchronizeTimeReadsRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
    @ResponsePayload
	public SynchronizeTimeReadsResponse requestSynchronizeTimeData(
    		@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SynchronizeTimeReadsRequest request) throws OsgpException {

        final SynchronizeTimeReadsResponse response = new SynchronizeTimeReadsResponse();

        final com.alliander.osgp.domain.core.valueobjects.smartmetering.SynchronizeTimeReadsRequest dataRequest = this.adhocMapper
        			.map(request, com.alliander.osgp.domain.core.valueobjects.smartmetering.SynchronizeTimeReadsRequest.class);
        
        
        final String correlationUid = this.adhocService.requestSynchronizeTimeData(organisationIdentification, dataRequest);
        
        final AsyncResponse asyncResponse = new AsyncResponse();
        asyncResponse.setCorrelationUid(correlationUid);
        asyncResponse.setDeviceId("567812346584849");
        response.setAsyncResponse(asyncResponse);

        //
        // } catch (final MethodConstraintViolationException e) {
        //
        //
        // LOGGER.error("Exception: {} while adding device: {} for organisation {}.",
        // // new Object[] { e.getMessage(),
        // // request.getDevice().getDeviceIdentification(),
        // // organisationIdentification }, e);
        // //
        // // throw new
        // // FunctionalException(FunctionalExceptionType.VALIDATION_ERROR,
        // // ComponentType.WS_CORE,
        // // new ValidationException(e.getConstraintViolations()));
        // //
        // // } catch (final Exception e) {
        // //
        // //
        // LOGGER.error("Exception: {} while adding device: {} for organisation {}.",
        // // new Object[] { e.getMessage(),
        // // request.getDevice().getDeviceIdentification(),
        // // organisationIdentification }, e);
        // //
        // // this.handleException(e);
        // // }
        //
        return response;
    }

    @PayloadRoot(localPart = "RetrieveSynchronizeTimeReadsRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
    @ResponsePayload
    public RetrieveSynchronizeTimeReadsResponse requestSynchronizeTimeData(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final RetrieveSynchronizeTimeReadsRequest request) throws OsgpException {

        LOGGER.info("Incoming RetrieveSynchronizeTimeReadsRequest for meter: {}.", request.getDeviceIdentification());

        final RetrieveSynchronizeTimeReadsResponse response = new RetrieveSynchronizeTimeReadsResponse();

        try {

            final SynchronizeTimeReads syncTimeReads = this.synchronizeTimeDataRepository
                    .findByCorrelationUidAndDeviceIdentification(request.getCorrelationUid(),
                            request.getDeviceIdentification());

            // TODO not OK when not found
            response.setSynchronizeTimeReads(this.adhocMapper.map(syncTimeReads,
                    com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeReads.class));

            // removing
            this.synchronizeTimeDataRepository.delete(syncTimeReads.getId());

        } catch (final Exception e) {

            LOGGER.error("Exception: {} while sending Synchronize time reads of device: {} for organisation {}.",
                    new Object[] { e.getMessage(), request.getDeviceIdentification(), organisationIdentification }, e);

            this.handleException(e);
        }

        return response;
    }
    
    private void handleException(final Exception e) throws OsgpException {
        // Rethrow exception if it already is a functional or technical
        // exception,
        // otherwise throw new technical exception.
        if (e instanceof OsgpException) {
            LOGGER.error("Exception occurred: ", e);
            throw (OsgpException) e;
        } else {
            LOGGER.error("Exception occurred: ", e);
            throw new TechnicalException(ComponentType.WS_SMART_METERING, e);
        }
    }
}
