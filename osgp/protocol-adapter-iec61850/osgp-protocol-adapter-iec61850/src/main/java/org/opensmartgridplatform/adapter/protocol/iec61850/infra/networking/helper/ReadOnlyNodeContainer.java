// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper;

import com.beanit.openiec61850.FcModelNode;
import java.util.Date;
import org.apache.commons.lang3.NotImplementedException;

public final class ReadOnlyNodeContainer extends NodeContainer {

  private static final String NOT_SUPPORTED = "Writing is not supported";

  public ReadOnlyNodeContainer(final String deviceIdentification, final FcModelNode fcmodelNode) {
    super(deviceIdentification, fcmodelNode);
  }

  @Override
  public void write() {
    throw new NotImplementedException(NOT_SUPPORTED);
  }

  @Override
  public void writeBoolean(final SubDataAttribute child, final boolean value) {
    throw new NotImplementedException(NOT_SUPPORTED);
  }

  @Override
  public void writeByte(final SubDataAttribute child, final byte value) {
    throw new NotImplementedException(NOT_SUPPORTED);
  }

  @Override
  public void writeDate(final SubDataAttribute child, final Date value) {
    throw new NotImplementedException(NOT_SUPPORTED);
  }

  @Override
  public void writeFloat(final SubDataAttribute child, final Float value) {
    throw new NotImplementedException(NOT_SUPPORTED);
  }

  @Override
  public void writeInteger(final SubDataAttribute child, final Integer value) {
    throw new NotImplementedException(NOT_SUPPORTED);
  }

  @Override
  public void writeShort(final SubDataAttribute child, final Short value) {
    throw new NotImplementedException(NOT_SUPPORTED);
  }

  @Override
  public void writeString(final SubDataAttribute child, final String value) {
    throw new NotImplementedException(NOT_SUPPORTED);
  }

  @Override
  public void writeUnsignedShort(final SubDataAttribute child, final Integer value) {
    throw new NotImplementedException(NOT_SUPPORTED);
  }

  @Override
  public NodeContainer getChild(final SubDataAttribute child) {
    return new ReadOnlyNodeContainer(
        this.deviceIdentification, (FcModelNode) this.parent.getChild(child.getDescription()));
  }

  @Override
  public NodeContainer getChild(final String child) {
    return new ReadOnlyNodeContainer(
        this.deviceIdentification, (FcModelNode) this.parent.getChild(child));
  }
}
