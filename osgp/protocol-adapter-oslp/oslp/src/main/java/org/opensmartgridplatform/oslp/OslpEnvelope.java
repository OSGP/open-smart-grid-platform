/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.oslp;

import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import org.opensmartgridplatform.oslp.Oslp.Message;

/**
 * Envelope class which holds the OSLP payload.
 */
public class OslpEnvelope implements Serializable {
    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -7877297705451116796L;

    /**
     * Constant for security configuration errors.
     */
    private static final String SECURITY_CONFIG_EXCEPTION = "Given security settings caused an error.";

    /**
     * Length of the security hash NOTE: length for ECDSA could be smaller (73),
     * but leave it for now on 128 for testing with AME.
     */
    public static final int SECURITY_KEY_LENGTH = 128;

    /**
     * Length of the sequence number.
     */
    public static final int SEQUENCE_NUMBER_LENGTH = 2;

    /**
     * Length of the manufacturer id.
     */
    public static final int MANUFACTURER_ID_LENGTH = 2;

    /**
     * Length of the device id.
     */
    public static final int DEVICE_ID_LENGTH = 10;

    /**
     * Length of the length.
     */
    public static final int LENGTH_INDICATOR_LENGTH = 2;

    /**
     * Buffer for security key bytes.
     */
    private byte[] securityKey = new byte[SECURITY_KEY_LENGTH];

    /**
     * Buffer for sequence number bytes.
     */
    private byte[] sequenceNumber = new byte[SEQUENCE_NUMBER_LENGTH];

    /**
     * Buffer for deviceid bytes.
     */
    private byte[] deviceId = new byte[DEVICE_ID_LENGTH + MANUFACTURER_ID_LENGTH];

    /**
     * Buffer for OSLP payload.
     */
    private Message payloadMessage;

    /**
     * Identification of signature algorithm.
     */
    private String signature;

    /**
     * Provider of signature algorithm.
     */
    private String provider;

    /**
     * PrivateKey used for signing.
     */
    private PrivateKey privateKey;

    /**
     * Indicates whether message is valid. Only available after validate method
     * has been called.
     */
    private boolean valid;

    /**
     * Default constructor.
     */
    public OslpEnvelope() {
    }

    /**
     * Private constructor.
     *
     * @param signature
     *            signature algorithm
     * @param provider
     *            algorithm provider
     * @param privateKey
     *            privatekey for signing
     * @param securityKey
     *            securitykey containing validation bytes
     * @param sequenceNumber
     *            sequencenumber
     * @param deviceId
     *            deviceid of the device, 10 bytes
     * @param payloadMessage
     *            payload to deliver
     */
    private OslpEnvelope(final String signature, final String provider, final PrivateKey privateKey,
            final byte[] securityKey, final byte[] sequenceNumber, final byte[] deviceId,
            final Message payloadMessage) {
        this.signature = signature;
        this.provider = provider;
        this.privateKey = privateKey;
        this.setSequenceNumber(sequenceNumber);
        this.setDeviceId(deviceId);
        this.setPayloadMessage(payloadMessage);

        // Generate new securityKey when not available
        if (securityKey == null || ArrayUtils.isEmpty(securityKey)) {
            this.setSecurityKey();
        } else {
            this.setSecurityKey(securityKey);
        }
    }

    /**
     * @return security key bytes.
     */
    public byte[] getSecurityKey() {
        return Arrays.copyOf(this.securityKey, this.securityKey.length);
    }

    /**
     * set security key bytes.
     */
    public void setSecurityKey(final byte[] newSecurityKey) {
        if (newSecurityKey == null) {
            this.securityKey = new byte[0];
        } else {
            if (newSecurityKey.length != SECURITY_KEY_LENGTH) {
                throw new IllegalArgumentException("SecurityKey is not of expected Length: " + SECURITY_KEY_LENGTH);
            }
            this.securityKey = Arrays.copyOf(newSecurityKey, newSecurityKey.length);
        }
    }

    /**
     * @return length indicator, based on overall message length including
     *         envelope.
     */
    public byte[] getLengthIndicator() {
        int messageLength = 0;

        if (this.payloadMessage != null) {
            messageLength += this.payloadMessage.getSerializedSize();
        }

        final byte[] sizeBytes = new byte[LENGTH_INDICATOR_LENGTH];
        sizeBytes[0] = (byte) ((messageLength >>> 8) & 0xFF);
        sizeBytes[1] = (byte) (messageLength & 0xFF);

        return sizeBytes;
    }

    /**
     * @return sequence number
     */
    public byte[] getSequenceNumber() {
        return Arrays.copyOf(this.sequenceNumber, this.sequenceNumber.length);
    }

    /**
     * Set new sequence number.
     */
    public void setSequenceNumber(final byte[] newSequenceNumber) {
        if (newSequenceNumber == null) {
            this.sequenceNumber = new byte[0];
        } else {
            if (newSequenceNumber.length != SEQUENCE_NUMBER_LENGTH) {
                throw new IllegalArgumentException(
                        "SequenceNumber is not of expected Length: " + SEQUENCE_NUMBER_LENGTH);
            }
            this.sequenceNumber = Arrays.copyOf(newSequenceNumber, newSequenceNumber.length);
        }
    }

    /**
     * @return deviceid
     */
    public byte[] getDeviceId() {
        return Arrays.copyOf(this.deviceId, this.deviceId.length);
    }

    /**
     * Set new device id and new manufacturer id. Both values will be combined
     * into one array.
     */
    public void setDeviceId(final byte[] newDeviceId) {
        // Check if the parameter is null.
        if (newDeviceId == null) {
            this.deviceId = new byte[0];
        } else {
            // Check the length.
            if (newDeviceId.length != (DEVICE_ID_LENGTH + MANUFACTURER_ID_LENGTH)) {
                throw new IllegalArgumentException("ManufacturerId + DeviceId is not of expected Length: "
                        + (DEVICE_ID_LENGTH + MANUFACTURER_ID_LENGTH));
            }

            // Set the combined Manufacturer ID and Device ID in the deviceId
            // field.
            this.deviceId = Arrays.copyOf(newDeviceId, newDeviceId.length);
        }
    }

    /**
     * @return OSLP payload
     */
    public Message getPayloadMessage() {
        return this.payloadMessage;
    }

    /**
     * Sets new OSLP payload.
     */
    public void setPayloadMessage(final Message payloadMessage) {
        this.payloadMessage = payloadMessage;
    }

    /**
     * Validates the envelope + payload (excluding securityKey) using signature
     * algorithm.
     *
     * @return true when securityKey and calculated signature match or false
     *         otherwise.
     */
    public boolean validate(final PublicKey publicKey) {
        try {
            this.valid = OslpUtils.validateSignature(OslpUtils.createSignBytes(this), this.getSecurityKey(), publicKey,
                    this.signature, this.provider);
        } catch (final GeneralSecurityException e) {
            throw new IllegalArgumentException(SECURITY_CONFIG_EXCEPTION, e);
        }

        return this.valid;
    }

    /**
     * Indicates whether message is valid. Only available after validate method
     * has been called.
     */
    public boolean isValid() {
        return this.valid;
    }

    /**
     * Calculate the new securityKey based on envelope + payload (excluding
     * securityKey) using signature algorithm.
     */
    private void setSecurityKey() {
        try {
            // Calculate and encrypt hash
            final byte[] sig = OslpUtils.createSignature(OslpUtils.createSignBytes(this), this.privateKey,
                    this.signature, this.provider);
            System.arraycopy(sig, 0, this.securityKey, 0, sig.length);
        } catch (final GeneralSecurityException e) {
            throw new IllegalArgumentException(SECURITY_CONFIG_EXCEPTION, e);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final OslpEnvelope that = (OslpEnvelope) o;
        if (this.signature != null ? !this.signature.equals(that.signature) : that.signature != null) {
            return false;
        }
        if (!Arrays.equals(this.deviceId, that.deviceId)) {
            return false;
        }
        if (this.provider != null ? !this.provider.equals(that.provider) : that.provider != null) {
            return false;
        }
        if (this.payloadMessage != null ? !this.payloadMessage.equals(that.payloadMessage)
                : that.payloadMessage != null) {
            return false;
        }
        if (this.privateKey != null ? !this.privateKey.equals(that.privateKey) : that.privateKey != null) {
            return false;
        }
        if (!Arrays.equals(this.securityKey, that.securityKey)) {
            return false;
        }
        if (!Arrays.equals(this.sequenceNumber, that.sequenceNumber)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = this.securityKey != null ? Arrays.hashCode(this.securityKey) : 0;
        result = 31 * result + (this.sequenceNumber != null ? Arrays.hashCode(this.sequenceNumber) : 0);
        result = 31 * result + (this.deviceId != null ? Arrays.hashCode(this.deviceId) : 0);
        result = 31 * result + (this.payloadMessage != null ? this.payloadMessage.hashCode() : 0);
        result = 31 * result + (this.signature != null ? this.signature.hashCode() : 0);
        result = 31 * result + (this.provider != null ? this.provider.hashCode() : 0);
        result = 31 * result + (this.privateKey != null ? this.privateKey.hashCode() : 0);
        return result;
    }

    public int getSize() {
        return OslpEnvelope.SECURITY_KEY_LENGTH + OslpEnvelope.SEQUENCE_NUMBER_LENGTH
                + OslpEnvelope.MANUFACTURER_ID_LENGTH + OslpEnvelope.DEVICE_ID_LENGTH
                + OslpEnvelope.LENGTH_INDICATOR_LENGTH + this.payloadMessage.getSerializedSize();
    }

    /**
     * Builder which constructs an OSLP envelope.
     */
    public static class Builder {
        private String signature;
        private String provider;
        private PrivateKey privateKey;
        private byte[] securityKey;
        private byte[] sequenceNumber = new byte[SEQUENCE_NUMBER_LENGTH];
        private byte[] deviceId = new byte[DEVICE_ID_LENGTH + MANUFACTURER_ID_LENGTH];
        private Message payloadMessage = Message.getDefaultInstance();

        public Builder withSignature(final String signature) {
            this.signature = signature;
            return this;
        }

        public Builder withProvider(final String provider) {
            this.provider = provider;
            return this;
        }

        public Builder withPrimaryKey(final PrivateKey privateKey) {
            this.privateKey = privateKey;
            return this;
        }

        public Builder withSecurityKey(final byte[] newSecurityKey) {
            if (newSecurityKey == null) {
                this.securityKey = new byte[0];
            } else {
                this.securityKey = Arrays.copyOf(newSecurityKey, newSecurityKey.length);
            }

            return this;
        }

        public Builder withSequenceNumber(final byte[] newSequenceNumber) {
            if (newSequenceNumber == null) {
                this.sequenceNumber = new byte[0];
            } else {
                this.sequenceNumber = Arrays.copyOf(newSequenceNumber, newSequenceNumber.length);
            }

            return this;
        }

        public Builder withDeviceId(final byte[] newDeviceId) {
            if (newDeviceId == null) {
                this.deviceId = new byte[0];
            } else {
                this.deviceId = Arrays.copyOf(newDeviceId, newDeviceId.length);
            }

            return this;
        }

        public Builder withPayloadMessage(final Message payloadMessage) {
            this.payloadMessage = payloadMessage;
            return this;
        }

        public OslpEnvelope build() {
            return new OslpEnvelope(this.signature, this.provider, this.privateKey, this.securityKey,
                    this.sequenceNumber, this.deviceId, this.payloadMessage);
        }
    }
}