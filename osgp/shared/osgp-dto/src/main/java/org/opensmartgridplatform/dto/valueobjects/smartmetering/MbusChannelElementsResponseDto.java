/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO with a list of {@link ChannelElementValuesDto}s determined from a lookup based on {@link
 * MbusChannelElementsDto}.
 */
public class MbusChannelElementsResponseDto implements Serializable {

  private static final long serialVersionUID = 5377631203726277889L;

  /**
   * Channel from one of the {@link #retrievedChannelElements} if it is a match for the device from
   * {@link #mbusChannelElementsDto}. Possibly {@code null} if not {@link #isChannelFound()}.
   */
  private final Short channel;

  private final List<ChannelElementValuesDto> retrievedChannelElements;

  /** DTO on which the contents of {@link #retrievedChannelElements} are based. */
  private final MbusChannelElementsDto mbusChannelElementsDto;

  public MbusChannelElementsResponseDto(
      final MbusChannelElementsDto mbusChannelElementsDto,
      final Short channel,
      final List<ChannelElementValuesDto> channelElements) {
    this.mbusChannelElementsDto = mbusChannelElementsDto;
    this.channel = channel;
    this.retrievedChannelElements = new ArrayList<>(channelElements);
  }

  @Override
  public String toString() {
    return String.format(
        "MbusChannelElementsResponseDto[channel: %s, input: %s, retrieved: %s]",
        this.channel, this.mbusChannelElementsDto, this.retrievedChannelElements);
  }

  public boolean isChannelFound() {
    return this.channel != null;
  }

  public Short getChannel() {
    return this.channel;
  }

  public List<ChannelElementValuesDto> getRetrievedChannelElements() {
    return new ArrayList<>(this.retrievedChannelElements);
  }

  public MbusChannelElementsDto getMbusChannelElementsDto() {
    return this.mbusChannelElementsDto;
  }
}
