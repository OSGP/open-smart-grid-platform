package com.alliander.osgp.adapter.ws.smartmetering.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.adapter.ws.smartmetering.domain.entities.MeterResponseData;
import com.alliander.osgp.adapter.ws.smartmetering.domain.repositories.MeterResponseDataRepository;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;

@Service
public class MeterResponseDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeterResponseDataService.class);

    @Autowired
    private MeterResponseDataRepository meterResponseDataRepository;

    /**
     * Queue response data object.
     *
     * @param meterResponseData
     */
    public void enqueue(final MeterResponseData meterResponseData) {
        this.meterResponseDataRepository.save(meterResponseData);
    }

    /**
     * Dequeue meter response data.
     *
     * If correlationUid does not match type, null will be returned and the data
     * will not be removed from the database.
     *
     * @param correlationUid
     * @param expectedClassType
     *            type to check the data object against.
     * @return MeterReponseData Response data. Null if expected class type does
     *         not match.
     * @throws FunctionalException
     */
    public MeterResponseData dequeue(final String correlationUid, final Class<?> expectedClassType)
            throws FunctionalException {

        final MeterResponseData meterResponseData = this.meterResponseDataRepository
                .findSingleResultByCorrelationUid(correlationUid);

        if (meterResponseData == null) {
            LOGGER.warn("No response data for correlation UID {}", correlationUid);
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_CORRELATION_UID,
                    ComponentType.WS_SMART_METERING);
        }

        if (!expectedClassType.isInstance(meterResponseData.getMessageData())) {
            /**
             * Return null if data is not of the expected type. The Meter
             * response data will not be removed, so it will be available for
             * the right request type.
             */
            final String warningResultClassType = meterResponseData.getMessageData() == null ? "NULL"
                    : meterResponseData.getMessageData().getClass().getName();

            LOGGER.warn("Incorrect type of response data: {} for correlation UID: {}", warningResultClassType,
                    meterResponseData.getCorrelationUid());

            return null;
        }

        this.remove(meterResponseData);
        return meterResponseData;
    }

    public MeterResponseData dequeue(final String correlationUid) throws FunctionalException {
        try {
            final MeterResponseData meterResponseData = this.meterResponseDataRepository
                    .findSingleResultByCorrelationUid(correlationUid);

            if (meterResponseData == null) {
                throw new FunctionalException(FunctionalExceptionType.UNKNOWN_CORRELATION_UID,
                        ComponentType.WS_SMART_METERING);
            }

            this.remove(meterResponseData);
            return meterResponseData;

        } catch (final FunctionalException e) {
            if (e.getExceptionType() == FunctionalExceptionType.UNKNOWN_CORRELATION_UID) {
                LOGGER.warn("No response data for correlation UID {}", correlationUid);
            }

            throw e;
        }
    }

    private void remove(final MeterResponseData meterResponseData) {
        LOGGER.info("deleting MeterResponseData for CorrelationUid {}", meterResponseData.getCorrelationUid());
        this.meterResponseDataRepository.delete(meterResponseData);
    }
}
