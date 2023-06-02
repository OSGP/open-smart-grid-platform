//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.webdevicesimulator.domain.entities;

import com.google.protobuf.Message;
import javax.persistence.Column;
import javax.persistence.Entity;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

@Entity
public class OslpLogItem extends AbstractEntity {
  /** */
  private static final long serialVersionUID = -7937120696814867266L;

  private static final int MAX_MESSAGE_LENGTH = 8000;

  private boolean incoming;

  private String deviceUid;

  @Column(length = MAX_MESSAGE_LENGTH)
  private String encodedMessage;

  @Column(length = MAX_MESSAGE_LENGTH)
  private String decodedMessage;

  private String deviceIdentification;

  @SuppressWarnings("unused")
  private OslpLogItem() {
    // Empty constructor for Hibernate.
  }

  public OslpLogItem(
      final byte[] deviceUid,
      final String deviceIdentification,
      final boolean incoming,
      final Message message) {
    this.deviceUid = Base64.encodeBase64String(deviceUid);
    this.deviceIdentification = deviceIdentification;
    this.incoming = incoming;

    // Truncate the logitems to length
    this.encodedMessage =
        StringUtils.substring(
            OslpLogItem.bytesToCArray(message.toByteArray()), 0, MAX_MESSAGE_LENGTH);
    this.decodedMessage = StringUtils.substring(message.toString(), 0, MAX_MESSAGE_LENGTH);
  }

  public boolean isIncoming() {
    return this.incoming;
  }

  public String getDeviceUid() {
    return this.deviceUid;
  }

  public String getEncodedMessage() {
    return this.encodedMessage;
  }

  public String getDecodedMessage() {
    return this.decodedMessage;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  private static String bytesToCArray(final byte[] bytes) {
    String s = javax.xml.bind.DatatypeConverter.printHexBinary(bytes);

    // Split every two chars with ', ' to create a C array.
    s = s.replaceAll("(.{2})", ", 0x$1");

    // Remove the leading comma.
    s = s.substring(2);

    return s;
  }
}
