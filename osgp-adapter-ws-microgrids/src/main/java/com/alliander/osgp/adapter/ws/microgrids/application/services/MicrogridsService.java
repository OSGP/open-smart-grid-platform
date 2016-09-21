package com.alliander.osgp.adapter.ws.microgrids.application.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.adapter.ws.microgrids.application.exceptionhandling.ResponseNotFoundException;
import com.alliander.osgp.adapter.ws.microgrids.domain.entities.RtuResponseData;
import com.alliander.osgp.adapter.ws.microgrids.infra.jms.MicrogridsRequestMessage;
import com.alliander.osgp.adapter.ws.microgrids.infra.jms.MicrogridsRequestMessageSender;
import com.alliander.osgp.adapter.ws.microgrids.infra.jms.MicrogridsRequestMessageType;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.exceptions.ArgumentNullOrEmptyException;
import com.alliander.osgp.domain.core.services.CorrelationIdProviderService;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.microgrids.entities.RtuDevice;
import com.alliander.osgp.domain.microgrids.valueobjects.DataRequest;
import com.alliander.osgp.domain.microgrids.valueobjects.DataResponse;
import com.alliander.osgp.domain.microgrids.valueobjects.EmptyResponse;
import com.alliander.osgp.domain.microgrids.valueobjects.Measurement;
import com.alliander.osgp.domain.microgrids.valueobjects.MeasurementFilter;
import com.alliander.osgp.domain.microgrids.valueobjects.MeasurementResultSystemIdentifier;
import com.alliander.osgp.domain.microgrids.valueobjects.SetPointsRequest;
import com.alliander.osgp.domain.microgrids.valueobjects.SystemFilter;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;

@Service
@Transactional(value = "wsTransactionManager")
@Validated
public class MicrogridsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MicrogridsService.class);

    @Autowired
    private boolean stubResponses;

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private CorrelationIdProviderService correlationIdProviderService;

    @Autowired
    private MicrogridsRequestMessageSender requestMessageSender;

    @Autowired
    private RtuResponseDataService responseDataService;

    private Map<String, Object> mockRequestHolder = new HashMap<>();

    public MicrogridsService() {
        // Parameterless constructor required for transactions
    }

    public String enqueueGetDataRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, @NotNull final DataRequest dataRequest)
            throws OsgpException {

        LOGGER.debug("enqueueGetDataRequest called with organisation {} and device {}", organisationIdentification,
                deviceIdentification);

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        if (this.stubResponses) {
            this.mockRequestHolder.put(correlationUid, dataRequest);
            return correlationUid;
        }

        final RtuDevice device = this.domainHelperService.findDevice(deviceIdentification);
        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.GET_DATA);

        final MicrogridsRequestMessage message = new MicrogridsRequestMessage(MicrogridsRequestMessageType.GET_DATA,
                correlationUid, organisationIdentification, deviceIdentification, dataRequest, null);

        try {
            this.requestMessageSender.send(message);
        } catch (final ArgumentNullOrEmptyException e) {
            throw new TechnicalException(ComponentType.WS_MICROGRIDS, e);
        }

        return correlationUid;
    }

    public DataResponse dequeueGetDataResponse(final String correlationUid) throws OsgpException {

        LOGGER.debug("dequeueGetDataRequest called with correlation uid {}", correlationUid);

        if (this.stubResponses) {
            return this.getStubbedGetDataResponse(correlationUid);
        }

        final RtuResponseData responseData = this.responseDataService.dequeue(correlationUid, ResponseMessage.class);
        final ResponseMessage response = (ResponseMessage) responseData.getMessageData();

        switch (response.getResult()) {
        case NOT_FOUND:
            throw new ResponseNotFoundException(ComponentType.WS_MICROGRIDS, "Response message not found.");
        case NOT_OK:
            if (response.getOsgpException() != null) {
                throw response.getOsgpException();
            }
            throw new TechnicalException(ComponentType.WS_MICROGRIDS, "Response message not ok.");
        case OK:
            if (response.getDataObject() != null) {
                return (DataResponse) response.getDataObject();
            }
            // Should not get here
            throw new TechnicalException(ComponentType.WS_MICROGRIDS, "Response message contains no data.");
        default:
            // Should not get here
            throw new TechnicalException(ComponentType.WS_MICROGRIDS, "Response message contains invalid result.");
        }

    }

    public String enqueueSetSetPointsRequest(final String organisationIdentification,
            final String deviceIdentification, final SetPointsRequest setPointsRequest) throws OsgpException {

        LOGGER.debug("enqueueSetSetPointsRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        if (this.stubResponses) {
            this.mockRequestHolder.put(correlationUid, setPointsRequest);
            return correlationUid;
        }

        final RtuDevice device = this.domainHelperService.findDevice(deviceIdentification);
        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SET_SETPOINT);

        final MicrogridsRequestMessage message = new MicrogridsRequestMessage(
                MicrogridsRequestMessageType.SET_SETPOINT, correlationUid, organisationIdentification,
                deviceIdentification, setPointsRequest, null);

        try {
            this.requestMessageSender.send(message);
        } catch (final ArgumentNullOrEmptyException e) {
            throw new TechnicalException(ComponentType.WS_MICROGRIDS, e);
        }

        return correlationUid;
    }

    public EmptyResponse dequeueSetSetPointsResponse(final String correlationUid) throws OsgpException {

        LOGGER.debug("dequeueSetSetPointsRequest called with correlation uid {}", correlationUid);

        if (this.stubResponses) {
            return this.getStubbedSetSetPointsResponse(correlationUid);
        }

        final RtuResponseData responseData = this.responseDataService.dequeue(correlationUid, ResponseMessage.class);
        final ResponseMessage response = (ResponseMessage) responseData.getMessageData();

        switch (response.getResult()) {
        case NOT_FOUND:
            throw new ResponseNotFoundException(ComponentType.WS_MICROGRIDS, "Response message not found.");
        case NOT_OK:
            if (response.getOsgpException() != null) {
                throw response.getOsgpException();
            }
            throw new TechnicalException(ComponentType.WS_MICROGRIDS, "Response message not ok.");
        case OK:
            return new EmptyResponse();
        default:
            // Should not get here
            throw new TechnicalException(ComponentType.WS_MICROGRIDS, "Response message contains invalid result.");
        }

    }

    private DataResponse getStubbedGetDataResponse(final String correlationUid) {
        // Send fake response, depending on requested data
        if (!this.mockRequestHolder.containsKey(correlationUid)) {
            return null;
        }
        final List<MeasurementResultSystemIdentifier> results = new ArrayList<>();
        final DataRequest request = (DataRequest) this.mockRequestHolder.get(correlationUid);
        final DateTime sampleTime = new DateTime(DateTimeZone.UTC);

        // Use original filters to fake responses
        for (final SystemFilter systemFilter : request.getSystemFilters()) {
            final List<Measurement> measurements = new ArrayList<>();

            if (systemFilter.isAll()) {
                measurements.add(new Measurement(1, "actualpower", 0, sampleTime, 33.0));
                measurements.add(new Measurement(1, "UL", 0, sampleTime, 44.0));
                measurements.add(new Measurement(2, "UL", 0, sampleTime, 55.0));
                measurements.add(new Measurement(3, "UL", 0, sampleTime, 66.0));
            } else {
                for (final MeasurementFilter measurementFilter : systemFilter.getMeasurementFilters()) {
                    measurements.add(new Measurement(1, measurementFilter.getNode(), 0, sampleTime, 99.0));
                }
            }

            results.add(new MeasurementResultSystemIdentifier(systemFilter.getId(), systemFilter.getSystemType(),
                    measurements));
        }

        this.mockRequestHolder.remove(correlationUid);

        return new DataResponse(results);

    }

    private EmptyResponse getStubbedSetSetPointsResponse(final String correlationUid) {
        LOGGER.debug("getStubbedSetSetPointsResponse called with correlationUid: {}", correlationUid);
        return new EmptyResponse();
    }

}
