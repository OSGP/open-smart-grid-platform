// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static org.openmuc.jdlms.datatypes.DataObject.Type.ARRAY;
import static org.openmuc.jdlms.datatypes.DataObject.Type.LONG_UNSIGNED;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.THD_CURRENT_OVER_LIMIT_COUNTER_L1;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.THD_CURRENT_OVER_LIMIT_COUNTER_L2;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.THD_CURRENT_OVER_LIMIT_COUNTER_L3;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.THD_INSTANTANEOUS_CURRENT_FINGERPRINT_L1;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.THD_INSTANTANEOUS_CURRENT_FINGERPRINT_L2;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.THD_INSTANTANEOUS_CURRENT_FINGERPRINT_L3;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.THD_INSTANTANEOUS_CURRENT_L1;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.THD_INSTANTANEOUS_CURRENT_L2;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.THD_INSTANTANEOUS_CURRENT_L3;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.NotSupportedByProtocolException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.objectconfig.MeterType;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetThdFingerprintRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetThdFingerprintResponseDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.stereotype.Component;

@Component()
public class GetThdFingerprintCommandExecutor
    extends AbstractCommandExecutor<Void, GetThdFingerprintResponseDto> {

  private static final int VALUE_ATTRIBUTE_ID = 2;

  private final List<DlmsObjectType> dlmsObjectTypes =
      List.of(
          THD_INSTANTANEOUS_CURRENT_L1,
          THD_INSTANTANEOUS_CURRENT_L2,
          THD_INSTANTANEOUS_CURRENT_L3,
          THD_INSTANTANEOUS_CURRENT_FINGERPRINT_L1,
          THD_INSTANTANEOUS_CURRENT_FINGERPRINT_L2,
          THD_INSTANTANEOUS_CURRENT_FINGERPRINT_L3,
          THD_CURRENT_OVER_LIMIT_COUNTER_L1,
          THD_CURRENT_OVER_LIMIT_COUNTER_L2,
          THD_CURRENT_OVER_LIMIT_COUNTER_L3);

  private final ObjectConfigService objectConfigService;

  private final DlmsHelper dlmsHelper;

  public GetThdFingerprintCommandExecutor(
      final ObjectConfigService objectConfigService, final DlmsHelper dlmsHelper) {
    super(GetThdFingerprintRequestDataDto.class);

    this.objectConfigService = objectConfigService;
    this.dlmsHelper = dlmsHelper;
  }

  @Override
  public Void fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {

    this.checkActionRequestType(bundleInput);

    return null;
  }

  @Override
  public ActionResponseDto asBundleResponse(final GetThdFingerprintResponseDto executionResult)
      throws ProtocolAdapterException {

    return executionResult;
  }

  @Override
  public GetThdFingerprintResponseDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Void useless,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    final List<CosemObject> cosemObjects = this.getCosemObjectsForPhase(device);

    final AttributeAddress[] attributeAddresses =
        cosemObjects.stream().map(this::getAttributeAddress).toArray(AttributeAddress[]::new);

    conn.getDlmsMessageListener()
        .setDescription(
            "GetThdFingerprint, retrieve attributes: "
                + JdlmsObjectToStringUtil.describeAttributes(attributeAddresses));

    final List<GetResult> results = this.dlmsHelper.getWithList(conn, device, attributeAddresses);

    final Map<DlmsObjectType, DataObject> resultMap = new EnumMap<>(DlmsObjectType.class);
    for (int i = 0; i < cosemObjects.size(); i++) {
      resultMap.put(
          DlmsObjectType.valueOf(cosemObjects.get(i).getTag()), this.getResultData(results, i));
    }
    return new GetThdFingerprintResponseDto(
        this.readInt(resultMap.getOrDefault(THD_INSTANTANEOUS_CURRENT_L1, null)),
        this.readInt(resultMap.getOrDefault(THD_INSTANTANEOUS_CURRENT_L2, null)),
        this.readInt(resultMap.getOrDefault(THD_INSTANTANEOUS_CURRENT_L3, null)),
        this.readList(resultMap.getOrDefault(THD_INSTANTANEOUS_CURRENT_FINGERPRINT_L1, null)),
        this.readList(resultMap.getOrDefault(THD_INSTANTANEOUS_CURRENT_FINGERPRINT_L2, null)),
        this.readList(resultMap.getOrDefault(THD_INSTANTANEOUS_CURRENT_FINGERPRINT_L3, null)),
        this.readInt(resultMap.getOrDefault(THD_CURRENT_OVER_LIMIT_COUNTER_L1, null)),
        this.readInt(resultMap.getOrDefault(THD_CURRENT_OVER_LIMIT_COUNTER_L2, null)),
        this.readInt(resultMap.getOrDefault(THD_CURRENT_OVER_LIMIT_COUNTER_L3, null)));
  }

  private DataObject getResultData(final List<GetResult> results, final int i)
      throws ProtocolAdapterException {
    final AccessResultCode resultCode = results.get(i).getResultCode();
    if (resultCode != AccessResultCode.SUCCESS) {
      throw new ProtocolAdapterException(
          String.format(
              "No success retrieving %s: AccessResultCode = %s",
              this.dlmsObjectTypes.get(i).name(), resultCode.toString()));
    }
    return results.get(i).getResultData();
  }

  private Integer readInt(final DataObject dataObject) throws ProtocolAdapterException {
    if (dataObject == null) {
      return null;
    }
    final Type type = dataObject.getType();
    final String description = dataObject.toString();
    if (type != LONG_UNSIGNED) {
      throw new ProtocolAdapterException("Unexpected data type from Thd Fingerprint: " + type);
    }
    return this.dlmsHelper.readInteger(dataObject, description);
  }

  @SuppressWarnings("java:S1168")
  private List<Integer> readList(final DataObject dataObject) throws ProtocolAdapterException {
    if (dataObject == null) {
      // we explicitly want to set the attribute in GetThdFingerprintResponseDto
      // to null and not empty list
      return null;
    }
    final Type type = dataObject.getType();
    if (type != ARRAY) {
      throw new ProtocolAdapterException("Unexpected data type from Thd Fingerprint: " + type);
    }
    return this.getFingerprintValues(dataObject);
  }

  private List<CosemObject> getCosemObjectsForPhase(final DlmsDevice device)
      throws ProtocolAdapterException {

    final Predicate<CosemObject> forPhase =
        cosemObject ->
            cosemObject
                .getMeterTypes()
                .contains(device.isPolyphase() ? MeterType.PP : MeterType.SP);

    final List<CosemObject> cosemObjects;
    try {
      cosemObjects =
          this.objectConfigService.getCosemObjectsIgnoringMissingTypes(
              device.getProtocolName(), device.getProtocolVersion(), this.dlmsObjectTypes);
    } catch (final ObjectConfigException e) {
      throw new ProtocolAdapterException("Error reading object configuration", e);
    }
    final List<CosemObject> cosemObjectsForPhase = cosemObjects.stream().filter(forPhase).toList();
    if (cosemObjectsForPhase.isEmpty()) {
      this.handleNoAttributeAddresses(device);
    }
    return cosemObjectsForPhase;
  }

  private List<Integer> getFingerprintValues(final DataObject dataObject)
      throws ProtocolAdapterException {
    if (!dataObject.getType().equals(ARRAY)) {
      throw new ProtocolAdapterException("Expected array for fingerprint values");
    }
    final List<DataObject> dataObjects = dataObject.getValue();

    final List<Integer> fingerprint = new ArrayList<>();

    for (final DataObject dataObject1 : dataObjects) {
      fingerprint.add(this.dlmsHelper.readInteger(dataObject1, "Read THD fingerprint value"));
    }

    return fingerprint;
  }

  private AttributeAddress getAttributeAddress(final CosemObject cosemObject) {
    return new AttributeAddress(
        cosemObject.getClassId(), cosemObject.getObis(), VALUE_ATTRIBUTE_ID);
  }

  private void handleNoAttributeAddresses(final DlmsDevice device)
      throws NotSupportedByProtocolException {
    final Protocol protocol = Protocol.forDevice(device);
    throw new NotSupportedByProtocolException(
        String.format(
            "No address found for protocol %s %s in list of optional types %s",
            protocol.getName(), protocol.getVersion(), this.dlmsObjectTypes));
  }
}
