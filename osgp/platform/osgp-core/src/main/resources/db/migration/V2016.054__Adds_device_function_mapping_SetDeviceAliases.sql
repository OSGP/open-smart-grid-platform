-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

INSERT INTO device_function_mapping(function_group, function)
  SELECT 'OWNER' ,'SET_DEVICE_ALIASES'
  WHERE NOT EXISTS (SELECT 1 
                    FROM device_function_mapping
                    WHERE function_group = 'OWNER'
                    AND function = 'SET_DEVICE_ALIASES');

INSERT INTO device_function_mapping(function_group, function)
  SELECT 'METADATA_MANAGEMENT' ,'SET_DEVICE_ALIASES'
  WHERE NOT EXISTS (SELECT 1 
                    FROM device_function_mapping
                    WHERE function_group = 'METADATA_MANAGEMENT'
                    AND function = 'SET_DEVICE_ALIASES');
