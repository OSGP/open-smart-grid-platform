package util;

import java.io.IOException;
import java.util.List;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.MethodParameter;
import org.openmuc.jdlms.MethodResult;
import org.openmuc.jdlms.SetParameter;

/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
public class DlmsConnectionStub implements DlmsConnection {
    @Override
    public List<GetResult> get(List<AttributeAddress> list) throws IOException {
        return null;
    }

    @Override
    public GetResult get(AttributeAddress attributeAddress) throws IOException {
        return null;
    }

    @Override
    public GetResult get(boolean b, AttributeAddress attributeAddress) throws IOException {
        return null;
    }

    @Override
    public List<GetResult> get(boolean b, List<AttributeAddress> list) throws IOException {
        return null;
    }

    @Override
    public List<AccessResultCode> set(List<SetParameter> list) throws IOException {
        return null;
    }

    @Override
    public List<AccessResultCode> set(boolean b, List<SetParameter> list) throws IOException {
        return null;
    }

    @Override
    public AccessResultCode set(boolean b, SetParameter setParameter) throws IOException {
        return null;
    }

    @Override
    public AccessResultCode set(SetParameter setParameter) throws IOException {
        return null;
    }

    @Override
    public MethodResult action(boolean b, MethodParameter methodParameter) throws IOException {
        return null;
    }

    @Override
    public MethodResult action(MethodParameter methodParameter) throws IOException {
        return null;
    }

    @Override
    public List<MethodResult> action(List<MethodParameter> list) throws IOException {
        return null;
    }

    @Override
    public List<MethodResult> action(boolean b, List<MethodParameter> list) throws IOException {
        return null;
    }

    @Override
    public void changeClientGlobalAuthenticationKey(byte[] bytes) {

    }

    @Override
    public void changeClientGlobalEncryptionKey(byte[] bytes) {

    }

    @Override
    public void disconnect() throws IOException {

    }

    @Override
    public void close() throws IOException {

    }
}
