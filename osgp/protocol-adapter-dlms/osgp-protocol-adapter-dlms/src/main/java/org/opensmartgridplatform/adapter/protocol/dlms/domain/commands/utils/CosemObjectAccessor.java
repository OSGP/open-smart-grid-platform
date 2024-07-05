// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

import java.io.IOException;
import java.util.Optional;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.MethodParameter;
import org.openmuc.jdlms.MethodResult;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.NotSupportedByProtocolException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeClass;
import org.opensmartgridplatform.dlms.interfaceclass.method.MethodClass;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;

public class CosemObjectAccessor {

  private static final String EXCEPTION_MSG_ACCESS_RESULT_NOT_SUCCESS =
      "Access result was not successful (%s) while "
          + "writing attribute %s, classId %s, obisCode %s.";
  private static final String EXCEPTION_MSG_WRITING_ATTRIBUTE =
      "An exception occurred while writing attribute %s, " + "classId %s, obisCode %s.";
  private static final String EXCEPTION_MSG_NO_METHOD_RESULT = "No MethodResult received.";
  private static final String EXCEPTION_MSG_NO_GET_RESULT =
      "No GetResult received while retrieving attribute %s, " + "classId %s, obisCode %s.";

  private final DlmsConnectionManager connector;
  private final ObjectConfigServiceHelper objectConfigServiceHelper;
  private final Protocol protocol;
  private final DlmsObjectType dlmsObjectType;

  private final String obisCode;
  private final int classId;

  public CosemObjectAccessor(
      final DlmsConnectionManager connector,
      final ObjectConfigServiceHelper objectConfigServiceHelper,
      final DlmsObjectType dlmsObjectType,
      final Protocol protocol)
      throws NotSupportedByProtocolException {
    this(connector, objectConfigServiceHelper, dlmsObjectType, protocol, null);
  }

  public CosemObjectAccessor(
      final DlmsConnectionManager connector,
      final ObjectConfigServiceHelper objectConfigServiceHelper,
      final DlmsObjectType dlmsObjectType,
      final Protocol protocol,
      final Short channel)
      throws NotSupportedByProtocolException {

    this.connector = connector;
    this.objectConfigServiceHelper = objectConfigServiceHelper;
    this.dlmsObjectType = dlmsObjectType;
    this.protocol = protocol;

    final Optional<CosemObject> optionalCosemObject =
        this.objectConfigServiceHelper.getOptionalCosemObject(
            this.protocol.getName(), this.protocol.getVersion(), this.dlmsObjectType);
    if (optionalCosemObject.isEmpty()) {
      throw new NotSupportedByProtocolException(
          String.format(
              "No address found for %s in protocol %s %s",
              dlmsObjectType.name(), protocol.getName(), protocol.getVersion()));
    }
    CosemObject cosemObject = optionalCosemObject.get();
    cosemObject = setChannel(cosemObject, channel);
    this.obisCode = cosemObject.getObis();
    this.classId = cosemObject.getClassId();
  }

  private static CosemObject setChannel(CosemObject cosemObject, final Short channel) {
    if (channel != null) {
      cosemObject =
          cosemObject.copyWithNewObis(cosemObject.getObis().replace("x", channel.toString()));
    }
    return cosemObject;
  }

  public DataObject readAttribute(final AttributeClass attributeClass)
      throws ProtocolAdapterException {
    final GetResult getResult;
    try {
      getResult = this.connector.getConnection().get(this.createAttributeAddress(attributeClass));
    } catch (final IOException e) {
      throw new ConnectionException(e);
    }

    if (getResult == null) {
      throw new ProtocolAdapterException(
          String.format(EXCEPTION_MSG_NO_GET_RESULT, attributeClass, this.classId, this.obisCode));
    }

    return getResult.getResultData();
  }

  public void writeAttribute(final AttributeClass attributeClass, final DataObject data)
      throws ProtocolAdapterException {
    final AttributeAddress attributeAddress = this.createAttributeAddress(attributeClass);
    final SetParameter setParameter = new SetParameter(attributeAddress, data);

    final AccessResultCode accessResultCode;
    try {
      accessResultCode = this.connector.getConnection().set(setParameter);
    } catch (final IOException e) {
      throw new ProtocolAdapterException(
          String.format(
              EXCEPTION_MSG_WRITING_ATTRIBUTE, attributeClass, this.classId, this.obisCode),
          e);
    }

    if (accessResultCode != AccessResultCode.SUCCESS) {
      throw new ProtocolAdapterException(
          String.format(
              EXCEPTION_MSG_ACCESS_RESULT_NOT_SUCCESS,
              accessResultCode.name(),
              attributeClass,
              this.classId,
              this.obisCode));
    }
  }

  public MethodResultCode callMethod(final MethodClass methodClass)
      throws ProtocolAdapterException {
    final MethodParameter methodParameter = this.createMethodParameter(methodClass);
    return this.handleMethod(methodParameter);
  }

  public MethodResultCode callMethod(final MethodClass methodClass, final DataObject dataObject)
      throws ProtocolAdapterException {
    final MethodParameter methodParameter = this.createMethodParameter(methodClass, dataObject);
    return this.handleMethod(methodParameter);
  }

  public AttributeAddress createAttributeAddress(final AttributeClass attributeClass) {
    final Optional<AttributeAddress> optionalAttributeAddress =
        this.objectConfigServiceHelper.findOptionalAttributeAddress(
            this.protocol, this.dlmsObjectType, 1, attributeClass.attributeId());
    return optionalAttributeAddress.orElseThrow();
  }

  public MethodParameter createMethodParameter(
      final MethodClass methodClass, final DataObject dataObject) {
    return new MethodParameter(this.classId, this.obisCode, methodClass.getMethodId(), dataObject);
  }

  public MethodParameter createMethodParameter(final MethodClass methodClass) {
    return new MethodParameter(this.classId, this.obisCode, methodClass.getMethodId());
  }

  private MethodResultCode handleMethod(final MethodParameter methodParameter)
      throws ProtocolAdapterException {
    final MethodResult result;
    try {
      result = this.connector.getConnection().action(methodParameter);
    } catch (final IOException e) {
      throw new ConnectionException(e);
    }

    if (result == null) {
      throw new ProtocolAdapterException(EXCEPTION_MSG_NO_METHOD_RESULT);
    }

    return result.getResultCode();
  }
}
