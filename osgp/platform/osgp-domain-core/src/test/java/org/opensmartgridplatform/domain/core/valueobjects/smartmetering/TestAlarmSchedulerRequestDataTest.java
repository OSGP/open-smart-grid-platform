/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

class TestAlarmSchedulerRequestDataTest {

  @Test
  void validate_dateInPastShouldThrowFunctionalException() {
    final Date pastDate = new Date(new Date().getTime() - 10000);

    final TestAlarmSchedulerRequestData testAlarmSchedulerRequestData =
        new TestAlarmSchedulerRequestData(pastDate, TestAlarmType.LAST_GASP);

    final FunctionalException functionalException =
        assertThrows(FunctionalException.class, testAlarmSchedulerRequestData::validate);
    Assertions.assertThat(functionalException.getCause().getMessage())
        .isEqualTo("The scheduled time should be in the future");
  }

  @Test
  void validate_dateInFutureShouldNotThrowFunctionalException() {
    final Date futureDate = new Date(new Date().getTime() + 10000);

    final TestAlarmSchedulerRequestData testAlarmSchedulerRequestData =
        new TestAlarmSchedulerRequestData(futureDate, TestAlarmType.LAST_GASP);

    assertDoesNotThrow(testAlarmSchedulerRequestData::validate);
  }

  @Test
  void validate_noAlarmTypeShouldThrowFunctionalException() {
    final Date pastDate = new Date(new Date().getTime() + 10000);

    final TestAlarmSchedulerRequestData testAlarmSchedulerRequestData =
        new TestAlarmSchedulerRequestData(pastDate, null);

    final FunctionalException functionalException =
        assertThrows(FunctionalException.class, testAlarmSchedulerRequestData::validate);
    Assertions.assertThat(functionalException.getCause().getMessage())
        .isEqualTo("The alarm type is mandatory");
  }

  @ParameterizedTest
  @EnumSource(TestAlarmType.class)
  @SuppressWarnings("unused")
  void valid(final TestAlarmType alarmType) {
    final Date futureDate = new Date(new Date().getTime() + 10000);

    final TestAlarmSchedulerRequestData testAlarmSchedulerRequestData =
        new TestAlarmSchedulerRequestData(futureDate, TestAlarmType.LAST_GASP);

    assertDoesNotThrow(() -> testAlarmSchedulerRequestData.validate());
  }
}
