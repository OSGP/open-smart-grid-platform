// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.MethodParameter;
import org.openmuc.jdlms.MethodResult;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.GetResultImpl;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.AttributeClass;
import org.opensmartgridplatform.dlms.interfaceclass.method.MethodClass;

public class DlmsConnectionStub implements DlmsConnection {

  private List<AttributeAddress> requestedAttributeAddresses = new ArrayList<>();
  private final List<SetParameter> parametersSet = new ArrayList<>();
  private final List<MethodParameter> methodsInvoked = new ArrayList<>();

  private final Map<String, List<DataObject>> returnValues = new HashMap<>();

  private DataObject defaultReturnValue = DataObject.newNullData();
  private MethodResult defaultMethodResult = null;

  private boolean closeCalled = false;

  @Override
  public List<GetResult> get(final List<AttributeAddress> attributeAddresses) {
    final List<GetResult> results = new ArrayList<>();
    for (final AttributeAddress attributeAddress : attributeAddresses) {
      results.add(this.get(attributeAddress));
    }
    return results;
  }

  @Override
  public GetResult get(final AttributeAddress attributeAddress) {
    final int invocationNr =
        Long.valueOf(
                this.requestedAttributeAddresses.stream()
                    .filter(addr -> this.getKey(addr).equals(this.getKey(attributeAddress)))
                    .count())
            .intValue();

    this.requestedAttributeAddresses.add(attributeAddress);

    if (this.returnValues.containsKey(this.getKey(attributeAddress))) {
      // If multiple returnvalues configured, than return return value based on invocations
      final int resultNr =
          Integer.min(
              this.returnValues.get(this.getKey(attributeAddress)).size() - 1, invocationNr);
      return new GetResultImpl(
          this.returnValues.get(this.getKey(attributeAddress)).get(resultNr),
          AccessResultCode.SUCCESS);
    } else {
      return new GetResultImpl(this.defaultReturnValue, AccessResultCode.SUCCESS);
    }
  }

  @Override
  public List<AccessResultCode> set(final List<SetParameter> setParameters) {
    final List<AccessResultCode> results = new ArrayList<>();
    for (final SetParameter setParameter : setParameters) {
      results.add(this.set(setParameter));
    }
    return results;
  }

  @Override
  public AccessResultCode set(final SetParameter setParameter) {
    this.parametersSet.add(setParameter);

    return AccessResultCode.SUCCESS;
  }

  @Override
  public List<MethodResult> action(final List<MethodParameter> methodParameters) {
    final List<MethodResult> results = new ArrayList<>();
    for (final MethodParameter methodParameter : methodParameters) {
      results.add(this.action(methodParameter));
    }
    return results;
  }

  @Override
  public MethodResult action(final MethodParameter methodParameter) {
    this.methodsInvoked.add(methodParameter);

    return this.defaultMethodResult;
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
  public List<AccessResultCode> set(final boolean var1, final List<SetParameter> var2) {
    return null;
  }

  @Override
  public AccessResultCode set(final boolean var1, final SetParameter var2) {
    return null;
  }

  @Override
  public MethodResult action(final boolean var1, final MethodParameter var2) {
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

  public List<SetParameter> getSetParameters(final AttributeClass attributeClass) {
    return this.parametersSet.stream()
        .filter(
            setParameter ->
                setParameter.getAttributeAddress().getId() == attributeClass.attributeId())
        .collect(Collectors.toList());
  }

  public boolean hasMethodBeenInvoked(final MethodClass methodClass) {
    return this.methodsInvoked.stream()
        .anyMatch(parameter -> parameter.getId() == methodClass.getMethodId());
  }

  public void addReturnValue(
      final AttributeAddress attributeAddress,
      final DataObject dataObject,
      final int nrOfInvocations) {
    for (int i = 0; i < nrOfInvocations; i++) {
      this.returnValues.putIfAbsent(this.getKey(attributeAddress), new ArrayList<>());
      this.returnValues.get(this.getKey(attributeAddress)).add(dataObject);
    }
  }

  public void addReturnValue(final AttributeAddress attributeAddress, final DataObject dataObject) {
    this.addReturnValue(attributeAddress, dataObject, 1);
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

  public void setDefaultMethodResult(final MethodResult methodResult) {
    this.defaultMethodResult = methodResult;
  }
}
