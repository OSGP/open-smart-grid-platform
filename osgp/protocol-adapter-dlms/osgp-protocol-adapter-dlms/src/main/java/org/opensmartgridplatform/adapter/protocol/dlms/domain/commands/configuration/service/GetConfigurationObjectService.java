package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GetConfigurationObjectService implements ProtocolService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetConfigurationObjectService.class);

    public ConfigurationObjectDto getConfigurationObject(final DlmsConnectionManager conn)
            throws ProtocolAdapterException {
        final AttributeAddress attributeAddress = AttributeAddressFactory.getConfigurationObjectAddress();
        conn.getDlmsMessageListener().setDescription(
                String.format("Retrieve current ConfigurationObject, attribute: %s",
                        JdlmsObjectToStringUtil.describeAttributes(attributeAddress)));
        return this.getConfigurationObject(this.getGetResult(conn, attributeAddress));
    }

    private GetResult getGetResult(final DlmsConnectionManager conn, final AttributeAddress attributeAddress)
            throws ProtocolAdapterException {
        LOGGER.info("Get current ConfigurationObject using AttributeAddress {}", attributeAddress);
        try {
            return this.handleBadResults(conn.getConnection().get(attributeAddress));
        } catch (final IOException e) {
            throw new ConnectionException(e);
        }
    }

    private GetResult handleBadResults(final GetResult getResult) throws ProtocolAdapterException {
        if (getResult == null) {
            throw new ProtocolAdapterException("No result received while retrieving current configuration object.");
        } else if (getResult.getResultCode() != AccessResultCode.SUCCESS) {
            throw new ProtocolAdapterException(
                    String.format("Non-successful result received retrieving configuration object: %s",
                            getResult.getResultCode()));
        }
        return getResult;
    }

    /**
     * Extracts the configuration object from the GetResult
     */
    abstract ConfigurationObjectDto getConfigurationObject(final GetResult result) throws ProtocolAdapterException;

    List<ConfigurationFlagDto> toConfigurationFlags(final byte[] flagBytes) {
        final List<ConfigurationFlagDto> flags = new ArrayList<>();
        // 1) convert first byte to int: b1 & 0xFF -> 0 0 0 b1
        // 2) shift the first int 8 bits to the left: -> 0 0 b1 0
        // 3) convert second byte to int: b2 & 0xFF -> 0 0 0 b2
        // add the 2 ints into a long: 0 0 b1 0 + 0 0 0 b2 = 0 0 0 0 0 0 b1 b2
        // create a BitSet from the long
        final BitSet bitSet = BitSet.valueOf(new long[] { ((flagBytes[0] & 0xFF) << 8) + (flagBytes[1] & 0xFF) });
        for (int index = bitSet.nextSetBit(0); index >= 0; index = bitSet.nextSetBit(index + 1)) {
            this.getFlagType(index).ifPresent(
                    configurationFlagType -> flags.add(new ConfigurationFlagDto(configurationFlagType, true)));
        }
        return flags;
    }

    abstract Optional<ConfigurationFlagTypeDto> getFlagType(final int bitPosition);

}
