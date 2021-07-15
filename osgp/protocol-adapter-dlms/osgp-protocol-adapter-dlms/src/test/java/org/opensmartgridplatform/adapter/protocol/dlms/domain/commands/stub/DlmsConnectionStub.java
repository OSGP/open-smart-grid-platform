/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.MethodParameter;
import org.openmuc.jdlms.MethodResult;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.GetResultImpl;

public class DlmsConnectionStub implements DlmsConnection {

  private List<AttributeAddress> requestedAttributeAddresses = new ArrayList<>();

  private final Map<String, DataObject> returnValues = new HashMap<>();
  private DataObject defaultReturnValue = DataObject.newNullData();

  private boolean closeCalled = false;

  @Override
  public List<GetResult> get(final List<AttributeAddress> params) {

    this.requestedAttributeAddresses.addAll(params);

    final List<GetResult> results = new ArrayList<>();

    for (final AttributeAddress attributeAddress : params) {
      results.add(this.get(attributeAddress));
    }

    return results;
  }

  @Override
  public GetResult get(final AttributeAddress var1) {

    this.requestedAttributeAddresses.addAll(Collections.singletonList(var1));

    if (this.returnValues.containsKey(this.getKey(var1))) {
      return new GetResultImpl(this.returnValues.get(this.getKey(var1)), AccessResultCode.SUCCESS);
    } else {
      return new GetResultImpl(this.defaultReturnValue, AccessResultCode.SUCCESS);
    }
  }

  @Override
  public GetResult get(final boolean var1, final AttributeAddress var2) {
    return null;
  }

  @Override
  public List<GetResult> get(final boolean var1, final List<AttributeAddress> var2) {
    return null;
  }

  @Override
  public List<AccessResultCode> set(final List<SetParameter> var1) {
    return null;
  }

  @Override
  public List<AccessResultCode> set(final boolean var1, final List<SetParameter> var2) {
    return null;
  }

  @Override
  public AccessResultCode set(final boolean var1, final SetParameter var2) {
    return null;
  }

  @Override
  public AccessResultCode set(final SetParameter var1) {
    return null;
  }

  @Override
  public MethodResult action(final boolean var1, final MethodParameter var2) {
    return null;
  }

  @Override
  public MethodResult action(final MethodParameter var1) {
    return null;
  }

  @Override
  public List<MethodResult> action(final List<MethodParameter> var1) {
    return null;
  }

  @Override
  public List<MethodResult> action(final boolean var1, final List<MethodParameter> var2) {
    return null;
  }

  @Override
  public void changeClientGlobalAuthenticationKey(final byte[] var1) {}

  @Override
  public void changeClientGlobalEncryptionKey(final byte[] var1) {}

  @Override
  public void disconnect() {}

  @Override
  public void close() {
    this.closeCalled = true;
  }

  public List<AttributeAddress> getRequestedAttributeAddresses() {
    return this.requestedAttributeAddresses;
  }

  public void clearRequestedAttributeAddresses() {
    this.requestedAttributeAddresses = new ArrayList<>();
  }

  public void addReturnValue(final AttributeAddress attributeAddress, final DataObject dataObject) {
    this.returnValues.put(this.getKey(attributeAddress), dataObject);
  }

  public void setDefaultReturnValue(final DataObject dataObject) {
    this.defaultReturnValue = dataObject;
  }

  public boolean isCloseCalled() {
    return this.closeCalled;
  }

  private String getKey(final AttributeAddress attributeAddress) {
    return attributeAddress.getInstanceId() + "-" + attributeAddress.getId();
  }
}
