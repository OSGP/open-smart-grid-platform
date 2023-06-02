//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.Objects;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class FaultResponseDto extends ActionResponseDto {

  private static final long serialVersionUID = -2599283959144295334L;

  private final Integer code;
  private final String message;
  private final String component;
  private final String innerException;
  private final String innerMessage;
  private final FaultResponseParametersDto faultResponseParameters;
  private final boolean retryable;

  private FaultResponseDto(final Builder builder) {
    super(OsgpResultTypeDto.NOT_OK, null, null);

    Objects.requireNonNull("Message is not allowed to be null", builder.message);

    this.code = builder.code;
    this.message = builder.message;
    this.component = builder.component;
    this.innerException = builder.innerException;
    this.innerMessage = builder.innerMessage;
    this.faultResponseParameters = builder.faultResponseParameters;
    this.retryable = builder.retryable;
  }

  public static class Builder {

    private Integer code;
    private String message;
    private String component;
    private String innerException;
    private String innerMessage;
    private FaultResponseParametersDto faultResponseParameters;
    private boolean retryable = true;

    public Builder withCode(final Integer code) {
      this.code = code;
      return this;
    }

    public Builder withMessage(final String message) {
      this.message = message;
      return this;
    }

    public Builder withComponent(final String component) {
      this.component = component;
      return this;
    }

    public Builder withInnerException(final String innerException) {
      this.innerException = innerException;
      return this;
    }

    public Builder withInnerMessage(final String innerMessage) {
      this.innerMessage = innerMessage;
      return this;
    }

    public Builder withFaultResponseParameters(
        final FaultResponseParametersDto faultResponseParameters) {
      this.faultResponseParameters = faultResponseParameters;
      return this;
    }

    public Builder withRetryable(final boolean retryable) {
      this.retryable = retryable;
      return this;
    }

    public FaultResponseDto build() {
      return new FaultResponseDto(this);
    }
  }
}
