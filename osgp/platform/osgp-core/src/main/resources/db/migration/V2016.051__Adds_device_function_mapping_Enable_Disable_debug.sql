-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

insert into device_function_mapping  (function_group, function) SELECT 'OWNER' ,'ENABLE_DEBUGGING' WHERE NOT EXISTS (SELECT 1 FROM device_function_mapping WHERE function_group = 'OWNER' and function = 'ENABLE_DEBUGGING');
insert into device_function_mapping  (function_group, function) SELECT 'OWNER' ,'DISABLE_DEBUGGING' WHERE NOT EXISTS (SELECT 1 FROM device_function_mapping WHERE function_group = 'OWNER' and function = 'DISABLE_DEBUGGING');