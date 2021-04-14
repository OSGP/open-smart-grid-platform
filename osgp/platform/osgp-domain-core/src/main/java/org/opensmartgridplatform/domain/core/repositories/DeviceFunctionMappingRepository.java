/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.repositories;

import java.util.Collection;
import java.util.List;
import org.hibernate.exception.SQLGrammarException;
import org.opensmartgridplatform.domain.core.entities.DeviceAuthorization;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceFunctionMappingRepository extends JpaRepository<DeviceAuthorization, Long> {
  @Query("select dfm.function from DeviceFunctionMapping dfm where dfm.functionGroup = ?1")
  List<DeviceFunction> findByDeviceFunctionGroup(DeviceFunctionGroup deviceFunctionGroup);

  /**
   * Returns the distinct device functions that belong with any of the given deviceFunctionGroups.
   * Be ware of exceptions if deviceFunctionGroups is null or empty.
   *
   * @param deviceFunctionGroups a collection containing at least one DeviceFunctionGroup.
   * @return device functions with the given groups.
   * @throws SQLGrammarException if deviceFunctionGroups does not contain any elements.
   */
  @Query(
      "select distinct dfm.function from DeviceFunctionMapping dfm where dfm.functionGroup in (?1)")
  List<DeviceFunction> findByDeviceFunctionGroups(
      Collection<DeviceFunctionGroup> deviceFunctionGroups);
}
