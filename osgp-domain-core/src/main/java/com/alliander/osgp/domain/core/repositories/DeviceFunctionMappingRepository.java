/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.repositories;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;

@Repository
public interface DeviceFunctionMappingRepository extends JpaRepository<DeviceAuthorization, Long> {
    @Query("select dfm.function from DeviceFunctionMapping dfm where dfm.functionGroup = ?1")
    List<DeviceFunction> findByDeviceFunctionGroup(DeviceFunctionGroup deviceFunctionGroup);

    @Query("select distinct dfm.function from DeviceFunctionMapping dfm where dfm.functionGroup in (?1)")
    List<DeviceFunction> findByDeviceFunctionGroups(Collection<DeviceFunctionGroup> deviceFunctionGroups);
}
