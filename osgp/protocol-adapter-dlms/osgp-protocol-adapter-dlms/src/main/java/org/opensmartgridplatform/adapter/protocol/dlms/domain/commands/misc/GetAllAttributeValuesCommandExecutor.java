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
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsDataDecoder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
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
  /* 0 is the index of the class number */
  private static final int CLASS_ID_INDEX = 0;
  /* 2 is index of the obis code */
  private static final int OBIS_CODE_INDEX = 2;
  /* 3 is the index of the attributes */
  private static final int ATTR_INDEX = 3;
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
    final List<DataObject> objectListElements = objectList.getValue();

    final List<ClassIdObisAttr> allObisCodes = this.getAllObisCodes(objectListElements);
    this.logAllObisCodes(allObisCodes);

    try {
      final String output = this.createOutput(device, conn, allObisCodes);
      LOGGER.debug("Total output is: {}", output);
      return output;
    } catch (final IOException e) {
      throw new ConnectionException(e);
    }
  }

  private void logAllObisCodes(final List<ClassIdObisAttr> allObisCodes) {
    int index = 1;
    LOGGER.debug("List of all ObisCodes:");
    for (final ClassIdObisAttr obisAttr : allObisCodes) {
      LOGGER.debug(
          "{}/{} {} #attr{}",
          index++,
          allObisCodes.size(),
          obisAttr.getObisCode().getValue(),
          obisAttr.getNoAttr());
    }
  }

  private String createOutput(
      final DlmsDevice device,
      final DlmsConnectionManager conn,
      final List<ClassIdObisAttr> allObisCodes)
      throws ProtocolAdapterException, IOException {

    final DlmsProfile dlmsProfile = this.getDlmsProfile(device);

    final List<CosemObject> meterContents = new ArrayList<>();

    int index = 1;
    for (final ClassIdObisAttr obisAttr : allObisCodes) {
      LOGGER.debug(
          "Creating output for {} {}/{}",
          obisAttr.getObisCode().getValue(),
          index++,
          allObisCodes.size());
      meterContents.add(this.getAllDataFromObisCode(dlmsProfile, conn, obisAttr));
    }

    final ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(Include.NON_NULL);
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

    return objectMapper.writeValueAsString(meterContents);
  }

  private CosemObject getAllDataFromObisCode(
      final DlmsProfile dlmsProfile,
      final DlmsConnectionManager conn,
      final ClassIdObisAttr obisAttr)
      throws ProtocolAdapterException, IOException {

    final List<DataObject> attributeData = new ArrayList<>();

    final int noOfAttr = obisAttr.getNoAttr();
    for (int attributeValue = 1; attributeValue <= noOfAttr; attributeValue++) {
      LOGGER.debug(
          "Creating output for {} attr: {}/{}",
          obisAttr.getObisCode().getValue(),
          attributeValue,
          noOfAttr);
      attributeData.add(
          this.getAllDataFromAttribute(
              conn, obisAttr.getClassNumber(), obisAttr.getObisCode(), attributeValue));
    }

    return this.dlmsDataDecoder.decodeObjectData(
        obisAttr.classNumber,
        this.getObisCode(obisAttr.obisCode),
        noOfAttr,
        attributeData,
        dlmsProfile);
  }

  private DataObject getAllDataFromAttribute(
      final DlmsConnectionManager conn,
      final int classNumber,
      final DataObject obisCode,
      final int attributeValue)
      throws ProtocolAdapterException, IOException {

    if (!obisCode.isByteArray()) {
      this.throwUnexpectedTypeProtocolAdapterException();
    }

    final byte[] obisCodeByteArray = obisCode.getValue();
    if (obisCodeByteArray.length != OBIS_CODE_BYTE_ARRAY_LENGTH) {
      this.throwUnexpectedTypeProtocolAdapterException();
    }
    final AttributeAddress attributeAddress =
        new AttributeAddress(classNumber, new ObisCode(obisCodeByteArray), attributeValue);

    conn.getDlmsMessageListener()
        .setDescription(
            "RetrieveAllAttributeValues, retrieve attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(attributeAddress));

    LOGGER.debug(
        "Retrieving configuration objects data for class id: {}, obis code: {}, attribute id: {}",
        classNumber,
        obisCodeByteArray,
        attributeValue);
    final GetResult getResult = conn.getConnection().get(attributeAddress);

    LOGGER.debug("ResultCode: {}", getResult.getResultCode());

    return getResult.getResultData();
  }

  private List<ClassIdObisAttr> getAllObisCodes(final List<DataObject> objectListElements)
      throws ProtocolAdapterException {
    final List<ClassIdObisAttr> allObisCodes = new ArrayList<>();

    for (final DataObject objectListElement : objectListElements) {
      final List<DataObject> objectListElementValues = objectListElement.getValue();
      final ClassIdObisAttr classIdObisAttr =
          new ClassIdObisAttr(
              this.getClassId(objectListElementValues.get(CLASS_ID_INDEX)),
              objectListElementValues.get(OBIS_CODE_INDEX),
              this.getNoOffAttributes(objectListElementValues));

      allObisCodes.add(classIdObisAttr);
    }
    return allObisCodes;
  }

  private int getClassId(final DataObject dataObject) throws ProtocolAdapterException {
    if (DataObject.Type.LONG_UNSIGNED != dataObject.getType()) {
      this.throwUnexpectedTypeProtocolAdapterException();
    }
    final Number number = dataObject.getValue();
    return number.intValue();
  }

  private void throwUnexpectedTypeProtocolAdapterException() throws ProtocolAdapterException {
    throw new ProtocolAdapterException("Unexpected type of element");
  }

  private int getNoOffAttributes(final List<DataObject> objectListElementValues)
      throws ProtocolAdapterException {
    final DataObject accessRights = objectListElementValues.get(ATTR_INDEX);
    if (!accessRights.isComplex()) {
      this.throwUnexpectedTypeProtocolAdapterException();
    }
    final List<DataObject> accessRightsValues = accessRights.getValue();
    final DataObject attributeAccess = accessRightsValues.get(0);
    if (!attributeAccess.isComplex()) {
      this.throwUnexpectedTypeProtocolAdapterException();
    }
    final List<DataObject> attributeAccessDescriptors = attributeAccess.getValue();
    return attributeAccessDescriptors.size();
  }

  private class ClassIdObisAttr {
    private final int classNumber;
    private final DataObject obisCode;
    private final int noAttr;

    public ClassIdObisAttr(final int classNumber, final DataObject obisCode, final int noAttr) {
      this.classNumber = classNumber;
      this.obisCode = obisCode;
      this.noAttr = noAttr;
    }

    public int getClassNumber() {
      return this.classNumber;
    }

    public DataObject getObisCode() {
      return this.obisCode;
    }

    public int getNoAttr() {
      return this.noAttr;
    }
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
