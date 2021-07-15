/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class GetMbusEncryptionKeyStatusByChannelRequestDataDto implements ActionRequestDto {

  private static final long serialVersionUID = 5456202868652046554L;

  private final short channel;

  public GetMbusEncryptionKeyStatusByChannelRequestDataDto(final short channel) {
    this.channel = channel;
  }

  public short getChannel() {
    return this.channel;
  }
}
