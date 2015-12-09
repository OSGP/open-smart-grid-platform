package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.ClientConnection;
import org.openmuc.jdlms.DataObject;
import org.openmuc.jdlms.GetRequestParameter;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.RequestParameterFactory;
import org.openmuc.jdlms.SetRequestParameter;
import org.openmuc.jdlms.internal.BitString;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationFlag;
import com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationFlagType;
import com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationFlags;
import com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationObject;
import com.alliander.osgp.dto.valueobjects.smartmetering.GprsOperationModeType;

@Component()
public class SetConfigurationObjectCommandExecutor implements CommandExecutor<ConfigurationObject, AccessResultCode> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetConfigurationObjectCommandExecutor.class);

    private static final int CLASS_ID = 1;
    private static final ObisCode OBIS_CODE = new ObisCode("0.1.94.31.3.255");
    private static final int ATTRIBUTE_ID = 2;

    @Autowired
    private ConfigurationObjectHelperService configurationObjectHelperService;

    @Autowired
    private DlmsHelperService dlmsHelperService;

    @Override
    public AccessResultCode execute(final ClientConnection conn, final ConfigurationObject configurationObject)
            throws IOException, ProtocolAdapterException {

        final ConfigurationObject configurationObjectOnDevice = this.retrieveConfigurationObject(conn);

        if (this.isRequestValid(configurationObject, configurationObjectOnDevice)) {
            final SetRequestParameter request = this.buildRequest(configurationObject, configurationObjectOnDevice);

            return AccessResultCode.SUCCESS; // conn.set(request).get(0);
        } else {
            throw new ProtocolAdapterException(
                    "Not a valid change request. One of the flags HLS_3_on_P3_enable or HLS_3_on_P3_enable or HLS_3_on_P3_enable must be enabled!");
        }
    }

    private SetRequestParameter buildRequest(final ConfigurationObject configurationObject,
            final ConfigurationObject configurationObjectOnDevice) {

        final BitString bitString = this.getMergedFlags(configurationObject, configurationObjectOnDevice);

        final DataObject complexData = this.buildRequestObject(configurationObject, bitString);
        LOGGER.info("Configuration object complex data: {}", this.dlmsHelperService.getDebugInfo(complexData));

        final RequestParameterFactory factory = new RequestParameterFactory(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);
        final SetRequestParameter request = factory.createSetRequestParameter(complexData);
        return request;
    }

    private DataObject buildRequestObject(final ConfigurationObject configurationObject, final BitString bitString) {
        final LinkedList<DataObject> linkedList = new LinkedList<DataObject>();
        if (GprsOperationModeType.ALWAYS_ON.equals(configurationObject.getGprsOperationMode())) {
            linkedList.add(DataObject.newEnumerateData(1));
        } else {
            linkedList.add(DataObject.newEnumerateData(0));
        }
        final DataObject newBitStringData = DataObject.newBitStringData(bitString);
        linkedList.add(newBitStringData);
        final DataObject complexData = DataObject.newStructureData(linkedList);
        return complexData;
    }

    private BitString getMergedFlags(final ConfigurationObject configurationObject,
            final ConfigurationObject configurationObjectOnDevice) {
        final List<ConfigurationFlag> configurationFlags = new ArrayList<ConfigurationFlag>();
        this.getNewFlags(configurationObject, configurationFlags);
        this.mergeOldFlags(configurationObjectOnDevice, configurationFlags);

        final byte[] newConfigurationObjectFlagsByteArray = this.configurationObjectHelperService
                .toByteArray(configurationFlags);
        final BitString bitString = new BitString(newConfigurationObjectFlagsByteArray, 16);
        return bitString;
    }

    private void mergeOldFlags(final ConfigurationObject configurationObjectOnDevice,
            final List<ConfigurationFlag> configurationFlags) {
        for (final ConfigurationFlag configurationFlagOnDevice : configurationObjectOnDevice.getConfigurationFlags()
                .getConfigurationFlag()) {
            final ConfigurationFlag configurationFlag = this.getConfigurationFlag(configurationFlags,
                    configurationFlagOnDevice.getConfigurationFlagType());
            if (configurationFlag == null) {
                configurationFlags.add(configurationFlagOnDevice);
            }
        }
    }

    private void getNewFlags(final ConfigurationObject configurationObject,
            final List<ConfigurationFlag> configurationFlags) {
        for (final ConfigurationFlag configurationFlag : configurationObject.getConfigurationFlags()
                .getConfigurationFlag()) {
            configurationFlags.add(configurationFlag);
        }
    }

    /**
     * Validation: A change of the configuration object is only accepted by the
     * meter when at least one of the following flags are enabled:
     * HLS_3_on_P3_enable HLS_4_on_P3_enable HLS_5_on_P3_enable
     */
    private boolean isRequestValid(final ConfigurationObject configurationObject,
            final ConfigurationObject configurationObjectOnDevice) {

        final List<ConfigurationFlag> configurationFlags = configurationObject.getConfigurationFlags()
                .getConfigurationFlag();
        final List<ConfigurationFlag> configurationFlagsOnDevice = configurationObjectOnDevice.getConfigurationFlags()
                .getConfigurationFlag();

        final boolean hls3onP3enable = this.isFlagEnabled(configurationFlags, configurationFlagsOnDevice,
                ConfigurationFlagType.HLS_3_ON_P_3_ENABLE);
        final boolean hls4onP3enable = this.isFlagEnabled(configurationFlags, configurationFlagsOnDevice,
                ConfigurationFlagType.HLS_4_ON_P_3_ENABLE);
        final boolean hls5onP3enable = this.isFlagEnabled(configurationFlags, configurationFlagsOnDevice,
                ConfigurationFlagType.HLS_5_ON_P_3_ENABLE);

        return hls3onP3enable || hls4onP3enable || hls5onP3enable;
    }

    private boolean isFlagEnabled(final List<ConfigurationFlag> configurationFlags,
            final List<ConfigurationFlag> configurationFlagsOnDevice, final ConfigurationFlagType flagType) {
        final ConfigurationFlag newConfigurationFlag = this.getConfigurationFlag(configurationFlags, flagType);
        if (newConfigurationFlag != null && newConfigurationFlag.isEnabled()) {
            return true;
        } else {
            final ConfigurationFlag currentConfigurationFlag = this.getConfigurationFlag(configurationFlagsOnDevice,
                    flagType);
            if (currentConfigurationFlag != null && currentConfigurationFlag.isEnabled()) {
                return true;
            }
        }
        return false;
    }

    private ConfigurationFlag getConfigurationFlag(final Collection<ConfigurationFlag> flags,
            final ConfigurationFlagType flagType) {
        for (final ConfigurationFlag configurationFlag : flags) {
            if (configurationFlag.getConfigurationFlagType().equals(flagType)) {
                return configurationFlag;
            }
        }
        return null;
    }

    private ConfigurationObject retrieveConfigurationObject(final ClientConnection conn) throws IOException,
            ProtocolAdapterException {

        final RequestParameterFactory factory = new RequestParameterFactory(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);
        final GetRequestParameter getRequestParameter = factory.createGetRequestParameter();

        LOGGER.info(
                "Retrieving current configuration object by issuing get request for class id: {}, obis code: {}, attribute id: {}",
                getRequestParameter.classId(), getRequestParameter.obisCode(), getRequestParameter.attributeId());
        final List<GetResult> getResultList = conn.get(getRequestParameter);

        if (getResultList == null || getResultList.isEmpty()) {
            throw new ProtocolAdapterException("No GetResult received while retrieving current configuration object.");
        }

        if (getResultList.size() > 1) {
            throw new ProtocolAdapterException(
                    "Expected 1 GetResult while retrieving current configuration object, got " + getResultList.size());
        }

        return this.getConfigurationObject(getResultList);
    }

    private ConfigurationObject getConfigurationObject(final List<GetResult> resultList)
            throws ProtocolAdapterException {

        final DataObject resultData = resultList.get(0).resultData();
        LOGGER.info("Configuration object current complex data: {}", this.dlmsHelperService.getDebugInfo(resultData));
        final LinkedList<DataObject> linkedList = resultData.value();
        final DataObject GprsOperationModeData = linkedList.get(0);
        GprsOperationModeType gprsOperationMode = null;
        if (((Number) GprsOperationModeData.value()).longValue() == 1) {
            gprsOperationMode = GprsOperationModeType.ALWAYS_ON;
        } else {
            gprsOperationMode = GprsOperationModeType.TRIGGERED;
        }

        final DataObject flagsData = linkedList.get(1);
        if (flagsData == null) {
            throw new ProtocolAdapterException("DataObject expected to contain a configuration object is null.");
        }
        if (!(flagsData.value() instanceof BitString)) {
            throw new ProtocolAdapterException("Value in DataObject is not a BitString: "
                    + resultData.value().getClass().getName());
        }
        final byte[] flagByteArray = ((BitString) flagsData.value()).bitString();

        return new ConfigurationObject(gprsOperationMode, new ConfigurationFlags(
                this.configurationObjectHelperService.toConfigurationFlags(flagByteArray)));
    }

}
