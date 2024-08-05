// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

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

    final Map<DlmsObjectType, Object> resultMap = new EnumMap<>(DlmsObjectType.class);
    for (int i = 0; i < cosemObjects.size(); i++) {
      resultMap.put(
          DlmsObjectType.valueOf(cosemObjects.get(i).getTag()), this.readResult(results, i));
    }
    return new GetThdFingerprintResponseDto(
        resultMap.getOrDefault(THD_INSTANTANEOUS_CURRENT_L1, null),
        resultMap.getOrDefault(THD_INSTANTANEOUS_CURRENT_L2, null),
        resultMap.getOrDefault(THD_INSTANTANEOUS_CURRENT_L3, null),
        resultMap.getOrDefault(THD_INSTANTANEOUS_CURRENT_FINGERPRINT_L1, null),
        resultMap.getOrDefault(THD_INSTANTANEOUS_CURRENT_FINGERPRINT_L2, null),
        resultMap.getOrDefault(THD_INSTANTANEOUS_CURRENT_FINGERPRINT_L3, null),
        resultMap.getOrDefault(THD_CURRENT_OVER_LIMIT_COUNTER_L1, null),
        resultMap.getOrDefault(THD_CURRENT_OVER_LIMIT_COUNTER_L2, null),
        resultMap.getOrDefault(THD_CURRENT_OVER_LIMIT_COUNTER_L3, null));
  }

  private Object readResult(final List<GetResult> results, final int idx)
      throws ProtocolAdapterException {
    final Type type = results.get(idx).getResultData().getType();
    final String description = results.get(idx).getResultData().toString();
    switch (type) {
      case LONG_UNSIGNED -> {
        return this.dlmsHelper.readInteger(results.get(idx), description);
      }
      case ARRAY -> {
        return this.getFingerprintValues(results.get(idx), description);
      }
      default ->
          throw new ProtocolAdapterException("Unexpected data type from Thd Fingerprint: " + type);
    }
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

  private List<Integer> getFingerprintValues(final GetResult getResult, final String description)
      throws ProtocolAdapterException {
    final AccessResultCode resultCode = getResult.getResultCode();
    if (resultCode != AccessResultCode.SUCCESS) {
      throw new ProtocolAdapterException(
          "No success retrieving " + description + ": AccessResultCode = " + resultCode);
    }

    final DataObject resultData = getResult.getResultData();
    if (!resultData.getType().equals(Type.ARRAY)) {
      throw new ProtocolAdapterException("Expected array for fingerprint values");
    }
    final List<DataObject> dataObjects = resultData.getValue();

    final List<Integer> fingerprint = new ArrayList<>();

    for (final DataObject dataObject : dataObjects) {
      fingerprint.add(this.dlmsHelper.readInteger(dataObject, "Read THD fingerprint value"));
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
