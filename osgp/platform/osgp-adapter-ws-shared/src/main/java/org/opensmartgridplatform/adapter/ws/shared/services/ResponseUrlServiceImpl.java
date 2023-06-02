//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.shared.services;

import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseUrlData;
import org.opensmartgridplatform.adapter.ws.domain.repositories.ResponseUrlDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResponseUrlServiceImpl implements ResponseUrlService {

  @Autowired private ResponseUrlDataRepository responseUrlDataRepository;

  @Override
  public void saveResponseUrl(final String correlationUid, final String responseUrl) {
    this.responseUrlDataRepository.save(new ResponseUrlData(correlationUid, responseUrl));
  }

  @Override
  public void saveResponseUrlIfNeeded(final String correlationUid, final String responseUrl) {
    if (correlationUid != null
        && !correlationUid.isEmpty()
        && responseUrl != null
        && !responseUrl.isEmpty()) {
      this.saveResponseUrl(correlationUid, responseUrl);
    }
  }

  @Override
  public boolean hasResponseUrl(final String correlationUid) {
    return this.responseUrlDataRepository.findSingleResultByCorrelationUid(correlationUid) != null;
  }

  @Override
  public String findResponseUrl(final String correlationUid) {
    final ResponseUrlData responseDataUrl =
        this.responseUrlDataRepository.findSingleResultByCorrelationUid(correlationUid);
    return responseDataUrl == null ? null : responseDataUrl.getResponseUrl();
  }

  @Override
  public void deleteResponseUrl(final String correlationUid) {
    final ResponseUrlData responseDataUrl =
        this.responseUrlDataRepository.findSingleResultByCorrelationUid(correlationUid);
    if (responseDataUrl != null) {
      this.responseUrlDataRepository.delete(responseDataUrl);
    }
  }

  @Override
  public String popResponseUrl(final String correlationUid) {
    final ResponseUrlData responseDataUrl =
        this.responseUrlDataRepository.findSingleResultByCorrelationUid(correlationUid);
    if (responseDataUrl != null) {
      this.responseUrlDataRepository.delete(responseDataUrl);
      return responseDataUrl.getResponseUrl();
    } else {
      return null;
    }
  }
}
