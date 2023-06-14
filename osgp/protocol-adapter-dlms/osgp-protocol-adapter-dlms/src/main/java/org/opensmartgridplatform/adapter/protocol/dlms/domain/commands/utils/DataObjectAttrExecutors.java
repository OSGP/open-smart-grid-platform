// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.SetParameter;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains all information to be used when setting a {@link SetParameter} to the device. Further
 * the execute method, which needs a live {@link DlmsConnection}, will do the actual call to the
 * device.
 */
public class DataObjectAttrExecutors {
  private static final String REQUESTS_FAILED_FOR = ": Requests failed for: {}";

  private static final Logger LOGGER = LoggerFactory.getLogger(DataObjectAttrExecutors.class);

  private final List<DataObjectAttrExecutor> dataObjectAttrExecutorList = new ArrayList<>();
  private String errString = "";
  private boolean containsError;
  private final boolean stopOnNoSuccess;
  private final String executor;

  public DataObjectAttrExecutors(final String executor) {
    this(executor, false);
  }

  /**
   * Creates the {@link DataObjectAttrExecutors} object and sets if the execution should continue or
   * stop in case of a non success return from an executor
   */
  public DataObjectAttrExecutors(final String executor, final boolean stopOnNoSuccess) {
    this.stopOnNoSuccess = stopOnNoSuccess;
    this.executor = executor;
  }

  /**
   * @param conn : the {@link DlmsConnectionManager} with the active connection to send the {@link
   *     SetParameter} to.
   * @throws ProtocolAdapterException when one or more of the set commands fail
   */
  public void execute(final DlmsConnectionManager conn) throws ProtocolAdapterException {

    try {
      for (final DataObjectAttrExecutor dataObjectAttrExecutor : this.dataObjectAttrExecutorList) {
        if (AccessResultCode.SUCCESS != dataObjectAttrExecutor.executeSet(conn)) {
          this.handleNoSuccess(dataObjectAttrExecutor);
        }
      }
    } catch (final IOException e) {
      LOGGER.error(this.executor + REQUESTS_FAILED_FOR, this.errString);
      throw new ConnectionException(e);
    }
    if (this.containsError) {
      LOGGER.error(this.executor + REQUESTS_FAILED_FOR, this.errString);
      throw new ProtocolAdapterException(this.errString);
    }
  }

  private void handleNoSuccess(final DataObjectAttrExecutor dataObjectAttrExecutor)
      throws ProtocolAdapterException {
    this.errString += dataObjectAttrExecutor.createRequestAndResultCodeInfo();
    this.containsError = true;
    if (this.stopOnNoSuccess) {
      LOGGER.error(this.executor + REQUESTS_FAILED_FOR, this.errString);
      throw new ProtocolAdapterException(
          this.errString
              + ". Stopping execution after element: "
              + this.dataObjectAttrExecutorList.indexOf(dataObjectAttrExecutor)
              + " (total elements: "
              + this.dataObjectAttrExecutorList.size()
              + ")");
    }
  }

  /**
   * Adds an {@link DataObjectAttrExecutor} to the list of executors
   *
   * @param executor the {@link DataObjectAttrExecutor}
   */
  public DataObjectAttrExecutors addExecutor(final DataObjectAttrExecutor executor) {
    this.dataObjectAttrExecutorList.add(executor);
    return this;
  }

  /**
   * @return the list of {@link DataObjectAttrExecutor}
   */
  public List<DataObjectAttrExecutor> getDataObjectAttrExecutorList() {
    return this.dataObjectAttrExecutorList;
  }

  public String getErrString() {
    return this.errString;
  }

  public boolean isContainsError() {
    return this.containsError;
  }

  public String describeAttributes() {
    final int numberOfAttributes = this.dataObjectAttrExecutorList.size();
    final AttributeAddress[] attributeAddresses = new AttributeAddress[numberOfAttributes];
    for (int i = 0; i < numberOfAttributes; i++) {
      attributeAddresses[i] = this.dataObjectAttrExecutorList.get(i).getAttrAddress();
    }
    return JdlmsObjectToStringUtil.describeAttributes(attributeAddresses);
  }
}
