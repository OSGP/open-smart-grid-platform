/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.iec60870;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.openmuc.j60870.Server;
import org.openmuc.j60870.ie.InformationElement;
import org.opensmartgridplatform.iec60870.factory.InformationElementFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Iec60870Server {
    private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870Server.class);

    private final InformationElementFactory informationElementFactory = new InformationElementFactory();

    private final Iec60870ServerEventListener iec60870ServerEventListener;
    private Server server;
    private final int port;
    private boolean listening = false;
    private Map<Integer, InformationElement[][]> processImage = new HashMap<>();

    public Iec60870Server(final Iec60870ServerEventListener iec60870ServerEventListener, final int port) {
        this.iec60870ServerEventListener = iec60870ServerEventListener;
        this.port = port;
    }

    public void start() {
        this.server = Server.builder().setPort(this.port).build();

        try {
            LOGGER.info("Starting IEC60870 Server on port {}.", this.port);
            this.server.start(this.iec60870ServerEventListener);
            this.listening = true;
            LOGGER.info("Started IEC60870 Server.");
        } catch (final IOException e) {
            LOGGER.error("Exception occurred while starting IEC60870 server.", e);
        }
    }

    public void stop() {
        LOGGER.info("Stopping IEC60870 Server on port {}.", this.port);
        this.server.stop();
        this.listening = false;
        LOGGER.info("Stopped IEC60870 Server.");
    }

    public Iec60870ServerEventListener getIec60870ServerEventListener() {
        return this.iec60870ServerEventListener;
    }

    public boolean isListening() {
        return this.listening;
    }

    public Map<Integer, InformationElement[][]> getProcessImage() {
        return this.processImage;
    }

    public void setProcessImage(final Map<Integer, InformationElement[][]> processImage) {
        this.processImage = processImage;
    }

    /**
     * If the informationObjectAddress is already in the process image, the
     * value is updated. Otherwise a new item is added to the process image.
     *
     * An event ASDU is sent to the controlling station if the value has
     * changed.
     *
     * @param informationObjectAddress
     *            the address of the item in the process image
     * @param informationObjectType
     *            the type of information object
     * @param value
     *            the information element value
     */
    public void updateInformationObject(final int informationObjectAddress,
            final Iec60870InformationObjectType informationObjectType, final Object value) {

        final InformationElement[][] informationElements = this.informationElementFactory
                .createInformationElements(informationObjectType, value);

        boolean valueChanged = true;
        if (this.processImage.containsKey(informationObjectAddress)) {
            valueChanged = this.hasChanged(informationElements, this.processImage.get(informationObjectAddress));
        }

        this.processImage.put(informationObjectAddress, informationElements);

        if (valueChanged) {
            this.iec60870ServerEventListener.sendInformationUpdateEvent(informationObjectAddress, informationElements);
        }
    }

    private boolean hasChanged(final InformationElement[][] newValue, final InformationElement[][] oldValue) {

        if (newValue.length != oldValue.length) {
            return true;
        }
        for (int index = 0; index < newValue.length; index++) {
            if (newValue[index].length != oldValue[index].length) {
                return true;
            }
            for (int idx = 0; idx < newValue[index].length; idx++) {
                if (!newValue[index][idx].toString().equals(oldValue[index][idx].toString())) {
                    return true;
                }
            }
        }
        return false;
    }

}
