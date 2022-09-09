package org.opensmartgridplatform.adapter.protocol.jasper.rest.json;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JasperErrorResponse {

  String errorMessage;
  String errorCode;
}
