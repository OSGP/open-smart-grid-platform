// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

import java.io.IOException;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains all information to be used when setting a {@link SetParameter} to the device. Further
 * the execute method, which needs a live {@link DlmsConnection}, will do the actual call to the
 * device.
 */
public class DataObjectAttrExecutor {
  private static final Logger LOGGER = LoggerFactory.getLogger(DataObjectAttrExecutor.class);

  private final String name;
  private final AttributeAddress attrAddress;
  private final DataObject value;
  private final int classId;
  private final ObisCode obisCode;
  private final int attributeId;
  private AccessResultCode resultCode;

  public DataObjectAttrExecutor(
      final String name,
      final AttributeAddress attrAddress,
      final DataObject value,
      final int classId,
      final ObisCode obisCode,
      final int attributeId) {
    this.name = name;
    this.attrAddress = attrAddress;
    this.value = value;
    this.classId = classId;
    this.obisCode = obisCode;
    this.attributeId = attributeId;
  }

  public String getName() {
    return this.name;
  }

  public AttributeAddress getAttrAddress() {
    return this.attrAddress;
  }

  public DataObject getValue() {
    return this.value;
  }

  public AccessResultCode getResultCode() {
    return this.resultCode;
  }

  /**
   * @param conn : the active {@link DlmsConnection} to send the {@link SetParameter} to.
   * @throws IOException is thrown when an error occurs with the connection to the dlms device
   */
  public AccessResultCode executeSet(final DlmsConnectionManager conn) throws IOException {
    LOGGER.debug("WRITING {}", this.name);
    this.resultCode =
        conn.getConnection().set(new SetParameter(this.getAttrAddress(), this.getValue()));
    return this.resultCode;
  }

  /**
   * Creates a {@link String} containing all information from this object.
   *
   * @return a {@link String} that contains all member information from the object. This can be used
   *     to send error notifications back to the caller
   */
  public String createRequestAndResultCodeInfo() {
    return String.format(
        "%n%s: Result(%s), request(%s), classId(%d), obisCode(%s), attributeId(%d)",
        this.name, this.resultCode, this.value, this.classId, this.obisCode, this.attributeId);
  }
}
