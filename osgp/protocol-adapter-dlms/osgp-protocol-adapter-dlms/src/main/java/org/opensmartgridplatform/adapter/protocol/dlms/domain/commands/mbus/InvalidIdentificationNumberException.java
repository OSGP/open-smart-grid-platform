package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import java.io.Serial;
import lombok.Getter;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelElementValuesDto;

@Getter
public class InvalidIdentificationNumberException extends ProtocolAdapterException {

  @Serial private static final long serialVersionUID = 8192416923143393111L;

  private final ChannelElementValuesDto channelElementValuesDto;

  public InvalidIdentificationNumberException(
      final String message, final ChannelElementValuesDto channelElementValuesDto) {
    super(message);
    this.channelElementValuesDto = channelElementValuesDto;
  }
}
