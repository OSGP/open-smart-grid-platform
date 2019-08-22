package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.interfaceclass.InterfaceClass;
import org.openmuc.jdlms.interfaceclass.attribute.DataAttribute;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GetConfigurationObjectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetConfigurationObjectService.class);

    public static final int CLASS_ID = InterfaceClass.DATA.id();
    public static final ObisCode OBIS_CODE = new ObisCode("0.1.94.31.3.255");
    public static final int ATTRIBUTE_ID = DataAttribute.VALUE.attributeId();

    /**
     * Allows implementations to indicate which protocols they handle
     * (make sure each protocol is handled exclusively by one implementation or results might be non-deterministic)
     */
    public abstract boolean handles(Protocol protocol);

    public ConfigurationObjectDto getConfigurationObjectDto(final DlmsConnectionManager conn)
            throws ProtocolAdapterException {
        try {
            return this.retrieveConfigurationObject(conn);
        } catch (final IOException e) {
            throw new ConnectionException(e);
        }
    }

    private ConfigurationObjectDto retrieveConfigurationObject(final DlmsConnectionManager conn)
            throws IOException, ProtocolAdapterException {

        final AttributeAddress configurationObjectValue = new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);

        conn.getDlmsMessageListener().setDescription(
                "retrieve current ConfigurationObject, attribute: " + JdlmsObjectToStringUtil.describeAttributes(
                        configurationObjectValue));

        LOGGER.info("Retrieving current configuration object by issuing get request for class id: {}, obis code: {}, "
                + "attribute id: {}", CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);
        final GetResult getResult = conn.getConnection().get(configurationObjectValue);

        if (getResult == null) {
            throw new ProtocolAdapterException("No result received while retrieving current configuration object.");
        } else if (getResult.getResultCode() != AccessResultCode.SUCCESS) {
            throw new ProtocolAdapterException(
                    "Non-sucessful result received retrieving configuration object: " + getResult.getResultCode());
        }

        return this.getConfigurationObject(getResult);
    }

    abstract ConfigurationObjectDto getConfigurationObject(final GetResult result) throws ProtocolAdapterException;

    List<ConfigurationFlagDto> toConfigurationFlagDtos(final byte[] flagByteArray) {
        final List<ConfigurationFlagDto> configurationFlags = new ArrayList<>();
        final BitSet bitSet = BitSet.valueOf(
                new long[] { ((flagByteArray[0] & 0xFF) << 8) + (flagByteArray[1] & 0xFF) });
        for (int index = bitSet.nextSetBit(0); index >= 0; index = bitSet.nextSetBit(index + 1)) {
            // TODO: handle SMR5
            ConfigurationFlagTypeDto.getDsmr4FlagType(index).ifPresent(
                    configurationFlagType -> configurationFlags.add(new ConfigurationFlagDto(configurationFlagType, true)));
        }
        return configurationFlags;
    }

}
