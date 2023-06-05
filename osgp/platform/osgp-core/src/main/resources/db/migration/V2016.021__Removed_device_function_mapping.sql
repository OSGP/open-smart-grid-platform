-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

delete from device_function_mapping where function_group = 'OWNER' and function = 'SEND_WAKEUP_SMS';
delete from device_function_mapping where function_group = 'OWNER' and function = 'GET_SMS_DETAILS';
