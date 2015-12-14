package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.ClientConnection;
import org.openmuc.jdlms.DataObject;
import org.openmuc.jdlms.GetRequestParameter;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodType;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainerGas;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsGas;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsQuery;

@Component()
public class GetPeriodicMeterReadsGasCommandExecutor implements
        CommandExecutor<PeriodicMeterReadsQuery, PeriodicMeterReadsContainerGas> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPeriodicMeterReadsGasCommandExecutor.class);

    private static final int CLASS_ID_PROFILE_GENERIC = 7;
    private static final ObisCode OBIS_CODE_INTERVAL_MBUS_1 = new ObisCode("0.1.24.3.0.255");
    private static final ObisCode OBIS_CODE_INTERVAL_MBUS_2 = new ObisCode("0.2.24.3.0.255");
    private static final ObisCode OBIS_CODE_INTERVAL_MBUS_3 = new ObisCode("0.3.24.3.0.255");
    private static final ObisCode OBIS_CODE_INTERVAL_MBUS_4 = new ObisCode("0.4.24.3.0.255");
    private static final ObisCode OBIS_CODE_DAILY_BILLING = new ObisCode("1.0.99.2.0.255");
    private static final ObisCode OBIS_CODE_MONTHLY_BILLING = new ObisCode("0.0.98.1.0.255");
    private static final byte ATTRIBUTE_ID_BUFFER = 2;

    private static final int CLASS_ID_CLOCK = 8;
    private static final byte[] OBIS_BYTES_CLOCK = new byte[] { 0, 0, 1, 0, 0, (byte) 255 };
    private static final byte ATTRIBUTE_ID_TIME = 2;

    private static final int CLASS_ID_DATA = 1;
    private static final byte[] OBIS_BYTES_AMR_PROFILE_STATUS = new byte[] { 0, 0, 96, 10, 2, (byte) 255 };

    private static final byte ATTRIBUTE_ID_VALUE = 2;

    private static final int ACCESS_SELECTOR_RANGE_DESCRIPTOR = 1;

    private static final int BUFFER_INDEX_CLOCK = 0;
    private static final int BUFFER_INDEX_AMR_STATUS = 1;
    private static final int BUFFER_INDEX_MBUS_VALUE_FIRST = 6;
    private static final int BUFFER_INDEX_MBUS_CAPTURETIME_FIRST = 7;
    private static final int BUFFER_INDEX_MBUS_VALUE_INT = 2;
    private static final int BUFFER_INDEX_MBUS_CAPTURETIME_INT = 3;

    @Autowired
    private DlmsHelperService dlmsHelperService;

    @Override
    public PeriodicMeterReadsContainerGas execute(final ClientConnection conn,
            final PeriodicMeterReadsQuery periodicMeterReadsRequest) throws IOException, ProtocolAdapterException {

        final PeriodType periodType;
        final DateTime beginDateTime;
        final DateTime endDateTime;
        if (periodicMeterReadsRequest != null) {
            periodType = periodicMeterReadsRequest.getPeriodType();
            beginDateTime = new DateTime(periodicMeterReadsRequest.getBeginDate());
            endDateTime = new DateTime(periodicMeterReadsRequest.getEndDate());
        } else {
            throw new IllegalArgumentException(
                    "PeriodicMeterReadsRequestData should contain PeriodType, BeginDate and EndDate.");
        }

        final GetRequestParameter profileBuffer = this.getProfileBuffer(periodType,
                periodicMeterReadsRequest.getChannel());
        final SelectiveAccessDescription selectiveAccessDescription = this.getSelectiveAccessDescription(periodType,
                beginDateTime, endDateTime);
        profileBuffer.setAccessSelection(selectiveAccessDescription);

        LOGGER.debug(
                "Retrieving current billing period and profiles for class id: {}, obis code: {}, attribute id: {}",
                profileBuffer.classId(), profileBuffer.obisCode(), profileBuffer.attributeId());
        if (selectiveAccessDescription != null) {
            LOGGER.debug("Selective access: selector=" + selectiveAccessDescription.accessSelector() + ", parameter="
                    + this.dlmsHelperService.getDebugInfo(selectiveAccessDescription.accessParameter()));
        }

        final List<GetResult> getResultList = conn.get(profileBuffer);

        checkResultList(getResultList);

        final List<PeriodicMeterReadsGas> periodicMeterReads = new ArrayList<>();

        final GetResult getResult = getResultList.get(0);
        final AccessResultCode resultCode = getResult.resultCode();
        LOGGER.debug("AccessResultCode: {}({})", resultCode.name(), resultCode.value());
        final DataObject resultData = getResult.resultData();
        LOGGER.debug(this.dlmsHelperService.getDebugInfo(resultData));
        final List<DataObject> bufferedObjectsList = resultData.value();

        for (final DataObject bufferedObject : bufferedObjectsList) {
            final List<DataObject> bufferedObjects = bufferedObject.value();
            this.processNextPeriodicMeterReads(periodType, beginDateTime, endDateTime, periodicMeterReads,
                    bufferedObjects, periodicMeterReadsRequest.getChannel());
        }

        return new PeriodicMeterReadsContainerGas(periodicMeterReads);
    }

    private void processNextPeriodicMeterReads(final PeriodType periodType, final DateTime beginDateTime,
            final DateTime endDateTime, final List<PeriodicMeterReadsGas> periodicMeterReads,
            final List<DataObject> bufferedObjects, final int channel) {

        final DataObject clock = bufferedObjects.get(BUFFER_INDEX_CLOCK);
        final DateTime bufferedDateTime = this.dlmsHelperService.fromDateTimeValue((byte[]) clock.value());
        if (bufferedDateTime.isBefore(beginDateTime) || bufferedDateTime.isAfter(endDateTime)) {
            final DateTimeFormatter dtf = ISODateTimeFormat.dateTime();
            LOGGER.warn("Not using an object from capture buffer (clock=" + dtf.print(bufferedDateTime)
                    + "), because the date does not match the given period: [" + dtf.print(beginDateTime) + " .. "
                    + dtf.print(endDateTime) + "].");
            return;
        }

        LOGGER.debug("Processing profile (" + periodType + ") objects captured at: {}",
                this.dlmsHelperService.getDebugInfo(clock));

        switch (periodType) {
        case INTERVAL:
            this.processNextPeriodicMeterReadsForInterval(periodicMeterReads, bufferedObjects, bufferedDateTime);
            break;
        case DAILY:
            this.processNextPeriodicMeterReadsForDaily(periodicMeterReads, bufferedObjects, bufferedDateTime, channel);
            break;
        case MONTHLY:
            this.processNextPeriodicMeterReadsForMonthly(periodicMeterReads, bufferedObjects, bufferedDateTime, channel);
            break;
        default:
            throw new AssertionError("Unknown PeriodType: " + periodType);
        }
    }

    private void processNextPeriodicMeterReadsForInterval(final List<PeriodicMeterReadsGas> periodicMeterReads,
            final List<DataObject> bufferedObjects, final DateTime bufferedDateTime) {

        final DataObject amrStatus = bufferedObjects.get(BUFFER_INDEX_AMR_STATUS);
        LOGGER.warn("TODO - handle amrStatus ({})", this.dlmsHelperService.getDebugInfo(amrStatus));

        final DataObject gasValue = bufferedObjects.get(BUFFER_INDEX_MBUS_VALUE_INT);
        LOGGER.debug("gasValue: {}", this.dlmsHelperService.getDebugInfo(gasValue));

        final DataObject gasCaptureTime = bufferedObjects.get(BUFFER_INDEX_MBUS_CAPTURETIME_INT);
        LOGGER.debug("gasCaptureTime: {}", this.dlmsHelperService.getDebugInfo(gasCaptureTime));

        final PeriodicMeterReadsGas nextPeriodicMeterReads = new PeriodicMeterReadsGas(bufferedDateTime.toDate(),
                PeriodType.INTERVAL, (Long) gasValue.value(), this.dlmsHelperService.fromDateTimeValue(
                        (byte[]) gasCaptureTime.value()).toDate());
        periodicMeterReads.add(nextPeriodicMeterReads);
    }

    private void processNextPeriodicMeterReadsForDaily(final List<PeriodicMeterReadsGas> periodicMeterReads,
            final List<DataObject> bufferedObjects, final DateTime bufferedDateTime, final int channel) {

        final DataObject amrStatus = bufferedObjects.get(BUFFER_INDEX_AMR_STATUS);
        LOGGER.warn("TODO - handle amrStatus ({})", this.dlmsHelperService.getDebugInfo(amrStatus));

        final DataObject gasValue = bufferedObjects.get(BUFFER_INDEX_MBUS_VALUE_FIRST + channel - 1);
        LOGGER.debug("gasValue: {}", this.dlmsHelperService.getDebugInfo(gasValue));
        final DataObject gasCaptureTime = bufferedObjects.get(BUFFER_INDEX_MBUS_CAPTURETIME_FIRST + channel - 1);
        LOGGER.debug("gasCaptureTime: {}", this.dlmsHelperService.getDebugInfo(gasCaptureTime));

        final PeriodicMeterReadsGas nextPeriodicMeterReads = new PeriodicMeterReadsGas(bufferedDateTime.toDate(),
                PeriodType.DAILY, (Long) gasValue.value(), this.dlmsHelperService.fromDateTimeValue(
                        (byte[]) gasCaptureTime.value()).toDate());
        periodicMeterReads.add(nextPeriodicMeterReads);
    }

    private void processNextPeriodicMeterReadsForMonthly(final List<PeriodicMeterReadsGas> periodicMeterReads,
            final List<DataObject> bufferedObjects, final DateTime bufferedDateTime, final int channel) {

        /*
         * Buffer indexes minus one, since Monthly captured objects don't
         * include the AMR Profile status.
         */
        final DataObject amrStatus = bufferedObjects.get(BUFFER_INDEX_AMR_STATUS);
        LOGGER.warn("TODO - handle amrStatus ({})", this.dlmsHelperService.getDebugInfo(amrStatus));

        final DataObject gasValue = bufferedObjects.get(BUFFER_INDEX_MBUS_VALUE_FIRST + channel - 2);
        LOGGER.debug("gasValue: {}", this.dlmsHelperService.getDebugInfo(gasValue));
        final DataObject gasCaptureTime = bufferedObjects.get(BUFFER_INDEX_MBUS_CAPTURETIME_FIRST + channel - 2);
        LOGGER.debug("gasCaptureTime: {}", this.dlmsHelperService.getDebugInfo(gasCaptureTime));

        final PeriodicMeterReadsGas nextPeriodicMeterReads = new PeriodicMeterReadsGas(bufferedDateTime.toDate(),
                PeriodType.MONTHLY, (Long) gasValue.value(), this.dlmsHelperService.fromDateTimeValue(
                        (byte[]) gasCaptureTime.value()).toDate());
        periodicMeterReads.add(nextPeriodicMeterReads);
    }

    private static void checkResultList(final List<GetResult> getResultList) throws ProtocolAdapterException {
        if (getResultList.isEmpty()) {
            throw new ProtocolAdapterException(
                    "No GetResult received while retrieving current billing period and profiles.");
        }

        if (getResultList.size() > 1) {
            LOGGER.info("Expected 1 GetResult while retrieving current billing period and profiles, got "
                    + getResultList.size());
        }
    }

    private ObisCode intervalForChannel(final int channel) throws ProtocolAdapterException {
        switch (channel) {
        case 1:
            return OBIS_CODE_INTERVAL_MBUS_1;
        case 2:
            return OBIS_CODE_INTERVAL_MBUS_2;
        case 3:
            return OBIS_CODE_INTERVAL_MBUS_3;
        case 4:
            return OBIS_CODE_INTERVAL_MBUS_4;
        default:
            throw new ProtocolAdapterException(String.format("channel %s not supported", channel));
        }
    }

    private GetRequestParameter getProfileBuffer(final PeriodType periodType, final int channel)
            throws ProtocolAdapterException {
        GetRequestParameter profileBuffer;

        switch (periodType) {
        case INTERVAL:
            profileBuffer = new GetRequestParameter(CLASS_ID_PROFILE_GENERIC, this.intervalForChannel(channel),
                    ATTRIBUTE_ID_BUFFER);
            break;
        case DAILY:
            profileBuffer = new GetRequestParameter(CLASS_ID_PROFILE_GENERIC, OBIS_CODE_DAILY_BILLING,
                    ATTRIBUTE_ID_BUFFER);
            break;
        case MONTHLY:
            profileBuffer = new GetRequestParameter(CLASS_ID_PROFILE_GENERIC, OBIS_CODE_MONTHLY_BILLING,
                    ATTRIBUTE_ID_BUFFER);
            break;
        default:
            throw new ProtocolAdapterException(String.format("periodtype %s not supported", periodType));
        }
        return profileBuffer;
    }

    private SelectiveAccessDescription getSelectiveAccessDescription(final PeriodType periodType,
            final DateTime beginDateTime, final DateTime endDateTime) {

        final int accessSelector = ACCESS_SELECTOR_RANGE_DESCRIPTOR;

        /*
         * Define the clock object {8,0-0:1.0.0.255,2,0} to be used as
         * restricting object in a range descriptor with a from value and to
         * value to determine which elements from the buffered array should be
         * retrieved.
         */
        final DataObject clockDefinition = DataObject.newStructureData(Arrays.asList(
                DataObject.newUInteger16Data(CLASS_ID_CLOCK), DataObject.newOctetStringData(OBIS_BYTES_CLOCK),
                DataObject.newInteger8Data(ATTRIBUTE_ID_TIME), DataObject.newUInteger16Data(0)));

        final DataObject fromValue = this.dlmsHelperService.asDataObject(beginDateTime);
        final DataObject toValue = this.dlmsHelperService.asDataObject(endDateTime);

        /*
         * List of object definitions to determine which of the capture objects
         * to retrieve from the buffer.
         */
        final List<DataObject> objectDefinitions = new ArrayList<>();

        switch (periodType) {
        case INTERVAL:
            this.addSelectedValuesForInterval(objectDefinitions);
            break;
        case DAILY:
            this.addSelectedValuesForDaily(objectDefinitions);
            break;
        case MONTHLY:
            this.addSelectedValuesForMonthly(objectDefinitions);
            break;
        default:
            throw new AssertionError("Unknown PeriodType: " + periodType);
        }

        /*
         * For properly limiting data retrieved from the meter selectedValues
         * should be something like: DataObject.newArrayData(objectDefinitions);
         */
        LOGGER.warn("TODO - figure out how to set selectedValues to something like: "
                + this.dlmsHelperService.getDebugInfo(DataObject.newArrayData(objectDefinitions)));
        /*
         * As long as specifying a subset of captured objects from the buffer
         * through selectedValues does not work, retrieve all captured objects
         * by setting selectedValues to an empty array.
         */
        final DataObject selectedValues = DataObject.newArrayData(Collections.<DataObject> emptyList());

        final DataObject accessParameter = DataObject.newStructureData(Arrays.asList(clockDefinition, fromValue,
                toValue, selectedValues));

        return new SelectiveAccessDescription(accessSelector, accessParameter);
    }

    private void addSelectedValuesForInterval(final List<DataObject> objectDefinitions) {
        /*-
         * Available objects in the profile buffer (1-0:99.1.0.255):
         * {8,0-0:1.0.0.255,2,0}    -  clock
         * {1,0-0:96.10.2.255,2,0}  -  AMR profile status
         */

        /*
         * Do not include {8,0-0:1.0.0.255,2,0} - clock here, since it is
         * already used as restricting object.
         */

        // {1,0-0:96.10.2.255,2,0} - AMR profile status
        objectDefinitions.add(DataObject.newStructureData(Arrays.asList(DataObject.newUInteger16Data(CLASS_ID_DATA),
                DataObject.newOctetStringData(OBIS_BYTES_AMR_PROFILE_STATUS),
                DataObject.newInteger8Data(ATTRIBUTE_ID_VALUE), DataObject.newUInteger16Data(0))));

    }

    private void addSelectedValuesForDaily(final List<DataObject> objectDefinitions) {
        /*-
         * Available objects in the profile buffer (1-0:99.2.0.255):
         * {8,0-0:1.0.0.255,2,0}    -  clock
         * {1,0-0:96.10.2.255,2,0}  -  AMR profile status
         *
         * Objects not retrieved with E meter readings:
         * {4,0-1.24.2.1.255,2,0}  -  M-Bus Master Value 1 Channel 1
         * {4,0-1.24.2.1.255,5,0}  -  M-Bus Master Value 1 Channel 1 Capture time
         * {4,0-2.24.2.1.255,2,0}  -  M-Bus Master Value 1 Channel 2
         * {4,0-2.24.2.1.255,5,0}  -  M-Bus Master Value 1 Channel 2 Capture time
         * {4,0-3.24.2.1.255,2,0}  -  M-Bus Master Value 1 Channel 3
         * {4,0-3.24.2.1.255,5,0}  -  M-Bus Master Value 1 Channel 3 Capture time
         * {4,0-4.24.2.1.255,2,0}  -  M-Bus Master Value 1 Channel 4
         * {4,0-4.24.2.1.255,5,0}  -  M-Bus Master Value 1 Channel 4 Capture time
         */

        /*
         * Do not include {8,0-0:1.0.0.255,2,0} - clock here, since it is
         * already used as restricting object.
         */

        // {1,0-0:96.10.2.255,2,0} - AMR profile status
        objectDefinitions.add(DataObject.newStructureData(Arrays.asList(DataObject.newUInteger16Data(CLASS_ID_DATA),
                DataObject.newOctetStringData(OBIS_BYTES_AMR_PROFILE_STATUS),
                DataObject.newInteger8Data(ATTRIBUTE_ID_VALUE), DataObject.newUInteger16Data(0))));

    }

    private void addSelectedValuesForMonthly(final List<DataObject> objectDefinitions) {
        /*-
         * Available objects in the profile buffer (0-0:98.1.0.255):
         * {8,0-0:1.0.0.255,2,0}    -  clock
         *
         * Objects not retrieved with E meter readings:
         * {4,0-1.24.2.1.255,2,0}  -  M-Bus Master Value 1 Channel 1
         * {4,0-1.24.2.1.255,5,0}  -  M-Bus Master Value 1 Channel 1 Capture time
         * {4,0-2.24.2.1.255,2,0}  -  M-Bus Master Value 1 Channel 2
         * {4,0-2.24.2.1.255,5,0}  -  M-Bus Master Value 1 Channel 2 Capture time
         * {4,0-3.24.2.1.255,2,0}  -  M-Bus Master Value 1 Channel 3
         * {4,0-3.24.2.1.255,5,0}  -  M-Bus Master Value 1 Channel 3 Capture time
         * {4,0-4.24.2.1.255,2,0}  -  M-Bus Master Value 1 Channel 4
         * {4,0-4.24.2.1.255,5,0}  -  M-Bus Master Value 1 Channel 4 Capture time
         */

        /*
         * Do not include {8,0-0:1.0.0.255,2,0} - clock here, since it is
         * already used as restricting object.
         */

    }
}
