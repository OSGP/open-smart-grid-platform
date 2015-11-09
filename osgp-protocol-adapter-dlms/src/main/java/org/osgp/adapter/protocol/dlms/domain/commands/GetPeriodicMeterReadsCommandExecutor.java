package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.ClientConnection;
import org.openmuc.jdlms.DataObject;
import org.openmuc.jdlms.GetRequestParameter;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodType;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReads;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainer;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequest;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestData;

@Component()
public class GetPeriodicMeterReadsCommandExecutor implements
CommandExecutor<PeriodicMeterReadsRequest, PeriodicMeterReadsContainer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPeriodicMeterReadsCommandExecutor.class);

    private static final int CLASS_ID_PROFILE_GENERIC = 7;
    private static final ObisCode OBIS_CODE_DAILY_BILLING = new ObisCode("1.0.99.2.0.255");
    private static final ObisCode OBIS_CODE_MONTHLY_BILLING = new ObisCode("0.0.98.1.0.255");
    private static final int ATTRIBUTE_ID_BUFFER = 2;

    private static final int BUFFER_INDEX_CLOCK = 0;
    private static final int BUFFER_INDEX_AMR_STATUS = 1;
    private static final int BUFFER_INDEX_A_POS_RATE_1 = 2;
    private static final int BUFFER_INDEX_A_POS_RATE_2 = 3;
    private static final int BUFFER_INDEX_A_NEG_RATE_1 = 4;
    private static final int BUFFER_INDEX_A_NEG_RATE_2 = 5;

    @Autowired
    private DlmsHelperService dlmsHelperService;

    @Override
    public PeriodicMeterReadsContainer execute(final ClientConnection conn,
            final PeriodicMeterReadsRequest periodicMeterReadsRequest) throws IOException, ProtocolAdapterException {

        final List<PeriodicMeterReadsRequestData> periodicMeterReadsRequestData = periodicMeterReadsRequest
                .getPeriodicMeterReadsRequestData();

        final PeriodType periodType;
        final DateTime dateTime;
        if (periodicMeterReadsRequestData != null && !periodicMeterReadsRequestData.isEmpty()) {
            periodType = periodicMeterReadsRequestData.get(0).getPeriodType();
            dateTime = new DateTime(periodicMeterReadsRequestData.get(0).getDate());
        } else {
            periodType = PeriodType.MONTHLY;
            dateTime = DateTime.now();
        }

        GetRequestParameter getProfileBuffer;

        if (periodType == PeriodType.DAILY) {
            getProfileBuffer = new GetRequestParameter(CLASS_ID_PROFILE_GENERIC, OBIS_CODE_DAILY_BILLING,
                    ATTRIBUTE_ID_BUFFER);
        } else {
            getProfileBuffer = new GetRequestParameter(CLASS_ID_PROFILE_GENERIC, OBIS_CODE_MONTHLY_BILLING,
                    ATTRIBUTE_ID_BUFFER);
        }

        LOGGER.info("Retrieving current billing period and profiles for class id: {}, obis code: {}, attribute id: {}",
                getProfileBuffer.classId(), getProfileBuffer.obisCode(), getProfileBuffer.attributeId());

        final List<GetResult> getResultList = conn.get(getProfileBuffer);

        if (getResultList.isEmpty()) {
            throw new ProtocolAdapterException(
                    "No GetResult received while retrieving current billing period and profiles.");
        }

        if (getResultList.size() > 1) {
            LOGGER.info("Expected 1 GetResult while retrieving current billing period and profiles, got "
                    + getResultList.size());
        }

        final PeriodicMeterReadsContainer periodicMeterReadsContainer = new PeriodicMeterReadsContainer();
        final List<PeriodicMeterReads> periodicMeterReads = new ArrayList<>();
        periodicMeterReadsContainer.setPeriodicMeterReads(periodicMeterReads);
        periodicMeterReadsContainer.setDeviceIdentification(periodicMeterReadsRequest.getDeviceIdentification());

        final GetResult getResult = getResultList.get(0);
        final AccessResultCode resultCode = getResult.resultCode();
        LOGGER.info("AccessResultCode: {}({})", resultCode.name(), resultCode.value());
        final DataObject resultData = getResult.resultData();
        LOGGER.info(this.dlmsHelperService.getDebugInfo(resultData));

        final List<DataObject> bufferedObjectsList = resultData.value();

        final int year = dateTime.getYear();
        final int monthOfYear = dateTime.getMonthOfYear();
        final int dayOfMonth = dateTime.getDayOfMonth();

        for (final DataObject bufferedObject : bufferedObjectsList) {

            final List<DataObject> bufferedObjects = bufferedObject.value();

            final DataObject clock = bufferedObjects.get(BUFFER_INDEX_CLOCK);
            final DateTime bufferedDateTime = this.dlmsHelperService.fromDateTimeValue((byte[]) clock.value());

            final boolean useBufferedObject;
            if (PeriodType.DAILY == periodType) {
                useBufferedObject = year == bufferedDateTime.getYear()
                        && monthOfYear == bufferedDateTime.getMonthOfYear()
                        && dayOfMonth == bufferedDateTime.getDayOfMonth();
            } else {
                useBufferedObject = year == bufferedDateTime.getYear()
                        && monthOfYear == bufferedDateTime.getMonthOfYear();
            }
            if (useBufferedObject) {
                LOGGER.info("Using object from capture buffer, because the date matches the given period.");
            } else {
                LOGGER.info("Not using an object from capture buffer, because the date does not match the given period.");
                continue;
            }

            LOGGER.info("clock: {}", this.dlmsHelperService.getDebugInfo(clock));

            final DataObject amrStatus = bufferedObjects.get(BUFFER_INDEX_AMR_STATUS);
            LOGGER.info("Skipping amrStatus ({}) and M-Bus values.", this.dlmsHelperService.getDebugInfo(amrStatus));
            final DataObject positiveActiveEnergyTariff1 = bufferedObjects.get(BUFFER_INDEX_A_POS_RATE_1);
            LOGGER.info("positiveActiveEnergyTariff1: {}",
                    this.dlmsHelperService.getDebugInfo(positiveActiveEnergyTariff1));
            final DataObject positiveActiveEnergyTariff2 = bufferedObjects.get(BUFFER_INDEX_A_POS_RATE_2);
            LOGGER.info("positiveActiveEnergyTariff2: {}",
                    this.dlmsHelperService.getDebugInfo(positiveActiveEnergyTariff2));
            final DataObject negativeActiveEnergyTariff1 = bufferedObjects.get(BUFFER_INDEX_A_NEG_RATE_1);
            LOGGER.info("negativeActiveEnergyTariff1: {}",
                    this.dlmsHelperService.getDebugInfo(negativeActiveEnergyTariff1));
            final DataObject negativeActiveEnergyTariff2 = bufferedObjects.get(BUFFER_INDEX_A_NEG_RATE_2);
            LOGGER.info("negativeActiveEnergyTariff2: {}",
                    this.dlmsHelperService.getDebugInfo(negativeActiveEnergyTariff2));

            final PeriodicMeterReads nextPeriodicMeterReads = new PeriodicMeterReads();
            nextPeriodicMeterReads.setLogTime(bufferedDateTime.toDate());
            nextPeriodicMeterReads.setActiveEnergyImportTariffOne((Long) positiveActiveEnergyTariff1.value());
            nextPeriodicMeterReads.setActiveEnergyImportTariffTwo((Long) positiveActiveEnergyTariff2.value());
            nextPeriodicMeterReads.setActiveEnergyExportTariffOne((Long) negativeActiveEnergyTariff1.value());
            nextPeriodicMeterReads.setActiveEnergyExportTariffTwo((Long) negativeActiveEnergyTariff2.value());
            periodicMeterReads.add(nextPeriodicMeterReads);
        }

        return periodicMeterReadsContainer;
    }

}
