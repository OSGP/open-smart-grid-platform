-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

-- Update SET_SETPOINT to SET_DATA
UPDATE device_function_mapping
SET function = 'SET_DATA'
WHERE function = 'SET_SETPOINT';

