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
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.AttributeAccessItem;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsDataDecoder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectListElement;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.objectconfig.AccessType;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsProfile;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemObisCodeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetAllAttributeValuesRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetAllAttributeValuesCommandExecutor
    extends AbstractCommandExecutor<DataObject, String> {

  private static final int OBIS_CODE_BYTE_ARRAY_LENGTH = 6;
  private static final int CLASS_ID_INDEX = 0;
  private static final int VERSION_INDEX = 1;
  private static final int OBIS_CODE_INDEX = 2;
  private static final int ATTR_INDEX = 3;
  private static final int ATTR_ID_INDEX = 0;
  private static final int ACCESS_MODE_INDEX = 1;
  private static final int CLASS_ID = 15;
  private static final ObisCode OBIS_CODE = new ObisCode("0.0.40.0.0.255");
  private static final int ATTRIBUTE_ID = 2;

  @Autowired private DlmsHelper dlmsHelper;

  @Autowired private DlmsDataDecoder dlmsDataDecoder;

  @Autowired private ObjectConfigService objectConfigService;

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetAllAttributeValuesCommandExecutor.class);

  public GetAllAttributeValuesCommandExecutor() {
    super(GetAllAttributeValuesRequestDto.class);
  }

  @Override
  public DataObject fromBundleRequestInput(final ActionRequestDto bundleInput)
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
      final DataObject object,
      final MessageMetadata messageMetadata)
      throws OsgpException {

    final AttributeAddress attributeAddress =
        new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);

    conn.getDlmsMessageListener()
        .setDescription(
            "RetrieveAllAttributeValues, retrieve attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(attributeAddress));

    LOGGER.debug(
        "Retrieving all attribute values for class id: {}, obis code: {}, attribute id: {}",
        CLASS_ID,
        OBIS_CODE,
        ATTRIBUTE_ID);

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
      LOGGER.debug("Total output is: {}", output);
      return output;
    } catch (final IOException e) {
      throw new ConnectionException(e);
    }
  }

  private void logAllObisCodes(final List<ObjectListElement> objectListElements) {
    int index = 1;
    LOGGER.debug("List of all ObisCodes:");
    for (final ObjectListElement element : objectListElements) {
      LOGGER.debug(
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
      throws ProtocolAdapterException, IOException {

    final DlmsProfile dlmsProfile = this.getDlmsProfile(device);

    final List<CosemObject> meterContents = new ArrayList<>();

    int index = 1;
    for (final ObjectListElement element : objectListElements) {
      LOGGER.debug(
          "Creating output for {} {}/{}",
          element.getLogicalName(),
          index++,
          objectListElements.size());
      meterContents.add(this.getAllDataFromObisCode(dlmsProfile, conn, element));
    }

    final ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(Include.NON_NULL);
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

    return objectMapper.writeValueAsString(meterContents);
  }

  private CosemObject getAllDataFromObisCode(
      final DlmsProfile dlmsProfile,
      final DlmsConnectionManager conn,
      final ObjectListElement objectListElement)
      throws ProtocolAdapterException, IOException {

    final List<DataObject> attributeData = new ArrayList<>();

    final int noOfAttr = objectListElement.getAttributes().size();
    for (int attributeValue = 1; attributeValue <= noOfAttr; attributeValue++) {
      LOGGER.debug(
          "Creating output for {} attr: {}/{}",
          objectListElement.getLogicalName(),
          attributeValue,
          noOfAttr);
      attributeData.add(
          this.getAllDataFromAttribute(
              conn,
              objectListElement.getClassId(),
              objectListElement.getLogicalName(),
              attributeValue));
    }

    return this.dlmsDataDecoder.decodeObjectData(objectListElement, attributeData, dlmsProfile);
  }

  private DataObject getAllDataFromAttribute(
      final DlmsConnectionManager conn,
      final int classNumber,
      final String obisCode,
      final int attributeValue)
      throws IOException {

    final AttributeAddress attributeAddress =
        new AttributeAddress(classNumber, new ObisCode(obisCode), attributeValue);

    conn.getDlmsMessageListener()
        .setDescription(
            "RetrieveAllAttributeValues, retrieve attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(attributeAddress));

    LOGGER.debug(
        "Retrieving configuration objects data for class id: {}, obis code: {}, attribute id: {}",
        classNumber,
        obisCode,
        attributeValue);
    final GetResult getResult = conn.getConnection().get(attributeAddress);

    LOGGER.debug("ResultCode: {}", getResult.getResultCode());

    return getResult.getResultData();
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
