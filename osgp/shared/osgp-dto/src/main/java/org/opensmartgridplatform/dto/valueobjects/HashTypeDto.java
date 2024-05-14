package org.opensmartgridplatform.dto.valueobjects;

import lombok.Getter;

@Getter
public enum HashTypeDto {
  SHA256("SHA-256"),
  MD5("MD5");

  private final String algorithmName;

  HashTypeDto(final String algorithmName) {
    this.algorithmName = algorithmName;
  }
}
