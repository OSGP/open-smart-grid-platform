/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

import org.openmuc.jdlms.CosemAttribute;
import org.openmuc.jdlms.CosemClass;
import org.openmuc.jdlms.CosemInterfaceObject;
import org.openmuc.jdlms.CosemMethod;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.simulator.protocol.dlms.util.DynamicValues;
import org.springframework.beans.factory.annotation.Autowired;

@CosemClass(id = 72)
public class MBusClientSetup extends CosemInterfaceObject {

  public static final int MBUS_CLIENT_CLASS_ID = InterfaceClass.MBUS_CLIENT.id();
  public static final int ATTRIBUTE_ID_PRIMARY_ADDRESS = 5;
  public static final int ATTRIBUTE_ID_IDENTIFICATION_NUMBER = 6;
  public static final int ATTRIBUTE_ID_MANUFACTURER_ID = 7;
  public static final int ATTRIBUTE_ID_VERSION = 8;
  public static final int ATTRIBUTE_ID_DEVICE_TYPE = 9;
  public static final int ATTRIBUTE_ID_CONFIGURATION = 13;
  public static final int ATTRIBUTE_ID_ENCRYPTION_KEY_STATUS = 14;

  public static final int METHOD_ID_SLAVE_DEINSTALL = 2;
  public static final int METHOD_ID_RESET_ALARM = 4;
  public static final int METHOD_ID_DATA_SEND = 6;
  public static final int METHOD_ID_SET_ENCRYPTION_KEY = 7;
  public static final int METHOD_ID_TRANSFER_KEY = 8;

  @Autowired private DynamicValues dynamicValues;

  @CosemAttribute(id = ATTRIBUTE_ID_PRIMARY_ADDRESS, type = Type.UNSIGNED)
  private DataObject primaryAddress;

  @CosemAttribute(id = ATTRIBUTE_ID_IDENTIFICATION_NUMBER, type = Type.DOUBLE_LONG_UNSIGNED)
  private DataObject identificationNumber;

  @CosemAttribute(id = ATTRIBUTE_ID_MANUFACTURER_ID, type = Type.LONG_UNSIGNED)
  private DataObject manufacturerId;

  @CosemAttribute(id = ATTRIBUTE_ID_VERSION, type = Type.UNSIGNED)
  private DataObject version;

  @CosemAttribute(id = ATTRIBUTE_ID_DEVICE_TYPE, type = Type.UNSIGNED)
  private DataObject deviceType;

  @CosemAttribute(id = ATTRIBUTE_ID_ENCRYPTION_KEY_STATUS, type = Type.ENUMERATE)
  private DataObject encryptionKeyStatus;

  public MBusClientSetup(final String logicalName) {
    super(logicalName);
  }

  @CosemMethod(id = METHOD_ID_SLAVE_DEINSTALL, consumes = Type.UNSIGNED)
  public void slaveDeinstall(final DataObject param) {
    /*
     * The method slave_deinstall also should set the M-Bus address and
     * destroy the encryption key on the M-Bus slave device. Since we only
     * simulate the master, we cannot implement those actions here.
     */
    this.setPrimaryAddress(DataObject.newUInteger8Data((byte) 0));
    this.dynamicValues.setDlmsAttributeValue(
        this, ATTRIBUTE_ID_CONFIGURATION, DataObject.newUInteger16Data(99));
    this.dynamicValues.setDlmsAttributeValue(
        this,
        ATTRIBUTE_ID_ENCRYPTION_KEY_STATUS,
        DataObject.newEnumerateData(EncryptionKeyStatusType.NO_ENCRYPTION_KEY.value));
  }

  @CosemMethod(id = METHOD_ID_RESET_ALARM, consumes = Type.INTEGER)
  public void resetAlarm(final DataObject param) {
    /*
     * This method does not provide any simulated functionality except for
     * accepting the method call. While there is no simulation of encrypted
     * communication with an M-Bus device, simulating any action in this
     * method would be pointless.
     */
  }

  @CosemMethod(id = METHOD_ID_DATA_SEND, consumes = Type.ARRAY)
  public void dataSend(final DataObject param) {
    /*
     * This method does not provide any simulated functionality except for
     * accepting the method call. While there is no simulation of encrypted
     * communication with an M-Bus device, simulating any action in this
     * method would be pointless.
     */
  }

  @CosemMethod(id = METHOD_ID_SET_ENCRYPTION_KEY, consumes = Type.OCTET_STRING)
  public void setEncryptionKey(final DataObject param) {
    /*
     * This method does not provide any simulated functionality except for
     * accepting the method call. While there is no simulation of encrypted
     * communication with an M-Bus device, simulating any action in this
     * method would be pointless.
     */
  }

  @CosemMethod(id = METHOD_ID_TRANSFER_KEY, consumes = Type.OCTET_STRING)
  public void transferKey(final DataObject param) {
    /*
     * This method does not provide any simulated functionality except for
     * accepting the method call. While there is no simulation of encrypted
     * communication with an M-Bus device, simulating any action in this
     * method would be pointless.
     */
  }

  public DataObject getPrimaryAddress() {
    return this.dynamicValues.getDlmsAttributeValue(this, ATTRIBUTE_ID_PRIMARY_ADDRESS);
  }

  public void setPrimaryAddress(final DataObject attributeValue) {
    this.dynamicValues.setDlmsAttributeValue(this, ATTRIBUTE_ID_PRIMARY_ADDRESS, attributeValue);
  }

  public DataObject getIdentificationNumber() {
    return this.dynamicValues.getDlmsAttributeValue(this, ATTRIBUTE_ID_IDENTIFICATION_NUMBER);
  }

  public void setIdentificationNumber(final DataObject attributeValue) {
    this.dynamicValues.setDlmsAttributeValue(
        this, ATTRIBUTE_ID_IDENTIFICATION_NUMBER, attributeValue);
  }

  public DataObject getManufacturerId() {
    return this.dynamicValues.getDlmsAttributeValue(this, ATTRIBUTE_ID_MANUFACTURER_ID);
  }

  public void setManufacturerId(final DataObject attributeValue) {
    this.dynamicValues.setDlmsAttributeValue(this, ATTRIBUTE_ID_MANUFACTURER_ID, attributeValue);
  }

  public DataObject getVersion() {
    return this.dynamicValues.getDlmsAttributeValue(this, ATTRIBUTE_ID_VERSION);
  }

  public void setVersion(final DataObject attributeValue) {
    this.dynamicValues.setDlmsAttributeValue(this, ATTRIBUTE_ID_VERSION, attributeValue);
  }

  public DataObject getDeviceType() {
    return this.dynamicValues.getDlmsAttributeValue(this, ATTRIBUTE_ID_DEVICE_TYPE);
  }

  public void setDeviceType(final DataObject attributeValue) {
    this.dynamicValues.setDlmsAttributeValue(this, ATTRIBUTE_ID_DEVICE_TYPE, attributeValue);
  }

  public DataObject getEncryptionKeyStatus() {
    return this.dynamicValues.getDlmsAttributeValue(this, ATTRIBUTE_ID_ENCRYPTION_KEY_STATUS);
  }
}
