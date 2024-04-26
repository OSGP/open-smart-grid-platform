// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import java.util.ArrayList;
import java.util.List;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.NotSupportedByProtocolException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetAdministrativeStatusDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetThdFingerprintResponseDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component()
public class GetThdFingerprintCommandExecutor
    extends AbstractCommandExecutor<Void, GetThdFingerprintResponseDto> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetThdFingerprintCommandExecutor.class);

  private final ObjectConfigServiceHelper objectConfigServiceHelper;

  private final DlmsHelper dlmsHelper;

  public GetThdFingerprintCommandExecutor(
      final ObjectConfigServiceHelper objectConfigServiceHelper, final DlmsHelper dlmsHelper) {
    super(GetAdministrativeStatusDataDto.class);

    this.objectConfigServiceHelper = objectConfigServiceHelper;
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

    final Protocol protocol = Protocol.forDevice(device);

    final AttributeAddress addressCurrentL1 =
        this.getAttributeAddress(DlmsObjectType.THD_INSTANTANEOUS_CURRENT_L1, protocol);
    final AttributeAddress addressCurrentL2 =
        this.getAttributeAddress(DlmsObjectType.THD_INSTANTANEOUS_CURRENT_L2, protocol);
    final AttributeAddress addressCurrentL3 =
        this.getAttributeAddress(DlmsObjectType.THD_INSTANTANEOUS_CURRENT_L3, protocol);
    final AttributeAddress addressCurrentFingerprintL1 =
        this.getAttributeAddress(DlmsObjectType.THD_INSTANTANEOUS_CURRENT_FINGERPRINT_L1, protocol);
    final AttributeAddress addressCurrentFingerprintL2 =
        this.getAttributeAddress(DlmsObjectType.THD_INSTANTANEOUS_CURRENT_FINGERPRINT_L2, protocol);
    final AttributeAddress addressCurrentFingerprintL3 =
        this.getAttributeAddress(DlmsObjectType.THD_INSTANTANEOUS_CURRENT_FINGERPRINT_L3, protocol);
    final AttributeAddress addressOverLimitCounterL1 =
        this.getAttributeAddress(DlmsObjectType.THD_CURRENT_OVER_LIMIT_COUNTER_L1, protocol);
    final AttributeAddress addressOverLimitCounterL2 =
        this.getAttributeAddress(DlmsObjectType.THD_CURRENT_OVER_LIMIT_COUNTER_L2, protocol);
    final AttributeAddress addressOverLimitCounterL3 =
        this.getAttributeAddress(DlmsObjectType.THD_CURRENT_OVER_LIMIT_COUNTER_L3, protocol);

    conn.getDlmsMessageListener()
        .setDescription(
            "GetAdministrativeStatus, retrieve attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(
                    addressCurrentL1,
                    addressCurrentL2,
                    addressCurrentL3,
                    addressCurrentFingerprintL1,
                    addressCurrentFingerprintL2,
                    addressCurrentFingerprintL3,
                    addressOverLimitCounterL1,
                    addressOverLimitCounterL2,
                    addressOverLimitCounterL3));

    LOGGER.debug("Retrieving THD fingerprint");

    final List<GetResult> results =
        this.dlmsHelper.getWithList(
            conn,
            device,
            addressCurrentL1,
            addressCurrentL2,
            addressCurrentL3,
            addressCurrentFingerprintL1,
            addressCurrentFingerprintL2,
            addressCurrentFingerprintL3,
            addressOverLimitCounterL1,
            addressOverLimitCounterL2,
            addressOverLimitCounterL3);

    final int currentL1 = this.dlmsHelper.readInteger(results.get(0), "Read current THD L1");
    final int currentL2 = this.dlmsHelper.readInteger(results.get(1), "Read current THD L2");
    final int currentL3 = this.dlmsHelper.readInteger(results.get(2), "Read current THD L3");
    final List<Integer> fingerprintL1 =
        this.getFingerprintValues(results.get(3), "Read fingerprint value L1");
    final List<Integer> fingerprintL2 =
        this.getFingerprintValues(results.get(4), "Read fingerprint value L2");
    final List<Integer> fingerprintL3 =
        this.getFingerprintValues(results.get(5), "Read fingerprint value L3");
    final int overLimitCounterL1 =
        this.dlmsHelper.readInteger(results.get(6), "Read THD over limit counter L1");
    final int overLimitCounterL2 =
        this.dlmsHelper.readInteger(results.get(7), "Read THD over limit counter L2");
    final int overLimitCounterL3 =
        this.dlmsHelper.readInteger(results.get(8), "Read THD over limit counter L3");

    return new GetThdFingerprintResponseDto(
        currentL1,
        currentL2,
        currentL3,
        fingerprintL1,
        fingerprintL2,
        fingerprintL3,
        overLimitCounterL1,
        overLimitCounterL2,
        overLimitCounterL3);
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

  private AttributeAddress getAttributeAddress(
      final DlmsObjectType dlmsObjectType, final Protocol protocol)
      throws NotSupportedByProtocolException {
    return this.objectConfigServiceHelper
        .findOptionalDefaultAttributeAddress(protocol, dlmsObjectType)
        .orElseThrow(
            () ->
                new NotSupportedByProtocolException(
                    String.format(
                        "No address found for %s in protocol %s %s",
                        dlmsObjectType.name(), protocol.getName(), protocol.getVersion())));
  }
}
