// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder.AttributeAccessItem;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder.DataDecoder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.datadecoder.ObjectListElement;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.objectconfig.AccessType;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.objectconfig.DlmsProfile;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemObisCodeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetAllAttributeValuesRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GetAllAttributeValuesCommandExecutor extends AbstractCommandExecutor<Void, String> {

  private static final int CLASS_ID_INDEX = 0;
  private static final int VERSION_INDEX = 1;
  private static final int OBIS_CODE_INDEX = 2;
  private static final int ATTR_INDEX = 3;
  private static final int ATTR_ID_INDEX = 0;
  private static final int ACCESS_MODE_INDEX = 1;
  private static final int ATTRIBUTE_ID = 2;

  private final ObjectConfigService objectConfigService;

  private final ObjectConfigServiceHelper objectConfigServiceHelper;

  private final DlmsHelper dlmsHelper;

  private final DataDecoder dataDecoder;

  @Autowired
  public GetAllAttributeValuesCommandExecutor(
      final ObjectConfigService objectConfigService,
      final ObjectConfigServiceHelper objectConfigServiceHelper,
      final DlmsHelper dlmsHelper,
      final DataDecoder dataDecoder) {
    super(GetAllAttributeValuesRequestDto.class);

    this.objectConfigService = objectConfigService;
    this.objectConfigServiceHelper = objectConfigServiceHelper;
    this.dlmsHelper = dlmsHelper;
    this.dataDecoder = dataDecoder;
  }

  @Override
  public Void fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {
    /*
     * GetAllAttributeValuesRequestDto does not contain any values to pass
     * on, and the GetAllAttributeValuesCommandExecutor takes a DataObject
     * as input that is ignored.
     */
    return null;
  }

  @Override
  public ActionResponseDto asBundleResponse(final String executionResult)
      throws ProtocolAdapterException {
    return new ActionResponseDto(executionResult);
  }

  @Override
  public String execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Void v,
      final MessageMetadata messageMetadata)
      throws OsgpException {

    final AttributeAddress attributeAddress =
        this.objectConfigServiceHelper.findAttributeAddress(
            device, Protocol.forDevice(device), DlmsObjectType.ASSOCIATION_LN, null, ATTRIBUTE_ID);

    conn.getDlmsMessageListener()
        .setDescription(
            "RetrieveAllAttributeValues, retrieve attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(attributeAddress));

    log.debug(
        "Retrieving all attribute values for class id: {}, obis code: {}, attribute id: {}",
        attributeAddress.getClassId(),
        attributeAddress.getInstanceId().asDecimalString(),
        attributeAddress.getId());

    final DataObject objectList = this.dlmsHelper.getAttributeValue(conn, attributeAddress);

    if (!objectList.isComplex()) {
      this.throwUnexpectedTypeProtocolAdapterException();
    }
    final List<DataObject> objectListElementDataObjects = objectList.getValue();

    final List<ObjectListElement> allObjectListElements =
        this.getAllObjectListElements(objectListElementDataObjects);
    this.logAllObisCodes(allObjectListElements);

    try {
      final String output = this.createOutput(device, conn, allObjectListElements);
      log.debug("Total output is: {}", output);
      return output;
    } catch (final IOException e) {
      throw new ConnectionException(e);
    }
  }

  private void logAllObisCodes(final List<ObjectListElement> objectListElements) {
    int index = 1;
    log.debug("List of all ObisCodes:");
    for (final ObjectListElement element : objectListElements) {
      log.debug(
          "{}/{} {} #attr{}",
          index++,
          objectListElements.size(),
          element.getLogicalName(),
          element.getAttributes().size());
    }
  }

  private String createOutput(
      final DlmsDevice device,
      final DlmsConnectionManager conn,
      final List<ObjectListElement> objectListElements)
      throws IOException {

    final DlmsProfile dlmsProfile = this.getDlmsProfile(device);

    final List<CosemObject> meterContents = new ArrayList<>();

    int index = 1;
    for (final ObjectListElement element : objectListElements) {
      log.debug(
          "Creating output for {} {}/{}",
          element.getLogicalName(),
          index++,
          objectListElements.size());
      meterContents.add(this.getAllDataFromObisCode(dlmsProfile, conn, device, element));
    }

    final ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(Include.NON_NULL);
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

    return objectMapper.writeValueAsString(meterContents);
  }

  private CosemObject getAllDataFromObisCode(
      final DlmsProfile dlmsProfile,
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final ObjectListElement objectListElement) {

    final List<DataObject> attributeData =
        this.getAllDataFromAttributes(conn, device, objectListElement);

    return this.dataDecoder.decodeObjectData(objectListElement, attributeData, dlmsProfile);
  }

  private List<DataObject> getAllDataFromAttributes(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final ObjectListElement objectListElement) {
    try {
      final int classId = objectListElement.getClassId();
      final String obisCode = objectListElement.getLogicalName();
      final AttributeAddress[] addresses =
          objectListElement.getAttributes().stream()
              .map(item -> new AttributeAddress(classId, obisCode, item.getAttributeId()))
              .toArray(AttributeAddress[]::new);

      conn.getDlmsMessageListener()
          .setDescription(
              "RetrieveAllAttributeValues, retrieve attributes: "
                  + JdlmsObjectToStringUtil.describeAttributes(addresses));

      log.debug(
          "Retrieving data for {} attributes of class id: {}, obis code: {}",
          addresses.length,
          classId,
          obisCode);
      final List<GetResult> getResults = this.dlmsHelper.getWithList(conn, device, addresses);

      if (getResults.stream()
          .allMatch(result -> result.getResultCode() == AccessResultCode.SUCCESS)) {
        log.debug("ResultCode: SUCCESS");
      } else {
        log.debug("ResultCode not SUCCESS for one or more attributes");
      }

      return getResults.stream().map(result -> result.getResultData()).toList();
    } catch (final Exception e) {
      log.debug("Failed reading attributes from " + objectListElement.getLogicalName(), e);
      return List.of();
    }
  }

  private List<ObjectListElement> getAllObjectListElements(
      final List<DataObject> objectListDataObjects) throws ProtocolAdapterException {
    final List<ObjectListElement> allElements = new ArrayList<>();

    for (final DataObject objectListDataObject : objectListDataObjects) {
      final List<DataObject> elementValues = objectListDataObject.getValue();
      final ObjectListElement element =
          new ObjectListElement(
              this.getClassId(elementValues.get(CLASS_ID_INDEX)),
              this.getVersion(elementValues.get(VERSION_INDEX)),
              this.getObis(elementValues.get(OBIS_CODE_INDEX)),
              this.getAttributeItems(elementValues.get(ATTR_INDEX)));

      allElements.add(element);
    }

    return allElements;
  }

  private int getClassId(final DataObject dataObject) throws ProtocolAdapterException {
    if (DataObject.Type.LONG_UNSIGNED != dataObject.getType()) {
      this.throwUnexpectedTypeProtocolAdapterException();
    }
    final Number number = dataObject.getValue();
    return number.intValue();
  }

  private int getVersion(final DataObject dataObject) throws ProtocolAdapterException {
    if (Type.UNSIGNED != dataObject.getType()) {
      this.throwUnexpectedTypeProtocolAdapterException();
    }
    final Number number = dataObject.getValue();
    return number.intValue();
  }

  private String getObis(final DataObject dataObject) throws ProtocolAdapterException {
    if (Type.OCTET_STRING != dataObject.getType()) {
      this.throwUnexpectedTypeProtocolAdapterException();
    }
    return this.getObisCode(dataObject);
  }

  private List<AttributeAccessItem> getAttributeItems(final DataObject dataObject)
      throws ProtocolAdapterException {
    if (!dataObject.isComplex()) {
      this.throwUnexpectedTypeProtocolAdapterException();
    }
    final List<DataObject> accessRightsValues = dataObject.getValue();
    final DataObject attributeAccess = accessRightsValues.get(0);
    if (!attributeAccess.isComplex()) {
      this.throwUnexpectedTypeProtocolAdapterException();
    }

    final List<DataObject> descriptors = attributeAccess.getValue();

    final List<AttributeAccessItem> attributeAccessItems = new ArrayList<>();

    for (final DataObject descriptor : descriptors) {
      final List<DataObject> descriptorValues = descriptor.getValue();
      attributeAccessItems.add(
          new AttributeAccessItem(
              this.getAttributeId(descriptorValues.get(ATTR_ID_INDEX)),
              this.getAccessMode(descriptorValues.get(ACCESS_MODE_INDEX))));
    }

    return attributeAccessItems;
  }

  private int getAttributeId(final DataObject dataObject) throws ProtocolAdapterException {
    if (Type.INTEGER != dataObject.getType()) {
      this.throwUnexpectedTypeProtocolAdapterException();
    }
    final Number number = dataObject.getValue();
    return number.intValue();
  }

  private AccessType getAccessMode(final DataObject dataObject) throws ProtocolAdapterException {
    if (Type.ENUMERATE != dataObject.getType()) {
      this.throwUnexpectedTypeProtocolAdapterException();
    }
    final Number number = dataObject.getValue();

    return switch (number.intValue()) {
      case 0 -> null;
      case 1 -> AccessType.R;
      case 2 -> AccessType.W;
      case 3 -> AccessType.RW;
      default -> null;
    };
  }

  private void throwUnexpectedTypeProtocolAdapterException() throws ProtocolAdapterException {
    throw new ProtocolAdapterException("Unexpected type of element");
  }

  private DlmsProfile getDlmsProfile(final DlmsDevice device) {
    final List<DlmsProfile> dlmsProfiles = this.objectConfigService.getConfiguredDlmsProfiles();
    return dlmsProfiles.stream()
        .filter(
            p ->
                p.getProfile().equals(device.getProtocolName())
                    && p.getVersion().equals(device.getProtocolVersion()))
        .toList()
        .get(0);
  }

  private String getObisCode(final DataObject obis) throws ProtocolAdapterException {
    final CosemObisCodeDto cosemObisCodeDto = this.dlmsHelper.readLogicalName(obis, "");
    return new ObisCode(cosemObisCodeDto.toByteArray()).asDecimalString();
  }
}
