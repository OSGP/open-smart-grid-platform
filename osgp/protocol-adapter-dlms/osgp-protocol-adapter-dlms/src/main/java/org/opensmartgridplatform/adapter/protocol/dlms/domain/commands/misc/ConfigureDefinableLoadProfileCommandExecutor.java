// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static org.opensmartgridplatform.dlms.interfaceclass.attribute.ProfileGenericAttribute.CAPTURE_OBJECTS;
import static org.opensmartgridplatform.dlms.interfaceclass.attribute.ProfileGenericAttribute.CAPTURE_PERIOD;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.DEFINABLE_LOAD_PROFILE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ma.glasnost.orika.MapperFacade;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.NotSupportedByProtocolException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ClockAttribute;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CaptureObjectDefinitionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DefinableLoadProfileConfigurationDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.stereotype.Component;

@Component
public class ConfigureDefinableLoadProfileCommandExecutor
    extends AbstractCommandExecutor<DefinableLoadProfileConfigurationDto, Void> {

  private final MapperFacade configurationMapper;
  private final ObjectConfigServiceHelper objectConfigServiceHelper;

  public ConfigureDefinableLoadProfileCommandExecutor(
      final MapperFacade configurationMapper,
      final ObjectConfigServiceHelper objectConfigServiceHelper) {
    super(DefinableLoadProfileConfigurationDto.class);
    this.configurationMapper = configurationMapper;
    this.objectConfigServiceHelper = objectConfigServiceHelper;
  }

  @Override
  public ActionResponseDto asBundleResponse(final Void executionResult)
      throws ProtocolAdapterException {
    // Always successful, otherwise a ProtocolAdapterException was thrown before.
    return new ActionResponseDto("Configure definable load profile was successful");
  }

  @Override
  public Void execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final DefinableLoadProfileConfigurationDto definableLoadProfileConfiguration,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    if (definableLoadProfileConfiguration.hasCaptureObjects()) {
      this.writeCaptureObjects(device, conn, definableLoadProfileConfiguration.getCaptureObjects());
    }

    if (definableLoadProfileConfiguration.hasCapturePeriod()) {
      this.writeCapturePeriod(device, conn, definableLoadProfileConfiguration.getCapturePeriod());
    }

    return null;
  }

  private void writeCaptureObjects(
      final DlmsDevice device,
      final DlmsConnectionManager conn,
      final List<CaptureObjectDefinitionDto> captureObjects)
      throws ProtocolAdapterException {
    this.writeAttribute(
        conn,
        new SetParameter(
            this.getAttributeAddress(device, DEFINABLE_LOAD_PROFILE, CAPTURE_OBJECTS.attributeId()),
            this.mapCaptureObjects(captureObjects, device)),
        "capture objects");
  }

  private DataObject mapCaptureObjects(
      final List<CaptureObjectDefinitionDto> captureObjects, final DlmsDevice device)
      throws NotSupportedByProtocolException {
    final List<DataObject> captureObjectsArray = new ArrayList<>();
    // Always make sure the capture object definition of the clock time is included as first object
    // in the list, and that it is included only once.
    final AttributeAddress clockAddress =
        this.getAttributeAddress(device, DlmsObjectType.CLOCK, ClockAttribute.TIME.attributeId());

    captureObjectsArray.add(this.getClockDefinition(clockAddress));
    for (final CaptureObjectDefinitionDto captureObject : captureObjects) {
      if (!this.isClockTimeDefinition(captureObject, clockAddress)) {
        captureObjectsArray.add(this.configurationMapper.map(captureObject, DataObject.class));
      }
    }
    return DataObject.newArrayData(captureObjectsArray);
  }

  private boolean isClockTimeDefinition(
      final CaptureObjectDefinitionDto captureObject, final AttributeAddress address) {
    return address.getClassId() == captureObject.getClassId()
        && Arrays.equals(
            address.getInstanceId().bytes(), captureObject.getLogicalName().toByteArray())
        && address.getId() == captureObject.getAttributeIndex();
  }

  private void writeCapturePeriod(
      final DlmsDevice device, final DlmsConnectionManager conn, final long capturePeriod)
      throws ProtocolAdapterException {
    this.writeAttribute(
        conn,
        new SetParameter(
            this.getAttributeAddress(device, DEFINABLE_LOAD_PROFILE, CAPTURE_PERIOD.attributeId()),
            DataObject.newUInteger32Data(capturePeriod)),
        "capture period");
  }

  private void writeAttribute(
      final DlmsConnectionManager conn, final SetParameter parameter, final String attributeName)
      throws ProtocolAdapterException {
    this.dlmsLogWrite(conn, parameter.getAttributeAddress(), attributeName);
    try {
      final AccessResultCode result = conn.getConnection().set(parameter);
      if (!result.equals(AccessResultCode.SUCCESS)) {
        throw new ProtocolAdapterException(
            String.format(
                "Attribute '%s' of the definable load profile was not set successfully. ResultCode: %s",
                attributeName, result.name()));
      }
    } catch (final IOException e) {
      throw new ConnectionException(e);
    }
  }

  private void dlmsLogWrite(
      final DlmsConnectionManager conn,
      final AttributeAddress attribute,
      final String attributeName) {
    conn.getDlmsMessageListener()
        .setDescription(
            "Writing definable load profile attribute '"
                + attributeName
                + "': "
                + JdlmsObjectToStringUtil.describeAttributes(attribute));
  }

  protected AttributeAddress getAttributeAddress(
      final DlmsDevice device, final DlmsObjectType dlmsObjectType, final int attributeId)
      throws NotSupportedByProtocolException {
    final Protocol protocol = Protocol.forDevice(device);
    return this.objectConfigServiceHelper
        .findOptionalAttributeAddress(protocol, dlmsObjectType, null, attributeId)
        .orElseThrow(
            () ->
                new NotSupportedByProtocolException(
                    String.format(
                        "No address found for %s in protocol %s %s",
                        dlmsObjectType.name(), protocol.getName(), protocol.getVersion())));
  }

  private DataObject getClockDefinition(final AttributeAddress clockAddress) {
    return DataObject.newStructureData(
        DataObject.newUInteger16Data(clockAddress.getClassId()),
        DataObject.newOctetStringData(clockAddress.getInstanceId().bytes()),
        DataObject.newInteger8Data((byte) clockAddress.getId()),
        DataObject.newUInteger16Data(0));
  }
}
