package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.BitString;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SetConfigurationObjectService implements ProtocolService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetConfigurationObjectService.class);
    private static final int NUMBER_OF_FLAG_BITS = 16;

    private final DlmsHelper dlmsHelper;

    SetConfigurationObjectService(final DlmsHelper dlmsHelper) {
        this.dlmsHelper = dlmsHelper;
    }

    public AccessResultCode setConfigurationObject(final DlmsConnectionManager conn,
            final ConfigurationObjectDto configurationToSet, final ConfigurationObjectDto configurationOnDevice) {

        final DataObject dataObject = this.buildSetParameterData(configurationToSet, configurationOnDevice);
        LOGGER.info("ConfigurationObject SetParameter Data : {}", this.dlmsHelper.getDebugInfo(dataObject));

        final AttributeAddress attributeAddress = AttributeAddressFactory.getConfigurationObjectAddress();
        final SetParameter setParameter = new SetParameter(attributeAddress, dataObject);
        conn.getDlmsMessageListener().setDescription(
                "SetConfigurationObject AttributeAddress: " + JdlmsObjectToStringUtil.describeAttributes(
                        attributeAddress));
        return this.getAccessResultCode(conn, setParameter);
    }

    private AccessResultCode getAccessResultCode(final DlmsConnectionManager conn, final SetParameter setParameter) {
        LOGGER.info("Set ConfigurationObject using SetParameter {}", setParameter);
        try {
            return conn.getConnection().set(setParameter);
        } catch (final IOException e) {
            throw new ConnectionException(e);
        }
    }

    abstract DataObject buildSetParameterData(ConfigurationObjectDto configurationToSet,
            ConfigurationObjectDto configurationOnDevice);

    BitString getFlags(final ConfigurationObjectDto configurationToSet,
            final ConfigurationObjectDto configurationOnDevice) {
        final List<ConfigurationFlagDto> flags = new ArrayList<>();
        this.addSettableFlags(configurationToSet, flags);
        this.addDeviceFlags(configurationOnDevice, flags);
        final byte[] flagBytes = this.toByteArray(flags);
        return new BitString(flagBytes, NUMBER_OF_FLAG_BITS);
    }

    private void addSettableFlags(final ConfigurationObjectDto configurationToSet,
            final List<ConfigurationFlagDto> flags) {
        configurationToSet.getConfigurationFlags().getFlags().stream().filter(
                flag -> !flag.getConfigurationFlagType().isReadOnly()).forEach(flags::add);
    }

    // If flags are missing, use the configurationOnDevice to supplement them.
    private void addDeviceFlags(final ConfigurationObjectDto configurationOnDevice,
            final List<ConfigurationFlagDto> flags) {
        configurationOnDevice.getConfigurationFlags().getFlags().forEach(flagOnDevice -> {
            if (flags.stream().noneMatch(
                    flag -> flag.getConfigurationFlagType() == flagOnDevice.getConfigurationFlagType())) {
                flags.add(flagOnDevice);
            }
        });
    }

    private byte[] toByteArray(final List<ConfigurationFlagDto> flags) {
        final BitSet bitSet = new BitSet(NUMBER_OF_FLAG_BITS);
        for (final ConfigurationFlagDto flag : flags) {
            if (flag.isEnabled()) {
                final ConfigurationFlagTypeDto configurationFlagType = flag.getConfigurationFlagType();
                bitSet.set(this.getBitPosition(configurationFlagType), true);
            }
        }
        // 16 bits is 2 bytes
        final byte[] byteArray = bitSet.toByteArray();
        // swap bytes to set MSB first
        final byte tmp = byteArray[1];
        byteArray[1] = byteArray[0];
        byteArray[0] = tmp;
        return byteArray;
    }

    abstract Integer getBitPosition(ConfigurationFlagTypeDto type);
}
