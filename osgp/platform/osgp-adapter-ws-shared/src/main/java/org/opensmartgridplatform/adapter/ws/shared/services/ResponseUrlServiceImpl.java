/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.shared.services;

import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseUrlData;
import org.opensmartgridplatform.adapter.ws.domain.repositories.ResponseUrlDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResponseUrlServiceImpl implements ResponseUrlService {

    @Autowired
    private ResponseUrlDataRepository responseUrlDataRepository;

    @Override
    public void saveResponseUrl(final String correlId, final String responseUrl) {
        this.responseUrlDataRepository.save(new ResponseUrlData(correlId, responseUrl));
    }

    @Override
    public void saveResponseUrlIfNeeded(final String correlId, final String responseUrl) {
        if (correlId != null && !correlId.isEmpty() && responseUrl != null && !responseUrl.isEmpty()) {
            this.saveResponseUrl(correlId, responseUrl);
        }
    }

    @Override
    public boolean hasResponseUrl(final String correlId) {
        return this.responseUrlDataRepository.findSingleResultByCorrelationUid(correlId) != null;
    }

    @Override
    public String findResponseUrl(final String correlId) {
        final ResponseUrlData responseDataUrl = this.responseUrlDataRepository
                .findSingleResultByCorrelationUid(correlId);
        return responseDataUrl == null ? null : responseDataUrl.getResponseUrl();
    }

    @Override
    public void deleteResponseUrl(final String correlId) {
        final ResponseUrlData responseDataUrl = this.responseUrlDataRepository
                .findSingleResultByCorrelationUid(correlId);
        if (responseDataUrl != null) {
            this.responseUrlDataRepository.delete(responseDataUrl);
        }
    }

    @Override
    public String popResponseUrl(final String correlId) {
        final ResponseUrlData responseDataUrl = this.responseUrlDataRepository
                .findSingleResultByCorrelationUid(correlId);
        if (responseDataUrl != null) {
            this.responseUrlDataRepository.delete(responseDataUrl);
            return responseDataUrl.getResponseUrl();
        } else {
            return null;
        }
    }

}
